package com.mo2christian.recognition.detect.service;

import com.google.cloud.vision.v1.*;
import com.mo2christian.recognition.detect.model.DetectResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Service pour la detection des motifs.
 */
public class DetectService{

    private static final Logger logger = LogManager.getLogger(DetectService.class.getClass());

    private float minScore;

    @PostConstruct
    public void init(){
        String mScore = System.getenv("MIN_SCORE");
        if (mScore != null){
            minScore = Float.parseFloat(mScore);
        }
    }

    public void setDefaultMinScore(float minScore) {
        this.minScore = minScore;
    }

    public DetectResponse detectLabels(String target, String bucketName, String objectName){
        try(ImageAnnotatorClient imageAnnotatorClient = ImageAnnotatorClient.create()) {
            Feature feat = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
            AnnotateImageRequest request =
                    AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(readImages(bucketName, objectName)).build();
            List<AnnotateImageRequest> requests = new ArrayList<>();
            requests.add(request);

            BatchAnnotateImagesResponse response = imageAnnotatorClient.batchAnnotateImages(requests);
            AnnotateImageResponse resp = response.getResponsesList().get(0);
            DetectResponse detectResponse = new DetectResponse();
            detectResponse.setLocale("en");
            List<String> texts = new LinkedList<>();
            resp.getLabelAnnotationsList().forEach(l -> {
                if (l.getScore() > minScore){
                    texts.add(l.getDescription());
                }
            });
            detectResponse.getWords().addAll(texts);
            return detectResponse;
        }
        catch(IOException ex){
            logger.error(ex);
            throw new RuntimeException(ex);
        }
    }

    private Image readImages(String bucketName, String name){
        ImageSource imgSource = ImageSource.newBuilder().setGcsImageUri("gs://" + bucketName + "/" + name).build();
        return Image.newBuilder().setSource(imgSource).build();
    }
}
