package utils;

/**
 * Created by Swinny on 10/18/2016.
 */
public class Intersection
{
    private Ray ray;
    private double intersectionSolution = Constants.INFINITY;

    public Intersection(Ray ray)
    {
        this.ray = ray;
    }

    public Ray getRay()
    {
        return ray;
    }

    public void setRay(Ray ray)
    {
        this.ray = ray;
    }

    public double getIntersectionSolution()
    {
        return intersectionSolution;
    }

    public void setIntersectionSolution(double intersectionSolution)
    {
        this.intersectionSolution = intersectionSolution;
    }
}
