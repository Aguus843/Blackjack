package ar.edu.unlu.blackjack.Controlador;

import ar.edu.unlu.blackjack.Vista.IVista;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IControlador extends Remote {

    void registrarCliente(IVista vista, String nickname, float saldo) throws RemoteException;

    void marcarListo(String nickname) throws RemoteException;

    boolean realizarApuesta(String nickname, float saldo) throws RemoteException;

    void pedirCarta(String nickname) throws RemoteException;

    void plantarse(String nickname) throws RemoteException;

    void doblar(String nickname) throws RemoteException;

    void dividir(String nickname) throws RemoteException;

    void recargarSaldo(String nickname, float saldo) throws RemoteException;

}
