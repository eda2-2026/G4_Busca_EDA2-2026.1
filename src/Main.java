import engine.BuscaBinariaConta;
import engine.BuscaSequencialNome;
import engine.CarregadorCSV;
import engine.TabelaHashAgencias;
import model.ContaBancaria;
import view.Formulario;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        // =====================================================================
        // 1. CARREGAMENTO DOS DADOS (Backend)
        // =====================================================================
        System.out.println("Iniciando o sistema bancário... Aguarde o carregamento na memória.");

        String caminhoArquivo = "data/contas_bancarias.csv";
        TabelaHashAgencias motorHash = new TabelaHashAgencias();

        long tempoInicioLoad = System.currentTimeMillis();
        List<ContaBancaria> bancoDeDados = CarregadorCSV.carregarDados(caminhoArquivo, motorHash);
        long tempoTotalLoad = System.currentTimeMillis() - tempoInicioLoad;

        if (bancoDeDados == null || bancoDeDados.isEmpty()) {
            Formulario.mostrarMensagem("Erro Crítico: Falha ao ler o arquivo CSV. O sistema será encerrado.", "Falha no Banco de Dados");
            return;
        }

        // =====================================================================
        // 2. MONTAGEM DA INTERFACE GRÁFICA (Frontend)
        // =====================================================================
        Formulario tela = new Formulario("Sistema Central de Atendimento");

        // Painel de Informações do Servidor
        tela.adicionarTexto("STATUS DO SERVIDOR: ONLINE");
        tela.adicionarTexto("Tempo de inicialização do Índice Hash: " + tempoTotalLoad + " ms.");
        tela.adicionarTexto("Total de clientes indexados na RAM: " + bancoDeDados.size());
        tela.adicionarTexto(" "); // Quebra de linha visual

        // Campos de Entrada
        tela.adicionarInput("Nome do Cliente");
        tela.adicionarInput("Agência (Ex: 1000)");
        tela.adicionarInput("Conta (Ex: 12345-6)");

        // =====================================================================
        // 3. AÇÃO 1: Botão da Busca Lenta (Varredura de Nomes com Tabela)
        // =====================================================================
        tela.adicionarAcao("Busca por Nome O(N)", () -> {
            String nome = tela.resposta("Nome do Cliente");

            if (nome.isBlank()) {
                tela.exibirAlerta("Erro: O campo Nome não pode estar vazio para esta busca.");
                return;
            }

            tela.exibirAlerta(""); // Limpa alertas anteriores

            long t0 = System.nanoTime();
            List<ContaBancaria> resultados = BuscaSequencialNome.buscarNome(nome, bancoDeDados);
            long t1 = System.nanoTime();
            String tempoExecucao = String.valueOf((t1 - t0) / 1_000_000.0);

            if (resultados.isEmpty()) {
                Formulario.mostrarMensagem("Nenhum cliente com o nome '" + nome + "' foi localizado.", "Busca Sequencial");
            } else {
                exibirTabelaResultados(resultados, tempoExecucao);
            }
        });

        // =====================================================================
        // 4. AÇÃO 2: Botão da Busca de Alta Performance (Índice Hash + Binária)
        // =====================================================================
        tela.adicionarAcao("Busca de Conta O(1) + O(log M)", () -> {
            String agencia = tela.resposta("Agência (Ex: 1000)");
            String conta = tela.resposta("Conta (Ex: 12345-6)");

            if (agencia.isBlank() || conta.isBlank()) {
                tela.exibirAlerta("Erro: Para localizar uma conta, preencha a Agência e a Conta.");
                return;
            }

            tela.exibirAlerta(""); // Limpa alertas anteriores

            try {
                long t0 = System.nanoTime();
                // Tenta realizar a busca indexada
                ContaBancaria resultado = BuscaBinariaConta.obterConta(agencia, conta, motorHash, bancoDeDados);
                long t1 = System.nanoTime();

                String relatorio = "Tempo de execução: " + ((t1 - t0) / 1_000_000.0) + " ms\n\n";

                if (resultado != null) {
                    relatorio += "DADOS DO CLIENTE:\n" + resultado.getAll();
                } else {
                    relatorio += "Nenhum cliente encontrado com a agência " + agencia + " e conta " + conta + ".";
                }

                Formulario.mostrarMensagem(relatorio, "Resultado: Busca Indexada");

            } catch (Exception e) {
                // Exibe o erro na parte inferior do formulário
                tela.exibirAlerta("Erro na busca: " + e.getMessage());

                // Opcional: Imprime o stacktrace no console para depuração técnica
                e.printStackTrace();
            }
        });

        // =====================================================================
        // 5. RENDERIZAÇÃO
        // =====================================================================
        tela.mostrar();
    }

    /**
     * Renderiza uma Tabela Visual (JTable) com todos os resultados encontrados.
     */
    private static void exibirTabelaResultados(List<ContaBancaria> resultados, String tempoExecucao) {
        // 1. Definimos as colunas individuais
        String[] colunas = {"Nome do Cliente", "Agência", "Conta"};

        // 2. A Matriz agora tem várias colunas (exemplo: 3 colunas)
        Object[][] matrizDados = new Object[resultados.size()][3];

        for (int i = 0; i < resultados.size(); i++) {
            ContaBancaria cliente = resultados.get(i);
            matrizDados[i][0] = cliente.getTitular();
            matrizDados[i][1] = cliente.getAgencia();
            matrizDados[i][2] = cliente.getConta();
        }

        // 3. Criamos a Tabela Visual
        javax.swing.JTable tabela = new javax.swing.JTable(matrizDados, colunas);
        tabela.setRowHeight(25);
        tabela.setEnabled(false);

        // ====================================================================
        // NOVO: CONFIGURAÇÃO DE LARGURA DAS COLUNAS
        // A janela tem 600 pixels no total. Vamos fatiar esse espaço:
        // ====================================================================
        tabela.getColumnModel().getColumn(0).setPreferredWidth(300); // Nome ganha espaço de sobra
        tabela.getColumnModel().getColumn(1).setPreferredWidth(150); // Agência fica compacta
        tabela.getColumnModel().getColumn(2).setPreferredWidth(150); // Conta fica compacta

        // 4. Colocamos a tabela dentro de um painel com barra de rolagem (Scroll)
        javax.swing.JScrollPane painelRolagem = new javax.swing.JScrollPane(tabela);
        painelRolagem.setPreferredSize(new java.awt.Dimension(600, 300)); // Tamanho da janela

        // 5. Adicionamos o tempo de busca no cabeçalho
        javax.swing.JPanel painelFinal = new javax.swing.JPanel(new java.awt.BorderLayout());
        painelFinal.add(new javax.swing.JLabel("Tempo de processamento: " + tempoExecucao + " ms."), java.awt.BorderLayout.NORTH);
        painelFinal.add(painelRolagem, java.awt.BorderLayout.CENTER);

        // 6. Chamamos o popup nativo do Java enviando o painel inteiro
        javax.swing.JOptionPane.showMessageDialog(null, painelFinal, "Foram encontrados " + resultados.size() + " resultados", javax.swing.JOptionPane.PLAIN_MESSAGE);
    }
}