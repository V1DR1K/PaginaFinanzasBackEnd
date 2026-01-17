package com.finanzas.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoMovimiento {
	GASTO("Gasto"),
	INVERSION("Inversion"),
	SALARIO("Salario");

	private final String valor;

	TipoMovimiento(String valor) {
		this.valor = valor;
	}

	@JsonValue
	public String getValor() {
		return valor;
	}

	@JsonCreator
	public static TipoMovimiento fromValor(String valor) {
		for (TipoMovimiento tipo : TipoMovimiento.values()) {
			if (tipo.valor.equalsIgnoreCase(valor)) {
				return tipo;
			}
		}
		throw new IllegalArgumentException("Valor no válido para TipoMovimiento: " + valor);
	}
}
