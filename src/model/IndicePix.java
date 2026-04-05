package model;

import java.util.ArrayList;
import java.util.List;

public class IndicePix {
    private String chavePix;
    private List<ContaBancaria> contas;

    public IndicePix(String chavePix) {
        this.chavePix = chavePix;
        this.contas = new ArrayList<>();
    }

    public void adicionarConta(ContaBancaria c) {
        this.contas.add(c);
    }

    public String getChavePix() {
        return chavePix;
    }

    public List<ContaBancaria> getContas() {
        return contas;
    }
}
