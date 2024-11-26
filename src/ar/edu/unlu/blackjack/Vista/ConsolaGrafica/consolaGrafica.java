package ar.edu.unlu.blackjack.Vista.ConsolaGrafica;

import ar.edu.unlu.blackjack.Controlador.Controlador;
import ar.edu.unlu.blackjack.Modelo.Carta;
import ar.edu.unlu.blackjack.Modelo.Mano;
import ar.edu.unlu.blackjack.Vista.IVista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.Objects;
import java.util.List;

public class consolaGrafica implements IVista {
    private final JFrame frame;
    private JPanel contentPane;
    private JButton btnEnter;
    private JTextField txtEntrada;
    private JTextArea txtSalida;

    Controlador controlador;
    private int jugadoresRestantes;
    private int cantJugadoresAgregados;
    private int estadoCantidadJugadores;
    private boolean esperandoNickname;
    private boolean flagArrancoPartida;
    private int estadoJugador;
    private boolean vaDecisionJugador;
    private boolean flag;
    private boolean yaRepartio;
    private boolean esperandoSaldo;
    private boolean seCargaronJugadores;
    private boolean inicioMano2;

    public consolaGrafica() {
        frame = new JFrame("Consola Blackjack");
        frame.setContentPane(contentPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // frame.pack();
        frame.setLocationRelativeTo(null);
        txtSalida.setBackground(Color.BLACK);
        txtSalida.setForeground(Color.WHITE);
        frame.setSize(600, 400);
        txtSalida.setEditable(false);
        txtSalida.setAutoscrolls(true);
        txtSalida.setLineWrap(true);
        estadoJugador = -1;
        jugadoresRestantes = 0;
        cantJugadoresAgregados = 0;
        estadoCantidadJugadores = 1;
        esperandoNickname = true;
        flagArrancoPartida = false;
        yaRepartio = false;
        esperandoSaldo = false;
        seCargaronJugadores = false;
        inicioMano2 = false;


        // Configuro para que el botón "Enviar" funcione con enter
        frame.getRootPane().setDefaultButton(btnEnter);

        btnEnter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtSalida.append(txtEntrada.getText() + "\n");
                if (estadoCantidadJugadores == 1){
                    solicitarCantidadJugadores(txtEntrada.getText().toLowerCase());
                }
                if (jugadoresRestantes > 0 && !seCargaronJugadores) {
                    // proceso de carga de nickname y saldo del jugador
                    if (esperandoNickname && txtEntrada.getText().matches("\\d*\\.?\\d")){
                        procesarCargaNickname();
                    }
                    else if (!txtEntrada.getText().matches("\\d*\\.?\\d+")){
                        esperandoNickname = false;
                        controlador.setNickname(txtEntrada.getText());
                        procesarCargaSaldo();
                        esperandoSaldo = true;
                    }else if (esperandoSaldo){
                        float saldo = Float.parseFloat(txtEntrada.getText());
                        controlador.setSaldo(saldo);
                        esperandoNickname = false;
                        esperandoSaldo = false;
                        cargarJugador();
                    }
                    if (jugadoresRestantes == 0){
                        controlador.setIndiceJugador(0);
                        mostrarMensaje("Comenzando la carga de apuestas...");
                        procesarCargaApuestas("");
                        seCargaronJugadores = true;
                        jugadoresRestantes = cantJugadoresAgregados;
                        controlador.setCantidadJugadoresTotales(cantJugadoresAgregados);
                    }
                }else if (jugadoresRestantes > 0){
                    procesarCargaApuestas(txtEntrada.getText());
                    if (jugadoresRestantes == 0){
                        controlador.setIndiceJugador(0);
                        mostrarMensaje("Comenzando partida...");
                        cicloPartida();
                        if (estadoJugador < 0){
                            try {
                                estadoJugador = checkEstadoMano();
                            } catch (RemoteException ex) {
                                throw new RuntimeException(ex);
                            }
                            if (estadoJugador >= 0) mostrarEstadosJugador(estadoJugador);
                        }
                    }
                    // ------- lógica partida comenzada --------
                }else if (flagArrancoPartida){
                    try {
                        procesarDecisionJugador(txtEntrada.getText().toLowerCase(), controlador.manoAUsar());
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                    if (!chequearSiTerminoPartida()){
                        if (estadoJugador < 0 && controlador.getJugadorDividio()){
                            estadoJugador = checkEstadoManosDivididas(controlador.manoAUsar());
                            if (estadoJugador >= 0) mostrarEstadosJugador(estadoJugador);
                        }
                    }else if (estadoJugador < 0 && !flag){ // flag -> jugador intento dividir
                        try {
                            estadoJugador = checkEstadoMano();
                        } catch (RemoteException ex) {
                            throw new RuntimeException(ex);
                        }
                        if (estadoJugador > 0) mostrarEstadosJugador(estadoJugador);
                    }
                }else if (estadoJugador >= 0){
                    mostrarEstadosJugador(estadoJugador);
                    if (estadoJugador == 0 || estadoJugador > 1) {
                        try {
                            procesarDecisionJugador(txtEntrada.getText().toLowerCase(), controlador.manoAUsar());
                        } catch (RemoteException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }else if (vaDecisionJugador){
                    try {
                        procesarDecisionJugador(txtEntrada.getText().toLowerCase(), controlador.manoAUsar());
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                }

                // condición para chequear si termino la partida contando el índice de jugadores -> si es fin termina y el crupier agarra cartas
                if (chequearSiTerminoPartida() && flagArrancoPartida){
                    try {
                        controlador.turnoCrupier();
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                    procesarGanadores();
                    reiniciarPartida();
                }
                txtSalida.setCaretPosition(txtSalida.getDocument().getLength());
                txtEntrada.setText("");
            }
        });
    }
    private boolean checkIngreso(String ingreso){
        return !ingreso.matches("\\d*\\.?\\d+");
    }
    private boolean chequearSiTerminoPartida(){
        return controlador.getIndiceJugadorActual() == controlador.getCantidadJugadoresTotal();
    }

    private void cargarJugador(){
        controlador.configurarJugadores(controlador.getNickname(), (float) controlador.getSaldo());
        esperandoNickname = true;
        esperandoSaldo = false;
        jugadoresRestantes--;
        cantJugadoresAgregados++;
        cambiarTurno();
        if (jugadoresRestantes > 0){
            procesarCargaNickname();
        }
    }
    private void procesarCargaNickname() {
        if (jugadoresRestantes == 0){
            return;
        }
        mostrarMensaje("Ingrese el nombre del jugador " + (cantJugadoresAgregados+1) + ": ");
        // esperandoNickname = false;
        // esperandoSaldo = true;

    }
    private void procesarCargaSaldo() {
        if (jugadoresRestantes == 0){
            mostrarMensaje("Todos los jugadores fueron agregados con éxito.");
            return;
        }
        mostrarMensaje("Ingrese el saldo del jugador " + (cantJugadoresAgregados+1) + ": ");
    }

    private void cambiarTurno(){
        controlador.cambiarTurnoJugador();
    }

    private void reiniciarPartida() {
        estadoJugador = -1;
        jugadoresRestantes = 0;
        cantJugadoresAgregados = 0;
        estadoCantidadJugadores = 1;
        esperandoNickname = true;
        flagArrancoPartida = false;
        yaRepartio = false;
        controlador.setIndiceJugador(0);
        controlador.setCantidadJugadoresTotales(0);
        controlador.clearJugadores();
        controlador.clearManoCrupier();
        controlador.resetBaraja();
        seCargaronJugadores = false;
        inicioMano2 = false;
    }

    public void setControlador(Controlador controlador) {
        this.controlador = controlador;
    }

    private void procesarGanadores(){
        controlador.evaluandoGanadores();
        mostrarMensaje("=============================================");
        mostrarMensaje("Presione Enter para comenzar una nueva partida");
        mostrarMensaje("=============================================");
    }

    private void procesarCargaApuestas(String monto) {
        /* verifico si el string monto coincide con:
        \\d* -> cero o mas digitos
        \\.? -> separador decimal (.) - (?) cero o una ocurrencia de comas
        \\d+ -> uno o mas digitos, asegura que haya al menos un numero despues del punto decimal
         */
        if (!monto.matches("\\d*\\.?\\d+") && controlador.getNombreJugador() != null){
            this.mostrarMensaje(controlador.getNombreJugador() + ": ingrese el monto a apostar (Saldo: $" + controlador.getSaldo() + "): ");
            return;
        }
        if (!Objects.equals(monto, "")){
            controlador.setMontoApostado(Float.parseFloat(monto));
            if (!controlador.cargarApuestaJugador(monto)) {
                mostrarMensaje("Error al apostar. Monto excedido.");
                procesarCargaApuestas("");
                return;
            }
            jugadoresRestantes--;
        }
        if (jugadoresRestantes > 0){
            txtEntrada.setText("");
            cambiarTurno();
            procesarCargaApuestas("");
        }
    }
    public void cicloPartida() {
        if (controlador.getIndiceJugadorActual() == controlador.getCantidadJugadoresTotal()){
            // chequear condicion
            try {
                controlador.turnoCrupier();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        while (controlador.getIndiceJugadorActual() != controlador.getCantidadJugadoresTotal() && !yaRepartio){
            controlador.repartirCartasIniciales(controlador.obtenerJugadorActual());
            controlador.cambiarTurnoJugador();
        }
        yaRepartio = true;
        if (controlador.getIndiceJugadorActual() == controlador.getCantidadJugadoresTotal()){
            controlador.cambiarTurnoJugador();
            // Coloco el indice en 0 nuevamente para que comience la partida
        }
        flagArrancoPartida = true;
        // mostrarMensaje("DEBUG -> IndiceJugador = " + controlador.getIndiceJugadorActual());
        mostrarMensaje("Es el turno de: " + controlador.getNombreJugador() + "\n");
        mostrarMensaje("El saldo del jugador es de: " + controlador.getSaldoJugadorActual());
        if (!controlador.crupierTieneCarta()){
            controlador.crupierPideCarta();
            controlador.crupierPideCarta();
        }
        mostrarMensaje("Cartas restantes: " + controlador.cartasRestantes());
        mostrarMensaje("El crupier tiene: " + controlador.crupierMuestraPrimerCarta());
        vaDecisionJugador = true;
        flag = true; // Flag para intercambiar entre decision y check jugador
    }

    private void mostrarConsola(){
        frame.setVisible(true);
    }

    private void mostrarEstadosJugador(int estadoJugador){
        if (estadoJugador == 1){
            // mostrarManoJugadorVista();
            mostrarMensaje("Felicitaciones " + controlador.getNombreJugador() + " conseguiste BJ!");
            vaDecisionJugador = false;
            cambiarTurno(); // ya con BJ no se puede seguir pidiendo ni plantarse.
            if (controlador.getIndiceJugadorActual() == controlador.getCantidadJugadoresTotal()){
            }
        }else if (estadoJugador == 2){
            mostrarMensaje("Usted pagó el seguro por un valor de ($" + controlador.getApuestaJugador()/2 + " pesos).");
            mostrarMenuOpciones();
            vaDecisionJugador = true;
        }else if (estadoJugador == 3){
            mostrarMenuOpcionesPuedeDividir();
            vaDecisionJugador = true;
        }else if (estadoJugador == 4){
            mostrarMensaje("Se pasó de 21. Perdió la ronda.");
            controlador.cambiarTurnoJugador();
        }else if (estadoJugador == 5){
            mostrarMenuOpcionesYaPidio();
            vaDecisionJugador = true;
        }else if (estadoJugador == 6){
            if (!controlador.getJugadorDividio()){
                cambiarTurno();
                vaDecisionJugador = false;
            }else{
                if (!inicioMano2){
                    // no inicio mano 2
                    cargarManoDividida();
                    // sigue con la siguiente mano
                }else{
                    cambiarTurno();
                    // si esta la mano iniciada y se planta se cambia el turno
                }
            }
        }
        else if (estadoJugador == 0){
            mostrarMenuOpciones();
            vaDecisionJugador = true;
        }
        this.estadoJugador = -1; // Lo seteo en -1 para que vuelva a ser check
    }

    /**
     * Metodo que recibe la cantidad de jugadores que van a jugar inicialmente
     * @param input -> entrada por teclado del nickname del jugador
     */
    private void solicitarCantidadJugadores(String input) {
        try {
            int cantidad = Integer.parseInt(input);
            if (cantidad >= 1 && cantidad <= 7){
                jugadoresRestantes = cantidad;
                mostrarMensaje("Cantidad de jugadores: " + jugadoresRestantes + "\n");
                estadoCantidadJugadores = 0;
                // procesarNombresJugadores();
            }else{
                this.mostrarMensaje("[!] Debes ingresar una cantidad entre 1 y 7\n");
                this.mostrarMensaje("Ingrese la cantidad de jugadores (1 - 7): ");
            }
        } catch (NumberFormatException e) {
            this.mostrarMensaje("[!] Debes ingresar una cantidad entre 1 y 7\n");
            this.mostrarMensaje("Ingrese la cantidad de jugadores (1 - 7): ");
        }
    }
    /**
     *
     */
    public void iniciarJuego() {
        mostrarConsola();
        this.mostrarMensaje("Bienvenido al Blackjack!\n");
        this.mostrarMensaje("Por favor, ingrese la cantidad de jugadores (1 - 7): ");
    }
    /**
     * @param mensaje -> le pasa el mensaje para que se muestre en pantalla de la consola.
     */
    public void mostrarMensaje(String mensaje) {
        txtSalida.append(mensaje + "\n");
        txtSalida.update(txtSalida.getGraphics());
    }
    public void mostrarMensaje2(String mensaje){
        txtSalida.append(mensaje);
        txtSalida.update(txtSalida.getGraphics());
    }

    public void mostrarCartasJugador(){
        List<Carta> cartas = controlador.getCartasMano();
        mostrarMensaje("El jugador " + controlador.getNombreJugador() + " tiene las siguientes cartas:");
        for (Carta carta : cartas){
            this.mostrarMensaje(carta.getValor() + " de " + carta.getPalo());
        }
    }
    @Override
    public void mostrarPuntuacionParcial(){
        int sumatoriaPuntaje = 0;
        int aux = 0;
        int ases = 0;
        for (Carta carta : controlador.getCartasMano()){
            sumatoriaPuntaje += carta.getValorNumerico();
            if (carta.getValorNumerico() == 11) ases++;
        }
        while (sumatoriaPuntaje > 21 && ases > 0){
            aux = sumatoriaPuntaje;
            sumatoriaPuntaje -= 10;
            ases--;
            if (ases == 1){
                aux = sumatoriaPuntaje;
                sumatoriaPuntaje -= 10;
                ases--;
            }
        }
        if ((controlador.jugadorActualTieneAs() && sumatoriaPuntaje < 21) && aux < 21){
            mostrarMensaje("El puntaje actual es de: " + (controlador.getPuntajeMano()-10) + "/" + controlador.getPuntajeMano());
        }else mostrarMensaje("El puntaje del jugador es de: " + controlador.getPuntajeMano());
    }

//    @Override
//    public void mostrarManoJugadorVista() {
//        mostrarMensaje("");
//        int sumatoriaPuntaje = 0;
//        int aux = 0;
//        int ases = 0;
//        mostrarMensaje(controlador.getNombreJugador() + " tiene las siguientes cartas:");
//        for (Carta cartaJugador : controlador.getCartasMano()) {
//            mostrarMensaje(cartaJugador.getValor() + " de " + cartaJugador.getPalo());
//            sumatoriaPuntaje += cartaJugador.getValorNumerico();
//            if (cartaJugador.getValorNumerico() == 11) ases++;
//        }
//        while (sumatoriaPuntaje > 21 && ases > 0){
//            aux = sumatoriaPuntaje;
//            sumatoriaPuntaje -= 10;
//            ases--;
//            if (ases == 1){
//                aux = sumatoriaPuntaje;
//                sumatoriaPuntaje -= 10;
//                ases--;
//            }
//        }
//        if ((controlador.jugadorActualTieneAs() && sumatoriaPuntaje <= 20) && aux < 21){
//            mostrarMensaje("El puntaje actual es de: " + (controlador.getPuntajeMano()-10) + "/" + controlador.getPuntajeMano());
//        }else mostrarMensaje("El puntaje del jugador es de: " + controlador.getPuntajeMano());
//        mostrarMensaje("");
//    }

    /**
     * Metodo donde muestra las manos del jugador cuando divide
     * CONSULTAR SI DEBO HACER LA MISMA METODOLOGIA QUE CON UNA MANO INDIVIDUAL (COMUNICACION ENTRE CONTROLADOR -> MODELO)
     */
    public void mostrarManosDivididasJugadorVista() {
        int sumatoriaPuntaje1 = 0;
        int sumatoriaPuntaje2 = 0;
        int ases1 = 0;
        int ases2 = 0;
        int aux1 = 0;
        int aux2 = 0;
        mostrarMensaje("===================================================");
        mostrarMensaje(controlador.getNombreJugador() + " tiene las siguientes cartas en ambas manos: ");
        Carta cartasMano1;
        Carta cartaMano2;

        int cantidadCartas = Math.max(controlador.getCantidadCartasManoIndice(0), controlador.getCantidadCartasManoIndice(1));
        mostrarMensaje("-= MANO 1=-\t\t -= MANO 2 =-\n");
        for (int i = 0; i < cantidadCartas; i++){
            if (i < controlador.getCantidadCartasManoIndice(0)){
                cartasMano1 = controlador.getCartasManoIndice(0, i);
                mostrarMensaje2(cartasMano1.getValor() + " de " + cartasMano1.getPalo() + "       ");
                sumatoriaPuntaje1 += cartasMano1.getValorNumerico();
                if (cartasMano1.getValorNumerico() == 11) ases1++;
            }else mostrarMensaje2("\t");
            if (i < controlador.getCantidadCartasManoIndice(1)){
                cartaMano2 = controlador.getCartasManoIndice(1, i);
                mostrarMensaje2("\t");
                mostrarMensaje2(cartaMano2.getValor() + " de " + cartaMano2.getPalo() + "\n");
                sumatoriaPuntaje2 += cartaMano2.getValorNumerico();
                if (cartaMano2.getValorNumerico() == 11) ases2++;
            }else mostrarMensaje2("\t");
        }
        while (sumatoriaPuntaje1 > 21 && ases1 > 0){
            aux1 = sumatoriaPuntaje1;
            sumatoriaPuntaje1 -= 10;
            ases1--;
            if (ases1 == 1){
                aux1 = sumatoriaPuntaje1;
                sumatoriaPuntaje1 -= 10;
                ases1--;
            }
        }
        while (sumatoriaPuntaje2 > 21 && ases2 > 0){
            aux2 = sumatoriaPuntaje2;
            sumatoriaPuntaje2 -= 10;
            ases2--;
            if (ases2 == 1){
                aux2 = sumatoriaPuntaje2;
                sumatoriaPuntaje2 -= 10;
                ases2--;
            }
        }
        mostrarMensaje("");
        if ((controlador.getJugadorTieneAsManoIndice(0) && sumatoriaPuntaje1 < 21) && aux1 < 21){
            mostrarMensaje("El puntaje actual de la mano 1 es: " + (controlador.getPuntajeManosIndices(0)-10) + "/" + controlador.getPuntajeManosIndices(0));
        }else mostrarMensaje("El puntaje actual de la mano 1 es: " + controlador.getPuntajeManosIndices(0));
        if ((controlador.getJugadorTieneAsManoIndice(1) && sumatoriaPuntaje2 < 21) && aux2 < 21){
            mostrarMensaje("El puntaje actual de la mano 2 es: " + (controlador.getPuntajeManosIndices(1)-10) + "/" + controlador.getPuntajeManosIndices(0));
        }else mostrarMensaje("El puntaje actual de la mano 2 es: " + controlador.getPuntajeManosIndices(1));
        mostrarMensaje("===================================================");
    }
    @Override
    public void mostrarPuntuacionParcialCrupier(){
        int sumatoriaPuntajeCrupier = 0;
        int auxCrupier = 0;
        int asesCrupier = 0;
        for (Carta c : controlador.getManoCrupier()){
            sumatoriaPuntajeCrupier += c.getValorNumerico();
            if (c.getValorNumerico() == 11) asesCrupier++;
        }
        while (sumatoriaPuntajeCrupier > 21 && asesCrupier > 0){
            auxCrupier = sumatoriaPuntajeCrupier;
            sumatoriaPuntajeCrupier -= 10;
            asesCrupier--;
            if (asesCrupier == 1){
                auxCrupier = sumatoriaPuntajeCrupier;
                sumatoriaPuntajeCrupier -= 10;
                asesCrupier--;
            }
        }
        if (sumatoriaPuntajeCrupier >= 17) mostrarCartasCrupier();
        if ((controlador.getCrupierTieneAsPrimera() && sumatoriaPuntajeCrupier < 21) && auxCrupier < 21){
            mostrarMensaje("El puntaje actual del crupier es de: " + (controlador.getPuntajeCrupier()-10) + "/" + controlador.getPuntajeCrupier());
        }else mostrarMensaje("El puntaje actual del crupier es de: " + controlador.getPuntajeCrupier());
    }
//    public void mostrarManoCrupierVista() {
//        mostrarMensaje("");
//        int sumatoriaPuntaje = 0;
//        int ases = 0;
//        int aux = 0;
//        mostrarMensaje("El crupier tiene:");
//        for (Carta carta : controlador.getManoCrupier()) {
//            mostrarMensaje(carta.getValor() + " de " + carta.getPalo());
//            sumatoriaPuntaje += carta.getValorNumerico();
//            if (carta.getValorNumerico() == 1) ases++;
//        }
//        while (sumatoriaPuntaje > 21 && ases > 0){
//            aux = sumatoriaPuntaje;
//            sumatoriaPuntaje -= 10;
//            ases--;
//            if (ases == 1){
//                aux = sumatoriaPuntaje;
//                sumatoriaPuntaje -= 10;
//                ases--;
//            }
//        }
//        if ((controlador.getCrupierTieneAsPrimera() && sumatoriaPuntaje < 21) && aux < 21){
//            mostrarMensaje("El puntaje actual del crupier es de: " + (controlador.getPuntajeCrupier()-10) + "/" + controlador.getPuntajeCrupier());
//        }else mostrarMensaje("El puntaje actual del crupier es de: " + controlador.getPuntajeCrupier());
//    }

    /**
     * Metodo para controlar al Crupier una vez se finaliza la opcion de pedir/doblar/dividir/plantarse

    public void turnoCrupier() {
        while (controlador.crupierDebePedirCarta()){
            mostrarMensaje("El crupier está obteniendo una carta...");
            try{
                Thread.sleep(1000);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
            controlador.crupierPideCarta();
            mostrarManoCrupierVista();
            imprimioCartas = true;
        }
        if (!imprimioCartas) mostrarManoCrupierVista();

        // Verifico si la mano se paso de 21
        if (controlador.crupierSePaso21()){
            mostrarMensaje("El crupier se pasó de los 21.");
        }else mostrarMensaje("El crupier se planta en " + controlador.getPuntajeCrupier() + ".");
        if (controlador.getPuntajeCrupier() >= 17){
            mostrarMensaje("====================================");
            mostrarMensaje("Evaluando ganadores...");
            mostrarMensaje("Presione Enter para continuar...");
        }
    }
     */
    private int checkEstadoMano() throws RemoteException {
        Mano mano = controlador.obtenerManoJugador();
        if (mano != null) {
            if (controlador.getJugadorSePlanto()){
                return 6; // 6 = se planta el jugador
            }
            if (!controlador.getJugadorDividio()) controlador.mostrarManoJugador();
            if (controlador.getJugadorTieneBlackjack()) {
                // mostrarManoJugadorVista();
                // mostrarMensaje("Felicitaciones " + controlador.getNombreJugador() + " conseguiste BJ!");
                return 1; // 1 == Blackjack al Jugador
            }
            if (controlador.getCrupierTieneAsPrimera() && !controlador.getJugadorPidioCarta()){
                controlador.checkJugadorPagaSeguro();
                if (controlador.getSaldoJugadorActual() >= controlador.getApuestaJugador()/2){
//                    int seguroBlackjack = JOptionPane.showConfirmDialog(
//                            frame, // JFrame
//                            "ATENCIÓN! El crupier tiene un As de primera.",
//                            "¿Desea pagar el seguro?",
//                            JOptionPane.YES_NO_OPTION
//                    );
                    String seguroBlackjack = txtEntrada.getText();
                    if (Integer.parseInt(seguroBlackjack) == 1) {
                        controlador.setPagoSeguroJugador(true);
                        mostrarMensaje("El jugador " + controlador.getNombreJugador() + " decidió pagar el seguro.");
                        controlador.ajustarSaldoJugador(-(controlador.getApuestaJugador() / 2));
                        return 2; // Jugador Pago Seguro BJ
                    } else {
                        mostrarMensaje(controlador.getNombreJugador() + " decidió no pagar el seguro.");
                    }
                }else mostrarMensaje("[!] No podés pagar el seguro debido que no tenés saldo suficiente (" + controlador.getSaldoJugadorActual() + ").");
            }
            if (controlador.getSePaso21Index(0)){
                return 4; // Jugador se pasa de 21.
            }
            if (controlador.getJugadorPuedeDividir() && controlador.getSaldoJugadorActual() >= controlador.getApuestaJugador()){
                // El jugador puede dividir por lo que retorno un '3'
                return 3; // puede dividir
            }
            if (controlador.getJugadorPidioCarta()) return 5; // Muestra Menu sin posibilidad de doblar ni dividir.

            return 0; // Continua el juego normalmente
        }
        flag = false;
        return -1; // Error en el chequeo
    }
    private void procesarDecisionJugador(String decision, int i) throws RemoteException{
        if (decision.equals("c")){
            mostrarMensaje(controlador.getNombreJugador() + " pidió una carta.");
            controlador.setJugadorPidioCarta(true);
            // controlador.recibirCartaJugador();
            controlador.setRepartirCartaAMano(i);
            if (controlador.getManosJugador().size() == 1) controlador.mostrarManoJugador();
            else mostrarManosDivididasJugadorVista();
            if (!controlador.getSePaso21Index(i)){
                mostrarMenuOpcionesYaPidio();
                vaDecisionJugador = true;
            }
        }else if (decision.equals("p")){
            if (controlador.getManosJugador().size() == 2) mostrarMensaje(controlador.getNombreJugador() + " se plantó con " + controlador.getPuntajeManosIndices(i) + " en la mano " + (i+1) + ".");
            else mostrarMensaje(controlador.getNombreJugador() + " se plantó con " + controlador.getPuntajeMano() + ".");
            controlador.setJugadorSePlanto(true);
        }else if (decision.equals("s")){
            if ((controlador.getSaldoJugadorActual() >= controlador.getApuestaJugador()) && controlador.compararDosCartasIguales() && !controlador.getJugadorDividio()){
                mostrarMensaje(controlador.getNombreJugador() + " dividió la mano.");
                dividirMano();
            }else if (controlador.getJugadorDividio()){
                mostrarMensaje("[!] -> Ya dividiste. No podés dividir dos veces!");
            }
            else{
                // mostrarMensaje("[!] No podés dividir dado que no tenés saldo suficiente!");
                mostrarMensaje("[!] -> Saldo no disponible para dividir.");
                estadoJugador = checkEstadoMano();
                if (estadoJugador >= 0) mostrarEstadosJugador(estadoJugador);
                flag = true;
                return;
            }
        }else if (decision.equals("d")){
            if (controlador.getSaldoJugadorActual() >= controlador.getApuestaJugador() && !controlador.getJugadorPidioCarta()){
                mostrarMensaje(controlador.getNombreJugador() + " dobló la mano.");
                controlador.recibirCartaJugador();
                controlador.jugadorDobloMano();
                controlador.setJugadorPidioCarta(true);
                controlador.mostrarManoJugador();
                vaDecisionJugador = false;
            }else if (controlador.getJugadorPidioCarta()){
                mostrarMensaje("[!] No podés doblar dado que ya pediste una carta!");
            }else mostrarMensaje("[!] No podés doblar dado que no tenés saldo suficiente!");
        }else mostrarMensaje("[!] Lo que se ingresó no es válido.");
        if (!controlador.getJugadorSePlanto()){
            if (controlador.getPuntajeMano() == 21){
                this.mostrarMensaje("Felicitaciones, conseguiste 21!");
                cambiarTurno();
                vaDecisionJugador = false;
            }else if (controlador.getSePaso21ManoPrincipal()){
                this.mostrarMensaje("Te pasaste de 21. Perdiste.");
                vaDecisionJugador = false;
                cambiarTurno();
            }else if (controlador.getJugadorDoblo()){
                cambiarTurno();
            }
        }else cambiarTurno();
        if (controlador.getIndiceJugadorActual() == controlador.getCantidadJugadoresTotal()){
            vaDecisionJugador = false;
        }
    }
    private void dividirMano(){
        mostrarMensaje("Dividiendo manos...");
        controlador.dividirManoJugador();
        controlador.setRepartirCartaAMano(0);
        controlador.setRepartirCartaAMano(1);
        controlador.setJugadorDividio(true);
        mostrarManosDivididasJugadorVista();
    }

    private void cargarManoDividida() {
        if (!inicioMano2 && controlador.manoAUsar() == 1){
            // seteo como false que pidio carta para continuar con la mano 2
            controlador.setJugadorPidioCarta(false);
            controlador.setJugadorSePlanto(false);
            estadoJugador = checkEstadoManosDivididas(1);
            inicioMano2 = true;
            // doy inicio a la mano 2
            mostrarEstadosJugador(estadoJugador);
        }
    }
    private int checkEstadoManosDivididas(int indiceMano){
        // mismo que checkEstadoMano() pero con lógica de manos divididas (2 manos para un jugador).
        List<Mano> manos = controlador.getManosJugador();
        if (manos != null){
            if (controlador.getJugadorSePlanto()){
                return 6; // jugador se plantó con la mano i
            }
            // mostrarManosDivididasJugadorVista();
            if (controlador.getTieneBlackjackPorIndiceMano(indiceMano)){
                return 1; // blackjack para la mano i
            }
            // No se consulta por seguro dado que para eso primero se consulta con una sola mano y luego se procede al juego
            if (controlador.getSePaso21Index(indiceMano)) return 4;
            if (controlador.getJugadorPidioCarta()) return 5;
            return 0; // retorna 0 y sigue normalmente
        }
        return -1;
    }

    private void mostrarMenuOpciones(){
        if (controlador.getJugadorDividio()){
            if (!inicioMano2){
                mostrarMensaje("MANO " + (controlador.manoAUsar() + 1) + " --> " + controlador.getNombreJugador() + ": ingrese 'c' para pedir, 'd' para doblar o 'p' para plantarse: ");
            }else mostrarMensaje("MANO " + (controlador.manoAUsar() + 2) + " --> " + controlador.getNombreJugador() + ": ingrese 'c' para pedir, 'd' para doblar o 'p' para plantarse: ");
        }
        else mostrarMensaje(controlador.getNombreJugador() + ": ingrese 'c' para pedir, 'd' para doblar o 'p' para plantarse: ");
    }
    private void mostrarMenuOpcionesYaPidio(){
        if (controlador.getJugadorDividio()){
            if (!inicioMano2){
                mostrarMensaje("MANO " + (controlador.manoAUsar() + 1) + " --> " + controlador.getNombreJugador() + ": ingrese 'c' para pedir o 'p' para plantarse: ");
            }else mostrarMensaje("MANO " + (controlador.manoAUsar() + 2) + " --> " + controlador.getNombreJugador() + ": ingrese 'c' para pedir o 'p' para plantarse: ");
        }
        else mostrarMensaje(controlador.getNombreJugador() + ": ingrese 'c' para pedir o 'p' para plantarse: ");
    }
    private void mostrarMenuOpcionesPuedeDividir(){
        mostrarMensaje(controlador.getNombreJugador() + ": ingrese 'c' para pedir, 'd' para doblar, 's' para dividir o 'p' para plantarse: ");
    }

    public void mostrarCartasCrupier(){
        for (Carta c : controlador.getManoCrupier()){
            mostrarMensaje(c.getValor() + " de " + c.getPalo());
        }
    }
}

