package ar.edu.unlu.blackjack.Modelo;

import java.io.Serializable;

public class DatosJugador implements Serializable {
    private static final long serialVersionUID = -403112224529969660L;
    private String nombre;
    private int cantidadGanadas;
    private float dineroTotalGanado;

    public DatosJugador(String nombre) {
        this.nombre = nombre;
        this.cantidadGanadas = 0;
        this.dineroTotalGanado = 0.0F;
    }

    public void registrarVictoria(float dineroGanado) {
        this.cantidadGanadas++;
        this.dineroTotalGanado += dineroGanado;
    }

    public String getNombre() { return nombre; }
    public int getCantidadGanadas() { return cantidadGanadas; }
    public float getDineroTotalGanado() { return dineroTotalGanado; }

    @Override
    public String toString() {
        return String.format("Jugador: %-15s | Partidas ganadas: %3d | Dinero total ganado: $%.2f", nombre, cantidadGanadas, dineroTotalGanado);
    }
}