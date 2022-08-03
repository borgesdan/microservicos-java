package br.com.dandev.msavaliadorcredito.application;

import br.com.dandev.msavaliadorcredito.application.ex.DadosClienteNotFoundException;
import br.com.dandev.msavaliadorcredito.application.ex.ErroComunicaoMSException;
import br.com.dandev.msavaliadorcredito.domain.model.DadosAvaliacao;
import br.com.dandev.msavaliadorcredito.domain.model.RetornoAvaliacaoCliente;
import br.com.dandev.msavaliadorcredito.domain.model.SituaoCliente;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("avaliacoes-credito")
@RequiredArgsConstructor
public class AvaliadorCreditoController {

    private final AvaliadorCreditoService avaliadorCreditoService;

    @GetMapping
    public String status() {
        return "ok";
    }

    @GetMapping(value = "situacao-cliente", params = "cpf")
    public ResponseEntity consultaSituacaoCliente(@RequestParam("cpf") String cpf) {
        try {
            SituaoCliente situaoCliente = avaliadorCreditoService.obterSituacaoCliente(cpf);
            return ResponseEntity.ok(situaoCliente);
        } catch (DadosClienteNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (ErroComunicaoMSException e) {
            return ResponseEntity.status(HttpStatus.resolve(e.getStatus())).build();
        }
    }

    @PostMapping
    public ResponseEntity realizarAvaliacao(@RequestBody DadosAvaliacao dados) {
        try {
             RetornoAvaliacaoCliente retornoAvaliacaoCliente = avaliadorCreditoService.realizarAvaliacao(dados.getCpf(), dados.getRenda());
             return ResponseEntity.ok(retornoAvaliacaoCliente);
        } catch (DadosClienteNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (ErroComunicaoMSException e) {
            return ResponseEntity.status(HttpStatus.resolve(e.getStatus())).build();
        }
    }
}
