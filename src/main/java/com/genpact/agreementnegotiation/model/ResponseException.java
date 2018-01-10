package com.genpact.agreementnegotiation.model;

public class ResponseException extends Exception {
    private String id;
    private String errorMessage;
    private Throwable ex;

    public ResponseException(String id, String errorMessage) {
        this.id = id;
        this.errorMessage = errorMessage;
    }

    public ResponseException(String id, String errorMessage, Throwable ex) {
        this.id = id;
        this.errorMessage = errorMessage;
        this.ex = ex;
    }

    public String getId() {
        return id;
    }


    @Override
    public String toString() {
        ex.printStackTrace();
        return "ResponseException{" +
                "id='" + id + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
