package model;

public class IndiceAgencia {
    private String agencia;
    private int limiteInferior;
    private int limiteSuperior;

    public IndiceAgencia(String agencia, int limiteInferior, int limiteSuperior){
        this.agencia = agencia;
        this.limiteInferior = limiteInferior;
        this.limiteSuperior = limiteSuperior;
    }

    public void setAgencia(String ag){
        agencia = ag;
    }
    public void atualizarSuperior(int i){
        limiteSuperior = i;
    }
    public void atualizarInferior(int i){
        limiteInferior = i;
    }
    public String getAgencia() { return agencia; }
    public int getlimiteSuperior() { return limiteSuperior; }
    public int getlimiteInferior() { return limiteInferior; }
}
