package com.znv.fssrqs.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Created by dongzelong on  2019/1/25 19:45.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
@Aspect
@Component
@Slf4j
public class LogAspect {
//    @Autowired
//    private ISysLogService sysLogService;

    @Pointcut("@annotation(com.znv.fssrqs.annotation.Log)")
    public void pointcut(){}

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint point){
        Object result=null;
        long beginTime=System.currentTimeMillis();
        try {
            result=point.proceed();
            long endTime=System.currentTimeMillis();
            insertLog(point,endTime-beginTime);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return result;
    }

    private void insertLog(ProceedingJoinPoint point, long time){
        MethodSignature signature= (MethodSignature) point.getSignature();
        Method method=signature.getMethod();
//        SysLog sysLog=new SysLog();
//        Log userAction=method.getAnnotation(Log.class);
//        if (userAction!=null){
//            sysLog.setUserAction(userAction.value());
//        }

        String className=point.getTarget().getClass().getName();
        String methodName=signature.getName();
        //请求的参数名
        String args= Arrays.toString(point.getArgs());
        //从session中获取当前登录人ID
        //SecurityUtils.getSubject().getSession().getAttribute("userId");
        String userId="0598a02a-335f-47e4-a87a-0c860be53fec";
//        sysLog.setUserId(userId);
//        sysLog.setCreateTime(new Timestamp(new Date().getTime()));
//        log.info("user{},classname:{},method name:{},parameters:{},execute time:{}ms",userId,className,methodName,args,time);
//        sysLogService.insertLog(sysLog);
    }

    @Pointcut("execution(public * com.znv.fssrqs.controller.*.*(..))")
    public void pointcutController(){}

    @Before("pointcutController()")
    public void beforeIntoController(JoinPoint point){
        String methodName=point.getSignature().getDeclaringTypeName()+"."+point.getSignature().getName();
        String params=Arrays.toString(point.getArgs());
        log.info("get in {} params:{}",methodName,params);
    }
}
