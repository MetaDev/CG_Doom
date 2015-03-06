/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import java.nio.DoubleBuffer;
import org.lwjgl.BufferUtils;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;

/**
 *
 * @author Harald
 */
public class Mouse {
    public static void getMousePosition(long window){
        DoubleBuffer x = BufferUtils.createDoubleBuffer(1);
                DoubleBuffer y = BufferUtils.createDoubleBuffer(1);

                glfwGetCursorPos(window, x, y);
                x.rewind();
                y.rewind();

                //x.get();
                //y.get();
    }
}
