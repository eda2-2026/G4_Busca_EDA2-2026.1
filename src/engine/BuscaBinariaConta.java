package engine;
import model.ContaBancaria;
import model.IndiceAgencia;

import java.util.List;

public class BuscaBinariaConta {
    public BuscaBinariaConta(){

    }
    public static ContaBancaria obterConta(String agencia, String conta, TabelaHashAgencias hashTable, List<ContaBancaria> bancoDeDados){
        if(agencia == null){
            return null;
        }

        IndiceAgencia objAgencia = hashTable.obterOcorrencia(agencia);
        if (objAgencia == null) {
            return null;
        }

        int inf = objAgencia.getlimiteInferior();
        int sup = objAgencia.getlimiteSuperior();
        return obterConta(inf,sup,conta,bancoDeDados);

    }

    public static ContaBancaria obterConta(int inf, int sup, String conta, List<ContaBancaria> bancoDeDados){

        if(inf>sup){
            return null;
        }

        int meio = (sup+inf)/2; // Depois do Caso Base: Evita gastar recursos desnecessariamente.

        if(conta.equals(bancoDeDados.get(meio).getConta())){
            return bancoDeDados.get(meio);
        } else if (Integer.parseInt(conta.replace("-", "")) > Integer.parseInt(bancoDeDados.get(meio).getConta().replace("-", ""))) {
           return obterConta(meio+1,sup,conta,bancoDeDados);
        }else{
           return obterConta(inf,meio-1,conta,bancoDeDados);
        }
    }


}
