package com.triadss.doctrack2.types;

public class Vector2i {
    public Vector2i(int x, int y) {
        this.x = x;
        this.y = y;
    }

    private int x;
    private int y;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Vector2i withY(int y) {
        return new Vector2i(x, y);
    }

    public Vector2i withX(int x) {
        return new Vector2i(x, y);
    }
}
