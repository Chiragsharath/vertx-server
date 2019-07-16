package com.ka09solutions.apiserver;

public class ApiResponse
{
    private String encodingType;
    private String response;
    private int hits;

    public ApiResponse(String encodingType, String response)
    {
        this.encodingType = encodingType;
        this.response = response;
    }

    public String getResponse()
    {
        return this.response;
    }

    public String getEncodingType()
    {
        return this.encodingType;
    }

    public void incrementHit()
    {
        this.hits++;
    }

    public int getHits()
    {
        return this.hits;
    }
}
