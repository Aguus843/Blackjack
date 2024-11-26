package ar.edu.unlu.blackjack.Modelo;

public class Carta {
    private String palo; // Corazon, Diamante, Pica, Trebol
    private String valor; // 2-10, J, Q, K, A

    public Carta(String valor, String palo) {
        this.palo = palo;
        this.valor = valor;
    }

    // Getters
    public String getPalo() {
        return palo;
    }
    public String getValor() {
        return valor;
    }

    public int getValorNumerico(){
        switch (valor){
            case "A":
                return 11; // As puede valer 11 dependiendo del contexto
            case "J":
            case "Q":
            case "K":
                return 10; // Cualquiera de las tres valen 10
            default:
                return Integer.parseInt(valor);
        }
    }

    // Muestro la carta

    public String mostrarCarta(){
        return valor + " de" + palo;
    }

}
