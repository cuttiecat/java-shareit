package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Configuration
public class WebClientConfig {

    @Value("(\"${shareit-server.url}\")")
    private String serviceUrl;

    @Bean
    public  BaseClient eventClient(RestTemplateBuilder builder){
        var restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serviceUrl))
                .build();

        return new BaseClient(restTemplate);
    }
}
