package ar.edu.unlu.blackjack.Modelo;

public class Regla {
    private static final int valorMaximo = 21;
    private static final int LimiteCrupier = 17;
    public Regla(){
    }
    public boolean esBlackjack(Mano manoActual) {
        return manoActual.getMano().size() == 2 && valorActualizadoMano(manoActual) == valorMaximo;
    }

    public boolean sePaso21(Mano manoActual) {
        return valorActualizadoMano(manoActual) > valorMaximo;
    }

    public boolean puedeDoblar(Jugador jugador){
        return jugador.getManoActual().getMano().size() == 2 && jugador.getSaldo() >= jugador.getApuesta()*2;
    }

    public boolean puedeDividir(Jugador jugador){
        Mano mano = jugador.getManoActual();
        if (mano.getMano().size() == 2) return mano.getMano().getFirst().getValorNumerico() == mano.getMano().get(1).getValorNumerico();
        return false;
    }

    public boolean debePedirCrupier(Mano manoCrupier){
        return valorActualizadoMano(manoCrupier) < LimiteCrupier;
    }

    public int valorActualizadoMano(Mano mano){
        int valor = 0;
        int ases = 0;
        for (Carta carta : mano.getMano()) {
            if (carta.getValorNumerico() == 11){
                ases++;
                valor += 11;
            }else if (carta.getValorNumerico() >= 10){
                valor += 10;
            }else {
                valor += carta.getValorNumerico();
            }
        }
        while (valor > valorMaximo && ases > 0) {
            valor -= 10;
            ases--;
        }
        return valor;
    }
}
