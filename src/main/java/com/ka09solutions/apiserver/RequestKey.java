package com.ka09solutions.apiserver;

import io.vertx.core.MultiMap;

import java.util.Map;

public class RequestKey
{
    private String url;
    private String params;

    public RequestKey(String url, MultiMap params)
    {
        this.url = url;
        this.params = this.paramsToString(params);
    }

    public String getUrl()
    {
        return url;
    }

    public String getParams()
    {
        return params;
    }

    private String paramsToString(MultiMap params)
    {
        if (params.isEmpty()) return "";
        StringBuilder sb = new StringBuilder(256);
        for (Map.Entry<String, String> keyvalue : params.entries())
        {
            sb.append(keyvalue.getKey()).append('=').append(keyvalue.getValue()).append("~");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        RequestKey that = (RequestKey) o;

        if (!url.equals(that.url))
        {
            return false;
        }
        return params.equals(that.params);

    }

    @Override
    public int hashCode()
    {
        int result = url.hashCode();
        result = 31 * result + params.hashCode();
        return result;
    }
}
