package org.lwjglx.opengl;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

public class GLContext {
    public GLContext() {
    }

    public static GLCapabilities getCapabilities() {
        return GL.getCapabilities();
    }
}
