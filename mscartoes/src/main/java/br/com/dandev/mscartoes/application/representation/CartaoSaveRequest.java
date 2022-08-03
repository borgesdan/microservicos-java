package br.com.dandev.mscartoes.application.representation;

import br.com.dandev.mscartoes.domain.BandeiraCartao;
import br.com.dandev.mscartoes.domain.Cartao;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartaoSaveRequest {
    private String nome;
    private BandeiraCartao bandeira;
    private BigDecimal renda;
    private BigDecimal limiteBasico;

    public Cartao toModel() {
        return new Cartao(nome, bandeira, renda, limiteBasico);
    }
}
