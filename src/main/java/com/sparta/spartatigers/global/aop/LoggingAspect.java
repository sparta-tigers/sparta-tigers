// package com.sparta.spartatigers.global.aop;
//
// import org.aspectj.lang.ProceedingJoinPoint;
// import org.aspectj.lang.annotation.Around;
// import org.aspectj.lang.annotation.Aspect;
// import org.aspectj.lang.annotation.Pointcut;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Component;
// import org.springframework.web.context.request.RequestContextHolder;
// import org.springframework.web.context.request.ServletRequestAttributes;
//
// import jakarta.servlet.http.HttpServletRequest;
// import lombok.extern.slf4j.Slf4j;
//
// @Aspect
// @Component
// @Slf4j
// public class LoggingAspect {
//
// 	@Pointcut()
// 	public void allController() {}
//
// 	@Around("allController()")
// 	public Object loggingRequestAndResponse(ProceedingJoinPoint joinPoint) throws Throwable {
//
// 		HttpServletRequest request =
// 			((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
//
// 		String method = request.getMethod();
// 		String requestUrl = request.getRequestURI();
// 		String requestBody = ;
//
// 		ResponseEntity response = (ResponseEntity<?>)joinPoint.proceed();
//
// 		String responseStatus;
// 		Object RawReseponseBody;
// 		String responseBody;
//
//
//
// 	}
//
//
// }
