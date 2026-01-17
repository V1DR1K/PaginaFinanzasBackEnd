package com.finanzas.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "bitget.api")
public class BitgetConfig {
	private String url;
	private String key;
	private String secret;
	private String passphrase;
}
