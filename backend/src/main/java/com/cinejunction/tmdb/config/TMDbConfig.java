package com.cinejunction.tmdb.config;

import com.cinejunction.tmdb.client.TMDbClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class TMDbConfig {

    @Value("${tmdb.api.key}")
    private String apiKey;

    @Value("${tmdb.base-url}")
    private String baseUrl;

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }

    @Bean
    public RestClient restClient(ClientHttpRequestFactory factory) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("accept", "application/json")
                .requestFactory(factory)
                .build();
    }

    @Bean
    public TMDbClient tmDbClient(RestClient restClient) {
        return new TMDbClient(restClient, apiKey);
    }
}
