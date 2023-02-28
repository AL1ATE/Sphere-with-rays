package Geometry;

import Geometry.Math.*;

import java.awt.*;

public class Triangle {

    private final Vector4[] points = new Vector4[3];
    public final Color color;

    public static final double EPS = 1E-6;
    public Triangle(Vector4 p1, Vector4 p2, Vector4 p3, Color color) {
        points[0] = p1;
        points[1] = p2;
        points[2] = p3;
        this.color = color;
    }

    // умножение каждой точки треугольника на матрицу
    public Triangle multiple(Matrix4 matrix4) {
        return new Triangle(matrix4.multiple(points[0]), matrix4.multiple(points[1]), matrix4.multiple(points[2]), color);
    }

    public Vector4 getPoint(int pointNum){
        return points[pointNum];
    }

    // вектор нормали(перпендикулярный плоскости треугольника)
    public Vector3 normalVec(){
        Vector3 normal;
        Vector3 v1 = new Vector3(points[1].minus(points[0]));
        Vector3 v2 = new Vector3(points[2].minus(points[0]));
        Vector3 crossProduct = v1.crossProduct(v2);
        if (crossProduct.sqrAbs() > EPS) {
            normal = crossProduct.normalized();
        } else {
            normal = new Vector3(0.0);
        }
        return normal;
    }
}
