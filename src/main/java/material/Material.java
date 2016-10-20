package material;

import utils.Constants;
import glm.vec._3.Vec3;

/**
 * Created by Swinny on 10/18/2016.
 */
public class Material
{
    private Vec3 diffuse;
    private Vec3 specular;
    private Vec3 refraction;

    private double shininess;
    private double refractiveIndex = Constants.AIR_REFRACTIVE_INDEX;

    public Material(Vec3 diffuse, Vec3 specular, Vec3 refraction, double shininess, double refractiveIndex)
    {
        this.diffuse = diffuse;
        this.specular = specular;
        this.refraction = refraction;

        this.shininess = shininess;
        this.refractiveIndex = refractiveIndex;
    }

    public Material(Vec3 diffuse, Vec3 specular, double shininess)
    {
        this(diffuse, specular, new Vec3(0.0, 0.0, 0.0), shininess, Constants.AIR_REFRACTIVE_INDEX);
    }

    public Material(Material material)
    {
        this(new Vec3(material.getDiffuse()),
                new Vec3(material.getSpecular()),
                new Vec3(material.getRefraction()),
                material.getShininess(),
                material.getRefractiveIndex());
    }

    public Vec3 getDiffuse()
    {
        return diffuse;
    }

    public void setDiffuse(Vec3 diffuse)
    {
        this.diffuse = diffuse;
    }

    public Vec3 getSpecular()
    {
        return specular;
    }

    public void setSpecular(Vec3 specular)
    {
        this.specular = specular;
    }

    public Vec3 getRefraction()
    {
        return refraction;
    }

    public void setRefraction(Vec3 refraction)
    {
        this.refraction = refraction;
    }

    public double getShininess()
    {
        return shininess;
    }

    public void setShininess(double shininess)
    {
        this.shininess = shininess;
    }

    public double getRefractiveIndex()
    {
        return refractiveIndex;
    }

    public void setRefractiveIndex(double refractiveIndex)
    {
        this.refractiveIndex = refractiveIndex;
    }
}
