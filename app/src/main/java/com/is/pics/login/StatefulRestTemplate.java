package com.is.pics.login;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

/**
 * Created by stefano on 11/02/15.
 */
public class StatefulRestTemplate extends RestTemplate {

    private String cookies;
    private HttpHeaders requestHeaders;
    private HttpEntity requestEntity;

    public StatefulRestTemplate(){
        this.cookies = "";
        this.requestHeaders = new HttpHeaders();
        this.requestHeaders.add("Cookie","");
        this.requestEntity = null;
        this.getMessageConverters().add(new FormHttpMessageConverter());
        this.getMessageConverters().add(new StringHttpMessageConverter());
        //this.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
    }

    public void initHeaders(){
        this.requestHeaders.set("Cookie", this.cookies);
    }

    public void initEntity(MultiValueMap<String, Object> parts){
        this.initHeaders();
        this.requestEntity = new HttpEntity(parts,this.requestHeaders);
    }

    public <T> ResponseEntity<T> exchangeForOur(URI url, HttpMethod method,
                                          Class<T> responseType) throws RestClientException {
        return super.exchange(url, method, this.requestEntity, responseType);
    }

    public String getCookies() {
        return cookies;
    }

    public void setCookies(String cookies) {
        this.cookies = cookies;
    }

    public HttpHeaders getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(HttpHeaders requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public HttpEntity getRequestEntity() {
        return requestEntity;
    }

    public void setRequestEntity(HttpEntity requestEntity) {
        this.requestEntity = requestEntity;
    }
}
