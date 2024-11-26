package ar.edu.unlu.blackjack.Modelo;

import ar.edu.unlu.rmimvc.observer.ObservableRemoto;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class BlackjackJuego extends ObservableRemoto implements IBlackjackJuego {
    private final Mazo mazo;
    private final List<Jugador> jugadores;
    private final Crupier crupier;
    private int cantidadJugadores;
    private int indiceJugador;
    private String nickname;
    private float saldo;

    public BlackjackJuego() {
        mazo = new Mazo();
        crupier = new Crupier();
        jugadores = new ArrayList<>();
        this.indiceJugador = 0;
    }
    public Crupier getCrupier(){
        return crupier;
    }
    public Mazo getMazo(){
        return mazo;
    }
    public List<Jugador> getJugadores(){
        return jugadores;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public void setSaldo(float saldo) {
        this.saldo = saldo;
    }
    public String getNickname() {
        return nickname;
    }
    public float getSaldo() {
        return saldo;
    }
    public Jugador getJugadorActualTurno(){
        return jugadores.get(indiceJugador);
    }
    public void cambiarTurno(){
        if (indiceJugador == jugadores.size()) this.indiceJugador = 0;
        else this.indiceJugador++;
    }
    public void setIndiceJugador(int indice){
        this.indiceJugador = indice;
    }

    @Override
    public Mano getManoJugador(){
        return jugadores.get(indiceJugador).getManoActual();
    }

    @Override
    public void checkSiPuedePagarSeguroBlackjack() throws RemoteException {
        if (getJugadorActualTurno().getSaldo() >= getApuestaJugador()/2) notificarObservadores(Evento.CRUPIER_TIENE_AS);
        else notificarObservadores(Evento.NO_PUEDE_PAGAR_SEGURO);
    }
    @Override
    public float getApuestaJugador(){
        return jugadores.get(indiceJugador).getApuesta();
    }
    @Override
    public int getIndice(){
        return this.indiceJugador;
    }
    @Override
    public int getCantidadJugadores(){
        return this.cantidadJugadores;
    }
    @Override
    public void setCantidadJugadores(int cantidad){
        this.cantidadJugadores = cantidad;
    }
    @Override
    public void setDividirMano() throws RemoteException {
        getJugadorActualTurno().getManoActual().dividirMano(getJugadorActualTurno());
        notificarObservadores(Evento.APUESTA_AMBAS_MANOS);
    }
    @Override
    public boolean crupierSePaso21(){
        return crupier.getPuntaje() > 21;
    }
    public List<Mano> getManosJugador(){
        return jugadores.get(indiceJugador).getManos();
    }
    @Override
    public void setAjustarSaldo(Jugador jugador, float monto) throws RemoteException {
        jugador.ajustarSaldo(monto);
        jugador.setMonto(monto);
        if (monto > 0){
            notificarObservadores(Evento.SALDO_AGREGADO);
        }else notificarObservadores(Evento.SALDO_RESTADO);
    }
    @Override
    public void setApuesta(Jugador jugador, float monto) throws RemoteException {
        jugador.setApuesta(monto);
        notificarObservadores(Evento.JUGADOR_APOSTO);
    }

    public boolean getPagoSeguroJugador(){
        return getJugadorActualTurno().getPagoSeguro();
    }

    // ----------- METODOS --------------

    @Override
    public boolean realizarApuesta(String monto) throws RemoteException{
        if (this.indiceJugador == jugadores.size()) return true;
        if (Float.parseFloat(monto) > getJugadorActualTurno().getSaldo() || Float.parseFloat(monto) <= 1){
            return false;
        }
        setAjustarSaldo(getJugadorActualTurno(), -Float.parseFloat(monto));
        setApuesta(getJugadorActualTurno(), Float.parseFloat(monto));
        getJugadorActualTurno().iniciarMano();
        return true;
    }

    @Override
    public void configurarJugadores(String nickname, float saldo){
        jugadores.add(new Jugador(nickname, saldo));
    }

    @Override
    public void repartirCartasIniciales(Jugador jugador){
        for (int i = 0; i < 2; i++){
            for (int j = 0; j < jugador.getManos().size(); j++){
                jugador.repartirCartaAMano(j, mazo.repartirCarta());
            }
        }
    }
    @Override
    public void evaluarGanadores() throws RemoteException {
        if (crupier.tieneBlackjack()) evaluarGanadoresBlackjack();
        else evaluarGanadoresNOBlackjack();
    }

    @Override
    public void evaluarGanadoresBlackjack() throws RemoteException {
        notificarObservadores(Evento.CRUPIER_BLACKJACK);
        setIndiceJugador(0);
        while (getIndice() != getCantidadJugadores()){
            if (crupier.multiplesManos()){
                // mano 1
                notificarObservadores(Evento.PUNTUACION_MANO1);
                if (getManosJugador().get(0).tieneBlackjack()){
                    notificarObservadores(Evento.CRUPIER_BLACKJACK_Y_EMPATE);
                    devolverApuesta(getJugadorActualTurno(), getJugadorActualTurno().getApuesta());
                }else if (getPagoSeguroJugador()){
                    notificarObservadores(Evento.DEVUELTO_POR_SEGURO);
                    devolverApuesta(getJugadorActualTurno(), getJugadorActualTurno().getApuesta());
                }else{
                    notificarObservadores(Evento.PERDIO_JUGADOR);
                }
                // mano 2
                notificarObservadores(Evento.PUNTUACION_MANO2);
                if (getManosJugador().get(1).tieneBlackjack()){
                    notificarObservadores(Evento.CRUPIER_BLACKJACK_Y_EMPATE);
                    devolverApuesta(getJugadorActualTurno(), getJugadorActualTurno().getApuestaMano2());
                }else{
                    notificarObservadores(Evento.PERDIO_JUGADOR);
                }
            }else{
                if (getJugadorActualTurno().tieneBlackjack()){
                    notificarObservadores(Evento.CRUPIER_BLACKJACK_Y_EMPATE);
                    devolverApuesta(getJugadorActualTurno(), getJugadorActualTurno().getApuesta());
                }else if (getPagoSeguroJugador()){
                    notificarObservadores(Evento.DEVUELTO_POR_SEGURO);
                    devolverApuesta(getJugadorActualTurno(), getJugadorActualTurno().getApuesta());
                }else{
                    notificarObservadores(Evento.PERDIO_JUGADOR);
                }
            }
            cambiarTurno();
        }
    }

    @Override
    public void evaluarGanadoresNOBlackjack() throws RemoteException {
        int puntajeCrupier = crupier.getPuntaje();
        setIndiceJugador(0);
        while (getIndice() != getCantidadJugadores()){
            if (getJugadorActualTurno().multiplesManos()){
                // Logica para jugador con 2 manos
                // mano 1
                notificarObservadores(Evento.PUNTUACION_MANO1);
                if (getJugadorActualTurno().getManos().get(0).sePaso21()){
                    notificarObservadores(Evento.PERDIO_JUGADOR);
                }else if (crupierSePaso21()){
                    notificarObservadores(Evento.GANADOR_JUGADOR);
                    adjudicarGanancia(getJugadorActualTurno(), getJugadorActualTurno().getApuesta());
                }else if (getJugadorActualTurno().getManoActual().getPuntaje() > puntajeCrupier){
                    // check mano 1
                    notificarObservadores(Evento.GANADOR_JUGADOR);
                    adjudicarGanancia(getJugadorActualTurno(), getJugadorActualTurno().getApuesta());
                }else if (getJugadorActualTurno().getManoActual().getPuntaje() < puntajeCrupier){
                    // check mano 1
                    notificarObservadores(Evento.PERDIO_JUGADOR);
                }else{
                    notificarObservadores(Evento.EMPATO_JUGADOR);
                    devolverApuesta(getJugadorActualTurno(), getJugadorActualTurno().getApuesta());
                }
                // mano 2
                notificarObservadores(Evento.PUNTUACION_MANO2);
                if (getJugadorActualTurno().getManos().get(1).sePaso21()){
                    notificarObservadores(Evento.PERDIO_JUGADOR);
                }else if (crupierSePaso21()){
                    notificarObservadores(Evento.GANADOR_JUGADOR);
                    adjudicarGanancia(getJugadorActualTurno(), getJugadorActualTurno().getApuesta());
                }else if (getJugadorActualTurno().getMano2().getPuntaje() > puntajeCrupier){
                    notificarObservadores(Evento.GANADOR_JUGADOR);
                    adjudicarGanancia(getJugadorActualTurno(), getJugadorActualTurno().getApuesta());
                }else if (getJugadorActualTurno().getMano2().getPuntaje() < puntajeCrupier){
                    notificarObservadores(Evento.PERDIO_JUGADOR);
                }else{
                    notificarObservadores(Evento.EMPATO_JUGADOR);
                    devolverApuesta(getJugadorActualTurno(), getJugadorActualTurno().getApuesta());
                }

                cambiarTurno();
            }else{
                notificarObservadores(Evento.ESPACIADOR_EN_CHAT);
                int puntajeMano1 = getJugadorActualTurno().getManoActual().getPuntaje();
                notificarObservadores(Evento.PUNTUACION_FINAL_JUGADOR);
                if (getManoJugador().sePaso21()){
                    notificarObservadores(Evento.PERDIO_JUGADOR);
                }else if (crupier.getPuntaje() > 21){
                    notificarObservadores(Evento.GANADOR_JUGADOR);
                    adjudicarGanancia(getJugadorActualTurno(), getApuestaJugador());
                }else if (puntajeMano1 > puntajeCrupier){
                    notificarObservadores(Evento.GANADOR_JUGADOR);
                    adjudicarGanancia(getJugadorActualTurno(), getApuestaJugador());
                }else if (puntajeMano1 < puntajeCrupier){
                    notificarObservadores(Evento.PERDIO_JUGADOR);
                }else{
                    notificarObservadores(Evento.EMPATO_JUGADOR);
                    devolverApuesta(getJugadorActualTurno(), getApuestaJugador());
                }
                cambiarTurno();
            }
        }
        notificarObservadores(Evento.PUNTUACION_FINAL_CRUPIER);
    }

    @Override
    public void devolverApuesta(Jugador jugador, float apuesta) throws RemoteException {
        jugador.ajustarSaldo(apuesta);
        if (crupier.tieneBlackjack() && jugador.getPagoSeguro()){
            notificarObservadores(Evento.DEVUELTO_POR_SEGURO);
        }
        notificarObservadores(Evento.SALDO_AGREGADO_EMPATE);
    }

    @Override
    public void adjudicarGanancia(Jugador jugador, float apuesta) throws RemoteException {
        // System.out.printf("%s: felicitaciones! Ganaste la apuesta.\n", jugador.getNombre());
        if (jugador.tieneBlackjack()){
            notificarObservadores(Evento.ADJUDICAR_GANANCIA_BJ);
            jugador.ajustarSaldo((float)2.5*apuesta);
        }else{
            notificarObservadores(Evento.ADJUDICAR_GANANCIA);
            jugador.ajustarSaldo(2*apuesta);
        }
    }
    /**
     * Metodo para controlar al Crupier una vez se finaliza la opcion de pedir/doblar/dividir/plantarse
     */
    @Override
    public void turnoCrupier() throws RemoteException{
        boolean imprimioCartas = false;
        while (crupier.debePedirCarta()){
            notificarObservadores(Evento.CRUPIER_PIDE_CARTA);
            crupier.pedirCarta(mazo.repartirCarta());
            notificarObservadores(Evento.MOSTRAR_MANO_CRUPIER);
            imprimioCartas = true;
        }
        if (!imprimioCartas) notificarObservadores(Evento.MOSTRAR_MANO_CRUPIER);

        // verifico si se paso de 21
        if (crupier.getPuntajeCrupier() > 21) notificarObservadores(Evento.CRUPIER_SE_PASO);
        else notificarObservadores(Evento.CRUPIER_SE_PLANTA);
    }

    public void mostrarManoJugador() throws RemoteException {
        notificarObservadores(Evento.ESPACIADOR_EN_CHAT);
        notificarObservadores(Evento.MANO);
        notificarObservadores(Evento.PUNTUACION_PARCIAL_JUGADOR);
        notificarObservadores(Evento.ESPACIADOR_EN_CHAT);
    }

    public void mostrarManoCrupier() throws RemoteException {
        notificarObservadores(Evento.ESPACIADOR_EN_CHAT);
        notificarObservadores(Evento.MOSTRAR_MANO_CRUPIER);
        notificarObservadores(Evento.PUNTUACION_FINAL_CRUPIER);
        notificarObservadores(Evento.ESPACIADOR_EN_CHAT);
    }
}
