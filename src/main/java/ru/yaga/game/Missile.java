package ru.yaga.game;

public class Missile {
    private double x, y; // Координаты ракеты
    private double angle; // Угол направления
    private double speed; // Скорость движения (изменяемая)
    private final double turnRate; // Скорость поворота
    private double fuel; // Запас топлива
    private boolean isActive; // Флаг активности

    public Missile(double startX, double startY) {
        this.x = startX;
        this.y = startY;
        this.angle = 0;
        this.speed = 1.0; // Начальная скорость ниже для лучшей инерции
        this.turnRate = Math.toRadians(1.5); // Ограничиваем угол поворота (1.5 градуса за кадр)
        this.fuel = 100;
        this.isActive = true;
    }

    public void update(Target target) {
        if (fuel <= 0) {
            isActive = false;
            return;
        }

        double targetAngle = Math.atan2(target.getY() - y, target.getX() - x);
        double angleDiff = targetAngle - angle;

        // Нормализация угла, чтобы он был в диапазоне -π до π
        angleDiff = (angleDiff + Math.PI) % (2 * Math.PI) - Math.PI;

        // Ограничиваем угол поворота за один кадр для плавного поворота
        if (Math.abs(angleDiff) > turnRate) {
            angle += Math.signum(angleDiff) * turnRate;
        } else {
            angle = targetAngle;
        }

        // Постепенное ускорение ракеты (имитация инерции)
        speed = Math.min(speed + 0.02, 3.0); // Медленное увеличение скорости до 3.0

        // Двигаемся по направлению текущего угла
        x += speed * Math.cos(angle);
        y += speed * Math.sin(angle);

        fuel -= 0.5;
    }

    public boolean hasHitTarget(Target target) {
        double distance = Math.hypot(target.getX() - x, target.getY() - y);
        return distance < 10; // Если расстояние меньше 10 пикселей, считаем попаданием
    }

    public boolean isActive() {
        return isActive;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getAngle() {
        return angle;
    }

    public double getFuel() {
        return this.fuel;
    }

    public void explodeNearTarget(Target target) {
        // Взрыв происходит ближе к цели
        this.x = (this.x + target.getX()) / 2;
        this.y = (this.y + target.getY()) / 2;
    }
}
