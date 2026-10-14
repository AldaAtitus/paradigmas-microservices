package br.edu.atitus.currencyapi.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BcbResponse(
        @JsonProperty("value") List<BcbCotacao> value
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record BcbCotacao(
            @JsonProperty("cotacaoCompra") Double cotacaoCompra,
            @JsonProperty("cotacaoVenda") Double cotacaoVenda,
            @JsonProperty("dataHoraCotacao") String dataHoraCotacao
    ) {}
}