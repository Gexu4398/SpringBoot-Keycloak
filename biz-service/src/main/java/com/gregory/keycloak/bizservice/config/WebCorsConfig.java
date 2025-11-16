package com.gregory.keycloak.bizservice.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class WebCorsConfig {

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {

    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedHeaders(List.of(CorsConfiguration.ALL));
    configuration.setAllowedMethods(List.of(CorsConfiguration.ALL));
    // 此处如果设置 allowCredentials 为 true，那么就要 allowOriginPatterns，否则 500。
    configuration.setAllowCredentials(true);
    configuration.setAllowedOriginPatterns(List.of(CorsConfiguration.ALL));
    // 告知浏览器此相应头可以暴露给 js，因为部分接口需要用到。
    configuration.addExposedHeader("x-amz-meta-username");
    configuration.addExposedHeader("x-amz-meta-status");
    configuration.addExposedHeader("x-amz-meta-retries");
    configuration.addExposedHeader("x-amz-meta-origin-filename");
    configuration.setMaxAge(5000L);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    // 此处要使用 /** 而不能用 * 或者 /*，否则 CorsFilter 会匹配不到路径，导致 CORS 错误。
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}