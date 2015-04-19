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
    private Vector3f center = null;
    private float size = 2f;
    private float data[];
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
        center = new Vector3f(pos.x + size / 2, pos.y + size / 2, pos.z + size / 2);
    }

    public static int getNrOfVertices() {
        return 6 * 4;
    }

    public static int getNrOfFloats() {
        return 6 * 4 * 11;
    }

    public static int getNrOfElements() {
        return vertexIndices.length;
    }

    public int[] getVertexIndices(int indexInVbo) {
        int[] indices = new int[Cube.vertexIndices.length];
        //add index in vbo (offset) to index of vertices
        int offset = (indexInVbo * getNrOfVertices());
        for (int i = 0; i < vertexIndices.length; i++) {
            indices[i] = vertexIndices[i] + offset;
        }
        return indices;
    }

    public Vector3f getPos() {
        return pos;
    }

    public float getSize() {
        return size;
    }

    public Vector3f getColor() {
        return color;
    }

    public void setPosition(Vector3f pos) {
        this.pos=pos;
        data = null;
    }

    public void setSize(float size) {
        this.size = size;
        data = null;
    }

    public void setColor(Vector3f color) {
        this.color = color;
        data = null;
    }

    public float distanceWithPoint(Vector3f p) {
        return p.subtract(center).lengthSquared();
    }

    private static Vector3f frontNormal = new Vector3f(0, 0, 1);
    private static Vector3f topNormal = new Vector3f(0, 1, 0);
    private static Vector3f backNormal = new Vector3f(0, 0, -1);
    private static Vector3f bottomNormal = new Vector3f(0, -1, 0);
    private static Vector3f leftNormal = new Vector3f(-1, 0, 0);
    private static Vector3f rightNormal = new Vector3f(1, 0, 0);

    public float[] getData() {
        if (data == null) {
            data = new float[]{
                //front
                pos.x, pos.y, pos.z + size, color.x, color.y, color.z, 0, 0, frontNormal.x, frontNormal.y, frontNormal.z,
                pos.x + size, pos.y, pos.z + size, color.x, color.y, color.z, 1, 0, frontNormal.x, frontNormal.y, frontNormal.z,
                pos.x + size, pos.y + size, pos.z + size, color.x, color.y, color.z, 1, 1, frontNormal.x, frontNormal.y, frontNormal.z,
                pos.x, pos.y + size, pos.z + size, color.x, color.y, color.z, 0, 1, frontNormal.x, frontNormal.y, frontNormal.z,
                //top
                pos.x, pos.y + size, pos.z + size, color.x, color.y, color.z, 0, 0, topNormal.x, topNormal.y, topNormal.z,
                pos.x + size, pos.y + size, pos.z + size, color.x, color.y, color.z, 1, 0, topNormal.x, topNormal.y, topNormal.z,
                pos.x + size, pos.y + size, pos.z, color.x, color.y, color.z, 1, 1, topNormal.x, topNormal.y, topNormal.z,
                pos.x, pos.y + size, pos.z, color.x, color.y, color.z, 0, 1, topNormal.x, topNormal.y, topNormal.z,
                //back
                pos.x + size, pos.y, pos.z, color.x, color.y, color.z, 0, 0, backNormal.x, backNormal.y, backNormal.z,
                pos.x, pos.y, pos.z, color.x, color.y, color.z, 1, 0, backNormal.x, backNormal.y, backNormal.z,
                pos.x, pos.y + size, pos.z, color.x, color.y, color.z, 1, 1, backNormal.x, backNormal.y, backNormal.z,
                pos.x + size, pos.y + size, pos.z, color.x, color.y, color.z, 0, 1, backNormal.x, backNormal.y, backNormal.z,
                //bottom
                pos.x, pos.y, pos.z, color.x, color.y, color.z, 0, 0, bottomNormal.x, bottomNormal.y, bottomNormal.z,
                pos.x + size, pos.y, pos.z, color.x, color.y, color.z, 1, 0, bottomNormal.x, bottomNormal.y, bottomNormal.z,
                pos.x + size, pos.y, pos.z + size, color.x, color.y, color.z, 1, 1, bottomNormal.x, bottomNormal.y, bottomNormal.z,
                pos.x, pos.y, pos.z + size, color.x, color.y, color.z, 0, 1, bottomNormal.x, bottomNormal.y, bottomNormal.z,
                //left
                pos.x, pos.y, pos.z, color.x, color.y, color.z, 0, 0, leftNormal.x, leftNormal.y, leftNormal.z,
                pos.x, pos.y, pos.z + size, color.x, color.y, color.z, 1, 0, leftNormal.x, leftNormal.y, leftNormal.z,
                pos.x, pos.y + size, pos.z + size, color.x, color.y, color.z, 1, 1, leftNormal.x, leftNormal.y, leftNormal.z,
                pos.x, pos.y + size, pos.z, color.x, color.y, color.z, 0, 1, leftNormal.x, leftNormal.y, leftNormal.z,
                //right
                pos.x + size, pos.y, pos.z + size, color.x, color.y, color.z, 0, 0, rightNormal.x, rightNormal.y, rightNormal.z,
                pos.x + size, pos.y, pos.z, color.x, color.y, color.z, 1, 0, rightNormal.x, rightNormal.y, rightNormal.z,
                pos.x + size, pos.y + size, pos.z, color.x, color.y, color.z, 1, 1, rightNormal.x, rightNormal.y, rightNormal.z,
                pos.x + size, pos.y + size, pos.z + size, color.x, color.y, color.z, 0, 1, rightNormal.x, rightNormal.y, rightNormal.z,};
        }
        return data;
    }

    public boolean intersectsWithLine(Vector3f point1, Vector3f point2) {

        //for each plane of the tile
        // a plane is a combination the top and origin coordinates and one axis is equal
        Vector3f topleft = new Vector3f();
        Vector3f bottomleft = new Vector3f();
        Vector3f topright = new Vector3f();
        float[] vertexData = getData();

        int floatsPerVertx = 11;
        int vertexPerFace = 4;
        int nrOfFaces=6;
        int topleftIndex;
        int toprightIndex;
        int bottomleftIndex;
        for (int i = 0; i < nrOfFaces; i++) {
            //define the 3 vertices of a face of the cube
            topleftIndex = (i * vertexPerFace + 3) * floatsPerVertx;
            toprightIndex = (i * vertexPerFace + 2) * floatsPerVertx;
            bottomleftIndex = (i * vertexPerFace + 0) * floatsPerVertx;
            topleft.x = vertexData[topleftIndex + 0];
            topleft.y = vertexData[topleftIndex + 1];
            topleft.z = vertexData[topleftIndex + 2];
            bottomleft.x = vertexData[bottomleftIndex + 0];
            bottomleft.y = vertexData[bottomleftIndex + 1];
            bottomleft.z = vertexData[bottomleftIndex + 2];
            topright.x = vertexData[toprightIndex + 0];
            topright.y = vertexData[toprightIndex + 1];
            topright.z = vertexData[toprightIndex + 2];
            if (interstectFaceWithLine(point1, point2, topleft, topright, bottomleft)) {
                return true;
            }
        }
        return false;
    }

    private boolean interstectFaceWithLine(Vector3f linepoint1, Vector3f linepoint2, Vector3f topleft, Vector3f topright, Vector3f bottomleft) {

        // 1. calculate plane normal
        Vector3f strtl = topright.subtract(topleft);
        Vector3f sbltl = bottomleft.subtract(topleft);
        Vector3f normal = strtl.cross(sbltl);

        // 2. check if ray parralel to plane
        Vector3f dR = linepoint1.subtract(linepoint2);

        float ndotdR = normal.dot(dR);

        if (Math.abs(ndotdR) < 1e-6f) { // Choose your tolerance
            return false;
        }

        float t = -normal.dot(linepoint1.subtract(topleft)) / ndotdR;
        //M is the point intersection
        Vector3f M = linepoint1.add(dR.scale(t));

        // 3. calculate u,v the projected/normalised coord of the ray onto the plane
        Vector3f dMS1 = M.subtract(topleft);
        float u = dMS1.dot(strtl);
        float v = dMS1.dot(sbltl);

        // 4. check if u,v are in the plane
        return (u >= 0.0f && u <= strtl.dot(strtl)
                && v >= 0.0f && v <= sbltl.dot(sbltl));
    }

}
