package com.tchepannou.wistia.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tchepannou.wistia.service.Http;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpImpl implements Http {
    private static final Logger LOG = LoggerFactory.getLogger(HttpImpl.class);

    @Autowired
    private Jackson2ObjectMapperBuilder jackson;


    //-- Http overrides
    @Override
    public <T> T post(String url, Map<String, String> params, Class<T> type) throws IOException {
        List<NameValuePair> nvps = params.keySet().stream()
                .map(key -> new BasicNameValuePair(key, params.get(key)))
                .collect(Collectors.toList())
                ;

        ObjectMapper mapper = jackson.build();
        String json = mapper.writeValueAsString(params);

        HttpPost request = new HttpPost(url);
        request.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
        request.addHeader("Accept", "application/json");

        LOG.info("POST " + url + "\n" + nvps);
        try (final CloseableHttpClient client = HttpClients.createDefault()) {
            try (final CloseableHttpResponse response = client.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                LOG.info("POST " + url + " " + statusCode);

                if (statusCode / 100 != 2) {
                    throw new IOException(response.getStatusLine().toString());
                }

                return jackson.build().readValue(response.getEntity().getContent(), type);

            }
        }
    }
}
