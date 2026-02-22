package ar.edu.unlu.blackjack.Modelo;

import java.io.Serializable;
import java.util.HashMap;

public class AdministradorJugadorMasGanador implements Serializable {

    private static final String ARCHIVO = "rankingJugadores.dat";

    private HashMap<String, DatosJugador> jugadores;
    private final Serializador serializador;

    public AdministradorJugadorMasGanador() {
        serializador = new Serializador();
        jugadores = cargarDesdeArchivo();
    }

    // guarda la victoria para el jugador (lo agrega si no existe)
    public void guardarVictoria(String nickname, float dineroGanado) {
        jugadores = cargarDesdeArchivo(); // recarga por si hubo cambios
        jugadores.putIfAbsent(nickname, new DatosJugador(nickname));
        jugadores.get(nickname).registrarVictoria(dineroGanado);
        serializador.escribir(ARCHIVO, jugadores);
    }

    // Devuelve todos los jugadores registrados
    public HashMap<String, DatosJugador> obtenerTodosLosJugadores() {
        return cargarDesdeArchivo();
    }

    private HashMap<String, DatosJugador> cargarDesdeArchivo() {
        Object leido = serializador.leer(ARCHIVO);
        if (leido instanceof HashMap) {
            return (HashMap<String, DatosJugador>) leido;
        }
        return new HashMap<>(); // primera vez que se usa, archivo vacío
    }
}