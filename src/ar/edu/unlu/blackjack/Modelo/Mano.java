package ar.edu.unlu.blackjack.Modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Mano{
    private List<Carta> mano;
    private int puntaje;
    private boolean doblo;
    private Scanner scanner;

    public Mano(){
        this.mano = new ArrayList<Carta>();
        this.puntaje = 0;
        this.doblo = false;
        scanner = new Scanner(System.in);
    }
    // Getters
    public List<Carta> getMano(){
        return this.mano;
    }
    public int getPuntaje(){
        return this.puntaje;
    }
    public void setPuntaje(int puntaje){
        this.puntaje = puntaje;
    }
    public boolean getDoblo(){
        return this.doblo;
    }
    public void setDoblo(boolean b){
        this.doblo = b;
    }

    public void recibirCarta(Carta carta){
        this.mano.add(carta);
        actualizarPuntaje();
    }

    public void actualizarPuntaje() {
        this.puntaje = 0;
        int cantidadDeAs = 0;
        // Recorro el array de la mano para saber qué cartas salieron
        for (Carta carta : mano){
            this.puntaje += carta.getValorNumerico();
            if (carta.getValor().equals("A")){
                cantidadDeAs++;
            }
        }
        // Si el jugador cuenta con Ases y tiene +21 de puntaje, se le cuenta como 1 en vez de 11.
        while (puntaje > 21 && cantidadDeAs > 0){
            this.puntaje -= 10;
            cantidadDeAs--;
        }
    }

    // Verifico si la mano se pasó de 21.
    public boolean sePaso21(){
        return this.puntaje > 21;
    }

    public boolean tieneAs(){
        for (Carta carta : mano){
            if (carta.getValor().equals("A")){
                return true;
            }
        }
        return false;
    }

    public void nuevaMano(){
        mano.clear();
        puntaje = 0;
    }

    public void doblarMano(Jugador jugador){
        jugador.ajustarSaldo(-jugador.getApuesta());
        jugador.setApuesta(jugador.getApuesta()*2);
        // mostrarMano(jugador);
        // jugador.mostrarSaldo();
        doblo = true;
    }
    public void dividirMano(Jugador jugador){
        // jugador.iniciarMano();
        jugador.agregarMano();
        // Paso lo de la mano uno
        List<Mano> manos = jugador.getManos();
        jugador.getMano2().recibirCarta(jugador.getManoActual().getMano().removeFirst());
        System.out.println("[LOG] Carta 1 eliminada de mano 1.");
        // Le asigno a la mano 2 la carta de la mano 1.
        System.out.println("[LOG] Carta 1 asignada a la mano 2.");
        setPuntaje(jugador.getManoActual().getMano().getFirst().getValorNumerico());
        System.out.println("[LOG] El puntaje a la mano 1 se ha seteado en: " + jugador.getManoActual().getPuntaje());
        // Ajusto la apuesta (Lo mismo para la mano 2)
        // jugador.mostrarManos();
        jugador.setApuestaMano2(jugador.getApuesta());
        jugador.ajustarSaldo(-jugador.getApuestaMano2());
        System.out.printf("%s: tu apuesta para ambas manos son -> Mano 1 (%d) -> Mano 2 (%d).\n", jugador.getNombre(), jugador.getApuesta(), jugador.getApuestaMano2());
    }
    public int seguroBlackjack(Jugador jugador){
        int ingreso;
        System.out.println("El crupier tiene un As de primer carta.");
        System.out.printf("Ingrese '1' para pagar el seguro o '0' para no pagar el seguro ($%d): ", jugador.getApuesta()/2);
        ingreso = scanner.nextInt();
        while (ingreso != 1 && ingreso != 0){
            System.out.println("[!] El numero ingresado no corresponde ni a '1' ni '0'.");
            System.out.println("Ingrese '1' para pagar el seguro o '0' para no pagar el seguro: ");
            ingreso = scanner.nextInt();
        }
        if (ingreso == 1) jugador.setPagoSeguro(true);
        return ingreso;
    }

    public boolean tieneBlackjack(){
        if ((getMano().getFirst().equals("A")) && (getMano().get(1).equals("10") || getMano().get(1).getValor().equals("J") || getMano().get(1).getValor().equals("Q") || getMano().get(1).getValor().equals("K"))){
            return true;
        }else return (getMano().getFirst().getValor().equals("10") || getMano().getFirst().getValor().equals("J") || getMano().getFirst().getValor().equals("Q") || getMano().getFirst().getValor().equals("K")) && getMano().get(1).getValor().equals("A");
    }


}
