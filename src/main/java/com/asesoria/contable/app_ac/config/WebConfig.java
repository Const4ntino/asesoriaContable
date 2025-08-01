package com.asesoria.contable.app_ac.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String rutaAbsoluta = System.getProperty("user.dir") + "/uploads/";
        System.out.println("Ruta real de uploads: " + rutaAbsoluta);

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + rutaAbsoluta);
    }
}