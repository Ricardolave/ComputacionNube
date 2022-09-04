package edu.uanfilms.moviereview.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CorrectionService {

    public String correct(String text){
        RestTemplate template= new RestTemplate();
        String url="http://localhost:8083/api/correct";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        HttpEntity<String> httpEntity=new HttpEntity<>(text,headers);
        ResponseEntity<String> response= template.postForEntity(url,httpEntity,String.class);
        return response.getBody();
    }
}
