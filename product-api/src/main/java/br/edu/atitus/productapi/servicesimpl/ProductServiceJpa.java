package br.edu.atitus.productapi.servicesimpl;

import br.edu.atitus.productapi.dtos.ProductRequest;
import br.edu.atitus.productapi.dtos.ProductResponse;
import br.edu.atitus.productapi.entities.ProductEntity;
import br.edu.atitus.productapi.repositories.ProductRepository;
import br.edu.atitus.productapi.services.ProductService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProductServiceJpa implements ProductService {

    private final ProductRepository repository;
    private final CurrencyClientService currencyClientService;

    public ProductServiceJpa(ProductRepository repository, CurrencyClientService currencyClientService) {
        this.repository = repository;
        this.currencyClientService = currencyClientService;
    }

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${app.promotion.message:Nenhuma Promoção Ativa}")
    private String promotionMessage;

    private ProductResponse getResponse(ProductEntity entity, String environment, String targetCurrency) {
        double convertedValue = entity.getPrice();
        String targetCurrencyUpper = targetCurrency.toUpperCase();

        return ProductResponse.fromEntity(
                entity, environment, promotionMessage, targetCurrencyUpper, convertedValue
        );
    }

    @Override
    public ProductResponse findById(Long id, String targetCurrency) throws Exception {
        var product = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
        String environment = "Product API running in port " + serverPort;
        return getResponse(product, environment, targetCurrency);
    }

    @Override
    public Page<ProductResponse> findAll(Pageable pageable, Long categoryId, String query, String targetCurrency) throws Exception {
        Page<ProductEntity> products;
        if (categoryId != null && query != null) {
            products = repository.findByCategoryIdAndTitleContainingIgnoreCase(categoryId, query, pageable);
        } else if (categoryId != null) {
            products = repository.findByCategoryId(categoryId, pageable);
        } else if (query != null) {
            products = repository.findByTitleContainingIgnoreCase(query, pageable);
        } else {
            products = repository.findAll(pageable);
        }
        String environment = "Product API running in port " + serverPort;
        return products.map(entity -> getResponse(entity, environment, targetCurrency));
    }

    @Override
    public ProductEntity save(ProductRequest request, Long userId) throws Exception {
        if (request == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Requisição inválida");
        if (request.title() == null || request.title().isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Título inválido");
        if (request.description() == null || request.description().isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Descrição inválida");
        if (request.price() < 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Preço inválido");

        var product = new ProductEntity();
        product.setTitle(request.title().trim());
        product.setDescription(request.description().trim());
        product.setPrice(request.price());
        product.setCategoryId(request.categoryId());
        product.setImageUrl(request.imageUrl() != null ? request.imageUrl().trim() : null);
        product.setSellerId(userId);

        return repository.save(product);
    }

    @Override
    public ProductEntity update(Long id, ProductRequest request, Long userId) throws Exception {
        if (request == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Requisição inválida");

        var product = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));


        if (request.title() == null || request.title().isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Título inválido");
        if (request.description() == null || request.description().isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Descrição inválida");
        if (request.price() <= 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Preço inválido");

        product.setTitle(request.title().trim());
        product.setDescription(request.description().trim());
        product.setPrice(request.price());
        product.setCategoryId(request.categoryId());
        if (request.imageUrl() != null) product.setImageUrl(request.imageUrl().trim());

        return repository.save(product);
    }

    @Override
    public void delete(Long id) throws Exception {
        var product = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
        repository.delete(product);
    }
}