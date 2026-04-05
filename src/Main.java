import engine.BuscaBinariaConta;
import engine.BuscaBinariaCpf;
import engine.BuscaSequencialNome;
import engine.CarregadorCSV;
import engine.TabelaHashAgencias;
import model.ContaBancaria;
import model.IndiceCpf;
import view.Formulario;

import java.util.List;
import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        String caminhoArquivo = "data/contas_bancarias.csv";
        TabelaHashAgencias motorHash = new TabelaHashAgencias();

        long t0 = System.currentTimeMillis();
        List<ContaBancaria> bancoDeDados = CarregadorCSV.carregarDados(caminhoArquivo, motorHash);
        List<IndiceCpf> indicesCpf = CarregadorCSV.carregarIndicesCpf("data/indices_cpf.csv", bancoDeDados);
        long tempoLoad = System.currentTimeMillis() - t0;

        if (bancoDeDados == null || bancoDeDados.isEmpty()) {
            Formulario.mostrarMensagem("Erro Crítico: Arquivo não encontrado.", "Erro");
            return;
        }

        new Main().menuPrincipal(bancoDeDados, motorHash, indicesCpf, tempoLoad);
    }

    public void menuPrincipal(List<ContaBancaria> db, TabelaHashAgencias hash, List<IndiceCpf> indicesCpf, long tempoLoad) {
        Formulario f = new Formulario("Sistema Central de Atendimento");

        f.adicionarTexto("• Clientes Indexados: " + db.size());
        f.adicionarTexto("• Tempo de Carga: " + tempoLoad + " ms");
        f.adicionarTexto("\nSelecione o tipo de busca:");

        f.adicionarAcao("Busca por Nome", () -> {
            f.ocultar();
            menuNome(f, db);
        });

        f.adicionarAcao("Busca por Conta", () -> {
            f.ocultar();
            menuAgenciaConta(f, db, hash);
        });

        f.adicionarAcao("Busca por CPF", () -> {
            f.ocultar();
            menuCpf(f, indicesCpf);
        });

        f.mostrar();
    }

    private void menuNome(Formulario anterior, List<ContaBancaria> db) {
        Formulario f = new Formulario("Consulta por Nome");
        f.adicionarTexto("Digite o nome completo ou parte dele:");
        f.adicionarInput("Nome do Cliente", true);

        f.adicionarAcao("Voltar", () -> { f.ocultar(); anterior.mostrar(); });

        f.adicionarAcao("Pesquisar O(N)", () -> {
            if (!f.valido()) return;
            f.exibirAlerta("");

            long t0 = System.nanoTime();
            // CORREÇÃO: De 'answer' para 'resposta'
            String nomeBusca = f.resposta("Nome do Cliente");
            List<ContaBancaria> resultados = BuscaSequencialNome.buscarNome(nomeBusca, db);

            long t1 = System.nanoTime();
            String tempo = String.valueOf((t1 - t0) / 1_000_000.0);

            if (resultados.isEmpty()) {
                f.exibirAlerta("Nenhum registro encontrado.");
            } else {
                exibirTabelaResultados(resultados, tempo);
            }
        });
        f.mostrar();
    }

    private void menuAgenciaConta(Formulario anterior, List<ContaBancaria> db, TabelaHashAgencias hash) {
        Formulario f = new Formulario("Busca Indexada (Hash + Binária)");
        f.adicionarInput("Agência (Ex: 1000)", true);
        f.adicionarInput("Conta (Ex: 12345-6)", true);

        f.adicionarAcao("Voltar", () -> { f.ocultar(); anterior.mostrar(); });

        f.adicionarAcao("Localizar Ficha", () -> {
            if (!f.valido()) return;
            f.exibirAlerta("");

            try {
                long t0 = System.nanoTime();
                ContaBancaria res = BuscaBinariaConta.obterConta(f.resposta("Agência (Ex: 1000)"), f.resposta("Conta (Ex: 12345-6)"), hash, db);
                long t1 = System.nanoTime();
                String tempo = String.valueOf((t1 - t0) / 1_000_000.0);

                if (res != null) {
                    exibirDetalhesConta(res, tempo);
                } else {
                    f.exibirAlerta("Conta não localizada no banco de dados.");
                }
            } catch (Exception e) {
                f.exibirAlerta("Erro na busca!");
            }
        });
        f.mostrar();
    }

    // =====================================================================
    // MENU CPF (BUSCA BINÁRIA EM O(LOG N))
    // =====================================================================

    private void menuCpf(Formulario anterior, List<IndiceCpf> indicesCpf) {
        Formulario f = new Formulario("Busca por CPF");
        f.adicionarTexto("Digite o CPF do cliente:");
        f.adicionarInput("CPF", true);

        f.adicionarAcao("Voltar", () -> { f.ocultar(); anterior.mostrar(); });

        f.adicionarAcao("Pesquisar O(log N)", () -> {
            if (!f.valido()) return;
            f.exibirAlerta("");

            long t0 = System.nanoTime();
            String cpfBusca = f.resposta("CPF");
            IndiceCpf resultadoCpf = BuscaBinariaCpf.obterCpf(cpfBusca, indicesCpf);

            long t1 = System.nanoTime();
            String tempo = String.valueOf((t1 - t0) / 1_000_000.0);

            if (resultadoCpf == null || resultadoCpf.getContas().isEmpty()) {
                f.exibirAlerta("Nenhuma conta vinculada a este CPF.");
            } else {
                // Abre o navegador visual de contas para o mesmo CPF
                exibirNavegadorContas(resultadoCpf.getContas(), tempo);
            }
        });
        f.mostrar();
    }

    // =====================================================================
    // RENDERIZAÇÃO ESTILIZADA (Recuperando o visual bonito)
    // =====================================================================

    private void exibirTabelaResultados(List<ContaBancaria> resultados, String tempo) {
        String[] colunas = {"Nome do Cliente", "Agência", "Conta"};
        Object[][] matriz = new Object[resultados.size()][3];

        for (int i = 0; i < resultados.size(); i++) {
            ContaBancaria c = resultados.get(i);
            matriz[i][0] = c.getTitular();
            matriz[i][1] = c.getAgencia();
            matriz[i][2] = c.getConta();
        }

        JTable tabela = new JTable(matriz, colunas);
        tabela.setRowHeight(25);
        tabela.setEnabled(false);

        // Ajuste de larguras (O toque "bonito" que faltava)
        tabela.getColumnModel().getColumn(0).setPreferredWidth(300);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(100);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(100);

        JScrollPane painel = new JScrollPane(tabela);
        painel.setPreferredSize(new Dimension(600, 300));

        JPanel layout = new JPanel(new BorderLayout());
        layout.add(new JLabel(" Processamento: " + tempo + " ms"), BorderLayout.NORTH);
        layout.add(painel, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(null, layout, "Resultados Encontrados", JOptionPane.PLAIN_MESSAGE);
    }

    private void exibirDetalhesConta(ContaBancaria c, String tempo) {
        Object[][] matriz = {
                {"Nome do Titular", c.getTitular()},
                {"CPF do Titular", c.getCpf()},
                {"Agência Bancária", c.getAgencia()},
                {"Número da Conta", c.getConta()},
                {"Tipo de Conta", c.getTipoConta()},
                {"Saldo Atual", "R$ " + c.getSaldo()},
                {"Chave PIX", c.getChavePix()}
        };

        JTable tabela = new JTable(matriz, new String[]{"Atributo", "Informação"});
        tabela.setRowHeight(30);
        tabela.getColumnModel().getColumn(0).setPreferredWidth(150);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(450);

        JScrollPane painel = new JScrollPane(tabela);
        painel.setPreferredSize(new Dimension(600, 240));

        JPanel layout = new JPanel(new BorderLayout());
        layout.add(new JLabel(" Busca O(1) concluída em: " + tempo + " ms"), BorderLayout.NORTH);
        layout.add(painel, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(null, layout, "Ficha Individual do Cliente", JOptionPane.INFORMATION_MESSAGE);
    }

    private void exibirNavegadorContas(List<ContaBancaria> contas, String tempoInicial) {
        int index = 0;
        int total = contas.size();

        while (true) {
            ContaBancaria c = contas.get(index);
            Object[][] matriz = {
                    {"Nome do Titular", c.getTitular()},
                    {"CPF do Titular", c.getCpf()},
                    {"Agência Bancária", c.getAgencia()},
                    {"Número da Conta", c.getConta()},
                    {"Tipo de Conta", c.getTipoConta()},
                    {"Saldo Atual", "R$ " + c.getSaldo()},
                    {"Chave PIX", c.getChavePix()}
            };

            JTable tabela = new JTable(matriz, new String[]{"Atributo", "Informação"});
            tabela.setRowHeight(30);
            tabela.getColumnModel().getColumn(0).setPreferredWidth(150);
            tabela.getColumnModel().getColumn(1).setPreferredWidth(450);

            JScrollPane painel = new JScrollPane(tabela);
            painel.setPreferredSize(new Dimension(600, 240));

            JPanel layout = new JPanel(new BorderLayout());
            String rotuloTempo = tempoInicial + " ms (Conta " + (index + 1) + " de " + total + ")";
            layout.add(new JLabel(" Busca O(log N) concluída em: " + rotuloTempo), BorderLayout.NORTH);
            layout.add(painel, BorderLayout.CENTER);

            Object[] opcoes;
            if (total == 1) {
                opcoes = new Object[]{"Fechar"};
            } else if (index == 0) {
                opcoes = new Object[]{"Próxima >>", "Fechar"};
            } else if (index == total - 1) {
                opcoes = new Object[]{"<< Anterior", "Fechar"};
            } else {
                opcoes = new Object[]{"<< Anterior", "Próxima >>", "Fechar"};
            }

            int escolha = JOptionPane.showOptionDialog(null, layout, "Ficha de Clientes Vinculados",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, opcoes, opcoes[0]);

            if (escolha < 0) {
                break; // Usuário fechou a janela no 'X'
            }

            String btn = opcoes[escolha].toString();
            if (btn.equals("Próxima >>")) {
                index++;
            } else if (btn.equals("<< Anterior")) {
                index--;
            } else {
                break; // "Fechar" acionado
            }
        }
    }
}