/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientnt;

/**
 * Class that creates a thread to call the refreshed every 5 seconds if window
 * of client is active.
 *
 * @author Gewrgios Kirmitsakis
 */
public class Refresher implements Runnable {

    private final GUImainBody obj;
    private final int time;

    /**
     * Initializing values, time/1000 = 1 second for the refresher to call the
     * method.
     *
     * @param obj the object that runs the GUI. Is needed to check if it is
     * active.
     */
    public Refresher(GUImainBody obj) {
        this.time = 5000;
        this.obj = obj;
        refreshingThreadCreator();
    }

    /**
     * Creates a thread if the clients window is active.
     */
    public void refreshingThreadCreator() {
        if (obj.isActive()) {
            Thread kls = new Thread(this);
            kls.start();
        }
    }

    /**
     * Calls the method to refresh the mail list every 'time' seconds until
     * window is inactive.
     */
    @Override
    public void run() {
        while (obj.isActive()) {
            System.out.println("running refresher");
            try {
                obj.refreshersMethode();
                Thread.sleep(time);
            } catch (InterruptedException ex) {
            }
        }
    }
}
