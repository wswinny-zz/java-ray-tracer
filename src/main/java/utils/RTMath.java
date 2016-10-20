package utils;

public class RTMath
{
    public static boolean solveQuadratic(double a, double b, double c, QuadraticSolution q)
    {
        double radical = (b * b) - (4 * a * c);

        if(radical < 0)
            return false;
        else if(radical == 0)
            return false;
        else
        {
            q.r0 = (-0.5 * (b - Math.sqrt(radical))) / a;
            q.r1 = (-0.5 * (b + Math.sqrt(radical))) / a;
        }

        return true;
    }
}
