import ez.EZMessenger;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        new EZMessenger().start();

        Thread.currentThread().join();
    }
}
