package ar.edu.unlu.blackjack.Controlador;
import ar.edu.unlu.blackjack.Modelo.*;
import ar.edu.unlu.blackjack.Vista.IVista;
import ar.edu.unlu.rmimvc.cliente.IControladorRemoto;
import ar.edu.unlu.rmimvc.observer.IObservableRemoto;

import java.rmi.RemoteException;
import java.util.List;

public class Controlador implements IControladorRemoto {
    private BlackjackJuego modelo;
    private IVista vista;

    public Controlador(IVista vista){
        this.vista = vista;
    }

    public void configurarJugadores(String nickname, float saldo){
        modelo.configurarJugadores(nickname, saldo);
    }

    public void setModelo(BlackjackJuego modelo){
        this.modelo = modelo;
    }

    public void crupierPideCarta(){
        modelo.getCrupier().pedirCarta(modelo.getMazo().repartirCarta());
    }

    public void repartirCartasIniciales(Jugador jugadorActual){
        modelo.repartirCartasIniciales(jugadorActual);
    }

    public String crupierMuestraPrimerCarta(){
        return modelo.getCrupier().mostrarPrimeraCarta();
    }

    public Jugador obtenerJugadorActual(){
        return modelo.getJugadorActualTurno();
    }

    public String cartasRestantes(){
        return String.valueOf(modelo.getMazo().cartasRestantes());
    }

    public void evaluandoGanadores(){
        try{
            modelo.evaluarGanadores();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void cambiarTurnoJugador(){
        modelo.cambiarTurno();
    }

    public int getIndiceJugadorActual(){
        return modelo.getIndice();
    }

    public int getCantidadJugadoresTotal(){
        return modelo.getCantidadJugadores();
    }

    public boolean cargarApuestaJugador(String monto){
        try{
            return modelo.realizarApuesta(monto);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean crupierTieneCarta(){
        return modelo.getCrupier().tieneCarta();
    }

    public Mano obtenerManoJugador(){
        return modelo.getManoJugador();
    }

    public String getNombreJugador(){
        return modelo.getJugadorActualTurno().getNombre();
    }

    public boolean jugadorActualTieneAs(){;
        return modelo.getJugadorActualTurno().getManoActual().tieneAs();
    }

    public int getPuntajeMano(){
        return modelo.getJugadorActualTurno().getManoActual().getPuntaje();
    }

    public boolean getJugadorTieneBlackjack(){
        return modelo.getJugadorActualTurno().tieneBlackjack();
    }

    public boolean getCrupierTieneAsPrimera(){
        return modelo.getCrupier().tieneAsPrimera();
    }

    public float getSaldoJugadorActual(){
        return modelo.getJugadorActualTurno().getSaldo();
    }

    public float getApuestaJugador(){
        return modelo.getApuestaJugador();
    }

    public float getApuestaJugadorMano2(){
        return modelo.getJugadorActualTurno().getApuestaMano2();
    }

    public void ajustarSaldoJugador(float monto){
        modelo.getJugadorActualTurno().ajustarSaldo(monto);
    }

    public boolean getJugadorPuedeDividir(){
        return modelo.getJugadorActualTurno().puedeDividir();
    }

    public void recibirCartaJugador(){
        modelo.getManoJugador().recibirCarta(modelo.getMazo().repartirCarta());
    }

    public boolean compararDosCartasIguales(){
        return modelo.getManoJugador().getMano().getFirst().getValor().equals(modelo.getManoJugador().getMano().get(1).getValor());
    }

    public void dividirManoJugador(){
        try{
            modelo.setDividirMano();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void setRepartirCartaAMano(int index){
        modelo.getJugadorActualTurno().repartirCartaAMano(index, modelo.getMazo().repartirCarta());
    }

    public int getCantidadCartasManoIndice(int index){
        return modelo.getJugadorActualTurno().getManos().get(index).getMano().size();
    }

    public void setIndiceJugador(int index){
        modelo.setIndiceJugador(index);
    }

    public void setCantidadJugadoresTotales(int cantidad){
        modelo.setCantidadJugadores(cantidad);
    }

    public void jugadorDobloMano(){
        modelo.getJugadorActualTurno().getManoActual().doblarMano(modelo.getJugadorActualTurno());
    }

    public Carta getCartasManoIndice(int indexMano, int indexCarta){
        return modelo.getJugadorActualTurno().getManos().get(indexMano).getMano().get(indexCarta);
    }

    public boolean getJugadorTieneAsManoIndice(int index){
        return modelo.getJugadorActualTurno().getManos().get(index).tieneAs();
    }

    public boolean getSePaso21ManoPrincipal(){
        return modelo.getJugadorActualTurno().getManoActual().sePaso21();
    }

    public boolean getSePaso21Index(int indice){
        return modelo.getJugadorActualTurno().getManos().get(indice).sePaso21();
    }

    public List<Carta> getManoCrupier(){
        return modelo.getCrupier().getManoCarta();
    }

    public int getPuntajeCrupier(){
        return modelo.getCrupier().getPuntaje();
    }

    public boolean crupierDebePedirCarta(){
        return modelo.getCrupier().debePedirCarta();
    }

    public boolean crupierSePaso21(){
        return modelo.crupierSePaso21();
    }

    public int getPuntajeManosIndices(int indice){
        return modelo.getJugadorActualTurno().getManos().get(indice).getPuntaje();

    }

    public List<Mano> getManosJugador(){
        return modelo.getManosJugador();
    }

    public List<Carta> getCartasMano(){
        return modelo.getJugadorActualTurno().getManoActual().getMano();
    }

    public void setPagoSeguroJugador(boolean b){
        modelo.getJugadorActualTurno().setPagoSeguro(b);
    }

    public float getMontoApostado(){
        return modelo.getJugadorActualTurno().getMontoDeApuesta();
    }

    public void setMontoApostado(float monto){
        modelo.getJugadorActualTurno().setMonto(monto);
    }

    public void setJugadorPidioCarta(boolean b){
        modelo.getJugadorActualTurno().setJugadorPidioCarta(b);
    }

    public boolean getJugadorPidioCarta(){
        return modelo.getJugadorActualTurno().getJugadorPidioCarta();
    }

    public void clearJugadores(){
        modelo.getJugadores().clear();
    }

    public void clearManoCrupier(){
        modelo.getCrupier().clearMano();
    }

    public void resetBaraja(){
        modelo.getMazo().reiniciarBaraja();
    }

    public void setNickname(String nickname){
        modelo.setNickname(nickname);
    }

    public void setSaldo(float saldo){
        modelo.setSaldo(saldo);
    }

    public String getNickname(){
        return modelo.getNickname();
    }

    public float getSaldo(){
        return modelo.getSaldo();
    }

    public boolean getJugadorSePlanto(){
        return modelo.getJugadorActualTurno().getSePlanto();
    }

    public void setJugadorSePlanto(boolean b){
        modelo.getJugadorActualTurno().setSePlanto(b);
    }

    public boolean getTieneBlackjackPorIndiceMano(int indice){
        return modelo.getManosJugador().get(indice).tieneBlackjack();
    }

    public void setJugadorDividio(boolean b){
        modelo.getJugadorActualTurno().setJugadorDividio(b);
    }

    public boolean getJugadorDividio(){
        return modelo.getJugadorActualTurno().getJugadorDividio();
    }

    public int manoAUsar(){
        if (!getJugadorDividio()) return 0;
        else if (getJugadorDividio() && (getJugadorSePlanto() || getSePaso21ManoPrincipal())) return 1; // si divide y se planta o se pasa, se utiliza la mano 2
        else return 0; // si divide y no se planta se usa la mano 1
    }

    public boolean getJugadorDoblo(){
        return modelo.getJugadorActualTurno().getDoblo();
    }

    public void checkJugadorPagaSeguro(){
        try {
            modelo.checkSiPuedePagarSeguroBlackjack();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
    public void turnoCrupier() throws RemoteException{
        try{
            modelo.turnoCrupier();
        }catch (RemoteException e){
            throw new RuntimeException(e);
        }
    }
    public void mostrarManoJugador() throws RemoteException{
        modelo.mostrarManoJugador();
    }
    public void mostrarManoCrupier() throws RemoteException{
        modelo.mostrarManoCrupier();
    }

    /**
     * @param t
     * @param <T>
     * @throws RemoteException
     */
    @Override
    public <T extends IObservableRemoto> void setModeloRemoto(T t) throws RemoteException {

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
                case ESPACIADOR_EN_CHAT:
                    this.vista.mostrarMensaje("========================================================");
                    break;
                case MANO:
                    this.vista.mostrarCartasJugador();
                    break;
                case BLACKJACK:
                    this.vista.mostrarMensaje("Felicitaciones, obtuviste un BJ!");
                    break;
                case CRUPIER_BLACKJACK:
                    this.vista.mostrarMensaje("El crupier obtuvo BJ!");
                    break;
                case SALDO_RESTADO:
                    // this.vista.mostrarJugadorSaldoRestado();
                    this.vista.mostrarMensaje("Se le restó " + (-this.getMontoApostado()) + " del saldo disponible.");
                    break;
                case EMPATO_JUGADOR:
                    this.vista.mostrarMensaje("Empataste con el crupier. Se te devolvió el monto apostado.");
                    break;
                case JUGADOR_APOSTO:
                    this.vista.mostrarMensaje("Apostaste el monto de " + this.getApuestaJugador());
                    break;
                case SALDO_AGREGADO:
                    this.vista.mostrarMensaje("Se te agregó saldo a tu cuenta! " + this.getApuestaJugador()*2);
                    break;
                case SALDO_AGREGADO_EMPATE:
                    this.vista.mostrarMensaje("Se te devolvió el monto apostado! (" + this.getApuestaJugador() + ")");
                    break;
                case JUGADOR_PAGO_SEGURO:
                    this.vista.mostrarMensaje("Pagaste el seguro de Blackjack! (" + this.getApuestaJugador()/2 + ")");
                    break;
                case CRUPIER_BLACKJACK_Y_EMPATE:
                    this.vista.mostrarMensaje("Como ambos tuvieron Blackjack, se te concedió el empate! Se te devolvió el monto apostado.");
                    break;
                case DEVUELTO_POR_SEGURO:
                    this.vista.mostrarMensaje("Dado que el crupier tuvo BJ y vos también, se te devolvió el monto apostado por el seguro.");
                case PERDIO_JUGADOR:
                    this.vista.mostrarMensaje("El jugador " + this.getNombreJugador() + " ha perdido.");
                    break;
                case GANADOR_JUGADOR:
                    this.vista.mostrarMensaje("Felicidades " + getNombreJugador() + " ganaste!");
                    break;
                case MANO_FINALIZADA:
                    this.vista.mostrarMensaje("Mano de finalizada");
                    break;
                case PUNTUACION_PARCIAL_JUGADOR:
                    this.vista.mostrarPuntuacionParcial();
                    break;
                case PUNTUACION_FINAL_JUGADOR:
                    this.vista.mostrarMensaje("El puntaje final de " + this.getNombreJugador() + " es: " + getPuntajeMano());
                    break;
                case PUNTUACION_FINAL_CRUPIER:
                    this.vista.mostrarMensaje("El puntaje final del crupier es: " + this.getPuntajeCrupier());
                    break;
                case ADJUDICAR_GANANCIA:
                    this.vista.mostrarMensaje("Felicitaciones! Ganaste la apuesta --> ($" + this.getApuestaJugador()*2 + ").");
                    break;
                case ADJUDICAR_GANANCIA_BJ:
                    this.vista.mostrarMensaje("Felicitaciones! Ganaste la apuesta con un BJ --> ($" + this.getApuestaJugador()*2.5 + ").");
                case APUESTA_AMBAS_MANOS:
                    this.vista.mostrarMensaje(this.getNombreJugador() + ": tu apuesta para ambas manos son -> Mano 1 (" + this.getApuestaJugador() + ") -> Mano 2 (" + this.getApuestaJugadorMano2() + ").");
                    break;
                case PUNTUACION_MANO1:
                    this.vista.mostrarMensaje("========================= MANO 1 =========================\n");
                    this.vista.mostrarMensaje("Puntuación final de la mano 1 de " + this.getNombreJugador() + " es --> " + this.getPuntajeManosIndices(0));
                    break;
                case PUNTUACION_MANO2:
                    this.vista.mostrarMensaje("\n========================= MANO 2 =========================\n");
                    this.vista.mostrarMensaje("Puntuación final de la mano 2 de " + this.getNombreJugador() + " es --> " + this.getPuntajeManosIndices(1));
                    break;
                case CRUPIER_TIENE_AS:
                    this.vista.mostrarMensaje("El crupier tiene As de primera. Ingrese '1' para pagar el seguro o '0' para no hacerlo. ");
                    break;
                case NO_PUEDE_PAGAR_SEGURO:
                    this.vista.mostrarMensaje("No puede pagar el seguro dado que no tiene saldo suficiente.");
                    break;
                case CRUPIER_SE_PASO:
                    this.vista.mostrarMensaje("El crupier se pasó de los 21.");
                    break;
                case CRUPIER_SE_PLANTA:
                    this.vista.mostrarMensaje("El crupier se planta en " + this.getPuntajeCrupier() + ".");
                    break;
                case CRUPIER_PIDE_CARTA:
                    this.vista.mostrarMensaje("\nEl crupier está obteniendo una carta...");
                    break;
                case MOSTRAR_MANO_CRUPIER:
                    this.vista.mostrarPuntuacionParcialCrupier();
                    break;
                default:
                    break;
            }
        }
    }
}
