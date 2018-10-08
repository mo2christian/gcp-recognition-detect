package com.mo2christian.recognition.detect.controller;

import com.mo2christian.recognition.detect.model.DetectRequest;
import com.mo2christian.recognition.detect.model.DetectResponse;
import com.mo2christian.recognition.detect.service.DetectService;
import com.mo2christian.recognition.detect.service.ServiceLocator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Rest controller pour la detection d'images
 */
@Path("/scan")
public class DetectController{

    private static Logger logger = LogManager.getLogger(DetectController.class.getClass());

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public DetectResponse detectLabel(DetectRequest request) {
        logger.info("Detection sur image gs://{}/{} avec la langue {}", request.getBucketName(), request.getObjectName(), request.getTarget());
        DetectService detectService = ServiceLocator.getInstance().getDetectService();
        return detectService.detectLabels(request.getTarget(), request.getBucketName(), request.getObjectName());
    }
}
