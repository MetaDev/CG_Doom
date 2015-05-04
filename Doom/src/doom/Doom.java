/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package doom;

import game.Game;
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
 * @author Tim
 */
public class Doom {

    // We need to strongly reference callback instances.
    private GLFWErrorCallback errorCallback;
    private GLFWKeyCallback keyCallback;
    private GLFWCursorPosCallback posCallback;
    private GLFWMouseButtonCallback mouseCallback;
    private GLFWWindowSizeCallback windowSizeCallback;

    // The window handle
    private long window;
    public int windowWidth = 800;
    public int windowHeight = 600;

    private static void setNatives() {
        if (System.getProperty("org.lwjgl.librarypath") == null) {
            Path path = Paths.get("native");
            String librarypath = path.toAbsolutePath().toString();
            System.out.println(librarypath);
            System.setProperty("org.lwjgl.librarypath", librarypath);
        }
    }
    public Game game;

    public void run() {
        setNatives();
        initWindow();

        game = new Game();

        System.out.println("Hello LWJGL " + Sys.getVersion() + "!");
        game.gameLoop(window);

        // Release window and window callbacks
        glfwDestroyWindow(window);
        keyCallback.release();

        // Terminate GLFW and release the GLFWerrorfun
        glfwTerminate();
        errorCallback.release();
        windowSizeCallback.release();
        posCallback.release();
        mouseCallback.release();
        game.exit();

    }

    private void initWindow() {
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
        if (System.getProperty("os.name").startsWith("Windows")) {
            glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
        } else {
            glfwWindowHint(GLFW_VISIBLE, GL_TRUE);

        }
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        // Create the window
        window = glfwCreateWindow(windowWidth, windowHeight, "Hello World!", NULL, NULL);
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
                boolean on;
                if (action == GLFW_PRESS) {
                    on = true;
                } else if (action == GLFW_RELEASE) {
                    on = false;
                } else {
                    return;
                }
                switch (key) {
                    case GLFW_KEY_W:
                        //UP 
                        game.player.forward(on);
                        break;
                    case GLFW_KEY_S:
                        //Down
                        game.player.backward(on);
                        break;
                    case GLFW_KEY_D:
                        //RIGHT 
                        game.player.right(on);
                        break;
                    case GLFW_KEY_A:
                        //left
                        game.player.left(on);
                    case GLFW_KEY_SPACE:
                        //left
                        game.player.jump(on);
                        break;

                }
            }

        }
        );

        //mouse button callback
        glfwSetMouseButtonCallback(window, mouseCallback
                = new GLFWMouseButtonCallback() {
                    @Override
                    public void invoke(long window, int button, int action, int mods
                    ) {
                        if (action == GLFW_PRESS && button == GLFW_MOUSE_BUTTON_LEFT) {
                            game.player.shoot();
                        }
                    }
                }
        );
        //set resize callback
        glfwSetWindowSizeCallback(window, windowSizeCallback
                = new GLFWWindowSizeCallback() {

                    @Override
                    public void invoke(long window, int width, int height
                    ) {
                        windowWidth = width;
                        windowHeight = height;
                        if (game != null) {
                            game.setResolution(windowWidth, windowHeight);
                        }
                    }
                }
        );
        // Get the resolution of the primary monitor
        ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

        // Center our window
        glfwSetWindowPos(
                window,
                (GLFWvidmode.width(vidmode) - windowWidth) / 2,
                (GLFWvidmode.height(vidmode) - windowHeight) / 2
        );

        //mouse position callback
        glfwSetCursorPosCallback(window, posCallback = new GLFWCursorPosCallback() {
            private float mouseSensitivy = 0.1f;

            @Override
            public void invoke(long window, double xpos, double ypos) {
                game.player.setMousePos((float) xpos, (float) ypos, windowWidth, windowHeight);

            }
        }
        );
        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync, Don't it lowers performance ans is not necessary in our case
        // glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }

    public static void main(String[] args) {
        new Doom().run();
    }

}
