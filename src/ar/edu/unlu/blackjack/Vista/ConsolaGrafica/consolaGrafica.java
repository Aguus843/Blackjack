package ar.edu.unlu.blackjack.Vista.ConsolaGrafica;

import ar.edu.unlu.blackjack.Controlador.Controlador;
import ar.edu.unlu.blackjack.Modelo.Carta;
import ar.edu.unlu.blackjack.Modelo.Mano;
import ar.edu.unlu.blackjack.Vista.IVista;

import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;
import java.util.List;

public class consolaGrafica implements IVista {
    private JFrame frame;
    private JPanel contentPane;
    private JButton btnEnter;
    private JTextField txtEntrada;
    private JTextArea txtSalida;
    private Controlador controlador;
    private boolean enSalaEspera;
    private boolean misDatosConfigurados;
    private boolean esperandoNickname;
    private boolean esperandoSaldo;

    private boolean juegoComenzado;
    private boolean faseApuestas;
    private boolean faseJuego;
    private boolean esmiTurno;
    private boolean esperandoDecision;

    private String nicknameTemporal;
    private float saldoTemporal;
    private boolean enVotacion;
    private boolean votacionMostrada;

    // ==================== SEGURO ====================
    // true mientras el servidor espera que este jugador responda la oferta de seguro
    private boolean esperandoRespuestaSeguro = false;
    // ================================================

    public consolaGrafica() {
        iniciarConsola();
        configurarApariencia();
        inicializarEstados();
        configurarEntradas();
    }

    private void iniciarConsola() {
        frame = new JFrame("Consola Gráfica - Blackjack :: Agustín Weisbek");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);

        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout(5, 5));

        txtSalida = new JTextArea();
        txtSalida.setEditable(false);
        txtSalida.setLineWrap(true);
        txtSalida.setWrapStyleWord(true);
        txtSalida.setFont(new Font("Consolas", Font.PLAIN, 13));

        JScrollPane scrollPane = new JScrollPane(txtSalida);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JPanel panelInferior = new JPanel(new BorderLayout(5, 0));

        txtEntrada = new JTextField();
        txtEntrada.setFont(new Font("Consolas", Font.PLAIN, 13));

        btnEnter = new JButton("Enviar");
        btnEnter.setPreferredSize(new Dimension(100, 30));

        panelInferior.add(txtEntrada, BorderLayout.CENTER);
        panelInferior.add(btnEnter, BorderLayout.EAST);

        contentPane.add(scrollPane, BorderLayout.CENTER);
        contentPane.add(panelInferior, BorderLayout.SOUTH);

        frame.setContentPane(contentPane);
        frame.getRootPane().setDefaultButton(btnEnter);
    }

    private void configurarApariencia() {
        txtSalida.setBackground(Color.BLACK);
        txtSalida.setForeground(Color.GREEN);
        txtSalida.setCaretColor(Color.GREEN);
        txtSalida.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    private void inicializarEstados() {
        enSalaEspera = true;
        misDatosConfigurados = false;
        esperandoNickname = true;
        esperandoSaldo = false;

        juegoComenzado = false;
        faseApuestas = false;
        faseJuego = false;
        esmiTurno = false;
        esperandoDecision = false;
        enVotacion = false;
        votacionMostrada = false;
        esperandoRespuestaSeguro = false;
    }

    private void configurarEntradas() {
        btnEnter.addActionListener(e -> procesarEntrada());
        txtEntrada.addActionListener(e -> procesarEntrada());
    }

    private void procesarEntrada() {
        String entrada = txtEntrada.getText().trim();

        if (entrada.isEmpty()) {
            return;
        }

        txtSalida.append("> " + entrada + "\n");

        try {
            // datos iniciales (nickname y saldo)
            if (enSalaEspera && !misDatosConfigurados) {
                procesarConfiguracionInicial(entrada);
                txtEntrada.setText("");
                txtSalida.setCaretPosition(txtSalida.getDocument().getLength());
                return;
            }

            // Comando COMENZAR en el lobby
            if (enSalaEspera && misDatosConfigurados && !juegoComenzado) {
                procesarComandoComenzar(entrada);
                txtEntrada.setText("");
                txtSalida.setCaretPosition(txtSalida.getDocument().getLength());
                return;
            }

            if (enVotacion){
                procesarVoto(entrada);
                txtEntrada.setText("");
                txtSalida.setCaretPosition(txtSalida.getDocument().getLength());
                return;
            }

            // ==================== SEGURO ====================
            // Cuando el servidor ofreció el seguro, SOLO aceptamos SI / NO.
            // Cualquier otro comando se ignora hasta que el jugador responda.
            if (esperandoRespuestaSeguro) {
                procesarRespuestaSeguro(entrada);
                txtEntrada.setText("");
                txtSalida.setCaretPosition(txtSalida.getDocument().getLength());
                return;
            }
            // ================================================

            if (entrada.equalsIgnoreCase("saldo")) {
                mostrarMensaje("\nTu saldo actual: $" + String.format("%.2f", controlador.getSaldoJugadorActual()) + "\n\n");
                txtEntrada.setText("");
                txtSalida.setCaretPosition(txtSalida.getDocument().getLength());
                return;
            }

            if (entrada.equalsIgnoreCase("ranking")) {
                mostrarRanking();
                txtEntrada.setText("");
                txtSalida.setCaretPosition(txtSalida.getDocument().getLength());
                return;
            }

            if (entrada.toLowerCase().startsWith("recargar")) {
                procesarRecarga(entrada);
                txtEntrada.setText("");
                txtSalida.setCaretPosition(txtSalida.getDocument().getLength());
                return;
            }

            // Verificar que el juego haya comenzado
            if (!juegoComenzado) {
                mostrarMensaje("El juego todavía no empezó.\n");
                txtEntrada.setText("");
                txtSalida.setCaretPosition(txtSalida.getDocument().getLength());
                return;
            }

            // Fase de apuestas
            if (faseApuestas) {
                if (esmiTurno) {
                    procesarApuesta(entrada);
                } else {
                    mostrarMensaje("Esperando tu turno para apostar...\n");
                }
                txtEntrada.setText("");
                txtSalida.setCaretPosition(txtSalida.getDocument().getLength());
                return;
            }

            // Fase de juego
            if (faseJuego) {
                if (esmiTurno && esperandoDecision) {
                    procesarDecisionJugador(entrada.toLowerCase());
                } else if (!esmiTurno) {
                    mostrarMensaje("Esperando tu turno para jugar...\n");
                } else {
                    mostrarMensaje("No podés realizar acciones en este momento. Tenes que esperar tu turno!\n");
                }
                txtEntrada.setText("");
                txtSalida.setCaretPosition(txtSalida.getDocument().getLength());
                return;
            }

            // mensaje de error
            mostrarMensaje("Estado del juego no reconocido.\n");

        } catch (RemoteException ex) {
        } catch (NumberFormatException ex) {
            mostrarMensaje("ERROR: Ingresa un número válido.\n");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        txtEntrada.setText("");
        txtSalida.setCaretPosition(txtSalida.getDocument().getLength());
    }

    private void mostrarRanking() {
        try {
            String rankingRaw = controlador.getRankingFormateado();

            mostrarMensaje("\n");
            mostrarMensaje(linea(55) + "\n");
            mostrarMensaje("||" + "              TOP 5 - RANKING JUGADORES           " + "||\n");
            mostrarMensaje(linea(55) + "\n");

            if (rankingRaw == null || rankingRaw.isBlank()) {
                mostrarMensaje("[INFO] Todavia no hay jugadores en el ranking.\n");
                return;
            }

            String[] filas = rankingRaw.trim().split("\n");
            int limite = Math.min(filas.length, 5);

            for (int i = 0; i < limite; i++) {
                String[] partes = filas[i].split(";");
                String nombre  = partes[1];
                String ganadas = partes[2];
                String dinero  = "$" + partes[3];

                mostrarMensaje(String.format("  %-15s  %4s victorias  %12s  \n", nombre, ganadas, dinero));
            }
            mostrarMensaje(linea(55) + "\n");

        } catch (RemoteException e) {
            mostrarMensaje("Error al obtener el ranking.\n\n");
        }
    }

    /**
     * comando "RECARGAR <monto>"
     */
    private void procesarRecarga(String entrada) throws RemoteException {
        String[] partes = entrada.trim().split("\\s+");

        if (partes.length != 2) {
            mostrarMensaje("Sintaxis: RECARGAR <monto>\n");
            mostrarMensaje("Ejemplo: RECARGAR 500\n\n");
            return;
        }

        try {
            float monto = Float.parseFloat(partes[1]);

            if (monto <= 0) {
                mostrarMensaje("\nEl monto debe ser mayor a 0.\n\n");
                return;
            }
            float saldoAntes = controlador.getSaldoJugadorActual();

            boolean exito = controlador.recargarSaldo(monto);

            if (exito) {
                float saldoDespues = controlador.getSaldoJugadorActual();

                mostrarMensaje("\n");
                mostrarMensaje("=======================================\n");
                mostrarMensaje("RECARGA EXITOSA\n");
                mostrarMensaje("=======================================\n");
                mostrarMensaje("Monto recargado: $" + String.format("%.2f", monto) + "\n");
                mostrarMensaje("Saldo anterior: $" + String.format("%.2f", saldoAntes) + "\n");
                mostrarMensaje("Saldo actual: $" + String.format("%.2f", saldoDespues) + "\n");
                mostrarMensaje("=======================================\n\n");
            } else {
                mostrarMensaje("\nError al recargar saldo.\n\n");
            }

        } catch (NumberFormatException e){
        }
    }

    private void procesarVoto(String voto) {
        voto = voto.trim().toLowerCase();

        if (voto.equals("si") || voto.equals("sí") || voto.equals("s")) {
            mostrarMensaje("\nVotaste SÍ - Vas a jugar otra partida!\n");
            try {
                controlador.votarSi();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }

            enVotacion = false;
            votacionMostrada = true;
            esperandoDecision = false;

        } else if (voto.equals("no") || voto.equals("n")) {
            mostrarMensaje("\nVotaste NO - Vas a salir de la partida\n");
            try {
                controlador.votarNo();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
            enVotacion = false;
            votacionMostrada = true;
            esperandoDecision = false;
            mostrarMensaje("Saliste de la partida.\n");

        } else {
            mostrarMensaje("Voto no válido. Escribi SI o NO.\n");
            mostrarMensaje("Voto: \n");
        }
    }

    @Override
    public void ofrecerSeguro() {
        SwingUtilities.invokeLater(() -> {
            try {
                float apuesta      = controlador.getApuestaJugador();
                float montoSeguro  = apuesta / 2f;
                float saldo        = controlador.getSaldoJugadorActual();
                boolean puedePagar = saldo >= montoSeguro;

                mostrarMensaje("El crupier muestra un AS como primera carta.\n\n");
                // mostrarMensaje("Tu apuesta actual: $" + String.format("%.2f", apuesta) + "\n");
                mostrarMensaje("Costo del seguro: $" + String.format("%.2f", montoSeguro) + " (mitad de tu apuesta)\n");
                mostrarMensaje("Tu saldo actual: $" + String.format("%.2f", saldo) + "\n\n");

                if (puedePagar) {
                    mostrarMensaje("Escribi: SI (pagar $" + String.format("%.2f", montoSeguro) + ") | NO (rechazar)\n");
                    mostrarMensaje("Respuesta: ");
                    esperandoRespuestaSeguro = true;
                } else {
                    mostrarMensaje("No tenés saldo suficiente para contratar el seguro.\n");
                    mostrarMensaje("(Necesitás $" + String.format("%.2f", montoSeguro) + ", tenés $" + String.format("%.2f", saldo) + ")\n\n");
                    esperandoRespuestaSeguro = false;
                    controlador.rechazarSeguro();
                }

            } catch (RemoteException e) {
                mostrarMensaje("Error al procesar la oferta de seguro.\n");
                esperandoRespuestaSeguro = false;
                try { controlador.rechazarSeguro(); } catch (RemoteException ex) { ex.printStackTrace(); }
            }
        });
    }

    private void procesarRespuestaSeguro(String respuesta) {
        respuesta = respuesta.trim().toLowerCase();

        if (respuesta.equals("si") || respuesta.equals("sí") || respuesta.equals("s")) {
            try {
                float montoSeguro = controlador.getApuestaJugador() / 2f;
                boolean exito = controlador.aceptarSeguro();

                if (exito) {
                    mostrarMensaje("\nSeguro pagado por $" + String.format("%.2f", montoSeguro) + ".\n");
                    mostrarMensaje("Si el crupier tiene Blackjack, recuperás tu apuesta principal.\n\n");
                } else {
                    mostrarMensaje("\nNo podes pagar el seguro porque no tenes saldo suficiente.\n");
                    mostrarMensaje("Se te rechaza el seguro automaticamente.\n\n");
                    controlador.rechazarSeguro();
                }
            } catch (RemoteException e) {
                mostrarMensaje("Error al aceptar el seguro.\n");
                try { controlador.rechazarSeguro(); } catch (RemoteException ex) { ex.printStackTrace(); }
            }

        } else if (respuesta.equals("no") || respuesta.equals("n")) {
            mostrarMensaje("\nSeguro rechazado. La partida continúa normalmente.\n\n");
            try {
                controlador.rechazarSeguro();
            } catch (RemoteException e) {
                mostrarMensaje("Error al rechazar el seguro.\n");
            }

        } else {
            mostrarMensaje("Escribí SI o NO.\n");
            mostrarMensaje("Respuesta: ");
            return;
        }
        esperandoRespuestaSeguro = false;
        mostrarMensaje("Esperando que los demás jugadores respondan...\n");
    }

    // config inicial
    private void procesarConfiguracionInicial(String entrada) throws RemoteException {
        if (esperandoNickname) {
            procesarNickname(entrada);
        } else if (esperandoSaldo) {
            procesarSaldo(entrada);
        }
    }

    private void procesarNickname(String entrada) {
        if (entrada.matches("\\d+")) {
            mostrarMensaje("El nickname no puede ser solo números.\n");
            mostrarMensaje("Nickname: ");
            return;
        }

        this.nicknameTemporal = entrada;
        mostrarMensaje("Nickname: " + entrada + "\n\n");

        esperandoNickname = false;
        esperandoSaldo = true;
        mostrarMensaje("Saldo inicial: ");
    }

    private void procesarSaldo(String entrada) {
        try {
            float saldo = Float.parseFloat(entrada);

            if (saldo <= 0) {
                mostrarMensaje("El saldo debe ser mayor a 0.\n");
                mostrarMensaje("Saldo inicial: ");
                return;
            }

            this.saldoTemporal = saldo;
            mostrarMensaje("Saldo: $" + String.format("%.0f", saldo) + "\n\n");
            controlador.configurarJugadores(nicknameTemporal, saldoTemporal);

            esperandoSaldo = false;
            misDatosConfigurados = true;

            mostrarMensaje("========================================\n");
            mostrarMensaje("  LISTO PARA JUGAR\n");
            mostrarMensaje("========================================\n\n");
            mostrarMensaje("Escribi COMENZAR (c) para iniciar\n");
            mostrarMensaje("Comandos: COMENZAR (c) | SALA | RANKING | AYUDA\n\n");

        } catch (NumberFormatException e) {
            mostrarMensaje("Ingresa un numero válido. Las letras no son validas.\n");
            mostrarMensaje("Saldo inicial: ");
        } catch (RemoteException e) {
            mostrarMensaje("Error al configurar jugador: " + e.getMessage() + "\n");
            mostrarMensaje("Saldo inicial: ");
        }
    }

    private void procesarComandoComenzar(String comando) throws RemoteException {
        comando = comando.toLowerCase();

        switch (comando) {
            case "comenzar":
            case "start":
            case "c":
                intentarComenzarPartida();
                break;

            case "sala":
            case "jugadores":
                mostrarJugadoresConectados();
                break;

            case "ayuda":
            case "help":
                mostrarMensaje("\nComandos:\n");
                mostrarMensaje("COMENZAR - Iniciar partida\n");
                mostrarMensaje("SAL - Ver jugadores\n");
                mostrarMensaje("RANKING - Ver top 5 jugadores\n");
                mostrarMensaje("AYUDA - Ver comandos\n\n");
                break;

            case "ranking":
                mostrarRanking();
                break;

            default:
                mostrarMensaje("Comando desconocido.\n");
                mostrarMensaje("Pone AYUDA para ver comandos.\n\n");
                break;
        }
    }

    private void intentarComenzarPartida() throws RemoteException {
        mostrarMensaje("Intentando comenzar...\n");

        if (!controlador.intentarComenzarPartida()) {
            int listos = controlador.getCantidadJugadoresListos();
            int total = controlador.getCantidadJugadoresConectados();

            mostrarMensaje("No se puede comenzar aún.\n");
            mostrarMensaje("Jugadores listos: " + listos + "/" + total + "\n\n");
        }
    }

    private void mostrarJugadoresConectados() throws RemoteException {
        List<String> jugadores = controlador.getJugadoresConectados();
        mostrarSalaEspera(jugadores, 0);
    }


    private void procesarApuesta(String entrada) throws RemoteException {
        if (!entrada.matches("\\d*\\.?\\d+")) {
            mostrarMensaje("Ingresa un número válido.\n");
            return;
        }

        float monto = Float.parseFloat(entrada);

        if (monto <= 0) {
            mostrarMensaje("El monto debe ser mayor a 0.\n");
            return;
        }

        float saldoActual = controlador.getSaldoJugadorActual();
        if (monto > saldoActual) {
            mostrarMensaje("Saldo insuficiente.\n");
            mostrarMensaje("Tu saldo: $" + String.format("%.0f", saldoActual) + "\n");
            return;
        }

        boolean apuestaExitosa = controlador.cargarApuestaJugador(String.valueOf(monto));

        if (apuestaExitosa) {
            mostrarMensaje("Apostaste: $" + String.format("%.0f", monto) + "\n\n");

            esmiTurno = false;
            faseApuestas = false;

            try {
                int totalJugadores = controlador.getCantidadJugadoresConectados();
                if (totalJugadores > 1) {
                    mostrarMensaje("Esperando a los demás...\n\n");
                } else {
                    mostrarMensaje("Preparando tu mano...\n\n");
                }
            } catch (Exception e) {
                mostrarMensaje("Esperando...\n\n");
            }
        } else {
            mostrarMensaje("Error al apostar. Intenta de nuevo.\n");
        }
    }

    // ============================================================
    // DECISIONES DE JUEGO
    // ============================================================

    private void procesarDecisionJugador(String decision) throws RemoteException {
        switch (decision) {
            case "pedir":
            case "p":
                accionPedir();
                break;

            case "plantar":
            case "pl":
                accionPlantar();
                break;

            case "doblar":
            case "d":
                accionDoblar();
                break;

            case "dividir":
            case "div":
                accionDividir();
                break;

            // El comando SEGURO ya NO es válido durante el turno de juego.
            // El seguro se ofrece automáticamente antes de que empiece el turno
            // a través del evento OFRECER_SEGURO → ofrecerSeguro().
            // Si el jugador intenta escribirlo igual, se le explica.
            case "seguro":
                mostrarMensaje("El seguro se ofrece automáticamente antes de tu turno\n");
                mostrarMensaje("cuando el crupier muestra un As. No podés activarlo manualmente.\n\n");
                mostrarMensaje("PEDIR | PLANTAR | DOBLAR | DIVIDIR | AYUDA\n");
                mostrarMensaje("Decisión: ");
                break;

            case "ayuda":
            case "?":
                mostrarAyudaJuego();
                break;

            case "ranking":
                mostrarRanking();
                break;

            default:
                mostrarMensaje("Comando no reconocido.\n");
                mostrarMensaje("Escribi AYUDA para ver comandos.\n\n");
                break;
        }
    }


    private void accionPedir() throws RemoteException {
        mostrarMensaje("[!] Pediste una carta!\n");
        controlador.pedirCarta();
        boolean dividio = (controlador.getJugadorDividio() || controlador.getManosJugador().size() > 1);

        if (dividio) {
            mostrarManosDivididasJugadorVista();

            int manoActual = controlador.getManoActualIndex();
            List<Mano> manos = controlador.getManosJugador();
            if (manos.size() <= manoActual) return;

            int puntaje = manos.get(manoActual).getPuntaje();

            if (puntaje > 21) {
                mostrarMensaje("\nTe pasaste de 21 en la mano " + (manoActual + 1) + "!\n");
                mostrarMensaje("Perdiste esta mano :(\n\n");

            } else if (puntaje == 21) {
                mostrarMensaje("\nLlegaste a 21 en la mano " + (manoActual + 1) + "!\n");
                mostrarMensaje("Te plantas automáticamente.\n\n");
            } else {
                mostrarMensaje("\nPIDIR | PLANTAR | DOBLAR | AYUDA\n");
                mostrarMensaje("Decisión: ");
            }

        } else {
            mostrarManoActualizada();
            int puntaje = controlador.getPuntajeMano();

            if (puntaje > 21) {
                mostrarMensaje("\nTe pasaste de 21!\n");
                mostrarMensaje("Perdiste esta mano :(\n\n");
                esmiTurno = false;
                esperandoDecision = false;
                controlador.cambiarTurnoJugador();
            } else if (puntaje == 21) {
                mostrarMensaje("\nLlegaste a 21 puntos!\n");
                mostrarMensaje("Te plantas automáticamente.\n\n");
                esmiTurno = false;
                esperandoDecision = false;
                controlador.cambiarTurnoJugador();
            } else {
                mostrarMensaje("\nIngresa una palabra para la decision!\n");
                mostrarMensaje("PEDIR | PLANTAR | DOBLAR | DIVIDIR | AYUDA\n");
                mostrarMensaje("Decisión: ");
            }
        }
    }

    private void accionPlantar() throws RemoteException {
        int puntaje = controlador.getPuntajeMano();

        mostrarMensaje("[!] Te plantaste con " + puntaje + " puntos!\n");

        List<Mano> manos = controlador.getManosJugador();
        int manoActualIndex = controlador.getManoActualIndex();

        if (manos.size() > 1 && manoActualIndex == 0) {
            mostrarMensaje("Mano 1 plantada. Esperando mano 2...\n");
        } else {
            mostrarMensaje("Esperando a los demas jugadores...\n");
            esmiTurno = false;
            esperandoDecision = false;
            faseJuego = false;
        }

        controlador.plantarse();
    }

    private void accionDoblar() throws RemoteException {
        if (controlador.getManosJugador().size() == 2){
            if (controlador.getManosJugador().get(0).getMano().size() != 2 && controlador.manoAUsar() == 0){
                mostrarMensaje("Solo podes doblar con 2 cartas en la mano 1!\n");
                return;
            }
            if (controlador.getManosJugador().get(1).getMano().size() != 2 && controlador.manoAUsar() == 1){
                mostrarMensaje("Solo podes doblar con 2 cartas en la mano 2!\n");
                return;
            }
        }
        else if (controlador.getCartasMano().size() != 2) {
            mostrarMensaje("Solo puedes doblar con 2 cartas.\n");
            return;
        }

        float apuesta;

        if (controlador.jugadorDividio() && controlador.manoAUsar() == 1) {
            apuesta = controlador.getApuestaJugadorMano2();
        } else {
            apuesta = controlador.getApuestaJugador();
        }

        float saldo = controlador.getSaldoJugadorActual();

        if (saldo < apuesta) {
            mostrarMensaje("Saldo insuficiente para doblar.\n");
            mostrarMensaje("Necesitas: $" + String.format("%.0f", apuesta) + " (mano " + (controlador.manoAUsar() + 1) + ")\n");
            return;
        }

        mostrarMensaje("[LOG] CONTROLADOR.JUGADORDOBLOMANO()...");
        controlador.jugadorDobloMano();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }

        float apuestaDespues = controlador.getApuestaJugador();
        float saldoDespues = controlador.getSaldoJugadorActual();

        mostrarMensaje("[DESPUÉS] Apuesta: $" + String.format("%.0f", apuestaDespues) + " | Saldo: $" + String.format("%.0f", saldoDespues) + "\n");
        mostrarMensaje("\nDoblaste tu apuesta! Apuesta total: $" + String.format("%.0f", apuestaDespues) + "\n");
        mostrarMensaje("Recibis una carta y te plantas automáticamente.\n");

        controlador.recibirCartaJugador();
        mostrarManoActualizada();
        controlador.plantarse();

        if (!controlador.jugadorDividio() || controlador.getManosJugador().size() <= 1) {
            esmiTurno = false;
            esperandoDecision = false;
        }
    }

    private void accionDividir() throws RemoteException {
        if (controlador.getCartasMano().size() != 2) {
            mostrarMensaje("Solo podes dividir con 2 cartas.\n");
            return;
        }

        List<Carta> cartas = controlador.getCartasMano();
        if (!cartas.get(0).getValor().equals(cartas.get(1).getValor())) {
            mostrarMensaje("Solo podes dividir cartas iguales.\n");
            return;
        }

        float apuesta = controlador.getApuestaJugador();
        float saldo = controlador.getSaldoJugadorActual();

        if (saldo < apuesta) {
            mostrarMensaje("Saldo insuficiente para dividir!\n");
            return;
        }

        controlador.dividirMano();
        controlador.setJugadorDividio(true);
        mostrarMensaje("\nDividiste tu mano.\n");
        mostrarMensaje("Jugando primera mano...\n");

        mostrarManosDivididasJugadorVista();
    }

    private void mostrarAyudaJuego() {
        mostrarMensaje("\nComandos disponibles:\n");
        mostrarMensaje("COMENZAR (C)       - Comenzar Partida\n");
        mostrarMensaje("PEDIR (P)          - Pedir carta\n");
        mostrarMensaje("PLANTAR (PL)       - Plantarse\n");
        mostrarMensaje("DOBLAR (D)         - Doblar apuesta\n");
        mostrarMensaje("DIVIDIR (DIV)      - Dividir mano\n");
        mostrarMensaje("RECARGAR <monto>   - Recargar saldo\n");
        mostrarMensaje("SALDO              - Ver el saldo actual\n");
        mostrarMensaje("AYUDA              - Ver comandos\n\n");
        mostrarMensaje("* El SEGURO se ofrece automáticamente cuando\n");
        mostrarMensaje("  el crupier muestra un As. No es un comando manual.\n\n");
    }

    // mostrar cartas

    private void mostrarManoActualizada() throws RemoteException {
        List<Carta> cartas = controlador.getCartasMano();
        int puntaje = controlador.getPuntajeMano();

        mostrarMensaje("\nTus cartas:\n");
        for (Carta carta : cartas) {
            mostrarMensaje("  • " + carta.getValor() + " de " + carta.getPalo() + "\n");
        }

        mostrarMensaje("\nPuntaje: " + puntaje);

        if (controlador.jugadorActualTieneAs() && puntaje <= 21) {
            mostrarMensaje(" (As suave)");
        }

        mostrarMensaje("\n");
    }

    @Override
    public void mostrarMensaje(String texto) {
        SwingUtilities.invokeLater(() -> {
            txtSalida.append(texto);
            txtSalida.setCaretPosition(txtSalida.getDocument().getLength());
        });
    }

    private String linea(int longitud) {
        return "=".repeat(longitud);
    }


    @Override
    public void setControlador(Controlador controlador) {
        this.controlador = controlador;
    }

    @Override
    public void iniciarJuego() {
        SwingUtilities.invokeLater(() -> {
            frame.setVisible(true);

            mostrarMensaje(linea(40) + "\n");
            mostrarMensaje("BIENVENIDO AL BLACKJACK!\n");
            mostrarMensaje(linea(40) + "\n\n");
            mostrarMensaje("Nickname: ");
        });
    }

    @Override
    public void mostrarSalaEspera(List<String> jugadores, int maximo) {
        SwingUtilities.invokeLater(() -> {
            if (!juegoComenzado) {
                mostrarMensaje("\n" + linea(40) + "\n");
                mostrarMensaje("SALA DE ESPERA\n");
                mostrarMensaje(linea(40) + "\n");
                mostrarMensaje("Jugadores: " + jugadores.size() + "\n\n");

                for (int i = 0; i < jugadores.size(); i++) {
                    mostrarMensaje("  " + (i + 1) + ". " + jugadores.get(i) + "\n");
                }

                mostrarMensaje(linea(40) + "\n\n");
            }
        });
    }

    @Override
    public void comenzarPartida() {
        SwingUtilities.invokeLater(() -> {
            try {
                controlador.resetBaraja();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
            juegoComenzado = true;
            enSalaEspera = false;

            votacionMostrada = false;
            enVotacion = false;
            esperandoRespuestaSeguro = false; // resetear seguro también

            mostrarMensaje("\n" + linea(40) + "\n");
            mostrarMensaje("PARTIDA INICIADA!\n");
            mostrarMensaje(linea(40) + "\n\n");
            mostrarMensaje("Ingrese el monto de su apuesta...\n\n");
        });
    }

    @Override
    public void notificarTurnoApuesta() {
        SwingUtilities.invokeLater(() -> {
            if (!faseJuego) {
                esmiTurno = true;
                faseApuestas = true;

                mostrarMensaje(linea(40) + "\n");
                mostrarMensaje("TU TURNO - APUESTA\n");
                mostrarMensaje(linea(40) + "\n");
                try {
                    mostrarMensaje("Saldo: $" + String.format("%.0f", controlador.getSaldoJugadorActual()) + "\n\n");
                    mostrarMensaje("Monto a apostar: ");
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public void notificarTurnoJugador() {
        SwingUtilities.invokeLater(() -> {
            esmiTurno = true;
            faseApuestas = false;
            faseJuego = true;
            esperandoDecision = true;

            mostrarMensaje(linea(40) + "\n");
            mostrarMensaje("ES TU TURNO - DECIDIS QUE HACER CON TU MANO\n");
            mostrarMensaje(linea(40) + "\n");

            // Carta del crupier
            String cartaCrupier = null;
            try {
                cartaCrupier = controlador.getCrupier().mostrarPrimeraCarta();
            } catch (RemoteException e) {
            }
            mostrarMensaje("Crupier: " + cartaCrupier + " + [NO REVELADO]\n\n");

            // Tus cartas
            List<Carta> cartas = null;
            try {
                cartas = controlador.getCartasMano();
            } catch (RemoteException e) {
            }
            mostrarMensaje("Tus cartas:\n");
            for (Carta carta : cartas) {
                mostrarMensaje("  • " + carta.getValor() + " de " + carta.getPalo() + "\n");
            }
            int puntaje = 0;
            try {
                puntaje = controlador.getPuntajeMano();
            } catch (RemoteException e) {
            }
            mostrarMensaje("\nPuntaje: " + puntaje);
            try {
                if (controlador.jugadorActualTieneAs() && puntaje <= 21) {
                    mostrarMensaje(" (As suave)");
                }
            } catch (RemoteException e) {
            }
            mostrarMensaje("\n\n");

            // verifico si hay blackjack
            try {
                if (controlador.getJugadorTieneBlackjack()) {
                    mostrarMensaje("BLACKJACK!!\n\n");
                    esmiTurno = false;
                    esperandoDecision = false;
                    controlador.cambiarTurnoJugador();
                    return;
                }
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
            // el seguro para esta instancia ya fue preguntado por lo que se sigue el
            // flujo normal
            mostrarMensaje("PEDIR | PLANTAR | DOBLAR | DIVIDIR | AYUDA\n");
            mostrarMensaje("Decisión: ");
        });
    }

    @Override
    public void mostrarCartasJugador() {
        // Implementado en notificarTurnoJugador
    }

    @Override
    public void mostrarManoJugador() throws RemoteException {
        mostrarManoActualizada();
    }

    @Override
    public void mostrarManosDivididasJugadorVista() throws RemoteException {
        SwingUtilities.invokeLater(() -> {
            try {
                if (!esmiTurno) {
                    return;
                }

                if (!controlador.jugadorDividio()) {
                    return;
                }

                List<Mano> manos = controlador.getManosJugador();
                if (manos.size() != 2) {
                    return;
                }

                mostrarMensaje("\n");
                mostrarMensaje(linea(50) + "\n");
                mostrarMensaje("MANOS DIVIDIDAS\n");
                mostrarMensaje(linea(50) + "\n\n");

                mostrarMensaje("--- MANO 1 ---\n");
                Mano mano1 = manos.get(0);
                for (Carta carta : mano1.getMano()) {
                    mostrarMensaje("  • " + carta.getValor() + " de " + carta.getPalo() + "\n");
                }
                mostrarMensaje("Puntaje: " + mano1.getPuntaje() + "\n\n");

                mostrarMensaje("--- MANO 2 ---\n");
                Mano mano2 = manos.get(1);
                for (Carta carta : mano2.getMano()) {
                    mostrarMensaje("  • " + carta.getValor() + " de " + carta.getPalo() + "\n");
                }
                mostrarMensaje("Puntaje: " + mano2.getPuntaje() + "\n\n");

                if (controlador.getManoActualIndex() == 0){
                    mostrarMensaje(linea(50) + "\n");
                    mostrarMensaje("Ahora vas a jugar la MANO 1 primero.\n");
                    mostrarMensaje(linea(50) + "\n\n");
                }

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void mostrarPuntuacionParcial() throws RemoteException {
        int puntaje = controlador.getPuntajeMano();
        mostrarMensaje("Tu puntaje: " + puntaje + "\n");
    }

    @Override
    public void mostrarPuntuacionParcialCrupier() throws RemoteException {
        if (controlador.getManoCrupier().size() > 2){
            int puntaje = 0;
            for (int i = 0; i < controlador.getManoCrupier().size(); i++){
                puntaje += controlador.getManoCrupier().get(i).getValorNumerico();
            }
            mostrarMensaje("Puntaje crupier: " + puntaje + "\n");
        }
        if (controlador.getManoCrupier().size() == 2){
            int puntaje = controlador.getManoCrupier().getFirst().getValorNumerico();
            mostrarMensaje("Puntaje crupier: " + puntaje + "\n");
        }
    }

    @Override
    public void cicloPartida() {
        // Implementado en el flujo principal
    }

    /**
     * Muestra los resultados finales de la partida
     */
    @Override
    public void mostrarResultados() {
        SwingUtilities.invokeLater(() -> {
            try {
                mostrarMensaje("\n");
                mostrarMensaje(linea(50) + "\n");
                mostrarMensaje("RESULTADOS FINALES\n");
                mostrarMensaje(linea(50) + "\n\n");

                String miNombre = controlador.getNickname();
                float miSaldo = controlador.getSaldoJugadorActual();
                int puntajeCrupier = controlador.getPuntajeCrupier();
                List<Carta> cartasCrupier = controlador.getCrupier().getManoCarta();

                mostrarMensaje(linea(20) + "\n");
                mostrarMensaje("||" + "MANO DEL CRUPIER"+" ||\n");
                mostrarMensaje(linea(20) + "\n");

                for (Carta carta : cartasCrupier) {
                    mostrarMensaje("  • " + carta.getValor() + " de " + carta.getPalo() + "\n");
                }

                mostrarMensaje("\n  Puntaje: " + puntajeCrupier);
                if (puntajeCrupier > 21) {
                    mostrarMensaje("\nSE PASÓ!\n");
                } else if (puntajeCrupier == 21 && cartasCrupier.size() == 2) {
                    mostrarMensaje("\nEL CRUPIER OBTUVO BLACKJACK!\n");
                } else {
                    mostrarMensaje("\n");
                }
                mostrarMensaje("\n");

                List<Mano> misManos = controlador.getManosJugador();
                boolean tengoDividido = misManos.size() > 1;

                if (tengoDividido) {
                    mostrarResultadosManosDivididas(miNombre, puntajeCrupier, misManos);
                } else {
                    mostrarResultadoManoSimple(miNombre, puntajeCrupier);
                }

                mostrarMensaje("Saldo actual: $" + String.format("%.2f", miSaldo) + "\n\n");

                faseJuego = false;
                esperandoDecision = false;
                esmiTurno = false;
                controlador.solicitarVotacion();
            } catch (RemoteException e) {
            }
        });
    }

    /**
     * Muestra resultados de una mano simple
     */
    private void mostrarResultadoManoSimple(String nombre, int puntajeCrupier) throws RemoteException {

        mostrarMensaje(linea(30) + "\n");
        mostrarMensaje("||" + "      TUS MANOS (" + nombre + ")"      +" ||\n");
        mostrarMensaje(linea(30) + "\n");

        int miPuntaje = controlador.getPuntajeMano();
        List<Carta> misCartas = controlador.getCartasMano();

        for (Carta carta : misCartas) {
            mostrarMensaje("  • " + carta.getValor() + " de " + carta.getPalo() + "\n");
        }

        mostrarMensaje("\n  Puntaje: " + miPuntaje);

        if (miPuntaje == 21 && misCartas.size() == 2) {
            mostrarMensaje("\nBLACKJACK\n\n");
            mostrarMensaje(linea(50) + "\n");
            mostrarMensaje("\nBLACKJACK! GANASTE!!!\n");
            mostrarMensaje(linea(50) + "\n\n");
            return;
        }

        mostrarMensaje("\n\n");
        mostrarMensaje(linea(50) + "\n");

        if (miPuntaje > 21) {
            mostrarMensaje("PERDISTE - Te pasaste\n");
        } else if (puntajeCrupier > 21) {
            mostrarMensaje("GANASTE! - El crupier se pasó\n");
        } else if (miPuntaje > puntajeCrupier) {
            mostrarMensaje("GANASTE! - " + miPuntaje + " vs " + puntajeCrupier + "\n");
        } else if (miPuntaje < puntajeCrupier) {
            mostrarMensaje("PERDISTE - " + miPuntaje + " vs " + puntajeCrupier + "\n");
        } else {
            mostrarMensaje("EMPATE - Apuesta devuelta\n");
        }

        mostrarMensaje(linea(50) + "\n\n");
    }

    /**
     * Muestra resultados de manos divididas
     */
    private void mostrarResultadosManosDivididas(String nombre, int puntajeCrupier, List<Mano> manos) throws RemoteException {

        mostrarMensaje(linea(40) + "\n");
        mostrarMensaje("||" + "      TUS MANOS (" + nombre + ")"      +" ||\n");
        mostrarMensaje(linea(40) + "\n");

        mostrarMensaje("--- MANO 1 ---\n");
        int puntajeMano1 = controlador.getPuntajeManosIndices(0);
        List<Carta> cartasMano1 = manos.get(0).getMano();

        for (Carta carta : cartasMano1) {
            mostrarMensaje("  • " + carta.getValor() + " de " + carta.getPalo() + "\n");
        }
        mostrarMensaje("Puntaje: " + puntajeMano1 + "\n");
        mostrarMensaje("Resultado: " + evaluarMano(puntajeMano1, puntajeCrupier) + "\n\n");

        mostrarMensaje("--- MANO 2 ---\n");
        int puntajeMano2 = controlador.getPuntajeManosIndices(1);
        List<Carta> cartasMano2 = manos.get(1).getMano();

        for (Carta carta : cartasMano2) {
            mostrarMensaje("  • " + carta.getValor() + " de " + carta.getPalo() + "\n");
        }
        mostrarMensaje("Puntaje: " + puntajeMano2 + "\n");
        mostrarMensaje("Resultado: " + evaluarMano(puntajeMano2, puntajeCrupier) + "\n\n");
    }

    /**
     * Evalúa el resultado de una mano
     */
    private String evaluarMano(int puntajeJugador, int puntajeCrupier) {
        if (puntajeJugador > 21) {
            return "PERDISTE";
        } else if (puntajeCrupier > 21) {
            return "GANASTE!";
        } else if (puntajeJugador > puntajeCrupier) {
            return "GANASTE!";
        } else if (puntajeJugador < puntajeCrupier) {
            return "PERDISTE";
        } else {
            return "EMPATE";
        }
    }

    @Override
    public void mostrarVotacion() {
        SwingUtilities.invokeLater(() -> {
            try {
                if (votacionMostrada) return;

                enVotacion = true;
                esperandoDecision = true;
                votacionMostrada = true;

                mostrarMensaje("\n");
                mostrarMensaje(linea(50) + "\n");
                mostrarMensaje("QUERES JUGAR OTRA PARTIDA?\n");
                mostrarMensaje(linea(50) + "\n\n");

                float saldo = controlador.getSaldoJugadorActual();
                mostrarMensaje("Tu saldo actual: $" + String.format("%.2f", saldo) + "\n\n");

                if (saldo == 0) {
                    mostrarMensaje("️No tenes saldo suficiente.\n");
                    mostrarMensaje("Deberías votar NO o recargar saldo.\n\n");
                    mostrarMensaje("Podés recargar en la fase de APUESTAS!");
                }

                mostrarMensaje("\nEscribi tu voto (SI - NO):\n");
                mostrarMensaje("SI - Jugar otra partida\n");
                mostrarMensaje("NO - Salir de esta partida\n\n");
                mostrarMensaje("Voto: ");

            } catch (RemoteException e) {
            }
        });
    }

    @Override
    public void actualizarEstadoVotacion() {
        SwingUtilities.invokeLater(() -> {
            try {
                String estado = controlador.getEstadoVotacion();
                mostrarMensaje("\n[VOTACIÓN] " + estado + "\n");
            } catch (RemoteException e) {
            }
        });
    }

    @Override
    public void jugadorLlegoA21() {
        SwingUtilities.invokeLater(() -> {
            try {
                int puntaje = controlador.getPuntajeMano();

                mostrarMensaje("\nLlegaste a 21 puntos!\n");
                mostrarMensaje("Puntaje: " + puntaje + "\n");
                mostrarMensaje("Te plantas automáticamente.\n\n");

                List<Mano> manos = controlador.getManosJugador();
                boolean dividio = controlador.jugadorDividio();

                if (dividio && manos.size() > 1) {
                    mostrarMensaje("Mano 1 llegó a 21. Esperando mano 2...\n");
                } else {
                    mostrarMensaje("Esperando a los demás jugadores...\n");
                    esmiTurno = false;
                    esperandoDecision = false;
                    faseJuego = false;
                }

            } catch (RemoteException e) {
            }
        });
    }

    @Override
    public void jugadorSePaso() {
        SwingUtilities.invokeLater(() -> {
            try {
                int puntaje = controlador.getPuntajeMano();

                mostrarMensaje("\nTe pasaste de 21!\n");
                mostrarMensaje("Puntaje final: " + puntaje + "\n");
                mostrarMensaje("Perdiste esta mano :(\n\n");

                List<Mano> manos = controlador.getManosJugador();
                boolean dividio = controlador.jugadorDividio();

                if (dividio && manos.size() > 1) {
                    mostrarMensaje("Mano 1 se pasó. Esperando mano 2...\n");

                } else {
                    mostrarMensaje("Esperando a los demás jugadores...\n");

                    esmiTurno = false;
                    esperandoDecision = false;
                    faseJuego = false;
                }

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void cambiarAMano2() {
        SwingUtilities.invokeLater(() -> {
            try {
                esmiTurno = true;
                faseJuego = true;
                esperandoDecision = true;

                mostrarMensaje("\n==================================================\n");
                mostrarMensaje("       Ahora juega la MANO 2\n");
                mostrarMensaje("==================================================\n\n");

                mostrarManosDivididasJugadorVista();

                mostrarMensaje("PEDIR | PLANTAR | DOBLAR | DIVIDIR | AYUDA\n");
                mostrarMensaje("Decisión: ");

            } catch (RemoteException e) {
            }
        });
    }

}