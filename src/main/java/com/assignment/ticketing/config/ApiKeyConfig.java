package com.assignment.ticketing.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Getter
@Component
public class ApiKeyConfig {

    @Value("${api.key.user}")
    private String user;

    @Value("${api.key.agent}")
    private String agent;
}