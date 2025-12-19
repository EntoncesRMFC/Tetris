package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.concurrent.atomic.AtomicLong;

public class GamePanel extends JPanel {
    int bWidth = 20;
    int bHeight = 20;
    Controller controller = new Controller();
    private boolean gameRunning = true;
    private int score = 0;

    public GamePanel() {
        this.setPreferredSize(new Dimension(200, 400));
        this.setBackground(Color.pink);

        // 设置焦点并添加键盘监听
        this.setFocusable(true);
        this.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                keyPressedHandler(e);
            }
        });

        // 游戏主循环线程
        AtomicLong lastTimeBonus = new AtomicLong(System.currentTimeMillis());

        // 在游戏主循环线程中添加时间检查
        new Thread(() -> {
            while (gameRunning) {
                controller.down();
                repaint();

                // 每秒加5分
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastTimeBonus.get() >= 1000) {
                    score += 5;
                    lastTimeBonus.set(currentTime);
                    repaint();
                }

                // 检查游戏是否结束
                if (!controller.isValid(controller.currentX, controller.currentY)) {
                    gameRunning = false;
                    int sumScore = score + controller.getScore();
                    JOptionPane.showMessageDialog(GamePanel.this,
                            "Game Over！\nYour score is " + sumScore,
                            "Game Over",
                            JOptionPane.INFORMATION_MESSAGE);
                }

                try {
                    Thread.sleep(controller.getSpeed());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    // 绘制网格线
    public void drawTableLines(Graphics g) {
        g.setColor(Color.gray);
        // 绘制垂直线
        for (int i = 0; i <= 10; i++) {
            g.drawLine(i * bWidth, 0, i * bWidth, 20 * bHeight);
        }
        // 绘制水平线
        for (int i = 0; i <= 20; i++) {
            g.drawLine(0, i * bHeight, 10 * bWidth, i * bHeight);
        }
    }

    // 绘制固定的方块
    public void drawFix(int[][] fix, Graphics g) {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 20; j++) {
                if (fix[i][j] == 1) {
                    g.setColor(Color.green);
                    g.fillRect(i * bWidth + 1, j * bHeight + 1, bWidth - 1, bHeight - 1);
                    // 添加3D效果
                    g.setColor(Color.green.darker());
                    g.drawRect(i * bWidth + 1, j * bHeight + 1, bWidth - 1, bHeight - 1);
                }
            }
        }
    }

    // 绘制当前下落的方块
    public void drawBlocks(int[] block, int x, int y, Graphics g) {
        Color blockColor = controller.getCurrentBlockColor();
        g.setColor(blockColor);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (block[i * 4 + j] == 1) {
                    int drawX = (x + j) * bWidth;
                    int drawY = (y + i) * bHeight;
                    g.fillRect(drawX + 1, drawY + 1, bWidth - 1, bHeight - 1);

                    // 添加高光效果
                    g.setColor(blockColor.brighter());
                    g.drawLine(drawX + 1, drawY + 1, drawX + bWidth - 2, drawY + 1);
                    g.drawLine(drawX + 1, drawY + 1, drawX + 1, drawY + bHeight - 2);

                    // 添加阴影效果
                    g.setColor(blockColor.darker());
                    g.drawLine(drawX + bWidth - 2, drawY + 1, drawX + bWidth - 2, drawY + bHeight - 2);
                    g.drawLine(drawX + 1, drawY + bHeight - 2, drawX + bWidth - 2, drawY + bHeight - 2);

                    g.setColor(blockColor);
                }
            }
        }
    }

    // 绘制分数
    public void drawScore(Graphics g) {
        int sumScore = score + controller.getScore();
        g.setColor(Color.black);
        g.setFont(new Font("MicrosoftYahei", Font.BOLD, 14));
        g.drawString("Score: " + sumScore, 10, 20);
        g.drawString("Level: " + controller.getLevel(), 10, 40);
    }

    // 绘制预览方块
    public void drawNextBlock(Graphics g) {
        int[] nextBlock = controller.getNextBlock();
        Color nextColor = controller.getNextBlockColor();
        g.setColor(nextColor);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("Next:", 220, 20);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (nextBlock[i * 4 + j] == 1) {
                    int drawX = 220 + j * (bWidth - 5);
                    int drawY = 40 + i * (bHeight - 5);
                    g.fillRect(drawX, drawY, bWidth - 5, bHeight - 5);

                    // 边框
                    g.setColor(Color.black);
                    g.drawRect(drawX, drawY, bWidth - 5, bHeight - 5);
                    g.setColor(nextColor);
                }
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 绘制背景
        g.setColor(Color.pink);
        g.fillRect(0, 0, getWidth(), getHeight());

        // 绘制游戏区域背景
        g.setColor(Color.white);
        g.fillRect(0, 0, 10 * bWidth, 20 * bHeight);

        // 绘制固定方块
        drawFix(controller.fix, g);

        // 绘制当前方块
        drawBlocks(controller.block, controller.currentX, controller.currentY, g);

        // 绘制网格线
        drawTableLines(g);

        // 绘制分数和预览
        drawScore(g);
        drawNextBlock(g);

        // 绘制边框
        g.setColor(Color.black);
        g.drawRect(0, 0, 10 * bWidth - 1, 20 * bHeight - 1);
    }

    // 键盘事件处理
    private void keyPressedHandler(KeyEvent e) {
        if (!gameRunning) return;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                controller.left();
                break;
            case KeyEvent.VK_RIGHT:
                controller.right();
                break;
            case KeyEvent.VK_UP:
                controller.turn();
                break;
            case KeyEvent.VK_DOWN:
                controller.down();
                //score += 1; // 加速下落加分
                break;
            case KeyEvent.VK_SPACE:
                controller.hardDrop();
                //score += 10; // 硬降加分
                break;
            case KeyEvent.VK_P:
                gameRunning = !gameRunning; // 暂停/继续
                break;
        }
        repaint();
    }

}