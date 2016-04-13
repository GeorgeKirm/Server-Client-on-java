package servernt;

import java.net.*;
import java.io.*;
import static java.lang.System.exit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.JOptionPane;
import message.Email;

/**
 * Class that communicates with the client and reads/writes the mails from the
 * system.
 *
 * @author Gewrgios Kirmitsakis
 */
class ServerNT extends Thread {

    private static final Lock lock = new ReentrantLock(true);
    private InputStream in;
    private OutputStream out;
    private ObjectInputStream mapIn;
    private ObjectOutputStream mapOut;
    private Socket clientSocket;
    private static String pathToFile1 = "";
    private static String pathToFile2 = "";
    private static volatile HashMap<String, List<Email>> dataAllH;
    private static volatile HashMap<String, String[]> dataUsersH;

    public ServerNT() {
    }

    /**
     * Constructor for sockets and streams with the client.
     *
     * @param aClientSocket the clients socket with the server.
     */
    private ServerNT(Socket aClientSocket) {
        try {
            clientSocket = aClientSocket;
            in = new BufferedInputStream(clientSocket.getInputStream());
            out = clientSocket.getOutputStream();
            mapIn = new ObjectInputStream(in);
            mapOut = new ObjectOutputStream(out);
            this.start();
        } catch (IOException e) {
            System.out.println("connection:" + e.getMessage());
        }
    }

    /**
     * Desirializes the 2 saved hashmaps. First is trying to find the files, if
     * its .jar it is looking in the same directory, if it is running from IDE
     * it is looking in the directory of the folder. If it does not find files
     * it will use the preexisting files saved as resources inside of the
     * project/jar.
     */
    private static void readFilesNewWay() {
        String namer = "";
        try {
            namer = new java.io.File(ServerNT.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName().toString();
//            JOptionPane.showMessageDialog(null, namer, "Info message", JOptionPane.INFORMATION_MESSAGE);
            pathToFile1 = new File(ServerNT.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).toString();
        } catch (URISyntaxException e) {
        }
        if (!namer.equals("classes")) {
            pathToFile2 = pathToFile1.replaceFirst(namer, "hashmap2.ser"); //if it finds .jar file this means it is running from jar
            pathToFile1 = pathToFile1.replaceFirst(namer, "hashmap1.ser"); // i change directory to be the directory from the .jar
        } else { // project running from IDE. NOTE: i am using netbeans as IDE so i dont know if it works for others (I dont see why not though)
            pathToFile1 = "hashmap1.ser";
            pathToFile2 = "hashmap2.ser";
        }
        try {
//            JOptionPane.showMessageDialog(null, "EDW: "+pathToFile1, "Info message", JOptionPane.INFORMATION_MESSAGE);
            FileInputStream fis1 = new FileInputStream(pathToFile1);
            FileInputStream fis2 = new FileInputStream(pathToFile2);
            readerFilesNewWay(fis1, fis2); // It fount the files in the paths location
        } catch (Exception e) { // didnt find the file in the paths location
            JOptionPane.showMessageDialog(null, "Didnt find files in default location.\nGetting info from preexisting files.", "Info message", JOptionPane.INFORMATION_MESSAGE);
            try {
                InputStream fis1 = ServerNT.class.getResourceAsStream("/servernt/hashmap1.ser");
                InputStream fis2 = ServerNT.class.getResourceAsStream("/servernt/hashmap2.ser");
                readerFilesNewWay(fis1, fis2); // It uses the preexisting files
            } catch (Exception ee) { // Error with the syntax of the file probably
                JOptionPane.showMessageDialog(null, "Error with the file", "Error message", JOptionPane.PLAIN_MESSAGE);
                System.exit(0);
            }
        }
    }

    /**
     * Reading the file from the paths location.
     *
     * @param fis1 stream for the file with the hashmap "dataAllH".
     * @param fis2 stream for the file with the hashmap "dataUsersH".
     * @throws Exception files on path does not exist.
     */
    private static void readerFilesNewWay(FileInputStream fis1, FileInputStream fis2) throws Exception {
        ObjectInputStream ois1 = new ObjectInputStream(fis1);
        dataAllH = (HashMap) ois1.readObject();
        ois1.close();
        fis1.close();
        ObjectInputStream ois2 = new ObjectInputStream(fis2);
        dataUsersH = (HashMap) ois2.readObject();
        ois2.close();
        fis2.close();
    }

    /**
     * Reading the recourses from inside the project.
     *
     * @param fis1 stream for the file with the hashmap "dataAllH".
     * @param fis2 stream for the file with the hashmap "dataUsersH".
     * @throws Exception files on path does not exist.
     */
    private static void readerFilesNewWay(InputStream fis1, InputStream fis2) throws Exception {
        ObjectInputStream ois1 = new ObjectInputStream(fis1);
        dataAllH = (HashMap) ois1.readObject();
        ois1.close();
        fis1.close();
        ObjectInputStream ois2 = new ObjectInputStream(fis2);
        dataUsersH = (HashMap) ois2.readObject();
        ois2.close();
        fis2.close();
    }

    /**
     * Saves the 2 hashmaps in path of the project/.jar.
     */
    public static void writeFilesNewWay() {
        lock.lock();
        try {
            FileOutputStream fos1 = new FileOutputStream(pathToFile1);
            ObjectOutputStream oos1 = new ObjectOutputStream(fos1);
            oos1.writeObject(dataAllH);
            oos1.close();
            fos1.close();
            FileOutputStream fos2 = new FileOutputStream(pathToFile2);
            ObjectOutputStream oos2 = new ObjectOutputStream(fos2);
            oos2.writeObject(dataUsersH);
            oos2.close();
            fos2.close();
            System.out.printf("Serialized HashMap data is saved in hashmap.ser\n");
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(null, "Error with the file", "Error message", JOptionPane.PLAIN_MESSAGE);
        } finally {
            lock.unlock();
        }
    }

    /**
     * First it loads the hashmaps. then it is opening a port and waiting for a
     * client to connect.
     */
    public static void mainLoop(int serverPort) {
        readFilesNewWay();
        try {
//            System.out.println("EDW "+serverPort);
            ServerSocket listenSocket = new ServerSocket(serverPort);
            while (true) {
                Socket clientSocket = listenSocket.accept(); //waits socket
                System.out.println("Request from client " + clientSocket.getInetAddress() + " at port " + clientSocket.getPort());
                ServerNT c = new ServerNT(clientSocket);
            }
        } catch (IOException e) {
            System.out.println("Listen socket:" + e.getMessage());
        }
    }

    /**
     * Checks the inputs of the client and if both username and password are ok,
     * it tells the server.
     *
     * @param userNamePasswordS has both the name and the password in a string
     * of the user what is trying to log in.
     * @return true if all ok or false if something was wrong.
     */
    private boolean logIn(String userNamePasswordS) {
        lock.lock();
        try {
            String[] userNamePasswordSS = userNamePasswordS.split(" ");
            if (dataUsersH.containsKey(userNamePasswordSS[0])) {
                //            System.out.println(dataUsersH.get(userNamePasswordSS[0])[0].equals(userNamePasswordSS[1]));
//                refresherH.put(userNamePasswordSS[0], false);
                return dataUsersH.get(userNamePasswordSS[0])[0].equals(userNamePasswordSS[1]);
            } else {
                return false;
            }

        } finally {
            lock.unlock();
        }
    }

    /**
     * Checks the inputs of the client and if both username and password are ok,
     * it registers the new user and, it tells the server.
     *
     * @param userNamePasswordS has both the name and the password in a string
     * of the user what is trying to log in.
     * @return true if all ok or false if something was wrong.
     */
    private boolean register(String userNamePasswordS) {
        lock.lock();
        try {
            boolean cheacker = false;
            String[] userNamePasswordSS = userNamePasswordS.split(" ");
            if (dataUsersH.get(userNamePasswordSS[0]) == null) {
                cheacker = true;
            }
            if (cheacker) {
                String[] passwordNumberSS = new String[3];
                passwordNumberSS[0] = userNamePasswordSS[1];
                passwordNumberSS[1] = "0"; // has 0 mails as he has new account
                passwordNumberSS[2] = "false"; // tells that user needs or dont need refresh
                dataUsersH.put(userNamePasswordSS[0], passwordNumberSS);
                List<Email> dataLE = new ArrayList<>();
                dataAllH.put(userNamePasswordSS[0], dataLE);
            }
            return cheacker;
        } finally {
            lock.unlock();
        }
    }

    /**
     * both sender and reciever (if he exist) gets +1 to the count of his emails
     * and the hashmap is updated with the new email.
     *
     * @param mail0 the mail that the user sents.
     */
    private void sentEmail(Email mail0) {
        lock.lock();
        try {
            //+1 to the count of their email counts
            dataUsersH.get(mail0.senterGetter())[1] = Integer.toString(Integer.parseInt(dataUsersH.get(mail0.senterGetter())[1]) + 1);
            if (dataUsersH.containsKey(mail0.recieverGetter())) {
                dataUsersH.get(mail0.recieverGetter())[1] = Integer.toString(Integer.parseInt(dataUsersH.get(mail0.recieverGetter())[1]) + 1);
            }
            dataUsersH.get(mail0.senterGetter())[2] = "false"; //false to refresh the client emails
            dataAllH.get(mail0.senterGetter()).add(mail0);
            if (dataUsersH.containsKey(mail0.recieverGetter())) { //if reciever exist
                Email mail1 = new Email(mail0); //creates the email for the reciever to update the hashmap
                dataAllH.get(mail0.recieverGetter()).add(mail1);
                dataUsersH.get(mail0.recieverGetter())[2] = "false";
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Deletes the email from the hashmap that the user wants to delete and the
     * count of his emails get -1.
     *
     * @param name name of the user who wants to delete an email.
     * @param mail ID of the mail.
     */
    private void deleteEmail(String name, int mail) {
        lock.lock();
        try {
            System.out.println(Integer.toString(Integer.parseInt(dataUsersH.get(name)[1]) - 1));
            if (dataUsersH.containsKey(name)) {
                System.out.println(name);
                dataUsersH.get(name)[1] = Integer.toString(Integer.parseInt(dataUsersH.get(name)[1]) - 1);
                dataAllH.get(name).remove(mail);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * It makes the value of the mail "isNew" to false because the reader is
     * reading it.
     *
     * @param name the user that wants to read the mail.
     * @param mail the ID of the mail that the user wants to read.
     */
    private void readThatMail(String name, int mail) {
        lock.lock();
        try {
            if (dataUsersH.containsKey(name)) {
//                System.out.println(name);
//                System.out.println(mail);
                dataAllH.get(name).get(mail).isNewSetter(false);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Removes the account from the hashmaps.
     *
     * @param name the username of the account.
     */
    private void deleteAccount(String name) {
        lock.lock();
        try {
            dataUsersH.remove(name);
            dataAllH.remove(name);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Changes the password of the users account.
     *
     * @param userNamePasswordS username and password of the account.
     */
    private void changePassword(String userNamePasswordS) {
        lock.lock();
        try {
            String[] userNamePasswordSS = userNamePasswordS.split(" ");
            dataUsersH.get(userNamePasswordSS[0])[0] = userNamePasswordSS[1];
        } finally {
            lock.unlock();
        }
    }

    /**
     * Getter for hashmap with mails.
     *
     * @return hashmap with the list of emails.
     */
    public HashMap<String, List<Email>> showMails() {
        return dataAllH;
    }

    /**
     * Closes the server.
     */
    public void closeServer() {
//        mapOut.close();
//        mapIn.close();
//        clientSocket.close();
        exit(0);
    }

    /**
     * Thread that has a loop and communicates with the client.
     */
    @Override
    public void run() {
        try {
            String commandS;
            while (true) {
                commandS = (String) mapIn.readObject(); // Gets the command from the client
                String dataS;
                boolean loulaB;
                switch (commandS) {
                    case "logIn":
                        System.out.println("logIn");
                        dataS = (String) mapIn.readObject(); // "Username password"
                        loulaB = logIn(dataS); // everything is great for login if loulaB= true
                        mapOut.writeObject(loulaB); // tells if logIn is ok or not
                        if (loulaB) {
                            List<Email> dataLE;
                            String[] dataSS = dataS.split(" ");
                            dataLE = dataAllH.get(dataSS[0]); // gets list of emails for that account
//                            for (Email loula : dataLE) {
//                                System.out.println(loula.soutAllGetter());
//                            }
                            mapOut.writeObject(dataLE); // sents to client the list of his emails
                        }
                        break;
                    case "register":
                        System.out.println("register");
                        dataS = (String) mapIn.readObject(); // "Username password"
                        loulaB = register(dataS); // everything is great for register if loulaB= true
                        mapOut.writeObject(loulaB); // tells if register is ok or not
                        if (loulaB) {
                            List<Email> dataLE;
                            String[] dataSS = dataS.split(" ");
                            dataLE = dataAllH.get(dataSS[0]); // gets the empty list
//                            for (Email loula : dataLE) {
//                                System.out.println(loula.soutAllGetter());
//                            }
                            mapOut.writeObject(dataLE); // sents the list
                        }
                        break;
                    case "changePassword":
                        System.out.println("changePassword");
                        dataS = (String) mapIn.readObject(); // "Username password"
                        changePassword(dataS);
                        mapOut.writeObject(true); // the command has been completed
                        break;
                    case "sentEmail":
                        System.out.println("sentEmail");
                        Email dataE1 = (Email) mapIn.readObject(); // mail that the client wants to sent (has everything that the server needs inside)
                        sentEmail(dataE1);
                        mapOut.writeObject(true); // the command has been completed
                        break;
                    case "deleteEmail":
                        System.out.println("deleteEmail");
                        dataS = (String) mapIn.readObject(); // username of the client
                        int dataE0 = (int) mapIn.readObject(); // ID of the mail
                        deleteEmail(dataS, dataE0);
                        mapOut.writeObject(true); // the command has been completed
                        break;
                    case "readThatMail":
                        System.out.println("readThatMail");
                        dataS = (String) mapIn.readObject();  // username of the client
                        int dataE2 = (int) mapIn.readObject(); // ID of the mail
                        readThatMail(dataS, dataE2);
                        break;
                    case "refresh":
                        System.out.println("refresh");
                        dataS = (String) mapIn.readObject(); // username of the client
                        if (dataUsersH.get(dataS)[2].equals("false")) { // if the client need to be refreshed
                            List<Email> dataLE;
                            dataLE = dataAllH.get(dataS); // gets the new list of emails
                            mapOut.writeObject(false); // tells account that the new list of mails will be sented
                            mapOut.reset();
                            mapOut.writeObject(dataLE); // sents the list
                            dataUsersH.get(dataS)[2] = "true";
                            System.out.println("nai");

//                            System.out.println(dataS);
//                            for (Email loula : dataLE) {
//                                System.out.println(loula.soutAllGetter());
//                            }
                        } else {
                            mapOut.writeObject(true); // tells the client that no need for refresh
                            System.out.println("oxi");
                        }

                        break;
                    case "deleteAccount":
                        System.out.println("deleteAccount");
                        dataS = (String) mapIn.readObject(); // username of the client
                        deleteAccount(dataS);
                        mapOut.writeObject(true); // command completed
                        break;
                    default:
                        System.out.println("ERROR wrong input");
                        break;
                }
            }
        } catch (EOFException e) {
            System.out.println("Client closed");
        } catch (IOException e) {
            System.out.println("readline:" + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("CNF:" + e.getMessage());
        } finally { // whatever happens the tread will close the streams and socket
            try {
//                writeFiles();
                mapOut.close();
                mapIn.close();
                clientSocket.close();
            } catch (IOException e) {/*close failed*/

            }
        }
    }
}

//Nothing is down here
//    private static void readFilesT() {
//        try {
//            pathToFile = new File(ServerNT.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).toString();
//        } catch (URISyntaxException e) {
//        }
//        if (pathToFile.endsWith("serverNT.jar")) {
//            pathToFile = pathToFile.replaceFirst("serverNT.jar", "UserMap1.txt");
//        } else {
//            int endIndex = pathToFile.lastIndexOf("build");
//            if (endIndex != -1) {
//                pathToFile = pathToFile.substring(0, endIndex) + "UserMap1.txt";
//            }
//        }
//        System.out.println("€ϛ↓Known bug to break mailϡϟ");
////        JOptionPane.showMessageDialog(null, pathToFile, "Debugging message", JOptionPane.PLAIN_MESSAGE);
//        try (BufferedReader theFile1 = new BufferedReader(new FileReader(pathToFile))) {
//            System.out.println("mpike");
//            readerFilesT(theFile1);
//        } catch (Exception e) {
//            JOptionPane.showMessageDialog(null, "Didnt find file \"/UserMap1.txt\" in default location.\n(" + pathToFile + ")\nGetting info from preexisting file.", "Info message", JOptionPane.INFORMATION_MESSAGE);
//            try (BufferedReader theFile = new BufferedReader(new InputStreamReader(ServerNT.class.getResourceAsStream("/files/UserMap0.txt")))) {
//                readerFilesT(theFile);
//            } catch (Exception ee) {
//                JOptionPane.showMessageDialog(null, "Error with the file", "Error message", JOptionPane.PLAIN_MESSAGE);
//                System.exit(0);
//            }
//        }
//    }
//    private static void readerFilesT(BufferedReader theFile) throws IOException {
//        dataAllH = new HashMap<>();
//        dataUsersH = new HashMap<>();
//        String InfoFromFileLine;
//        int i = 0;
//        while ((InfoFromFileLine = theFile.readLine()) != null) {
//            System.out.println(InfoFromFileLine);
//            List<Email> dataLE = new ArrayList<>();
//            int howManyEmailsI = Integer.parseInt(InfoFromFileLine);
//            String nameS = theFile.readLine();
//            System.out.println(nameS);
//            String passwordS = theFile.readLine();
//            System.out.println(passwordS);
//            String[] passwordNumberSS = new String[3];
//            passwordNumberSS[0] = passwordS;
//            passwordNumberSS[1] = InfoFromFileLine;
//            passwordNumberSS[2] = "false";
//            dataUsersH.put(nameS, passwordNumberSS);
//            for (int j = 0; j < howManyEmailsI; j++) {
//                Email dataE = new Email(); //isNew//senter//reciever//subject//mainBody
//                dataE.isNewSetter(theFile.readLine());
//                System.out.println(dataE.isNewSGetter());
//                dataE.senterSetter(theFile.readLine());
//                System.out.println(dataE.senterGetter());
//                dataE.recieverSetter(theFile.readLine());
//                System.out.println(dataE.recieverGetter());
//                dataE.subjectSetter(theFile.readLine());
//                System.out.println(dataE.subjectGetter());
//                dataE.mainBodySetter(theFile.readLine());
//                System.out.println(dataE.mainBodyGetter());
//                dataLE.add(dataE);
//            }
//            dataAllH.put(nameS, dataLE);
//        }
//    }
//    public static void writeFiles() {
//        lock.lock();
//        try {
////            try (PrintWriter theFile1 = new PrintWriter(new BufferedWriter(new FileWriter(ServerNT.class.getResource("/files/UserMap1.txt").getPath(), false)))) {
//            try (PrintWriter theFile1 = new PrintWriter(new BufferedWriter(new FileWriter(pathToFile, false)))) {
//                for (Entry<String, List<Email>> entry : dataAllH.entrySet()) {
//                    theFile1.println(dataUsersH.get(entry.getKey())[1]);
//                    theFile1.println(entry.getKey());
//                    theFile1.println(dataUsersH.get(entry.getKey())[0]);
//                    for (int i = 0; i < Integer.parseInt(dataUsersH.get(entry.getKey())[1]); i++) {
//                        theFile1.println(entry.getValue().get(i).writeAllGetter());
//                    }
//                }
//            } catch (IOException e) {
//                JOptionPane.showMessageDialog(null, "It apears to be a problem with the program files.",
//                        "Error message", JOptionPane.PLAIN_MESSAGE);
//            }
//        } finally {
//            lock.unlock();
//        }
//    }
