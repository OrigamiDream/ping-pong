package kr.origami.pingpong;

public class Bar {

    private final int x;
    private int y;

    private final int width;
    private final int height;

    public Bar(int x) {
        this.x = x;
        this.y = PingPong.actualHeight() / 2 - getHeight() / 2;

        this.width = 10;
        this.height = 70;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isInside(double x, double y) {
        return this.x <= x && this.y <= y && (this.x + width) >= x && (this.y + height) >= y;
    }
}
