package ru.yaga.game;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Homing Missile Simulation");
        GamePanel panel = new GamePanel();
        frame.add(panel);
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        new Timer(30, e -> panel.updateGame()).start();
    }
}

