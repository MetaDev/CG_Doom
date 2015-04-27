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
    private float movementSpeed = 1f;
    private float rotationSpeed = 100f;
    private float rotationIncrease = 0;
    private boolean movementIncrease;
    //tile containing the players movement
    private Tile tile;
    private Cube cube;
    private Game game;

    boolean jump;

    public void updateLogic(float delta) {
        rotate(delta);
        move(delta);
    }

    private void rotate(float delta) {
        //divide by updateLogic rate
        rotation += (rotationIncrease * rotationSpeed) * delta;
        // rotation = rotation % 360;
    }

    public Tile getTile() {
        return tile;
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
        float pitch = (relY / (height / 2)) * (90 / getZoom());
        setCameraPitch(pitch);
        //if in bound rotate camera
        if (xpos > boundStart && xpos < boundEnd) {
            // yaw and pitch is between -90 and 90 
            //scale with height and width
            float yaw = (relX / (height / 2)) * (90 / getZoom());

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
            rotationIncrease = (difX / boundStart) / getZoom();
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
        //move backward
        return x;
    }

    public float getDrawY() {
        return (z + camera.getHeight() / getZoom());
    }

    public float getDrawZ() {
        //move backward
        return -(y);
    }

    public float getDrawRotX() {
        return camera.getPitch();
    }

    public float getDrawRotY() {
        return (camera.getYaw() + rotation);
    }

    public Player(Tile tile, Cube cube, Game game) {

        this.game = game;
        this.cube = cube;

        this.camera = new Camera(0.2f);
        setTilePosition(tile);

    }

    public float getCharCubeY() {
        return tile.getTopZ();
    }

    public Matrix4f getModelView() {
        //use inverse values because the world is transformed opposing to you
        //pitch yaw and scale are view
        //rotate the pitch around the X axis
        Matrix4f pitch = Matrix4f.rotate(-camera.getPitch(), 1.0f, 0.0f, 0.0f);
        //rotate the yaw around the Y axis
        Matrix4f yaw = Matrix4f.rotate(-(camera.getYaw() + rotation), 0.0f, 1.0f, 0.0f);

        //translate to the position vector's location, inverse translation
        Matrix4f translate = Matrix4f.translate(-getDrawX(), -getDrawY(), -getDrawZ());
        return pitch.multiply(yaw).multiply(translate);
    }

    public float getZoom() {
        return game.board.rootSize / (tile.getAbsSize() * 8);
    }

    private Vector2f direction = new Vector2f();

    public void left(boolean on) {
        movementIncrease = on;
        setMoveDirection(180);
    }

    public void right(boolean on) {
        movementIncrease = on;
        setMoveDirection(0);
    }

    public void forward(boolean on) {
        movementIncrease = on;
        setMoveDirection(90);
    }

    private void setMoveDirection(float angle) {
        direction.x = (float) Math.cos(Math.toRadians((camera.getYaw() + rotation + angle)));
        direction.y = (float) Math.sin(Math.toRadians((camera.getYaw() + rotation + angle)));
    }

    private void move(float delta) {
        float scale = getZoom();
        if (movementIncrease) {
            float newX = x + direction.x * (movementSpeed / scale) * delta;
            float newY = y + direction.y * (movementSpeed / scale) * delta;
            if (inTile(newX, newY)) {
                x = (newX);
                y = (newY);
            }
        } else if (jump) {
            if (z <= tile.getTopZ() + tile.getAbsSize()) {
                //difference with max jump hight decides speed
                float jumpspeed = Math.max(0.1f, (tile.getAbsSize() - (z - tile.getTopZ())) / tile.getAbsSize());
                float newZ = z + jumpspeed * (movementSpeed / scale) * delta;
                if (newZ <= tile.getTopZ() + tile.getAbsSize()) {
                    z = newZ;
                } else {
                    jump = false;
                    return;
                }
            }

        }
        if (!jump) {
            if (z > tile.getTopZ()) {
                float newZ = z - (movementSpeed / scale) * delta;
                if (newZ >tile.getTopZ()) {
                    z = newZ;
                }else{
                    z=tile.getTopZ();
                }
            }
        }

    }

    private boolean inTile(float x, float y) {
        return x > tile.getAbsX() && y < tile.getAbsY()
                && x < tile.getAbsX() + tile.getAbsSize()
                && y > tile.getAbsY() - tile.getAbsSize();
    }

    public void backward(boolean on) {
        movementIncrease = on;
        setMoveDirection(-90);
    }

    public void jump(boolean on) {
        if (on && !isJumping()) {
            jump = true;
        }else if(!on){
            jump =false;
        }
    }

    private boolean isJumping() {
        return z > tile.getTopZ();
    }

    public void shoot() {
        Matrix4f projection = game.getProjectionMatrix();
        Matrix4f product = projection.multiply(getModelView());
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
        //second target point
        mouse = new Vector4f(mouseXtarget, mouseYtarget, -0.5f, 1);
        position = inverse.multiply(mouse);
        Vector3f target1 = new Vector3f();
        position.w = 1 / position.w;
        target1.x = position.x * position.w;
        target1.y = position.y * position.w;
        target1.z = position.z * position.w;

        Cube cubeHit = game.board.getClosestCubeInFrontByRay(target, target1);

        if (cubeHit != null) {
            Tile newTile = game.board.getTileOfCube(cubeHit);
            if (newTile != null) {
                setTilePosition(newTile);
            }
        }

    }

    public void setTilePosition(Tile tile) {
        this.tile = tile;
        game.setZoom(getZoom());
        x = tile.getAbsCenterX();
        y = tile.getAbsCenterY();
        z = tile.getTopZ();
        cube.setSize(tile.getAbsSize() / 16);
        jump = false;
        cube.setPosition(tile.getDrawCenterTopPosition());
        game.bindSceneForRendering();

    }

    /**
     *
     * @author Tim
     */
    private class Camera {

        public float getYaw() {
            return yaw;
        }

        public void setYaw(float yaw) {
            this.yaw = yaw;
        }

        public float getPitch() {
            return pitch;
        }

        public void setPitch(float pitch) {
            this.pitch = pitch;
        }
        private float height;
        //the rotation around the Y axis of the camera
        private float yaw = 0.0F;
        //the rotation around the X axis of the camera
        private float pitch = 0.0F;

        public Camera(float height) {
            this.height = height;
        }

        public float getHeight() {
            return height;
        }

        public void setHeight(float height) {
            this.height = height;
        }

        public void pitch(float amount) {
            pitch += amount;
        }

        public void yaw(float amount) {
            yaw += amount;
        }
    }
}
