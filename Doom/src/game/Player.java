/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import math.Matrix4f;
import math.Vector2f;
import math.Vector3f;
import math.Vector4f;
import render.Cube;

/**
 *
 * @author Harald
 */
public class Player {
//internally x, y, z are a logical axis, for the drawing coordinates a conversion is required

    private float x;
    private float y;
    private float z;
    private float rotation;
    private Camera camera;
    //the up vector changes the axis of the player
    private Vector3f up = new Vector3f(1, 1, -1);
    private float movementSpeed = 0.1f;
    private float rotationSpeed = 50f;
    private float rotationIncrease = 0;
    //tile containing the players movement
    private Tile tile;
    private Board board;

    public void flipUp() {

    }

    public void update(int updateRate) {
        //divide by update rate
        rotation += (rotationIncrease * rotationSpeed) / updateRate;
        rotation = rotation % 360;
    }

    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

    public float getRotation() {
        return rotation;
    }
    private float mouseXtarget;
    private float mouseYtarget;

    public void setMousePos(float xpos, float ypos, float width, float height) {
        //when mouse position chagens start by setting the rotation increase to 0
        rotationIncrease = (0);
        xpos = Math.min(width, Math.max(0, xpos));
        ypos = Math.min(height, Math.max(0, ypos));
        //only move if inside window frame
        //yaw and pitch are zero at center of window
        float relX = (width / 2) - (float) xpos;
        float relY = (height / 2) - (float) ypos;
        //save normalised center position for targetting
        mouseXtarget = (2 * xpos) / width - 1;
        mouseYtarget = 1 - (2 * ypos) / height;
        float boundStart = (width - height) / 2;
        float boundEnd = height + boundStart;
        float pitch = (relY / (height / 2)) * 90;
        setCameraPitch(pitch);
        //if in bound rotate camera
        if (xpos > boundStart && xpos < boundEnd) {
            // yaw and pitch is between -90 and 90 
            //scale with height and width
            float yaw = (relX / (height / 2)) * 90;

            setCameraYaw(yaw);

        } //if not rotate player with amount of pixels oustside of bound
        else {
            float difX = 0;
            //mouse is left of bound
            if (xpos < boundStart) {
                //rotate player left
                difX = boundStart - xpos;
            } //mouse is right of bound
            else if (xpos > boundEnd) {
                difX = boundEnd - xpos;
            }
            //difX interval should be -boundStart,boundStart
            //normalise
            rotationIncrease = (difX / boundStart);
        }

    }

    public void setCameraYaw(float amount) {
        this.camera.setYaw(amount);
    }

    public void setCameraPitch(float amount) {
        this.camera.setPitch(amount);
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public float getDrawX() {
        return x;
    }

    public float getDrawY() {
        return (z + camera.getHeight());
    }

    public float getDrawZ() {
        return -y;
    }

    public float getDrawRotX() {
        return camera.getPitch() * up.x;
    }

    public float getDrawRotY() {
        return (camera.getYaw() + rotation) * up.y;
    }

    public Player(Tile tile, Board board, float rotation, Camera camera) {
        this.tile = tile;
        this.board = board;
        x = tile.getAbsCenterX();
        y = tile.getAbsCenterY();
        z = tile.getTopZ();
        System.out.println(tile.getDrawOriginPosition().x + "   " + tile.getDrawOriginPosition().y + " " + tile.getDrawOriginPosition().z);
        this.y = tile.getAbsCenterY();
        this.rotation = rotation;
        this.camera = camera;

    }

    //translates and rotate the matrix so that it looks through the camera
    //this dose basic what gluLookAt() does
    public Matrix4f lookThrough() {
        //use inverse values because the world is transformed opposing to you
        //pitch yaw and scale are view
        //rotate the pitch around the X axis
        Matrix4f pitch = Matrix4f.rotate(-camera.getPitch(), 1.0f, 0.0f, 0.0f);
        //rotate the yaw around the Y axis
        Matrix4f yaw = Matrix4f.rotate(-(camera.getYaw() + rotation), 0.0f, 1.0f, 0.0f);

        //TODO put in model matrix
        //translate to the position vector's location, inverse translation
        Matrix4f translate = Matrix4f.translate(-getDrawX(), -getDrawY(), -getDrawZ());
        float ratio = getScale();
        Matrix4f scale = Matrix4f.scale(ratio, ratio, ratio);
        return scale.multiply(pitch.multiply(yaw).multiply(translate));
    }

    public float getScale() {
        return board.rootSize / (tile.getAbsSize() * 8);
    }

    private Vector2f direction = new Vector2f();

    public void left() {
        setMoveDirection(90);
        move();
    }

    public void right() {
        setMoveDirection(-90);
        move();
    }

    public void forward() {
        setMoveDirection(0);
        move();
    }

    private void setMoveDirection(float moveAngle) {
        direction.x = (float) Math.cos(Math.toRadians((camera.getYaw() + rotation + 90 + moveAngle)));
        direction.y = (float) Math.sin(Math.toRadians((camera.getYaw() + rotation + 90 + moveAngle)));
    }

    private void move() {
        float scale = getScale();
        float newX = x + direction.x * (movementSpeed / scale);
        float newY = y + direction.y * (movementSpeed / scale);
        System.out.println(newX + "   " + newY);

        if (inTile(newX, newY)) {
            x = (newX);
            y = (newY);
        }

    }

    private boolean inTile(float x, float y) {
        return x > tile.getAbsX() && y < tile.getAbsY()
                && x < tile.getAbsX() + tile.getAbsSize()
                && y > tile.getAbsY() - tile.getAbsSize();
    }

    public void backward() {
        setMoveDirection(180);
        move();
    }

    public void shoot() {
        Matrix4f projection = board.getCurrentGame().getProjectionMatrix();
        Matrix4f model = lookThrough();
        Matrix4f product = projection.multiply(model);
        Matrix4f inverse = product.inverse();
        //the depth value you can manually go from -1 to 1 ( zNear, zFar )
        float winZ = 0f;
        Vector4f mouse = new Vector4f(mouseXtarget, mouseYtarget, winZ, 1);
        Vector4f position = inverse.multiply(mouse);
        Vector3f target = new Vector3f();
        position.w = 1 / position.w;
        target.x = position.x * position.w;
        target.y = position.y * position.w;
        target.z = position.z * position.w;

        Vector3f eye = new Vector3f(0, 0, 0);
        System.out.println(target.x + " " + target.y + " " + target.z);
        Cube hit = board.getClosestCubeInFrontByRay(eye, target);
        Cube newCube1 = new Cube(eye, .1f, new Vector3f(1f, 0.1f, 0.7f));
        board.getCurrentGame().addCubeToScene(newCube1);

        Cube newCube2 = new Cube(target, .1f, new Vector3f(0.5f, 0.3f, 0.2f));
        board.getCurrentGame().addCubeToScene(newCube2);

        System.out.println(hit);
        if (hit != null) {
            board.removeCubeFromBoard(hit);
        }

    }
}
