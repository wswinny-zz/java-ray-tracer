package utils;

import glm.vec._4.Vec4;

/**
 * Created by Swinny on 10/18/2016.
 */
public class Ray
{
    private Vec4 origin;
    private Vec4 direction;

    public Ray(Vec4 origin, Vec4 direction)
    {
        this.origin = new Vec4(origin);
        this.direction = new Vec4(direction);
    }

    public void print()
    {
        System.out.printf("o: %f %f %f %f \t", this.origin.x, this.origin.y, this.origin.w, this.origin.z);
        System.out.printf("d: %f %f %f %f\n", this.direction.x, this.direction.y, this.direction.w, this.direction.z);
    }

    public Vec4 getOrigin()
    {
        return origin;
    }

    public void setOrigin(Vec4 origin)
    {
        this.origin = origin;
    }

    public Vec4 getDirection()
    {
        return direction;
    }

    public void setDirection(Vec4 direction)
    {
        this.direction = direction;
    }
}
