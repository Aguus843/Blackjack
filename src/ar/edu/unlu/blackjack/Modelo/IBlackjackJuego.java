package ar.edu.unlu.blackjack.Modelo;

import ar.edu.unlu.rmimvc.observer.IObservableRemoto;

import java.rmi.RemoteException;
import java.util.List;

public interface IBlackjackJuego extends IObservableRemoto {
    Crupier getCrupier() throws RemoteException;

    Mazo getMazo() throws RemoteException;

    void setIndiceJugador(int indice) throws RemoteException;

    void cambiarTurno() throws RemoteException;

    Jugador getJugadorActualTurno() throws RemoteException;

    int getIndice() throws RemoteException;

    void setDividirMano() throws RemoteException;

    boolean crupierSePaso21() throws RemoteException;

    void setApuesta(Jugador jugador, float monto) throws RemoteException;

    boolean realizarApuesta(String monto) throws RemoteException;

    Jugador configurarJugadores(String nickname, float saldo) throws RemoteException;

    void repartirCartasIniciales() throws RemoteException;

    void evaluarGanadores() throws RemoteException;

    void evaluarGanadoresBlackjack() throws RemoteException;

    void evaluarGanadoresNOBlackjack() throws RemoteException;

    void devolverApuesta(Jugador jugador, float apuesta) throws RemoteException;

    void adjudicarGanancia(Jugador jugador, float apuesta) throws RemoteException;

    void turnoCrupier() throws RemoteException;

    boolean todosJugadoresListos() throws RemoteException;

    List<String> getNombresJugadoresConectados() throws RemoteException;

    int getCantidadJugadoresConectados() throws RemoteException;

    int getCantidadJugadoresListos() throws RemoteException;

    boolean intentarComenzarPartida() throws RemoteException;

    void inicializarPartida() throws RemoteException;

    Jugador getJugadorPorNickname(String nickname) throws RemoteException;

    List<Carta> getCartasJugador(String nickname) throws RemoteException;

    int getPuntajeJugador(String nickname) throws RemoteException;

    void repartirCartaAJugador(String nickname) throws RemoteException;

    void jugadorSePlanta() throws RemoteException;

    void jugadorDoblaApuesta() throws RemoteException;

    void votarNuevaPartida(String nickname, boolean voto) throws RemoteException;

    String getEstadoVotacion() throws RemoteException;

    void iniciarVotacionNuevaPartida() throws RemoteException;

    boolean recargarSaldo(String nickname, float monto) throws RemoteException;

    void pedirCartaJugador() throws RemoteException;

    int getManoActualIndex() throws RemoteException;

    String obtenerRankingTotal() throws RemoteException;

}
