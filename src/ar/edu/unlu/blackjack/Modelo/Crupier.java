package ar.edu.unlu.blackjack.Modelo;

import java.util.List;

public class Crupier extends Jugador{
    private static final int LimiteCrupier = 17;
    private Mano mano;

    public Crupier() {
        super("Crupier", 0);
        mano = new Mano();
    }

    public List<Carta> getManoCarta(){
        return mano.getMano();
    }
    public Mano getManoCrupier(){
        return mano;
    }
    public int getPuntajeCrupier(){
        return mano.getPuntaje();
    }
    public void pedirCarta(Carta carta){
        mano.recibirCarta(carta);
    }
    public void clearMano(){
        mano.nuevaMano();
    }
    public boolean tieneAsPrimera(){
        return getManoCrupier().getMano().getFirst().getValor().equals("A");
    }

    // Metodo para saber si debe pedir otra carta
    public boolean debePedirCarta(){
        return mano.getPuntaje() < LimiteCrupier;
    }
    public int getPuntaje(){
        return mano.getPuntaje();
    }

    public String mostrarPrimeraCarta(){
        // Valido primero
        if (mano.getMano().isEmpty()){
            return ("No se puede mostrar el carta de crupier dado que no tiene ninguna.");
        }else{
            return mano.getMano().get(0).getValor() + " de " + mano.getMano().get(0).getPalo();
            // Imprime la primera carta del crupier siendo que la segunda está oculta.
        }
    }

    @Override
    public boolean tieneBlackjack(){
        Mano manoCheck = getManoCrupier();
        String primeraCarta = manoCheck.getMano().getFirst().getValor();
        String segundaCarta = manoCheck.getMano().get(1).getValor();
        if (primeraCarta.equals("A") && (segundaCarta.equals("10") || segundaCarta.equals("J") || segundaCarta.equals("Q") || segundaCarta.equals("K"))) {
            return true;
        } else return (primeraCarta.equals("10") || primeraCarta.equals("J") || primeraCarta.equals("Q") || primeraCarta.equals("K")) && segundaCarta.equals("A");
    }

    public void mostrarMano(){
        System.out.println();
        int sumatoriaPuntaje = 0;
        int ases = 0;
        int aux = 0;
        System.out.println("El crupier tiene:");
        for (Carta carta : mano.getMano()) {
            System.out.printf("%s de %s\n", carta.getValor(), carta.getPalo());
            sumatoriaPuntaje += carta.getValorNumerico();
            if (carta.getValorNumerico() == 1) ases++;
        }
        while (sumatoriaPuntaje > 21 && ases > 0){
            aux = sumatoriaPuntaje;
            sumatoriaPuntaje -= 10;
            ases--;
        }
        if ((tieneAsPrimera() && sumatoriaPuntaje < 21) && aux < 21){
            System.out.printf("El puntaje actual del crupier es de: %d/%d\n", mano.getPuntaje()-10, mano.getPuntaje());
        }
        System.out.println("El puntaje actual del crupier es de: " + mano.getPuntaje());
    }

    public boolean tieneCarta(){
        // return getManoCrupier().size() > 0;
        return !getManoCrupier().getMano().isEmpty();
    }

}
