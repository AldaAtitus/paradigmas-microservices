package br.edu.atitus.currencyapi.controllers;

import br.edu.atitus.currencyapi.dtos.CurrencyResponse;
import br.edu.atitus.currencyapi.services.CurrencyService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/ws/currencies")
public class WsCurrencyController {

    private final CurrencyService service;

    public WsCurrencyController(@Qualifier("currencyServiceJpa") CurrencyService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<CurrencyResponse> getCurrency(
            @RequestParam String source,
            @RequestParam String target,
            @RequestHeader("X-User-Type") Integer userType) throws Exception {

        // Regra de autorização: Admin (0) ou Comum (1)
        if (userType == null || (userType != 0 && userType != 1)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário sem permissão");
        }

        var response = service.findBySourceCurrencyAndTargetCurrency(source, target);
        return ResponseEntity.ok(response);
    }

    // Tratamento de erro específico para esta rota
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> exceptionHandler(Exception ex) {
        return ResponseEntity.status(500).body("Ops!!! Algo deu errado. - " + ex.getMessage());
    }
}