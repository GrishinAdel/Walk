package com.adelvanchik;

import com.adelvanchik.core.EngineManager;
import com.adelvanchik.core.WindowManager;
import com.adelvanchik.core.utils.Consts;

public class Launcher {
    private static WindowManager window;
    private static Game game;

    public static void main(String[] args) {
        window = new WindowManager(Consts.TITLE,1600,900,false);
        game = new Game();
        EngineManager engine = new EngineManager();

        try {
            engine.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static WindowManager getWindow() {
        return window;
    }

    public static Game getGame() {
        return game;
    }
}