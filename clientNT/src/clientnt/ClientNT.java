/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientnt;

import message.Email;
import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Class that communicates with the server and the client.
 *
 * @author Gewrgios Kirmitsakis
 */
public class ClientNT {

    private List<Email> dataLE = new ArrayList<>();
    private Boolean dataB;

    private Socket s = null;
    private ObjectOutputStream mapOut;
    private ObjectInputStream mapIn;
    private InputStream in;
    private OutputStream out;

    /**
     * server closed or something went wrong.
     */
    private void callBrokenPipe() {
        JOptionPane.showMessageDialog(null, "No connection to server! Closing client",
                "Info-chan message", JOptionPane.PLAIN_MESSAGE);
        closer();
    }

    /**
     * closes streams and socket.
     */
    public void closer() {
        try {
            mapOut.close();
            mapIn.close();
            s.close();
        } catch (IOException e) {
        }
//        finally {
//            GUIforLogInOn temp1= new GUIforLogInOn();
//            temp1.setLocationRelativeTo(null);
//            temp1.setVisible(true);
//        }
    }

    /**
     * Establish communication with the server.
     *
     * @param commandS logIn/Register.
     * @param userNameS "username password".
     * @param ip IP that the user wants to connect to.
     * @param serverPort port that the user wants to connect to.
     * @return if everything went ok.
     */
    public boolean clientFirstConnaction(String commandS, String userNameS, String ip, int serverPort) {
        try {
            s = new Socket(ip, serverPort);
            out = s.getOutputStream();
            mapOut = new ObjectOutputStream(out);
            in = new BufferedInputStream(s.getInputStream());
            mapIn = new ObjectInputStream(in);
            mapOut.writeObject(commandS);
            mapOut.writeObject(userNameS);
            dataB = (Boolean) mapIn.readObject();
            if (dataB) {
                dataLE = (List<Email>) mapIn.readObject();
            }
        } catch (UnknownHostException e) {
            System.out.println("Socket:" + e.getMessage());
        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("readline:" + e.getMessage());
            callBrokenPipe();
            return false;
        } catch (ClassNotFoundException e) {
            System.out.println("CNF:" + e.getMessage());
//        }finally {if(s!=null) try {s.close();}catch (IOException e){System.out.println("close:"+e.getMessage());}}
        }
        return dataB;
    }

    /**
     * Get the values ready to establish communication for register.
     *
     * @param userNameS username of the account.
     * @param passwordS password of the account.
     * @param ip IP that the user wants to connect to.
     * @param port port that the user wants to connect to.
     * @return true if the register was successful.
     */
    public boolean register(String userNameS, String passwordS, String ip, String port) {
        int portI = Integer.parseInt(port);
        String dataS = userNameS + " " + passwordS;
        return clientFirstConnaction("register", dataS, ip, portI);
    }

    /**
     * Get the values ready to establish communication for register.
     *
     * @param userNameS username of the account.
     * @param passwordS password of the account.
     * @param ip IP that the user wants to connect to.
     * @param port port that the user wants to connect to.
     * @return true if the register was successful.
     */
    public boolean logIn(String userNameS, String passwordS, String ip, String port) {
        int portI = Integer.parseInt(port);
        String dataS = userNameS + " " + passwordS;
        return clientFirstConnaction("logIn", dataS, ip, portI);
    }

    /**
     * Sends to server the staff it needs to change password of that account.
     * NOTE: GUI for that class was not implemented on that version of the
     * project so this function is not been used by the client.
     *
     * @param userNameS.
     * @param passwordS.
     * @return true if password changed.
     */
    public boolean changePassword(String userNameS, String passwordS) {
        String dataS = userNameS + " " + passwordS;
        try {
            mapOut.writeObject("changePassword");
            mapOut.writeObject(dataS);
            dataB = (Boolean) mapIn.readObject();
        } catch (UnknownHostException e) {
            System.out.println("Socket:" + e.getMessage());
        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("readline:" + e.getMessage());
            callBrokenPipe();
            return false;
        } catch (ClassNotFoundException e) {
            System.out.println("CNF:" + e.getMessage());
        }
        return dataB;
    }

    /**
     * Sends username of the account and the ID of the mail that the user wants
     * to delete.
     *
     * @param userNameS username from this account.
     * @param mail ID of the mail.
     * @return true if the command was completed from the server.
     */
    public boolean deleteEmail(String userNameS, int mail) {
        try {
            mapOut.writeObject("deleteEmail");
            mapOut.writeObject(userNameS);
            mapOut.writeObject(mail);
            dataB = (Boolean) mapIn.readObject();
        } catch (UnknownHostException e) {
            System.out.println("Socket:" + e.getMessage());
        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("readline:" + e.getMessage());
            callBrokenPipe();
            return false;
        } catch (ClassNotFoundException e) {
            System.out.println("CNF:" + e.getMessage());
        }
        return dataB;
    }

    /**
     * Sends username of the account that the user wants to delete.
     *
     * @param userNameS username from this account.
     * @return true if the command was completed from the server.
     */
    public boolean deleteAccount(String userNameS) {
        try {
            mapOut.writeObject("deleteAccount");
            mapOut.writeObject(userNameS);
            dataB = (Boolean) mapIn.readObject();
        } catch (UnknownHostException e) {
            System.out.println("Socket:" + e.getMessage());
        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("readline:" + e.getMessage());
            callBrokenPipe();
            return false;
        } catch (ClassNotFoundException e) {
            System.out.println("CNF:" + e.getMessage());
        }
        return dataB;
    }

    /**
     * Is called when user wants to refresh or sent a mail.
     *
     * @param commandS the command that the user wants to do (refresh/sentMail).
     * @param userNameS the username of that account.
     * @param dataToSentE mail that the client want to sent.
     * @return true if the client took the new list of emails back from the
     * server.
     */
    private boolean clientSentMailOrRefresh(String commandS, String userNameS, Email dataToSentE) {
        try {
            mapOut.writeObject(commandS);
            if (userNameS != null) { // if null the client is senting a mail and his username excist in the mail that he sents.. so no need to resent it
                mapOut.writeObject(userNameS);
            }
            if (dataToSentE != null) { // if null the client wants to refresh his list with emails.. so no need to send anything
                mapOut.writeObject(dataToSentE);
            }
            dataB = (Boolean) mapIn.readObject();
            if (!dataB) { // the server will send back the new list with emails
                dataLE = (List<Email>) mapIn.readObject();
//                for (Email loula : dataLE) {
//                    System.out.println(loula.soutAllGetter());
//                }
            }
        } catch (UnknownHostException e) {
            System.out.println("Socket:" + e.getMessage());
        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("readline:" + e.getMessage());
            callBrokenPipe();
            return false;
        } catch (ClassNotFoundException e) {
            System.out.println("CNF:" + e.getMessage());
        }
        return dataB;
    }

    /**
     * Gets ready the values to call the function to sent the email.
     *
     * @param mail that the client wants to sent.
     * @return true if everything went ok.
     */
    public boolean sentMessage(Email mail) {
        boolean hkoB = clientSentMailOrRefresh("sentEmail", null, mail);
        return hkoB;
    }

    /**
     * Gets ready the values to call the function to refresh email list.
     *
     * @param userNameS.
     * @return false if it was needed for the list to be refreshed.
     */
    public boolean refresh(String userNameS) {
        boolean hkoB = clientSentMailOrRefresh("refresh", userNameS, null);
        return hkoB;
    }

    /**
     * Getter for the list with the emails.
     *
     * @return the list with the emails.
     */
    public List<Email> dataGetter() {
        if (dataLE.isEmpty()) {

        } else {
//            for (Email loula : dataLE) {
//                System.out.println(loula.soutAllGetter());
//            }
        }
        return dataLE;
    }

    /**
     * User reads the mail so it tells that to the server to update isNew value.
     *
     * @param userNameS username for that account.
     * @param mail ID of the mail that the user wants to read.
     */
    public void readThatMail(String userNameS, int mail) {
        try {
            mapOut.writeObject("readThatMail");
            mapOut.writeObject(userNameS);
            mapOut.writeObject(mail);
        } catch (UnknownHostException e) {
            System.out.println("Socket:" + e.getMessage());
        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("readline:" + e.getMessage());
            callBrokenPipe();
        }
    }

    /**
     * Is not used by anything.. will be deleted in future updates.
     */
    public void closing() {
        try {
            mapOut.close();
            mapIn.close();
        } catch (UnknownHostException e) {
            System.out.println("Socket:" + e.getMessage());
        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("readline:" + e.getMessage());
            callBrokenPipe();
        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (IOException e) {
                    System.out.println("close:" + e.getMessage());
                }
            }
        }
    }
}
