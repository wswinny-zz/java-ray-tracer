package scene;

import sceneobjects.SceneObject;
import light.Light;
import glm.vec._3.Vec3;

import java.util.ArrayList;

/**
 * Created by Swinny on 10/18/2016.
 */
public class Scene
{
    private Vec3 backgroundColor = new Vec3(1.0f, 1.0f, 1.0f);
    private Vec3 ambientLight = new Vec3(0.0f, 0.0f, 0.0f);

    private int imageWH = 0;
    private double cornerDist = 0;

    private ArrayList<SceneObject> sceneObjects;
    private ArrayList<Light> lights;

    public Scene(int imageWH, double cornerDist)
    {
        this.imageWH = imageWH;
        this.cornerDist = cornerDist;

        this.sceneObjects = new ArrayList<SceneObject>();
        this.lights = new ArrayList<Light>();
    }

    public Vec3 getBackgroundColor()
    {
        return backgroundColor;
    }

    public void setBackgroundColor(Vec3 backgroundColor)
    {
        this.backgroundColor = backgroundColor;
    }

    public Vec3 getAmbientLight()
    {
        return ambientLight;
    }

    public void setAmbientLight(Vec3 ambientLight)
    {
        this.ambientLight = ambientLight;
    }

    public int getImageWH()
    {
        return imageWH;
    }

    public void setImageWH(int imageWH)
    {
        this.imageWH = imageWH;
    }

    public double getCornerDist()
    {
        return cornerDist;
    }

    public void setCornerDist(double cornerDist)
    {
        this.cornerDist = cornerDist;
    }

    public ArrayList<SceneObject> getSceneObjects()
    {
        return sceneObjects;
    }

    public ArrayList<Light> getLights()
    {
        return lights;
    }
}
