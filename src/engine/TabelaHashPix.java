package engine;

import model.ContaBancaria;
import model.IndicePix;

public class TabelaHashPix {

    // Inicializando com um bom número primo largo suficiente para 100k+ registros 
    IndicePix[] hashVetor = new IndicePix[200003];

    public TabelaHashPix() {
    }

    // Usando Linear Probing (Open Addressing) igualzinho a TabelaHashAgencias
    public void registrarOcorrencia(ContaBancaria conta) {
        String chave = conta.getChavePix();
        if (chave == null || chave.isEmpty()) return;

        int indice = calcularHash(chave);
        int interferencia = 0;

        while(true){
            if(hashVetor[indice] == null){
                hashVetor[indice] = new IndicePix(chave);
                hashVetor[indice].adicionarConta(conta);
                return;
            } else if(hashVetor[indice].getChavePix().equals(chave)){
                // Se a chave for igual e já existir, não sobrescreve, apenas adiciona no Index apontado
                hashVetor[indice].adicionarConta(conta);
                return;
            } else {
                indice = (indice + 1) % hashVetor.length;
                interferencia++;

                if (interferencia == hashVetor.length) {
                    throw new IllegalStateException("Pânico: Tabela Hash de Pix completamente lotada!");
                }
            }
        }
    }

    public IndicePix obterOcorrencia(String chave) {
        if (chave == null || chave.isEmpty()) return null;

        int indice = calcularHash(chave);
        int interferencia = 0;

        while (true) {
            if (hashVetor[indice] == null) {
                return null;
            } else if (hashVetor[indice].getChavePix().equals(chave)) {
                return hashVetor[indice];
            } else {
                indice = (indice + 1) % hashVetor.length;
                interferencia++;

                if (interferencia == hashVetor.length) {
                    return null;
                }
            }
        }
    }

    private int calcularHash(String chave) {
        long hash = 7;
        int tamanhoDoVetor = hashVetor.length;
        
        for (int i = 0; i < chave.length(); i++) {
            hash = (hash * 31) + chave.charAt(i);
        }

        return (int) ((hash & 0x7fffffff) % tamanhoDoVetor);
    }
}
