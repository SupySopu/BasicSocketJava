/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socketredes;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Servidor {
    
    // El servidor va a escuchar al puerto
    private int puerto;
    // Cada cliente va a ser manejado para que funcione en el servidor
    private List<ManejadorCliente> clientes;

    public Servidor(int puerto) {
        this.puerto = puerto;
        clientes = new ArrayList<>();
    }
    
    // Abre el puerto de servidor y se queda escuchando las peticiones de conexión
    public void iniciar(){
        try (ServerSocket serverSocket = new ServerSocket(puerto)){
            System.out.println("Servidor iniciado en el puerto " + puerto);
            
            // Para recibir todas las conexiones y agregar usuarios, se utiliza este while(true)
            while (true){
                Socket socket = serverSocket.accept();
                ManejadorCliente mc = new ManejadorCliente(socket, this);
                agregarCliente(mc);
                mc.start();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    // synchronized se utiliza para evitar que multiples hilos modifiquen la lista al mismo tiempo.
    // por ejemplo si dos o mas clientes se conectan al mismo tiempo
    public synchronized void agregarCliente(ManejadorCliente mc){
        clientes.add(mc);
    }
    
    public synchronized void eliminarCliente(ManejadorCliente mc){
        clientes.remove(mc);
    }
    
    public synchronized void enviarATodos(Mensaje mensaje){
        for (ManejadorCliente mc : clientes){
            mc.enviarMensaje(mensaje);
        }
    }
    
    public synchronized void enviarPrivado(String destinatario, Mensaje mensaje){
        for (ManejadorCliente mc : clientes){
            if (mc.getNombre().equals(destinatario)){
                mc.enviarMensaje(mensaje);
                break;
            }
        }
    }
    
    public synchronized String[] obtenerUsuarios(){
        return clientes.stream()
                .map(ManejadorCliente::getNombre)
                .toArray(String[]::new);
    }
}
