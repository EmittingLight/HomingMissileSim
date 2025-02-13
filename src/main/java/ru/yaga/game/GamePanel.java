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

    public GamePanel() {
        this.missile = new Missile(50, 50);
        this.target = new Target(300, 300);

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
            public void mouseClicked(MouseEvent e) {
                target.setPosition(e.getX(), e.getY()); // Передвигаем цель по клику мыши
                exploded = false; // Сбрасываем взрыв при новом запуске
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

        // Проверяем, произошёл ли взрыв
        if (exploded) {
            if (explosionImage != null) {
                g2d.drawImage(explosionImage, (int) missile.getX() - 25, (int) missile.getY() - 25, 50, 50, this);
            } else {
                g2d.setColor(Color.ORANGE);
                g2d.fillOval((int) missile.getX() - 10, (int) missile.getY() - 10, 20, 20);
            }
        } else {
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
    }

    public void updateGame() {
        if (!exploded) {
            missile.update(target);
            // Проверяем, достигла ли ракета цели
            if (missile.hasHitTarget(target)) {
                exploded = true;
            }
        }
        repaint();
    }
}
