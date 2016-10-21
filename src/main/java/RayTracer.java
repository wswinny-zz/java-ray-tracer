import glm.Glm;
import glm.vec._3.Vec3;
import glm.vec._4.Vec4;
import light.Light;
import scene.Scene;
import scene.SceneParser;
import sceneobjects.SceneObject;
import utils.Constants;
import utils.Intersection;
import utils.Ray;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RayTracer
{
    private Scene scene;
    private BufferedImage image;
    private ExecutorService executorService;
    private ArrayList<Future<?>> futures;

    public RayTracer(Scene scene)
    {
        this.scene = scene;
        this.image = new BufferedImage(this.scene.getImageWH(),
                this.scene.getImageWH(),
                BufferedImage.TYPE_INT_RGB);

        new gui.Window(this.image);

        this.futures = new ArrayList<Future<?>>();
        this.executorService = Executors.newFixedThreadPool(10);

        this.castRays();

        System.out.println("Casted Rays");
        System.out.println("Waiting for rays to finish tracing");

        this.waitForFutures();
        this.outputImage();

        System.out.println("Done!");
    }

    public static void main(String [] beans)
    {
        Scene scene = new SceneParser(new File("export.obj")).parseObjFile();

        System.out.println("Parsed Scene");

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

    private void waitForFutures()
    {
        for(Future<?> future : this.futures)
        {
            try
            {
                future.get();
            }
            catch (InterruptedException | ExecutionException e)
            {
                e.printStackTrace();
            }
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
                final int finalX = x;
                final int finalY = y;

                Future<?> future = this.executorService.submit(() ->
                {
                    double pixX = (-this.scene.getCornerDist() + (pixelWidth / 2.0)) + (finalX * pixelWidth);
                    double pixY = (+this.scene.getCornerDist() - (pixelWidth / 2.0)) - (finalY * pixelWidth);

                    Vec4 direction = new Vec4(pixX, pixY, 0.0, Constants.POINT);
                    direction = Glm.sub_(direction, Constants.CAMERA);
                    direction = direction.normalize();

                    Ray ray = new Ray(Constants.CAMERA, direction);

                    Vec3 color = this.trace(ray, 0, Constants.AIR_REFRACTIVE_INDEX);

                    Graphics g = this.image.getGraphics();
                    g.setColor(new Color(color.x, color.y, color.z));
                    g.fillRect(finalX, finalY, 1, 1);
                });

                this.futures.add(future);
            }
        }
    }

    private Vec3 trace(Ray ray, int depth, double currentRefractiveIndex)
    {
        if(depth > Constants.MAX_RECURSIVE_DEPTH)
            return new Vec3(0.0f);

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

        if(tMin == Constants.INFINITY)
            return this.scene.getBackgroundColor();

        Vec4 uPrime = new Vec4(currentObject.getIT().mul_(ray.getOrigin()));
        Vec4 vPrime = new Vec4(currentObject.getIT().mul_(ray.getDirection()));

        Vec3 pWorld = new Vec3(ray.getOrigin()).add_(new Vec3(ray.getDirection()).mul_((float) tMin));
        Vec3 p = new Vec3(uPrime).add_(new Vec3(vPrime).mul_((float) tMin));
        Vec3 N = currentObject.getNormal(p);
        Vec3 V = new Vec3(ray.getDirection()).mul_(-1.0f).normalize();

        //return N.abs();

        Vec3 color = this.scene.getAmbientLight().mul_(currentObject.getMaterial().getDiffuse());
        Vec3 phong = this.calculatePhongLightingColor(currentObject, pWorld, V, N);
        Vec3 reflection = this.calculateReflectiveColor(currentObject, pWorld, N, V, depth, currentRefractiveIndex);
        Vec3 refraction = this.calculateRefractiveColor(currentObject, ray, N, pWorld, depth, currentRefractiveIndex);

        Vec3 returnColor = color.add_(phong).add_(reflection).add_(refraction);

        return new Vec3(
                Math.min(returnColor.x, 1.0f),
                Math.min(returnColor.y, 1.0f),
                Math.min(returnColor.z, 1.0f));
    }

    private Vec3 calculatePhongLightingColor(SceneObject object, Vec3 P, Vec3 V, Vec3 N)
    {
        Vec3 diffuse = new Vec3(0.0f);
        Vec3 specular = new Vec3(0.0f);

        for(Light light : this.scene.getLights())
        {
            Vec3 L = (light.getPosition().sub_(P)).normalize();
            Vec3 R = L.sub_(N.mul_(L.dot(N)).mul_(2.0f)).normalize(); //reflection from L
            Vec3 H = V.add_(L).normalize();

            if(shadow(P, L, N))
                continue;

            float diffuseCoefficent = (float) Math.max(N.dot(L), 0.0);
            diffuse = diffuse.add_(light.getColor().mul_(diffuseCoefficent).mul_(object.getMaterial().getDiffuse()));

            float specularCoefficent = (float) Math.pow(Math.max(H.dot(N), 0.0), object.getMaterial().getShininess() * Constants.BLINN_EXPONENT_MULTIPLIER);
            specular = specular.add_(new Vec3(light.getColor().mul_(specularCoefficent).mul_(object.getMaterial().getSpecular())));
        }

        return diffuse.add_(specular);
    }

    private Vec3 calculateReflectiveColor(SceneObject object, Vec3 P, Vec3 N, Vec3 V, int depth, double currentRefractiveIndex)
    {
        Vec3 specular = object.getMaterial().getSpecular();

        if(specular.x != 0 || specular.y != 0 || specular.z != 0)
        {
            Ray reflect = new Ray(
                    new Vec4(P.add_(N.mul_((float) Constants.CANCER_DELTA)), Constants.POINT),
                    new Vec4(V.sub_(N.mul_(V.dot(N)).mul_(2)).normalize(), Constants.VEC).mul_(-1.0f)
            );

            return object.getMaterial().getSpecular().mul_(trace(reflect, depth + 1, currentRefractiveIndex));
        }

        return new Vec3(0.0f);
    }

    private Vec3 calculateRefractiveColor(SceneObject object, Ray ray, Vec3 N, Vec3 P, int depth, double currentRefractiveIndex)
    {
        Vec3 refraction = object.getMaterial().getRefraction();

        if(refraction.x != 0.0f || refraction.y != 0.0f || refraction.z != 0.0f)
        {
            double nr;
            if(new Vec3(ray.getDirection()).dot(N) > 0) //are we inside the sphere
            {
                nr = currentRefractiveIndex / Constants.AIR_REFRACTIVE_INDEX;
                currentRefractiveIndex = Constants.AIR_REFRACTIVE_INDEX;
                N = N.mul_(-1); //make the normal point the other direction
            }
            else //to another material
            {
                nr = currentRefractiveIndex / object.getMaterial().getRefractiveIndex();
                currentRefractiveIndex = object.getMaterial().getRefractiveIndex();
            }

            double ni = N.dot(new Vec3(ray.getDirection())); // n dot i
            double sqrtInside = 1.0 - (nr * nr) * (1.0 - (ni * ni)); //inside the sqrt

            if(sqrtInside > 0)
            {
                Ray refract = new Ray(
                        new Vec4(P.sub_(N.mul_((float) Constants.CANCER_DELTA)), Constants.POINT),
                        new Vec4(N.mul_((float) (nr * ni - Math.sqrt(sqrtInside))).sub_(new Vec3(ray.getDirection().mul_(-1)).mul_((float) nr)), Constants.VEC).normalize()
                );

                return object.getMaterial().getRefraction().mul_(trace(refract, depth + 1, currentRefractiveIndex));
            }
        }

        return new Vec3(0.0f);
    }


    private boolean shadow(Vec3 P, Vec3 L, Vec3 N)
    {
        Ray ray = new Ray(
                new Vec4(P.add_(N.mul_((float) Constants.CANCER_DELTA)), Constants.POINT),
                new Vec4(L , Constants.VEC).normalize());

        for(SceneObject object : this.scene.getSceneObjects())
        {
            if (object.intersect(new Intersection(ray)))
                return true;
        }

        return false;
    }
}
