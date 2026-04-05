package model;

import java.util.ArrayList;
import java.util.List;

public class IndiceCpf {
    private String cpf;
    private List<ContaBancaria> contas;

    public IndiceCpf(String cpf) {
        this.cpf = cpf;
        this.contas = new ArrayList<>();
    }

    public String getCpf() {
        return cpf;
    }

    public List<ContaBancaria> getContas() {
        return contas;
    }

    public void adicionarConta(ContaBancaria conta) {
        this.contas.add(conta);
    }
}
