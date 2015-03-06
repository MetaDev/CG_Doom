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
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import org.lwjgl.opengl.GLContext;
import render.Camera;
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
    private boolean running = true;
    /**
     * Used for timing calculations.
     */
    protected Timer timer = new Timer();
    public static final int TARGET_FPS = 75;
    public static final int TARGET_UPS = 30;
    public Player player;
    public Game() {
        player= new Player(0,0,0,new Camera(0));
    }

    public void gameLoop(long window) {
        float delta;
        float accumulator = 0f;
        float interval = 1f / TARGET_UPS;
        float alpha;
        while (running && (glfwWindowShouldClose(window) == GL_FALSE)) {

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
            alpha = accumulator / interval;

            /* Render game and update timer FPS */
            update(alpha);
            render(alpha);
            timer.updateFPS();

            /* Update timer */
            timer.update();
            //mandatory after render
            glfwSwapBuffers(window); // swap the color buffers

        }
    }

    private void update(float delta) {
        previousAngle = angle;
        angle += delta * angelPerSecond;
    }

    private void render(float alpha) {
        glClear(GL_COLOR_BUFFER_BIT);

        vao.bind();
        program.use();

       // float lerpAngle = (1f - alpha) * previousAngle + alpha * angle;
        Matrix4f model = player.lookThrough();
        program.setUniform(uniModel, model);

        glDrawArrays(GL_TRIANGLES, 0, amountOfVertices);
    }
   
public int amountOfVertices;
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
        Cube cube = new Cube(new Vector3f(-0.5f, -0.5f, -5f),1f, new Vector3f(0, 1, 0));
        Cube cube1 = new Cube(new Vector3f(0.5f, 0.5f, -3f),1f, new Vector3f(1, 1, 0));
        List<Cube> cubes = new ArrayList<>();
        cubes.add(cube);
        cubes.add(cube1);
        int amountOfFloats = (cubes.size()*cubes.get(0).getData().length);
        amountOfVertices=amountOfFloats/6;
        /* Vertex data */
        
        FloatBuffer vertices = BufferUtils.createFloatBuffer(amountOfFloats);
        for(Cube c: cubes){
            vertices.put(c.getData());
        }
        vertices.flip();

        /* Generate Vertex Buffer Object */
        vbo = new VertexBufferObject();
        vbo.bind(GL_ARRAY_BUFFER);
        vbo.uploadData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
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

        /* Set projection matrix to an orthographic projection */
        Matrix4f projection = Matrix4f.perspective(45f, ratio, 0.1f, 10f);
        
        int uniProjection = program.getUniformLocation("projection");
        program.setUniform(uniProjection, projection);
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

    public void render(long window) {

    }

    //continous update
    public void update() {

    }

    public void input() {
        // Poll for window events. The key callback above will only be
        // invoked during this call.
        glfwPollEvents();
    }
}
