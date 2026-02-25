package ar.edu.unlu.blackjack.Vista.interfazGrafica;

import ar.edu.unlu.blackjack.Modelo.Carta;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

public class panelMesa extends JPanel {

    private final int ANCHO_CARTA = 100;
    private final int ALTO_CARTA = 140;
    private final int SEPARACION_CARTAS = 110;

    private static final String RUTA_CARTAS = "src/ar/edu/unlu/blackjack/cartas_files/";
    private static final String RUTA_REVERSO = "src/ar/edu/unlu/blackjack/cartas_files/time702-copy-6_51163893512_l.jpg";

    private List<Carta> cartasJugador;
    private List<Carta> cartasCrupier;
    private List<Carta> cartasMano2;
    private boolean crupierCartaOculta;

    public panelMesa(List<Carta> cartasJugador, List<Carta> cartasCrupier, List<Carta> cartasMano2, boolean crupierCartaOculta) {
        this.cartasJugador = cartasJugador;
        this.cartasCrupier = cartasCrupier;
        this.cartasMano2 = cartasMano2;
        this.crupierCartaOculta = crupierCartaOculta;
        setBackground(new Color(0, 120, 0));
        setPreferredSize(new Dimension(1100, 600));
    }

    public void setCrupierCartaOculta(boolean b) {
        this.crupierCartaOculta = b;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        int ovalX = 50, ovalY = 30;
        int ovalWidth = width - 100, ovalHeight = height - 60;

        g2d.setColor(new Color(0, 80, 0));
        g2d.fillOval(ovalX + 5, ovalY + 5, ovalWidth, ovalHeight);

        g2d.setColor(new Color(0, 100, 0));
        g2d.fillOval(ovalX, ovalY, ovalWidth, ovalHeight);

        g2d.setColor(new Color(218, 165, 32));
        g2d.setStroke(new BasicStroke(5));
        g2d.drawOval(ovalX, ovalY, ovalWidth, ovalHeight);

        g2d.setColor(new Color(218, 165, 32, 100));
        g2d.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 60));
        String texto = "BLACKJACK";
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(texto, (width - fm.stringWidth(texto)) / 2, height / 2 + 30);

        dibujarCartasCrupier(g2d, width);
        dibujarCartasJugador(g2d, width, height);
        if (cartasMano2 != null && !cartasMano2.isEmpty()) dibujarMano2(g2d, width, height);
    }

    private void dibujarCartasCrupier(Graphics2D g2d, int width) {
        if (cartasCrupier == null || cartasCrupier.isEmpty()) return;

        int y = 80;
        int xInicial = (width - (cartasCrupier.size() * SEPARACION_CARTAS)) / 2;

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString("CRUPIER", xInicial, y - 15);

        for (int i = 0; i < cartasCrupier.size(); i++) {
            Carta carta = cartasCrupier.get(i);
            int x = xInicial + (i * SEPARACION_CARTAS);

            // PRIMERA carta siempre visible, SEGUNDA oculta durante la partida
            boolean oculta = (i > 0 && crupierCartaOculta);
            dibujarCarta(g2d, carta, x, y, oculta);
        }
    }

    private void dibujarCartasJugador(Graphics2D g2d, int width, int height) {
        if (cartasJugador == null || cartasJugador.isEmpty()) return;

        int y = height - 230;
        int xInicial = (width - (cartasJugador.size() * SEPARACION_CARTAS)) / 2;

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString("TUS CARTAS", xInicial, y - 15);

        for (int i = 0; i < cartasJugador.size(); i++) {
            int x = xInicial + (i * SEPARACION_CARTAS);
            dibujarCarta(g2d, cartasJugador.get(i), x, y, false);
        }
    }

    private void dibujarMano2(Graphics2D g2d, int width, int height) {
        int y = height - 400;
        int xInicial = (width - (cartasMano2.size() * SEPARACION_CARTAS)) / 2;

        g2d.setColor(Color.YELLOW);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString("MANO 2", xInicial, y - 15);

        for (int i = 0; i < cartasMano2.size(); i++) {
            int x = xInicial + (i * SEPARACION_CARTAS);
            dibujarCarta(g2d, cartasMano2.get(i), x, y, false);
        }
    }

    private void dibujarCarta(Graphics2D g2d, Carta carta, int x, int y, boolean oculta) {
        BufferedImage imagen = oculta ? cargarImagenReverso() : cargarImagenCarta(carta.getValor(), carta.getPalo());

        if (imagen != null) {
            g2d.drawImage(imagen, x, y, ANCHO_CARTA, ALTO_CARTA, null);
        } else {
            if (oculta) dibujarCartaOcultaFallback(g2d, x, y);
            else dibujarCartaVisibleFallback(g2d, carta, x, y);
        }
    }

    private void dibujarCartaVisibleFallback(Graphics2D g2d, Carta carta, int x, int y) {
        g2d.setColor(new Color(0, 0, 0, 80));
        g2d.fillRoundRect(x + 3, y + 3, ANCHO_CARTA, ALTO_CARTA, 10, 10);

        g2d.setColor(Color.WHITE);
        g2d.fillRoundRect(x, y, ANCHO_CARTA, ALTO_CARTA, 10, 10);

        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(x, y, ANCHO_CARTA, ALTO_CARTA, 10, 10);

        String palo = carta.getPalo();
        Color colorPalo = (palo.contains("Corazon") || palo.contains("Diamante")) ? Color.RED : Color.BLACK;
        g2d.setColor(colorPalo);

        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.drawString(carta.getValor(), x + 10, y + 30);

        String simbolo = obtenerSimbolo(palo);
        g2d.setFont(new Font("Arial", Font.BOLD, 50));
        FontMetrics fm = g2d.getFontMetrics();
        int xCentro = x + (ANCHO_CARTA - fm.stringWidth(simbolo)) / 2;
        int yCentro = y + (ALTO_CARTA + fm.getAscent()) / 2 - 10;
        g2d.drawString(simbolo, xCentro, yCentro);
    }

    private void dibujarCartaOcultaFallback(Graphics2D g2d, int x, int y) {
        g2d.setColor(new Color(0, 0, 0, 80));
        g2d.fillRoundRect(x + 3, y + 3, ANCHO_CARTA, ALTO_CARTA, 10, 10);

        g2d.setColor(new Color(30, 60, 140));
        g2d.fillRoundRect(x, y, ANCHO_CARTA, ALTO_CARTA, 10, 10);

        g2d.setColor(new Color(50, 80, 180));
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 10; j++) {
                g2d.fillOval(x + i * 14 + 8, y + j * 14 + 8, 8, 8);
            }
        }

        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(x, y, ANCHO_CARTA, ALTO_CARTA, 10, 10);
    }

    private String obtenerSimbolo(String palo) {
        if (palo.contains("Corazon")) return "♥";
        if (palo.contains("Diamante")) return "♦";
        if (palo.contains("Trébol") || palo.contains("Trebol")) return "♣";
        if (palo.contains("Pica")) return "♠";
        return "?";
    }

    private BufferedImage cargarImagenCarta(String valor, String palo) {
        try {
            String nombreArchivo = valor + "_" + normalizarPalo(palo) + ".jpg";
            String ruta = RUTA_CARTAS + nombreArchivo;
            File archivo = new File(ruta);

            if (archivo.exists()) {
                return ImageIO.read(archivo);
            }
        } catch (Exception e) {
        }
        return null;
    }

    private String normalizarPalo(String palo) {
        palo = palo.toLowerCase();
        if (palo.contains("corazon")) return "corazones";
        if (palo.contains("diamante")) return "diamantes";
        if (palo.contains("trebol") || palo.contains("trébol")) return "treboles";
        if (palo.contains("pica")) return "picas";
        return palo;
    }

    private BufferedImage cargarImagenReverso() {
        try {
            File archivo = new File(RUTA_REVERSO);
            if (archivo.exists()) {
                return ImageIO.read(archivo);
            }
        } catch (Exception e) {
        }
        return null;
    }
}