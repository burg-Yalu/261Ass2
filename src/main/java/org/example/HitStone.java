package org.example;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Random;
import javax.swing.*;


public class HitStone extends GameEngine {

    // 游戏状态枚举
    private enum GameState {
        RUNNING, PAUSED, GAME_OVER
    }

    private GameTimer gameTimer;
    private GameState gameState = GameState.RUNNING;
    private boolean statusWindowVisible = false;
    private SoundPlayer backgroundMusic;
    private int score = 0;
    private int lives;


    // 创建游戏
    public static void main(String[] args) {
        createGame(new HitStone());
    }

    // 声明
    Image BackgroundImage = loadImage("src/main/resources/background.png");
    Image bat_normal_middle = loadImage("src/main/resources/bat_large.png");
    Image bat_long_middle = loadImage("src/main/resources/bat_huge.png");
    Image bat_short_middle = loadImage("src/main/resources/bat_small.png");
    Image heartImage = loadImage("src/main/resources/heart.png");
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
        batstatus = 1;
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
        if (batstatus == 0) {
            batWidth = 50;
        } else if (batstatus == 1) {
            batWidth = 100;
        } else if (batstatus == 2) {
            batWidth = 150;
        }
    }


    public void drawBat() {
        saveCurrentTransform();
        translate(batx, baty);
        //drawImage(bat_normal_middle, -batWidth / 2, 0, batWidth, 16);
        if (batstatus == 0) {
            drawImage(bat_short_middle, -batWidth/2, 0, batWidth, 16);
        } else if (batstatus == 1) {
            drawImage(bat_normal_middle, -batWidth/2, 0, batWidth, 16);
        } else if (batstatus == 2) {
            drawImage(bat_long_middle, -batWidth/2, 0, batWidth, 16);
        }
        restoreLastTransform();
    }
    public void drawTimeScore(double x, double y) {
        // Draw text on the screen
        mGraphics.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
        mGraphics.drawString("Time: " + gameTimer.getFormattedTime(),  (int)x, (int)y);
        int nextY = (int)y + mGraphics.getFontMetrics().getHeight();
        mGraphics.drawString("Score: " + score,  (int)x, nextY);
        int hpX = 600; // 生命值显示的位置
        int hpY = 20;
        mGraphics.drawString("HP: ", hpX, hpY);
        for (int i = 0; i < lives; i++) {
            drawImage(heartImage, hpX + 30 + (i * 20), hpY - 15, 16, 16); // 绘制心形图标
        }
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
                if (!batToStopBall){
                    ballX[i] = batx - distanceX[i];
                    ballY[i] = baty - 16;
                }
                if (ballY[i] >= 700) {
                    lives--; // 生命值减少
                    batToStopBall = false;
                    if (lives <= 0) {
                        showGameStatus("Game Lose");
                        backgroundMusic.stop();
                        gameover = true; // 设置游戏结束标志
                    }
//                    if (lives <= 0) {
//                        backgroundMusic.stop(); // 停止当前背景音乐
//                        //init();
//                        gameState = GameState.RUNNING;
//                        gameTimer.start();
//
//                        showGameStatus("Game Over, click OK to continue");
//                    }
                }

            }
            // 触板反弹
            if (!batToStopBall && ballX[i] + 8 >= batx - batWidth / 2 && ballX[i] + 8 <= batx + batWidth / 2 && ballY[i] + 16 >= baty && ballY[i] + 16 < baty + 16) {
                distanceX[i] = batx - ballX[i];
                ballVX[i] = 0;
                ballVY[i] = 0;
                ballX[i] = batx - distanceX[i];
                ballY[i] = baty - 16;
            } else if (batToStopBall && ballX[i] + 8 >= batx - batWidth / 2 && ballX[i] + 8 <= batx + batWidth / 2 && ballY[i] + 16 >= baty && ballY[i] + 16 < baty + 16) {
                double l = length(ballX[i] + 8 - batx, 16);
                ballVX[i] = 240 * (ballX[i] + 8 - batx) / l;
                ballVY[i] = -240 * (16) / l;
            }
            if (ballX[i] < 0 || ballX[i] + 16 > 700) {
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
    double[] buffX;
    double[] buffY;
    boolean[] buffActive;
    int[] bluecount;

    public void initBrick(){

        brickX = new double[100];
        brickY = new double[100];
        brickTimer = new double[100];
        brickActive = new boolean[100];
        brickType = new int[100];
        brickLife = new int[100];
        brickBreak = new boolean[100];
        buffX = new double[100];
        buffY = new double[100];
        buffActive = new boolean[100];
        bluecount = new int[100];

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Random random = new Random();
                double randomNumber = random.nextDouble();
                if(randomNumber  < 0.8){
                    int randType = rand(5);
                    brickActive[10*i+j] = true;
                    brickType[10*i+j] = randType;
                    brickX[10*i+j] = 60.0 * j + 75;
                    brickY[10*i+j] = 30 * i + 30;
                    brickLife[10*i+j] = 1;
                    if (randType == 0){brickLife[i] = 2;}
                    brickBreak[10*i+j] = false;
                    bluecount[i]=2;
                }

            }
        }


        //bule can be defeated by twice hits
        bule1 = new Image[7];
        bule1[0] = subImage(loadImage("src/main/resources/Stones/stone_normal_blue.png"),5,4,54,25);
        bule1[1] = loadImage("src/main/resources/Stones/stone_damaged2_blue.png");
        for (int i = 2; i < 7; i++) {
            bule1[i] = subImage(loadImage("src/main/resources/Stones/stone_breaking_blue_strip5.png"),65*(i-2),0,60,30);
        }
        // green can explosion
        green = new Image[6];
        green[0] = subImage(loadImage("src/main/resources/Stones/stone_normal_green.png"),5,4,54,25);
        for (int i = 1; i < 6; i++) {
            green[i] = subImage(loadImage("src/main/resources/Stones/stone_breaking_green_strip5.png"),65*(i-1),0,60,30);
        }
        //orange
        orange = new Image[7];
        orange[0] = subImage(loadImage("src/main/resources/Stones/orange_plus-removebg-preview.png"),5,4,54,25);
        for (int i = 1; i < 6; i++) {
            orange[i] = subImage(loadImage("src/main/resources/Stones/stone_breaking_orange_strip5.png"),65*(i-1),0,60,30);
        }
        orange[6] = loadImage("src/main/resources/Plus.png");
        //pink
        pink = new Image[7];
        pink[0] = subImage(loadImage("src/main/resources/Stones/pink.png"),4,5,55,23);
        for (int i = 1; i < 6; i++) {
            pink[i] = subImage(loadImage("src/main/resources/Stones/stone_breaking_pink_strip5.png"),65*(i-1),0,60,30);
        }
        pink[6] = loadImage("src/main/resources/minus.png");
        //purple
        purple = new Image[7];
        purple[0] = subImage(loadImage("src/main/resources/Stones/stone_life_purple.png"),5,4,54,25);
        for (int i = 1; i < 6; i++) {
            purple[i] = subImage(loadImage("src/main/resources/Stones/stone_breaking_purple_strip5.png"),65*(i-1),0,60,30);
        }
        purple[6] = loadImage("src/main/resources/heart.png");
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
                            if(brickType[i]==0){
                                bluecount[i] -= 1;
                            }
                            if (brickLife[i] == 0) {
                                SoundPlayer.playSound("src/main/resources/break.wav");
                            }
                        }else if((abs(brickY[i]-15-(ballY[j]+8))<8||abs(ballY[j]+8-(brickY[i]+15))<8) && abs(ballX[j]+8-(brickX[i]))<30){
                            brickLife[i] --;
                            ballVY[j] *= -1;
                            if(brickType[i]==0){
                                bluecount[i] -= 1;
                            }
                            if (brickLife[i] == 0) {
                                SoundPlayer.playSound("src/main/resources/break.wav");
                            }
                        }
                        if (brickLife[i]==0){
                            score += 10;
                            brickActive[i] = false;
                            buffActive[i] = true;
                            createBuff(i);
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
                if (brickType[i] == 0) {
                    drawImage(bule1[0], -30, -15, 60, 30);
                }else if (brickType[i] == 1) {
                    drawImage(green[0], -30, -15, 60, 30);
                }else if (brickType[i] == 2) {
                    drawImage(orange[0], -30, -15, 60, 30);
                }else if (brickType[i] == 3) {
                    drawImage(pink[0], -30, -15, 60, 30);
                }else if (brickType[i] == 4) {
                    drawImage(purple[0], -30, -15, 60, 30);
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
                }else if(brickType[i]==3){
                    drawImage(pink[j],-30, -15, 60, 30);
                }
                restoreLastTransform();
            }
        }
    }
    public void createBuff(int i){
        if (brickType[i] != 0 && brickType[i] != 1){
            buffX[i] = brickX[i];
            buffY[i] = brickY[i];
            buffActive[i] = true;
        } else return;
    }
    public void updateBuff(double dt){
        for (int i = 0; i < 100; i++) {
            if (buffActive[i]){
                buffY[i] += 240 * dt;
                if (abs(buffX[i]-batx)<(batWidth/2 + 16) && abs(buffY[i]-baty)<16){

                    if (brickType[i] == 2 && batstatus<2){
                        batstatus++;
                        score += 10;
                    }else if (brickType[i] == 3 && batstatus>0){
                        batstatus--;
                        score -= 10;
                    }else if (brickType[i] == 4 && lives<4){
                        lives++;
                    }
                    buffActive[i] = false;
                }
            }

        }
    }
    public void drawBuff(){
        for (int i = 0; i < 100; i++) {
            if (buffActive[i]){
                saveCurrentTransform();
                translate(buffX[i],buffY[i]);
                if (brickType[i] == 2){
                    drawImage(orange[6],-16,-16,32,32);
                }else if (brickType[i] == 3){
                    drawImage(pink[6],-16,-16,32,32);
                }else if (brickType[i] == 4){
                    drawImage(purple[6],-16,-16,32,32);
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
        //batWidth = 90;

        // 初始化计时器
        gameTimer = new GameTimer();
        //gameTimer.start();
        score = 0;
        lives = 2;
        backgroundMusic = new SoundPlayer("src/main/resources/background_music.wav");
        backgroundMusic.play();
    }

    // 更新游戏
    @Override
    public void update(double dt) {
        if (gameState == GameState.RUNNING) {
            updateBat(dt);
            updateBall(dt);
            updateBrick(dt);
            updateBuff(dt);
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
        drawBuff();
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
            if (!gameTimer.running) { // 仅在计时器没有运行时启动
                gameTimer.start();
            }
        }
        // 用户按下 'P' 键暂停游戏
        if (e.getKeyCode() == KeyEvent.VK_P) {
            if (gameState == GameState.RUNNING) {
                gameState = GameState.PAUSED;
                gameTimer.stop(); // 暂停计时器
                backgroundMusic.pause(); // 暂停背景音乐
                showGameStatus("Game Paused, click OK to restart");
            }
        }
        // 用户按下 'R' 键重启游戏
        if (e.getKeyCode() == KeyEvent.VK_R) {
            backgroundMusic.pause(); // 停止当前背景音乐
            init();
            gameState = GameState.RUNNING;
            gameTimer.start();

            showGameStatus("Game Restarted, click OK to continue");
        }
    }

    private void showGameStatus(String status) {
        if (!statusWindowVisible) {
            statusWindowVisible = true;
            new GameStatusWindow(status, new GameStatusWindow.GameStatusCallback() {
                @Override
                public void onOKClicked() {
                    if (gameState == GameState.PAUSED) {
                        gameState = GameState.RUNNING;
                        gameTimer.start();
                        backgroundMusic.resume();

                    }
                    if (gameover) { // 如果游戏结束，重新初始化
                        init();
                    }
                    statusWindowVisible = false;
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