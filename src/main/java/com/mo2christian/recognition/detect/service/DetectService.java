package com.mo2christian.recognition.detect.service;

import com.google.cloud.vision.v1.*;
import com.mo2christian.recognition.detect.model.DetectResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Service pour la detection des motifs.
 */
public class DetectService{

    private static final Logger logger = LogManager.getLogger(DetectService.class.getClass());

    private TranslateService translateService;

    private float minScore;

    public void setMinScore(float minScore) {
        this.minScore = minScore;
    }

    public void setTranslateService(TranslateService translateService) {
        this.translateService = translateService;
    }

    public DetectResponse detectLabels(String target, String bucketName, String objectName){
        try(ImageAnnotatorClient imageAnnotatorClient = ImageAnnotatorClient.create();) {
            Feature feat = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
            AnnotateImageRequest request =
                    AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(readImages(bucketName, objectName)).build();
            List<AnnotateImageRequest> requests = new ArrayList<>();
            requests.add(request);

            BatchAnnotateImagesResponse response = imageAnnotatorClient.batchAnnotateImages(requests);
            AnnotateImageResponse resp = response.getResponsesList().get(0);
            DetectResponse detectResponse = new DetectResponse();
            detectResponse.setLocale(target);
            List<String> texts = new LinkedList<>();
            resp.getLabelAnnotationsList().forEach(l -> {
                if (l.getScore() > minScore){
                    texts.add(l.getDescription());
                }
            });

            logger.debug("Traduction pour l'image gs://{}/{} avec la langue {}", bucketName, objectName, target);
            detectResponse.getWords().addAll(translateService.translate(resp.getLabelAnnotations(0).getLocale(), target, texts.toArray(new String[0])));
            return detectResponse;
        }
        catch(IOException ex){
            logger.error(ex);
            throw new RuntimeException(ex);
        }
    }

    Image readImages(String bucketName, String name) throws MalformedURLException, IOException {
        ImageSource imgSource = ImageSource.newBuilder().setGcsImageUri("gs://" + bucketName + "/" + name).build();
        Image img = Image.newBuilder().setSource(imgSource).build();
        return img;
    }
}
