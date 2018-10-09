package com.mo2christian.recognition.detect.service;

import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service de traduction.
 */
public class TranslateService{

    private Logger logger = LogManager.getLogger(TranslateService.class.getClass());

    private String translateAPI;

    public void setTranslateAPI(String translateAPI) {
        this.translateAPI = translateAPI;
    }

    List<String> translate(String from, String target, String... texts){
        logger.debug("Traduction de {} à {}", from, target);
        TranslateResponse response;
        HttpPost httpPost = new HttpPost(translateAPI);
        httpPost.addHeader("Content-Type", "application/json");
        httpPost.addHeader("Accept", "application/json");

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            TranslateRequest translateRequest = new TranslateRequest();
            translateRequest.setFrom(from);
            translateRequest.setTarget(target);
            translateRequest.getWords().addAll(Arrays.asList(texts));
            Gson g = new Gson();
            HttpEntity entity = new StringEntity(g.toJson(translateRequest));
            httpPost.setEntity(entity);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode > 299 || statusCode < 200)
                throw new RuntimeException(httpResponse.getStatusLine().getReasonPhrase());
            String result = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()))
                    .lines().collect(Collectors.joining("\n"));
            response = g.fromJson(result, TranslateResponse.class);
        }
        catch(IOException ex){
            logger.error(ex);
            throw new RuntimeException(ex);
        }
        return response.getWords();
    }

    /**
     * Objet encapsulant la reponse de l'API translate
     */
    private class TranslateResponse{

        private List<String> words;

        TranslateResponse(){
            words = new LinkedList<>();
        }

        public List<String> getWords() {
            return words;
        }

        void setWords(List<String> words) {
            this.words = words;
        }
    }

    /**
     * Objet representant la requete à envoyer à lAPI translate
     */
    private class TranslateRequest {

        private String from;

        private String target;

        private List<String> words;

        TranslateRequest(){
            words = new LinkedList<>();
        }

        String getFrom() {
            return from;
        }

        void setFrom(String from) {
            this.from = from;
        }

        String getTarget() {
            return target;
        }

        void setTarget(String target) {
            this.target = target;
        }

        List<String> getWords() {
            return words;
        }

        void setWords(List<String> words) {
            this.words = words;
        }
    }
}
