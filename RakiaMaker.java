package rakia;

public class RakiaMaker extends Person{

    private static int ID_COUNTER = 1;
    Fruits fruit;


    RakiaMaker(Fruits fruit, Village village) {
        super("Bai Ilia-" + ID_COUNTER++, 50, village);
        this.fruit = fruit;
    }

    @Override
    public void run() {
        while(true) {
            Tank tank = village.makeRakia(this);
            if(tank != null){
                try {
                    tank.makeRakia(this);
                } catch (TankException e) {
                    //nothing happens
                }
            }
        }
    }
}
