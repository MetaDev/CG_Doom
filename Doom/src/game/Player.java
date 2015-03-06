/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import math.Matrix4f;
import math.Vector3f;
import render.Camera;

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

    public float getX() {
        return x;
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

    public void setRotation(float rotation) {
        this.rotation = rotation;
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
        eye=new Vector3f(x,y,camera.getHeight());
    }
    //translates and rotate the matrix so that it looks through the camera
    //this dose basic what gluLookAt() does
    public Matrix4f lookThrough()
    {
        //use inverse values because the world is transformed opposing to you
        //roatate the pitch around the X axis
        Matrix4f pitch = Matrix4f.rotate(-camera.getPitch(), 1.0f, 0.0f, 0.0f);
        //roatate the yaw around the Y axis
       Matrix4f yaw = Matrix4f.rotate(-camera.getYaw(), 0.0f, 1.0f, 0.0f);
       //rotate around z axis for player rotation
       Matrix4f rot = Matrix4f.rotate(rotation, 0.0f,0f, 1f);
        //translate to the position vector's location
         Matrix4f translate = Matrix4f.translate(-x, 0, y);
         return translate.multiply(pitch.multiply(yaw));
    }
    public Vector3f getEye(){
        eye.x=x;
        eye.y=y;
        eye.z=camera.getHeight();
        return eye;
    }
    public Vector3f getTarget(){
        Vector3f eye= getEye();
        float pitch = camera.getPitch();
        float yaw = camera.getYaw();
        Vector3f target = new Vector3f(
	eye.x - (float) (Math.cos(Math.toRadians(pitch)) * Math.sin(Math.toRadians(yaw))),
	eye.y + (float) (Math.sin(Math.toRadians(pitch))),
	eye.z + (float) (Math.cos(Math.toRadians(pitch)) * Math.cos(Math.toRadians(yaw)))
);
        return target;
    }
    
}
