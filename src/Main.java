import engine.BuscaBinariaConta;
import engine.BuscaSequencialNome;
import engine.CarregadorCSV;
import engine.TabelaHashAgencias;
import model.ContaBancaria;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        System.out.println("Iniciando o sistema bancário de alta performance...\n");

        String caminhoArquivo = "data/contas_bancarias.csv";

        // 1. Inicia o Motor da Tabela Hash
        TabelaHashAgencias motorHash = new TabelaHashAgencias();

        // 2. Carregamento e Indexação Simultânea
        long tempoInicioLoad = System.currentTimeMillis();
        List<ContaBancaria> bancoDeDados = CarregadorCSV.carregarDados(caminhoArquivo, motorHash);
        long tempoTotalLoad = System.currentTimeMillis() - tempoInicioLoad;

        // Teste de Sanidade e Relatório Base
        if (bancoDeDados != null && !bancoDeDados.isEmpty()) {
            System.out.println("\n==================================================");
            System.out.println("             RELATÓRIO DE CARREGAMENTO            ");
            System.out.println("==================================================");
            System.out.println("Tempo de processamento: " + tempoTotalLoad + " ms.");
            System.out.println("Total de registros na RAM: " + bancoDeDados.size());
            System.out.println("Primeira conta [0]: " + bancoDeDados.get(0).getAll());
            System.out.println("Última conta [" + (bancoDeDados.size() - 1) + "]: " + bancoDeDados.get(bancoDeDados.size() - 1).getAll());
            System.out.println("==================================================\n");


            // 3. Teste de Mesa: Busca Sequencial O(N)
            System.out.println(">>> TESTE 1: Busca Sequencial por Nome (Varredura Completa O(N))");
            long tempoInicioSeq = System.nanoTime(); // Usando nanoTime para maior precisão
            List<ContaBancaria> nomesBusca = BuscaSequencialNome.buscarNome("Renan", bancoDeDados);
            long tempoTotalSeq = System.nanoTime() - tempoInicioSeq;

            System.out.println("Resultados encontrados: " + nomesBusca.size());
            if(!nomesBusca.isEmpty()){
                System.out.println("Exemplo: " + nomesBusca.get(0).getAll());
            }
            System.out.println("Tempo de execução: " + (tempoTotalSeq / 1_000_000.0) + " ms.\n");


            // 4. Teste de Mesa: O Motor Híbrido O(1) + O(log M)
            System.out.println(">>> TESTE 2: Busca Binária Delimitada por Hash O(log M)");

            // Pegamos dados de uma conta real que existe no final do seu CSV para testar o pior cenário
            String agenciaAlvo = bancoDeDados.get(bancoDeDados.size() - 1).getAgencia();
            String contaAlvo = bancoDeDados.get(bancoDeDados.size() - 1).getConta();

            long tempoInicioBin = System.nanoTime();
            ContaBancaria contaEncontrada = BuscaBinariaConta.obterConta(agenciaAlvo, contaAlvo, motorHash, bancoDeDados);
            long tempoTotalBin = System.nanoTime() - tempoInicioBin;

            if (contaEncontrada != null) {
                System.out.println("Sucesso! Conta localizada: " + contaEncontrada.getAll());
            } else {
                System.out.println("Falha: A conta " + contaAlvo + " da agência " + agenciaAlvo + " não foi encontrada.");
            }
            System.out.println("Tempo de execução: " + (tempoTotalBin / 1_000_000.0) + " ms.\n");


            // 5. Teste de Mesa: Cliente Inexistente (Validação de Failsafe)
            System.out.println(">>> TESTE 3: Busca de cliente fantasma");
            ContaBancaria contaFantasma = BuscaBinariaConta.obterConta("9999", "00000-0", motorHash, bancoDeDados);
            if (contaFantasma == null) {
                System.out.println("Comportamento esperado: Retornou null corretamente e não quebrou o sistema.");
            }

        } else {
            System.err.println("Falha Crítica: O banco de dados retornou vazio. Verifique o caminho do arquivo CSV.");
        }
    }
}