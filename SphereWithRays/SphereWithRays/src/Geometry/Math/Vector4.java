package Geometry.Math;

public class Vector4 {
    public final Double x;
    public final Double y;
    public final Double z;
    public final Double w;

    public Vector4(Double x, Double y, Double z, Double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vector4 plus(Vector4 vec){
        return new Vector4(x + vec.x, y + vec.y, z + vec.z, w + vec.w);
    }

    public Vector4 neg(){
        return new Vector4(-x, -y, -z, -w);
    }

    public Vector4 minus(Vector4 vec){
        return plus(vec.neg());
    }

    public Vector4 multiple(double number) {
        return new Vector4(x * number, y * number, z * number, w * number);
    }

    public Vector4 divide(double number) {
        double EPS = 1E-6;
        if (Math.abs(number) <= EPS) {
            throw new IllegalArgumentException("divide by zero");
        }
        return multiple(1.0 / number);
    }


}