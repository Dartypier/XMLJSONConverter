package it.unifi.cli;

//This class provide a progress counter when waiting the format conversion
//that can require some time with big files
class ProgressThread extends Thread{

    public void run() {
        System.out.print("Converting");
        while (!Thread.interrupted()) {
            System.out.print(".");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("\nConversion completed");
                break; // Exit the loop on interrupt
            }
        }
    }
}
