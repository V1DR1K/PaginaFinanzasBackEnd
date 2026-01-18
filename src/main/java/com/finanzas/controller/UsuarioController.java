package com.finanzas.controller;

import com.finanzas.config.JwtUtil;
import com.finanzas.models.dto.CambioContrasenaRequest;
import com.finanzas.models.dto.LoginRequest;
import com.finanzas.models.dto.LoginResponse;
import com.finanzas.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:4200", "http://172.17.160.1:4200", "http://192.168.0.146:4200", "http://204.216.191.205"})
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            boolean esValido = usuarioService.validarCredenciales(
                    loginRequest.getUsuario(),
                    loginRequest.getContrasena()
            );

            if (esValido) {
                String token = jwtUtil.generateToken(loginRequest.getUsuario());
                LoginResponse response = new LoginResponse(token, loginRequest.getUsuario());
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("mensaje", "Credenciales inválidas");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", "Error en el servidor: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/cambiar-contrasena")
    public ResponseEntity<?> cambiarContrasena(
            @RequestBody CambioContrasenaRequest request,
            Authentication authentication) {
        try {
            String usuario = authentication.getName();
            boolean cambioExitoso = usuarioService.cambiarContrasena(
                    usuario,
                    request.getContrasenaActual(),
                    request.getContrasenaNueva()
            );

            if (cambioExitoso) {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "Contraseña cambiada exitosamente");
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("mensaje", "Contraseña actual incorrecta");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", "Error al cambiar contraseña: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
