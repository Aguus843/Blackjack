package ar.edu.unlu.blackjack.Modelo;

import ar.edu.unlu.blackjack.Enumerado.Evento;
import ar.edu.unlu.rmimvc.observer.ObservableRemoto;

import java.rmi.RemoteException;
import java.util.*;

public class BlackjackJuego extends ObservableRemoto implements IBlackjackJuego {
    private Mazo mazo;
    private final List<Jugador> jugadores;
    private final Crupier crupier;
    private int indiceJugador;
    private int manoActualIndex;

    private boolean juegoIniciado;
    private Map<String, Boolean> jugadoresListos;
    private boolean salaAbierta;
    private boolean faseApuestas;

    // sistema de votacion para seguir la partida
    private Map<String, Boolean> votosNuevaPartida = new HashMap<>();

    private final AdministradorJugadorMasGanador administradorRanking;

    public BlackjackJuego() {
        mazo = new Mazo();
        crupier = new Crupier();
        jugadores = new ArrayList<>();
        this.indiceJugador = 0;

        this.juegoIniciado = false;
        this.salaAbierta = true;
        this.jugadoresListos = new HashMap<>();
        this.faseApuestas = true;
        this.manoActualIndex = 0;
        this.administradorRanking = new AdministradorJugadorMasGanador();
    }
    @Override
    public Crupier getCrupier(){
        return crupier;
    }
    @Override
    public Mazo getMazo(){
        return mazo;
    }
    @Override
    public Jugador getJugadorActualTurno(){
        return jugadores.get(indiceJugador);
    }

    @Override
    public int getManoActualIndex() throws RemoteException{
        return this.manoActualIndex;
    }

    @Override
    public void cambiarTurno() throws RemoteException{

        // verifico si hay mano pendiente ANTES de incrementar
        if (indiceJugador < jugadores.size()) {
            Jugador jugadorActual = jugadores.get(indiceJugador);

            if (jugadorActual.getJugadorDividio()) {
                // si tiene una division en curso, no cambiamos el turno
                return;
            }
        }
        this.indiceJugador++;
        // Verificar si todos los jugadores terminaron
        if (indiceJugador >= jugadores.size()) {
            // SOLO finalizar partida si estamos en fase de JUEGO
            if (!faseApuestas) finalizarPartida();
        } else {
            // siguiente jugador
            if (faseApuestas) notificarObservadores(Evento.NOTIFICAR_TURNO_APUESTA);
            else notificarObservadores(Evento.NOTIFICAR_TURNO_JUGADOR);
        }
    }
    @Override
    public void setIndiceJugador(int indice){
        this.indiceJugador = indice;
    }
    @Override
    public int getIndice(){
        return this.indiceJugador;
    }
    @Override
    public void setDividirMano() throws RemoteException {
        Jugador jugador = getJugadorActualTurno();

        // Dividir mano
        jugador.getManoActual().dividirMano(jugador);
        jugador.setJugadorDividio(true);
        // MUESTRO LAS APUESTAS DE AMBAS MANOS
        notificarObservadores(Evento.APUESTA_AMBAS_MANOS);

        Carta carta1 = mazo.repartirCarta();
        Carta carta2 = mazo.repartirCarta();

        jugador.getManoActual().recibirCarta(carta1); // Mano 1
        jugador.getMano2().recibirCarta(carta2); // Mano 2

        // se muestran ambas manos a traves del controlador
        notificarObservadores(Evento.MOSTRAR_MANOS_DIVIDIDAS);
    }
    @Override
    public boolean crupierSePaso21(){
        return crupier.getPuntaje() > 21;
    }
    @Override
    public void setApuesta(Jugador jugador, float monto) throws RemoteException {
        jugador.ajustarSaldo(-monto);
        jugador.setApuesta(monto);
        notificarObservadores(Evento.JUGADOR_APOSTO);
    }

    // ----------- METODOS --------------

    @Override
    public boolean realizarApuesta(String monto) throws RemoteException {
        float montoFloat = Float.parseFloat(monto);
        Jugador jugadorActual = getJugadorActualTurno();

        if (jugadorActual.getSaldo() < montoFloat) {
            System.out.println("[SERVIDOR] Saldo insuficiente");
            return false;
        }

        setApuesta(jugadorActual, montoFloat);
        cambiarTurno();

        // Verificar si TODOS apostaron
        if (getIndice() >= jugadores.size()) {
            // CAMBIAR A FASE DE JUEGO PQ TODOS APOSTARON
            faseApuestas = false;

            repartirCartasIniciales();
            notificarObservadores(Evento.CICLO_PARTIDA);
            notificarObservadores(Evento.MOSTRAR_MANO_JUGADOR);
            notificarObservadores(Evento.MOSTRAR_MANO_CRUPIER);

            setIndiceJugador(0);
            for (int i = 0; i < jugadores.size(); i++) {
                if (jugadores.get(i).tieneBlackjack()) jugadorSePlanta();
            }

            notificarObservadores(Evento.NOTIFICAR_TURNO_JUGADOR);

        } else {
            notificarObservadores(Evento.NOTIFICAR_TURNO_APUESTA);
        }

        return true;
    }
    /**
     * Configura un nuevo jugador en el juego
     *
     * @param nickname Nombre del jugador
     * @param saldo    Saldo inicial
     * @return la instancia de un jugador
     */
    @Override
    public Jugador configurarJugadores(String nickname, float saldo) throws RemoteException {
        for (Jugador j : jugadores) {
            if (j.getNombre().equalsIgnoreCase(nickname)) return null;
        }
        Jugador jugador = new Jugador(nickname, saldo);
        jugadores.add(jugador);
        jugadoresListos.put(nickname, true);

        notificarObservadores(Evento.JUGADOR_CONECTADO);
        notificarObservadores(Evento.JUGADOR_MARCO_LISTO);
        notificarObservadores(Evento.ACTUALIZAR_SALA_ESPERA);

        return jugador;
    }

    /**
     * Reparte una carta a un jugador específico por nickname
     */
    @Override
    public void repartirCartaAJugador(String nickname) throws RemoteException {
        Jugador jugador = getJugadorPorNickname(nickname);

        if (jugador == null) throw new RemoteException("Jugador no encontrado: " + nickname);

        Carta carta = mazo.repartirCarta();
        jugador.getManoActual().recibirCarta(carta);

        int puntaje = jugador.getManoActual().getPuntaje();

        // verifico si se pasó de 21
        if (jugador.getManoActual().sePaso21()) jugador.setSePlanto(true);

        if (puntaje == 21) jugador.setSePlanto(true);
    }

    // DEBUG
    @Override
    public void inicializarPartida() throws RemoteException {
        crupier.resetearParaNuevaPartida(); // reseteo la mano del crupier siempre que se inicia la partida
        faseApuestas = true;

        for (Jugador j : jugadores){
            if (j.getActivo()){
                j.resetearParaNuevaPartida();
                if(j.getJugadorDividio()) j.eliminarMano();

                j.setJugadorDividio(false);
            }
        }
        faseApuestas = true;
        setIndiceJugador(0);

        this.mazo = new Mazo();
        mazo.reiniciarBaraja();
        mazo.barajar();
        // Preparar manos de cada jugador
        for (Jugador jugador : jugadores) {
            jugador.getManos().clear();
            jugador.agregarMano();
        }

        // Preparar mano del crupier
        crupier.getManos().clear();
        crupier.agregarMano();

        // iniciar fase de apuestas con el primer jugador
        setIndiceJugador(0);
        notificarObservadores(Evento.NOTIFICAR_TURNO_APUESTA);
    }

    @Override
    public void repartirCartasIniciales() throws RemoteException {
        // Repartir 2 cartas a cada jugador
        for (Jugador jugador : jugadores) {
            jugador.repartirCartaAMano(0, mazo.repartirCarta());
            jugador.repartirCartaAMano(0, mazo.repartirCarta());
        }

        // Repartir al crupier
        crupier.repartirCartaAMano(0, mazo.repartirCarta());
        crupier.repartirCartaAMano(0, mazo.repartirCarta());
    }

    @Override
    public void evaluarGanadores() throws RemoteException {
        // me aseguro que el indice esté en 0 antes de evaluar
        indiceJugador = 0;

        if (crupier.tieneBlackjack()) evaluarGanadoresBlackjack();
        else evaluarGanadoresNOBlackjack();

        // despues de evaluar reseteo el indice una vez mas (por las dudas)
        indiceJugador = 0;
    }

    @Override
    public void evaluarGanadoresBlackjack() throws RemoteException {
        for (int i = 0; i < jugadores.size(); i++) {
            Jugador jugador = jugadores.get(i);

            indiceJugador = i;

            if (jugador.multiplesManos()){
                // mano 1
                if (jugador.getManos().get(0).tieneBlackjack()){
                    notificarObservadores(Evento.CRUPIER_BLACKJACK_Y_EMPATE);
                    devolverApuesta(jugador, jugador.getApuesta());
                }else if (jugador.getPagoSeguro()){
                    notificarObservadores(Evento.DEVUELTO_POR_SEGURO);
                    devolverApuesta(jugador, jugador.getApuesta());
                }else{
                    notificarObservadores(Evento.PERDIO_JUGADOR);
                }

                // mano 2
                if (jugador.getManos().get(1).tieneBlackjack()){
                    notificarObservadores(Evento.CRUPIER_BLACKJACK_Y_EMPATE);
                    devolverApuesta(jugador, jugador.getApuestaMano2());
                }else{
                    notificarObservadores(Evento.PERDIO_JUGADOR);
                }
            }else{
                if (jugador.tieneBlackjack()){
                    notificarObservadores(Evento.CRUPIER_BLACKJACK_Y_EMPATE);
                    devolverApuesta(jugador, jugador.getApuesta());
                }else if (jugador.getPagoSeguro()){
                    notificarObservadores(Evento.DEVUELTO_POR_SEGURO);
                    devolverApuesta(jugador, jugador.getApuesta());
                }else{
                    notificarObservadores(Evento.PERDIO_JUGADOR);
                }
            }

            // notificarObservadores(Evento.PUNTUACION_FINAL_JUGADOR);
        }
    }

    @Override
    public void evaluarGanadoresNOBlackjack() throws RemoteException {
        int puntajeCrupier = crupier.getPuntaje();
        for (int i = 0; i < jugadores.size(); i++) {
            Jugador jugador = jugadores.get(i);

            indiceJugador = i;

            if (jugador.multiplesManos()) {
                // MANO 1
                if (jugador.getManos().get(0).sePaso21()) {
                    notificarObservadores(Evento.PERDIO_JUGADOR);
                } else if (crupierSePaso21()) {
                    notificarObservadores(Evento.GANADOR_JUGADOR);
                    adjudicarGanancia(jugador, jugador.getApuesta());
                } else if (jugador.getManos().get(0).getPuntaje() > puntajeCrupier) {
                    notificarObservadores(Evento.GANADOR_JUGADOR);
                    adjudicarGanancia(jugador, jugador.getApuesta());
                } else if (jugador.getManos().get(0).getPuntaje() < puntajeCrupier) {
                    notificarObservadores(Evento.PERDIO_JUGADOR);
                } else {
                    notificarObservadores(Evento.EMPATO_JUGADOR);
                    devolverApuesta(jugador, jugador.getApuesta());
                }

                // MANO 2
                if (jugador.getManos().get(1).sePaso21()) {
                    notificarObservadores(Evento.PERDIO_JUGADOR);
                } else if (crupierSePaso21()) {
                    notificarObservadores(Evento.GANADOR_JUGADOR);
                    adjudicarGanancia(jugador, jugador.getApuestaMano2());
                } else if (jugador.getMano2().getPuntaje() > puntajeCrupier) {
                    notificarObservadores(Evento.GANADOR_JUGADOR);
                    adjudicarGanancia(jugador, jugador.getApuestaMano2());
                } else if (jugador.getMano2().getPuntaje() < puntajeCrupier) {
                    notificarObservadores(Evento.PERDIO_JUGADOR);
                } else {
                    notificarObservadores(Evento.EMPATO_JUGADOR);
                    devolverApuesta(jugador, jugador.getApuestaMano2());
                }

            } else {
                // MANO SIMPLE
                int puntajeMano1 = jugador.getManoActual().getPuntaje();
                // notificarObservadores(Evento.PUNTUACION_FINAL_JUGADOR);

                if (jugador.getManoActual().sePaso21()) {
                    notificarObservadores(Evento.PERDIO_JUGADOR);
                } else if (crupier.getPuntaje() > 21) {
                    notificarObservadores(Evento.GANADOR_JUGADOR);
                    adjudicarGanancia(jugador, jugador.getApuesta());
                } else if (puntajeMano1 > puntajeCrupier) {
                    notificarObservadores(Evento.GANADOR_JUGADOR);
                    adjudicarGanancia(jugador, jugador.getApuesta());
                } else if (puntajeMano1 < puntajeCrupier) {
                    notificarObservadores(Evento.PERDIO_JUGADOR);
                } else {
                    notificarObservadores(Evento.EMPATO_JUGADOR);
                    devolverApuesta(jugador, jugador.getApuesta());
                }
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
        float ganancia;
        if (jugador.tieneBlackjack()){
            notificarObservadores(Evento.ADJUDICAR_GANANCIA_BJ);
            ganancia = (float) 2.5 * apuesta;
        } else {
            notificarObservadores(Evento.ADJUDICAR_GANANCIA);
            ganancia = (float) 2 * apuesta;
        }
        jugador.ajustarSaldo(ganancia);
        // registrar ganadas en el ranking persistente
        administradorRanking.guardarVictoria(jugador.getNombre(), ganancia);
    }
    /**
     * Metodo para controlar al Crupier una vez se finaliza la opcion de pedir/doblar/dividir/plantarse
     */
    @Override
    public void turnoCrupier() throws RemoteException {
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

    /**
     * Un jugador pide una carta
     * Verifica automáticamente si se pasó de 21 o llegó a 21
     */
    @Override
    public void pedirCartaJugador() throws RemoteException {
        Jugador jugador = getJugadorActualTurno();

        // determina que mano jugar
        Mano manoAJugar;
        if (jugador.getJugadorDividio() && jugador.multiplesManos()) manoAJugar = jugador.getManos().get(manoActualIndex);
        else manoAJugar = jugador.getManoActual();
        // Repartir carta
        Carta carta = mazo.repartirCarta();
        if (carta == null) return;

        manoAJugar.recibirCarta(carta);

        int puntaje = manoAJugar.getPuntaje();
        notificarObservadores(Evento.CARTA_REPARTIDA_JUGADOR);

        // Verificar si se pasó de 21
        if (manoAJugar.sePaso21()) {
            notificarObservadores(Evento.JUGADOR_SE_PASO);

            // Si hay mano 2 pendiente, cambiar a ella
            if (jugador.getJugadorDividio() && manoActualIndex == 0) {
                manoActualIndex = 1;
                notificarObservadores(Evento.CAMBIAR_A_MANO2);
                return;
            }

            // Si no hay más manos, avanzar turno
            jugador.setJugadorDividio(false);
            manoActualIndex = 0;
            cambiarTurno();
            return;
        }

        // Verificar si llegó a 21
        if (puntaje == 21) {
            notificarObservadores(Evento.JUGADOR_LLEGO_21);

            // Si hay mano 2 pendiente, cambiar
            if (jugador.getJugadorDividio() && manoActualIndex == 0) {
                manoActualIndex = 1;
                notificarObservadores(Evento.CAMBIAR_A_MANO2);
                return;
            }
            // Si no hay más manos, avanzar turno
            jugador.setJugadorDividio(false);
            manoActualIndex = 0;
            cambiarTurno();
        }
    }

    /**
     * Un jugador se planta
     */
    @Override
    public void jugadorSePlanta() throws RemoteException {
        Jugador jugador = getJugadorActualTurno();

        if (jugador == null) return;

        if (jugador.getJugadorDividio() && jugador.multiplesManos()) {
            // me planto en la mano actual si el jugador dividio
            // si es mano 1, actualizo el indice a la mano 2
            if (manoActualIndex == 0) {
                manoActualIndex = 1;
                notificarObservadores(Evento.JUGADOR_SE_PLANTO);
                notificarObservadores(Evento.CAMBIAR_A_MANO2);
                return;
            }

            // Si es mano 2, terminar turno
            jugador.setSePlanto(true);
            jugador.setJugadorDividio(false);
            manoActualIndex = 0;  // reseteo para siguiente jugador
            notificarObservadores(Evento.JUGADOR_SE_PLANTO);
            cambiarTurno();
        } else {
            // No hay división, plantarse normal
            jugador.setSePlanto(true);
            notificarObservadores(Evento.JUGADOR_SE_PLANTO);
            cambiarTurno();
        }
    }

    @Override
    public void jugadorDoblaApuesta() throws RemoteException{
        Jugador jugador = getJugadorActualTurno();
        if (jugador == null) return;

        if (jugador.getJugadorDividio() && jugador.multiplesManos()){
            // Doblar según la mano actual
            if (manoActualIndex == 0){
                // Doblar mano 1
                jugador.getManoActual().doblarMano(jugador);
            } else if (manoActualIndex == 1){
                // Doblar mano 2
                float apuestaActual = jugador.getApuestaMano2();
                jugador.ajustarSaldo(-apuestaActual);
                jugador.setApuestaMano2(apuestaActual * 2);
            }
        } else {
            // Mano simple - doblar normal
            jugador.getManoActual().doblarMano(jugador);
        }
    }

    private void finalizarPartida() throws RemoteException {
        // Turno del crupier
        turnoCrupier();
        // Evaluar ganadores
        evaluarGanadores();

        notificarObservadores(Evento.PARTIDA_FINALIZADA);

        setIndiceJugador(0); // reinicio el indice para que se reinicie la ronda una vez terminado todo
    }

    @Override
    public boolean todosJugadoresListos() throws RemoteException {
        if (jugadores.isEmpty()) return false;

        for (Boolean listo : jugadoresListos.values()) if (!listo) return false;
        return true;
    }

    @Override
    public int getCantidadJugadoresConectados() throws RemoteException {
        return jugadores.size();
    }

    @Override
    public int getCantidadJugadoresListos() throws RemoteException {
        int count = 0;
        for (Boolean listo : jugadoresListos.values()) {
            if (listo) count++;
        }
        return count;
    }

    @Override
    public List<String> getNombresJugadoresConectados() throws RemoteException {
        List<String> nombres = new ArrayList<>();
        for (Jugador jugador : jugadores) {
            String nombre = jugador.getNombre();
            boolean listo = jugadoresListos.getOrDefault(nombre, false);
            nombres.add(nombre + (listo ? " (LISTO)" : " (ESPERANDO)"));
        }
        return nombres;
    }

    @Override
    public boolean intentarComenzarPartida() throws RemoteException {
        // Validar que haya al menos 1 jugador
        if (jugadores.isEmpty()) return false;
        // Validar que todos estén listos
        if (!todosJugadoresListos()) return false;

        if (!juegoIniciado && salaAbierta) {
            juegoIniciado = true;
            salaAbierta = false;
            inicializarPartida();
            notificarObservadores(Evento.TODOS_JUGADORES_LISTOS);
            return true;
        }
        return false;
    }

    @Override
    public Jugador getJugadorPorNickname(String nickname) throws RemoteException {
        for (Jugador jugador : jugadores) {
            if (jugador.getNombre().equals(nickname)) return jugador;
        }
        return null;
    }

    @Override
    public List<Carta> getCartasJugador(String nickname) throws RemoteException {
        Jugador jugador = getJugadorPorNickname(nickname);
        if (jugador == null) throw new RemoteException("Jugador no encontrado: " + nickname);

        return jugador.getManoActual().getMano();
    }

    @Override
    public int getPuntajeJugador(String nickname) throws RemoteException {
        Jugador jugador = getJugadorPorNickname(nickname);
        if (jugador == null) throw new RemoteException("Jugador no encontrado: " + nickname);

        return jugador.getManoActual().getPuntaje();
    }

    // SISTEMA DE VOTACION

    /**
     * Inicia la votación para una nueva partida
     */
    @Override
    public void iniciarVotacionNuevaPartida() throws RemoteException {
        votosNuevaPartida.clear();

        // Resetear votos de jugadores
        for (Jugador jugador : jugadores) {
            if (jugador.getActivo()) jugador.setVotoNuevaPartida(null);
        }
        notificarObservadores(Evento.INICIAR_VOTACION_NUEVA_PARTIDA);
    }

    /**
     * Registra el voto de un jugador
     */
    public void votarNuevaPartida(String nickname, boolean voto) throws RemoteException {
        Jugador jugador = getJugadorPorNickname(nickname);
        if (jugador == null || !jugador.getActivo()) return;
        if (votosNuevaPartida.containsKey(nickname)) return;

        votosNuevaPartida.put(nickname, voto);
        jugador.setVotoNuevaPartida(voto);

        notificarObservadores(Evento.ACTUALIZAR_VOTOS);

        // Verificar si todos votaron
        int jugadoresActivos = getJugadoresActivos();
        int votosTotales = votosNuevaPartida.size();
        if (votosTotales >= jugadoresActivos) procesarResultadoVotacion();
    }

    /**
     * Procesa el resultado de la votación
     */
    private void procesarResultadoVotacion() throws RemoteException {
        int votosASi = 0;
        // Contar votos y actualizar estado de jugadores
        for (Map.Entry<String, Boolean> voto : votosNuevaPartida.entrySet()) {
            if (voto.getValue()) votosASi++;
            else {
                // Marcar jugador como inactivo
                Jugador jugador = getJugadorPorNickname(voto.getKey());
                jugador.setActivo(false);
            }
        }
        notificarObservadores(Evento.VOTACION_COMPLETADA);

        // nadie quiere jugar
        if (votosASi == 0) notificarObservadores(Evento.JUEGO_TERMINADO_TODOS_SALIERON);
        else iniciarNuevaPartida();
    }

    /**
     * Inicia una nueva partida con los jugadores activos
     */
    private void iniciarNuevaPartida() throws RemoteException {
        crupier.resetearParaNuevaPartida();
        // Resetear jugadores activos
        for (Jugador jugador : jugadores) {
            if (jugador.getActivo()) jugador.resetearParaNuevaPartida();
        }

        // Reiniciar estado del juego
        faseApuestas = true;
        setIndiceJugador(0);
        manoActualIndex = 0;
        // Crear nuevo mazo
        this.mazo = new Mazo();
        mazo.barajar();
        notificarObservadores(Evento.NUEVA_PARTIDA_INICIADA);
        // Iniciar fase de apuestas
        notificarObservadores(Evento.NOTIFICAR_TURNO_APUESTA);
    }

    /**
     * devuelve la cantidad de votos activos
     */
    private int getJugadoresActivos() {
        int count = 0;
        for (Jugador jugador : jugadores) {
            if (jugador.getActivo()) count++;
        }
        return count;
    }

    /**
     * devuelve la informacion de los votos actuales
     */
    public String getEstadoVotacion() {
        int votosASi = 0;
        int votosANo = 0;
        int sinVotar = 0;
        for (Jugador jugador : jugadores) {
            Boolean voto = jugador.getVotoNuevaPartida();
            if (voto == null) {
                if (jugador.getActivo()) sinVotar++;
            } else if (voto) {
                votosASi++;
            } else {
                votosANo++;
            }
        }
        return "SÍ: " + votosASi + " | NO: " + votosANo + " | Esperando: " + sinVotar;
    }

    @Override
    public boolean recargarSaldo(String nickname, float monto) throws RemoteException {
        if (monto <= 0) return false;
        Jugador jugador = getJugadorPorNickname(nickname);
        if (jugador == null) return false;
        jugador.ajustarSaldo(monto);
        return true;
    }

    @Override
    public String obtenerRankingTotal() throws RemoteException {
        List<DatosJugador> lista = new ArrayList<>(administradorRanking.obtenerTodosLosJugadores().values());
        if (lista.isEmpty()) return null;

        lista.sort((a, b) -> Integer.compare(b.getCantidadGanadas(), a.getCantidadGanadas()));

        StringBuilder sb = new StringBuilder();
        int pos = 1;
        for (DatosJugador d : lista) {
            sb.append(pos++).append(";")
                    .append(d.getNombre()).append(";")
                    .append(d.getCantidadGanadas()).append(";")
                    .append(String.format("%.2f", d.getDineroTotalGanado()))
                    .append("\n");
        }
        return sb.toString();
    }

}
