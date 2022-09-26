package edu.uanfilms.moviereview.service;

import edu.uanfilms.moviereview.domain.Review;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CorrectionService {

    public Review correct(Review review) {
        RestTemplate template = new RestTemplate();
        //"http://localhost:8083/api/correctReview";
        String url = System.getenv("BATCH_URL");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Review> httpEntity = new HttpEntity<Review>(review, headers);
        ResponseEntity<Review> response = template.postForEntity(url, httpEntity, Review.class);
        return response.getBody();
    }
}
