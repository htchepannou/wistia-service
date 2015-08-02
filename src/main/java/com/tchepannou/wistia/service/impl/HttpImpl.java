package com.tchepannou.wistia.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tchepannou.wistia.service.Http;
import org.apache.commons.io.IOUtils;
import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpImpl implements Http {
    private static final Logger LOG = LoggerFactory.getLogger(HttpImpl.class);

    @Autowired
    private Jackson2ObjectMapperBuilder jackson;



    //-- Http overrides
    @Override
    public void delete(String url) throws IOException {
        LOG.info("DELETE " + url);
        submit(new HttpDelete(url));
    }

    @Override
    public <T> T get(String url, Class<T> type) throws IOException {
        LOG.info("GET " + url);
        return submit(new HttpGet(url), type);
    }

    @Override
    public <T> T post(String url, Map<String, String> params, Class<T> type) throws IOException {
        List<NameValuePair> nvps = params.keySet().stream()
                .map(key -> new BasicNameValuePair(key, params.get(key)))
                .collect(Collectors.toList())
        ;

        HttpPost request = new HttpPost(url);
        request.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
        request.addHeader("Accept", "application/json");
        request.addHeader("Content-Type", ContentType.APPLICATION_FORM_URLENCODED.getMimeType());

        LOG.info("POST " + url + "\n" + nvps);
        return submit(request, type);
    }

    @Override
    public <T> T postJson(String url, Map<String, String> params, Class<T> type) throws IOException {
        ObjectMapper mapper = jackson.build();
        String json = mapper.writeValueAsString(params);

        HttpPost request = new HttpPost(url);
        request.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
        request.addHeader("Accept", "application/json");
        request.addHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType());

        LOG.info("POST " + url + "\n" + json);
        return submit(request, type);
    }

    private <T> T submit (HttpRequestBase request, Class<T> type) throws IOException {
        try (final CloseableHttpClient client = HttpClients.createDefault()) {
            try (final CloseableHttpResponse response = client.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode / 100 != 2) {
                    throw new IOException("Invalid status code: " + response.getStatusLine().toString());
                }

                String json = toString(response.getEntity().getContent());
                LOG.info(json);
                return jackson.build().readValue(json, type);
            }
        }
    }

    private void submit (HttpRequestBase request) throws IOException {
        try (final CloseableHttpClient client = HttpClients.createDefault()) {
            try (final CloseableHttpResponse response = client.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode / 100 != 2) {
                    throw new IOException("Invalid status code: " + response.getStatusLine().toString());
                }
            }
        }
    }

    private String toString(InputStream in) throws IOException{
        try(ByteArrayOutputStream out = new ByteArrayOutputStream()){
            IOUtils.copy(in, out);
            return out.toString("utf-8");
        }
    }
}
