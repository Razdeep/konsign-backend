package com.razdeep.konsignapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KonsignConfig {

    // TODO: figure out why custom configuration is not working

    @Value("konsign.cookie.maxAge")
    public static int cookieMaxAge = 604800;

    @Value("konsign.cookie.httpOnly")
    public static boolean cookieHttpOnly = true;

    @Value("konsign.cookie.cookiePath")
    public static String cookiePath = "/";
}
