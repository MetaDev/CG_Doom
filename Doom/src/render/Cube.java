/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package render;

import math.Vector3f;

/**
 *
 * @author Harald
 */
public class Cube {

    private Vector3f pos = null;
    private float size = 2f;
    private Vector3f color;

    public Cube(Vector3f pos, float size, Vector3f color) {
        this.pos = pos;
        this.color = color;
        this.size = size;
    }

    public float[] getData() {
        return new float[]{
            //one face of the cube
            pos.x, pos.y, pos.z, color.x, color.y, color.z,
            pos.x + size, pos.y, pos.z, color.x, color.y, color.z,
            pos.x + size, pos.y + size, pos.z, color.x, color.y, color.z,
            pos.x, pos.y, pos.z, color.x, color.y, color.z,
            pos.x + size, pos.y + size, pos.z, color.x, color.y, color.z,
            pos.x, pos.y + size, pos.z, color.x, color.y, color.z,
            //  another
            pos.x + size, pos.y, pos.z, color.x, color.y, color.z,
            pos.x + size, pos.y + size, pos.z, color.x, color.y, color.z,
            pos.x + size, pos.y + size, pos.z + size, color.x, color.y, color.z,
            pos.x + size, pos.y, pos.z, color.x, color.y, color.z,
            pos.x + size, pos.y, pos.z + size, color.x, color.y, color.z,
            pos.x + size, pos.y + size, pos.z + size, color.x, color.y, color.z,
            //       another
            pos.x, pos.y, pos.z, color.x, color.y, color.z,
            pos.x + size, pos.y, pos.z, color.x, color.y, color.z,
            pos.x, pos.y, pos.z + size, color.x, color.y, color.z,
            pos.x, pos.y, pos.z + size, color.x, color.y, color.z,
            pos.x + size, pos.y, pos.z, color.x, color.y, color.z,
            pos.x + size, pos.y, pos.z + size, color.x, color.y, color.z,
            //            
            pos.x, pos.y + size, pos.z, color.x, color.y, color.z,
            pos.x + size, pos.y + size, pos.z, color.x, color.y, color.z,
            pos.x + size, pos.y + size, pos.z + size, color.x, color.y, color.z,
            pos.x, pos.y + size, pos.z, color.x, color.y, color.z,
            pos.x, pos.y + size, pos.z + size, color.x, color.y, color.z,
            pos.x + size, pos.y + size, pos.z + size, color.x, color.y, color.z, //     

            pos.x, pos.y, pos.z, color.x, color.y, color.z,
            pos.x, pos.y + size, pos.z, color.x, color.y, color.z,
            pos.x, pos.y + size, pos.z + size, color.x, color.y, color.z,
            pos.x, pos.y, pos.z, color.x, color.y, color.z,
            pos.x, pos.y + size, pos.z + size, color.x, color.y, color.z,
            pos.x, pos.y, pos.z + size, color.x, color.y, color.z,
            //
            pos.x, pos.y, pos.z + size, color.x, color.y, color.z,
            pos.x, pos.y + size, pos.z + size, color.x, color.y, color.z,
            pos.x + size, pos.y + size, pos.z + size, color.x, color.y, color.z,
            pos.x, pos.y, pos.z + size, color.x, color.y, color.z,
            pos.x + size, pos.y + size, pos.z + size, color.x, color.y, color.z,
            pos.x + size, pos.y, pos.z + size, color.x, color.y, color.z
        };
    }

}
