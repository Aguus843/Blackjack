package ar.edu.unlu.blackjack.Vista;

import ar.edu.unlu.blackjack.Controlador.Controlador;

public interface IVista {
    void setControlador(Controlador controlador);

    void iniciarJuego();
    void mostrarMensaje(String mensaje);
    void mostrarMensaje2(String mensaje);
    void mostrarCartasJugador();
    void mostrarManosDivididasJugadorVista();
    void mostrarPuntuacionParcialCrupier();
    void cicloPartida();

    void mostrarPuntuacionParcial();
}
