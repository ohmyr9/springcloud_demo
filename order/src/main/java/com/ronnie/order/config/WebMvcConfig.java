//package com.ronnie.order.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.sleuth.Tracer;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//public class WebMvcConfig implements WebMvcConfigurer {
//    @Autowired
//    private final Tracer tracer;
//
//    public WebMvcConfig(Tracer tracer) {
//        this.tracer = tracer;
//    }
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new AuthorizeInterceptor(tracer));
//    }
//
//}
