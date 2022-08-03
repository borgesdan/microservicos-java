package br.com.dandev.msavaliadorcredito.application;

import br.com.dandev.msavaliadorcredito.application.ex.DadosClienteNotFoundException;
import br.com.dandev.msavaliadorcredito.application.ex.ErroComunicaoMSException;
import br.com.dandev.msavaliadorcredito.domain.model.CartaoCliente;
import br.com.dandev.msavaliadorcredito.domain.model.DadosCliente;
import br.com.dandev.msavaliadorcredito.domain.model.SituaoCliente;
import br.com.dandev.msavaliadorcredito.infra.clients.CartoesResourceClient;
import br.com.dandev.msavaliadorcredito.infra.clients.ClienteResourceClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AvaliadorCreditoService {

    private final ClienteResourceClient clientesClient;
    private final CartoesResourceClient cartoesClient;

    public SituaoCliente obterSituacaoCliente(String cpf)
            throws DadosClienteNotFoundException, ErroComunicaoMSException {

        try{
            ResponseEntity<DadosCliente> dadosClienteResponse = clientesClient.dadosCliente(cpf);
            ResponseEntity<List<CartaoCliente>> cartoesResponse = cartoesClient.getCartoesByCliente(cpf);

            return SituaoCliente
                    .builder()
                    .cliente(dadosClienteResponse.getBody())
                    .cartoes(cartoesResponse.getBody())
                    .build();
        }
        catch(FeignException.FeignClientException e){
            int status = e.status();

            if(HttpStatus.NOT_FOUND.value() == status) {
                throw new DadosClienteNotFoundException();
            }

            throw new ErroComunicaoMSException(e.getMessage(), status);
        }
    }

}
