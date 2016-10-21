package sceneobjects;

import glm.Glm;
import glm.mat._4.Mat4;
import glm.vec._3.Vec3;
import glm.vec._4.Vec4;
import jglm.Vec;
import utils.Constants;
import utils.Intersection;

/**
 * Created by swinny on 10/21/16.
 */
public class Triangle extends SceneObject
{
    private static final double EPSILON = 0.000001;

    private Vec3 a, b, c;
    private Vec4 N;

    public Triangle(Mat4 T, Vec3 a, Vec3 b, Vec3 c, Vec4 N)
    {
        this.T = T;
        this.IT = this.T.inverse();

        this.a = a;
        this.b = b;
        this.c = c;

        this.N = N;
    }

    public Triangle(Mat4 T, Vec3 a, Vec3 b, Vec3 c)
    {
        this(T, a, b, c, new Vec4((b.min_(a)).mul_(b.min_(c)), Constants.VEC).normalize());
    }

    @Override
    public Vec3 getNormal(Vec3 P)
    {
        return new Vec3(this.N);
    }

    @Override
    public boolean intersect(Intersection intersection)
    {
        Vec3 edge1, edge2;
        Vec3 P, Q, T;

        float det, inv_det, u, v;
        float t;

        edge1 = this.b.sub_(this.a);
        edge2 = this.c.sub_(this.a);

        P = Glm.cross_(new Vec3(intersection.getRay().getDirection()), edge2);
        det = edge1.dot(P);

        if(det > -EPSILON && det < EPSILON)
            return false;

        inv_det = 1.f / det;

        T = new Vec3(intersection.getRay().getOrigin()).sub_(this.a);
        u = T.dot(P) * inv_det;

        if(u < 0.f || u > 1.f)
            return false;

        Q = Glm.cross_(T, edge1);
        v = new Vec3(intersection.getRay().getDirection()).dot(Q) * inv_det;

        if(v < 0.f || u + v  > 1.f)
            return false;

        t = edge2.dot(Q) * inv_det;

        if(t > EPSILON)
        {
            intersection.setIntersectionSolution(t);
            return true;
        }

        return false;
    }

    public Vec3 getA() {
        return a;
    }

    public void setA(Vec3 a) {
        this.a = a;
    }

    public Vec3 getB() {
        return b;
    }

    public void setB(Vec3 b) {
        this.b = b;
    }

    public Vec3 getC() {
        return c;
    }

    public void setC(Vec3 c) {
        this.c = c;
    }

    public Vec4 getN() {
        return N;
    }

    public void setN(Vec4 N) {
        this.N = N;
    }
}
