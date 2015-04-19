/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import game.Light.Orb;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import lwjglUtil.Shader;
import lwjglUtil.ShaderProgram;
import lwjglUtil.Texture;
import lwjglUtil.VertexArrayObject;
import lwjglUtil.VertexBufferObject;
import math.Matrix4f;
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
 * @author Harald
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
    public static final int TARGET_FPS = 60;
    public static final int TARGET_UPS = 30;
    public Board board;

    public Game() {
        //create board
        board = new Board(this);
        //init all opengl shizzle
        enter();
        //construct player
        Tile start = board.getRandomTile();
        player = new Player(start, playerCube, this, 0, new Camera(.2f));
        //add all tiles to a list

    }

    public void gameLoop(long window) {
        float delta;
        float accumulator = 0f;
        float interval = 1f / TARGET_UPS;
        float alpha;
        while ((glfwWindowShouldClose(window) == GL_FALSE)) {

            /* Get delta time and updateLogic the accumulator */
            delta = timer.getDelta();
            accumulator += delta;

            /* Handle input */
            input();

            /* Update game and timer UPS if enough time has passed */
            while (accumulator >= interval) {
                //fixed loop
                update();
                timer.updateUPS();
                accumulator -= interval;
            }

            /* Calculate alpha value for interpolation */
            alpha = accumulator / interval;

            /* Render game and updateLogic timer FPS */
            render();
            timer.updateFPS();
            //update player render
            player.updateRender();

            /* Update timer */
            timer.update();
            //mandatory after render
            glfwSwapBuffers(window); // swap the color buffers
            //show FPS and UPS
            glfwSetWindowTitle(window, "FPS: " + timer.getFPS() + " UPS: " + timer.getUPS());

        }
    }

    private void render() {
        int veoPos = 0;
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        //very important!! which face is hidden by other, HUH!?
        glEnable(GL11.GL_DEPTH_TEST);
        vao.bind();
        texture.bind();
        program.use();
        //model*view matrix
        Matrix4f modelview = player.getModelView();

        //set modelview matrix back to normal
        program.setUniform(uniModelView, modelview);
        //draw actual cubes
        glDrawElements(GL_TRIANGLES, reflectedCubes * Cube.getNrOfElements(), GL_UNSIGNED_INT, 0);
        veoPos += reflectedCubes * Cube.getNrOfElements();

        //draw shade  plane, textured cube
        switchTexture(shadePlaneTexture);
        glDrawElements(GL_TRIANGLES, nrOfTextureCubes * Cube.getNrOfElements(), GL_UNSIGNED_INT, veoPos);
        veoPos += nrOfTextureCubes * Cube.getNrOfElements();

        //draw board, colored cubes
        switchTexture(color);
        glDrawElements(GL_TRIANGLES,  ((nrOfCubes) * Cube.getNrOfElements())-veoPos, GL_UNSIGNED_INT, veoPos);

        //draw reflection  of first n cubes last
        //flip and draw blended, no stencil buffer used
        //flip over y plane
        glEnable(GL_BLEND);
        //needs seperate transform to be able to rotate around correct axis
        //reflective plane height
        program.setUniform(uniModelView, modelview.multiply(Matrix4f.translate(0, getReflectionSurfaceY(), 0).multiply(Matrix4f.scale(1, -1, 1).multiply(Matrix4f.translate(0, -getReflectionSurfaceY(), 0)))));
        //show over other cubes
        glDisable(GL11.GL_DEPTH_TEST);

        glDrawElements(GL_TRIANGLES, reflectedCubes * Cube.getNrOfElements(), GL_UNSIGNED_INT, 0);
        glEnable(GL11.GL_DEPTH_TEST);
        glDisable(GL_BLEND);
        
        //set modelview matrix back to normal
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

        projectionMatrix = Matrix4f.perspective(90f / zoom, ratio, 0.1f, 1000f);

        int uniProjection = program.getUniformLocation("projection");
        program.setUniform(uniProjection, projectionMatrix);

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

    private Texture texture;
    private Texture color;
    private Texture shadePlaneTexture;

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
        shadePlaneTexture = Texture.loadTexture("resources/test.png");
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
        //the first cube is the character, moves and is reflected
        playerCube = new Cube(new Vector3f(0, 0, 0), 1, new Vector3f(1, 0, 1));
        cubes.add(playerCube);
        reflectedCubes = 1;
        //add ceiling and floor 
        Vector3f boardRootOrigin = board.root.getDrawOriginPosition();
        cubes.add(new Cube(new Vector3f(boardRootOrigin.x, boardRootOrigin.y + 10, boardRootOrigin.z), board.root.getAbsSize(), new Vector3f(1, 1, 1)));
        cubes.add(new Cube(new Vector3f(boardRootOrigin.x, boardRootOrigin.y - 10, boardRootOrigin.z), board.root.getAbsSize(), new Vector3f(1, 1, 1)));
        nrOfTextureCubes = 2;
        //add board to cubes
        board.getTilesToCube().values().stream().forEach((cube) -> {
            cubes.add(cube);
        });
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
        Vector3f lightDir = new Vector3f(1, -1, 0);
        Vector3f lightColor = new Vector3f(1, 1, 1);
        float ambientlight = 0.2f;
        int sunLightColor = program.getUniformLocation("moodLight.vertexColor");
        int sunLightDirection = program.getUniformLocation("moodLight.vertexDirection");
        int sunLightintensity = program.getUniformLocation("moodLight.fAmbientIntensity");
        GL20.glUniform3f(sunLightColor, lightColor.x, lightColor.y, lightColor.z);
        GL20.glUniform3f(sunLightDirection, lightDir.x, lightDir.y, lightDir.z);
        GL20.glUniform1f(sunLightintensity, ambientlight);

        /* Set orb light struct*/
        Vector3f orbLightColor = new Vector3f(0f, 0.3f, 0.3f);
        Vector3f orbLightPosition = new Vector3f(boardRootOrigin.x, boardRootOrigin.y + 5, boardRootOrigin.z);
        float constantAttenuation = 0.2f;
        float linearAttenuation = 0.01f;

        int orbLightColorLoc = program.getUniformLocation("orbLight.vertexColor");
        int orbLightPositionLoc = program.getUniformLocation("orbLight.vertexPosition");
        int orbLightConstantAttenuationLoc = program.getUniformLocation("orbLight.fConstantAttenuation");
        int orbLightLinearAttenuationLoc = program.getUniformLocation("orbLight.fLinearAttenuation");
        int orbLightAmbientIntensityLoc = program.getUniformLocation("orbLight.fAmbientIntensity");

        GL20.glUniform3f(orbLightColorLoc, orbLightColor.x, orbLightColor.y, orbLightColor.z);
        GL20.glUniform3f(orbLightPositionLoc, orbLightPosition.x, orbLightPosition.y, orbLightPosition.z);
        GL20.glUniform1f(orbLightConstantAttenuationLoc, constantAttenuation);
        GL20.glUniform1f(orbLightLinearAttenuationLoc, linearAttenuation);
        GL20.glUniform1f(orbLightAmbientIntensityLoc, ambientlight);

        /* Set projection matrix to an orthographic projection */
        setResolution(widthBuffer.get(), heightBuffer.get());

        //set blend paramaters
        uniAlpha = program.getUniformLocation("alpha");
        GL20.glUniform1f(uniAlpha, 0.2f);
        glBlendEquationSeparate(GL_FUNC_ADD, GL_FUNC_ADD);
        glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);

        glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
    }
    private float width;
    private float height;

    public void setOrb(Orb orb) {
        Vector3f orbLightColor = new Vector3f(0f, 0.3f, 0.3f);
        Vector3f orbLightPosition = orb.getPosition();
        float constantAttenuation = 0.2f;
        float linearAttenuation = 0.005f;

        int orbLightColorLoc = program.getUniformLocation("orbLight.vertexColor");
        int orbLightPositionLoc = program.getUniformLocation("orbLight.vertexPosition");
        int orbLightConstantAttenuationLoc = program.getUniformLocation("orbLight.fConstantAttenuation");
        int orbLightLinearAttenuationLoc = program.getUniformLocation("orbLight.fLinearAttenuation");
        int orbLightAmbientIntensityLoc = program.getUniformLocation("orbLight.fAmbientIntensity");

        GL20.glUniform3f(orbLightColorLoc, orbLightColor.x, orbLightColor.y, orbLightColor.z);
        GL20.glUniform3f(orbLightPositionLoc, orbLightPosition.x, orbLightPosition.y, orbLightPosition.z);
        GL20.glUniform1f(orbLightConstantAttenuationLoc, constantAttenuation);
        GL20.glUniform1f(orbLightLinearAttenuationLoc, linearAttenuation);
        // GL20.glUniform1f(orbLightAmbientIntensityLoc, ambientlight);
    }
    private Orb orb;

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
    private double test;

    //continous updateLogic of the world
    public void update() {
        player.updateLogic(TARGET_UPS);
        //orb.step();
    }

    public void input() {
        // Poll for window events. The key callback above will only be
        // invoked during this call.
        glfwPollEvents();
    }
}
