package ar.edu.unlu.blackjack.Modelo;

import ar.edu.unlu.rmimvc.observer.IObservableRemoto;

import java.rmi.RemoteException;

public interface IBlackjackJuego extends IObservableRemoto {

    Mano getManoJugador();

    void checkSiPuedePagarSeguroBlackjack() throws RemoteException;

    float getApuestaJugador();

    int getIndice();

    int getCantidadJugadores();

    void setCantidadJugadores(int cantidad);

    void setDividirMano() throws RemoteException;

    boolean crupierSePaso21();

    void setAjustarSaldo(Jugador jugador, float monto) throws RemoteException;

    void setApuesta(Jugador jugador, float monto) throws RemoteException;

    boolean realizarApuesta(String monto) throws RemoteException;

    void configurarJugadores(String nickname, float saldo);

    void repartirCartasIniciales(Jugador jugador);

    void evaluarGanadores() throws RemoteException;

    void evaluarGanadoresBlackjack() throws RemoteException;

    void evaluarGanadoresNOBlackjack() throws RemoteException;

    void devolverApuesta(Jugador jugador, float apuesta) throws RemoteException;

    void adjudicarGanancia(Jugador jugador, float apuesta) throws RemoteException;

    void turnoCrupier() throws RemoteException;
}
