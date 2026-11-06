package br.edu.atitus.productapi.controllers;

import br.edu.atitus.productapi.dtos.ProductRequest;
import br.edu.atitus.productapi.entities.ProductEntity;
import br.edu.atitus.productapi.services.ProductService;
import br.edu.atitus.productapi.servicesimpl.AuthorizationService;
import br.edu.atitus.productapi.servicesimpl.R2StorageService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/ws/products")
public class WsProductController {

    private final ProductService service;
    private final AuthorizationService authService;
    private final R2StorageService storageService;

    public WsProductController(
            @Qualifier("productServiceJpa") ProductService service,
            AuthorizationService authService,
            R2StorageService storageService) {
        this.service = service;
        this.authService = authService;
        this.storageService = storageService;
    }

    @PostMapping
    public ResponseEntity<ProductEntity> postProduct(
            @RequestBody ProductRequest dto,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Email") String userEmail,
            @RequestHeader("X-User-Type") Integer userType) throws Exception {

        // Valida se o usuário é administrador
        authService.requireAdmin(userType);

        var product = service.save(dto);
        return ResponseEntity.status(201).body(product);
    }
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("X-User-Type") Integer userType) throws Exception {

        authService.requireAdmin(userType); // Apenas Admin pode subir imagens

        String url = storageService.upload(file);
        return ResponseEntity.ok(url); // Retorna a URL pública (ex: https://pub-xxx.r2.dev/arquivo.png)
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductEntity> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductRequest dto,
            @RequestHeader("X-User-Type") Integer userType) throws Exception {

        authService.requireAdmin(userType); // Garante que só admin passa
        var product = service.update(id, dto);
        return ResponseEntity.ok().build(); // Exemplo
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id,
            @RequestHeader("X-User-Type") Integer userType) throws Exception {

        authService.requireAdmin(userType); // Garante que só admin passa
        service.delete(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException e) {
        return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> exceptionHandler(Exception ex) {
        return ResponseEntity.status(500).body("Ops!!! Algo deu errado. - " + ex.getMessage());
    }
}