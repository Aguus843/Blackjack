package ar.edu.unlu.blackjack.Vista;
import ar.edu.unlu.blackjack.Controlador.Controlador;

import java.rmi.RemoteException;
import java.util.List;

public interface IVista {
    void setControlador(Controlador controlador);

    void iniciarJuego();
    void mostrarMensaje(String mensaje);
    void mostrarCartasJugador() throws RemoteException;
    void mostrarManosDivididasJugadorVista() throws RemoteException;
    void mostrarPuntuacionParcialCrupier() throws RemoteException;
    void cicloPartida() throws RemoteException;
    void notificarTurnoJugador() throws RemoteException;
    void mostrarManoJugador() throws RemoteException;

    void mostrarPuntuacionParcial() throws RemoteException;
    void mostrarSalaEspera(List<String> jugadores, int maximo);

    void comenzarPartida() throws RemoteException;

    void notificarTurnoApuesta() throws RemoteException;

    void mostrarResultados() throws RemoteException;

    void mostrarVotacion() throws RemoteException;

    void actualizarEstadoVotacion() throws RemoteException;

    void jugadorLlegoA21() throws RemoteException;

    void jugadorSePaso() throws RemoteException;

    void cambiarAMano2() throws RemoteException;

}
