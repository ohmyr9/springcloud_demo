//package com.ronnie.order.aop;
//
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.aspectj.lang.reflect.MethodSignature;
//
//import java.lang.reflect.Method;
//
//@Aspect
//public class BaseAspect {
//
//    private Integer limitSize;
//
//
//    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
//    private void controllerMethod() {
//    }
//
//
//    @Around("controllerMethod()")
//    public Object around(ProceedingJoinPoint joinPoint) {
//        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
//        Method method = signature.getMethod();
//        Class<?> declaringClass = method.getDeclaringClass();
//        long start = System.currentTimeMillis();
//
//        Object var10;
//        try {
//            Object returnValue = joinPoint.proceed();
//            if (logger.isInfoEnabled()) {
//                String outputValueJsonStr = JSON.toJSONString(returnValue, PASSWORD_FILTER, new SerializerFeature[0]);
//                String inputArgsJsonStr = this.inputArgsToJsonStr(joinPoint);
//                int outputValueRealSize = outputValueJsonStr.length();
//                if (this.methodLogType.equals(MethodLogType.limit) && outputValueRealSize > this.limitSize) {
//                    outputValueJsonStr = outputValueJsonStr.substring(0, this.limitSize);
//                }
//
//                long elapsedTime = System.currentTimeMillis() - start;
//                MDC.put("elapsedTime", String.valueOf(elapsedTime));
//                logger.info("{}.{}({}), the result is {} , output value size is {} ", new Object[]{declaringClass, method.getName(), inputArgsJsonStr, outputValueJsonStr, outputValueRealSize});
//            }
//
//            Object var21 = returnValue;
//            return var21;
//        } catch (Throwable var16) {
//            Throwable throwable = var16;
//            long elapsedTime = System.currentTimeMillis() - start;
//            MDC.put("elapsedTime", String.valueOf(elapsedTime));
//            var10 = this.exceptionHandler.handelException(throwable, declaringClass, method, this.inputArgsToJsonStr(joinPoint), elapsedTime);
//        } finally {
//            MDC.remove("elapsedTime");
//        }
//
//        return var10;
//    }
//
//
//    private String getCallPath(Method method) {
//        String path = null;
//        Annotation[] annotations = method.getAnnotations();
//        Annotation[] var4 = annotations;
//        int var5 = annotations.length;
//
//        for(int var6 = 0; var6 < var5; ++var6) {
//            Annotation annotation = var4[var6];
//            if (annotation.annotationType().isAnnotationPresent(RequestMapping.class)) {
//                Class<? extends Annotation> annotationType = annotation.annotationType();
//                if (GetMapping.class.equals(annotationType)) {
//                    GetMapping mapping = (GetMapping)method.getAnnotation(GetMapping.class);
//                    path = this.getPath(mapping.value(), mapping.path());
//                    break;
//                }
//
//                if (PostMapping.class.equals(annotationType)) {
//                    PostMapping mapping = (PostMapping)method.getAnnotation(PostMapping.class);
//                    path = this.getPath(mapping.value(), mapping.path());
//                    break;
//                }
//            }
//        }
//
//        return path;
//    }
//
//    private String inputArgsToJsonStr(ProceedingJoinPoint joinPoint) {
//        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
//        String[] argNames = methodSignature.getParameterNames();
//        Class[] argTypes = methodSignature.getParameterTypes();
//        Object[] args = joinPoint.getArgs();
//        StringBuilder inputArgs = new StringBuilder();
//        if (argNames != null && argNames.length > 0) {
//            for(int i = 0; i < argNames.length; ++i) {
//                String argName = argNames[i];
//                Class argType = argTypes[i];
//                boolean shouldIgnore = false;
//                Class[] var11 = this.ignoreClasses;
//                int var12 = var11.length;
//
//                for(int var13 = 0; var13 < var12; ++var13) {
//                    Class ignoreClass = var11[var13];
//                    if (ignoreClass.isAssignableFrom(argType)) {
//                        shouldIgnore = true;
//                    }
//                }
//
//                if (!shouldIgnore) {
//                    Object arg = args[i];
//
//                    try {
//                        inputArgs.append(argType.getSimpleName()).append(" ").append(argName).append(":").append(JSON.toJSONString(arg, PASSWORD_FILTER, new SerializerFeature[0])).append(",");
//                    } catch (RuntimeException var15) {
//                        logger.warn("error occur while convert to JSONString {}-{}:{}", new Object[]{argType, argName, arg, var15});
//                    }
//                }
//            }
//        }
//
//        if (inputArgs.length() > 1) {
//            inputArgs.deleteCharAt(inputArgs.length() - 1);
//        }
//
//        return inputArgs.toString();
//    }
//
//    private String getApiParams(ProceedingJoinPoint joinPoint) {
//        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
//        Annotation[][] parameterAnnotations = methodSignature.getMethod().getParameterAnnotations();
//        Class[] argTypes = methodSignature.getParameterTypes();
//        Object[] args = joinPoint.getArgs();
//        StringBuilder params = new StringBuilder();
//        if (args != null && args.length > 0) {
//            for(int i = 0; i < args.length; ++i) {
//                Class argType = argTypes[i];
//                Object arg = args[i];
//                Annotation annotation = parameterAnnotations[i][0];
//                String name;
//                if (annotation.annotationType().equals(RequestParam.class)) {
//                    name = ((RequestParam)annotation).value();
//                } else if (annotation.annotationType().equals(PathVariable.class)) {
//                    name = ((PathVariable)annotation).value();
//                } else {
//                    name = argType.getSimpleName();
//                }
//
//                params.append(name).append(":").append(JSON.toJSONString(arg)).append(",");
//            }
//        }
//
//        if (params.length() > 1) {
//            params.deleteCharAt(params.length() - 1);
//        }
//
//        return params.toString();
//    }
//
//    private String getPath(String[] value, String[] path) {
//        if (path.length > 0) {
//            return path[0];
//        } else {
//            return value.length > 0 ? value[0] : null;
//        }
//    }
//
//    private boolean isApiClient(String path) {
//        return !StringUtils.isEmpty(path);
//    }
//
//    private String buildApiLock(String serviceName, String path, String params) {
//        String lock = serviceName + path + params;
//        return lock.intern();
//    }
//}
