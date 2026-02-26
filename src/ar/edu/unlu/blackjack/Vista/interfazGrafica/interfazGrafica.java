package ar.edu.unlu.blackjack.Vista.interfazGrafica;

import ar.edu.unlu.blackjack.Controlador.Controlador;
import ar.edu.unlu.blackjack.Modelo.Carta;
import ar.edu.unlu.blackjack.Modelo.Mano;
import ar.edu.unlu.blackjack.Vista.IVista;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class interfazGrafica extends JFrame implements IVista {

    private Controlador controlador;

    private JPanel panelPrincipal;
    private panelMesa panelMesa;

    private JButton btnPedir;
    private JButton btnPlantar;
    private JButton btnDoblar;
    private JButton btnDividir;
    private JButton btnComenzar;
    private JButton btnRecargarSaldo;
    private JButton btnRanking;
    private JButton btnApostar;

    private JLabel lblNickname;
    private JLabel lblSaldo;
    private JLabel lblApuesta;
    private JLabel lblPuntajeJugador;
    private JLabel lblPuntajeCrupier;
    private JLabel lblEstado;

    // Estados
    private boolean enSalaEspera;
    private boolean misDatosConfigurados;
    private boolean faseApuestas;
    private boolean faseJuego;
    private boolean esmiTurno;
    private boolean esperandoDecision;

    private String nicknameTemporal;
    private float saldoTemporal;
    private boolean votacionMostrada;
    private boolean yaAposte;

    private List<Carta> cartasJugador;
    private List<Carta> cartasCrupier;
    private List<Carta> cartasMano2;
    private boolean crupierCartaOculta;

    public interfazGrafica() {
        super("Interfaz Gráfica - Blackjack :: Agustín Weisbek");
        inicializarEstados();
        inicializarComponentes();
        configurarVentana();
        configurarListeners();
    }

    private void inicializarEstados() {
        enSalaEspera = true;
        misDatosConfigurados = false;
        faseApuestas = false;
        faseJuego = false;
        esmiTurno = false;
        esperandoDecision = false;

        votacionMostrada = false;
        yaAposte = false;

        cartasJugador = new ArrayList<>();
        cartasCrupier = new ArrayList<>();
        cartasMano2 = new ArrayList<>();
        crupierCartaOculta = true;
    }

    private void inicializarComponentes() {
        panelPrincipal = new JPanel(new BorderLayout(0, 0));
        panelPrincipal.setBackground(new Color(0, 100, 0));

        panelMesa = new panelMesa(cartasJugador, cartasCrupier, cartasMano2, crupierCartaOculta);
        panelPrincipal.add(panelMesa, BorderLayout.CENTER);

        panelPrincipal.add(crearPanelInfo(), BorderLayout.NORTH);

        panelPrincipal.add(crearPanelBotones(), BorderLayout.SOUTH);

        setContentPane(panelPrincipal);
    }

    private JPanel crearPanelInfo() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 15, 5));
        panel.setBackground(new Color(0, 80, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        lblNickname = crearLabel("Jugador: -", 16, true);
        lblSaldo = crearLabel("Saldo: $0", 16, true);
        lblApuesta = crearLabel("Apuesta: $0", 16, true);
        lblPuntajeJugador = crearLabel("Tu Puntaje: 0", 16, true);
        lblPuntajeCrupier = crearLabel("Crupier: ?", 16, true);
        lblEstado = crearLabel("Configurando...", 16, false);
        lblEstado.setForeground(Color.YELLOW);

        panel.add(lblNickname);
        panel.add(lblSaldo);
        panel.add(lblApuesta);
        panel.add(lblPuntajeJugador);
        panel.add(lblPuntajeCrupier);
        panel.add(lblEstado);

        return panel;
    }

    private JLabel crearLabel(String texto, int tamanio, boolean negrita) {
        JLabel label = new JLabel(texto, SwingConstants.CENTER);
        label.setFont(new Font("Arial", negrita ? Font.BOLD : Font.PLAIN, tamanio));
        label.setForeground(Color.WHITE);
        return label;
    }

    private JPanel crearPanelBotones() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(new Color(0, 80, 0));

        // Panel para botones de juego
        JPanel panelJuego = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 15));
        panelJuego.setBackground(new Color(0, 80, 0));
        panelJuego.setBorder(BorderFactory.createEmptyBorder(10, 20, 5, 20));

        btnComenzar = crearBoton("COMENZAR", new Color(34, 139, 34));
        btnComenzar.setPreferredSize(new Dimension(200, 60));
        btnComenzar.setFont(new Font("Arial", Font.BOLD, 20));
        btnComenzar.setVisible(false);

        btnPedir = crearBoton("PEDIR", new Color(70, 130, 180));
        btnPlantar = crearBoton("PLANTAR", new Color(220, 20, 60));
        btnDoblar = crearBoton("DOBLAR", new Color(255, 140, 0));
        btnDividir = crearBoton("DIVIDIR", new Color(138, 43, 226));

        panelJuego.add(btnComenzar);
        panelJuego.add(btnPedir);
        panelJuego.add(btnPlantar);
        panelJuego.add(btnDoblar);
        panelJuego.add(btnDividir);

        // Panel para botón de recarga y apostar
        JPanel panelRecarga = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        panelRecarga.setBackground(new Color(0, 80, 0));
        panelRecarga.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));

        btnRanking = crearBoton("RANKING JUGADORES", new Color(184, 134, 11));
        btnRanking.setPreferredSize(new Dimension(200, 45));
        btnRanking.setFont(new Font("Arial", Font.BOLD, 16));

        btnRecargarSaldo = crearBoton("RECARGAR SALDO", new Color(46, 125, 50));
        btnRecargarSaldo.setPreferredSize(new Dimension(200, 45));
        btnRecargarSaldo.setFont(new Font("Arial", Font.BOLD, 16));

        btnApostar = crearBoton("APOSTAR", new Color(21, 21, 21));
        btnApostar.setPreferredSize(new Dimension(200, 45));
        btnApostar.setFont(new Font("Arial", Font.BOLD, 16));
        btnApostar.setEnabled(false);

        panelRecarga.add(btnRanking);
        panelRecarga.add(btnRecargarSaldo);
        panelRecarga.add(btnApostar);

        // ambos paneles
        panelPrincipal.add(panelJuego, BorderLayout.CENTER);
        panelPrincipal.add(panelRecarga, BorderLayout.SOUTH);

        deshabilitarBotonesJuego();

        return panelPrincipal;
    }

    private JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setPreferredSize(new Dimension(160, 55));
        boton.setFont(new Font("Arial", Font.BOLD, 18));
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createRaisedBevelBorder());
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Color colorOriginal = color;
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (boton.isEnabled()) boton.setBackground(colorOriginal.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(colorOriginal);
            }
        });

        return boton;
    }

    private void configurarVentana() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(870,705);
        setLocationRelativeTo(null);
        setResizable(true);
    }

    private void configurarListeners() {
        btnComenzar.addActionListener(e -> accionComenzar());
        btnPedir.addActionListener(e -> accionPedir());
        btnPlantar.addActionListener(e -> accionPlantar());
        btnDoblar.addActionListener(e -> accionDoblar());
        btnDividir.addActionListener(e -> accionDividir());
        btnRanking.addActionListener(e -> accionVerRanking());
        btnRecargarSaldo.addActionListener(e -> accionRecargarSaldo());
        btnApostar.addActionListener(e -> accionApostar());
    }

    // ACCIONES

    private void accionComenzar() {
        if (!enSalaEspera || !misDatosConfigurados) return;

        try {
            actualizarEstado("Intentando comenzar...");

            if (!controlador.intentarComenzarPartida()) {
                int listos = controlador.getCantidadJugadoresListos();
                int total = controlador.getCantidadJugadoresConectados();
                actualizarEstado("Esperando: " + listos + "/" + total);
            } else {
                btnComenzar.setEnabled(false);
                actualizarEstado("Iniciando...");
            }

        } catch (RemoteException e) {
            mostrarError("Error: " + e.getMessage());
        }
    }

    private void accionPedir() {
        if (!esmiTurno || !esperandoDecision) return;

        try {
            controlador.pedirCarta();
            actualizarCartas();
        } catch (RemoteException e) {
            mostrarError("Error al pedir carta");
        }
    }

    private void accionPlantar() {
        if (!esmiTurno || !esperandoDecision) return;

        try {
            controlador.plantarse();
            deshabilitarBotonesJuego();
            actualizarEstado("Te plantaste");
        } catch (RemoteException e) {
            mostrarError("Error al plantarse");
        }
    }

    private void accionDoblar() {
        if (!esmiTurno || !esperandoDecision) return;

        try {
            if (controlador.getManosJugador().size() == 2) {
                int manoActual = controlador.manoAUsar();
                if (controlador.getManosJugador().get(manoActual).getMano().size() != 2) {
                    mostrarMensaje("Solo puedes doblar con 2 cartas");
                    return;
                }
            } else if (controlador.getCartasMano().size() != 2) {
                mostrarMensaje("Solo puedes doblar con 2 cartas");
                return;
            }

            float apuesta;
            int manoActual = controlador.manoAUsar();

            if (controlador.jugadorDividio() && manoActual == 1) {
                apuesta = controlador.getApuestaJugadorMano2();
            } else {
                apuesta = controlador.getApuestaJugador();
            }

            float saldo = controlador.getSaldoJugadorActual();

            if (saldo < apuesta) {
                mostrarMensaje("Saldo insuficiente. Necesitas: $" + String.format("%.0f", apuesta));
                return;
            }

            controlador.jugadorDobloMano();
            Thread.sleep(100);
            controlador.recibirCartaJugador();
            actualizarCartas();
            actualizarInfo();

            if (!controlador.jugadorDividio() || controlador.getManosJugador().size() <= 1) {
                esmiTurno = false;
                esperandoDecision = false;
            }

            controlador.plantarse();

        } catch (Exception e) {
            mostrarError("Error al doblar");
        }
    }

    private void accionDividir() {
        if (!esmiTurno || !esperandoDecision) return;

        try {
            if (!controlador.getJugadorPuedeDividir()) {
                mostrarMensaje("No puedes dividir esta mano");
                return;
            }

            float saldo = controlador.getSaldoJugadorActual();
            float apuesta = controlador.getApuestaJugador();

            if (saldo < apuesta) {
                mostrarMensaje("Saldo insuficiente para dividir");
                return;
            }

            controlador.dividirMano();
            controlador.setJugadorDividio(true);
            actualizarCartas();
            actualizarEstado("Mano dividida - MANO 1");

        } catch (RemoteException e) {
            mostrarError("Error al dividir");
        }
    }

    private void accionVerRanking() {
        try {
            String rankingRaw = controlador.getRankingFormateado();

            if (rankingRaw == null || rankingRaw.isBlank()) {
                mostrarMensaje("Todavía no hay jugadores en el ranking.");
                return;
            }

            // Parsear el String a filas para la JTable
            String[] filas = rankingRaw.trim().split("\n");
            String[] columnas = {"#", "Jugador", "Partidas Ganadas", "Plata Total Ganada"};
            Object[][] datos = new Object[filas.length][4];
            for (int i = 0; i < filas.length; i++) {
                String[] partes = filas[i].split(";");
                datos[i][0] = partes[0];
                datos[i][1] = partes[1];
                datos[i][2] = partes[2];
                datos[i][3] = "$" + partes[3];
            }

            // Tabla
            JTable tabla = new javax.swing.JTable(datos, columnas) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            tabla.setFont(new Font("Arial", Font.PLAIN, 14));
            tabla.setRowHeight(28);
            tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
            tabla.setBackground(new Color(245, 245, 245));
            tabla.getTableHeader().setBackground(new Color(0, 80, 0));
            tabla.getTableHeader().setForeground(Color.WHITE);
            tabla.setSelectionBackground(new Color(200, 230, 200));

            // Centrar columnas
            DefaultTableCellRenderer centrado = new javax.swing.table.DefaultTableCellRenderer();
            centrado.setHorizontalAlignment(SwingConstants.CENTER);
            for (int i = 0; i < columnas.length; i++) tabla.getColumnModel().getColumn(i).setCellRenderer(centrado);

            // Ancho de columnas
            tabla.getColumnModel().getColumn(0).setPreferredWidth(30);
            tabla.getColumnModel().getColumn(1).setPreferredWidth(150);
            tabla.getColumnModel().getColumn(2).setPreferredWidth(130);
            tabla.getColumnModel().getColumn(3).setPreferredWidth(160);

            JScrollPane scroll = new JScrollPane(tabla);
            scroll.setPreferredSize(new Dimension(510, Math.min(filas.length * 28 + 60, 400)));

            JOptionPane.showMessageDialog(
                    this,
                    scroll,
                    "Ranking de Jugadores",
                    JOptionPane.PLAIN_MESSAGE
            );

        } catch (RemoteException e) {
            mostrarError("Error al obtener el ranking: " + e.getMessage());
        }
    }

    private void accionRecargarSaldo() {
        try {
            float saldoActual = controlador.getSaldoJugadorActual();

            String montoStr = JOptionPane.showInputDialog(this, "Saldo actual: $" + String.format("%.2f", saldoActual) + "\n\nIngresa el monto a recargar:", "Recargar Saldo", JOptionPane.QUESTION_MESSAGE);

            if (montoStr == null || montoStr.trim().isEmpty()) {
                return; // el usuario apreto en cancelar
            }

            float monto = Float.parseFloat(montoStr);

            if (monto <= 0) {
                mostrarError("El monto debe ser mayor a 0");
                return;
            }

            boolean exito = controlador.recargarSaldo(monto);

            if (exito) {
                float saldoDespues = controlador.getSaldoJugadorActual();

                lblSaldo.setText("Saldo: $" + String.format("%.2f", saldoDespues));

                String mensaje = "Recarga exitosa!\n\n";

                mostrarMensaje(mensaje);
                actualizarEstado("Saldo recargado: +$" + String.format("%.0f", monto));

            } else {
                mostrarError("Error al recargar saldo");
            }

        } catch (NumberFormatException e) {
            mostrarError("Ingresa un monto válido");
        } catch (RemoteException e) {
            mostrarError("Error de conexión al recargar saldo");
        }
    }

    private void accionApostar() {
        if (!esmiTurno || !faseApuestas || yaAposte) {
            // si no es mi turno ni estamos en fase de apuestas y si ya aposte, no hago nada
            return;
        }
        try {
            float saldoActual = controlador.getSaldoJugadorActual();
            if (saldoActual <= 0) {
                mostrarError("No tenes saldo suficiente!");
                return;
            }

            String apuestaStr = JOptionPane.showInputDialog(this, "Saldo: $" + String.format("%.0f", saldoActual) + "\n\nIngresa tu apuesta:", "Tu Turno - Apuesta", JOptionPane.QUESTION_MESSAGE);

            if (apuestaStr == null || apuestaStr.trim().isEmpty()) {
                return; // el usuario cancelo la operacion
            }

            float monto = Float.parseFloat(apuestaStr);
            if (monto <= 0) {
                mostrarError("El monto debe ser mayor a 0");
                return;
            }

            if (monto > saldoActual) {
                mostrarError("Saldo insuficiente. Tu saldo: $" + String.format("%.0f", saldoActual));
                return;
            }
            boolean apuestaExitosa = controlador.cargarApuestaJugador(String.valueOf(monto));

            if (apuestaExitosa) {
                lblApuesta.setText("Apuesta: $" + String.format("%.2f", monto));
                lblSaldo.setText("Saldo: $" + String.format("%.2f", controlador.getSaldoJugadorActual()));

                // Marcar que ya aposté y deshabilitar botón
                yaAposte = true;
                esmiTurno = false;
                faseApuestas = false;
                btnApostar.setEnabled(false);

                int totalJugadores = controlador.getCantidadJugadoresConectados();
                if (totalJugadores > 1) {
                    actualizarEstado("Esperando otros jugadores...");
                } else {
                    actualizarEstado("Preparando mano...");
                }
            } else {
                mostrarError("Error al procesar apuesta");
            }

        } catch (NumberFormatException e) {
            mostrarError("Ingresa un número válido");
        } catch (RemoteException e) {
            mostrarError("Error de conexión al apostar");
        }
    }

    // ==================== ACTUALIZACIÓN ====================

    private void actualizarCartas() {
        SwingUtilities.invokeLater(() -> {
            try {
                cartasJugador.clear();
                List<Carta> cartas = controlador.getCartasMano();
                cartasJugador.addAll(cartas);

                cartasCrupier.clear();
                List<Carta> cartasCrup = controlador.getManoCrupier();
                cartasCrupier.addAll(cartasCrup);

                cartasMano2.clear();
                if (controlador.jugadorDividio() && controlador.getManosJugador().size() > 1) {
                    List<Mano> manos = controlador.getManosJugador();
                    cartasMano2.addAll(manos.get(1).getMano());
                }

                actualizarPuntajes();
                panelMesa.repaint();

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }

    private void actualizarPuntajes() {
        try {
            int puntaje = controlador.getPuntajeMano();
            lblPuntajeJugador.setText("Tu Puntaje: " + puntaje);

            if (!crupierCartaOculta) {
                int puntajeCrupier = controlador.getPuntajeCrupier();
                lblPuntajeCrupier.setText("Crupier: " + puntajeCrupier);
            } else {
                lblPuntajeCrupier.setText("Crupier: ?");
            }
        } catch (RemoteException e) {
            lblPuntajeJugador.setText("Tu Puntaje: ?");
        }
    }

    private void actualizarInfo() {
        SwingUtilities.invokeLater(() -> {
            try {
                lblSaldo.setText("Saldo: $" + String.format("%.2f", controlador.getSaldoJugadorActual()));
                lblApuesta.setText("Apuesta: $" + String.format("%.2f", controlador.getApuestaJugador()));
                actualizarPuntajes();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }

    private void actualizarEstado(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            lblEstado.setText(mensaje);
        });
    }

    private void habilitarBotonesJuego() {
        btnPedir.setEnabled(true);
        btnPlantar.setEnabled(true);
        btnDoblar.setEnabled(true);
        btnDividir.setEnabled(true);

        btnPedir.setVisible(true);
        btnPlantar.setVisible(true);
        btnDoblar.setVisible(true);
        btnDividir.setVisible(true);
    }

    private void deshabilitarBotonesJuego() {
        btnPedir.setEnabled(false);
        btnPlantar.setEnabled(false);
        btnDoblar.setEnabled(false);
        btnDividir.setEnabled(false);
    }

    private void ocultarBotonesJuego() {
        btnPedir.setVisible(false);
        btnPlantar.setVisible(false);
        btnDoblar.setVisible(false);
        btnDividir.setVisible(false);
    }
    @Override
    public void setControlador(Controlador controlador) {
        this.controlador = controlador;
    }

    @Override
    public void iniciarJuego() {
        SwingUtilities.invokeLater(() -> {
            setVisible(true);
            mostrarDialogoConfiguracion();
        });
    }

    private void mostrarDialogoConfiguracion() {
        String nickname = JOptionPane.showInputDialog(this, "Ingresa tu nickname:", "Configuración Inicial", JOptionPane.QUESTION_MESSAGE);

        if (nickname == null || nickname.trim().isEmpty()) {
            System.exit(0);
            return;
        }

        if (nickname.matches("\\d+")) {
            mostrarError("El nickname no puede ser solo números");
            mostrarDialogoConfiguracion();
            return;
        }

        nicknameTemporal = nickname;
        lblNickname.setText("Jugador: " + nickname);

        String saldoStr = JOptionPane.showInputDialog(this, "Ingresa tu saldo inicial:", "Configuración Inicial", JOptionPane.QUESTION_MESSAGE);

        try {
            float saldo = Float.parseFloat(saldoStr);

            if (saldo <= 0) {
                mostrarError("El saldo debe ser mayor a 0");
                mostrarDialogoConfiguracion();
                return;
            }

            saldoTemporal = saldo;
            lblSaldo.setText("Saldo: $" + String.format("%.2f", saldo));

            controlador.configurarJugadores(nicknameTemporal, saldoTemporal);

            misDatosConfigurados = true;

            btnComenzar.setVisible(true);
            btnComenzar.setEnabled(true);
            ocultarBotonesJuego();

            actualizarEstado("LISTO - Apreta COMENZAR");

        } catch (NumberFormatException e) {
            mostrarError("Saldo inválido");
            mostrarDialogoConfiguracion();
        } catch (RemoteException e) {
            mostrarError("Error: " + e.getMessage());
            mostrarDialogoConfiguracion();
        }
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        SwingUtilities.invokeLater(() -> actualizarEstado(mensaje));
    }

    @Override
    public void mostrarCartasJugador(){
        actualizarCartas();
        actualizarInfo();
    }

    @Override
    public void mostrarManosDivididasJugadorVista() {
        actualizarCartas();
    }

    @Override
    public void mostrarPuntuacionParcialCrupier() {
        actualizarPuntajes();
    }

    @Override
    public void cicloPartida() throws RemoteException {
        SwingUtilities.invokeLater(() -> {
            faseJuego = true;
            faseApuestas = false;
            yaAposte = false;
            crupierCartaOculta = true;
            panelMesa.setCrupierCartaOculta(true);

            actualizarCartas();
            actualizarInfo();
            actualizarEstado("Ronda iniciada - Esperando turno...");
        });
    }

    @Override
    public void notificarTurnoJugador() {
        SwingUtilities.invokeLater(() -> {
            esmiTurno = true;

            if (!faseJuego) {
                //Si ya aposté, ignorar
                if (yaAposte) {
                    actualizarEstado("Esperando otros jugadores...");
                    return;
                }

                faseApuestas = true;

                btnApostar.setEnabled(true);
                actualizarEstado("ES TU TURNO - Presiona APOSTAR");

            } else {
                // fase de juego
                esperandoDecision = true;
                habilitarBotonesJuego();
                actualizarCartas();
                actualizarInfo();
                actualizarEstado("ES TU TURNO");
            }
        });
    }

    @Override
    public void mostrarManoJugador(){
        actualizarCartas();
        actualizarInfo();
    }

    @Override
    public void mostrarPuntuacionParcial(){
        actualizarPuntajes();
    }

    @Override
    public void mostrarSalaEspera(List<String> jugadores, int maximo) {
        SwingUtilities.invokeLater(() -> {
            enSalaEspera = true;
            StringBuilder sb = new StringBuilder("Sala: ");
            sb.append(jugadores.size());
            if (maximo > 0) sb.append("/").append(maximo);
            actualizarEstado(sb.toString());
        });
    }

    @Override
    public void comenzarPartida() {
        SwingUtilities.invokeLater(() -> {
            // guardo el estado del boton apostar
            boolean apostarEstabaHabilitado = btnApostar.isEnabled();
            boolean esMiTurnoActual = esmiTurno;

            enSalaEspera = false;
            faseJuego = false;
            votacionMostrada = false;

            // Solo resetear yaAposte si NO es mi turno
            if (!esMiTurnoActual) {
                yaAposte = false;
                faseApuestas = true;
                esmiTurno = false;
            } else {
                // Es mi turno, mantener estados
                faseApuestas = true;
            }

            btnComenzar.setVisible(false);
            btnComenzar.setEnabled(false);

            // mantener estado del botón APOSTAR si ya estaba habilitado
            if (apostarEstabaHabilitado && esMiTurnoActual) {
                btnApostar.setEnabled(true);
            } else {
                btnApostar.setEnabled(false);
            }

            btnPedir.setVisible(true);
            btnPlantar.setVisible(true);
            btnDoblar.setVisible(true);
            btnDividir.setVisible(true);
            deshabilitarBotonesJuego();

            if (apostarEstabaHabilitado && esMiTurnoActual) {
                actualizarEstado("ES TU TURNO - Presiona APOSTAR");
            } else {
                actualizarEstado("¡PARTIDA INICIADA!");
            }
        });
    }

    @Override
    public void notificarTurnoApuesta() {
        notificarTurnoJugador();
    }

    @Override
    public void mostrarResultados() {
        SwingUtilities.invokeLater(() -> {
            faseJuego = false;
            crupierCartaOculta = false;
            panelMesa.setCrupierCartaOculta(false); // flag que permite mostrar todas las cartas del crupier
            deshabilitarBotonesJuego();
            actualizarCartas();
            actualizarInfo();
            actualizarEstado("Partida finalizada");

            votacionMostrada = false; // resetear para que mostrarVotacion() ser llamado
            Timer timer = new Timer(2000, e -> {
                try {
                    controlador.solicitarVotacion();
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            });
            timer.setRepeats(false);
            timer.start();
        });
    }

    @Override
    public void mostrarVotacion() {
        SwingUtilities.invokeLater(() -> {
            if (votacionMostrada) return;

            esperandoDecision = true;
            votacionMostrada = true;

            try {
                float saldo = controlador.getSaldoJugadorActual();
                String mensaje = "Queres jugar otra partida?\n\nSaldo: $" +
                        String.format("%.2f", saldo);

                if (saldo == 0) {
                    mensaje += "\n\nSin saldo!";
                }

                Object[] opciones = {"SÍ", "NO"};
                int respuesta = JOptionPane.showOptionDialog(this,
                        mensaje,
                        "Votación",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null, opciones, opciones[0]);

                if (respuesta == JOptionPane.YES_OPTION) {
                    controlador.votarSi();
                    actualizarEstado("Votaste SÍ");
                } else {
                    controlador.votarNo();
                    actualizarEstado("Votaste NO");
                }

            } catch (RemoteException e) {
                mostrarError("Error al votar");
            }
        });
    }

    @Override
    public void actualizarEstadoVotacion() {
        SwingUtilities.invokeLater(() -> {
            try {
                String estado = controlador.getEstadoVotacion();
                actualizarEstado(estado);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void jugadorLlegoA21() {
        SwingUtilities.invokeLater(() -> {
            deshabilitarBotonesJuego();
            actualizarEstado("¡21!");
        });
    }

    @Override
    public void jugadorSePaso() {
        SwingUtilities.invokeLater(() -> {
            deshabilitarBotonesJuego();
            actualizarEstado("Te pasaste");
        });
    }

    @Override
    public void cambiarAMano2() {
        SwingUtilities.invokeLater(() -> {
            esmiTurno = true;
            faseJuego = true;
            esperandoDecision = true;
            habilitarBotonesJuego();
            actualizarEstado("MANO 2");
            actualizarCartas();
        });
    }

    @Override
    public void ofrecerSeguro() {
        SwingUtilities.invokeLater(() -> {
            try {
                float apuesta = controlador.getApuestaJugador();
                float montoSeguro = apuesta / 2f;
                float saldo = controlador.getSaldoJugadorActual();

                // Armar el mensaje informativo
                String mensaje;
                boolean puedePagar = saldo >= montoSeguro;

                if (puedePagar) {
                    mensaje = "El crupier tiene un AS!\n\n" +
                            "Podés pagar un SEGURO por: $" + String.format("%.2f", montoSeguro) +
                            "\n\nTu saldo actual: $" + String.format("%.2f", saldo);
                } else {
                    mensaje = "El crupier tiene un AS!\n\n" + "No tenés saldo suficiente para pagar el seguro.\n" + "(el seguro es la mitad de la apuesta)";
                }

                if (puedePagar) {
                    Object[] opciones = {"PAGAR SEGURO ($" + String.format("%.2f", montoSeguro) + ")", "NO PAGAR"};
                    int respuesta = JOptionPane.showOptionDialog(
                            this,
                            mensaje,
                            "Oferta de Seguro",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE,
                            null,
                            opciones,
                            opciones[1]
                    );

                    if (respuesta == JOptionPane.YES_OPTION) {
                        boolean exito = controlador.aceptarSeguro();
                        if (exito) {
                            actualizarEstado("Seguro contratado - $" + String.format("%.2f", montoSeguro));
                            actualizarInfo(); // refrescar saldo en pantalla
                        } else {
                            mostrarError("No se pudo procesar el seguro (saldo insuficiente).");
                            controlador.rechazarSeguro();
                        }
                    } else {
                        controlador.rechazarSeguro();
                        actualizarEstado("Seguro rechazado");
                    }
                } else {
                    // No puede pagar
                    JOptionPane.showMessageDialog(this, mensaje, "Oferta de Seguro", JOptionPane.INFORMATION_MESSAGE);
                    controlador.rechazarSeguro();
                    actualizarEstado("Sin saldo para el seguro");
                }

            } catch (RemoteException e) {
                mostrarError("Error al procesar la oferta de seguro: " + e.getMessage());
                // Intentamos registrar el rechazo para no bloquear la partida
                try {
                    controlador.rechazarSeguro();
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    // ==================== AUXILIARES ====================

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Mensaje de Error", JOptionPane.ERROR_MESSAGE);
    }

}