package br.edu.atitus.productapi.controllers;

import br.edu.atitus.productapi.servicesimpl.R2StorageService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/ws/media")
public class MediaController {

    private final R2StorageService storageService;

    public MediaController(R2StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadMedia(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("X-User-Id") Long userId) throws Exception {
        // Qualquer usuário logado (o gateway já validou o JWT)
        String url = storageService.upload(file);
        return ResponseEntity.ok(Map.of("url", url));
    }
}