package com.adelvanchik.core;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

public class WindowManager {

    public static final float FOV = (float) Math.toRadians(60); // Поле зрения
    public static final float Z_NEAR = 0.01f; // Поле зрения
    public static final float Z_FAR = 1000f; // Поле зрения

    private final String title;

    private int width, height; // Ширина и высота

    private long window; // Длина окна

    private boolean resize, vSync; // изменение размера и синхронизация

    private final Matrix4f projectionMatrix;

    public WindowManager(String title, int width, int height, boolean vSync) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.vSync = vSync;
        projectionMatrix = new Matrix4f();
    }

    public void init() {
        GLFWErrorCallback.createPrint(System.err).set(); // Обратный вызов ошибок - необходимо при возникновении ошибок

        if (!GLFW.glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GL11.GL_FALSE); // изначально окно не видимо
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GL11.GL_TRUE); // Возможность изменения окна
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3); // Основная версия
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2); // Второстепенная версия
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_ANY_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE); // используем основной профиль
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE); // Открытая прямая совместимость

        boolean maximised = false;
        if (width == 0 || height == 0) {
            width = 100;
            height = 100;
            GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, GLFW.GLFW_TRUE);
            maximised = true;
        }

        window = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
        if (window == MemoryUtil.NULL) {
            throw new RuntimeException("Не удалось создать окно GLFW");
        }

        GLFW.glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            this.height = height;
            this.width = width;
            this.setResize(true);
        }); // Нужно для получения информации об обновленном размере ширины и высоты

        GLFW.glfwSetKeyCallback(window,(window,key,scancode,action,mods) -> {
            if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE) {
                GLFW.glfwSetWindowShouldClose(window, true); // Закрываем окно при нажатии на ESC
            }
        }); // Обратный вызов

        if (maximised) {
            GLFW.glfwMaximizeWindow(window); // Разворачиваем экран по максимуму
        } else {
            GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor()); // Расположение в центре экрана
            GLFW.glfwSetWindowPos(window,(vidMode.width()-width)/2, (vidMode.height()-height)/2);
        }

        GLFW.glfwMakeContextCurrent(window);

        if(isvSync()) {
            GLFW.glfwSwapInterval(1);
        }

        GLFW.glfwShowWindow(window); // Показываем окно пользователю

        GL.createCapabilities(); // Создание возможностей

        GL11.glClearColor(0.0f,0.0f,0.0f,0.0f); // Установка цвета экрана
        GL11.glEnable(GL11.GL_DEPTH_TEST); // Проведение глубинного теста
        GL11.glEnable(GL11.GL_STENCIL_TEST); // Проведение теста по трафарету
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BACK);
    }

    // Метод обновления
    public void update() {
        GLFW.glfwSwapBuffers(window);
        GLFW.glfwPollEvents();
    }

    // Метод очистки
    public void cleanup() {
        GLFW.glfwDestroyWindow(window);
    }

    // Метод цветопередачи
    public void setClearColor(float r, float g, float b, float a) {
        GL11.glClearColor(r,g,b,a);
    }

    // Проверка наличия нажатия клавиш
    public boolean isKeyPressed(int keyCode) {
        return GLFW.glfwGetKey(window, keyCode) == GLFW.GLFW_PRESS;
    }

    // Функция закрытия окна
    public boolean windowShouldClose() {
        return GLFW.glfwWindowShouldClose(window);
    }

    public String getTitle() {
        return title;
    }

    public void setTittle(String tittle) {
        GLFW.glfwSetWindowTitle(window,tittle);
    }

    public boolean isResize() {
        return resize;
    }

    public void setResize(boolean resize) {
        this.resize = resize;
    }

    public boolean isvSync() {
        return vSync;
    }

    public void setvSync(boolean vSync) {
        this.vSync = vSync;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public long getWindow() {
        return window;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4f updateProjectionMatrix() {
        float aspectRatio = (float) width/height;
        return projectionMatrix.setPerspective(FOV,aspectRatio,Z_NEAR,Z_FAR);
    }

    public Matrix4f updateProjectionMatrix(Matrix4f matrix, int width, int height) {
        float aspectRatio = (float) width/height;
        return matrix.setPerspective(FOV,aspectRatio,Z_NEAR,Z_FAR);
    }
}
