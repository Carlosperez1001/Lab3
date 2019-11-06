import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Garden {

    final Lock lock = new ReentrantLock();
    final Condition canNotDig  = lock.newCondition(); //J waits until 5 holes are unfilled
    final Condition canDig  = lock.newCondition(); //J waits until 5 holes are unfilled


    final Condition  canPlant = lock.newCondition();// C waits until holes is availbe
    final Condition  canNotPlant = lock.newCondition();// C waits until holes is availbe

    final Condition  canFill = lock.newCondition();// C waits until holes is availbe
    final Condition  canNotFill= lock.newCondition();// C waits until holes is availbe

    int holeCount = 0;
    private String[] plots = new String[10];


    private  int countHoles() {
        holeCount = 0;
        for (int i = 0; i < 10; i++) {
            if (plots[i] == "hole") {
                holeCount += 1;
            }
        }
    return  holeCount;
    }

    private  boolean plantedInPast() {
        boolean pastPlant = false;
        for (int i = 0; i < 10; i++) {
            if (plots[i] == "plant") {
                pastPlant=true;
            }
        }
        return  pastPlant;
    }

/*---- Jordan Tasks ----*/
    public void waitToDig()throws InterruptedException {
        countHoles();
        lock.lock();
        try{
            while(holeCount == 5 ){
                System.out.println("Jordan is waiting");
                canNotDig.await();
            }
            canFill.signal();
            canPlant.signal();


        }finally {
            lock.unlock();
        }
    }

    public void dig(int plotPosition) throws InterruptedException {
        countHoles();
        lock.lock();
        try{
            while(holeCount >= 5 ){ // J Can not dig
                canDig.await();
            }
                plots[plotPosition] = "hole";
                System.out.printf("%-12s%-12s%-12s%-12s\n", "Jordan dug a hole ", "", "", (++plotPosition));
                Thread.sleep(0);

                canNotDig.signal();

        }finally {
            lock.unlock();
        }

    }


/*---- Charles Tasks ----*/

    public void waitToPlant()throws InterruptedException {
        countHoles();
        lock.lock();
        try{
            while(plantedInPast()==true || holeCount ==0){
                canPlant.await();
                System.out.println("Charles is waiting");
            }

            canFill.signal();
            canDig.signal();
        }finally {
            lock.unlock();
        }
    }

    public void plant(int plotPosition) throws InterruptedException {
        countHoles();
        lock.lock();
        try{
            while(plantedInPast() == true || holeCount == 0 ){
                canNotPlant.await();
            }

            plots[plotPosition] = "plant";
            System.out.printf("%-12s%-12s%-12s%-12s\n", "Charles planted a hole \t\t", (++plotPosition), "", "");
            Thread.sleep(0);

            canNotPlant.signal();

        }finally {
            lock.unlock();
        }

    }


    public void waitToFill(int plotPosition)throws InterruptedException {
        countHoles();
        lock.lock();
        try{
            while(plots[plotPosition] !="plant"){
                System.out.println("Tracy is waiting");
                canNotFill.await();
            }
            canPlant.signal();
            canDig.signal();


        }finally {
            lock.unlock();
        }
    }

    public void fill(int plotPosition) throws InterruptedException {
        countHoles();
        lock.lock();
        try{
            while(plots[plotPosition] !="plant" ){
                canFill.await();
            }
            plots[plotPosition] = "Fill";
            System.out.printf("%-12s%-12s%-12s%-12s\n", "Tracy  filled a hole.", "", (++plotPosition), "");
            Thread.sleep(0);

            canNotFill.signal();

        }finally {
            lock.unlock();
        }

    }
}

