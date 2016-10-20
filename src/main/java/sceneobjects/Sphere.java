package sceneobjects;

import utils.Intersection;
import utils.QuadraticSolution;
import utils.RTMath;
import glm.mat._4.Mat4;
import glm.vec._3.Vec3;
import glm.vec._4.Vec4;

/**
 * Created by Swinny on 10/18/2016.
 */
public class Sphere extends SceneObject
{
    public Sphere(Mat4 T)
    {
        this.T = T;
        this.IT = this.T.inverse();
    }

    public boolean intersect(Intersection intersection)
    {
        Vec4 uPrime = this.IT.mul_(intersection.getRay().getOrigin());
        Vec4 vPrime = this.IT.mul_(intersection.getRay().getDirection());

        double a = Vec4.dot(vPrime, vPrime);
        double b = 2 * Vec4.dot(vPrime, uPrime);
        double c = Vec3.dot(new Vec3(uPrime), new Vec3(uPrime)) - 1;

        QuadraticSolution q = new QuadraticSolution();

        if(!RTMath.solveQuadratic(a, b, c, q))
            return false;
        if(q.r0 > q.r1)
        {
            double temp = q.r0;
            q.r0 = q.r1;
            q.r1 = temp;
        }
        if(q.r0 < 0)
        {
            q.r0 = q.r1;

            if(q.r0 < 0)
                return false;
        }

        intersection.setIntersectionSolution(q.r0);

        return true;
    }
}
