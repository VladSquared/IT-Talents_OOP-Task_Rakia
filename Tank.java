package rakia;

import java.util.Random;

public class Tank {
    private Village village;
    private Fruits fruit;
    private double occupancy;
    private boolean isMakingRakia;
    String name;

    Tank(Village village, String name) {
        this.name = name;
        this.fruit = null;
        this.occupancy = 0;
        this.isMakingRakia = false;
        this.village = village;
    }

    double getOccupancy() {
        return occupancy;
    }

    public Fruits getFruit() {
        return fruit;
    }

    void tryToAddFruit(Picker picker, Double amount) throws TankException {
        if (isMakingRakia) {
            throw new TankException("Making rakia now..!");
        }
        addFruit(picker, amount);
    }

    synchronized void addFruit(Picker picker, Double amount) throws TankException {

        if (this.fruit == null) {
            this.fruit = picker.getFruit();
        }
        if (isMakingRakia || this.fruit != picker.getFruit()) {
            throw new TankException("Wrong fruit type tank or the tank is making rakia");
        }

        occupancy += amount;
        fruit.addCollectedFruit(amount);
        System.out.println(amount + " kg added to " + this.name + " from " + picker.getPersonName() +
                " and total occupancy is " + this.occupancy);

    }

    synchronized void makeRakia(RakiaMaker rakiaMaker) throws TankException {
        if (occupancy < 10) {
            throw new TankException("The tank is not ready yet");
        }
        isMakingRakia = true;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.getStackTrace();
        }
        double producedLiters = new Random().nextDouble() * this.occupancy;
        fruit.addLiters(producedLiters);
        System.out.println(rakiaMaker.getPersonName() + " made " + String.format("%.2f", producedLiters) + "L of " + this.fruit + " rakia for the citizens");
        fruit = null;
        occupancy = 0;
        isMakingRakia = false;
        village.notifyEmptyTank();
    }
}
