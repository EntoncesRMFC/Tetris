package org.example;

import java.awt.*;
import java.util.Random;

public class Controller {
    int[][] fix = new int[10][20];
    private Random random = new Random();

    public void setScore(int score) {
        this.score = score;
    }

    private int score = 0;
    private int level = 1;
    private int linesCleared = 0;

    // 7种俄罗斯方块形状
    private int[][][] shapes = {
            // I
            {
                    {0,0,0,0, 1,1,1,1, 0,0,0,0, 0,0,0,0},
                    {0,0,1,0, 0,0,1,0, 0,0,1,0, 0,0,1,0}
            },
            // J
            {
                    {1,0,0,0, 1,1,1,0, 0,0,0,0, 0,0,0,0},
                    {0,1,1,0, 0,1,0,0, 0,1,0,0, 0,0,0,0},
                    {0,0,0,0, 1,1,1,0, 0,0,1,0, 0,0,0,0},
                    {0,1,0,0, 0,1,0,0, 1,1,0,0, 0,0,0,0}
            },
            // L
            {
                    {0,0,1,0, 1,1,1,0, 0,0,0,0, 0,0,0,0},
                    {0,1,0,0, 0,1,0,0, 0,1,1,0, 0,0,0,0},
                    {0,0,0,0, 1,1,1,0, 1,0,0,0, 0,0,0,0},
                    {1,1,0,0, 0,1,0,0, 0,1,0,0, 0,0,0,0}
            },
            // O
            {
                    {0,0,0,0, 0,1,1,0, 0,1,1,0, 0,0,0,0}
            },
            // S
            {
                    {0,0,0,0, 0,1,1,0, 1,1,0,0, 0,0,0,0},
                    {0,1,0,0, 0,1,1,0, 0,0,1,0, 0,0,0,0}
            },
            // T
            {
                    {0,0,0,0, 1,1,1,0, 0,1,0,0, 0,0,0,0},
                    {0,1,0,0, 1,1,0,0, 0,1,0,0, 0,0,0,0},
                    {0,1,0,0, 1,1,1,0, 0,0,0,0, 0,0,0,0},
                    {0,1,0,0, 0,1,1,0, 0,1,0,0, 0,0,0,0}
            },
            // Z
            {
                    {0,0,0,0, 1,1,0,0, 0,1,1,0, 0,0,0,0},
                    {0,0,1,0, 0,1,1,0, 0,1,0,0, 0,0,0,0}
            }
    };

    // 方块颜色
    private Color[] colors = {
            Color.cyan,    // I
            Color.blue,    // J
            Color.orange,  // L
            Color.yellow,  // O
            Color.green,   // S
            Color.magenta, // T
            Color.red      // Z
    };

    int currentShape;
    int currentRotation;
    int[] block;
    Color currentColor;

    int nextShape;
    int nextRotation;
    int[] nextBlock;
    Color nextColor;

    int currentX = 3;
    int currentY = 0;

    public Controller() {
        // 初始化当前方块和下一个方块
        currentShape = random.nextInt(shapes.length);
        currentRotation = 0;
        block = shapes[currentShape][currentRotation].clone();
        currentColor = colors[currentShape];

        generateNextBlock();
    }

    // 生成下一个方块
    private void generateNextBlock() {
        nextShape = random.nextInt(shapes.length);
        nextRotation = 0;
        nextBlock = shapes[nextShape][nextRotation].clone();
        nextColor = colors[nextShape];
    }

    // 获取下一个方块
    public int[] getNextBlock() {
        return nextBlock;
    }

    public Color getNextBlockColor() {
        return nextColor;
    }

    public Color getCurrentBlockColor() {
        return currentColor;
    }

    // 检查位置是否有效
    public boolean isValid(int x, int y) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (block[i * 4 + j] == 1) {
                    int boardX = x + j;
                    int boardY = y + i;

                    // 检查边界
                    if (boardX < 0 || boardX >= 10 || boardY >= 20) {
                        return false;
                    }

                    // 检查是否与已固定的方块重叠
                    if (boardY >= 0 && fix[boardX][boardY] == 1) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // 检查旋转是否有效
    public boolean isValidRotation(int[] newBlock, int x, int y) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (newBlock[i * 4 + j] == 1) {
                    int boardX = x + j;
                    int boardY = y + i;

                    if (boardX < 0 || boardX >= 10 || boardY >= 20 ||
                            (boardY >= 0 && fix[boardX][boardY] == 1)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // 将当前方块固定到游戏区域
    public void addfix() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (block[i * 4 + j] == 1) {
                    int x = currentX + j;
                    int y = currentY + i;
                    if (y >= 0) { // 确保y坐标有效
                        fix[x][y] = 1;
                    }
                }
            }
        }
        checkLines();
    }

    // 检查并消除完整的行
    private void checkLines() {
        int linesRemoved = 0;

        for (int y = 19; y >= 0; y--) {
            boolean lineComplete = true;
            for (int x = 0; x < 10; x++) {
                if (fix[x][y] == 0) {
                    lineComplete = false;
                    break;
                }
            }

            if (lineComplete) {
                // 消除这一行
                for (int yy = y; yy > 0; yy--) {
                    for (int x = 0; x < 10; x++) {
                        fix[x][yy] = fix[x][yy - 1];
                    }
                }
                // 清空最上面一行
                for (int x = 0; x < 10; x++) {
                    fix[x][0] = 0;
                }

                linesRemoved++;
                linesCleared++;
                y++; // 重新检查当前行，因为上面的行下移了
            }
        }

        // 更新分数和等级
        if (linesRemoved > 0) {
            // 标准计分：考虑等级加成
            int baseScore = switch (linesRemoved) {
                case 1 -> 100;
                case 2 -> 300;
                case 3 -> 500;
                case 4 -> 800;
                default -> 0;
            };
            score += baseScore * level;  // 分数随等级提高

            level = linesCleared / 10 + 1;  // 每消10行升一级

            // 如果需要，可以更新界面显示
            System.out.println("DEBUG: 消行=" + linesRemoved +
                    " 加分=" + (baseScore * level) +
                    " 总分=" + score);
        }
    }

    // 下落一格
    public void down() {
        if (isValid(currentX, currentY + 1)) {
            currentY++;
        } else {
            if (currentY <= 0) {
                // 游戏结束
                return;
            }
            addfix();
            // 使用下一个方块
            currentShape = nextShape;
            currentRotation = nextRotation;
            block = nextBlock.clone();
            currentColor = nextColor;

            generateNextBlock();

            currentX = 3;
            currentY = 0;
        }
    }

    // 向左移动
    public void left() {
        if (isValid(currentX - 1, currentY)) {
            currentX--;
        }
    }

    // 向右移动
    public void right() {
        if (isValid(currentX + 1, currentY)) {
            currentX++;
        }
    }

    // 旋转方块
    public void turn() {
        int nextRotation = (currentRotation + 1) % shapes[currentShape].length;
        int[] newBlock = shapes[currentShape][nextRotation].clone();

        // 尝试旋转
        if (isValidRotation(newBlock, currentX, currentY)) {
            block = newBlock;
            currentRotation = nextRotation;
        } else {
            // 尝试墙踢（wall kick）：左右移动一格再旋转
            for (int offset : new int[]{-1, 1, -2, 2}) {
                if (isValidRotation(newBlock, currentX + offset, currentY)) {
                    block = newBlock;
                    currentRotation = nextRotation;
                    currentX += offset;
                    break;
                }
            }
        }
    }

    // 硬降（直接落到底部）
    public void hardDrop() {
        while (isValid(currentX, currentY + 1)) {
            currentY++;
        }
        addfix();
        currentShape = nextShape;
        currentRotation = nextRotation;
        block = nextBlock.clone();
        currentColor = nextColor;
        generateNextBlock();
        currentX = 3;
        currentY = 0;
    }

    // 获取当前下落速度（根据等级）
    public int getSpeed() {
        return Math.max(50, 500 - (level - 1) * 50);
    }

    // 获取当前等级
    public int getLevel() {
        return level;
    }

    // 获取当前分数
    public int getScore() {
        return score;
    }
}