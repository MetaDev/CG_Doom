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
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
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

    private int uniModel;
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
        board = new Board(this);

    }

    public void gameLoop(long window) {
        float delta;
        float accumulator = 0f;
        float interval = 1f / TARGET_UPS;
        float alpha;
        while ((glfwWindowShouldClose(window) == GL_FALSE)) {

            /* Get delta time and update the accumulator */
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

            /* Render game and update timer FPS */
            render();
            timer.updateFPS();

            /* Update timer */
            timer.update();
            //mandatory after render
            glfwSwapBuffers(window); // swap the color buffers
            //show FPS and UPS
            glfwSetWindowTitle(window, "FPS: " + timer.getFPS() + " UPS: " + timer.getUPS());

        }
    }

    private void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        //very important!! which face is hidden by other, HUH!?
        glEnable(GL11.GL_DEPTH_TEST);
        vao.bind();
        texture.bind();
        program.use();

        Matrix4f model = board.player.lookThrough();
        program.setUniform(uniModel, model);
        //draw shade plane
        switchTexture(shadePlaneTexture);
        glDrawElements(GL_TRIANGLES, nrOfTextureCubes * Cube.getNrOfElements(), GL_UNSIGNED_INT, 0);
        //draw board
        switchTexture(color);
        glDrawElements(GL_TRIANGLES, (nrOfCubes) * Cube.getNrOfElements(), GL_UNSIGNED_INT, nrOfTextureCubes * Cube.getNrOfElements());
    }
    private Matrix4f projectionMatrix;
    public void resolutionChanged(float width, float height) {

        float ratio = width / height;

        projectionMatrix = Matrix4f.perspective(90f, ratio, 0.1f, 1000f);

        int uniProjection = program.getUniformLocation("projection");
        program.setUniform(uniProjection, projectionMatrix);
    }
    public int nrOfCubes;

    public void addCubeToScene(Cube cube) {
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

        //set amount of cubes rendered with which texture
    }
    private Texture texture;
    private Texture color;
    private Texture shadePlaneTexture;

    private void switchTexture(Texture tex) {
        texture = tex;
        texture.bind();

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
        int width = widthBuffer.get();
        int height = heightBuffer.get();

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
        //add ceiling and floor 
        Vector3f boardRootOrigin = board.root.getDrawOriginPosition();
        cubes.add(new Cube(new Vector3f(boardRootOrigin.x, boardRootOrigin.y+10, boardRootOrigin.z),board.root.getAbsSize(), new Vector3f(1, 1, 1)));
        cubes.add(new Cube(new Vector3f(boardRootOrigin.x, boardRootOrigin.y-10,boardRootOrigin.z), board.root.getAbsSize(), new Vector3f(1, 1, 1)));
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
        uniModel = program.getUniformLocation("model");
        program.setUniform(uniModel, model);

        /* Set view matrix to identity matrix */
        Matrix4f view = new Matrix4f();
        int uniView = program.getUniformLocation("view");
        program.setUniform(uniView, view);

        /* Set projection matrix to an orthographic projection */
        float ratio = width / height;
        resolutionChanged(width,height);
        

        glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
    }

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
        program.pointVertexAttribute(posAttrib, 3, 8 * Float.BYTES, 0);

        /* Specify Color Pointer */
        int colAttrib = program.getAttributeLocation("color");
        program.enableVertexAttribute(colAttrib);
        program.pointVertexAttribute(colAttrib, 3, 8 * Float.BYTES, 3 * Float.BYTES);

        /* Specify Texture Pointer */
        int texAttrib = program.getAttributeLocation("texcoord");
        program.enableVertexAttribute(texAttrib);
        program.pointVertexAttribute(texAttrib, 2, 8 * Float.BYTES, 6 * Float.BYTES);
    }

    //continous update of the world
    public void update() {
        board.player.update(TARGET_UPS);
    }

    public void input() {
        // Poll for window events. The key callback above will only be
        // invoked during this call.
        glfwPollEvents();
    }
}
