package engine;
import model.ContaBancaria;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CarregadorCSV {

    // O método é estático para que você possa chamá-lo sem precisar "instanciar" o carregador
    public static List<ContaBancaria> carregarDados(String caminhoArquivo, TabelaHashAgencias hashTable, TabelaHashPix hashPix) {

        // Criação inicial de 100.000 posições no array - Não é limitado a 100.000, pode ocorrer 1M, 10M etc.
        List<ContaBancaria> bancoDeDados = new ArrayList<>(100000);

        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo))) {

            String linha;

            // Lê a primeira linha e a ignora (pois é o cabeçalho: CPF;Nome;Agencia...)
            br.readLine();

            // Lê o arquivo linha por linha até chegar no final (null)
            int i = 0;
            while ((linha = br.readLine()) != null) {

                String[] dados = linha.split(";");

                // 1. O OBJETO (A "Casa" no Heap):
                // O comando 'new' aloca espaço real para os dados no Heap. Esse objeto não morre sozinho.
                // 2. A VARIÁVEL (O "Post-it" na Stack):
                // 'novaConta' é só um ponteiro temporário. O escopo dela acaba no fim do while,
                // então essa variável específica "morre" a cada iteração.
                ContaBancaria novaConta = new ContaBancaria(dados);

                // 3. A SALVAÇÃO (Evitando o Garbage Collector):
                // Ao adicionar na lista (que foi declarada FORA do while), nós copiamos
                // o endereço do objeto para um local seguro. A variável temporária morre,
                // mas a "casa" continua viva e acessível através da lista bancoDeDados!
                bancoDeDados.add(novaConta);
                hashTable.registrarOcorrencia(novaConta.getAgencia(),i);
                hashPix.registrarOcorrencia(novaConta);
                i++;
            }

            System.out.println("Sucesso: " + bancoDeDados.size() + " contas carregadas na memória!");

        } catch (IOException e) {
            System.err.println("Erro crítico ao tentar ler o arquivo CSV: " + e.getMessage());
        }

        return bancoDeDados;
    }

    public static List<model.IndiceCpf> carregarIndicesCpf(String caminhoArquivo, List<ContaBancaria> bancoDeDados) {
        List<model.IndiceCpf> indicesCpf = new ArrayList<>();
        // Assumindo que num mundo real teremos um CPF para quase todas as contas
        // então a capacidade inicial pode ser um pouco menor que o banco cheio
        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha;
            br.readLine(); // ignora o cabeçalho (cpf;indices)

            while ((linha = br.readLine()) != null) {
                String[] dados = linha.split(";");
                String cpf = dados[0];
                String[] strIndices = dados[1].split(",");

                model.IndiceCpf indice = new model.IndiceCpf(cpf);
                for (String strIdx : strIndices) {
                    int idx = Integer.parseInt(strIdx);
                    indice.adicionarConta(bancoDeDados.get(idx));
                }
                indicesCpf.add(indice);
            }
            System.out.println("Sucesso: " + indicesCpf.size() + " CPFs indexados carregados na memória!");
        } catch (IOException e) {
            System.err.println("Aviso: índice de CPFs não encontrado. Execute o script Python. " + e.getMessage());
        }
        return indicesCpf;
    }
}