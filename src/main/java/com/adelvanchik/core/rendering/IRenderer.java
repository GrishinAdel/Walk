package com.adelvanchik.core.rendering;

import com.adelvanchik.core.Camera;
import com.adelvanchik.core.entity.Model;
import com.adelvanchik.core.lighting.DirectionalLight;
import com.adelvanchik.core.lighting.PointLight;
import com.adelvanchik.core.lighting.SpotLight;

public interface IRenderer<T> {
    void init() throws Exception;

    void render(Camera camera, PointLight[] pointLights, SpotLight[] spotLights,
                       DirectionalLight directionalLight);

    abstract void bind(Model model);

    void unbind();

    void prepare(T t, Camera camera);

    public void cleanup();
}
