package com.adelvanchik.core.lighting;

import org.joml.Vector3f;

public class SpotLight {

    private PointLight pointLight;
    private Vector3f coneDirections;
    private float cutoff;

    public SpotLight(PointLight pointLight, Vector3f coneDirections, float cutoff) {
        this.pointLight = pointLight;
        this.coneDirections = coneDirections;
        this.cutoff = cutoff;
    }

    public SpotLight(SpotLight spotLight) {
        this.pointLight = spotLight.getPointLight();
        this.coneDirections = spotLight.getConeDirections();
        setCutoff(spotLight.getCutoff());
    }

    public PointLight getPointLight() {
        return pointLight;
    }

    public void setPointLight(PointLight pointLight) {
        this.pointLight = pointLight;
    }

    public Vector3f getConeDirections() {
        return coneDirections;
    }

    public void setConeDirections(Vector3f coneDirections) {
        this.coneDirections = coneDirections;
    }

    public float getCutoff() {
        return cutoff;
    }

    public void setCutoff(float cutoff) {
        this.cutoff = cutoff;
    }
}
