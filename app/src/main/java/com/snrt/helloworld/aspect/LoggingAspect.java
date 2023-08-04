package com.snrt.helloworld.aspect;

import android.util.Log;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class LoggingAspect {
    private static final String TAG = "LoggingAspect";
    ThreadLocal<Long> time = new ThreadLocal<>();
    @Before("execution(* com.snrt.helloworld.music.*.getMusics(..))")
    public void before() {
        // 在方法调用前执行的逻辑，比如打印日志
        long startTime = System.currentTimeMillis();
        Log.e(TAG, "方法开始时间: "+ startTime);
        time.set(startTime);
    }
    @After("execution(* com.snrt.helloworld.music.*.getMusics(..))")
    public void after() {
        // 在方法调用前执行的逻辑，比如打印日志
        long endTime = System.currentTimeMillis();
        Log.e(TAG, "方法结束时间: "+ endTime);
        Log.e(TAG, "after: "+ (endTime - time.get()) +"ms");
    }
}
