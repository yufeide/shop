package com.yufei.shop.Aspect;

import com.yufei.shop.annotation.TotalTime;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * AOP切面：处理@ApiTimeMonitor注解，统计方法耗时
 */
@Aspect // 标记为切面类
@Component // 交给Spring容器管理
public class ApiTimeMonitorAspect {
    // 日志记录（推荐使用SLF4J，而非System.out）
    private static final Logger log = LoggerFactory.getLogger(ApiTimeMonitorAspect.class);

    /**
     * 定义切点：拦截所有标注了@TotalTime的方法
     */
    @Pointcut("@annotation(com.yufei.shop.annotation.TotalTime)")
    public void apiTimeMonitorPointcut() {}

    /**
     * 环绕通知：在方法执行前后记录时间，计算耗时
     */
    @Around("apiTimeMonitorPointcut()")
    public Object monitorApiTime(ProceedingJoinPoint joinPoint) throws Throwable {
        // 1. 方法执行前：记录开始时间
        long startTime = System.currentTimeMillis();

        // 2. 执行目标方法（核心业务逻辑）
        Object result = joinPoint.proceed();

        // 3. 方法执行后：计算耗时
        long endTime = System.currentTimeMillis();
        long costTime = endTime - startTime;

        // 4. 获取注解信息和方法信息，打印日志
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        TotalTime annotation = method.getAnnotation(TotalTime.class);

        // 拼接日志内容：类名 + 方法名 + 注解描述 + 耗时
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = method.getName();
        String apiDesc = annotation.value().isEmpty() ? methodName : annotation.value();

        // 打印耗时日志（可根据需求输出到控制台/日志文件/数据库）
        log.info("【接口耗时统计】类名：{} | 方法名：{} | 接口描述：{} | 耗时：{}ms",
                className, methodName, apiDesc, costTime);

        // 5. 返回方法执行结果（不影响原业务）
        return result;
    }
}