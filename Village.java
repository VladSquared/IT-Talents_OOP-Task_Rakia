package rakia;

import com.google.gson.Gson;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Random;

enum Fruits {
    GRAPE(), PLUMS(), APRICOT();

    private double liters = 0;
    private double totalCollected = 0;

    Fruits() {

    }

    void addLiters(double liters) {
        if (liters > 0) {
            this.liters += liters;
        }
    }

    void addCollectedFruit(double kilograms) {
        if (kilograms > 0) {
            this.totalCollected += kilograms;
        }
    }

    double getLiters() {
        return liters;
    }

    public double getTotalCollected() {
        return totalCollected;
    }
}

public class Village {
    private ArrayList<Tank> tanks;
    private Random r = new Random();

    public Village() {
        this.tanks = new ArrayList<>();
    }

    public void startProducingRakia() {
        for (int i = 0; i < 5; i++) {
            Tank tank = new Tank(this, "rakia.Tank-" + (i + 1));
            tanks.add(tank);
        }

        for (int i = 0; i < 7; i++) {
            new Picker(Fruits.values()[r.nextInt(Fruits.values().length)], this).start();//random fruit
        }

        new RakiaMaker(Fruits.GRAPE, this).start();
        new RakiaMaker(Fruits.PLUMS, this).start();
        new RakiaMaker(Fruits.APRICOT, this).start();

        Thread t0 = new Thread(() -> {
            printStatistics();
        });
        t0.setDaemon(true);
        t0.start();
    }

    synchronized void addFruitToATank(Picker picker) {
        boolean successfullyAdded = false;
        for (Tank tank : tanks) {
            try {
                tank.tryToAddFruit(picker, 1.0);
                successfullyAdded = true;
                if (tank.getOccupancy() >= 10) {
                    notifyAll();
                }
                break;
            } catch (TankException e) {
                //nothing happens
            }
        }
        if (!successfullyAdded) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    synchronized Tank makeRakia(RakiaMaker maker) {
        Tank readyTank = null;
        for (Tank tank : tanks) {
            if (tank.getOccupancy() >= 10 && tank.getFruit() == maker.fruit) {
                readyTank = tank;
                break;
            }
        }
        if (readyTank == null) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return readyTank;
    }

    synchronized void notifyEmptyTank() {
        notifyAll();
    }

    private void printStatistics() {
        int refreshPrintRate = 3000;
        File f = new File("statistics");
        f.mkdir();
        int counterFiles = 1;

        while (true) {
            try {
                Thread.sleep(refreshPrintRate);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            File file = new File("statistics/" + counterFiles + "_after_" + refreshPrintRate*counterFiles/1000 + "_seconds.txt");
            counterFiles++;

            Fruits mostOfFruit = Fruits.values()[0];
            for (Fruits fruit : Fruits.values()) {
                if (fruit.getTotalCollected() > mostOfFruit.getTotalCollected()) {
                    mostOfFruit = fruit;
                }
            }

            Fruits mostOfRakia = Fruits.values()[0];
            for (Fruits fruit : Fruits.values()) {
                if (fruit.getLiters() > mostOfFruit.getLiters()) {
                    mostOfRakia = fruit;
                }
            }

            try {
                PrintStream ps = new PrintStream(file);
                ps.println("The most is collected from " + mostOfFruit + " - " + mostOfFruit.getTotalCollected() + " kilograms");
                ps.println("The most is produced from " + mostOfRakia + " RAKIA - " + String.format("%.2f", mostOfRakia.getLiters()) + " Liters");
                ps.println("The Grape / Apricot ratio is: " + String.format("%.2f", Fruits.GRAPE.getLiters() / Fruits.APRICOT.getLiters()));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
