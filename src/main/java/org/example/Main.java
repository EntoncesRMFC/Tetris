package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("毛子方块");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 500);
        frame.setLocation(300, 100);

        GamePanel gamePanel = new GamePanel();
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(gamePanel, BorderLayout.CENTER);

        gamePanel.setFocusable(true);
        gamePanel.requestFocusInWindow();

        frame.setVisible(true);
    }
}