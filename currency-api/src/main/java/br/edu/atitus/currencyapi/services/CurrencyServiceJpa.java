package br.edu.atitus.currencyapi.services;

import br.edu.atitus.currencyapi.dtos.BcbResponse;
import br.edu.atitus.currencyapi.dtos.CurrencyResponse;
import br.edu.atitus.currencyapi.repositories.CurrencyRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CurrencyServiceJpa implements CurrencyService {

    private final CurrencyRepository repository;
    private final BcbClientService bcbClientService;

    public CurrencyServiceJpa(
            CurrencyRepository repository,
            BcbClientService bcbClientService) {
        this.repository = repository;
        this.bcbClientService = bcbClientService;
    }

    @Value("${server.port:8100}")
    private String serverPort;

    @Override
    public CurrencyResponse findBySourceCurrencyAndTargetCurrency(
            String sourceCurrency,
            String targetCurrency
    ) throws Exception {

        String sourceUpper = sourceCurrency.toUpperCase();
        String targetUpper = targetCurrency.toUpperCase();

        // 1. Tenta consultar a API do BCB
        try {
            BcbResponse bcb = bcbClientService.getCotacao(sourceUpper, "10-13-2026");
            if (bcb != null && bcb.value() != null && !bcb.value().isEmpty()) {
                Double cotacao = bcb.value().get(0).cotacaoCompra();
                if (cotacao != null) {
                    return new CurrencyResponse(
                            sourceUpper,
                            targetUpper,
                            cotacao,
                            "Currency API running in Port: " + serverPort + " - Fonte: BCB"
                    );
                }
            }
        } catch (Exception e) {
            // Fallback: ignora o erro e tenta o banco local
        }

        // 2. Fallback: consulta o banco local
        var currency = repository.findBySourceCurrencyAndTargetCurrency(
                sourceUpper,
                targetUpper
        ).orElseThrow(() ->
                new EntityNotFoundException("Cotação não encontrada")
        );

        return CurrencyResponse.fromEntity(currency, "Currency API running in Port: " + serverPort + " - Fonte: Banco Local");
    }
}