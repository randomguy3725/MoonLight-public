/*
 * Copyright (c) 2002-2008 LWJGL Project
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'LWJGL' nor the names of
 *   its contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.lwjglx.util.glu;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.ARBImaging.GL_TABLE_TOO_LARGE;
import static org.lwjgl.opengl.GL12.GL_BGR;
import static org.lwjgl.opengl.GL12.GL_BGRA;
import static org.lwjgl.opengl.GL30.GL_INVALID_FRAMEBUFFER_OPERATION;

/**
 * Util.java
 * <p/>
 * <p/>
 * Created 7-jan-2004
 *
 * @author Erik Duijs
 */
public class Util {

    /**
     * temp IntBuffer of one for getting an int from some GL functions
     */
    private static final IntBuffer scratch = BufferUtils.createIntBuffer(16);

    /**
     * Return ceiling of integer division
     *
     * @param a
     * @param b
     * @return int
     */
    protected static int ceil(int a, int b) {
        return (a % b == 0 ? a / b : a / b + 1);
    }

    /**
     * Normalize vector
     *
     * @param v
     * @return float[]
     */
    protected static float[] normalize(float[] v) {
        float r;

        r = (float) Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
        if (r == 0.0)
            return v;

        r = 1.0f / r;

        v[0] *= r;
        v[1] *= r;
        v[2] *= r;

        return v;
    }

    /**
     * Calculate cross-product
     *
     * @param v1
     * @param v2
     * @param result
     */
    protected static void cross(float[] v1, float[] v2, float[] result) {
        result[0] = v1[1] * v2[2] - v1[2] * v2[1];
        result[1] = v1[2] * v2[0] - v1[0] * v2[2];
        result[2] = v1[0] * v2[1] - v1[1] * v2[0];
    }

    /**
     * Method compPerPix.
     *
     * @param format
     * @return int
     */
    protected static int compPerPix(int format) {
        /* Determine number of components per pixel */
        return switch (format) {
            case GL11.GL_COLOR_INDEX, GL11.GL_STENCIL_INDEX, GL11.GL_DEPTH_COMPONENT, GL11.GL_RED, GL11.GL_GREEN,
                    GL11.GL_BLUE, GL11.GL_ALPHA, GL11.GL_LUMINANCE -> 1;
            case GL11.GL_LUMINANCE_ALPHA -> 2;
            case GL11.GL_RGB, GL_BGR -> 3;
            case GL11.GL_RGBA, GL_BGRA -> 4;
            default -> -1;
        };
    }

    /**
     * Method nearestPower.
     * <p/>
     * Compute the nearest power of 2 number.  This algorithm is a little strange, but it works quite well.
     *
     * @param value
     * @return int
     */
    protected static int nearestPower(int value) {
        int i;

        i = 1;

        /* Error! */
        if (value == 0)
            return -1;

        for (; ; ) {
            if (value == 1) {
                return i;
            } else if (value == 3) {
                return i << 2;
            }
            value >>= 1;
            i <<= 1;
        }
    }

    /**
     * Method bytesPerPixel.
     *
     * @param format
     * @param type
     * @return int
     */
    protected static int bytesPerPixel(int format, int type) {
        int n, m;

        n = switch (format) {
            case GL11.GL_COLOR_INDEX, GL11.GL_STENCIL_INDEX, GL11.GL_DEPTH_COMPONENT, GL11.GL_RED, GL11.GL_GREEN,
                    GL11.GL_BLUE, GL11.GL_ALPHA, GL11.GL_LUMINANCE -> 1;
            case GL11.GL_LUMINANCE_ALPHA -> 2;
            case GL11.GL_RGB, GL_BGR -> 3;
            case GL11.GL_RGBA, GL_BGRA -> 4;
            default -> 0;
        };

        m = switch (type) {
            case GL11.GL_UNSIGNED_BYTE -> 1;
            case GL11.GL_BYTE -> 1;
            case GL11.GL_BITMAP -> 1;
            case GL11.GL_UNSIGNED_SHORT -> 2;
            case GL11.GL_SHORT -> 2;
            case GL11.GL_UNSIGNED_INT -> 4;
            case GL11.GL_INT -> 4;
            case GL11.GL_FLOAT -> 4;
            default -> 0;
        };

        return n * m;
    }

    public static String translateGLErrorString(int error_code) {
        return switch (error_code) {
            case GL11.GL_NO_ERROR -> "No error";
            case GL11.GL_INVALID_ENUM -> "Invalid enum";
            case GL11.GL_INVALID_VALUE -> "Invalid value";
            case GL11.GL_INVALID_OPERATION -> "Invalid operation";
            case GL11.GL_STACK_OVERFLOW -> "Stack overflow";
            case GL11.GL_STACK_UNDERFLOW -> "Stack underflow";
            case GL11.GL_OUT_OF_MEMORY -> "Out of memory";
            case GL_TABLE_TOO_LARGE -> "Table too large";
            case GL_INVALID_FRAMEBUFFER_OPERATION -> "Invalid framebuffer operation";
            default -> null;
        };
    }
}