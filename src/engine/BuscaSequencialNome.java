package engine;
import model.ContaBancaria;
import java.util.ArrayList;
import java.util.List;


public class BuscaSequencialNome {
    private BuscaSequencialNome(){ //Construtor Privado
    }

    public static List<ContaBancaria> buscarNome(String nomeDeBusca, List<ContaBancaria> dadosContas){
    List<ContaBancaria> contasRetorno = new ArrayList<>();
    int size = dadosContas.size();

    for (int i = 0; i < size; i++) {
        ContaBancaria tmp = dadosContas.get(i); // Ao invés de chamar get(i) duas vezes(Desperdício de processamento),
                                                // armazenamos a referência em uma variável temporária.
        if(tmp.getTitular().toLowerCase().contains(nomeDeBusca.toLowerCase())){ // Padroniza os nomes e
                                                                                // Compara se o nome digitado
                                                                                // está contido no titular da posição i,
            contasRetorno.add(tmp);
        }
    }

    return contasRetorno; // Retorna uma lista com a referência de todas as instâncias que possui o nome digitado.

    }

}
