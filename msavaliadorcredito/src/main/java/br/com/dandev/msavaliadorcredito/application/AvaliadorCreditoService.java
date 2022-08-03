package br.com.dandev.msavaliadorcredito.application;

import br.com.dandev.msavaliadorcredito.application.ex.DadosClienteNotFoundException;
import br.com.dandev.msavaliadorcredito.application.ex.ErroComunicaoMSException;
import br.com.dandev.msavaliadorcredito.domain.model.*;
import br.com.dandev.msavaliadorcredito.infra.clients.CartoesResourceClient;
import br.com.dandev.msavaliadorcredito.infra.clients.ClienteResourceClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

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

    public RetornoAvaliacaoCliente realizarAvaliacao(String cpf, Long renda)
        throws DadosClienteNotFoundException, ErroComunicaoMSException {

        try{
            ResponseEntity<DadosCliente> dadosClienteResponse = clientesClient.dadosCliente(cpf);
            ResponseEntity<List<Cartao>> cartoesResponse = cartoesClient.getCartoesRendaAte(renda);

            List<Cartao> cartoes = cartoesResponse.getBody();
            var listaCartoesAprovados = getCartoesAprovados(cartoes, dadosClienteResponse.getBody());

            return new RetornoAvaliacaoCliente(listaCartoesAprovados);

        } catch(FeignException.FeignClientException e){
            int status = e.status();

            if(HttpStatus.NOT_FOUND.value() == status) {
                throw new DadosClienteNotFoundException();
            }

            throw new ErroComunicaoMSException(e.getMessage(), status);
        }
    }

    private List<CartaoAprovado> getCartoesAprovados(List<Cartao> cartoes, DadosCliente dadosCliente){
        var listaCartoesAprovados = cartoes.stream().map(cartao -> {

            BigDecimal limiteBasico = cartao.getLimiteBasico();
            BigDecimal idadeBD = BigDecimal.valueOf(dadosCliente.getIdade());
            var fator = idadeBD.divide(BigDecimal.valueOf(10));
            BigDecimal limiteAprovado = fator.multiply(limiteBasico);

            CartaoAprovado aprovado = new CartaoAprovado();
            aprovado.setCartao(cartao.getNome());
            aprovado.setBandeira(cartao.getBandeira());
            aprovado.setLimiteAprovado(limiteAprovado);

            return aprovado;
        }).collect(Collectors.toList());

        return listaCartoesAprovados;
    }

}
