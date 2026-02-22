package ar.edu.unlu.blackjack.Modelo;

import java.io.Serializable;
import java.util.List;

/**
 * Clase Crupier que extiende de Jugador
 * Usa el sistema de manos heredado de Jugador para ser compatible con RMI
 */
public class Crupier extends Jugador implements Serializable {
    private static final int LimiteCrupier = 17;
    public Crupier() {
        super("Crupier", 0);
    }

    public List<Carta> getManoCarta() {
        if (getManos().isEmpty()) {
            throw new IllegalStateException("El crupier no tiene manos");
        }
        return getManos().get(0).getMano();
    }

    public Mano getManoCrupier() {
        if (getManos().isEmpty()) {
            throw new IllegalStateException("El crupier no tiene manos");
        }
        return getManos().get(0);
    }

    public int getPuntajeCrupier() {
        if (getManos().isEmpty()) {
            return 0;
        }
        return getManos().get(0).getPuntaje();
    }

    public void pedirCarta(Carta carta) {
        if (getManos().isEmpty()) {
            agregarMano();
        }
        getManos().get(0).recibirCarta(carta);
    }

    public boolean tieneAsPrimera() {
        if (getManos().isEmpty() || getManoCrupier().getMano().isEmpty()) {
            return false;
        }
        return getManoCrupier().getMano().get(0).getValor().equals("A");
    }

    public boolean debePedirCarta() {
        if (getManos().isEmpty()) {
            return false;
        }
        return getManos().get(0).getPuntaje() < LimiteCrupier;
    }

    public int getPuntaje() {
        return getPuntajeCrupier();
    }

    public String mostrarPrimeraCarta() {
        if (getManos().isEmpty() || getManos().get(0).getMano().isEmpty()) {
            return "No se puede mostrar la carta del crupier dado que no tiene ninguna.";
        }
        Carta primeraCarta = getManos().get(0).getMano().get(0);
        return primeraCarta.getValor() + " de " + primeraCarta.getPalo();
    }

    @Override
    public boolean tieneBlackjack() {
        if (getManos().isEmpty()) {
            return false;
        }

        Mano manoCheck = getManoCrupier();
        if (manoCheck.getMano().size() < 2) {
            return false;
        }

        String primeraCarta = manoCheck.getMano().get(0).getValor();
        String segundaCarta = manoCheck.getMano().get(1).getValor();

        // As + 10 o J o Q o K
        if (primeraCarta.equals("A") && (segundaCarta.equals("10") || segundaCarta.equals("J") || segundaCarta.equals("Q") || segundaCarta.equals("K"))) {
            return true;
        }

        // Carta de 10 o J o Q o K + As
        return (primeraCarta.equals("10") || primeraCarta.equals("J") || primeraCarta.equals("Q") || primeraCarta.equals("K")) && segundaCarta.equals("A");
    }
}