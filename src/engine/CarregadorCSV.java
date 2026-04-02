package engine;

import model.ContaBancaria;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CarregadorCSV {

    // O método é estático para que você possa chamá-lo sem precisar "instanciar" o carregador
    public static List<ContaBancaria> carregarDados(String "data/contas_bancarias.csv") {

        // Criação inicial de 100.000 posições no array - Não é limitado a 100.000, pode ocorrer 1M, 10M etc.
        List<ContaBancaria> bancoDeDados = new ArrayList<>(100000);

        // O 'try-with-resources' (dentro dos parênteses) garante que o arquivo será
        // fechado automaticamente no final, mesmo se der erro, evitando vazamento de memória.
        try (BufferedReader br = new BufferedReader(new FileReader("data/contas_bancarias.csv"))) {

            String linha;

            // Lê a primeira linha e a ignora (pois é o cabeçalho: CPF;Nome;Agencia...)
            br.readLine();

            // Lê o arquivo linha por linha até chegar no final (null)
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
            }

            System.out.println("Sucesso: " + bancoDeDados.size() + " contas carregadas na memória!");

        } catch (IOException e) {
            System.err.println("Erro crítico ao tentar ler o arquivo CSV: " + e.getMessage());
        }

        return bancoDeDados;
    }
}