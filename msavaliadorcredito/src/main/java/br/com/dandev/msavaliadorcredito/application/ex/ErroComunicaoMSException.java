package br.com.dandev.msavaliadorcredito.application.ex;

import lombok.Getter;

public class ErroComunicaoMSException extends Exception {
    @Getter
    private Integer status;

    public ErroComunicaoMSException(String msg, Integer status) {
        super(msg);
        this.status = status;
    }
}
