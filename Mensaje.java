/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socketredes;

// Permite leer y escribir el estado de un objeto. 
//Se utiliza para la transimisión de datos de objetos en redes
import java.io.Serializable;


public class Mensaje implements Serializable{
    private String remitente;
    private String contenido;
    private byte[] imagen;
    private String destinatario;
    private String[] listaUsuarios;
    
    
    // Crea el mensaje
    public Mensaje(String remitente, String contenido){
        this.remitente = remitente;
        this.contenido = contenido;
        this.imagen = null;
        this.destinatario = "Todos";
        this.listaUsuarios = null;
    }
    
    // Crea la imagen
    public Mensaje(String remitente, byte[] imagen){
        this.remitente = remitente;
        this.contenido = null;
        this.imagen = imagen;
        this.destinatario = "Todos";
        this.listaUsuarios = null;
    }
    
    // Construye una lista de usuarios
    public Mensaje(String remitente, String contenido, String[] listaUsuarios){
        this.remitente = remitente;
        this.contenido = null;
        this.imagen = null;
        this.destinatario = "Todos";
        this.listaUsuarios = listaUsuarios;
    }

    public String getRemitente() {return remitente;}
    public String getContenido() {return contenido;}
    public byte[] getImagen() {return imagen;}
    
    public void setDestinatario(String destinatario) { this.destinatario = destinatario; }
    public String getDestinatario() {return destinatario;}

    public void setListaUsuarios(String[] listaUsuarios) {this.listaUsuarios = listaUsuarios;}
    
    // En el caso de que destinatario no sea null y que no sea equivalente a "todos"
    // la función demuestra que es privado.
    public boolean esPrivado() {
        return destinatario != null && !destinatario.equals("Todos");
    }
    
    // Verifica si tiene una imagen o no.
    public boolean tieneImagen() {return imagen != null;}
    
    // Hace que el texto sea legible por el usuario
    //Override permite detectar errores al compilar
    @Override
    public String toString(){
        if (tieneImagen()){
            if (esPrivado()){
                return remitente + " (privado a " + destinatario + ") ha enviado una imagen.";
            } else {
                return remitente + " ha enviado una imagen.";
            }
        } else{
            if (esPrivado()) return remitente + " (privado a " + destinatario + ") ha enviado una imagen.";
            else return remitente + ": " + contenido;
        }
    }
}
