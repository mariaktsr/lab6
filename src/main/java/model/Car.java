package model;

//Класс автомобиля

public class Car {
    private String name; //Поле может быть null

    public Car(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        if (name != null) {
            return name;
        } else {
            return "null";
        }
    }
}