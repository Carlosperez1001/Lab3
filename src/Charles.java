import java.util.Random;

class Charles implements Runnable {

    Garden garden;
    Random rand = new Random();

    public Charles(Garden g) {
        this.garden = g;
    }

    public void run() {
        try {
            Thread.sleep(rand.nextInt(1000)); // makes the execution more random
            for (int i = 0; i < 10; i++) {
               garden.waitToPlant();
                garden.plant(i);
               Thread.sleep(rand.nextInt(100)); // digging
            }
        } catch (InterruptedException e) {
            System.out.println(e);
            e.printStackTrace();
            System.exit(1);
        }
    }
}
