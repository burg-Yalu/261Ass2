package org.example;

import java.awt.*;
import java.awt.event.KeyEvent;
import javax.swing.*;

public class HitStone extends GameEngine {

    // 游戏状态枚举
    private enum GameState {
        RUNNING, PAUSED, GAME_OVER
    }

    private GameTimer gameTimer;
    private GameState gameState = GameState.RUNNING;
    private boolean statusWindowVisible = false;
    private int score = 0;
    // 创建游戏
    public static void main(String[] args) {
        createGame(new HitStone());
    }

    // 声明
    Image BackgroundImage = loadImage("src/main/resources/background.png");
    Image bat_normal_middle = loadImage("src/main/resources/bat_large.png");
    Image bat_long_middle = loadImage("src/main/resources/bat_huge.png");
    Image bat_short_middle = loadImage("src/main/resources/bat_small.png");
    boolean gameover;

    // Bat
    double batx;
    double baty;
    double batvx;
    double batvy;
    int batDirection;
    boolean batToStopBall;

    int batstatus; // 0,1,2,3 表示不同形式的bat
    double batWidth;

    public void initBat() {
        batx = 350;
        baty = 600;
        batvx = 0;
        batvy = 0;
        batstatus = 0;
        batDirection = 0;
        batToStopBall = false;
    }

    public void updateBat(double dt) {
        batx += batvx * dt;

        if (batDirection == 1) {
            batvx = -90;
        } else if (batDirection == 2) {
            batvx = 90;
        } else if (batDirection == 0) {
            batvx = 0;
        }

        if (batx - (batWidth / 2) <= 0 && batDirection == 1) {
            batvx = 0;
        } else if (batx + (batWidth / 2) >= 700 && batDirection == 2) {
            batvx = 0;
        }

        batx += batvx * dt;

        //bat status
        if (batstatus == 0) {
            batWidth = 100;
        } else if (batstatus == 1) {
            batWidth = 150;
        } else if (batstatus == 2) {
            batWidth = 50;
        }

    }

    public void drawBat() {
        saveCurrentTransform();
        translate(batx, baty);
        //drawImage(bat_normal_middle, -batWidth / 2, 0, batWidth, 16);

        if (batstatus == 0) {
            drawImage(bat_normal_middle, -batWidth/2, 0, batWidth, 16);
        } else if (batstatus == 1) {
            drawImage(bat_long_middle, -batWidth/2, 0, batWidth, 16);
        } else if (batstatus == 2) {
            drawImage(bat_short_middle, -batWidth/2, 0, batWidth, 16);
        }

        restoreLastTransform();
    }
    public void drawTimeScore(double x, double y) {
        // Draw text on the screen
        mGraphics.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
        mGraphics.drawString("Time: " + gameTimer.getFormattedTime(),  (int)x, (int)y);
        int nextY = (int)y + mGraphics.getFontMetrics().getHeight();
        mGraphics.drawString("Score: " + score,  (int)x, nextY);
    }
    // Ball
    Image[] ball;
    int ballMax;
    double[] ballX;
    double[] ballY;
    double[] ballVX;
    double[] ballVY;
    double[] distanceX;
    int[] ballType; // 不同球的效果
    boolean[] ballActive;

    public void initBall() {
        ball = new Image[4];
        ball[0] = loadImage("src/main/resources/ball_blue.png");
        ball[1] = loadImage("src/main/resources/ball_pinball.png");
        ball[2] = loadImage("src/main/resources/ball_purple.png");
        ball[3] = loadImage("src/main/resources/ball_red.png");
        ballMax = 9;

        ballX = new double[ballMax];
        ballY = new double[ballMax];
        ballVX = new double[ballMax];
        ballVY = new double[ballMax];
        distanceX = new double[ballMax];
        ballType = new int[ballMax];
        ballActive = new boolean[ballMax];

        for (int i = 0; i < ballMax; i++) {
            ballActive[i] = false;
        }
        // 启用一个新球
        ballActive[0] = true;
        ballX[0] = batx;
        ballY[0] = baty - 15;
    }

    public void updateBall(double dt) {
        for (int i = 0; i < ballMax; i++) {
            if (ballActive[i]) {
                if (!(!batToStopBall && ballX[i] + 8 >= batx - batWidth / 2 && ballX[i] + 8 <= batx + batWidth / 2 && ballY[i] + 16 >= baty)) {
                    ballX[i] += ballVX[i] * dt;
                    ballY[i] += ballVY[i] * dt;
                } else {
                    ballX[i] = batx - distanceX[i];
                    ballY[i] = baty - 16;
                }
            }
            // 触板反弹
            if (!batToStopBall && ballX[i] + 8 >= batx - batWidth / 2 && ballX[i] + 8 <= batx + batWidth / 2 && ballY[i] + 16 >= baty) {
                distanceX[i] = batx - ballX[i];
                ballVX[i] = 0;
                ballVY[i] = 0;
                ballX[i] = batx - distanceX[i];
                ballY[i] = baty - 16;
            } else if (batToStopBall && ballX[i] + 8 >= batx - batWidth / 2 && ballX[i] + 8 <= batx + batWidth / 2 && ballY[i] + 16 >= baty) {
                double l = length(ballX[i] + 8 - batx, 16);
                ballVX[i] = 240 * (ballX[i] + 8 - batx) / l;
                ballVY[i] = -240 * (16) / l;
            }
            if (ballX[i] <= 0 || ballX[i] + 16 >= 700) {
                ballVX[i] *= -1;
            }
            if (ballY[i] <= 0) {
                ballVY[i] *= -1;
            } else if (ballY[i] >= 700) {
                gameover = true;
            }
        }
    }

    // 球的中心是 x+8, y+8
    public void drawBall() {
        for (int i = 0; i < ballMax; i++) {
            if (ballActive[i]) {
                saveCurrentTransform();
                translate(ballX[i], ballY[i]);
                drawImage(ball[0], 0, 0, 16, 16);
                restoreLastTransform();
            }
        }
    }



    //bricks
    Image[] bule1;
    Image[] green;
    Image[] orange;

    Image[] pink;
    Image[] purple;
    double[] brickX;
    double[] brickY;
    int[] brickType;
    int[] brickLife;
    double[] brickTimer;
    boolean[] brickActive;
    boolean[] brickBreak;


    public void initBrick(){

        brickX = new double[100];
        brickY = new double[100];
        brickTimer = new double[100];
        brickActive = new boolean[100];
        brickType = new int[100];
        brickLife = new int[100];
        brickBreak = new boolean[100];

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                brickActive[10*i+j] = true;
                brickType[10*i+j] = 2;
                brickX[10*i+j] = 60.0 * j + 75;
                brickY[10*i+j] = 30 * i + 30;
                brickLife[10*i+j] = 1;
                brickBreak[10*i+j] = false;
            }
        }


        //bule can be defeated by twice hits
        bule1 = new Image[7];
        bule1[0] = loadImage("src/main/resources/Stones/stone_normal_blue.png");
        bule1[1] = loadImage("src/main/resources/Stones/stone_damaged2_blue.png");
        for (int i = 2; i < 7; i++) {
            bule1[i] = subImage(loadImage("src/main/resources/Stones/stone_breaking_blue_strip5.png"),65*(i-2),0,60,30);
        }
        // green can explosion
        green = new Image[6];
        green[0] = loadImage("src/main/resources/Stones/stone_normal_blue.png");
        for (int i = 1; i < 6; i++) {
            green[i] = subImage(loadImage("src/main/resources/Stones/stone_breaking_green_strip5.png"),65*(i-1),0,60,30);
        }
        //orange
        orange = new Image[6];
        orange[0] = subImage(loadImage("src/main/resources/Stones/stone_normal_orange.png"),5,4,54,25);
        for (int i = 1; i < 6; i++) {
            orange[i] = subImage(loadImage("src/main/resources/Stones/stone_breaking_orange_strip5.png"),65*(i-1),0,60,30);
        }
        //pink
        pink = new Image[6];
        pink[0] = loadImage("src/main/resources/Stones/pink_minus.jpg");
        for (int i = 1; i < 6; i++) {
            pink[i] = subImage(loadImage("src/main/resources/Stones/stone_breaking_pink_strip5.png"),65*(i-1),0,60,30);
        }
        //purple
        purple = new Image[6];
        purple[0] = loadImage("src/main/resources/Stones/stone_life_purple.png");
        for (int i = 1; i < 6; i++) {
            purple[i] = subImage(loadImage("src/main/resources/Stones/stone_breaking_purple_strip5.png"),65*(i-1),0,60,30);
        }
    }
    public void updateBrick(double dt){
        for (int i = 0; i < 100; i++) {
            if (brickActive[i]) {
                for (int j = 0; j < ballMax; j++) {
                    if (ballActive[j]) {
//                        if ((abs(ballX[j] + 8 - (brickX[i] - 30)) < 8 || abs(ballX[j] + 8 - (brickX[i] + 30)) < 8) && ballY[j] + 16 > (brickY[i] - 15) && ballY[j] < (brickY[i] + 15)) {
//                            brickActive[i] = false;
//                            ballVX[j] *= -1;
//                            break;
//                        }
//                        if ((abs(ballY[j] + 8 - (brickY[i] - 15)) < 8 || abs(ballY[j] + 8 - (brickY[i] + 15)) < 8) && ballX[j] + 16 > (brickX[i] - 30) && ballX[j] < (brickX[i] + 30)) {
//                            brickActive[i] = false;
//                            ballVY[j] *= -1;
//                            break;
//                        }
                        if((abs(brickX[i]-30-(ballX[j]+8))<8||abs(ballX[j]+8-(brickX[i]+30))<8) && abs(ballY[j]+8-(brickY[i]))<15){
                            brickLife[i] --;
                            ballVX[j] *= -1;
                        }else if((abs(brickY[i]-15-(ballY[j]+8))<8||abs(ballY[j]+8-(brickY[i]+15))<8) && abs(ballX[j]+8-(brickX[i]))<30){
                            brickLife[i] --;
                            ballVY[j] *= -1;
                        }
                        if (brickLife[i]==0){
                            brickActive[i] = false;
                            brickTimer[i] = 0;
                        }else if (brickLife[i]<0){
                            brickActive[i] = false;
                        }
                    }
                }
            }else brickTimer[i] += dt;
            if (brickTimer[i]>0.5 && brickLife[i]==0){
                brickLife[i]--;
            }
        }
    }
    public int getAnimationFrame(double timer, double duration, int numFrames) {
        // Get frame
        int i = (int)floor(((timer % duration) / duration) * numFrames);
        // Check range
        if(i >= numFrames) {
            i = numFrames-1;
        }
        // Return
        return i;
    }
    public void drawBrik(){

        for (int i = 0; i < 100; i++) {
            if (brickActive[i]) {
                saveCurrentTransform();
                translate(brickX[i], brickY[i]);
                if (brickType[i] == 2) {
                    drawImage(orange[0], -30, -15, 60, 30);
                }
                restoreLastTransform();
            }else if (brickLife[i] == 0){
                saveCurrentTransform();
                translate(brickX[i], brickY[i]);
                int j = getAnimationFrame(brickTimer[i], 0.5, 5);
                if (brickType[i]==0){
                    drawImage(bule1[j],-30, -15, 60, 30);
                }else if (brickType[i]==1){
                    drawImage(green[j],-30, -15, 60, 30);
                }else if(brickType[i]==2){
                    drawImage(orange[j],-30, -15, 60, 30);
                }
                restoreLastTransform();
            }
        }
    }



    // 初始化游戏
    public void init() {
        setWindowSize(700, 700);
        initBat();
        initBall();
        initBrick(); // 添加这行代码以初始化砖块
        batWidth = 90;

        // 初始化计时器
        gameTimer = new GameTimer();
        gameTimer.start();
        score = 0;
    }

    // 更新游戏
    @Override
    public void update(double dt) {
        if (gameState == GameState.RUNNING) {
            updateBat(dt);
            updateBall(dt);
            updateBrick(dt);
        }
    }

    // 渲染游戏
    @Override
    public void paintComponent() {
        drawImage(BackgroundImage, 0, 0, 700, 700);
        drawTimeScore(20,20);

        drawBat();
        drawBall();
        drawBrik();



    }

    // 按键监听器
    public void keyPressed(KeyEvent e) {
        // 用户按下左箭头
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            batDirection = 1;
        }
        // 用户按下右箭头
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            batDirection = 2;
        }
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            batToStopBall = true;
        }
        // 用户按下 'P' 键暂停游戏
        if (e.getKeyCode() == KeyEvent.VK_P) {
            if (gameState == GameState.RUNNING) {
                gameState = GameState.PAUSED;
                gameTimer.stop(); // 暂停计时器
                showGameStatus("Game Paused, click OK to restart");
            }
        }
        // 用户按下 'R' 键重启游戏
        if (e.getKeyCode() == KeyEvent.VK_R) {
            init();
            gameState = GameState.RUNNING;
            showGameStatus("Game Restarted, click OK to continue");
        }
    }

    private void showGameStatus(String status) {
        if (!statusWindowVisible) { // 检查状态窗口是否已经可见
            statusWindowVisible = true; // 将状态设置为窗口已显示
            new GameStatusWindow(status, new GameStatusWindow.GameStatusCallback() {
                @Override
                public void onOKClicked() {
                    // 关闭游戏状态窗口后继续游戏
                    if (gameState == GameState.PAUSED) {
                        gameState = GameState.RUNNING;
                        gameTimer.start(); // 重新启动计时器
                    }
                    statusWindowVisible = false; // 将状态设置为窗口已关闭
                }
            });
        }
    }

    public void keyReleased(KeyEvent e) {
        // 用户释放左箭头或右箭头
        if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) {
            batDirection = 0;
        }
    }

    // 内部计时器类
    private class GameTimer {
        private long startTime;
        private long elapsedTime;
        private boolean running;

        public GameTimer() {
            reset();
        }

        public void start() {
            if (!running) {
                startTime = System.currentTimeMillis() - elapsedTime;
                running = true;
            }
        }

        public void stop() {
            if (running) {
                elapsedTime = System.currentTimeMillis() - startTime;
                running = false;
            }
        }

        public void reset() {
            startTime = 0;
            elapsedTime = 0;
            running = false;
        }

        public long getElapsedTime() {
            if (running) {
                return System.currentTimeMillis() - startTime;
            } else {
                return elapsedTime;
            }
        }

        public String getFormattedTime() {
            long millis = getElapsedTime();
            long seconds = (millis / 1000) % 60;
            long minutes = (millis / (1000 * 60)) % 60;
            long hours = (millis / (1000 * 60 * 60)) % 24;
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
    }
}
