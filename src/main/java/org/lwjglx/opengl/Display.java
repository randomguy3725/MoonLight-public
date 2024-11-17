package org.lwjglx.opengl;

import lombok.Getter;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjglx.LWJGLException;
import org.lwjglx.Sys;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.*;
import org.lwjglx.input.KeyCodes;
import org.lwjglx.input.Keyboard;
import org.lwjglx.input.Mouse;

import java.awt.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Display {

    private static String windowTitle = "Minecraft 1.8.9";

    private static boolean displayCreated = false;
    private static boolean displayFocused = false;
    private static boolean displayVisible = true;
    private static boolean displayDirty = false;
    private static boolean displayResizable = false;
    private static boolean startFullscreen = false;

    private static DisplayMode mode = new DisplayMode(1280, 720);
    @Getter
    private static DisplayMode desktopDisplayMode = new DisplayMode(854, 480);

    private static final int latestEventKey = 0;

    private static int displayX = 0;
    private static int displayY = 0;

    private static boolean displayResized = false;
    private static int displayWidth = 0;
    private static int displayHeight = 0;
    private static int displayFramebufferWidth = 0;
    private static int displayFramebufferHeight = 0;

    private static boolean latestResized = false;
    private static int latestWidth = 0;
    private static int latestHeight = 0;
    private static boolean cancelNextChar = false;
    private static Keyboard.KeyEvent ingredientKeyEvent;
    private static ByteBuffer[] savedIcons;

    static {
        Sys.initialize(); // init using dummy sys method

        long monitor = glfwGetPrimaryMonitor();
        GLFWVidMode vidmode = glfwGetVideoMode(monitor);

        int monitorWidth = vidmode.width();
        int monitorHeight = vidmode.height();
        int monitorBitPerPixel = vidmode.redBits() + vidmode.greenBits() + vidmode.blueBits();
        int monitorRefreshRate = vidmode.refreshRate();

        desktopDisplayMode = new DisplayMode(monitorWidth, monitorHeight, monitorBitPerPixel, monitorRefreshRate);
    }

    public static void create() throws LWJGLException {
        create(new PixelFormat(), (new ContextAttribs(3, 3)).withProfileCore(true));
    }

    public static void create(PixelFormat pf) throws LWJGLException {
        create(pf, (new ContextAttribs(3, 3)).withProfileCore(true));
    }

    public static void create(PixelFormat pixelFormat, ContextAttribs attribs) throws LWJGLException {
        create(pixelFormat, attribs, NULL);
    }

    public static void create(PixelFormat pixelFormat, ContextAttribs attribs, long sharedWindow) {
        if (displayCreated) {
            return;
        }

        long monitor = glfwGetPrimaryMonitor();
        GLFWVidMode vidmode = glfwGetVideoMode(monitor);

        int monitorWidth = vidmode.width();
        int monitorHeight = vidmode.height();
        int monitorBitPerPixel = vidmode.redBits() + vidmode.greenBits() + vidmode.blueBits();
        int monitorRefreshRate = vidmode.refreshRate();

        desktopDisplayMode = new DisplayMode(monitorWidth, monitorHeight, monitorBitPerPixel, monitorRefreshRate);

        final int ctxMajor = (attribs != null) ? attribs.getMajorVersion() : 2;
        final int ctxMinor = (attribs != null) ? attribs.getMinorVersion() : 1;
        final boolean ctxForwardCompat = attribs != null && attribs.isForwardCompatible();

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, ctxMajor);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, ctxMinor);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, ctxForwardCompat ? GLFW_TRUE : GLFW_FALSE);

        if (attribs != null) {
            if (attribs.isProfileCore()) {
                glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
            } else if (attribs.isProfileCompatibility()) {
                glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_COMPAT_PROFILE);
            }
        }

        glfwWindowHint(GLFW_MAXIMIZED, GLFW_FALSE);
        glfwWindowHint(GLFW_FOCUSED, GLFW_TRUE);
        displayFocused = true;
        glfwWindowHint(GLFW_ICONIFIED, GLFW_FALSE);
        displayVisible = true;
        glfwWindowHint(GLFW_DECORATED, GLFW_TRUE);

        glfwWindowHint(GLFW_SRGB_CAPABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_DOUBLEBUFFER, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_NO_ERROR, GLFW_FALSE);
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);

        glfwWindowHint(GLFW_POSITION_X, (monitorWidth - mode.getWidth()) / 2);
        glfwWindowHint(GLFW_POSITION_Y, (monitorHeight - mode.getHeight()) / 2);

        Window.handle = glfwCreateWindow(mode.getWidth(), mode.getHeight(), windowTitle, NULL, sharedWindow);

        if (Window.handle == 0L) {
            throw new IllegalStateException("Failed to create Display window");
        }

        Window.keyCallback = new GLFWKeyCallback() {

            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                cancelNextChar = false;
                if (key > GLFW_KEY_SPACE && key <= GLFW_KEY_GRAVE_ACCENT) { // Handle keys have a char. Exclude space to
                    // avoid extra input when switching IME
                    if ((GLFW_MOD_CONTROL & mods) != 0 && (GLFW_MOD_ALT & mods) == 0) { // Handle ctrl + x/c/v.
                        Keyboard.addGlfwKeyEvent(window, key, scancode, action, mods, (char) (key & 0x1f));
                        cancelNextChar = true; // Cancel char event from ctrl key since its already handled here
                    } else if (action > 0) { // Delay press and repeat key event to actual char input. There is ALWAYS a
                        // char after them
                        ingredientKeyEvent = new Keyboard.KeyEvent(
                                KeyCodes.glfwToLwjgl(key),
                                '\0',
                                action > 1 ? Keyboard.KeyState.REPEAT : Keyboard.KeyState.PRESS,
                                Sys.getNanoTime());
                    } else { // Release event
                        if (ingredientKeyEvent != null && ingredientKeyEvent.key == KeyCodes.glfwToLwjgl(key)) {
                            ingredientKeyEvent.queueOutOfOrderRelease = true;
                        }
                        Keyboard.addGlfwKeyEvent(window, key, scancode, action, mods, '\0');
                    }
                } else { // Other key with no char event associated
                    char mappedChar = key == GLFW_KEY_ENTER ? 0x0D :
                            key == GLFW_KEY_ESCAPE ? 0x1B :
                                    key == GLFW_KEY_TAB ? 0x09 :
                                            key == GLFW_KEY_BACKSPACE ? 0x08 :
                                                    '\0';
                    Keyboard.addGlfwKeyEvent(window, key, scancode, action, mods, mappedChar);
                }
            }
        };

        Window.charCallback = new GLFWCharCallback() {
            @Override
            public void invoke(long window, int codepoint) {
                if (cancelNextChar) { // Char event being cancelled
                    cancelNextChar = false;
                } else if (ingredientKeyEvent != null) {
                    ingredientKeyEvent.aChar = (char) codepoint; // Send char with ASCII key event here
                    Keyboard.addRawKeyEvent(ingredientKeyEvent);
                    if (ingredientKeyEvent.queueOutOfOrderRelease) {
                        ingredientKeyEvent = ingredientKeyEvent.copy();
                        ingredientKeyEvent.state = Keyboard.KeyState.RELEASE;
                        Keyboard.addRawKeyEvent(ingredientKeyEvent);
                    }
                    ingredientKeyEvent = null;
                } else {
                    Keyboard.addCharEvent(0, (char) codepoint); // Non-ASCII chars
                }
            }
        };

        Window.cursorPosCallback = new GLFWCursorPosCallback() {

            @Override
            public void invoke(long window, double xpos, double ypos) {
                Mouse.addMoveEvent(xpos, ypos);
            }
        };

        Window.mouseButtonCallback = new GLFWMouseButtonCallback() {

            @Override
            public void invoke(long window, int button, int action, int mods) {
                Mouse.addButtonEvent(button, action == GLFW.GLFW_PRESS);
            }
        };

        Window.scrollCallback = new GLFWScrollCallback() {

            @Override
            public void invoke(long window, double xoffset, double yoffset) {
                Mouse.addWheelEvent(yoffset);
            }
        };

        Window.windowFocusCallback = new GLFWWindowFocusCallback() {

            @Override
            public void invoke(long window, boolean focused) {
                displayFocused = focused;
            }
        };


        Window.windowIconifyCallback = new GLFWWindowIconifyCallback() {

            @Override
            public void invoke(long window, boolean iconified) {
                displayVisible = !iconified;
            }
        };

        Window.windowSizeCallback = new GLFWWindowSizeCallback() {

            @Override
            public void invoke(long window, int width, int height) {
                latestResized = true;
                latestWidth = width;
                latestHeight = height;
            }
        };

        Window.windowPosCallback = new GLFWWindowPosCallback() {

            @Override
            public void invoke(long window, int xpos, int ypos) {
                displayX = xpos;
                displayY = ypos;
            }
        };

        Window.windowRefreshCallback = new GLFWWindowRefreshCallback() {

            @Override
            public void invoke(long window) {
                displayDirty = true;
            }
        };

        Window.framebufferSizeCallback = new GLFWFramebufferSizeCallback() {

            @Override
            public void invoke(long window, int width, int height) {
                displayFramebufferWidth = width;
                displayFramebufferHeight = height;
            }
        };

        Window.setCallbacks();

        displayWidth = mode.getWidth();
        displayHeight = mode.getHeight();

        IntBuffer fbw = BufferUtils.createIntBuffer(1);
        IntBuffer fbh = BufferUtils.createIntBuffer(1);
        GLFW.glfwGetFramebufferSize(Window.handle, fbw, fbh);
        displayFramebufferWidth = fbw.get(0);
        displayFramebufferHeight = fbh.get(0);

        displayX = (monitorWidth - mode.getWidth()) / 2;
        displayY = (monitorHeight - mode.getHeight()) / 2;

        glfwMakeContextCurrent(Window.handle);
        GL.createCapabilities();

        if (savedIcons != null) {
            setIcon(savedIcons);
            savedIcons = null;
        }

        glfwSwapInterval(1);

        displayCreated = true;

        if (isCreated() && GLFW.glfwRawMouseMotionSupported()) {
            GLFW.glfwSetInputMode(Window.handle, GLFW_RAW_MOUSE_MOTION, GLFW_TRUE);
        }

        if (startFullscreen) {
            setFullscreen(true);
        }

        int[] x = new int[1], y = new int[1];
        GLFW.glfwGetWindowSize(Window.handle, x, y);
        Window.windowSizeCallback.invoke(Window.handle, x[0], y[0]);
        GLFW.glfwGetFramebufferSize(Window.handle, x, y);
        Window.framebufferSizeCallback.invoke(Window.handle, x[0], y[0]);
    }

    public static boolean isCreated() {
        return displayCreated;
    }

    public static boolean isActive() {
        return displayFocused;
    }

    public static boolean isVisible() {
        return displayVisible;
    }

    public static void setLocation(int new_x, int new_y) {
        System.out.println("TODO: Implement Display.setLocation(int, int)");
    }

    public static void setVSyncEnabled(boolean sync) {
        glfwSwapInterval(sync ? 1 : 0);
    }

    public static long getWindow() {
        return Window.handle;
    }

    public static void update() {
        update(true);
    }

    public static void update(boolean processMessages) {
        swapBuffers();
        displayDirty = false;

        if (processMessages) processMessages();
    }

    public static void processMessages() {
        glfwPollEvents();
        Keyboard.poll();
        Mouse.poll();

        if (latestResized) {
            latestResized = false;
            displayResized = true;
            displayWidth = latestWidth;
            displayHeight = latestHeight;
        } else {
            displayResized = false;
        }
    }

    public static void swapBuffers() {
        glfwSwapBuffers(Window.handle);
    }

    public static void destroy() {
        Window.releaseCallbacks();
        glfwDestroyWindow(Window.handle);

        /*
         * try { glfwTerminate(); } catch (Throwable t) { t.printStackTrace(); }
         */
        displayCreated = false;
    }

    public static void setDisplayMode(DisplayMode dm) {
        mode = dm;
    }

    public static DisplayMode getDisplayMode() {
        return mode;
    }

    public static DisplayMode[] getAvailableDisplayModes() {
        IntBuffer count = BufferUtils.createIntBuffer(1);
        GLFWVidMode.Buffer modes = GLFW.glfwGetVideoModes(glfwGetPrimaryMonitor());

        DisplayMode[] displayModes = new DisplayMode[count.get(0)];

        for (int i = 0; i < count.get(0); i++) {
            modes.position(i * GLFWVidMode.SIZEOF);

            int w = modes.width();
            int h = modes.height();
            int b = modes.redBits() + modes.greenBits() + modes.blueBits();
            int r = modes.refreshRate();

            displayModes[i] = new DisplayMode(w, h, b, r);
        }

        return displayModes;
    }

    public static boolean wasResized() {
        return displayResized;
    }

    public static int getX() {
        return displayX;
    }

    public static int getY() {
        return displayY;
    }

    public static int getWidth() {
        return displayWidth;
    }

    public static int getHeight() {
        return displayHeight;
    }

    public static int getFramebufferWidth() {
        return displayFramebufferWidth;
    }

    public static int getFramebufferHeight() {
        return displayFramebufferHeight;
    }

    public static void setTitle(String title) {
        windowTitle = title;
        if (isCreated()) {
            glfwSetWindowTitle(getWindow(), title);
        }
    }

    public static boolean isCloseRequested() {
        return glfwWindowShouldClose(Window.handle);
    }

    public static boolean isDirty() {
        return displayDirty;
    }

    public static void setInitialBackground(float red, float green, float blue) {
        // no-op
    }

    public static int setIcon(ByteBuffer[] icons) {
        if (getWindow() == 0) {
            savedIcons = icons;
            return 0;
        }
        GLFWImage.Buffer glfwImages = GLFWImage.calloc(icons.length);
        ByteBuffer[] nativeBuffers = new ByteBuffer[icons.length];
        for (int icon = 0; icon < icons.length; icon++) {
            nativeBuffers[icon] = BufferUtils.createByteBuffer(icons[icon].capacity());
            nativeBuffers[icon].put(icons[icon]);
            nativeBuffers[icon].flip();
            int dimension = (int) Math.sqrt(nativeBuffers[icon].limit() / 4D);
            if (dimension * dimension * 4 != nativeBuffers[icon].limit()) {
                throw new IllegalStateException();
            }
            glfwImages.put(icon, GLFWImage.create().set(dimension, dimension, nativeBuffers[icon]));
        }
        GLFW.glfwSetWindowIcon(getWindow(), glfwImages);
        glfwImages.free();
        return 0;
    }

    public static void setResizable(boolean resizable) {
        displayResizable = resizable;
        // Ignore the request because why would you make the game window non-resizable
    }

    public static boolean isResizable() {
        return displayResizable;
    }

    public static void setDisplayModeAndFullscreen(DisplayMode mode) {
        // TODO
        System.out.println("TODO: Implement Display.setDisplayModeAndFullscreen(DisplayMode)");
    }

    private static final int[] savedX = new int[1];
    private static final int[] savedY = new int[1];
    private static final int[] savedW = new int[1];
    private static final int[] savedH = new int[1];

    public static void setFullscreen(boolean fullscreen) {
        final long window = getWindow();
        if (window == 0) {
            startFullscreen = fullscreen;
            return;
        }
        final boolean currentState = isFullscreen();
        if (currentState == fullscreen) {
            return;
        }
        if (fullscreen) {
            glfwGetWindowPos(window, savedX, savedY);
            glfwGetWindowSize(window, savedW, savedH);
            long monitorId = glfwGetPrimaryMonitor();
            final GLFWVidMode vidMode = glfwGetVideoMode(monitorId);
            glfwSetWindowMonitor(window, monitorId, 0, 0, vidMode.width(), vidMode.height(), vidMode.refreshRate());
        } else {
            glfwSetWindowMonitor(window, NULL, savedX[0], savedY[0], savedW[0], savedH[0], 0);
        }
    }

    public static boolean isFullscreen() {
        if (getWindow() != 0) {
            return glfwGetWindowMonitor(getWindow()) != NULL;
        }
        return false;
    }

    public static void setParent(Canvas parent) {
        // Do nothing as set parent not supported
    }

    public static void releaseContext() {
        glfwMakeContextCurrent(0);
    }

    public static boolean isCurrent() {
        return true;
    }

    public static void makeCurrent() {
        glfwMakeContextCurrent(Window.handle);
    }

    public static String getAdapter() {
        if (isCreated()) {
            return GL11.glGetString(GL11.GL_VENDOR);
        }
        return "Unknown";
    }

    public static String getVersion() {
        if (isCreated()) {
            return GL11.glGetString(GL11.GL_VERSION);
        }
        return "Unknown";
    }

    public static String getTitle() {
        return windowTitle;
    }

    public static Canvas getParent() {
        return null;
    }

    public static float getPixelScaleFactor() {
        if (!isCreated()) {
            return 1.0f;
        }
        float[] xScale = new float[1];
        float[] yScale = new float[1];
        glfwGetWindowContentScale(getWindow(), xScale, yScale);
        return Math.max(xScale[0], yScale[0]);
    }

    public static void setSwapInterval(int value) {
        glfwSwapInterval(value);
    }

    public static void setDisplayConfiguration(float gamma, float brightness, float contrast) {
        // ignore
    }

    /**
     * An accurate sync method that will attempt to run at a constant frame rate. It should be called once every frame.
     *
     * @param fps - the desired frame rate, in frames per second
     */
    public static void sync(int fps) {
        Sync.sync(fps);
    }
    private static class Window {

        static long handle;

        static GLFWKeyCallback keyCallback;
        static GLFWCharCallback charCallback;
        static GLFWCursorPosCallback cursorPosCallback;
        static GLFWMouseButtonCallback mouseButtonCallback;
        static GLFWScrollCallback scrollCallback;
        static GLFWWindowFocusCallback windowFocusCallback;
        static GLFWWindowIconifyCallback windowIconifyCallback;
        static GLFWWindowSizeCallback windowSizeCallback;
        static GLFWWindowPosCallback windowPosCallback;
        static GLFWWindowRefreshCallback windowRefreshCallback;
        static GLFWFramebufferSizeCallback framebufferSizeCallback;

        public static void setCallbacks() {
            GLFW.glfwSetKeyCallback(handle, keyCallback);
            GLFW.glfwSetCharCallback(handle, charCallback);
            GLFW.glfwSetCursorPosCallback(handle, cursorPosCallback);
            GLFW.glfwSetMouseButtonCallback(handle, mouseButtonCallback);
            GLFW.glfwSetScrollCallback(handle, scrollCallback);
            GLFW.glfwSetWindowFocusCallback(handle, windowFocusCallback);
            GLFW.glfwSetWindowIconifyCallback(handle, windowIconifyCallback);
            GLFW.glfwSetWindowSizeCallback(handle, windowSizeCallback);
            GLFW.glfwSetWindowPosCallback(handle, windowPosCallback);
            GLFW.glfwSetWindowRefreshCallback(handle, windowRefreshCallback);
            GLFW.glfwSetFramebufferSizeCallback(handle, framebufferSizeCallback);
        }

        public static void releaseCallbacks() {
            keyCallback.free();
            charCallback.free();
            cursorPosCallback.free();
            mouseButtonCallback.free();
            scrollCallback.free();
            windowFocusCallback.free();
            windowIconifyCallback.free();
            windowSizeCallback.free();
            windowPosCallback.free();
            windowRefreshCallback.free();
            framebufferSizeCallback.free();
        }
    }
}
