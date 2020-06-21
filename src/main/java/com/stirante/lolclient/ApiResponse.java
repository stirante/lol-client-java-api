package com.stirante.lolclient;

public class ApiResponse<T> {

    private final T responseObject;
    private final String rawResponse;
    private final int statusCode;

    public ApiResponse(T responseObject, String rawResponse, int statusCode) {
        this.responseObject = responseObject;
        this.rawResponse = rawResponse;
        this.statusCode = statusCode;
    }

    public T getResponseObject() {
        return responseObject;
    }

    public String getRawResponse() {
        return rawResponse;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public boolean isOk() {
        return statusCode / 200 == 1;
    }

}
