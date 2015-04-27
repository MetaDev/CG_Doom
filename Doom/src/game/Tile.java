/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import java.util.Random;
import math.Vector3f;
import render.Cube;

/**
 *
 * @author Harald
 */
public class Tile {

    private float absSize;
    private float absPosX;
    private float absPosY;
    private float size;
    private Vector3f color;
    private Vector3f position;
    private Tile[][] children;
    private Tile parent;
    private int level;
    private int childFraction = 1;
    // relative position in parent
    private int i;
    private int j;

    // mothertile aka "The Board"

    public Tile(Vector3f color, float size, float x, float y) {
        this.color = color;
        this.size = size;
        absPosX = x;
        absPosY = y;

    }

  

    // any child tile
    public Tile(Vector3f color, int i, int j, int level,
            Tile parent) {
        this.color = color;
        this.parent = parent;
        this.level = level;
        this.i = i;
        this.j = j;
        
       

    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }

    public void undivide() {
        children = null;
    }

    public void divide(int fraction) {
        if (fraction != 2 && fraction != 4 && fraction != 8) {
            return;
        }
        //divide one for sure
        divide();
        //if fraction higher than 2, further divide children equally
        if (fraction == 4) {
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    children[i][j].divide();
                }
            }
        } else if (fraction == 8) {
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    children[i][j].divide(4);
                }
            }
        }

    }

    // don't allow empty (null) children
    public void divide() {
        childFraction = 2;
        children = new Tile[2][2];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                float color = ((i + j) % 2) % 2;
                Random rn = new Random();
                //give each child a random color, between 0,1/2 for white tiles and between 1/2,1 for black tiles
                children[i][j] = new Tile(
                        new Vector3f((color+rn.nextFloat())/2, (color+rn.nextFloat())/2, (color+rn.nextFloat())/2), i, j, level + 1, this);
            }
        }
    }

    public int getChildFraction() {
        return childFraction;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void removeChild(int i, int j) {
        children[i][j] = null;
    }

    public Tile[][] getChildren() {
        return children;
    }

    public Tile getParent() {
        return parent;
    }

    // implement recursive positioning and size
    private float getRelX() {
        if (parent != null) {
            return i * getRelSize();
        } else {
            return 0;
        }
    }

    private float getRelY() {
        if (parent != null) {
            return j * getRelSize();
        } else {
            return 0;
        }
    }

    public float getAbsX() {
        if (parent != null) {
            return getRelX() + parent.getAbsX();
        } else {
            return absPosX;
        }

    }

    public float getAbsCenterX() {
        return getAbsX() + getAbsSize() / 2;
    }

    public float getAbsCenterY() {
        return getAbsY() - getAbsSize() / 2;
    }

    public float getTopZ() {
        return getAbsSize() / 2;
    }

    public float getBottomZ() {
        return -getAbsSize() / 2;
    }

    public float getAbsY() {
        if (parent != null) {
            return -getRelY() + parent.getAbsY();
        } else {
            return absPosY;
        }
    }

    public int getAbsFraction() {
        if (parent != null) {
            return parent.childFraction * parent.getAbsFraction();
        } else {
            return 1;
        }
    }

    //returns top-left position of the tile
    public Vector3f getDrawOriginPosition() {
        if (position == null) {
            position = new Vector3f(getAbsX(), getBottomZ(), -getAbsY());
        }
        return position;
    }
    private Vector3f centerTopPosition;
    public Vector3f getDrawCenterTopPosition(){
         if (centerTopPosition == null) {
            centerTopPosition = new Vector3f(getAbsCenterX(), getTopZ(), -getAbsCenterY());
        }
        return centerTopPosition;
    }
    
    // get size relative to container
    public float getRelSize() {
        if (parent != null) {
            return (parent.getRelSize() / parent.getChildFraction());
        } else {
            return size;
        }

    }

    public float getAbsSize() {
        if (parent != null) {
            return parent.getAbsSize() / parent.getChildFraction();
        } else {
            return size;
        }
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    
}
