package ar.edu.unlu.blackjack.Modelo;

import java.util.ArrayList;
import java.util.Collections;

public class Mazo {
    private ArrayList<Carta> cartas;

    public Mazo() {
        cartas = new ArrayList<Carta>();

        String[] palos = {"Diamantes", "Tréboles", "Corazones", "Picas"};
        String[] valores = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        // Tests
        // String[] valores = {"A", "J", "10", "K", "Q"};
        // String[] valores = {"A", "2", "3", "4", "5"};
        // String[] valores = {"A", "2", "8"};

        // Inicializo las cartas con sus valores
        for (String palo : palos) {
            for (String valor : valores) {
                cartas.add(new Carta(valor, palo));
            }
        }

        // Mezclo las cartas con barajar();

        barajar();
    }
    // Metodo para mezclar
    public void barajar() {
        Collections.shuffle(cartas);
    }

    // Hago el metodo en el que reparte las cartas una vez mezcladas
    public Carta repartirCarta(){
        // Valido si no hay mas cartas en la baraja
        if (cartas.isEmpty()){
            System.out.println("No hay mas cartas en la baraja.");
            return null;
        }
        return cartas.removeFirst(); // reparto y actualizo el mazo
    }

    // Hago un metodo para volver a barajar (Reinicio)
    public void reiniciarBaraja(){
        cartas.clear();
        String[] palos = {"Diamantes", "Tréboles", "Corazones", "Picas"};
        String[] valores = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        // String[] valores = {"A", "K"};

        // Inicializo las cartas con sus valores
        for (String palo : palos) {
            for (String valor : valores) {
                cartas.add(new Carta(valor, palo));
            }
        }
        barajar();

    }

    // Metodo para saber cuantas cartas quedan en la baraja (más que nada para validar)
    public int cartasRestantes(){
        return cartas.size();
    }
}
