/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

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
    public static  List<Cube> constructCubes(float fillFactor, int levels, float size,  Vector3f position) {
        Random rn = new Random();
        float cubeSize=size/levels;
        //iterate 3 dimensions
        for(int i=0;i<levels;i++){
            
        }
        return null;

    }
}
