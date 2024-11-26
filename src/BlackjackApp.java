import ar.edu.unlu.blackjack.Controlador.Controlador;
import ar.edu.unlu.blackjack.Modelo.BlackjackJuego;
import ar.edu.unlu.blackjack.Vista.ConsolaGrafica.consolaGrafica;

import java.rmi.RemoteException;

public class BlackjackApp {
    public static void main(String[] args) {
        // Consola Grafica
        BlackjackJuego modelo = new BlackjackJuego();
        consolaGrafica vista = new consolaGrafica();
        Controlador controlador = new Controlador(vista);
        try {
            modelo.agregarObservador(controlador);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        controlador.setModelo(modelo);
        vista.setControlador(controlador);
        vista.iniciarJuego();

        // Consola Normal
//        IVista vistaConsola = (IVista) new VistaConsola();
//        Controlador controlador2 = new Controlador(vistaConsola);
//        modelo.addObserver(controlador2);
//        vistaConsola.setControlador(controlador2);
//        controlador2.setModelo(new BlackjackJuego());
//        vistaConsola.iniciarJuego();
    }
}
