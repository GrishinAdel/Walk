package com.adelvanchik;

import com.adelvanchik.core.*;
import com.adelvanchik.core.entity.Entity;
import com.adelvanchik.core.entity.Material;
import com.adelvanchik.core.entity.Model;
import com.adelvanchik.core.entity.Texture;
import com.adelvanchik.core.entity.terrain.Terrain;
import com.adelvanchik.core.lighting.DirectionalLight;
import com.adelvanchik.core.lighting.PointLight;
import com.adelvanchik.core.lighting.SpotLight;
import com.adelvanchik.core.rendering.RenderManager;
import com.adelvanchik.core.utils.Consts;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game implements ILogic {
    private final RenderManager render;
    private final ObjectLoader loader;
    private final WindowManager window;

    private List<Entity> entities;
    private List<Terrain> terrains;
    private Camera camera;
    Vector3f cameraInc;

    private float lightAngle, spotAngle = 0, spotInc = 1;
    private DirectionalLight directionalLight;
    private PointLight[] pointLights;
    private SpotLight[] spotLights;

    public Game() {
        render = new RenderManager();
        window = Launcher.getWindow();
        loader = new ObjectLoader();
        camera = new Camera();
        cameraInc = new Vector3f(0, 0, 0);
        lightAngle = -90;
    }

    @Override
    public void init() throws Exception {
        render.init();

        entities = new ArrayList<>();


        Model model = loader.loadOBJModel("/models/cactus.obj");
        model.setTexture(new Texture(loader.loadTexture("textures/cactus_texture.jpg")), 1f);

        terrains = new ArrayList<>();
        Terrain terrain = new Terrain(new Vector3f(-1600,-1,-1600), loader,
                new Material(new Texture(loader.loadTexture("textures/terrain.jpg")),0.1f));

        terrains.add(terrain);

        Random rnd = new Random();
        for (int i = 0; i  < 100; i++) {
            float x = rnd.nextFloat()*1000;
            float y = 0;
            float z = rnd.nextFloat()*-1000;
            entities.add(new Entity(model, new Vector3f(x,y,z),
                    new Vector3f(0,0,0),1));
        }
        for (int i = 0; i  < 100; i++) {
            float x = rnd.nextFloat()*-1000;
            float y = 0;
            float z = rnd.nextFloat()*-1000;
            entities.add(new Entity(model, new Vector3f(x,y,z),
                    new Vector3f(0,0,0),1));
        }
        for (int i = 0; i  < 100; i++) {
            float x = rnd.nextFloat()*-1000;
            float y = 0;
            float z = rnd.nextFloat()*1000;
            entities.add(new Entity(model, new Vector3f(x,y,z),
                    new Vector3f(0,0,0),1));
        }
        for (int i = 0; i  < 100; i++) {
            float x = rnd.nextFloat()*1000;
            float y = 0;
            float z = rnd.nextFloat()*1000;
            entities.add(new Entity(model, new Vector3f(x,y,z),
                    new Vector3f(0,0,0),1));
        }

        // point light
        float lightIntensity = 1.0f;
        Vector3f lightPosition = new Vector3f(-0.5f,-0.5f,-3.2f);
        Vector3f lightColour = new Vector3f(1,1,1);
        PointLight pointLight = new PointLight(lightColour, lightPosition, lightIntensity,0,0,1);

        // spot light
        Vector3f  coneDir = new Vector3f(0,0,1);
        float cutoff = (float) Math.cos(Math.toRadians(100));
        SpotLight spotLight = new SpotLight(new PointLight(lightColour, new Vector3f(0,0,1f),
                lightIntensity, 0,0,1), coneDir, cutoff);

        SpotLight spotLight1 = new SpotLight(new PointLight(lightColour, lightPosition, lightIntensity,
                0,0,1), coneDir, cutoff);
        spotLight1.getPointLight().setPosition(new Vector3f(0.5f,0.5f,-3.6f));

        // directional light
        lightPosition = new Vector3f(-1,-10,0);
        lightColour = new Vector3f(1,1,1);
        directionalLight = new DirectionalLight(lightColour,lightPosition,lightIntensity);

        pointLights = new PointLight[] {pointLight};
        spotLights = new SpotLight[] {spotLight, spotLight1};
    }

    @Override
    public void input() {
        cameraInc.set(0, 0, 0);
        if (window.isKeyPressed(GLFW.GLFW_KEY_W)) cameraInc.z = -1;
        if (window.isKeyPressed(GLFW.GLFW_KEY_E)) cameraInc.z = -50;
        if (window.isKeyPressed(GLFW.GLFW_KEY_S)) cameraInc.z = 1;
        if (window.isKeyPressed(GLFW.GLFW_KEY_A)) cameraInc.x = -1;
        if (window.isKeyPressed(GLFW.GLFW_KEY_D)) cameraInc.x = 1;
        if (window.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT)) cameraInc.y = -1;
        if (window.isKeyPressed(GLFW.GLFW_KEY_SPACE)) cameraInc.y = 1;

    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        camera.movePosition(
                cameraInc.x * Consts.CAMERA_STEP,
                cameraInc.y * Consts.CAMERA_STEP,
                cameraInc.z * Consts.CAMERA_STEP);

        if (mouseInput.isRightButtonPress()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation( rotVec.y * Consts.MOUSE_SENSITIVITY, rotVec.x * Consts.MOUSE_SENSITIVITY, 0);
        }


        spotAngle += spotInc * 0.5f;
        if (spotAngle > 4) {
            spotInc = -1;
        } else if ( spotAngle <= -4) {
            spotAngle = 1;
        }

        double spotAngleRad = Math.toRadians(spotAngle);
        Vector3f coneDir = spotLights[0].getPointLight().getPosition();
        coneDir.y = (float) Math.sin(spotAngleRad);

        lightAngle += 0.5f;
        if(lightAngle>90) {
            directionalLight.setIntensity(0);
            if (lightAngle>=360) {
                lightAngle = -90;
            }
        } else if (lightAngle<=-80 || lightAngle >=80) {
            float factor = 1 - (Math.abs(lightAngle)-80)/10.0f;
            directionalLight.setIntensity(factor);
            directionalLight.getColour().y = Math.max(factor,0.9f);
            directionalLight.getColour().z = Math.max(factor,0.5f);
        } else {
            directionalLight.setIntensity(1);
            directionalLight.getColour().x = 1;
            directionalLight.getColour().y = 1;
            directionalLight.getColour().z = 1;
        }
        double angRad = Math.toRadians(lightAngle);
        directionalLight.getDirection().x = (float) Math.sin(angRad);
        directionalLight.getDirection().y = (float) Math.cos(angRad);

        for (Entity entity: entities) {
            render.processEntity(entity);
        }

        for (Terrain terrain: terrains) {
            render.processTerrain(terrain);
        }
    }

    @Override
    public void render() {
        render.render(camera, directionalLight, pointLights, spotLights);
    }

    @Override
    public void cleanup() {
        render.cleanup();
        loader.cleanup();
    }
}
