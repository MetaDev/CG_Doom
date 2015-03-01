/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package render;

import javax.vecmath.Vector3f;

/**
 *
 * @author Harald
 */
public class Cube {

    private Vector3f pos = null;
    private float cubeSize = 100f;

    public Cube(Vector3f pos) {
        this.pos = pos;
    }

    public float[] getData(){
        return new float[] {    pos.x,pos.y,pos.z,
                    pos.x + cubeSize,pos.y,pos.z,
                    pos.x + cubeSize,pos.y + cubeSize,pos.z,
                    pos.x,pos.y + cubeSize,pos.z,

                    pos.x,pos.y,pos.z + cubeSize,
                    pos.x + cubeSize,pos.y,pos.z + cubeSize,
                    pos.x + cubeSize,pos.y + cubeSize,pos.z + cubeSize,
                    pos.x,pos.y + cubeSize,pos.z + cubeSize,

                    pos.x,pos.y,pos.z,
                    pos.x,pos.y,pos.z + cubeSize,
                    pos.x,pos.y + cubeSize,pos.z + cubeSize,
                    pos.x,pos.y + cubeSize,pos.z,

                    pos.x + cubeSize,pos.y,pos.z,
                    pos.x + cubeSize,pos.y,pos.z + cubeSize,
                    pos.x + cubeSize,pos.y + cubeSize,pos.z + cubeSize,
                    pos.x + cubeSize,pos.y + cubeSize,pos.z,

                    pos.x,pos.y,pos.z,
                    pos.x,pos.y,pos.z + cubeSize,
                    pos.x + cubeSize,pos.y,pos.z + cubeSize,
                    pos.x + cubeSize,pos.y,pos.z,

                    pos.x,pos.y + cubeSize,pos.z,
                    pos.x,pos.y + cubeSize,pos.z + cubeSize,
                    pos.x + cubeSize,pos.y + cubeSize,pos.z + cubeSize,
                    pos.x + cubeSize,pos.y + cubeSize,pos.z,
                };
    }

}