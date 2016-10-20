package sceneobjects;

import material.Material;
import utils.Intersection;
import glm.mat._4.Mat4;

/**
 * Created by Swinny on 10/18/2016.
 */
public abstract class SceneObject
{
    protected Material material;
    protected Mat4 T;
    protected Mat4 IT;

    public abstract boolean intersect(Intersection intersection);

    public void setMaterial(Material material) { this.material = material; }

    public Material getMaterial()
    {
        return this.material;
    }

    public Mat4 getT()
    {
        return this.T;
    }

    public Mat4 getIT()
    {
        return this.IT;
    }
}
