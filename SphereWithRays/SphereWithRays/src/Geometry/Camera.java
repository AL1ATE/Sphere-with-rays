package Geometry;

import Geometry.Math.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static Geometry.Triangle.EPS;

public class Camera {
    private final Matrix4 screenProjection;

    public Vector3 position = new Vector3(0.0);

    protected Matrix4 rotation = Matrix4.identity();

    public Camera(int width, int height, double fov, double ZNear, double ZFar) {
        double aspect = (double) width / (double) height;
        Matrix4 P = Matrix4.projectMatrix(fov, aspect, ZNear, ZFar);
        Matrix4 S = Matrix4.screenArea(width, height);
        screenProjection = S.multiple(P);

    }

    private Matrix4 camTransformMatrix() {
        return Matrix4.viewMatrix(Matrix4.translation(position).multiple(rotation));
    }

    public void move(Vector3 add){
        position = position.plus(add);
    }

    public void rotate(Vector3 rotation){
        this.rotation = this.rotation.multiple(Matrix4.rotateByVec(rotation));
    }

    private List<Triangle> project(IModel mesh) {
        List<Triangle> projectedTriangle = new ArrayList<>();

        for (Triangle meshTri : mesh.list) {

            Triangle triInCamCoordSystem = meshTri.multiple(Matrix4.translation(mesh.position)).multiple(camTransformMatrix());

            // если хоть одна точка треугольника находится за камерой, то пропускаем его
            if(triInCamCoordSystem.getPoint(0).z < EPS ||
                    triInCamCoordSystem.getPoint(1).z < EPS || triInCamCoordSystem.getPoint(2).z < EPS){
                continue;
            }

            // проецируем треугольник умножим на матрицу проекции
            Triangle projected = triInCamCoordSystem.multiple(screenProjection);


            // делаем цвет тем темнее чем мы более под острым углом к камере
            double normalAngle = triInCamCoordSystem.normalVec().dot(new Vector3(triInCamCoordSystem.getPoint(0))
                    .minus(position).normalized());
            Color clearColor = meshTri.color;
            Color color = new Color(
                    (int) (clearColor.getRed() * ( 0.5 * Math.abs(normalAngle) + 0.5)),
                    (int) (clearColor.getGreen() * (0.5 * Math.abs(normalAngle) + 0.5 )),
                    (int)  (clearColor.getBlue() * (0.5 * Math.abs(normalAngle ) + 0.5)),
                    clearColor.getAlpha());


            // нормализуем используя деление на четвертую координату
            Triangle projectedNormalized = new Triangle(
                    projected.getPoint(0).divide(projected.getPoint(0).w),
                    projected.getPoint(1).divide(projected.getPoint(1).w),
                    projected.getPoint(2).divide(projected.getPoint(2).w),
                    color);

            // сохраняем
            projectedTriangle.add(projectedNormalized);
        }
        return projectedTriangle;
    }

    private static void sortByDistance(List<Triangle> triangles){
        try {
            triangles.sort((t1, t2) -> {
                double z1Sum = t1.getPoint(0).z + t1.getPoint(1).z + t1.getPoint(2).z;
                double z2Sum = t2.getPoint(0).z + t2.getPoint(1).z + t2.getPoint(2).z;

                if(Math.abs(z1Sum - z2Sum) < 1E-6)  return 0;
                else if(z1Sum < z2Sum) return 1;

                return -1;
            });
        } catch (IllegalArgumentException ignored){

        }
    }

    public List<Triangle> projected(List<IModel> models){
        // проецируем все модели
        List<Triangle> projected = new ArrayList<>();
        for(IModel model: models){
            projected.addAll(project(model));
        }
        // сортируем по удалению от камеры(алгоритм художника)
        Camera.sortByDistance(projected);

        return projected;
    }


}
