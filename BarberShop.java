import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

/**
 * @author Masoud Dabbaghi
 * @version 1.0
 * @link https://github.com/masoudd2159/Barber-Shop
 * @since 2019
 **/

public class BarberShop extends Thread {

    int count;
    private static final int TIME_SLEEP = 1500;
    private Queue<Integer> queue;

    private static Semaphore maxCapacity;
    private static Semaphore sofa;
    private static Semaphore barberChair;
    private static Semaphore coord;
    private static Semaphore mutex_1;
    private static Semaphore mutex_2;
    private static Semaphore custReady;
    private static Semaphore leaveBarberChair;
    private static Semaphore payment;
    private static Semaphore receipt;
    private static Semaphore[] finished = new Semaphore[50];

    private BarberShop() {
        queue = new LinkedList<>();

        maxCapacity = new Semaphore(20, true);
        sofa = new Semaphore(4, true);
        barberChair = new Semaphore(3, true);
        coord = new Semaphore(3, true);
        mutex_1 = new Semaphore(1, true);
        mutex_2 = new Semaphore(1, true);
        custReady = new Semaphore(0, true);
        leaveBarberChair = new Semaphore(0, true);
        payment = new Semaphore(0, true);
        receipt = new Semaphore(0, true);

        for (int i = 0; i < 50; i++) {
            finished[i] = new Semaphore(0, true);
        }
    }

    class Customer extends Thread {

        int custnr;
        int id;

        Customer(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                maxCapacity.acquire();
                this.enterShop();
                mutex_1.acquire();
                count++;
                custnr = count;
                mutex_1.release();
                sofa.acquire();
                this.sitOnSofa();
                barberChair.acquire();
                this.getUpFromSofa();
                sofa.release();
                this.sitOnBarberChair();
                mutex_2.acquire();
                this.enQueue(id);
                custReady.release();
                mutex_2.release();
                finished[id].acquire();
                this.leaveBarberChairs();
                leaveBarberChair.release();
                this.pay();
                payment.release();
                receipt.acquire();
                this.exitShop();
                maxCapacity.release();
            } catch (
                    InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void enterShop() {
            try {
                System.out.println("Customer " + id + " Enter To Shop");
                sleep(TIME_SLEEP);
            } catch (InterruptedException ignored) {
            }
        }

        private void sitOnSofa() {
            try {
                System.out.println("Customer " + id + " Sit on Sofa");
                Thread.sleep(TIME_SLEEP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void getUpFromSofa() {
            try {
                System.out.println("Customer " + id + " Get Up From Sofa");
                Thread.sleep(TIME_SLEEP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void sitOnBarberChair() {
            try {
                System.out.println("Customer " + id + " Sit On Barber Chair");
                Thread.sleep(TIME_SLEEP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void enQueue(int q) {
            queue.add(q);
            System.out.println("Customer " + id + " Add Queue : " + q);
        }

        private void leaveBarberChairs() {
            try {
                System.out.println("Customer " + id + " Leave Barber Chairs");
                Thread.sleep(TIME_SLEEP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void pay() {
            try {
                System.out.println("Customer " + id + " Pey Money");
                Thread.sleep(TIME_SLEEP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void exitShop() {
            try {
                System.out.println("Customer " + id + " Exit Shop");
                Thread.sleep(TIME_SLEEP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class Barber extends Thread {

        int bCust;
        int id;

        Barber(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    custReady.acquire();
                    mutex_2.acquire();
                    deQueue();
                    mutex_2.release();
                    coord.acquire();
                    cutHair();
                    coord.release();
                    finished[bCust].release();
                    leaveBarberChair.acquire();
                    barberChair.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void deQueue() {
            bCust = queue.remove();
            System.out.println("Barber " + id + " Remove Queue : " + bCust);
        }

        private void cutHair() {
            try {
                System.out.println("Barber " + id + " Cut Hair");
                Thread.sleep(TIME_SLEEP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class Cashier extends Thread {

        int id;

        Cashier(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    payment.acquire();
                    coord.acquire();
                    acceptPay();
                    coord.release();
                    receipt.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void acceptPay() {
            try {
                System.out.println("Cashier " + id + " Accept Pay");
                Thread.sleep(TIME_SLEEP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) {
        BarberShop barberShop = new BarberShop();
        barberShop.start();
    }

    @Override
    public void run() {
        for (int i = 0; i < 50; i++) {
            Customer customer = new Customer(i);
            customer.start();
        }

        for (int i = 0; i < 3; i++) {
            Barber barber = new Barber(i);
            barber.start();
        }

        for (int i = 0; i < 1; i++) {
            Cashier cashier = new Cashier(i);
            cashier.start();
        }
    }
}
