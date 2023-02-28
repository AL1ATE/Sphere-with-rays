package Geometry.Math;

public class Vector3 {
    public final Double x;
    public final Double y;
    public final Double z;

    public Vector3(Double x, Double y, Double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3(Double a) {
        this(a, a, a);
    }

    public Vector3(Vector4 vector4) {
        this(vector4.x, vector4.y, vector4.z);
    }

    public Vector3 plus(Vector3 vec){
        return new Vector3(x + vec.x, y + vec.y, z + vec.z);
    }

    public Vector3 neg(){
        return new Vector3(-x, -y, -z);
    }

    public Vector3 minus(Vector3 vec){
        return plus(vec.neg());
    }

    public Vector3 multiple(double val) {
        return new Vector3(x * val, y * val, z * val);
    }

    // длина вектора в квадрате
    public double sqrAbs(){
        return x * x + y * y + z * z;
    }

    public double abs(){
        return Math.abs(sqrAbs());
    }



    public Vector3 normalized() {
        double EPS = 1E-6;
        double vecAbs = sqrAbs();
        if (vecAbs > EPS) {
            return multiple(1 / Math.sqrt(vecAbs));
        } else {
            return new Vector3(1.0);
        }
    }

    public Vector3 crossProduct(Vector3 vec){
        return new Vector3(y * vec.z - vec.y * z,
                z * vec.x - vec.z * x,
                x * vec.y - vec.x * y);
    }



    public double dot(Vector3 vec)  {
        return vec.x * x + vec.y * y + vec.z * z;
    }

    public Vector4 make4DVector() {
        return new Vector4(x, y, z, 1.0);
    }
}
