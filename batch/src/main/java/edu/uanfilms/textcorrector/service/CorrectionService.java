package edu.uanfilms.textcorrector.service;


import edu.uanfilms.textcorrector.domain.BadWord;
import edu.uanfilms.textcorrector.repository.CorrectionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CorrectionService {

    private final CorrectionRepository correctionRepository;

    public CorrectionService(CorrectionRepository correctionRepository) {
        this.correctionRepository = correctionRepository;
    }

    public String correct(String comment) {
        String[] words= comment.split(" ");
        List<String> badWords= correctionRepository.findAll().stream().map(badWord -> badWord.getWord()).collect(Collectors.toList());
        StringBuilder sb= new StringBuilder();
        for ( String word: words) {
            if(badWords.contains(word.toUpperCase())){
                sb.append(" ").append("****");
            }
            else{
                sb.append(" ").append(word);
            }
        }
        return sb.toString();
    }
}
