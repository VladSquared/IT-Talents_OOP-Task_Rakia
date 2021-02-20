package rakia;

class Picker extends Person {

    private static int ID_COUNTER = 1;
    Fruits fruit;


    public Picker(Fruits fruit, Village village) {
        super(("Joro-" + ID_COUNTER++), 25, village);
        this.fruit = fruit;
    }

    Fruits getFruit() {
        return fruit;
    }

    @Override
    public void run() {
        while (true) {

            village.addFruitToATank(this);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
