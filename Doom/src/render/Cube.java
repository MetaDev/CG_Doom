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
    public static int[] vertexIndices = {
        // front
        0, 1, 2,
        2, 3, 0,
        // top
        4, 5, 6,
        6, 7, 4,
        // back
        8, 9, 10,
        10, 11, 8,
        // bottom
        12, 13, 14,
        14, 15, 12,
        // left
        16, 17, 18,
        18, 19, 16,
        // right
        20, 21, 22,
        22, 23, 20,};

    public Cube(Vector3f pos, float size, Vector3f color) {
        this.pos = pos;
        this.color = color;
        this.size = size;
    }

    public static int getNrOfVertices() {
        return 6 * 4;
    }

    public static int getNrOfFloats() {
        return 6 * 4 * 8;
    }
    public static int getNrOfElements(){
        return vertexIndices.length;
    }
    public int[] getVertexIndices(int indexInVbo) {
        int[] indices = new int[Cube.vertexIndices.length];
        //add index in vbo (offset) to index of vertices
        int offset = (indexInVbo*getNrOfVertices());
        for(int i=0;i<vertexIndices.length;i++){
            indices[i]=vertexIndices[i]+offset;
        }
        return indices;
    }

    public float[] getData() {
        return new float[]{
            //front
            pos.x, pos.y, pos.z + size, color.x, color.y, color.z, 0, 0,
            pos.x + size, pos.y, pos.z + size, color.x, color.y, color.z, 1, 0,
            pos.x + size, pos.y + size, pos.z + size, color.x, color.y, color.z, 1, 1,
            pos.x, pos.y + size, pos.z + size, color.x, color.y, color.z, 0, 1,
            //top
            pos.x, pos.y + size, pos.z + size, color.x, color.y, color.z, 0, 0,
            pos.x + size, pos.y + size, pos.z + size, color.x, color.y, color.z, 1, 0,
            pos.x + size, pos.y + size, pos.z, color.x, color.y, color.z, 1, 1,
            pos.x, pos.y + size, pos.z, color.x, color.y, color.z, 0, 1,
            //back
            pos.x + size, pos.y, pos.z, color.x, color.y, color.z, 0, 0,
            pos.x, pos.y, pos.z, color.x, color.y, color.z, 1, 0,
            pos.x, pos.y + size, pos.z, color.x, color.y, color.z, 1, 1,
            pos.x + size, pos.y + size, pos.z, color.x, color.y, color.z, 0, 1,
            //bottom
            pos.x, pos.y, pos.z, color.x, color.y, color.z, 0, 0,
            pos.x + size, pos.y, pos.z, color.x, color.y, color.z, 1, 0,
            pos.x + size, pos.y, pos.z + size, color.x, color.y, color.z, 1, 1,
            pos.x, pos.y, pos.z + size, color.x, color.y, color.z, 0, 1,
            //left
            pos.x, pos.y, pos.z, color.x, color.y, color.z, 0, 0,
            pos.x, pos.y, pos.z + size, color.x, color.y, color.z, 1, 0,
            pos.x, pos.y + size, pos.z + size, color.x, color.y, color.z, 1, 1,
            pos.x, pos.y + size, pos.z, color.x, color.y, color.z, 0, 1,
            //right
            pos.x + size, pos.y, pos.z + size, color.x, color.y, color.z, 0, 0,
            pos.x + size, pos.y, pos.z, color.x, color.y, color.z, 1, 0,
            pos.x + size, pos.y + size, pos.z, color.x, color.y, color.z, 1, 1,
            pos.x + size, pos.y + size, pos.z + size, color.x, color.y, color.z, 0, 1,};
    }

}
