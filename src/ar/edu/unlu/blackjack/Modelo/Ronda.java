package ar.edu.unlu.blackjack.Modelo;

import ar.edu.unlu.blackjack.Enumerado.Estado;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ronda {
    private List<Jugador> jugadores;
    private Map<Jugador, List<Carta>> jugadorYCartas;
    Mazo mazo;
    private Map<Jugador, Float> apuesta;
    private Map<Jugador, String> resultado;
    private Crupier crupier;
    private Regla regla;
    private Estado estado;

    public Ronda() {
        this.apuesta = new HashMap<>();
        this.resultado = new HashMap<>();
    }

    public void iniciarRonda(){
        estado = Estado.RONDA_COMENZADA;
        repartirCartasGlobal(jugadorYCartas, mazo);

    }



    private void procesarAccionesJugador(){
        // turnoJugador <--
        for (Jugador jugador : jugadores) {
            while (jugador.debeSeguirJugando()){
                
            }

        }
    }

    public void repartirCartasGlobal(Map<Jugador, List<Carta>> jugadoresYCartas, Mazo mazo){
        for (Jugador jugador : jugadoresYCartas.keySet()){
            List<Carta> mano = jugadoresYCartas.get(jugador);
            mano.add(mazo.repartirCarta());
            mano.add(mazo.repartirCarta());
            // asigno las cartas a la respectiva mano del jugador
        }

    }

    public void evaluarGanadores(int indice){
        int puntajeCrupier = crupier.getPuntaje();
        if (crupier.tieneBlackjack()){

        }
        for (Jugador jugador : jugadores) {
            if (jugador.multiplesManos()){
                // logica para multiples manos
            }else{
                if (regla.esBlackjack(jugador.getManoActual())){
                    resultado.put(jugador, resultado.get(jugador)+ " ganaste la apuesta!");
                }else if (regla.sePaso21(jugador.getManoActual())){
                    resultado.put(jugador, resultado.get(jugador)+ " te pasaste de 21! Perdiste.");
                }else{
                    if (crupier.getManoCrupier().sePaso21()){
                        resultado.put(jugador, resultado.get(jugador)+ " ganaste la apuesta dado que el crupier se pasó de 21!");
                    }else if (crupier.getManoCrupier().getPuntaje() > jugador.getManoActual().getPuntaje()){
                        resultado.put(jugador, resultado.get(jugador) + " perdiste dado que el crupier obtuvo mas puntaje que vos!");
                    }else if (crupier.getManoCrupier().getPuntaje() < jugador.getManoActual().getPuntaje()){
                        resultado.put(jugador, resultado.get(jugador) + " ganaste la apuesta!");
                    }else resultado.put(jugador, resultado.get(jugador) + " obtuviste un empate dado que ambos tienen el mismo puntaje!");
                }
            }
        }
    }

    public void terminarRonda(){
        estado = Estado.RONDA_TERMINADA;
    }
}
