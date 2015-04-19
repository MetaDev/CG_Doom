/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import math.Vector3f;
import render.Cube;

/**
 *
 * @author Harald
 */
public class Board {

    public Tile root;
    public float rootSize = 10;
    private Game currentGame;
    //keep a list of tiles consisting the board for iteration convinience
    private Map<Tile, Cube> tilesToCube;

    public Board(Game game) {
        currentGame = game;
        //set root 50 to the left and 50 forward
        root = new Tile(new Vector3f(1, 1, 1), rootSize, -rootSize / 2, -rootSize / 2);
        createRandomBoard();

        constructCubes();
    }

    public Game getCurrentGame() {
        return currentGame;
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
    private Set<Cube> cubes;

    public void constructCubes() {
        tilesToCube = new HashMap<>();
        recursiveBoardAsCubes(root, tilesToCube);
        cubes = new HashSet<>(tilesToCube.values());
    }

    private void recursiveBoardAsCubes(Tile tile, Map<Tile, Cube> tilesToCube) {
        //if tile has no children draw it
        if (tile.getChildren() == null) {
            tilesToCube.put(tile, new Cube(tile.getDrawOriginPosition(), tile.getAbsSize(), tile.getColor()));
        } else {
            //iterate all children
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    recursiveBoardAsCubes(tile.getChildren()[i][j], tilesToCube);
                }
            }
        }

    }

    public Map<Tile, Cube> getTilesToCube() {
        return tilesToCube;
    }

    public Cube getRandomCube() {
        int size = cubes.size();
        int item = new Random().nextInt(size); // In real life, the Random object should be rather more shared than this
        int i = 0;
        for (Cube obj : cubes) {
            if (i == item) {
                return obj;
            }
            i = i + 1;
        }
        return null;
    }

    public void removeCubeFromBoard(Cube cube) {
        cubes.remove(cube);
        currentGame.removeCubeScene(cube);
    }

    public Cube getCubeOfTile(Tile tile) {
        return tilesToCube.get(tile);
    }
    public Tile getTileOfCube(Cube cube){
        for(Map.Entry<Tile,Cube> entry:tilesToCube.entrySet()){
            if(entry.getValue().equals(cube)){
                return entry.getKey();
            }
        }
        return null;
    }
    public Set<Cube> getCubes() {
        return cubes;
    }

    //return the cube closest to ray1 and closer to ray 2 than ray 1 (in front of player)
    public Cube getClosestCubeInFrontByRay(Vector3f ray1, Vector3f ray2) {
        List<Cube> cubesHit = new ArrayList<>();
        //first find all tiles intersecting with ray
        for (Cube c : cubes) {
            if (c.intersectsWithLine(ray1, ray2)) {
                cubesHit.add(c);
            }
        }
        float tempDist = Float.MAX_VALUE;
        Cube closest = null;
        //iterate found cubes and decide which one fits the property defined above
        for (Cube c : cubesHit) {
            float distanceToRay1 = c.distanceWithPoint(ray1);
            float distanceToRay2 = c.distanceWithPoint(ray2);
            //if in front
            if (distanceToRay1 < distanceToRay2 && distanceToRay1 < tempDist) {
                tempDist = distanceToRay1;
                closest = c;
            }
        }
        return closest;
    }

}
