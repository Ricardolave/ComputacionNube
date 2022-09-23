package edu.uanfilms.textcorrector.web.rest;

import edu.uanfilms.textcorrector.service.CorrectionService;
import edu.uanfilms.textcorrector.web.dto.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@Transactional
public class CorrectionResource{

    private final  CorrectionService correctionService;

    public CorrectionResource(CorrectionService correctionService) {
        this.correctionService = correctionService;
    }

    @PostMapping("/correct")
    public ResponseEntity<String> correctText(@RequestBody String comment) throws URISyntaxException {
       return ResponseEntity.ok(correctionService.correct(comment));
    }

    @PostMapping("/correctReview")
    public ResponseEntity<Review> correctReview(@RequestBody Review review) throws URISyntaxException, IOException {
        return ResponseEntity.ok(correctionService.correctReview(review));
    }
}