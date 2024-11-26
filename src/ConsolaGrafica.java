import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConsolaGrafica extends JFrame {
    private JTextArea consola;        // Área de texto para la consola
    private JTextField entradaTexto;  // Campo de entrada de texto
    private JButton botonEnviar;      // Botón para enviar el texto

    public ConsolaGrafica() {
        // Configuración del JFrame
        setTitle("BLACKJACK");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Configurar el área de texto para la consola
        consola = new JTextArea();
        consola.setEditable(false); // Solo lectura
        consola.setBackground(Color.WHITE);
        consola.setForeground(Color.BLACK);
        consola.setFont(new Font("Monospaced", Font.PLAIN, 14));

        // Agregar un JScrollPane para permitir desplazamiento en la consola
        JScrollPane scrollPane = new JScrollPane(consola);
        add(scrollPane, BorderLayout.CENTER);

        // Panel para la entrada de texto y el botón
        JPanel panelInferior = new JPanel();
        panelInferior.setLayout(new BorderLayout());

        // Configurar el campo de entrada de texto
        entradaTexto = new JTextField();
        entradaTexto.setBackground(Color.BLACK);
        entradaTexto.setForeground(Color.WHITE);
        entradaTexto.setFont(new Font("Monospaced", Font.PLAIN, 14));
        panelInferior.add(entradaTexto, BorderLayout.CENTER);

        // Configurar el botón de enviar
        botonEnviar = new JButton("Enviar");
        panelInferior.add(botonEnviar, BorderLayout.EAST);

        // Agregar el panel inferior al JFrame
        add(panelInferior, BorderLayout.SOUTH);

        // Acción para el botón de enviar
        botonEnviar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enviarTexto();
            }
        });

        // Permitir el envío con Enter
        entradaTexto.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enviarTexto();
            }
        });

        setVisible(true);
    }

    // Método para enviar texto a la consola
    private void enviarTexto() {
        String texto = entradaTexto.getText();
        if (!texto.isEmpty()) {
            consola.append("> " + texto + "\n"); // Muestra el texto en el área de la consola
            entradaTexto.setText(""); // Limpia el campo de entrada
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ConsolaGrafica());
    }
}
