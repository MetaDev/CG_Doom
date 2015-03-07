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
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
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

    private final CharSequence vertexSource
            = "#version 150 core\n"
            + "\n"
            + "in vec3 position;\n"
            + "in vec3 color;\n"
            + "\n"
            + "out vec3 vertexColor;\n"
            + "\n"
            + "uniform mat4 model;\n"
            + "uniform mat4 view;\n"
            + "uniform mat4 projection;\n"
            + "\n"
            + "void main() {\n"
            + "    vertexColor = color;\n"
            + "    mat4 mvp = projection * view * model;\n"
            + "    gl_Position = mvp * vec4(position, 1.0);\n"
            + "}";
    private final CharSequence fragmentSource
            = "#version 150 core\n"
            + "\n"
            + "in vec3 vertexColor;\n"
            + "\n"
            + "out vec4 fragColor;\n"
            + "\n"
            + "void main() {\n"
            + "    fragColor = vec4(vertexColor, 1.0);\n"
            + "}";
    private VertexArrayObject vao;
    private VertexBufferObject vbo;
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
    public static final int TARGET_UPS = 60;
    public Board board;
    public Game() {
       board=new Board();
       Tile root = board.root;
       float rootSize=root.getAbsSize();
       
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
                update();
                timer.updateUPS();
                accumulator -= interval;
            }

            /* Calculate alpha value for interpolation */
            //alpha = accumulator / interval;

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
        program.use();

        // float lerpAngle = (1f - alpha) * previousAngle + alpha * angle;
        Matrix4f model = board.player.lookThrough();
        program.setUniform(uniModel, model);

        glDrawArrays(GL_TRIANGLES, 0, amountOfVertices);
         //glDrawArrays(GL11.GL_LINE_LOOP, 0, amountOfVertices);
    }

    public void resolutionChanged() {
        /* Get width and height for calculating the ratio */
        long window = GLFW.glfwGetCurrentContext();
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        GLFW.glfwGetFramebufferSize(window, width, height);
        float ratio = width.get() / (float) height.get();

        Matrix4f projection = Matrix4f.perspective(90f, ratio, 0.1f, 100f);

        int uniProjection = program.getUniformLocation("projection");
        program.setUniform(uniProjection, projection);
    }
    public int amountOfVertices;

    public void cubesToDraw(List<Cube> cubes) {
        int amountOfFloats = (cubes.size() * cubes.get(0).getData().length);
        amountOfVertices = amountOfFloats / 6;
        /* Vertex data */

        FloatBuffer vertices = BufferUtils.createFloatBuffer(amountOfFloats);
        for (Cube c : cubes) {
            vertices.put(c.getData());
        }
        vertices.flip();

        /* Generate Vertex Buffer Object */
        vbo = new VertexBufferObject();
        vbo.bind(GL_ARRAY_BUFFER);
        vbo.uploadData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
    }

    public void enter() {

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the ContextCapabilities instance and makes the OpenGL
        // bindings available for use.
        GLContext.createFromCurrent();
        System.out.println("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION));
        /* Generate Vertex Array Object */
        vao = new VertexArrayObject();
        vao.bind();
        
        List<Cube> cubes = new ArrayList<>();
       
        //cubes.add(cube);
        cubes.add(new Cube(new Vector3f(-5f, -5f, -10f), 5f, new Vector3f(0, 1, 0)));
        cubes.add(new Cube(new Vector3f(-5f, -5f, -5f), 5f, new Vector3f(0, 1, 1)));
        cubes.add(new Cube(new Vector3f(0f, -5f, -5f), 5f, new Vector3f(1, 0, 1)));
        //cubes.add(new Cube(new Vector3f(0f, -5f, -10f), 5f, new Vector3f(0, 0, 1)));
        cubesToDraw(board.getBoardAsCubes());
        //cubesToDraw(cubes);
        
        
        
        
        /* Load shaders */
        vertexShader = new Shader(GL_VERTEX_SHADER, vertexSource);
        fragmentShader = new Shader(GL_FRAGMENT_SHADER, fragmentSource);

        /* Create shader program */
        program = new ShaderProgram();
        program.attachShader(vertexShader);
        program.attachShader(fragmentShader);
        //program.bindFragmentDataLocation(0, "fragColor");
        program.link();
        program.use();

        specifyVertexAttributes();

        /* Get uniform location for the model matrix */
        uniModel = program.getUniformLocation("model");

        /* Set view matrix to identity matrix */
        Matrix4f view = new Matrix4f();
        int uniView = program.getUniformLocation("view");
        program.setUniform(uniView, view);

        /* Get width and height for calculating the ratio */
        long window = GLFW.glfwGetCurrentContext();
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        GLFW.glfwGetFramebufferSize(window, width, height);
        float ratio = width.get() / (float) height.get();

        Matrix4f projection = Matrix4f.perspective(90f, ratio, 0.1f, 100f);

        int uniProjection = program.getUniformLocation("projection");
        program.setUniform(uniProjection, projection);

        glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
    }

    public void exit() {
        vao.delete();
        vbo.delete();
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
        program.pointVertexAttribute(posAttrib, 3, 6 * Float.BYTES, 0);

        /* Specify Color Pointer */
        int colAttrib = program.getAttributeLocation("color");
        program.enableVertexAttribute(colAttrib);
        program.pointVertexAttribute(colAttrib, 3, 6 * Float.BYTES, 3 * Float.BYTES);
    }

   

    //continous update of the world
    public void update() {

    }

    public void input() {
        // Poll for window events. The key callback above will only be
        // invoked during this call.
        glfwPollEvents();
    }
}
