package ar.edu.unlu.blackjack.Modelo;

import ar.edu.unlu.blackjack.Controlador.Controlador;
import ar.edu.unlu.blackjack.Enumerado.Evento;

public interface Observable {
    void addObserver(Observador observador);
    void deleteObserver(Controlador controladorConsolaGrafica);
    void notificarObservadores(Evento evento);
}
