package edu.uanfilms.textcorrector.service;


import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import edu.uanfilms.textcorrector.domain.BadWord;
import edu.uanfilms.textcorrector.repository.CorrectionRepository;
import edu.uanfilms.textcorrector.web.dto.Review;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CorrectionService {

    private final CorrectionRepository correctionRepository;

    public CorrectionService(CorrectionRepository correctionRepository) {
        this.correctionRepository = correctionRepository;
    }
    private File createFile(Review review) throws IOException{
        String localPath = System.getenv("BLOB_FILE_DIRECTORY");
        String fileName = "\\review" + java.util.UUID.randomUUID() + ".txt";
        File localFile = new File(localPath + fileName);
        // Write text to the file
        FileWriter writer = new FileWriter(localPath + fileName, true);
        writer.write("Reseña escrita \n");
        writer.write("Fecha: "+ Instant.now()+"\n");
        writer.write("Codigo de pelicula: "+ review.getMovie() +"\n");
        writer.write("Calificacion: "+ review.getScore()+"\n");
        writer.write("Comentario: "+ review.getComment()+"\n");
        writer.close();
        return localFile;
    }

    private String saveRegistry(File file) {
        String connectString = System.getenv("AZURE_STORAGE_CONNECTION_STRING");
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(connectString)
                .buildClient();
        String containerName = "review";
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        BlobClient blobClient = containerClient.getBlobClient(file.getName());
        System.out.println("\nSubiendo como blob a :\n\t" + blobClient.getBlobUrl());
        blobClient.uploadFromFile(file.getPath());
        file.delete();
        return blobClient.getBlobUrl();
    }

    public String correct(String comment) {
        String[] words= comment.split(" ");
        List<String> badWords= correctionRepository.findAll().stream().map(badWord -> badWord.getWord()).collect(Collectors.toList());
        StringBuilder sb= new StringBuilder();
        for (String word: words) {
            if(badWords.contains(word.toUpperCase())){
                sb.append(" ").append("****");
            }
            else{
                sb.append(" ").append(word);
            }
        }
        return sb.toString();
    }


    public Review correctReview(Review review) throws IOException {
        review.setComment(correct(review.getComment()));
        File file= createFile(review);
        String blobPath=saveRegistry(file);
        review.setBlobPath(blobPath);
        return review;
    }
}
