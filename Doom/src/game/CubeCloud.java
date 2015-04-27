/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import math.Vector3f;
import render.Cube;

/**
 *
 * @author Harald
 */
public class CubeCloud {

    //generate arandom cloud of cubes 
    public static List<Cube> constructCubes(float fillFactor, int base,int levels, float size, Vector3f position, Vector3f color) {
        Random rn = new Random();
        List<Cube> cubes = new ArrayList<>();
        float cubeSize = size / base;
        //iterate 3 dimensions
        for (int i = 0; i < base; i++) {
            for (int j = 0; j < levels; j++) {
                for (int k = 0; k < base; k++) {
                    //next int retruns 0 <= x <= 1
                     if(rn.nextFloat()<= fillFactor){
                         // add cube at indexed location
                         cubes.add(new Cube(new Vector3f(position.x+i*cubeSize, position.y+j*cubeSize, position.z+k*cubeSize), cubeSize, color));
                     }
                }
            }
        }
        return cubes;

    }
}
