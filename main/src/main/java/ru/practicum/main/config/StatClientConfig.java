package ru.practicum.main.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.stats.client.StatClient;

@Configuration
public class StatClientConfig {

    @Value("${stats.url}")
    private String url;

    @Bean
    StatClient statsClient() {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        return new StatClient(url, builder);
    }

}
