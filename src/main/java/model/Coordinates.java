package model;

//Класс координат

public class Coordinates {
    private double x;
    private Long y; //Значение поля должно быть больше -228, Поле не может быть null

    public Coordinates(double x, Long y) {
        if (y == null || y <= -228) {
            throw new IllegalArgumentException("Координата Y не может быть null и должна быть больше -228");
        }
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }
    public Long getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }
    public void setY(Long y) {
        if (y == null || y <= -228) {
            throw new IllegalArgumentException("Координата Y не может быть null и должна быть больше -228");
        }
        this.y = y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}