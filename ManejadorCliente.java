/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socketredes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ManejadorCliente extends Thread {
    // Se realiza la conexión entre el cliente y el servidor
    private Socket socket;
    // Utiliza el objeto Servidor para acceder a los métodos de la clase
    // y por ejemplo, poder enviar mensajes o imagenes.
    private Servidor servidor;
    // Recibe los objetos Mensaje enviados por el usuario
    private ObjectInputStream entrada;
    // Es por donde se envian los mensajes al cliente desde el server.
    private ObjectOutputStream salida;
    // Nombr del user
    private String nombre;
    
    public ManejadorCliente(Socket socket, Servidor servidor){
        this.socket = socket;
        this.servidor = servidor;
    }

    public String getNombre() {return nombre;}
    
    @Override
    public void run(){
        try{
            // Envia y recibe mensajes a través del socket.
            // Se envian como object streams porque no solo son cadenas de texto, sino que contienen imagenes
            salida = new ObjectOutputStream(socket.getOutputStream());
            entrada = new ObjectInputStream(socket.getInputStream());
            
            // Lee el primer mensaje con el nombre del usuario
            Mensaje mensajeInicio = (Mensaje) entrada.readObject();
            this.nombre = mensajeInicio.getRemitente();
            System.out.println(nombre + " se ha unido al chat.");
            servidor.enviarATodos(new Mensaje("Servidor", nombre + " se ha unido al chat"));
            
            Mensaje mensaje;
            while ((mensaje = (Mensaje) entrada.readObject()) != null) {
                if (mensaje.esPrivado()){
                    servidor.enviarPrivado(mensaje.getDestinatario(), mensaje);
                } else {
                    servidor.enviarATodos(mensaje);
                }
            }
            
        } catch(Exception e) {
            System.out.println("Cliente desconectado: " + nombre);
        } finally {
            servidor.eliminarCliente(this);
            servidor.enviarATodos(new Mensaje("Servidor", nombre + " ha salido del chat."));
            
            try{
                socket.close();
            } catch (IOException ignored){}
        }
    }
    
    public void enviarMensaje(Mensaje mensaje){
        try{
            salida.writeObject(mensaje);
        } catch (IOException e){
            System.out.println("Error enviando mensaje a " + nombre);
        }
    }
    
}
