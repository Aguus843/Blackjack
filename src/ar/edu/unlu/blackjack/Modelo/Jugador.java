package ar.edu.unlu.blackjack.Modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Jugador implements Serializable {
    private String nombre;
    private List<Mano> manos;
    private Saldo saldo;
    private float apuesta;
    private float apuestaMano2;
    private boolean pagoSeguro;
    private boolean sePlanto;
    private boolean jugadorDividio;

    private boolean activo = true;  // Si está participando en la partida actual
    private Boolean votoNuevaPartida = null;  // null = no votó, true = si, false = no

    public Jugador(String nombre, float saldoInicial) {
        this.nombre = nombre;
        this.manos = new ArrayList<>();
        this.saldo = new Saldo(saldoInicial);
        this.apuesta = 0;
        this.apuestaMano2 = 0;
        this.pagoSeguro = false;
        this.jugadorDividio = false;
        manos.add(new Mano());
    }

    public boolean getActivo() {
        return this.activo;
    }
    public void setActivo(boolean activo){
        this.activo = activo;
    }
    public Boolean getVotoNuevaPartida(){
        return this.votoNuevaPartida;
    }
    public void setVotoNuevaPartida(Boolean voto) {
        this.votoNuevaPartida = voto;
    }

    public void resetearParaNuevaPartida() {
        this.votoNuevaPartida = null;
        this.sePlanto = false;
        this.pagoSeguro = false;
        this.apuesta = 0;
        this.apuestaMano2 = 0;
        this.jugadorDividio = false; // reseteo flag de que el jugador dividio manos

        // reseteo la/s mano/s
        manos.clear();
        manos.add(new Mano());

    }

    public boolean getSePlanto(){
        return this.sePlanto;
    }
    public void setSePlanto(boolean sePlanto){
        this.sePlanto = sePlanto;
    }
    public void setJugadorDividio(boolean jugadorDividio){
        this.jugadorDividio = jugadorDividio;
    }
    public boolean getJugadorDividio(){
        return this.jugadorDividio;
    }
    public float getSaldo(){
        return saldo.getSaldo();
    }
    public void ajustarSaldo(float monto){
        if (monto > 0){
            saldo.agregarSaldo(monto);
            // System.out.println("Saldo agregado: " + getSaldo()+monto);
        }else {
            if (!saldo.retirarSaldo(-monto)) {
                // System.out.println("Saldo insuficiente para apostar: " + monto);
            }
        }
    }
    public void retirarSaldo(float monto){
        if (monto > 0) saldo.retirarSaldo(monto);
        else if (saldo.retirarSaldo(-monto)) return;
    }

    // Metodo que reparte a UNA mano.
    public void repartirCartaAMano(int indexMano, Carta carta){
        List<Mano> manos = getManos();
        while (manos.size() <= indexMano){
            manos.add(new Mano());
        }

        if (carta != null) manos.get(indexMano).recibirCarta(carta);
    }

    public String getNombre() {
        return this.nombre;
    }
    public float getApuesta(){
        return this.apuesta;
    }
    public float getApuestaMano2(){
        return this.apuestaMano2;
    }
    public void setApuestaMano2(float monto){
        this.apuestaMano2 = monto;
    }
    public void setApuesta(float monto){
        this.apuesta = monto;
    }
    public boolean getPagoSeguro(){
        return this.pagoSeguro;
    }
    public void agregarMano(){
        manos.add(new Mano());
    }
    public void eliminarMano(){
        manos.remove(1);
    }
    // Getter de mano actual (Cuando NO hay division)
    public Mano getManoActual() {
        return manos.getFirst();
    }
    public Mano getMano2(){
        return manos.get(1);
    }
    // Getter de Manos
    public List<Mano> getManos(){
        return manos;
    }
    public boolean multiplesManos(){
        return manos.size() > 1;
    }
    public boolean puedeDividir(){
        Mano mano = getManoActual();
        if (mano != null){
            return mano.getMano().getFirst().getValor().equals(mano.getMano().get(1).getValor());
        }
        return false;
    }
    public void setPagoSeguro(boolean b) {
        this.pagoSeguro = b;
    }
    public boolean tieneBlackjack(){
        List<Mano> manos = getManos();
        for (Mano mano : manos) {
            String primeraCarta = mano.getMano().getFirst().getValor();
            String segundaCarta = mano.getMano().get(1).getValor();

            if (primeraCarta.equals("A") && (segundaCarta.equals("10") || segundaCarta.equals("J") || segundaCarta.equals("Q") || segundaCarta.equals("K"))) {
                return true;
            } else if ((primeraCarta.equals("10") || primeraCarta.equals("J") || primeraCarta.equals("Q") || primeraCarta.equals("K")) && segundaCarta.equals("A")) {
                return true;
            }
        }
        return false;
    }
}
