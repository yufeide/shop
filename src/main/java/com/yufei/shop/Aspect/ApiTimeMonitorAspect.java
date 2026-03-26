package com.yufei.shop.Aspect;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yufei.shop.annotation.TotalTime;
import com.yufei.shop.entity.ApiCallRecord;
import com.yufei.shop.mapper.ApiCallRecordMapper;
import com.yufei.shop.util.ThreadLocalUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * AOP切面：处理@ApiTimeMonitor注解，统计方法耗时
 */
@Aspect
@Component
public class ApiTimeMonitorAspect {

    private static final Logger log = LoggerFactory.getLogger(ApiTimeMonitorAspect.class);

    @Autowired
    private ApiCallRecordMapper apiCallRecordMapper;

    @Pointcut("@annotation(com.yufei.shop.annotation.TotalTime)")
    public void apiTimeMonitorPointcut() {}

    @Around("apiTimeMonitorPointcut()")
    public Object monitorApiTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long endTime = System.currentTimeMillis();
        long costTime = endTime - startTime;

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        TotalTime annotation = method.getAnnotation(TotalTime.class);

        String className = joinPoint.getTarget().getClass().getName();
        String methodName = method.getName();
        String apiDesc = annotation.value().isEmpty() ? methodName : annotation.value();

        log.info("【接口耗时统计】类名：{} | 方法名：{} | 接口描述：{} | 耗时：{}ms",
                className, methodName, apiDesc, costTime);

        saveApiCallRecord(className, methodName, apiDesc, costTime, joinPoint, result);

        return result;
    }

    private void saveApiCallRecord(String className, String methodName, String apiDesc, 
                                long costTime, ProceedingJoinPoint joinPoint, Object result) {
        try {
            ApiCallRecord record = new ApiCallRecord();
            record.setClassName(className);
            record.setMethodName(methodName);
            record.setApiDesc(apiDesc);
            record.setCostTime(costTime);

            Long userId = (Long) ThreadLocalUtil.get("userId");
            record.setUserId(userId != null ? userId.toString() : "anonymous");

            Object[] args = joinPoint.getArgs();
            record.setRequestParams(args.length > 0 ? args[0].toString() : "");

            record.setResponseStatus(result != null ? "success" : "failed");

            apiCallRecordMapper.insert(record);
        } catch (Exception e) {
            log.error("保存接口调用记录失败", e);
        }
    }
}
