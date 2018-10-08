package com.mo2christian.recognition.detect.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Object encapsulant la reponse de detection.
 */
public class DetectResponse {

    private List<String> words;

    private String locale;

    public DetectResponse(){
        words = new LinkedList<>();
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public List<String> getWords() {
        return words;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }
}
