package com.mo2christian.recognition.detect.service;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ServiceLocator {
    private static ServiceLocator ourInstance = new ServiceLocator();

    public static ServiceLocator getInstance() {
        return ourInstance;
    }

    private ClassPathXmlApplicationContext context;

    private ServiceLocator() {
        context = new ClassPathXmlApplicationContext("/applicationContext.xml");
    }

    public DetectService getDetectService(){
        return context.getBean(DetectService.class);
    }

    public void close(){
        context.close();
    }
}
