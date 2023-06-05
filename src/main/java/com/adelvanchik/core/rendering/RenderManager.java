package com.adelvanchik.core.rendering;

import com.adelvanchik.Launcher;
import com.adelvanchik.core.Camera;
import com.adelvanchik.core.ShaderManager;
import com.adelvanchik.core.WindowManager;
import com.adelvanchik.core.entity.Entity;
import com.adelvanchik.core.entity.Texture;
import com.adelvanchik.core.entity.terrain.Terrain;
import com.adelvanchik.core.lighting.DirectionalLight;
import com.adelvanchik.core.lighting.PointLight;
import com.adelvanchik.core.lighting.SpotLight;
import com.adelvanchik.core.utils.Consts;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11C.glViewport;

public class RenderManager {

    private final WindowManager window;
    private EntityRender entityRenderer;
    private TerrainRender terrainRender;

    public RenderManager() {
        window = Launcher.getWindow();
    }

    public void init() throws Exception {
        entityRenderer = new EntityRender();
        terrainRender = new TerrainRender();
        entityRenderer.init();
        terrainRender.init();
    }

    public static void renderLights(PointLight[] pointLights, SpotLight[] spotLights,
                             DirectionalLight directionalLight, ShaderManager shader) {

        shader.setUniform("ambientLight", Consts.AMBIENT_LIGHT);
        shader.setUniform("specularPower", Consts.SPECULAR_POWER);

        int numLights = spotLights != null ? spotLights.length : 0;
        for (int i = 0; i < numLights; i++) {
            shader.setUniform("spotLights", spotLights[i], i);
        }

        numLights = pointLights != null ? pointLights.length : 0;
        for (int i = 0; i < numLights; i++) {
            shader.setUniform("pointLights", pointLights[i], i);
        }
        shader.setUniform("directionalLight", directionalLight);
    }


    public void render(Camera camera, DirectionalLight directionalLight,
                       PointLight[] pointLights, SpotLight[] spotLights) {
        clear();

        if(window.isResize()) {
            glViewport(0,0,window.getWidth(),window.getHeight());
            window.setResize(true);
        }

        entityRenderer.render(camera, pointLights, spotLights, directionalLight);
        terrainRender.render(camera, pointLights, spotLights, directionalLight);
    }

    public void processEntity(Entity entity) {
        List<Entity> entityList = entityRenderer.getEntities().get(entity.getModel());
        if (entityList != null) {
            entityList.add(entity);
        } else {
            List<Entity> newEntityList = new ArrayList<>();
            newEntityList.add(entity);
            entityRenderer.getEntities().put(entity.getModel(), newEntityList);
        }
    }

    public void processTerrain(Terrain terrain) {
        terrainRender.getTerrain().add(terrain);
    }
    public void clear() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public void cleanup() {
        entityRenderer.cleanup();
        terrainRender.cleanup();
    }
}
