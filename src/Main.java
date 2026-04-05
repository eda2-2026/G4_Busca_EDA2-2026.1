import engine.BuscaBinariaConta;
import engine.BuscaSequencialNome;
import engine.CarregadorCSV;
import engine.TabelaHashAgencias;
import model.ContaBancaria;
import view.Formulario;

import java.util.List;
import javax.swing.*;
import java.awt.*;

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

        tela.adicionarTexto("STATUS DO SERVIDOR: ONLINE");
        tela.adicionarTexto("Tempo de inicialização do Índice Hash: " + tempoTotalLoad + " ms.");
        tela.adicionarTexto("Total de clientes indexados na RAM: " + bancoDeDados.size());
        tela.adicionarTexto(" ");

        tela.adicionarInput("Nome do Cliente");
        tela.adicionarInput("Agência (Ex: 1000)");
        tela.adicionarInput("Conta (Ex: 12345-6)");

        // =====================================================================
        // 3. AÇÃO 1: Busca por Nome O(N) - Tabela Horizontal
        // =====================================================================
        tela.adicionarAcao("Busca por Nome O(N)", () -> {
            String nome = tela.resposta("Nome do Cliente");

            if (nome.isBlank()) {
                tela.exibirAlerta("Erro: O campo Nome não pode estar vazio para esta busca.");
                return;
            }

            tela.exibirAlerta("");

            try {
                long t0 = System.nanoTime();
                List<ContaBancaria> resultados = BuscaSequencialNome.buscarNome(nome, bancoDeDados);
                long t1 = System.nanoTime();
                String tempoExecucao = String.valueOf((t1 - t0) / 1_000_000.0);

                if (resultados.isEmpty()) {
                    Formulario.mostrarMensagem("Nenhum cliente com o nome '" + nome + "' foi localizado.", "Busca Sequencial");
                } else {
                    exibirTabelaResultados(resultados, tempoExecucao);
                }
            } catch (Exception e) {
                tela.exibirAlerta("Erro na busca por nome: " + e.getMessage());
            }
        });

        // =====================================================================
        // 4. AÇÃO 2: Busca Indexada O(1) + O(log M) - Ficha Vertical com Try-Catch
        // =====================================================================
        tela.adicionarAcao("Busca de Conta O(1) + O(log M)", () -> {
            String agencia = tela.resposta("Agência (Ex: 1000)");
            String conta = tela.resposta("Conta (Ex: 12345-6)");

            if (agencia.isBlank() || conta.isBlank()) {
                tela.exibirAlerta("Erro: Para localizar uma conta, preencha a Agência e a Conta.");
                return;
            }

            tela.exibirAlerta("");

            try {
                long t0 = System.nanoTime();
                // Realiza a busca utilizando o motor hash e busca binária interna
                ContaBancaria resultado = BuscaBinariaConta.obterConta(agencia, conta, motorHash, bancoDeDados);
                long t1 = System.nanoTime();
                String tempoExecucao = String.valueOf((t1 - t0) / 1_000_000.0);

                if (resultado != null) {
                    // Exibe a ficha técnica vertical do cliente
                    exibirDetalhesConta(resultado, tempoExecucao);
                } else {
                    Formulario.mostrarMensagem("Nenhum cliente encontrado com a agência " + agencia + " e conta " + conta + ".", "Resultado: Busca Indexada");
                }

            } catch (Exception e) {
                // Feedback de erro diretamente na interface via formulário
                tela.exibirAlerta("Erro na busca indexada: " + e.getMessage());
                e.printStackTrace();
            }
        });

        tela.mostrar();
    }

    /**
     * Renderiza uma Tabela Horizontal (Listagem) para múltiplos resultados.
     */
    private static void exibirTabelaResultados(List<ContaBancaria> resultados, String tempoExecucao) {
        String[] colunas = {"Nome do Cliente", "Agência", "Conta"};
        Object[][] matrizDados = new Object[resultados.size()][3];

        for (int i = 0; i < resultados.size(); i++) {
            ContaBancaria cliente = resultados.get(i);
            matrizDados[i][0] = cliente.getTitular();
            matrizDados[i][1] = cliente.getAgencia();
            matrizDados[i][2] = cliente.getConta();
        }

        JTable tabela = new JTable(matrizDados, colunas);
        tabela.setRowHeight(25);
        tabela.setEnabled(false);
        tabela.getColumnModel().getColumn(0).setPreferredWidth(300);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(150);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(150);

        JScrollPane painelRolagem = new JScrollPane(tabela);
        painelRolagem.setPreferredSize(new Dimension(600, 300));

        JPanel painelFinal = new JPanel(new BorderLayout());
        painelFinal.add(new JLabel("Tempo de processamento: " + tempoExecucao + " ms."), BorderLayout.NORTH);
        painelFinal.add(painelRolagem, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(null, painelFinal, "Resultados da Busca (" + resultados.size() + ")", JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Renderiza uma Tabela Vertical (Ficha Individual) para um único cliente.
     */
    private static void exibirDetalhesConta(ContaBancaria cliente, String tempoExecucao) {
        String[] colunas = {"Atributo", "Informação Cadastral"};
        Object[][] matrizDados = {
                {"Nome do Titular", cliente.getTitular()},
                {"CPF do Titular", cliente.getCpf()},
                {"Agência Bancária", cliente.getAgencia()},
                {"Número da Conta", cliente.getConta()},
                {"Tipo de Conta", cliente.getTipoConta()},
                {"Saldo", cliente.getSaldo()},
                {"Chave PIX", cliente.getChavePix()}
        };

        JTable tabela = new JTable(matrizDados, colunas);
        tabela.setRowHeight(30);
        tabela.setEnabled(false);
        tabela.getColumnModel().getColumn(0).setPreferredWidth(150);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(450);

        JScrollPane painelRolagem = new JScrollPane(tabela);
        painelRolagem.setPreferredSize(new Dimension(600, 250));

        JPanel painelFinal = new JPanel(new BorderLayout());
        painelFinal.add(new JLabel("Busca Indexada realizada em: " + tempoExecucao + " ms."), BorderLayout.NORTH);
        painelFinal.add(painelRolagem, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(null, painelFinal, "Ficha Completa do Cliente", JOptionPane.INFORMATION_MESSAGE);
    }
}