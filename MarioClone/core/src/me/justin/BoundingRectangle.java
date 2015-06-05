package me.justin;

import com.badlogic.gdx.math.Rectangle;

public class BoundingRectangle {

    //coords of the box relative to the origin
    public float x=0, y=0, width=0, height=0;
    //the position of the box
    public float originX=0, originY=0; //Used for world space transformations

    public BoundingRectangle() {}

    public BoundingRectangle(float x, float y, float width, float height) {
       set(x,y,width,height);
    }

    public boolean overlaps(BoundingRectangle r) {
        if (r == null) return false;
        return x < r.x + r.width && x + width > r.x && y < r.y + r.height && y + height > r.y;
    }

    public boolean overlapsWorldSpace(BoundingRectangle r) {
        if (r == null) return false;
        return getWorldX() < r.getWorldX() + r.width
                && getWorldX() + width > r.getWorldX()
                && getWorldY() < r.getWorldY() + r.height
                && getWorldY() + height > r.getWorldY();
    }

    public boolean overlapsX(BoundingRectangle r) {
        if (r == null) return false;
        return x < r.x + r.width && x + width > r.x;
    }

    public boolean overlapsWorldX(BoundingRectangle r) {
        if (r == null) return false;
        return getWorldX() < r.getWorldX() + r.width && getWorldX() + width > r.getWorldX();
    }

    public boolean overlapsY(BoundingRectangle r) {
        if (r == null) return false;
        return y < r.y + r.height && y + height > r.y;
    }

    public boolean overlapsWorldY(BoundingRectangle r) {
        if (r == null) return false;
        return getWorldY() < r.getWorldY() + r.height && getWorldY() + height > r.getWorldY();
    }

    public void setOrigin(float x, float y) {
        this.originX = x;
        this.originY = y;
    }

    public float getWorldX() {return x + originX;}
    public float getWorldY() {return y + originY;}

    public float getLeft() { return x;}
    public float getWorldLeft() {return getLeft() + originX;}

    public float getRight() { return x + width;}
    public float getWorldRight() {return getRight() + originX;}

    public float getBottom() { return y;}
    public float getWorldBottom() {return getBottom() + originY;}

    public float getTop() { return y + height;}
    public float getWorldTop() {return getTop() + originY;}

    public void set(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}