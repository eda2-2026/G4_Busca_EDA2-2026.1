package engine;
import model.IndiceCpf;
import java.util.List;

public class BuscaBinariaCpf {

    public static IndiceCpf obterCpf(String cpfProcurado, List<IndiceCpf> listaCpf) {
        if (listaCpf == null || cpfProcurado == null) return null;
        
        return buscaBinariaRecursiva(0, listaCpf.size() - 1, cpfProcurado, listaCpf);
    }

    private static IndiceCpf buscaBinariaRecursiva(int inf, int sup, String cpf, List<IndiceCpf> listaCpf) {
        if (inf > sup) {
            return null; // Não encontrou
        }

        int meio = (sup + inf) / 2;
        IndiceCpf cpfNoMeio = listaCpf.get(meio);

        // Compara em ordem alfabética
        int comparacao = cpf.compareTo(cpfNoMeio.getCpf());

        if (comparacao == 0) {
            return cpfNoMeio; // Achou!
        } else if (comparacao > 0) {
            // O cpf procurado é "maior" (vem depois no alfabeto), então busca na metade direita
            return buscaBinariaRecursiva(meio + 1, sup, cpf, listaCpf);
        } else {
            // O cpf procurado é "menor" (vem antes no alfabeto), busca na metade esquerda
            return buscaBinariaRecursiva(inf, meio - 1, cpf, listaCpf);
        }
    }
}
