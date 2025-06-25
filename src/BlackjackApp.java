import ar.edu.unlu.blackjack.Controlador.Controlador;
import ar.edu.unlu.blackjack.Modelo.BlackjackJuego;
import ar.edu.unlu.blackjack.Modelo.IBlackjackJuego;
import ar.edu.unlu.blackjack.Vista.ConsolaGrafica.consolaGrafica;

import java.rmi.RemoteException;

public class BlackjackApp {
    public static void main(String[] args) {
        // Consola Grafica
        IBlackjackJuego modelo = new BlackjackJuego();
        consolaGrafica vista = new consolaGrafica();
        Controlador controlador = new Controlador(vista);
        try {
            modelo.agregarObservador(controlador);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        controlador.setModelo(modelo);
        // controlador.setModeloRemoto((T)modelo);
        vista.setControlador(controlador);
        vista.iniciarJuego();

        /*
        https://www.youtube.c11om/watch?v=nXiBcd3jLZY&ab_channel=WalterPanessi
        Clase RMI (Link al repo de libreria RMI)
         */
    }
}
