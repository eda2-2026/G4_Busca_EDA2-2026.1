package view;
import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

public class Formulario {
    private final Map<String, JTextField> mapaInputs = new LinkedHashMap<>();
    private final Map<String, JComboBox<String>> mapaDropdowns = new HashMap<>();
    private final Map<String, JPanel> mapaBotoes = new HashMap<>();
    private final Map<String, ButtonGroup> mapaRadios = new HashMap<>();
    private final Set<String> camposObrigatorios = new HashSet<>();
    private final JLabel mensagemAlerta = new JLabel();
    private JDialog dialog;
    private JFrame frameInvisivel;
    private JPanel painelPrincipal;
    private JPanel painelInferior;
    private JPanel painelCompleto;
    private JPanel painelAlerta;
    private JPanel painelAcoes;
    private JPanel painelTexto;

    private static final String TITULO_PADRAO = "Interface";

    // -------------------------------------------------------------------------
    // Exceções internas
    // -------------------------------------------------------------------------

    private static class CampoVazioException extends RuntimeException {
        public CampoVazioException() {
            super("Campo obrigatório não preenchido.");
        }
    }

    private static class EntidadeDuplicadaException extends RuntimeException {
        public EntidadeDuplicadaException(String mensagem) {
            super(mensagem);
        }
    }

    private static class EntidadeInexistenteException extends RuntimeException {
        public EntidadeInexistenteException(String mensagem) {
            super(mensagem);
        }
    }

    // -------------------------------------------------------------------------
    // FiltroRegex interno
    // -------------------------------------------------------------------------

    private static class FiltroRegex extends DocumentFilter {
      private final Pattern pattern;
  
      public FiltroRegex(String regex) {
          this.pattern = Pattern.compile(regex);
      }
  
      @Override
      public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
              throws BadLocationException {
          StringBuilder sb = new StringBuilder(fb.getDocument().getText(0, fb.getDocument().getLength()));
          sb.insert(offset, string);
          if (pattern.matcher(sb.toString()).matches()) {
              super.insertString(fb, offset, string, attr);
          }
      }
  
      @Override
      public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
              throws BadLocationException {
          StringBuilder sb = new StringBuilder(fb.getDocument().getText(0, fb.getDocument().getLength()));
          sb.replace(offset, offset + length, text);
          if (pattern.matcher(sb.toString()).matches()) {
              super.replace(fb, offset, length, text, attrs);
          }
      }
  }
  
  private static String obterExpressaoRegular(String regex) {
      return switch (regex.toUpperCase()) {
          case "DATA"      -> "^\\d{0,2}(/\\d{0,2}(/\\d{0,4})?)?$";
          case "HORARIO"   -> "^\\d{0,2}(:\\d{0,2})?$";
          case "ALGARISMOS" -> "^\\d*$";
          default          -> regex;
      };
  }

    // -------------------------------------------------------------------------
    // Construtores e montagem
    // -------------------------------------------------------------------------

    public Formulario() {
        montarFormulario();
        criarDialogo(TITULO_PADRAO);
    }

    public Formulario(String titulo) {
        montarFormulario();
        criarDialogo(titulo);
    }

    public static void mostrarMensagem(String mensagem, String titulo) {
        JOptionPane.showMessageDialog(null, mensagem, titulo, JOptionPane.PLAIN_MESSAGE);
    }

    public static void mostrarMensagem(String mensagem) {
        JOptionPane.showMessageDialog(null, mensagem, TITULO_PADRAO, JOptionPane.PLAIN_MESSAGE);
    }

    private void montarFormulario() {
        gerarPaineis();
        configurarMensagemAlerta();
        montarPainelInferior();
        montarPainelCompleto();
    }

    private void criarDialogo(String titulo) {
        criarFrameInvisivel();
        dialog = new JDialog(frameInvisivel, titulo, true);
        dialog.setContentPane(painelCompleto);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                frameInvisivel.dispose();
                System.exit(0);
            }
        });
    }

    private void criarFrameInvisivel() {
        frameInvisivel = new JFrame();
        frameInvisivel.setUndecorated(true);
        frameInvisivel.setSize(0, 0);
        frameInvisivel.setLocationRelativeTo(null);
        frameInvisivel.setOpacity(0f);
        frameInvisivel.setVisible(true);
    }

    private void montarPainelCompleto() {
        painelCompleto.add(painelTexto, BorderLayout.NORTH);
        painelCompleto.add(painelPrincipal, BorderLayout.CENTER);
        painelCompleto.add(painelInferior, BorderLayout.SOUTH);
    }

    private void montarPainelInferior() {
        painelInferior.add(painelAlerta, BorderLayout.CENTER);
        painelInferior.add(painelAcoes, BorderLayout.SOUTH);
    }

    private void configurarMensagemAlerta() {
        mensagemAlerta.setForeground(Color.RED);
        painelAlerta.add(mensagemAlerta);
        painelAlerta.setPreferredSize(new Dimension(0, 20));
        painelAlerta.setVisible(true);
    }

    private void gerarPaineis() {
        painelCompleto = new JPanel(new BorderLayout());
        painelCompleto.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        painelAcoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelInferior = new JPanel(new BorderLayout());

        painelPrincipal = new JPanel(new GridLayout(0, 2));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(2, 2, 8, 2));

        painelAlerta = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelAlerta.setPreferredSize(new Dimension(0, 30));

        painelTexto = new JPanel();
        painelTexto.setLayout(new BoxLayout(painelTexto, BoxLayout.Y_AXIS));
        painelTexto.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
    }

    // -------------------------------------------------------------------------
    // API pública
    // -------------------------------------------------------------------------

    public void mostrar() {
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    public void ocultar() {
        dialog.setVisible(false);
    }

    public String resposta(String input) {
        JTextField campo = mapaInputs.get(input);
        if (campo == null) {
            throw new EntidadeInexistenteException("O input \"" + input + "\" não existe");
        }
        return campo.getText();
    }

    public String opcao(String dropdown) {
        JComboBox<String> opcoes = mapaDropdowns.get(dropdown);
        if (opcoes == null) {
            throw new EntidadeInexistenteException("O dropdown \"" + dropdown + "\" não existe");
        }
        Object selecionado = opcoes.getSelectedItem();
        assert selecionado != null;
        return selecionado.toString();
    }

    public String selecao(String radio) {
        ButtonGroup opcoes = mapaRadios.get(radio);
        if (opcoes == null) {
            throw new EntidadeInexistenteException("O radio \"" + radio + "\" não existe");
        }
        for (AbstractButton botao : Collections.list(opcoes.getElements())) {
            if (botao.isSelected()) return botao.getText();
        }
        throw new CampoVazioException();
    }

    public void adicionarInput(String texto, boolean obrigatorio, String regex) {
        verificarInputValido(texto);
        String labelTexto = texto + (obrigatorio ? "*" : "");
        painelPrincipal.add(new JLabel(labelTexto));

        JTextField input = new JTextField(20);
        painelPrincipal.add(input);
        mapaInputs.put(texto, input);

        if (obrigatorio) camposObrigatorios.add(texto);

        if (regex != null) {
            String expressao = obterExpressaoRegular(regex);
            ((AbstractDocument) input.getDocument()).setDocumentFilter(new FiltroRegex(expressao));
        }
        atualizar();
    }

    public void adicionarSenha(String texto) {
        verificarInputValido(texto);
        painelPrincipal.add(new JLabel(texto + "*"));

        JPasswordField inputSenha = new JPasswordField(20);
        painelPrincipal.add(inputSenha);
        mapaInputs.put(texto, inputSenha);
        camposObrigatorios.add(texto);
        atualizar();
    }

    public void adicionarInput(String texto) {
        adicionarInput(texto, false, null);
    }

    public void adicionarInput(String texto, boolean obrigatorio) {
        adicionarInput(texto, obrigatorio, null);
    }

    public void adicionarInput(String texto, String regex) {
        adicionarInput(texto, false, regex);
    }

    public void preencherInput(String input, String texto) {
        JTextField campo = mapaInputs.get(input);
        if (campo != null) {
            campo.setText(texto);
        } else {
            throw new EntidadeInexistenteException("O componente \"" + input + "\" não existe");
        }
    }

    public void preencherInput(String novoValor) {
        if (mapaInputs.isEmpty()) return;
        String ultimoInput = null;
        for (String chave : mapaInputs.keySet()) ultimoInput = chave;
        if (ultimoInput != null) preencherInput(ultimoInput, novoValor);
    }

    public void adicionarDropdown(String texto, String[] opcoes) {
        verificarDropdownValido(texto);
        painelPrincipal.add(new JLabel(texto));

        JComboBox<String> dropdown = new JComboBox<>(opcoes);
        painelPrincipal.add(dropdown);
        mapaDropdowns.put(texto, dropdown);
        atualizar();
    }

    public void adicionarRadio(String texto, String[] opcoes) {
        if (mapaRadios.containsKey(texto)) {
            throw new EntidadeDuplicadaException("Componente \"" + texto + "\" duplicado");
        }
        painelPrincipal.add(new JLabel(texto + "*"));

        JPanel painelRadio = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ButtonGroup radio = new ButtonGroup();
        boolean selecionar = true;
        for (String opcao : opcoes) {
            JRadioButton botaoRadio = new JRadioButton(opcao);
            radio.add(botaoRadio);
            painelRadio.add(botaoRadio);
            if (selecionar) { botaoRadio.setSelected(true); selecionar = false; }
        }
        painelPrincipal.add(painelRadio);
        mapaRadios.put(texto, radio);
        atualizar();
    }

    public void adicionarTexto(String texto) {
        for (String linha : texto.split("\n", -1)) {
            JLabel componente = new JLabel(linha.isBlank() ? " " : linha);
            painelTexto.add(componente);
        }
    }

    public void exibirAlerta(String texto) {
        mensagemAlerta.setForeground(Color.RED);
        mensagemAlerta.setText((texto == null || texto.isBlank()) ? " " : texto);
        atualizar();
    }

    public void exibirAlerta(String texto, Color cor) {
        exibirAlerta(texto);
        if (cor != null) mensagemAlerta.setForeground(cor);
        atualizar();
    }

    public void exibirAlerta() {
        mensagemAlerta.setText(" ");
        atualizar();
    }

    public void copiarTexto(String texto) {
        StringSelection selecao = new StringSelection(texto);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selecao, null);
        exibirAlerta("'" + texto + "' copiado para a área de transferência!", Color.BLUE);
    }

    public void salvarArquivo(String texto, String nomeArquivo) {
        try {
            Path pastaOutput = Paths.get("saida");
            if (!Files.exists(pastaOutput)) Files.createDirectories(pastaOutput);
            Path caminhoCompleto = pastaOutput.resolve(nomeArquivo);
            Files.writeString(caminhoCompleto, texto);
            exibirAlerta("Arquivo salvo com sucesso!", Color.BLUE);
        } catch (IOException e) {
            exibirAlerta("Erro ao salvar arquivo.");
            System.out.println(e.getMessage());
        }
    }

    public void adicionarBotao(String texto, String textoBotao, Runnable acao) {
        if (texto == null) texto = "";
        JPanel painelBotoesLinha = mapaBotoes.get(texto);
        if (painelBotoesLinha == null) {
            painelPrincipal.add(new JLabel(texto));
            painelBotoesLinha = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            painelPrincipal.add(painelBotoesLinha);
            mapaBotoes.put(texto, painelBotoesLinha);
        }
        JButton botao = new JButton(textoBotao);
        botao.addActionListener(e -> acao.run());
        painelBotoesLinha.add(botao);
        atualizar();
    }

    public void adicionarAcao(String texto, Runnable acao) {
        JButton botao = new JButton(texto);
        botao.addActionListener(e -> acao.run());
        painelAcoes.add(botao);
        atualizar();
    }

    public boolean valido() {
        for (String campo : camposObrigatorios) {
            String valor = resposta(campo);
            if (valor == null || valor.isBlank()) {
                exibirAlerta("O campo \"" + campo + "\" é obrigatório.");
                return false;
            }
        }
        exibirAlerta(null);
        return true;
    }

    private void verificarInputValido(String texto) {
        if (mapaInputs.containsKey(texto)) {
            throw new EntidadeDuplicadaException("Componente \"" + texto + "\" duplicado.");
        }
    }

    private void verificarDropdownValido(String texto) {
        if (mapaDropdowns.containsKey(texto)) {
            throw new EntidadeDuplicadaException("Componente \"" + texto + "\" duplicado.");
        }
    }

    private void atualizar() {
        painelAcoes.revalidate();
        painelAcoes.repaint();
        painelPrincipal.revalidate();
        painelPrincipal.repaint();
    }
}
