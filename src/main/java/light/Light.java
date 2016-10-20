package light;

import glm.vec._3.Vec3;

/**
 * Created by Swinny on 10/18/2016.
 */
public class Light
{
    private Vec3 position;
    private Vec3 color;

    public Light(Vec3 position, Vec3 color)
    {
        this.position = position;
        this.color = color;
    }

    public Vec3 getPosition()
    {
        return position;
    }

    public void setPosition(Vec3 position)
    {
        this.position = position;
    }

    public Vec3 getColor()
    {
        return color;
    }

    public void setColor(Vec3 color)
    {
        this.color = color;
    }
}
