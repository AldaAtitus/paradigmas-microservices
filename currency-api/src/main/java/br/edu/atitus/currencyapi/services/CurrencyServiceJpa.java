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
        String data = "10-13-2026";

        try {
            // Caso especial: BRL → BRL
            if (sourceUpper.equals("BRL") && targetUpper.equals("BRL")) {
                return new CurrencyResponse(
                        sourceUpper,
                        targetUpper,
                        1.0,
                        "Currency API running in Port: " + serverPort + " - Fonte: BCB"
                );
            }

            // Obtém a cotação da moeda de origem em relação ao BRL
            Double cotacaoSource = sourceUpper.equals("BRL")
                    ? 1.0
                    : getCotacaoBcb(sourceUpper, data);

            // Obtém a cotação da moeda de destino em relação ao BRL
            Double cotacaoTarget = targetUpper.equals("BRL")
                    ? 1.0
                    : getCotacaoBcb(targetUpper, data);

            // Calcula a taxa de conversão entre as duas moedas
            Double conversionRate = cotacaoSource / cotacaoTarget;

            return new CurrencyResponse(
                    sourceUpper,
                    targetUpper,
                    conversionRate,
                    "Currency API running in Port: " + serverPort + " - Fonte: BCB"
            );

        } catch (Exception e) {
            // Fallback: consulta o banco local
            var currency = repository.findBySourceCurrencyAndTargetCurrency(
                    sourceUpper,
                    targetUpper
            ).orElseThrow(() ->
                    new EntityNotFoundException("Cotação não encontrada")
            );

            return CurrencyResponse.fromEntity(
                    currency,
                    "Currency API running in Port: " + serverPort + " - Fonte: Banco Local"
            );
        }
    }

     //Obtém a cotação de uma moeda em relação ao BRL via BCB.
     //Lança exceção se a cotação não estiver disponível.
    private Double getCotacaoBcb(String moeda, String data) {
        BcbResponse bcb = bcbClientService.getCotacao(moeda, data);
        if (bcb == null || bcb.value() == null || bcb.value().isEmpty()) {
            throw new RuntimeException("Cotação não encontrada no BCB para " + moeda);
        }
        Double cotacao = bcb.value().get(0).cotacaoCompra();
        if (cotacao == null) {
            throw new RuntimeException("Cotação nula no BCB para " + moeda);
        }
        return cotacao;
    }
}