package ru.yaga.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import javax.imageio.ImageIO;

public class GamePanel extends JPanel {
    private Missile missile;
    private Target target;
    private Image rocketImage;
    private Image explosionImage;
    private Image scaledRocketImage;
    private boolean exploded = false;
    private boolean draggingTarget = false;
    private double lastTargetX, lastTargetY;
    private boolean missileLaunched = false;
    private static final double LAUNCH_RANGE = 200.0; // Дальность действия радара

    public GamePanel() {
        this.missile = new Missile(50, 50);
        this.target = new Target(300, 300);
        this.lastTargetX = target.getX();
        this.lastTargetY = target.getY();

        try {
            rocketImage = ImageIO.read(getClass().getResource("/img_2.png"));
            explosionImage = ImageIO.read(getClass().getResource("/img_3.png")); // Загружаем картинку взрыва
            if (rocketImage != null) {
                scaledRocketImage = rocketImage.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            } else {
                System.err.println("Ошибка: изображение ракеты не найдено!");
            }
        } catch (IOException e) {
            System.err.println("Ошибка загрузки изображения");
            e.printStackTrace();
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (Math.hypot(target.getX() - e.getX(), target.getY() - e.getY()) < 10) {
                    draggingTarget = true;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                draggingTarget = false;
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (draggingTarget) {
                    target.setPosition(e.getX(), e.getY());
                    repaint();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Рисуем цель
        g2d.setColor(Color.BLUE);
        g2d.fillOval((int) target.getX() - 5, (int) target.getY() - 5, 10, 10);

        // Рисуем радиус обнаружения радара
        g2d.setColor(new Color(0, 255, 0, 100)); // Полупрозрачный зелёный
        g2d.drawOval((int) missile.getX() - (int) LAUNCH_RANGE, (int) missile.getY() - (int) LAUNCH_RANGE,
                (int) LAUNCH_RANGE * 2, (int) LAUNCH_RANGE * 2);

        // Если ракета взорвалась, рисуем взрыв и выходим
        if (exploded) {
            if (explosionImage != null) {
                g2d.drawImage(explosionImage, (int) missile.getX() - 25, (int) missile.getY() - 25, 50, 50, this);
            } else {
                g2d.setColor(Color.ORANGE);
                g2d.fillOval((int) missile.getX() - 10, (int) missile.getY() - 10, 20, 20);
            }
            return; // Выходим, чтобы не рисовать ракету
        }

        // Рисуем ракету с поворотом
        if (scaledRocketImage != null) {
            AffineTransform transform = new AffineTransform();
            transform.translate(missile.getX() - 25, missile.getY() - 25);
            transform.rotate(missile.getAngle() + Math.PI / 2, 25, 25);
            g2d.drawImage(scaledRocketImage, transform, this);
        } else {
            g2d.setColor(Color.RED);
            g2d.fillOval((int) missile.getX() - 5, (int) missile.getY() - 5, 10, 10);
        }
    }


    public void updateGame() {
        double distanceToTarget = Math.hypot(missile.getX() - target.getX(), missile.getY() - target.getY());

        // Запуск ракеты, если цель в зоне досягаемости
        if (!missileLaunched && distanceToTarget <= LAUNCH_RANGE) {
            missileLaunched = true;
            System.out.println("Ракета запущена!");
        }

        if (missileLaunched && !exploded) {
            missile.update(target);

            // Проверяем, достигла ли ракета цели
            if (missile.hasHitTarget(target)) {
                exploded = true;
                missileLaunched = false;
                System.out.println("Ракета поразила цель!");
            }

            // Проверяем, если топлива не хватает для дальнейшего движения
            if (missile.getFuel() <= 0) {
                exploded = true;
                missileLaunched = false;
                System.out.println("Ракета самоуничтожилась из-за полного расхода топлива!");
            }

            // Проверяем, сместилась ли цель и хватит ли топлива на манёвр
            double targetMovement = Math.hypot(lastTargetX - target.getX(), lastTargetY - target.getY());
            System.out.println("Текущий уровень топлива: " + missile.getFuel());
            System.out.println("Смещение цели: " + targetMovement);

            if (targetMovement > 0 && missile.getFuel() < distanceToTarget / 5) {
                exploded = true;
                missileLaunched = false;
                System.out.println("Ракета самоуничтожилась из-за нехватки топлива при манёвре!");
            }

            lastTargetX = target.getX();
            lastTargetY = target.getY();
        }

        repaint();
    }

}
