/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import math.Vector3f;

/**
 *
 * @author Harald
 */
public class Light {

    //directional
    private Vector3f lightDir;
    private Vector3f lightColor;
    private float ambientlight;

    public enum OrbAxis {

        X, Y, Z
    }

    //point light
    public class Orb {

        private Vector3f color;
        private Vector3f center;
        private float radius;
        private float speed;
        private float ambientIntensity;
        private float angle;
        private Vector3f pos;
        private float constantAttenuation = 0.2f;
        private float linearAttenuation = 0.005f;
        //TODO
        //rotate around specified axis

        public void step() {
            angle += speed;
            if (angle > Math.PI * 2) {
                angle -= Math.PI * 2;
            }
        }

        public Vector3f getPosition() {
            pos.x = center.x + radius * (float) Math.cos(angle);
            pos.y = center.y;
            pos.z = center.z + radius * (float) Math.sin(angle);
            return pos;
        }
    }
}
