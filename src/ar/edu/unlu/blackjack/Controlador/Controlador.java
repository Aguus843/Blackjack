package ar.edu.unlu.blackjack.Controlador;
import ar.edu.unlu.blackjack.Enumerado.Evento;
import ar.edu.unlu.blackjack.Modelo.*;
import ar.edu.unlu.blackjack.Vista.IVista;
import ar.edu.unlu.rmimvc.cliente.IControladorRemoto;
import ar.edu.unlu.rmimvc.observer.IObservableRemoto;

import java.rmi.RemoteException;
import java.util.List;

public class Controlador implements IControladorRemoto {
    private IBlackjackJuego modelo;
    private IVista vista;
    private Jugador jugadorActual;
    private String nickname;

    public Controlador(IVista vista){
        this.vista = vista;
    }

    public void configurarJugadores(String nickname, float saldo) throws RemoteException {
        this.nickname = nickname;
        this.jugadorActual = modelo.configurarJugadores(nickname, saldo);
    }

    public void setModelo(IBlackjackJuego modelo){
        this.modelo = modelo;
    }

    public void cambiarTurnoJugador() throws RemoteException {
        modelo.cambiarTurno();
    }

    public boolean cargarApuestaJugador(String monto){
        try{
            return modelo.realizarApuesta(String.valueOf(monto));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean jugadorActualTieneAs() throws RemoteException {;
        // return modelo.getJugadorActualTurno().getManoActual().tieneAs();
        return modelo.getJugadorPorNickname(nickname).getManoActual().tieneAs();
    }

    public int getPuntajeMano() throws RemoteException {
        // return modelo.getJugadorActualTurno().getManoActual().getPuntaje();
        return modelo.getPuntajeJugador(nickname);
    }

    public boolean getJugadorTieneBlackjack() throws RemoteException {
        return modelo.getJugadorActualTurno().tieneBlackjack();
    }

    public boolean getCrupierTieneAsPrimera() throws RemoteException {
        return modelo.getCrupier().tieneAsPrimera();
    }

    public float getSaldoJugadorActual() throws RemoteException {
        // return modelo.getSaldoJugador(nickname);
        return modelo.getJugadorPorNickname(nickname).getSaldo();
    }

    public float getApuestaJugador() throws RemoteException {
        // return modelo.getApuestaJugador();
        return modelo.getJugadorPorNickname(nickname).getApuesta();
    }

    public float getApuestaJugadorMano2() throws RemoteException {
        // return modelo.getJugadorActualTurno().getApuestaMano2();
        return modelo.getJugadorPorNickname(nickname).getApuestaMano2();
    }

    public void retirarSaldoJugador(float monto) throws RemoteException{
        modelo.getJugadorActualTurno().retirarSaldo(monto);
    }

    public boolean getJugadorPuedeDividir() throws RemoteException {
        return modelo.getJugadorActualTurno().puedeDividir();
    }

    public void recibirCartaJugador() throws RemoteException {
        modelo.repartirCartaAJugador(nickname);
    }

    public void jugadorDobloMano() throws RemoteException {
        modelo.jugadorDoblaApuesta();
    }

    public List<Carta> getManoCrupier() throws RemoteException {
        return modelo.getCrupier().getManoCarta();
    }

    public int getPuntajeCrupier() throws RemoteException {
        return modelo.getCrupier().getPuntaje();
    }

    public int getPuntajeManosIndices(int indice) throws RemoteException {
        return modelo.getJugadorActualTurno().getManos().get(indice).getPuntaje();

    }

    public List<Mano> getManosJugador() throws RemoteException {
        // return modelo.getJugadorActualTurno().getManos(); // saca la mano del cliente de la vista correspondiente
        return modelo.getJugadorPorNickname(nickname).getManos();
    }

    public boolean jugadorDividio() throws RemoteException {
        return jugadorActual.getJugadorDividio();
    }

    public List<Carta> getCartasMano() throws RemoteException {
        // return modelo.getJugadorActualTurno().getManoActual().getMano();
        return modelo.getCartasJugador(nickname);
        // return jugadorActual.getManoActual().getMano();
    }

    public void resetBaraja() throws RemoteException {
        modelo.getMazo().reiniciarBaraja();
    }

    public String getNickname() throws RemoteException {
        return jugadorActual.getNombre();
    }

    public void setJugadorDividio(boolean b) throws RemoteException {
        jugadorActual.setJugadorDividio(b);
    }

    public boolean getJugadorDividio() throws RemoteException {
        return modelo.getJugadorActualTurno().getJugadorDividio();
    }

    public int manoAUsar() throws RemoteException {
        if (!getJugadorDividio()) return 0;
        else if (getJugadorDividio() && (modelo.getJugadorActualTurno().getSePlanto() || modelo.getJugadorActualTurno().getManoActual().sePaso21())) return 1; // si divide y se planta o se pasa, se utiliza la mano 2
        else return 0; // si divide y no se planta se usa la mano 1
    }

    public List<String> getJugadoresConectados() throws RemoteException {
        return modelo.getNombresJugadoresConectados();
    }

    public boolean intentarComenzarPartida() throws RemoteException {
        return modelo.intentarComenzarPartida();
    }

    public int getCantidadJugadoresConectados() throws RemoteException {
        return modelo.getCantidadJugadoresConectados();
    }

    public int getCantidadJugadoresListos() throws RemoteException {
        return modelo.getCantidadJugadoresListos();
    }

    public void votarSi() throws RemoteException{
        modelo.votarNuevaPartida(nickname, true);
    }

    public void votarNo() throws RemoteException{
        modelo.votarNuevaPartida(nickname, false);
    }

    public String getEstadoVotacion() throws RemoteException{
        return modelo.getEstadoVotacion();
    }

    public void pedirCarta() throws RemoteException {
        modelo.pedirCartaJugador();
    }

    public void plantarse() throws RemoteException {
        modelo.jugadorSePlanta();
    }

    public boolean recargarSaldo(float monto) throws RemoteException{
        return modelo.recargarSaldo(nickname, monto);
    }

    public void dividirMano() throws RemoteException {
        modelo.setDividirMano();
    }

    public void solicitarVotacion() throws RemoteException {
        modelo.iniciarVotacionNuevaPartida();
    }

    public Crupier getCrupier() throws RemoteException {
        return modelo.getCrupier();
    }

    public int getManoActualIndex() throws RemoteException{
        return modelo.getManoActualIndex();
    }

    public String getRankingFormateado() throws RemoteException {
        return modelo.obtenerRankingTotal();
    }

    public boolean aceptarSeguro() throws RemoteException {
        return modelo.pagarSeguro(jugadorActual.getNombre());
    }

    public void rechazarSeguro() throws RemoteException {
        modelo.rechazarSeguro(jugadorActual.getNombre());
    }

    // ===========================================================

    /**
     * @param
     * @param <T>
     * @throws RemoteException
     */
    @Override
    public <T extends IObservableRemoto> void setModeloRemoto(T modeloRemoto) throws RemoteException {
        this.modelo = (IBlackjackJuego) modeloRemoto;
    }

    /**
     * @param iObservableRemoto
     * @param o
     * @throws RemoteException
     */
    @Override
    public void actualizar(IObservableRemoto iObservableRemoto, Object o) throws RemoteException {
        if (o instanceof Evento){
            switch((Evento) o){
                case EMPATO_JUGADOR:
                    this.vista.mostrarMensaje("Empataste con el crupier. Se te devolvió el monto apostado.\n");
                    break;
                case JUGADOR_APOSTO:
                    this.vista.mostrarMensaje("Apostaste el monto de " + String.format("%.2f", this.getApuestaJugador()) + "\n");
                    break;
                case SALDO_AGREGADO_EMPATE:
                    this.vista.mostrarMensaje("Se te devolvió el monto apostado! ($" + this.getApuestaJugador() + ")\n");
                    break;
                case OFRECER_SEGURO:
                    this.vista.ofrecerSeguro();
                    break;
                case GANANCIA_SEGURO_PAGADA:
                    vista.mostrarMensaje("[!] El seguro te devolvió la apuesta dado que el crupier tenía BJ!.");
                    vista.mostrarCartasJugador();
                    break;
                case CRUPIER_BLACKJACK_Y_EMPATE:
                    this.vista.mostrarMensaje("Como ambos tuvieron Blackjack, se te concedió el empate! Se te devolvió el monto apostado.\n");
                    break;
                case DEVUELTO_POR_SEGURO:
                    this.vista.mostrarMensaje("Dado que el crupier tuvo BJ y vos también, se te devolvió el monto apostado por el seguro.\n");
                case PERDIO_JUGADOR:
                    this.vista.mostrarMensaje("El jugador " + this.getNickname() + " ha perdido.\n");
                    break;
                case GANADOR_JUGADOR:
                    this.vista.mostrarMensaje("Felicidades " + this.getNickname() + " ganaste!\n");
                    break;
                case PUNTUACION_FINAL_CRUPIER:
                    this.vista.mostrarMensaje("El puntaje final del crupier es: " + this.getPuntajeCrupier() + "\n");
                    break;
                case ADJUDICAR_GANANCIA:
                    this.vista.mostrarMensaje("Felicitaciones! Ganaste la apuesta --> ($" + this.getApuestaJugador()*2 + ").\n");
                    break;
                case ADJUDICAR_GANANCIA_BJ:
                    this.vista.mostrarMensaje("Felicitaciones! Ganaste la apuesta con un BJ --> ($" + this.getApuestaJugador()*2.5 + ").\n");
                    break;
                case APUESTA_AMBAS_MANOS:
                    this.vista.mostrarMensaje(this.nickname + ": tu apuesta para ambas manos son -> Mano 1 (" + this.getApuestaJugador() + ") -> Mano 2 (" + this.getApuestaJugadorMano2() + ").\n");
                    break;
                case CRUPIER_SE_PASO:
                    this.vista.mostrarMensaje("El crupier se pasó de los 21.\n");
                    break;
                case CRUPIER_SE_PLANTA:
                    this.vista.mostrarMensaje("El crupier se planta en " + this.getPuntajeCrupier() + ".\n");
                    break;
                case CRUPIER_PIDE_CARTA:
                    this.vista.mostrarMensaje("\nEl crupier está obteniendo una carta...\n");
                    break;
                case MOSTRAR_MANO_CRUPIER:
                    this.vista.mostrarPuntuacionParcialCrupier();
                    break;
                case MOSTRAR_MANO_JUGADOR:
                    this.vista.mostrarManoJugador();
                    break;
                case CICLO_PARTIDA:
                    this.vista.cicloPartida();
                    break;
                case CAMBIAR_A_MANO2:
                    if (modelo.getJugadorActualTurno() != null && modelo.getJugadorActualTurno().getNombre().equals(nickname)){
                        this.vista.cambiarAMano2();
                    }
                    break;
                case NOTIFICAR_TURNO_JUGADOR:
                    if(modelo.getJugadorActualTurno() != null && modelo.getJugadorActualTurno().getNombre().equals(nickname)){
                        this.vista.notificarTurnoJugador();
                    }
                    else{
                        if (!jugadorActual.getSePlanto() && !jugadorActual.getManoActual().sePaso21()) this.vista.mostrarMensaje("[!] Esperando turno de " + modelo.getJugadorActualTurno().getNombre() + "...\n");
                    }
                    break;
                case NOTIFICAR_TURNO_APUESTA:
                    if (modelo.getJugadorActualTurno() != null && modelo.getJugadorActualTurno().getNombre().equals(nickname)){
                        vista.notificarTurnoApuesta();
                    }
                    else vista.mostrarMensaje("Esperando tu turno para apostar...\n");
                    break;
                case JUGADOR_CONECTADO:
                    this.vista.mostrarMensaje("[!] Un jugador se ha conectado.\n");
                    break;
                case JUGADOR_MARCO_LISTO:
                    int listos = modelo.getCantidadJugadoresListos();
                    int total = modelo.getCantidadJugadoresConectados();
                    this.vista.mostrarMensaje("[!] Jugador listo (" + listos + "/" + total + ")\n");
                    break;
                case ACTUALIZAR_SALA_ESPERA:
                    List<String> jugadores = modelo.getNombresJugadoresConectados();
                    this.vista.mostrarSalaEspera(jugadores, modelo.getCantidadJugadoresConectados());
                    break;
                case TODOS_JUGADORES_LISTOS:
                    this.vista.comenzarPartida();
                    break;
                case CARTA_REPARTIDA_JUGADOR:
                    if (modelo.getJugadorActualTurno().getNombre().equals(jugadorActual.getNombre())) vista.mostrarCartasJugador();
                    break;
                case JUGADOR_SE_PASO:
                    if (jugadorActual.getNombre().equals(nickname)) this.vista.jugadorSePaso();
                    break;
                case JUGADOR_SE_PLANTO:
                    if (!modelo.getJugadorActualTurno().getNombre().equals(jugadorActual.getNombre())) vista.mostrarMensaje(modelo.getJugadorActualTurno().getNombre() + " se planto!\n");
                    break;
                case JUGADOR_LLEGO_21:
                    if (modelo.getJugadorActualTurno().getNombre().equals(jugadorActual.getNombre())) this.vista.jugadorLlegoA21();
                    break;
                case MOSTRAR_MANOS_DIVIDIDAS:
                    if (modelo.getJugadorActualTurno() != null && modelo.getJugadorActualTurno().getNombre().equals(nickname)){
                        this.vista.mostrarManosDivididasJugadorVista();
                    }
                    break;
                case INICIAR_VOTACION_NUEVA_PARTIDA:
                    this.vista.mostrarVotacion();
                    break;
                case ACTUALIZAR_VOTOS:
                    this.vista.actualizarEstadoVotacion();
                    break;
                case VOTACION_COMPLETADA:
                    this.vista.mostrarMensaje("\n==== VOTACION COMPLETADA ====\n");
                    break;
                case NUEVA_PARTIDA_INICIADA:
                    this.vista.mostrarMensaje("\n==== NUEVA PARTIDA INICIADA ====\n");
                    this.vista.comenzarPartida();
                    break;
                case JUEGO_TERMINADO_TODOS_SALIERON:
                    vista.mostrarMensaje("\n===== JUEGO TERMINADO =====\n");
                    vista.mostrarMensaje("Todos los jugadores salieron.\n");
                    vista.mostrarMensaje("Gracias por jugar!\n");
                    break;
                case PARTIDA_FINALIZADA:
                    vista.mostrarResultados();
                    break;
                default:
                    break;
            }
        }
    }
}
