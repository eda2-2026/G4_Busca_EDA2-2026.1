package engine;

import model.IndiceAgencia;


public class TabelaHashAgencias {

    IndiceAgencia[] hashVetor = new IndiceAgencia[7919]; // Criação do Vetor. 7919 é primo e caberá ao uso
                                                        // Temos por definição dos dados da agência 1000 até 5051
                                                        // Motivo de escolha: O banco do Brasil por exemplo,
                                                        // Possui aproximadamente 4.000 agências


    public TabelaHashAgencias(){
    }


    // Não adotaremos Rehashing pois o número de agências é limitado e cabe no vetor.

    public void registrarOcorrencia(String agencia, int indiceLinha) {
        int indice = calcularHash(agencia);
        int interferencia = 0;

        while(true){
            if(hashVetor[indice] == null){
                hashVetor[indice] = new IndiceAgencia(agencia, indiceLinha, indiceLinha);
                return;
            } else if(hashVetor[indice].getAgencia().equals(agencia)){
                hashVetor[indice].atualizarSuperior(indiceLinha);
                return;
            } else {
                indice = (indice + 1) % hashVetor.length;
                interferencia++;

                // Se o número de passos for igual ao tamanho do vetor, demos a volta completa!
                if (interferencia == hashVetor.length) {
                    throw new IllegalStateException("Pânico: Tabela Hash completamente lotada!");
                }
            }
        }
    }

    public IndiceAgencia obterOcorrencia(String agencia) {
        int indice = calcularHash(agencia);
        int interferencia = 0;

        while (true) {
            if (hashVetor[indice] == null) {
                return null;
            }
            else if (hashVetor[indice].getAgencia().equals(agencia)) {
                return hashVetor[indice];
            }
            else {
                indice = (indice + 1) % hashVetor.length;
                interferencia++;

                if (interferencia == hashVetor.length) {
                    return null;
                }
            }
        }
    }







    /**
     * Para que nossa função hash não seja apenas uma operação mod utilizaremos
     * O cálculo do índice da Tabela Hash usando o método Polynomial Rolling Hash.
     * Padrão de mercado para excelente dispersão de chaves alfanuméricas curtas.
     */
    private int calcularHash(String agencia) {
        long hash = 7; // Começamos com um número primo base
        int tamanhoDoVetor = hashVetor.length;
        // Varremos cada dígito da agência (ex: '1', '0', '0', '0')
        for (int i = 0; i < agencia.length(); i++) {
            // Multiplica por 31 (primo mágico) e soma o valor ASCII do caractere
            hash = (hash * 31) + agencia.charAt(i);
        }

        // O operador bitwise '& 0x7fffffff' força o número a ser sempre positivo,
        // caso a multiplicação estoure o limite do tipo inteiro no Java (Overflow).
        // Por fim, aplicamos o módulo sobre o tamanho físico do seu vetor.
        return (int) ((hash & 0x7fffffff) % tamanhoDoVetor);
    }
}
