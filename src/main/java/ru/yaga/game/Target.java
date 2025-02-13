package ru.yaga.game;

public class Target {
    private double x, y;

    public Target(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    // Метод для создания копии цели с прогнозируемыми координатами
    public Target getPredictedTarget(double predictedX, double predictedY) {
        return new Target(predictedX, predictedY);
    }
}


