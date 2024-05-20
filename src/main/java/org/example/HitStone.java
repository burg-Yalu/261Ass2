package org.example;

import java.awt.*;
import java.awt.event.KeyEvent;

public class HitStone extends GameEngine{


    //create game
    public static void main(String[] args) {
        createGame(new HitStone());
    }

    //Declaration
    Image BackgroundImage = loadImage("src/main/resources/background.png");
    Image bat_normal_middle = loadImage("src/main/resources/bat_large.png");
    boolean gameover;

    //Bat
    double batx;
    double baty;
    double batvx;
    double batvy;
    int batDirection;
    boolean batToStopBall;

    int batstatus;//0,1,2,3 means different formats of bats
    double batWidth;
    public void initBat(){

        batx = 350;
        baty = 600;

        batvx =0;
        batvy=0;

        batstatus = 0;
        batDirection = 0;
        batToStopBall = false;
    }
    public void updateBat(double dt){
        batx += batvx * dt;

        if (batDirection == 1){
            batvx = -90;
        }else if (batDirection == 2){
            batvx = 90;
        }else if (batDirection == 0){
            batvx = 0;
        }


        if ( batx-(batWidth/2)<=0 && batDirection == 1){
            batvx = 0;
        }else if ( batx+(batWidth/2)>=700 && batDirection == 2){
            batvx = 0;
        }

        batx += batvx * dt;

    }
    public void drawBat(){
        saveCurrentTransform();

        translate(batx,baty);

        drawImage(bat_normal_middle,-batWidth/2,0,batWidth,16);

        restoreLastTransform();
    }



    //Ball
    Image[] ball;

    int ballMax;
    double[] ballX;
    double[] ballY;
    double[] ballVX;
    double[] ballVY;
    double[] distanceX;
    int[] ballType;//different balls and effect
    boolean[] ballActive;

    public void initBall(){
        ball = new Image[4];
        ball[0] = loadImage("src/main/resources/ball_blue.png");
        ball[1] = loadImage("src/main/resources/ball_pinball.png");
        ball[2] = loadImage("src/main/resources/ball_purple.png");
        ball[3] = loadImage("src/main/resources/ball_red.png");
        ballMax  = 9;

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
        //to enable a new ball
        ballActive[0] = true;

        ballX[0] = batx;
        ballY[0] = baty-15;
    }

    public void updateBall(double dt){
        for (int i = 0; i < ballMax; i++) {
            if (ballActive[i]){
                if (!(!batToStopBall && ballX[i]+8 >= batx-batWidth/2 && ballX[i]+8 <= batx+batWidth/2 && ballY[i]+16 >= baty)){
                    ballX[i] += ballVX[i] * dt;
                    ballY[i] += ballVY[i] * dt;
                }else {
                    ballX[i] = batx - distanceX[i];
                    ballY[i] = baty - 16;
                }
            }
            //触板反弹
            if (!batToStopBall && ballX[i]+8 >= batx-batWidth/2 && ballX[i]+8 <= batx+batWidth/2 && ballY[i]+16 >= baty){
                distanceX[i] = batx - ballX[i];
                ballVX[i] = 0;
                ballVY[i] = 0;
                ballX[i] = batx - distanceX[i];
                ballY[i] = baty - 16;
            }else if (batToStopBall && ballX[i]+8 >= batx-batWidth/2 && ballX[i]+8 <= batx+batWidth/2 && ballY[i]+16 >= baty){
                double l = length(ballX[i]+8-batx,16);
                                ballVX[i] = 240 * (ballX[i]+8-batx)/l;
                                ballVY[i] = -240 * (16)/l;
            }
            if (ballX[i]<=0 || ballX[i]+16 >= 700 ){
                ballVX[i] *= -1;
            }
            if (ballY[i] <=0 ){
                ballVY[i] *= -1;
            }else if (ballY[i] >=700){
                gameover = true;
            }
        }
    }
    //center of ball is x+8,y-8
    public void drawBall(){
        for (int i = 0; i < ballMax; i++) {
            if (ballActive[i]){
                saveCurrentTransform();

                translate(ballX[i],ballY[i]);

                drawImage(ball[0],0,0,16,16);

                restoreLastTransform();
            }
        }
    }


    //bricks
    Image[] bule;
    Image[] green;
    Image[] orange;

    Image[] pink;
    Image[] purple;


    public void initBrick(){
        //bule can be defeated by twice hits
        bule = new Image[7];
        bule[0] = loadImage("src/main/resources/Stones/stone_normal_blue.png");
        bule[1] = loadImage("src/main/resources/Stones/stone_damaged2_blue.png");
        for (int i = 2; i < 7; i++) {
            bule[i] = subImage(loadImage("src/main/resources/Stones/stone_breaking_blue_strip5.png"),65*(i-2),0,60,30);
        }
        // green can explosion
        green = new Image[6];
        green[0] = loadImage("src/main/resources/Stones/stone_normal_green.png");


    }



    //init the game
    public void init(){
        setWindowSize(700,700);
        initBat();
        initBall();
        batWidth = 90;
    }


    //update the whole game
    @Override
    public void update(double dt) {

        updateBat(dt);
        updateBall(dt);

    }


    @Override
    public void paintComponent() {

        drawImage(BackgroundImage,0,0,700, 700);

        drawBat();
        drawBall();
    }

    //Key listener
    public void keyPressed(KeyEvent e) {
        //The user pressed left arrow
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            // Record it
            batDirection = 1;
        }
        // The user pressed right arrow
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            // Record it
            batDirection = 2;
        }
        if (e.getKeyCode() == KeyEvent.VK_SPACE){
            batToStopBall = true;
        }
    }
    public void keyReleased(KeyEvent e) {
        // The user released left arrow
        if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) {
            // Record it
            batDirection = 0;
        }

    }
}
