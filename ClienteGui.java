/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socketredes;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class ClienteGui extends JFrame {
    private String nombre;
    private String host;
    private int puerto;
    private Socket socket;
    private ObjectOutputStream salida;
    private ObjectInputStream entrada;
    private JTextPane areaMensajes;
    private JTextField campoMensaje;
    private JButton botonEnviar;
    private JButton botonImagen;
    private JComboBox<String> comboUsuarios;
    
    public ClienteGui(String nombre, String host, int puerto) {
        this.nombre = nombre;
        this.host = host;
        this.puerto = puerto;
        inicializarInterfaz();
        conectarServidor();
    }
    private void inicializarInterfaz() {
        setTitle("Chat - " + nombre);
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        // Área de mensajes
        areaMensajes = new JTextPane();
        areaMensajes.setEditable(false);
        JScrollPane scroll = new JScrollPane(areaMensajes);

        add(scroll, BorderLayout.CENTER);
        // Panel inferior
        JPanel panelInferior = new JPanel(new BorderLayout(5, 5));
        campoMensaje = new JTextField();
        botonEnviar = new JButton("Enviar");
        botonImagen = new JButton("");
        panelInferior.add(botonImagen, BorderLayout.WEST);
        panelInferior.add(campoMensaje, BorderLayout.CENTER);
        panelInferior.add(botonEnviar, BorderLayout.EAST);
        add(panelInferior, BorderLayout.SOUTH);
        // Panel superior: combo de usuarios
        comboUsuarios = new JComboBox<>();
        comboUsuarios.addItem("Todos");
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(comboUsuarios, BorderLayout.WEST);
        add(panelSuperior, BorderLayout.NORTH);
        // Acciones
        botonEnviar.addActionListener(e -> enviarMensaje());
        campoMensaje.addActionListener(e -> enviarMensaje());
        botonImagen.addActionListener(e -> subirImagen());
        setVisible(true);
    }

    private void conectarServidor() {
        try {
            socket = new Socket(host, puerto);
            salida = new ObjectOutputStream(socket.getOutputStream());
            entrada = new ObjectInputStream(socket.getInputStream());
            // Enviar mensaje de bienvenida con nombre
            salida.writeObject(new Mensaje(nombre, "Hola, me he conectado"));
            appendTexto("Conectado al servidor en " + host + ":" + puerto + "\n");
            // Hilo receptor
            Thread receptor = new Thread(() -> {
                try {
                    while (true) {
                        Mensaje mensaje;
                        try {
                            mensaje = (Mensaje) entrada.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            appendTexto(" Desconectado del servidor.\n");
                            break;
                        }
                        final Mensaje mensajeFinal = mensaje;
                        SwingUtilities.invokeLater(() -> mostrarMensaje(mensajeFinal));
                        // Actualizar combo de usuarios (puedes pedir al servidor la lista real)
                        if (mensaje.getRemitente().equals("Servidor")) {

                            // Esto se puede mejorar solicitando lista de usuarios
                        }
                    }
                } catch (Exception e) {
                    appendTexto(" Error inesperado.\n");
                }
            });
            receptor.start();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al conectar: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    private void enviarMensaje() {
        String texto = campoMensaje.getText().trim();
        if (!texto.isEmpty()) {
            try {
                String destinatario = (String) comboUsuarios.getSelectedItem();
                Mensaje mensaje = new Mensaje(nombre, texto);
                if (!destinatario.equals("Todos")) {
                    mensaje.setDestinatario(destinatario);
                }
                salida.writeObject(mensaje);
                campoMensaje.setText("");
            } catch (IOException e) {
                appendTexto(" No se pudo enviar el mensaje.\n");
            }
        }else{
            JOptionPane.showMessageDialog(this, "Mensaje vacio");
        }
    }

    private void subirImagen() {
        JFileChooser fileChooser = new JFileChooser();
        int opcion = fileChooser.showOpenDialog(this);
        if (opcion == JFileChooser.APPROVE_OPTION) {
            try {
                File archivo = fileChooser.getSelectedFile();
                byte[] bytesImagen = java.nio.file.Files.readAllBytes(archivo.toPath());
                Mensaje mensajeImagen = new Mensaje(nombre, bytesImagen);
                String destinatario = (String) comboUsuarios.getSelectedItem();
                if (!destinatario.equals("Todos")) mensajeImagen.setDestinatario(destinatario);
                salida.writeObject(mensajeImagen);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error al enviar la imagen: " + ex.getMessage());
            }
        }
    }

    private void mostrarMensaje(Mensaje mensaje) {
        StyledDocument doc = areaMensajes.getStyledDocument();
        try {
            if (mensaje.tieneImagen()) {

                ImageIcon icon = new ImageIcon(mensaje.getImagen());
                // Redimensionar 300x300
                Image img = icon.getImage();
                Image imgRed = img.getScaledInstance(300, 300, Image.SCALE_SMOOTH);
                ImageIcon iconRed = new ImageIcon(imgRed);
                JLabel labelImagen = new JLabel(iconRed);
                areaMensajes.setCaretPosition(doc.getLength());
                areaMensajes.insertComponent(labelImagen);
                doc.insertString(doc.getLength(), "\n", null);
            } else {
                doc.insertString(doc.getLength(), mensaje + "\n", null);
            }
            areaMensajes.setCaretPosition(doc.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void appendTexto(String texto) {
        SwingUtilities.invokeLater(() -> {
            try {
                areaMensajes.getStyledDocument().insertString(
                areaMensajes.getStyledDocument().getLength(), texto, null);
                areaMensajes.setCaretPosition(areaMensajes.getStyledDocument().getLength());
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        String nombre = JOptionPane.showInputDialog("Ingrese su nombre:");
        
        if (nombre != null && !nombre.trim().isEmpty()) {
            SwingUtilities.invokeLater(() -> new ClienteGui(nombre, "localhost", 5000));
        } else {
             JOptionPane.showMessageDialog(null, "Nombre no valido");
        }
    }
}