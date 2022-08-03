package br.com.dandev.mscartoes.application;

import br.com.dandev.mscartoes.domain.ClienteCartao;
import br.com.dandev.mscartoes.infra.repository.ClienteCartaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteCartaoService {
    private final ClienteCartaoRepository repository;

    public List<ClienteCartao> listaCartoesByCpf(String cpf){
        return repository.findByCpf(cpf);
    }
}
