package com.finanzas.service;

import com.finanzas.models.*;
import com.finanzas.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InsightService {

    @Autowired
    private InsightRepository insightRepository;

    @Autowired
    private GastoInusualRepository gastoInusualRepository;

    @Autowired
    private MovimientosRepository movimientosRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Map<String, Object> getInsights(Long userId) {
        List<Insight> insights = insightRepository.findByUserIdOrderByFechaDesc(userId);
        List<GastoInusual> gastosInusuales = gastoInusualRepository.findByUserIdAndFechaGreaterThanEqualOrderByFechaDesc(
                userId, LocalDate.now().minusMonths(1));

        Map<String, Object> response = new HashMap<>();
        response.put("insights", insights);
        response.put("gastosInusuales", gastosInusuales);
        response.put("insightsNoLeidos", insightRepository.countByUserIdAndLeidoFalse(userId));

        return response;
    }

    @Transactional
    public Insight marcarComoLeido(Long insightId, Long userId) {
        Insight insight = insightRepository.findById(insightId)
                .orElseThrow(() -> new IllegalArgumentException("Insight no encontrado"));

        if (!insight.getUserId().equals(userId)) {
            throw new IllegalArgumentException("No tienes permiso para acceder a este insight");
        }

        insight.setLeido(true);
        return insightRepository.save(insight);
    }

    @Scheduled(cron = "0 0 2 * * *") // Todos los días a las 2 AM
    @Transactional
    public void generarInsightsAutomatico() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        for (Usuario usuario : usuarios) {
            try {
                generarInsights(usuario.getId());
            } catch (Exception e) {
                System.err.println("Error generando insights para usuario " + usuario.getId() + ": " + e.getMessage());
            }
        }
    }

    @Transactional
    public void generarInsights(Long userId) {
        LocalDate hoy = LocalDate.now();
        LocalDate mesActual = hoy.withDayOfMonth(1);
        LocalDate mesAnterior = mesActual.minusMonths(1);

        // 1. Comparar gastos por categoría
        compararGastosPorCategoria(userId, mesActual, mesAnterior);

        // 2. Detectar gastos inusuales
        detectarGastosInusuales(userId);

        // 3. Sugerencias de ahorro
        generarSugerenciasAhorro(userId);
    }

    private void compararGastosPorCategoria(Long userId, LocalDate mesActual, LocalDate mesAnterior) {
        List<Object[]> gastosActuales = movimientosRepository.findGastosPorCategoriaYMes(userId, mesActual);
        List<Object[]> gastosAnteriores = movimientosRepository.findGastosPorCategoriaYMes(userId, mesAnterior);

        Map<String, Double> mapAnteriores = gastosAnteriores.stream()
                .collect(Collectors.toMap(
                        arr -> (String) arr[0],
                        arr -> ((Number) arr[1]).doubleValue()
                ));

        for (Object[] gastoActual : gastosActuales) {
            String categoria = (String) gastoActual[0];
            Double totalActual = ((Number) gastoActual[1]).doubleValue();
            Double totalAnterior = mapAnteriores.getOrDefault(categoria, 0.0);

            if (totalAnterior > 0) {
                double cambio = ((totalActual - totalAnterior) / totalAnterior) * 100;

                if (cambio > 20) {
                    Insight insight = new Insight();
                    insight.setTipo(Insight.TipoInsight.GASTO_AUMENTADO);
                    insight.setSeveridad(Insight.SeveridadInsight.WARNING);
                    insight.setTitulo("Aumento en gastos de " + categoria);
                    insight.setDescripcion(String.format(
                            "Gastaste %.0f%% más este mes en %s comparado con el mes anterior",
                            cambio, categoria
                    ));
                    insight.setMonto(BigDecimal.valueOf(totalActual - totalAnterior));
                    insight.setCategoria(categoria);
                    insight.setUserId(userId);
                    insightRepository.save(insight);
                } else if (cambio < -20) {
                    Insight insight = new Insight();
                    insight.setTipo(Insight.TipoInsight.GASTO_DISMINUIDO);
                    insight.setSeveridad(Insight.SeveridadInsight.INFO);
                    insight.setTitulo("¡Excelente! Redujiste gastos en " + categoria);
                    insight.setDescripcion(String.format(
                            "Gastaste %.0f%% menos este mes en %s. ¡Sigue así!",
                            Math.abs(cambio), categoria
                    ));
                    insight.setMonto(BigDecimal.valueOf(totalAnterior - totalActual));
                    insight.setCategoria(categoria);
                    insight.setUserId(userId);
                    insightRepository.save(insight);
                }
            }
        }
    }

    private void detectarGastosInusuales(Long userId) {
        LocalDate ultimos3Meses = LocalDate.now().minusMonths(3);

        List<Movimientos> movimientos = movimientosRepository
                .findByUserIdAndTipoAndFechaGreaterThanEqual(userId, "egreso", ultimos3Meses);

        Map<String, List<Movimientos>> porCategoria = movimientos.stream()
                .collect(Collectors.groupingBy(m ->
                        m.getTipoMovimiento() != null ? m.getTipoMovimiento().name() : "SIN_CATEGORIA"));

        for (Map.Entry<String, List<Movimientos>> entry : porCategoria.entrySet()) {
            String categoria = entry.getKey();
            List<Movimientos> movimientosCategoria = entry.getValue();

            if (movimientosCategoria.size() < 3) continue;

            double promedio = movimientosCategoria.stream()
                    .mapToDouble(Movimientos::getCantidad)
                    .average()
                    .orElse(0.0);

            double desviacionEstandar = calcularDesviacionEstandar(movimientosCategoria, promedio);
            double umbral = promedio + (2 * desviacionEstandar);

            for (Movimientos mov : movimientosCategoria) {
                if (mov.getCantidad() > umbral && mov.getCantidad() > promedio * 2) {
                    GastoInusual gastoInusual = new GastoInusual();
                    gastoInusual.setMovimientoId(mov.getId());
                    gastoInusual.setTipo(categoria);
                    gastoInusual.setCantidad(BigDecimal.valueOf(mov.getCantidad()));
                    gastoInusual.setDescripcion(mov.getDescripcion());
                    gastoInusual.setFecha(mov.getFecha());
                    gastoInusual.setPromedioHistorico(BigDecimal.valueOf(promedio));

                    BigDecimal desv = desviacionEstandar > 0
                            ? BigDecimal.valueOf((mov.getCantidad() - promedio) / desviacionEstandar)
                            : BigDecimal.ZERO;
                    gastoInusual.setDesviacion(desv.setScale(2, RoundingMode.HALF_UP));

                    gastoInusual.setRazon(String.format(
                            "Este gasto de $%.2f es %.1f veces mayor que tu promedio habitual de $%.2f en %s",
                            (double)mov.getCantidad(), mov.getCantidad() / promedio, promedio, categoria
                    ));
                    gastoInusual.setUserId(userId);
                    gastoInusualRepository.save(gastoInusual);

                    Insight insight = new Insight();
                    insight.setTipo(Insight.TipoInsight.GASTO_INUSUAL);
                    insight.setSeveridad(Insight.SeveridadInsight.ALERT);
                    insight.setTitulo("Gasto inusual detectado");
                    insight.setDescripcion(gastoInusual.getRazon());
                    insight.setMonto(BigDecimal.valueOf(mov.getCantidad()));
                    insight.setCategoria(categoria);
                    insight.setUserId(userId);
                    insightRepository.save(insight);
                }
            }
        }
    }

    private void generarSugerenciasAhorro(Long userId) {
        LocalDate mesActual = LocalDate.now().withDayOfMonth(1);

        List<Movimientos> gastosHormiga = movimientosRepository
                .findByUserIdAndTipoAndCantidadLessThanAndFechaGreaterThanEqual(
                        userId, "egreso", 5000, mesActual);

        if (gastosHormiga.size() > 20) {
            double totalGastosHormiga = gastosHormiga.stream()
                    .mapToDouble(Movimientos::getCantidad)
                    .sum();

            Insight insight = new Insight();
            insight.setTipo(Insight.TipoInsight.AHORRO_POTENCIAL);
            insight.setSeveridad(Insight.SeveridadInsight.INFO);
            insight.setTitulo("Potencial de ahorro detectado");
            insight.setDescripcion(String.format(
                    "Realizaste %d pequeñas compras este mes que suman $%.2f. " +
                    "Reducir estos gastos hormiga podría ayudarte a ahorrar significativamente.",
                    gastosHormiga.size(), totalGastosHormiga
            ));
            insight.setMonto(BigDecimal.valueOf(totalGastosHormiga));
            insight.setUserId(userId);
            insightRepository.save(insight);
        }
    }

    private double calcularDesviacionEstandar(List<Movimientos> movimientos, double promedio) {
        double varianza = movimientos.stream()
                .mapToDouble(m -> Math.pow(m.getCantidad() - promedio, 2))
                .average()
                .orElse(0.0);
        return Math.sqrt(varianza);
    }
}
