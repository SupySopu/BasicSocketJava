/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socketredes;

import javax.swing.JOptionPane;

/**
 *
 * @author Redes-20
 */
public class SocketRedes {

    public static void main(String[] args) {
        String opcionServerClient = JOptionPane.showInputDialog("Ingrese 'server' para iniciar servidor o 'client' para iniciar cliente");
        if (opcionServerClient.equalsIgnoreCase("server")) {
            new Servidor(5000).iniciar();
        } else if (opcionServerClient.equalsIgnoreCase("client")) {
            ClienteGui.main(null); // abre la interfaz gráfica
        } else {
        System.out.println("Opción inválida.");
        }
    }
    
}
