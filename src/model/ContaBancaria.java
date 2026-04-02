package model;

public class ContaBancaria {

    private String cpf;
    private String titular;
    private String agencia;
    private String conta;
    private String tipoConta;
    private double saldo;
    private String chavePix;

/*
    O Construtor: ensina o Java a criar o objeto a partir de um array de Strings
    Exemplo: Temos a seguinte instância na memória do computador
    43260189769;Renan Rocha;8144;48444-5;Salário;147972.25;43260189769
    A Classe CarregadorCSV cria um vetor de string com 7 posições utilizando o método split (;)
    Cada posição representa um atributo, ContaBancaria lê.
*/
    public ContaBancaria(String[] dadosDoCsv) {
        this.cpf = dadosDoCsv[0];
        this.titular = dadosDoCsv[1];
        this.agencia = dadosDoCsv[2];
        this.conta = dadosDoCsv[3];
        this.tipoConta = dadosDoCsv[4];
        this.saldo = Double.parseDouble(dadosDoCsv[5]); // Converte String para número decimal
        this.chavePix = dadosDoCsv[6];
    }

    // Métodos Getters (Como os atributos são privados, precisamos liberar a leitura)
    // Isso será essencial para o seu motor de busca e para o JPanel
    public String getCpf() { return cpf; }
    public String getTitular() { return titular; }
    public String getAgencia() { return agencia; }
    public String getConta() { return conta; }
    public String getTipoConta() { return tipoConta; }
    public double getSaldo() { return saldo; }
    public String getChavePix() { return chavePix; }
    public String getAll() {
        return "{CPF: " + cpf +
                ", Titular: " + titular +
                ", Agência: " + agencia +
                ", Conta: " + conta +
                ", Tipo de Conta: " + tipoConta +
                ", Saldo: " + saldo +
                ", Pix: " + chavePix + "}";
    }

}