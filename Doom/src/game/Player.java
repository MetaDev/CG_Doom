/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import math.Matrix4f;
import math.Vector2f;
import math.Vector3f;

/**
 *
 * @author Harald
 */
public class Player {

    private float x;
    private float y;
    private float rotation;
    private Camera camera;
    private Vector3f eye;
    private float movementSpeed = 0.1f;
    private float rotationSpeed = 50f;
    private float rotationIncrease = 0;
    //scalar of the screen height (min(width,height))
    //private float nonRotatingFrameWidth = 1;

    public float getX() {
        return x;
    }

    public void update(int updateRate) {
        //divide by update rate
        rotation += (rotationIncrease * rotationSpeed) / updateRate;
        rotation = rotation % 360;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getRotation() {
        return rotation;
    }

    public void setMousePos(float xpos, float ypos, float width, float height) {
        //when mouse position chagens start by setting the rotation increase to 0
        rotationIncrease = (0);
        xpos = Math.min(width, Math.max(0, xpos));
        ypos = Math.min(height, Math.max(0, ypos));
        //only move if inside window frame
        //yaw and pitch are zero at center of window
        float relX = (width / 2 - (float) xpos);
        float relY = (height / 2 - (float) ypos);
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

    public Player(float x, float y, float rotation, Camera camera) {
        this.x = x;
        this.y = y;
        this.rotation = rotation;
        this.camera = camera;
        eye = new Vector3f(x, y, camera.getHeight());
    }

    //translates and rotate the matrix so that it looks through the camera
    //this dose basic what gluLookAt() does
    public Matrix4f lookThrough() {
        //use inverse values because the world is transformed opposing to you
        //roatate the pitch around the X axis
        Matrix4f pitch = Matrix4f.rotate(-camera.getPitch(), 1.0f, 0.0f, 0.0f);
        //roatate the yaw around the Y axis
        Matrix4f yaw = Matrix4f.rotate(-(camera.getYaw() + rotation), 0.0f, 1.0f, 0.0f);

        //translate to the position vector's location
        Vector3f eye = getEye();
        Matrix4f translate = Matrix4f.translate(eye.x, eye.y, eye.z);
        return pitch.multiply(yaw).multiply(translate);
    }

    public Vector3f getEye() {
        eye.x = -x;
        eye.y = -camera.getHeight();
        eye.z = y;
        return eye;
    }

    public Vector3f getTarget() {
        Vector3f eye = getEye();
        float pitch = camera.getPitch();
        float yaw = camera.getYaw();
        Vector3f target = new Vector3f(
                eye.x - (float) (Math.cos(Math.toRadians(pitch)) * Math.sin(Math.toRadians(yaw))),
                eye.y + (float) (Math.sin(Math.toRadians(pitch))),
                eye.z + (float) (Math.cos(Math.toRadians(pitch)) * Math.cos(Math.toRadians(yaw)))
        );
        return target;
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
        direction.x = (float) Math.sin(Math.toRadians((camera.getYaw() + rotation + moveAngle)));
        direction.y = (float) Math.cos(Math.toRadians((camera.getYaw() + rotation + moveAngle)));
    }

    private void move() {
        x -= direction.x * movementSpeed;
        y += direction.y * movementSpeed;
    }

    public void backward() {
       setMoveDirection(180);
        move();
    }

}
