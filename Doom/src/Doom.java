/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import org.lwjgl.Sys;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;
import render.Camera;

/**
 *
 * @author Harald
 */
public class Doom {

    /**
     * @param args the command line arguments
     */
    // We need to strongly reference callback instances.
    private GLFWErrorCallback errorCallback;
    private GLFWKeyCallback keyCallback;
    private GLFWCursorPosCallback posCallback;
    private GLFWMouseButtonCallback mouseCallback;
    // The window handle
    private long window;

    private static void setNatives() {
        if (System.getProperty("org.lwjgl.librarypath") == null) {
            Path path = Paths.get("native");
            String librarypath = path.toAbsolutePath().toString();
            System.out.println(librarypath);
            System.setProperty("org.lwjgl.librarypath", librarypath);
        }
    }

    public void run() {
        setNatives();

        System.out.println("Hello LWJGL " + Sys.getVersion() + "!");
        try {
            init();
            loop();

            // Release window and window callbacks
            glfwDestroyWindow(window);
            keyCallback.release();
            mouseCallback.release();
            posCallback.release();
        } finally {
            // Terminate GLFW and release the GLFWerrorfun
            glfwTerminate();
            errorCallback.release();
        }
    }
    int WIDTH;
    int HEIGHT;
    private static double dX;
    private static double dY;

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        glfwSetErrorCallback(errorCallback = errorCallbackPrint(System.err));

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (glfwInit() != GL11.GL_TRUE) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure our window
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable

        WIDTH = 600;
        HEIGHT = 600;

        // Create the window
        window = glfwCreateWindow(WIDTH, HEIGHT, "Hello World!", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                    glfwSetWindowShouldClose(window, GL_TRUE); // We will detect this in our rendering loop
                }
                if (action == GLFW_REPEAT || action == GLFW_PRESS) {
                    if (key == GLFW_KEY_W) {

                    } else if (key == GLFW_KEY_A) {

                    } else if (key == GLFW_KEY_S) {

                    } else if (key == GLFW_KEY_D) {

                    }
                }

            }
        });
        //mouse position callback
        glfwSetCursorPosCallback(window, posCallback = new GLFWCursorPosCallback() {
            private double prevX;
            private double prevY;

            @Override
            public void invoke(long window, double xpos, double ypos) {
                Doom.dX = xpos-prevX ;
                Doom.dY =ypos- prevY;
                prevX = xpos;
                prevY = ypos;
            }
        });
        //mouse button callback
        glfwSetMouseButtonCallback(window, mouseCallback = new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {

            }
        });
        // Get the resolution of the primary monitor
        ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        // Center our window
        glfwSetWindowPos(
                window,
                (GLFWvidmode.width(vidmode) - WIDTH) / 2,
                (GLFWvidmode.height(vidmode) - HEIGHT) / 2
        );

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }

    private void loop() {
//        //camera code
//        Camera camera = new Camera(0, 0, 0);
//
//        float dx = 0.0f;
//        float dy = 0.0f;
//        float dt = 0.0f; //length of frame
//        float lastTime = 0.0f; // when the last frame was
//        float time = 0.0f;
//
//        float mouseSensitivity = 0.1f;
//        float movementSpeed = 10.0f; //move 10 units per second
//        
//        //mouse
//        // This line is critical for LWJGL's interoperation with GLFW's
//        // OpenGL context, or any context that is managed externally.
//        // LWJGL detects the context that is current in the current thread,
//        // creates the ContextCapabilities instance and makes the OpenGL
//        // bindings available for use.
//        GLContext.createFromCurrent();
//        System.out.println("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION));
//        // Set the clear color
//        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
//
//        GL11.glMatrixMode(GL11.GL_PROJECTION);
//        GL11.glLoadIdentity();
//
//        GL11.glOrtho(-10, 10, -10, 10, -5, 5);
//        GL11.glMatrixMode(GL11.GL_MODELVIEW);
//        // Run the rendering loop until the user has attempted to close
//        // the window or has pressed the ESCAPE key.
//        while (glfwWindowShouldClose(window) == GL_FALSE) {
//            //you would draw your scene here.
//            //mouse
//            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
//
//            glEnable(GL11.GL_CULL_FACE);
//            glEnable(GL11.GL_DEPTH_TEST);
//            glDepthFunc(GL_LESS);
//            //camera code
//            //time = Sys.getTime();
//            dt = (time - lastTime) / 1000.0f;
//            lastTime = time;
//
//            //distance in mouse movement from the last getDX() call.
//            dx = (float) Doom.dX;
//            //distance in mouse movement from the last getDY() call.
//            dy = (float) Doom.dY;
//
//            //reset
//            Doom.dX = 0;
//            Doom.dY = 0;
//            //controll camera yaw from x movement fromt the mouse
//            camera.yaw(dx * mouseSensitivity);
//            //controll camera pitch from y movement fromt the mouse
//            camera.pitch(dy * mouseSensitivity);
//            //TODO
//            //camera.walkForward(movementSpeed*dt);
//
//            //set the modelview matrix back to the identity
//            GL11.glLoadIdentity();
//            //look through the camera before you draw anything
//            camera.lookThrough();
//
//
//            //GL11.glRotatef((float)deltaX, (float)deltaY, 1, 1);
//            GL11.glBegin(GL11.GL_QUADS);
//
//            GL11.glColor3f(1, 1, 0);
//            glVertex3f(1.0f, 1.0f, -1.0f);          // Top Right Of The Quad (Top)
//            glVertex3f(-1.0f, 1.0f, -1.0f);          // Top Left Of The Quad (Top)
//            glVertex3f(-1.0f, 1.0f, 1.0f);          // Bottom Left Of The Quad (Top)
//            glVertex3f(1.0f, 1.0f, 1.0f);          // Bottom Right Of The Quad (Top)
//
//            GL11.glColor3f(1, 1, 1);
//            glVertex3f(1.0f, -1.0f, 1.0f);          // Top Right Of The Quad (Bottom)
//            glVertex3f(-1.0f, -1.0f, 1.0f);          // Top Left Of The Quad (Bottom)
//            glVertex3f(-1.0f, -1.0f, -1.0f);          // Bottom Left Of The Quad (Bottom)
//            glVertex3f(1.0f, -1.0f, -1.0f);          // Bottom Right Of The Quad (Bottom)
//
//            GL11.glColor3f(0, 1, 1);
//            glVertex3f(1.0f, 1.0f, 1.0f);          // Top Right Of The Quad (Front)
//            glVertex3f(-1.0f, 1.0f, 1.0f);          // Top Left Of The Quad (Front)
//            glVertex3f(-1.0f, -1.0f, 1.0f);          // Bottom Left Of The Quad (Front)
//            glVertex3f(1.0f, -1.0f, 1.0f);          // Bottom Right Of The Quad (Front)
//
//            GL11.glColor3f(1, 0, 1);
//            glVertex3f(1.0f, -1.0f, -1.0f);          // Bottom Left Of The Quad (Back)
//            glVertex3f(-1.0f, -1.0f, -1.0f);          // Bottom Right Of The Quad (Back)
//            glVertex3f(-1.0f, 1.0f, -1.0f);          // Top Right Of The Quad (Back)
//            glVertex3f(1.0f, 1.0f, -1.0f);          // Top Left Of The Quad (Back)
//
//            GL11.glColor3f(0, 1, 0);
//            glVertex3f(-1.0f, 1.0f, 1.0f);          // Top Right Of The Quad (Left)
//            glVertex3f(-1.0f, 1.0f, -1.0f);          // Top Left Of The Quad (Left)
//            glVertex3f(-1.0f, -1.0f, -1.0f);          // Bottom Left Of The Quad (Left)
//            glVertex3f(-1.0f, -1.0f, 1.0f);          // Bottom Right Of The Quad (Left)
//
//            GL11.glColor3f(0, 0, 1);
//            glVertex3f(1.0f, 1.0f, -1.0f);          // Top Right Of The Quad (Right)
//            glVertex3f(1.0f, 1.0f, 1.0f);          // Top Left Of The Quad (Right)
//            glVertex3f(1.0f, -1.0f, 1.0f);          // Bottom Left Of The Quad (Right)
//            glVertex3f(1.0f, -1.0f, -1.0f);          // Bottom Right Of The Quad (Right)
//
//            GL11.glEnd();
//
//            //last thing to do
//            glfwSwapBuffers(window); // swap the color buffers
//
//            // Poll for window events. The key callback above will only be
//            // invoked during this call.
//            glfwPollEvents();
//        }
    }

//    public static void main(String[] args) {
//        new Doom().run();
//    }

}
