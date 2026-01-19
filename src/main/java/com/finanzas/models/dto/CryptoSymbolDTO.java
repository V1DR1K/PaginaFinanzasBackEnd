package com.finanzas.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CryptoSymbolDTO {
    private String instId;
    private String display;
    private String source;
}
