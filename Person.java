package rakia;

abstract class Person extends Thread{
    private String name;
    private int age;
    Village village;

    public Person(String name, int age, Village village) {
        this.name = name;
        this.age = age;
        this.village = village;
    }

    public String getPersonName() {
        return name;
    }
}
