/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientnt;

import java.io.IOException;

/**
 * Simple class to call the logIn GUI. It is the "main" of the clientNT package
 * NOTE: In future updates it will be deleted if it keeps that simple task.
 *
 * @author Gewrgios Kirmitsakis
 */
public class Starter {

    public static void main(String args[]) throws IOException {
//        ClientNT temp2= new ClientNT();
//        temp2.startingUp();
        GUIforLogInOn temp1 = new GUIforLogInOn();
        temp1.setLocationRelativeTo(null);
        temp1.setVisible(true);
    }
}
