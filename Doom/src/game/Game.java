/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lwjglUtil.Shader;
import lwjglUtil.ShaderProgram;
import lwjglUtil.Texture;
import lwjglUtil.VertexArrayObject;
import lwjglUtil.VertexBufferObject;
import math.Matrix4f;
import math.Util;
import math.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetWindowTitle;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.GL_ZERO;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL14.GL_FUNC_ADD;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import org.lwjgl.opengl.GL20;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glBlendEquationSeparate;
import org.lwjgl.opengl.GLContext;
import render.Cube;

/**
 *
 * @author Tim
 */
public class Game {

    private VertexArrayObject vao;
    private VertexBufferObject vbo;
    private VertexBufferObject ebo;

    private Shader vertexShader;
    private Shader fragmentShader;
    private ShaderProgram program;
    public Player player;

    private int uniModelView;
    private int uniAlpha;
    private float previousAngle = 0f;
    private float angle = 0f;
    private final float angelPerSecond = 3f;
    /**
     * Used for timing calculations.
     */
    protected Timer timer = new Timer();
    public static final int TARGET_UPS = 60;
    public Board board;

    public Game() {
        //create board
        board = new Board(this);
        //init all opengl shizzle
        enter();
        //construct player
         Tile start ;
        if(tutorial){
            start =board.getRandomTile();
        }else{
            start=board.getRandomTile();
        }

        player = new Player(start, playerCube, this);
        //add all tiles to a list

    }

    public void gameLoop(long window) {
        float delta;
        timer.init();
        int frames = 0;
        while ((glfwWindowShouldClose(window) == GL_FALSE)) {

            /* Get delta time and updateLogic the accumulator */
            delta = timer.getDelta();
            /* Handle input */
            input();

            //fixed loop
            timer.updateUPS();

            update(delta);


            /* Render game and updateLogic timer FPS */
            timer.updateFPS();
            render(frames);

            /* Update timer */
            timer.update();
            //mandatory after render
            glfwSwapBuffers(window); // swap the color buffers
            //show FPS and UPS
            glfwSetWindowTitle(window, "FPS/UPS: " + timer.getFPS());

            frames++;
            //reset frames
            if (frames > 60 * cloudsLoopSec) {
                frames = frames % (60 * cloudsLoopSec);
            }
        }
    }

    private void render(int frame) {
        int veoPos = 0;
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        //very important!! which face is hidden by other, HUH!?
        glEnable(GL11.GL_DEPTH_TEST);
        //model*view matrix
        Matrix4f modelview = player.getModelView();

        //set modelview matrix back to normal
        program.setUniform(uniModelView, modelview);
        //draw actual cubes
        switchTexture(color);
        glDrawElements(GL_TRIANGLES, reflectedCubes * Cube.getNrOfElements(), GL_UNSIGNED_INT, 0);
        veoPos += reflectedCubes * Cube.getNrOfElements();

        //draw shade  plane, textured cube
        switchTexture(clouds.get(frame / (60 / clouds.size() * cloudsLoopSec) % clouds.size()));
        glDrawElements(GL_TRIANGLES, veoPos + nrOfTextureCubes * Cube.getNrOfElements(), GL_UNSIGNED_INT, veoPos);
        veoPos += nrOfTextureCubes * Cube.getNrOfElements();

        //draw board, colored cubes
        switchTexture(color);
        glDrawElements(GL_TRIANGLES, ((nrOfCubes) * Cube.getNrOfElements()) - veoPos, GL_UNSIGNED_INT, veoPos);

        //draw reflection  of first n cubes last
        //flip and draw blended, no stencil buffer used
        //flip over y plane
        //needs seperate transform to be able to rotate around correct axis
        //reflective plane height
        program.setUniform(uniModelView, modelview.multiply(Matrix4f.translate(0, getReflectionSurfaceY(), 0).multiply(Matrix4f.scale(1, -1, 1).multiply(Matrix4f.translate(0, -getReflectionSurfaceY(), 0)))));
        //show over other cubes
        glDisable(GL11.GL_DEPTH_TEST);
        glEnable(GL_BLEND);

        glDrawElements(GL_TRIANGLES, reflectedCubes * Cube.getNrOfElements(), GL_UNSIGNED_INT, 0);
        glDisable(GL_BLEND);
        glEnable(GL11.GL_DEPTH_TEST);

        program.setUniform(uniModelView, modelview);
        glDrawElements(GL_TRIANGLES, reflectedCubes * Cube.getNrOfElements(), GL_UNSIGNED_INT, 0);

    }
    private Matrix4f projectionMatrix;
    int reflectedCubes = 0;

    private float getReflectionSurfaceY() {
        return player.getCharCubeY();
    }

    private void updateProjectionMatrix() {
        float ratio = width / height;

        projectionMatrix = Matrix4f.perspective(90f / zoom, ratio, Math.max(0.1f / zoom, 0.0001f), 1000f);

        int uniProjection = program.getUniformLocation("projection");
        program.setUniform(uniProjection, projectionMatrix);
        //also the vao has to be updated
        vao.bind();

    }

    public void setResolution(float width, float height) {
        this.width = width;
        this.height = height;
        updateProjectionMatrix();
    }
    private float zoom;

    public void setZoom(float zoom) {

        this.zoom = zoom;
        updateProjectionMatrix();
    }
    public int nrOfCubes;

    public void addCubeToScene(Cube cube) {
        //add to head
        scene.add(cube);
        bindSceneForRendering();
    }

    public void removeCubeScene(Cube cube) {
        scene.remove(cube);
        bindSceneForRendering();
    }
    private int nrOfTextureCubes;
    public List<Cube> scene;

    public void bindSceneForRendering() {
        nrOfCubes = scene.size();
        //calculate amount of floats of all cubes
        int amountOfFloats = (nrOfCubes * Cube.getNrOfFloats());
        /* Vertex data */

        FloatBuffer vertices = BufferUtils.createFloatBuffer(amountOfFloats);
        for (Cube c : scene) {
            vertices.put(c.getData());
        }
        vertices.flip();

        vbo.uploadData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        /* Element data */
        IntBuffer elements = BufferUtils.createIntBuffer(Cube.getNrOfElements() * scene.size());
        int cubeIndex = 0;
        for (Cube c : scene) {
            elements.put(c.getVertexIndices(cubeIndex));
            cubeIndex++;
        }
        elements.flip();

        ebo.uploadData(GL_ELEMENT_ARRAY_BUFFER, elements, GL_STATIC_DRAW);

    }
 private int cloudsLoopSec = 8;
    private Texture texture;

    
    private Texture color;
    private List<Texture> clouds;
   

    private void switchTexture(Texture tex) {
        texture = tex;
        texture.bind();

    }

    public void movePointLight(Vector3f newPostion) {

    }
    private Cube playerCube;

    public Cube getPlayerCube() {
        return playerCube;
    }

    public static boolean tutorial = true;

    public void enter() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the ContextCapabilities instance and makes the OpenGL
        // bindings available for use.
        GLContext.createFromCurrent();
        /* Get width and height of framebuffer */
        long window = GLFW.glfwGetCurrentContext();
        IntBuffer widthBuffer = BufferUtils.createIntBuffer(1);
        IntBuffer heightBuffer = BufferUtils.createIntBuffer(1);
        GLFW.glfwGetFramebufferSize(window, widthBuffer, heightBuffer);

        /* Create textures */
        color = Texture.loadTexture("resources/white.png");
        //load all clouds
        clouds = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            clouds.add(Texture.loadTexture("resources/cloud/" + i + ".png"));
        }
        switchTexture(color);

        /* Generate Vertex Array Object */
        vao = new VertexArrayObject();
        vao.bind();
        /* Generate Vertex Buffer Object */
        vbo = new VertexBufferObject();
        vbo.bind(GL_ARRAY_BUFFER);
        /* Generate Element Buffer Object */
        ebo = new VertexBufferObject();
        ebo.bind(GL_ELEMENT_ARRAY_BUFFER);

        List<Cube> cubes = new ArrayList<>();
        Vector3f boardRootOrigin = board.root.getDrawOriginPosition();
        //the first cube is the character, moves and is reflected
        playerCube = new Cube(new Vector3f(0, 0, 0), 1f, new Vector3f(1, 0, 1));
        cubes.add(playerCube);
        reflectedCubes = 1;
        //add skybox
        cubes.add(new Cube(new Vector3f(boardRootOrigin.x, boardRootOrigin.y - board.root.getAbsSize(), boardRootOrigin.z), board.root.getAbsSize() * 2, new Vector3f(1, 1, 1)));
        nrOfTextureCubes += 1;
        if (!tutorial) {

            //add board to cubes
            board.getTilesToCube().values().stream().forEach((cube) -> {
                cubes.add(cube);
            });
            //add random cubeclouds to random tiles to fill the board
            int nrOfCubeClouds = 100;
            Random fillFactor = new Random();
            int minLevel = 5;
            int maxLevel = 50;
            int minBase = 1;
            int maxBase = 10;
            for (int i = 0; i < nrOfCubeClouds; i++) {
                Tile pos = board.getRandomTile();
                cubes.addAll(CubeCloud.constructCubes(fillFactor.nextFloat(), Util.randInt(minBase, maxBase), Util.randInt(minLevel, maxLevel), pos.getAbsSize() / 4, pos.getDrawCenterTopPosition(), pos.getColor()));
            }
        } else {
            cubes.add(new Cube(new Vector3f(0, 0, 0), 10, new Vector3f(1, 1, 1)));
            //first cube is rendered with thsi texture
            color=Texture.loadTexture("resources/test.png");
            switchTexture(color);

        }

        //set as scene scene
        scene = cubes;
        System.out.println(scene.size());

        bindSceneForRendering();
        /* Load shaders */
        vertexShader = Shader.loadShader(GL_VERTEX_SHADER, "resources/vertex.glsl");
        fragmentShader = Shader.loadShader(GL_FRAGMENT_SHADER, "resources/fragment.glsl");

        /* Create shader program */
        program = new ShaderProgram();
        program.attachShader(vertexShader);
        program.attachShader(fragmentShader);
        program.bindFragmentDataLocation(0, "fragColor");
        program.link();
        program.use();

        specifyVertexAttributes();

        /* Set texture uniform */
        int uniTex = program.getUniformLocation("texImage");
        program.setUniform(uniTex, 0);
        /* Set model matrix to identity matrix */
        Matrix4f model = new Matrix4f();
        uniModelView = program.getUniformLocation("modelview");
        program.setUniform(uniModelView, model);

        /* Set mood light struct*/
        Vector3f lightDir = new Vector3f(1, 1, 0);
        Vector3f lightColor = new Vector3f(0.5f, 0.5f, 0.5f);
        float ambientlight = 0.4f;
        int sunLightColor = program.getUniformLocation("moodLight.vertexColor");
        int sunLightDirection = program.getUniformLocation("moodLight.vertexDirection");
        int sunLightintensity = program.getUniformLocation("moodLight.fAmbientIntensity");
        /* Set texture uniform */
        program.setUniform(sunLightColor, lightColor);
        program.setUniform(sunLightDirection, lightDir);
        program.setUniform(sunLightintensity, ambientlight);

        /* Set orb light struct*/
        Vector3f orbLightColor = new Vector3f(0.5f, 0.5f, 0.5f);
        Vector3f orbLightPosition = new Vector3f(0, 0, 0);
        float constantAttenuation = 0.2f;
        float linearAttenuation = 0.02f;
        float quadraticAttenuation = 0.005f;

        int orbLightColorLoc = program.getUniformLocation("orbLight.vertexColor");
        int orbLightPositionLoc = program.getUniformLocation("orbLight.vertexPosition");
        int orbLightConstantAttenuationLoc = program.getUniformLocation("orbLight.fConstantAttenuation");
        int orbLightLinearAttenuationLoc = program.getUniformLocation("orbLight.fLinearAttenuation");
        int orbLightQuadraticAttenuationLoc = program.getUniformLocation("orbLight.fQuadraticAttenuation");
        int orbLightAmbientIntensityLoc = program.getUniformLocation("orbLight.fAmbientIntensity");

        program.setUniform(orbLightColorLoc, orbLightColor);
        program.setUniform(orbLightPositionLoc, orbLightPosition);
        program.setUniform(orbLightConstantAttenuationLoc, constantAttenuation);
        program.setUniform(orbLightLinearAttenuationLoc, linearAttenuation);
        program.setUniform(orbLightQuadraticAttenuationLoc, quadraticAttenuation);
        program.setUniform(orbLightAmbientIntensityLoc, ambientlight);

        /* Set projection matrix to an orthographic projection */
        setResolution(widthBuffer.get(), heightBuffer.get());

        //set blend paramaters
        uniAlpha = program.getUniformLocation("alpha");
        program.setUniform(uniAlpha, 0.2f);
        glBlendEquationSeparate(GL_FUNC_ADD, GL_FUNC_ADD);
        glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);

        glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
    }
    private float width;
    private float height;

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public void exit() {
        vao.delete();
        vbo.delete();
        ebo.delete();
        color.delete();
        vertexShader.delete();
        fragmentShader.delete();
        program.delete();
    }

    /**
     * Specifies the vertex attributes.
     */
    private void specifyVertexAttributes() {
        /* Specify Vertex Pointer */
        int posAttrib = program.getAttributeLocation("position");
        program.enableVertexAttribute(posAttrib);
        program.pointVertexAttribute(posAttrib, 3, 11 * Float.BYTES, 0);

        /* Specify Color Pointer */
        int colAttrib = program.getAttributeLocation("color");
        program.enableVertexAttribute(colAttrib);
        program.pointVertexAttribute(colAttrib, 3, 11 * Float.BYTES, 3 * Float.BYTES);

        /* Specify Texture Pointer */
        int texAttrib = program.getAttributeLocation("texcoord");
        program.enableVertexAttribute(texAttrib);
        program.pointVertexAttribute(texAttrib, 2, 11 * Float.BYTES, 6 * Float.BYTES);
        /* Specify Normal Pointer */
        int normalAttrib = program.getAttributeLocation("normal");
        program.enableVertexAttribute(normalAttrib);
        program.pointVertexAttribute(normalAttrib, 3, 11 * Float.BYTES, 8 * Float.BYTES);
    }

    //continous updateLogic of the world
    public void update(float delta) {
        player.updateLogic(delta);
        //positoin point light above head of the player
        int orbLightPositionLoc = program.getUniformLocation("orbLight.vertexPosition");
        GL20.glUniform3f(orbLightPositionLoc, player.getDrawX(), player.getDrawY() + 1, player.getDrawZ());

    }

    public void input() {
        // Poll for window events. The key callback above will only be
        // invoked during this call.
        glfwPollEvents();
    }
}
