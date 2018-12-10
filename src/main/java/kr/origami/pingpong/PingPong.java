package kr.origami.pingpong;

import avis.juikit.Juikit;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class PingPong {

    public static int WIDTH = 1000;
    public static int HEIGHT = 500;
    public static final int RADIUS = 10;

    private static final Random RANDOM = new Random();

    private static final List<Bar> BARS = new ArrayList<>();

    private static final int UP = 38;
    private static final int DOWN = 40;

    private static final AtomicBoolean MOVING = new AtomicBoolean(false);
    private static final AtomicBoolean DIRECTION = new AtomicBoolean(false);

    private static final AtomicInteger A_SCORE = new AtomicInteger();
    private static final AtomicInteger B_SCORE = new AtomicInteger(-1);

    private static final AtomicInteger SPEED = new AtomicInteger();
    private static final AtomicInteger ACTUAL_SPEED = new AtomicInteger();

    private static final Ball BALL = new Ball();

    private static final AtomicBoolean START = new AtomicBoolean();

    public static Bar player() {
        return BARS.get(0);
    }

    public static int actualHeight() {
        return HEIGHT - 20;
    }

    public static void initBall() {
        BALL.x = WIDTH / 2;
        BALL.y = actualHeight() / 2;

        BALL.vecX = RANDOM.nextBoolean() ? -6 : 6;
        BALL.vecY = RANDOM.nextBoolean() ? -6 : 6;
    }

    public static void main(String args[]) {
        BARS.add(new Bar(100));
        BARS.add(new Bar(WIDTH - 100));
        Juikit.createFrame()
                .title("Ping-pong")
                .antialiasing(true)
                .background(Color.BLACK)
                .size(WIDTH, HEIGHT)
                .closeOperation(WindowConstants.EXIT_ON_CLOSE)
                .centerAlign()
                .repaintInterval(10L)
                .painter((juikit, graphics) -> {
                    WIDTH = juikit.width();
                    HEIGHT = juikit.height();
                    BARS.get(0).setX(100);
                    BARS.get(1).setX(WIDTH - 100);

                    graphics.setColor(Color.WHITE);
                    graphics.drawString(Math.max(0, A_SCORE.get()) + " | " + Math.max(0, B_SCORE.get()), WIDTH / 2 - 20, 50);
                    graphics.drawString("x" + Math.max(1, ACTUAL_SPEED.get()), WIDTH / 2 - 10, 25);

                    SPEED.incrementAndGet();

                    if(SPEED.get() % 500 == 0) {
                        int speed = Math.max(ACTUAL_SPEED.incrementAndGet(), 1);
                        if(BALL.vecX < 0) {
                            BALL.vecX -= speed;
                        } else {
                            BALL.vecX += speed;
                        }

                        if(BALL.vecY < 0) {
                            BALL.vecY -= speed;
                        } else {
                            BALL.vecY += speed;
                        }
                    }

                    if(MOVING.get()) {
                        if(!START.get()) {
                            START.set(true);
                        }
                        Bar player = player();
                        int y;
                        if(DIRECTION.get()) {
                            y = player.getY() - 10;
                            y = Math.max(0, y);
                        } else {
                            y = player.getY() + 10 + player.getHeight();
                            y = Math.min(actualHeight(), y);
                            y -= player.getHeight();
                        }
                        player.setY(y);
                    }

                    if(START.get()) {
                        double nextX = BALL.x + BALL.vecX;
                        double nextY = BALL.y + BALL.vecY;
                        if(nextY <= 0 || nextY >= actualHeight()) {
                            BALL.vecY *= -1;
                        }

                        if(player().isInside(nextX, nextY)) {
                            BALL.vecX = Math.abs(BALL.vecX);
                        }

                        if(BARS.get(1).isInside(nextX, nextY)) {
                            BALL.vecX = Math.abs(BALL.vecX) * -1;
                        }

                        BALL.x += BALL.vecX;
                        BALL.y += BALL.vecY;

                        boolean finished = false;

                        if(nextX <= 0) {
                            B_SCORE.incrementAndGet();
                            finished = true;
                        }

                        if(nextX >= WIDTH) {
                            A_SCORE.incrementAndGet();
                            finished = true;
                        }

                        if(finished) {
                            SPEED.set(0);
                            ACTUAL_SPEED.set(0);
                            initBall();
                            START.set(false);
                        }

                        Bar computer = BARS.get(1);
                        if((computer.getY() + computer.getHeight() / 2) > BALL.y) {
                            computer.setY(computer.getY() - Math.min(8 + ACTUAL_SPEED.get(), 12));
                        } else {
                            computer.setY(computer.getY() + Math.min(8 + ACTUAL_SPEED.get(), 12));
                        }
                    }

                    for(Bar bar : BARS) {
                        graphics.fillRect(bar.getX(), bar.getY(), bar.getWidth(), bar.getHeight());
                    }
                    graphics.fillOval(BALL.x - (RADIUS / 2), BALL.y - (RADIUS / 2), RADIUS, RADIUS);
                })
                .keyPressed((juikit, keyEvent) -> {
                    switch (keyEvent.getKeyCode()) {
                        case UP:
                            MOVING.set(true);
                            DIRECTION.set(true);
                            break;

                        case DOWN:
                            MOVING.set(true);
                            DIRECTION.set(false);
                            break;
                    }
                })
                .keyReleased((juikit, keyEvent) -> {
                    MOVING.set(false);
                })
                .visibility(true);
    }

}
