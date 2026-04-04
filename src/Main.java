import engine.CarregadorCSV;
import engine.TabelaHashAgencias;
import model.ContaBancaria;
import java.util.List;
import engine.BuscaSequencialNome;

public class Main {
    public static void main(String[] args) {

        System.out.println("Iniciando o sistema bancário...");

        // Caminho relativo para o arquivo de dados
        String caminhoArquivo = "data/contas_bancarias.csv";

        // Inicia o cronômetro
        long tempoInicio = System.currentTimeMillis();

        // Chama o motor de carregamento que você construiu
        List<ContaBancaria> bancoDeDados = CarregadorCSV.carregarDados("data/contas_bancarias.csv", new TabelaHashAgencias());

        // Para o cronômetro
        long tempoFim = System.currentTimeMillis();
        long tempoTotal = tempoFim - tempoInicio;

        // Teste de Sanidade: Verifica se a lista não está vazia e exibe os resultados
        if (bancoDeDados != null && !bancoDeDados.isEmpty()) {
            System.out.println("\n--- RELATÓRIO DE CARREGAMENTO ---");
            System.out.println("Tempo de carregamento: " + tempoTotal + " milissegundos.");
            System.out.println("Total de registros na memória: " + bancoDeDados.size());

            // Pega o primeiro elemento (índice 0)
            System.out.println("\nPrimeira conta [0]: " + bancoDeDados.get(0).getAll());

            // Pega o último elemento (índice tamanho - 1)
            int ultimoIndice = bancoDeDados.size() - 1;
            System.out.println("Última conta [" + ultimoIndice + "]: " + bancoDeDados.get(ultimoIndice).getAll());
            System.out.println("---------------------------------\n");


            // Exemplo de uso do método buscarNome
            List<ContaBancaria> nomesBusca = BuscaSequencialNome.buscarNome("Renan",bancoDeDados);
            for(int i = 0; i < nomesBusca.size(); i++){
                System.out.println(nomesBusca.get(i).getAll());
            }

        } else {
            System.out.println("Falha: O banco de dados retornou vazio. Verifique o caminho do arquivo.");
        }
    }
}