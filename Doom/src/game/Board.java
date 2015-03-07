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
import render.Camera;
import render.Cube;

/**
 *
 * @author Harald
 */
public class Board {

    public Player player;
    public Tile root;

    public Board() {
        //set root 50 to the left and 50 forward
        root = new Tile(new Vector3f(1, 1, 1), 10,-5,-5);
        player= new Player(0, 0, 0, new Camera(1));
        createRandomBoard();
    }


    public void createRandomBoard() {

        int iterations = 100;
        Random rn = new Random();
        // the more iterations the more fractioned tiles
        int randFraction;
        for (int i = 0; i < iterations; i++) {

            // pick random fraction 2,4,8
            randFraction = (int) Math.pow(2, 1 + rn.nextInt(3));

            // now choose random tile
            getRandomTile().divide(randFraction);

        }
    }

    // return a tile with an abs fraction smaller then the one given
    public Tile getRandomTile() {
        Tile tileIt;
        tileIt = root;
        // now choose random tile
        int randCol;
        int randRow;
        Random rn = new Random();
        while (tileIt.getChildren() != null) {
            randCol = rn.nextInt(tileIt.getChildren().length);
            randRow = rn.nextInt(tileIt.getChildren().length);
            tileIt = tileIt.getChildren()[randCol][randRow];
        }
        return tileIt;
    }

    public List<Cube> getBoardAsCubes() {
        List<Cube> cubes = new ArrayList<>();
        recursiveBoardAsCubes(root, cubes);
        return cubes;
    }

    private void recursiveBoardAsCubes(Tile tile, List<Cube> cubes) {
        //if tile has no children draw it
        if (tile.getChildren() == null) {
            cubes.add(new Cube(tile.getDrawPosition(), tile.getAbsSize(), tile.getColor()));
        } else {
            //iterate all children
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    recursiveBoardAsCubes(tile.getChildren()[i][j], cubes);
                }
            }
        }

    }
}
