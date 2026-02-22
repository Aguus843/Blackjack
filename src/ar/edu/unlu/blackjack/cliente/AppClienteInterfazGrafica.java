package ar.edu.unlu.blackjack.cliente;

import ar.edu.unlu.blackjack.Controlador.Controlador;
import ar.edu.unlu.blackjack.Vista.ConsolaGrafica.consolaGrafica;
import ar.edu.unlu.blackjack.Vista.IVista;
import ar.edu.unlu.blackjack.Vista.interfazGrafica.interfazGrafica;
import ar.edu.unlu.rmimvc.RMIMVCException;
import ar.edu.unlu.rmimvc.Util;
import ar.edu.unlu.rmimvc.cliente.Cliente;

import javax.swing.*;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class AppClienteInterfazGrafica {

    public static void main(String[] args) {
        ArrayList<String> ips = Util.getIpDisponibles();
        String ip = (String) JOptionPane.showInputDialog(
                null,
                "Seleccione la IP en la que escuchar� peticiones el cliente", "IP del cliente",
                JOptionPane.QUESTION_MESSAGE,
                null,
                ips.toArray(),
                null
        );
        String port = (String) JOptionPane.showInputDialog(
                null,
                "Seleccione el puerto en el que escuchar� peticiones el cliente", "Puerto del cliente",
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                9999
        );
        String ipServidor = (String) JOptionPane.showInputDialog(
                null,
                "Seleccione la IP en la corre el servidor", "IP del servidor",
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                null
        );
        String portServidor = (String) JOptionPane.showInputDialog(
                null,
                "Seleccione el puerto en el que corre el servidor", "Puerto del servidor",
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                8888
        );
        IVista vistaGrafica = new interfazGrafica();
        Controlador controladorInterfaz = new Controlador(vistaGrafica);

        // VISTA INTERFAZ
        vistaGrafica.setControlador(controladorInterfaz);
        Cliente c = new Cliente(ip, Integer.parseInt(port), ipServidor, Integer.parseInt(portServidor));
        try {
            c.iniciar(controladorInterfaz);
        } catch (RemoteException | RMIMVCException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        vistaGrafica.iniciarJuego();
    }

}