/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package doom;

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
        } finally {
            // Terminate GLFW and release the GLFWerrorfun
            glfwTerminate();
            errorCallback.release();
        }
    }

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

        int WIDTH = 300;
        int HEIGHT = 300;

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
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the ContextCapabilities instance and makes the OpenGL
        // bindings available for use.
        GLContext.createFromCurrent();
        System.out.println("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION));
        // Set the clear color
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(-10, 10, -10, 10, -5, 5);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        int x = 0;
        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (glfwWindowShouldClose(window) == GL_FALSE) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
           
            glEnable(GL11.GL_CULL_FACE);
            glEnable(GL11.GL_DEPTH_TEST);
            glDepthFunc(GL_LESS);
            GL11.glLoadIdentity();

            
            GL11.glRotatef(x++, 1, 1, 1);
            GL11.glBegin(GL11.GL_QUADS);

            GL11.glColor3f(1, 1, 0);
            glVertex3f(1.0f, 1.0f, -1.0f);          // Top Right Of The Quad (Top)
            glVertex3f(-1.0f, 1.0f, -1.0f);          // Top Left Of The Quad (Top)
            glVertex3f(-1.0f, 1.0f, 1.0f);          // Bottom Left Of The Quad (Top)
            glVertex3f(1.0f, 1.0f, 1.0f);          // Bottom Right Of The Quad (Top)

           GL11.glColor3f(1, 1, 1);
            glVertex3f(1.0f, -1.0f, 1.0f);          // Top Right Of The Quad (Bottom)
            glVertex3f(-1.0f, -1.0f, 1.0f);          // Top Left Of The Quad (Bottom)
            glVertex3f(-1.0f, -1.0f, -1.0f);          // Bottom Left Of The Quad (Bottom)
            glVertex3f(1.0f, -1.0f, -1.0f);          // Bottom Right Of The Quad (Bottom)

            
            GL11.glColor3f(0, 1, 1);
            glVertex3f(1.0f, 1.0f, 1.0f);          // Top Right Of The Quad (Front)
            glVertex3f(-1.0f, 1.0f, 1.0f);          // Top Left Of The Quad (Front)
            glVertex3f(-1.0f, -1.0f, 1.0f);          // Bottom Left Of The Quad (Front)
            glVertex3f(1.0f, -1.0f, 1.0f);          // Bottom Right Of The Quad (Front)

           GL11.glColor3f(1, 0, 1);
            glVertex3f(1.0f, -1.0f, -1.0f);          // Bottom Left Of The Quad (Back)
            glVertex3f(-1.0f, -1.0f, -1.0f);          // Bottom Right Of The Quad (Back)
            glVertex3f(-1.0f, 1.0f, -1.0f);          // Top Right Of The Quad (Back)
            glVertex3f(1.0f, 1.0f, -1.0f);          // Top Left Of The Quad (Back)

            GL11.glColor3f(0, 1, 0);
            glVertex3f(-1.0f, 1.0f, 1.0f);          // Top Right Of The Quad (Left)
            glVertex3f(-1.0f, 1.0f, -1.0f);          // Top Left Of The Quad (Left)
            glVertex3f(-1.0f, -1.0f, -1.0f);          // Bottom Left Of The Quad (Left)
            glVertex3f(-1.0f, -1.0f, 1.0f);          // Bottom Right Of The Quad (Left)

            GL11.glColor3f(0, 0, 1);
            glVertex3f(1.0f, 1.0f, -1.0f);          // Top Right Of The Quad (Right)
            glVertex3f(1.0f, 1.0f, 1.0f);          // Top Left Of The Quad (Right)
            glVertex3f(1.0f, -1.0f, 1.0f);          // Bottom Left Of The Quad (Right)
            glVertex3f(1.0f, -1.0f, -1.0f);          // Bottom Right Of The Quad (Right)
            
            GL11.glEnd();

            //last thing to do
           glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
    }

    public static void main(String[] args) {
        new Doom().run();
    }

}
