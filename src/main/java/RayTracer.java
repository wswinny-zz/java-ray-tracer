import light.Light;
import material.Material;
import scene.Scene;
import sceneobjects.SceneObject;
import sceneobjects.Sphere;
import utils.Constants;
import utils.Intersection;
import utils.Ray;
import glm.Glm;
import glm.mat._4.Mat4;
import glm.vec._3.Vec3;
import glm.vec._4.Vec4;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Swinny on 10/18/2016.
 */
public class RayTracer
{
    private Scene scene;
    private BufferedImage image;

    public RayTracer(Scene scene)
    {
        this.scene = scene;
        this.image = new BufferedImage(this.scene.getImageWH(),
                this.scene.getImageWH(),
                BufferedImage.TYPE_INT_RGB);

        this.castRays();
        this.outputImage();
    }

    public static void main(String [] beans)
    {
        Scene scene = new Scene(512, 0.5);
        scene.setBackgroundColor(new Vec3(1.0, 1.0, 1.0));
        scene.setAmbientLight(new Vec3(0.2, 0.2, 0.2));

        Mat4 transform = new Mat4(1.0f);
        transform.translate(new Vec3(0.0, 0.0, -2.0));

        Material mat = new Material(
                new Vec3(1.0, 0.0, 0.0),
                new Vec3(0.2, 0.2, 0.2),
                new Vec3(0.5, 0.5, 0.5),
                30.0,
                Constants.AIR_REFRACTIVE_INDEX);

        Sphere sphere = new Sphere(transform);
        sphere.setMaterial(mat);
        scene.getSceneObjects().add(sphere);

        Light light = new Light(new Vec3(1.0f, 1.0f, 200.0f),
                new Vec3(0.0f, 255.0f, 0.0f));
        scene.getLights().add(light);

        new RayTracer(scene);
    }

    private void outputImage()
    {
        try
        {
            File outputfile = new File("rayTracedImage.png");
            ImageIO.write(this.image, "png", outputfile);
        }
        catch (IOException e)
        {
            System.out.println("Could not output image!");
        }
    }

    private void castRays()
    {
        double pixelWidth = (2 * this.scene.getCornerDist()) / this.scene.getImageWH();
        double halfPixWidth = pixelWidth / 2.0;

        for(int y = 0; y < this.scene.getImageWH(); ++y)
        {
            for(int x = 0; x < this.scene.getImageWH(); ++x)
            {
                double pixX = (-this.scene.getCornerDist() + (pixelWidth / 2.0)) + (x * pixelWidth);
                double pixY = (+this.scene.getCornerDist() - (pixelWidth / 2.0)) - (y * pixelWidth);

                Vec4 direction = new Vec4(pixX, pixY, 0.0, Constants.POINT);
                direction = Glm.sub_(direction, Constants.CAMERA);
                direction = direction.normalize();

                Ray ray = new Ray(Constants.CAMERA, direction);

                Vec3 color = this.trace(ray, 0, Constants.AIR_REFRACTIVE_INDEX);

                Graphics g = this.image.getGraphics();
                g.setColor(new Color(color.x, color.y, color.z));
                g.fillRect(x, y, 1, 1);
            }
        }
    }

    private Vec3 trace(Ray ray, int depth, double currentRefractiveIndex)
    {
        if(depth > Constants.MAX_RECURSIVE_DEPTH)
            return new Vec3(0.0, 0.0, 0.0);

        double tMin = Constants.INFINITY;
        SceneObject currentObject = null;

        for(SceneObject object : this.scene.getSceneObjects())
        {
            Intersection intersection = new Intersection(ray);

            if(object.intersect(intersection))
            {
                if(intersection.getIntersectionSolution() < tMin)
                {
                    tMin = intersection.getIntersectionSolution();
                    currentObject = object;
                }
            }
        }

        if(tMin == Constants.INFINITY || currentObject == null)
            return this.scene.getBackgroundColor();

        Vec4 uPrime = new Vec4(currentObject.getIT().mul_(ray.getOrigin()));
        Vec4 vPrime = new Vec4(currentObject.getIT().mul_(ray.getDirection()));

        Vec3 pWorld = new Vec3(ray.getOrigin()).add_(new Vec3(ray.getDirection()).mul_((float) tMin));
        Vec3 p = new Vec3(uPrime).add_(new Vec3(vPrime).mul_((float) tMin));
        Vec3 N = new Vec3(Glm.transpose_(currentObject.getIT()).mul_(new Vec4(p, Constants.VEC))).normalize();

        //return Glm.abs_(N);

        Vec3 color = this.scene.getAmbientLight().mul_(currentObject.getMaterial().getDiffuse());
        Vec3 diffuse = new Vec3(0.0f);
        Vec3 specular = new Vec3(0.0f);
        Vec3 reflection = new Vec3(0.0f);
        Vec3 refraction = new Vec3(0.0f);

        Vec3 V = new Vec3(ray.getDirection()).mul_(-1.0f).normalize();

        for(Light light : this.scene.getLights())
        {
            Vec3 L = (light.getPosition().sub_(pWorld)).normalize();
            Vec3 H = V.add_(L).normalize();

            float diffuseCoefficent = (float) Math.max(N.dot(L), 0.0);
            diffuse = diffuse.add_(light.getColor().mul_(diffuseCoefficent).mul_(currentObject.getMaterial().getDiffuse()));

            float specularCoefficent = (float) Math.pow(Math.max(H.dot(N), 0.0), currentObject.getMaterial().getShininess() * Constants.BLINN_EXPONENT_MULTIPLIER);
            specular = specular.add_(new Vec3(light.getColor().mul_(specularCoefficent).mul_(currentObject.getMaterial().getSpecular())));
        }

        Vec3 returnColor = color.add_(diffuse).add_(specular).add_(reflection).add_(refraction);

        return new Vec3(
                Math.min(returnColor.x, 1.0f),
                Math.min(returnColor.y, 1.0f),
                Math.min(returnColor.z, 1.0f));
    }
}
