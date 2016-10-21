package scene;

import com.owens.oobjloader.builder.Build;
import com.owens.oobjloader.builder.Face;
import com.owens.oobjloader.parser.Parse;
import glm.mat._4.Mat4;
import glm.vec._3.Vec3;
import glm.vec._4.Vec4;
import light.Light;
import material.Material;
import sceneobjects.Sphere;
import sceneobjects.Triangle;
import utils.Constants;

import java.io.*;
import java.util.Stack;

public class SceneParser
{
    private Scene scene;
    private File file;

    public SceneParser(File file)
    {
        this.file = file;
        this.scene = new Scene();
    }

    public Scene parseObjFile()
    {
        Mat4 defaultTrans = new Mat4(1.0f);

        Material defaultMat = new Material(new Vec3(1.0, 1.0, 1.0),
                new Vec3(1.0, 1.0, 1.0), 5.0);

        this.scene.setImageWH(1024);
        this.scene.setCornerDist(0.5f);

        this.scene.setBackgroundColor(new Vec3(0.0f, 1.0, 0.0));
        this.scene.getLights().add(new Light(
                new Vec3(0.0, 1.0, 1.0),
                new Vec3(1.0, 0.0, 1.0)
        ));

        try
        {
            Build builder = new Build();
            Parse obj = new Parse(builder, this.file.getName());

            for(Face face : builder.faces)
            {
                Triangle triangle = new Triangle(
                        defaultTrans,
                        new Vec3(face.vertices.get(0).v.x, face.vertices.get(0).v.y, face.vertices.get(0).v.z ),
                        new Vec3(face.vertices.get(1).v.x, face.vertices.get(1).v.y, face.vertices.get(1).v.z ),
                        new Vec3(face.vertices.get(2).v.x, face.vertices.get(2).v.y, face.vertices.get(2).v.z )
                );

                triangle.setMaterial(defaultMat);
                triangle.setN(new Vec4(face.vertices.get(0).n.x, face.vertices.get(0).n.y, face.vertices.get(0).n.z, Constants.VEC));
                this.scene.getSceneObjects().add(triangle);
            }
        }
        catch (Exception e)
        {

        }

        return this.scene;
    }

    public Scene parseScnFile()
    {
        Stack<Mat4> currentTransform = new Stack<Mat4>();
        Stack<Material> currentMaterial = new Stack<Material>();

        currentTransform.push(new Mat4(1.0f));

        Material defaultMat = new Material(new Vec3(1.0, 1.0, 1.0),
                new Vec3(1.0, 1.0, 1.0), 5.0);

        currentMaterial.push(defaultMat);

        BufferedReader br = null;

        try
        {
            br = new BufferedReader(new FileReader(this.file));

            for(String line; (line = br.readLine()) != null;)
            {
                if(line.equals(""))
                    continue;

                String [] splitLine = line.trim().split(" ");

                switch(splitLine[0])
                {
                    case "#":
                        continue;
                    case "view":
                        this.scene.setImageWH(Integer.parseInt(splitLine[1]));
                        this.scene.setCornerDist(Double.parseDouble(splitLine[2]));
                        break;
                    case "group":
                        currentTransform.push(new Mat4(currentTransform.peek()));
                        currentMaterial.push(new Material(currentMaterial.peek()));
                        break;
                    case "groupend":
                        currentTransform.pop();
                        currentMaterial.pop();
                        break;
                    case "background":
                        Vec3 backgroundColor = new Vec3(
                                Double.parseDouble(splitLine[1]),
                                Double.parseDouble(splitLine[2]),
                                Double.parseDouble(splitLine[3])
                        );
                        this.scene.setBackgroundColor(backgroundColor);
                        break;
                    case "ambient":
                        Vec3 ambientColor = new Vec3(
                                Double.parseDouble(splitLine[1]),
                                Double.parseDouble(splitLine[2]),
                                Double.parseDouble(splitLine[3])
                        );
                        this.scene.setAmbientLight(ambientColor);
                        break;
                    case "light":
                        Vec3 lightColor = new Vec3(
                                Double.parseDouble(splitLine[1]),
                                Double.parseDouble(splitLine[2]),
                                Double.parseDouble(splitLine[3])
                        );

                        Vec3 lightPos = new Vec3(
                                Double.parseDouble(splitLine[4]),
                                Double.parseDouble(splitLine[5]),
                                Double.parseDouble(splitLine[6])
                        );

                        this.scene.getLights().add(new Light(lightPos, lightColor));
                        break;
                    case "sphere":
                        Sphere sphere = new Sphere(new Mat4(currentTransform.peek()));
                        sphere.setMaterial(currentMaterial.peek());
                        this.scene.getSceneObjects().add(sphere);
                        break;
                    case "triangle":
                        Vec3 a = new Vec3(
                                Double.parseDouble(splitLine[1]),
                                Double.parseDouble(splitLine[2]),
                                Double.parseDouble(splitLine[3])
                        );

                        Vec3 b = new Vec3(
                                Double.parseDouble(splitLine[4]),
                                Double.parseDouble(splitLine[5]),
                                Double.parseDouble(splitLine[6])
                        );

                        Vec3 c = new Vec3(
                                Double.parseDouble(splitLine[7]),
                                Double.parseDouble(splitLine[8]),
                                Double.parseDouble(splitLine[9])
                        );

                        Triangle triangle = new Triangle(new Mat4(currentTransform.peek()), a, b, c);
                        triangle.setMaterial(currentMaterial.peek());
                        this.scene.getSceneObjects().add(triangle);
                        break;
                    case "material":
                        Vec3 diffuse = new Vec3(
                                Double.parseDouble(splitLine[1]),
                                Double.parseDouble(splitLine[2]),
                                Double.parseDouble(splitLine[3])
                        );

                        Vec3 specular = new Vec3(
                                Double.parseDouble(splitLine[4]),
                                Double.parseDouble(splitLine[5]),
                                Double.parseDouble(splitLine[6])
                        );

                        double shininess = Double.parseDouble(splitLine[7]);

                        currentMaterial.pop();
                        currentMaterial.push(new Material(diffuse, specular, shininess));
                        break;
                    case "refraction":
                        Vec3 refraction = new Vec3(
                                Double.parseDouble(splitLine[1]),
                                Double.parseDouble(splitLine[2]),
                                Double.parseDouble(splitLine[3])
                        );

                        double refractiveIndex = Double.parseDouble(splitLine[4]);

                        currentMaterial.peek().setRefraction(refraction);
                        currentMaterial.peek().setRefractiveIndex(refractiveIndex);
                        break;
                    case "texture":
                        break;
                    case "move":
                        Vec3 moveVector = new Vec3(
                                Double.parseDouble(splitLine[1]),
                                Double.parseDouble(splitLine[2]),
                                Double.parseDouble(splitLine[3])
                        );

                        Mat4 moveMat = new Mat4(currentTransform.peek().translate(moveVector));

                        currentTransform.pop();
                        currentTransform.push(moveMat);
                        break;
                    case "scale":
                        Vec3 scaleVector = new Vec3(
                                Double.parseDouble(splitLine[1]),
                                Double.parseDouble(splitLine[2]),
                                Double.parseDouble(splitLine[3])
                        );

                        Mat4 scaleMat = new Mat4(currentTransform.peek().scale(scaleVector));

                        currentTransform.pop();
                        currentTransform.push(scaleMat);
                        break;
                    case "rotate":
                        double angle = Double.parseDouble(splitLine[1]);

                        Vec3 rotateVector = new Vec3(
                                Double.parseDouble(splitLine[2]),
                                Double.parseDouble(splitLine[3]),
                                Double.parseDouble(splitLine[4])
                        );

                        Mat4 rotateMat = new Mat4(currentTransform.peek().rotate((float)Math.toRadians(angle), rotateVector));

                        currentTransform.pop();
                        currentTransform.push(rotateMat);
                        break;
                    default:
                        System.out.println("Undefined scenefile item: " + splitLine[0]);
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.out.println("Bad scene file, exiting!");
            System.exit(-1);
        }
        finally
        {
            if(br != null)
            {
                try
                {
                    br.close();
                }
                catch (IOException e)
                {

                }
            }
        }

        return this.scene;
    }
}
