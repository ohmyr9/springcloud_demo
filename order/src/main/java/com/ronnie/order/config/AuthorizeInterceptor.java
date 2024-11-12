//package com.ronnie.order.config;
//
//import com.ronnie.common.constant.RequestHeaders;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.cloud.sleuth.BaggageInScope;
//import org.springframework.cloud.sleuth.Tracer;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.method.HandlerMethod;
//import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//@Configuration
//@Slf4j
//public class AuthorizeInterceptor extends HandlerInterceptorAdapter {
//    private final Tracer tracer;
//
//    public AuthorizeInterceptor(Tracer tracer) {
//        this.tracer = tracer;
//    }
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        if (!(handler instanceof HandlerMethod)) {
//            return true;
//        }
//        BaggageInScope baggage = tracer.getBaggage(RequestHeaders.CURRENT_USER);
//        if(baggage != null) {
//            log.info("current User is {}", baggage.get());
//        }
//        HandlerMethod handlerMethod = (HandlerMethod) handler;
//        String authzHeader = request.getHeader("AuthConstant.AUTHORIZATION_HEADER");
//        return true;
//    }
//}