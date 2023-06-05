package com.adelvanchik.core;

import com.adelvanchik.Launcher;
import com.adelvanchik.core.utils.Consts;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

public class EngineManager {

    public static final long NANOSECOND = 1_000_000_000L;
    public static final float FRAMERATE = 1000; // Частота кадров

    public static int fps; // Показатель кадров в секунду
    private static float frametime = 1.0f / FRAMERATE; // Время кадра

    private boolean isRunning;

    private WindowManager window;
    private MouseInput mouseInput;
    private GLFWErrorCallback errorCallback;
    private ILogic logic;

    private void init() throws Exception {
        GLFW.glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
        window = Launcher.getWindow();
        logic = Launcher.getGame();
        mouseInput = new MouseInput();
        window.init();
        logic.init();
        mouseInput.init();
    }

    public void start() throws Exception {
        init();
        if (isRunning) return;
        run();
    }

    public void run() {
        this.isRunning = true;
        int frames = 0;
        long frameCounter = 0;
        long lastTime = System.nanoTime();
        double unprocessedTime = 0;

        while(isRunning) {
            boolean render = false;
            long startTime = System.nanoTime();
            long passedTime = startTime-lastTime;
            lastTime = startTime;

            unprocessedTime += passedTime / (double) NANOSECOND;
            frameCounter += passedTime;

            input();

            while(unprocessedTime > frametime) {
                render = true;
                unprocessedTime -=frametime;

                if (window.windowShouldClose()) stop();

                if (frameCounter >= NANOSECOND) {
                    setFps(frames);
                    window.setTittle(Consts.TITLE + " " + getFps());
                    frames = 0;
                    frameCounter = 0;
                }
            }

            if (render) {
                update(frametime);
                render();
                frames++;
            }
        }
        cleanup();
    }

    private void stop() {
        if(!isRunning) return;
        isRunning = false;
    }

    private void input() {
        mouseInput.input();
        logic.input();
    }

    private void render() {
        logic.render();
        window.update();
    }

    private void update(float interval) {
        logic.update(interval, mouseInput);
    }

    private void cleanup() {
        window.cleanup();
        logic.cleanup();
        errorCallback.free();
        GLFW.glfwTerminate();
    }

    public static int getFps() {
        return fps;
    }

    public static void setFps(int fps) {
        EngineManager.fps = fps;
    }
}
