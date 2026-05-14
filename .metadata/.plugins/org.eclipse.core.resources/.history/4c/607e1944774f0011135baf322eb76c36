package com.contafree.auth_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtProperties {
	private String secret;
	private long accessTokenExpiration = 900000 ; // 15 min en ms
	private long refreshTokenExpiration = 604800000; // 7 días en ms
}
