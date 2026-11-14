package br.edu.atitus.productapi.servicesimpl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Service
public class R2StorageService {

    private final S3Client s3Client;

    @Value("${cloudflare.r2.bucket}")
    private String bucket;

    @Value("${cloudflare.r2.public-url}")
    private String publicUrl;

    // 1. Definir os tipos permitidos
    private static final Set<String> ALLOWED_TYPES = Set.of("image/png", "image/jpeg", "image/webp");

    // 2. Definir o tamanho máximo (5 MB)
    private static final long MAX_SIZE = 5 * 1024 * 1024;

    public R2StorageService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String upload(MultipartFile file) throws IOException {
        // 3. Validar tipo de arquivo
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Tipo de arquivo inválido. Apenas PNG, JPG e WebP são permitidos.");
        }

        // 4. Validar tamanho
        if (file.getSize() > MAX_SIZE) {
            throw new IllegalArgumentException("Arquivo muito grande. O limite é de 5 MB.");
        }

        // 5. Gerar nome seguro (UUID + extensão validada)
        String extension = contentType.substring(contentType.indexOf("/") + 1);
        if (extension.equals("jpeg")) {
            extension = "jpg"; // Padroniza .jpg em vez de .jpeg
        }
        String fileName = UUID.randomUUID() + "." + extension;

        // 6. Fazer o upload para o R2
        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .contentType(contentType)
                .build();

        s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        // 7. Retornar a URL pública
        return publicUrl + "/" + fileName;
    }
}