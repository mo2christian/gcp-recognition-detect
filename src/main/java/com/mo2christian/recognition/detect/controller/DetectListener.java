package com.mo2christian.recognition.detect.controller;

import com.mo2christian.recognition.detect.service.ServiceLocator;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 *
 */
public class DetectListener implements ServletContextListener{

    public void contextDestroyed(ServletContextEvent sce) {
        ServiceLocator.getInstance().close();
    }

    public void contextInitialized(ServletContextEvent sce) {
        ServiceLocator.getInstance();
    }
}
