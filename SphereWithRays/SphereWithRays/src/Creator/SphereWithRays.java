package Creator;

import Geometry.IModel;
import Geometry.Math.Matrix4;
import Geometry.Math.Vector3;
import Geometry.Math.Vector4;
import Geometry.Triangle;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SphereWithRays extends IModel {
    // число точек для разбиения окружности(больше - качественнее, но медленнее отрисовка)
    public static final int pointNum = 50;
    public SphereWithRays(double r, int rayAmount, double high, double raySize, RayType rayType,
                          int rayPolyPointsNum, Color spColor, Color rayColor){
        // для начала сфера
        // разбиваем окружность на "слои"
        for (int hStep = 0; hStep < pointNum; hStep++) {
            // нижний слой
            double a1 = hStep * Math.PI / pointNum; // [-PI; PI)
            double h1 = r * Math.cos(a1);
            double r1 = r * Math.sin(a1);
            // слой который ровно над предыдущим
            double a2 = (hStep + 1) * Math.PI / pointNum; // (-PI; PI]
            double h2 = r * Math.cos(a2);
            double r2 = r * Math.sin(a2);

            // число точек для слоя(максимальное из двух)(пропорционально радиусу слоя(а следовательно и длине окружности)
            int numOfPointsForFloor = (int) ((Math.max(r1, r2) / r) * pointNum * 2);
            // все точки для нижнего слоя
            List<Vector3> from = getCirclePoints(numOfPointsForFloor, r1, h1, 0);
            // все точки для верхнего слоя(чуть выше, с другим радиусом и с небольшим сдвигом по углу)
            List<Vector3> to = getCirclePoints(numOfPointsForFloor, r2, h2, 2 * Math.PI / numOfPointsForFloor);


            for (int i = 0; i < numOfPointsForFloor; i++) {
                int nextInd = i + 1 < numOfPointsForFloor ? i + 1 : 0;
                // соединяем каждую пару из двух точек с верхнего и нижнего этажа двумя треугольниками
                list.add(new Triangle(from.get(i).make4DVector(),
                        to.get(i).make4DVector(),
                        from.get(nextInd).make4DVector(),
                        spColor));
                list.add(new Triangle(to.get(i).make4DVector(),
                        from.get(nextInd).make4DVector(),
                        to.get(nextInd).make4DVector(),
                        spColor));
            }
        }
        // теперь лучики
        int hSteps = rayAmount / 2;
        for (int hStep = 0; hStep <= hSteps; hStep++) {
            // все для текущего этажа
            double a = hStep * Math.PI / hSteps; // - PI to PI
            double h = r * Math.cos(a);
            double rNow = r * Math.sin(a);
            // число лучей на текущем месте(хотя бы 1)
            int numOfRays = (int) (rNow * rayAmount) + 1;
            // точки лучей
            List<Vector3> floor = getCirclePoints(numOfRays, rNow, h, 0);
            for (int i = 0; i < numOfRays; i++) {
                Vector3 pointOnSphere = floor.get(i);
                // для каждой точки на текущем слое генерируем и сохраняем лучик
                list.addAll(rayGenerate(pointOnSphere, high, raySize, rayType, rayPolyPointsNum, rayColor));
            }
        }
    }

    // создание одного луча
    private static List<Triangle> rayGenerate(Vector3 pointOnSphere, double high, double raySize, RayType rayType,
                                              int rayPolyPointsNum, Color color){
        List<Triangle> list = new ArrayList<>();
        // вершина в относительной системе координат
        Vector3 baseRayTop = new Vector3(0.0, 0.0,  high);

        // мы хотим рассчитать на сколько надо повернуть каждый луч, что бы нормаль его основания стала равной необходимой нам

        // v1 - необходимая нам нормаль - нормализованный вектор от точки на сферы(предполагается что центр сферы - точка (0, 0, 0))
        Vector3 v1 = pointOnSphere.normalized();
        // v2 - наша нормаль(так как мы знаем что основание это только x и y координаты, а вершина только z)
        Vector3 v2 = new Vector3(0.0, 0.0,  -1.0);
        // матрица поворота
        Matrix4 rotation;

        double EPS = 1E-5;
        if(v1.dot(v2) > 1 - EPS) {
            // если векторы противоположный, то поворачиваем относительно любой оси(пусть x) на 180
            rotation = Matrix4.xRotateMatrix(Matrix4.PI);
        }
        else if(v1.dot(v2) < -(1 - EPS)){
            // если векторы сходны, то не поворачиваем
            rotation = Matrix4.identity();
        }
        else {
            // иначе считаем ось поворота(векторное произведение векторов)
            Vector3 axis = v1.crossProduct(v2);
            // и кратчайшую дугу
            double angle = Math.sqrt(v1.abs() * v2.abs()) + v1.dot(v2);
            rotation = Matrix4.rotation(axis, angle * Math.PI / 2);
        }

        // генерируем луч, сначала в относительно системе координат
        switch (rayType){
            case PLANE -> {
                Triangle triangle = new Triangle(new Vector4(-raySize, 0.0, 0.0, 1.0),
                        new Vector4(raySize, 0.0, 0.0, 1.0),
                        baseRayTop.make4DVector(),
                        color);
                list.add(triangle);
            }
            case CONE_OR_PYRAMIDE -> {
                List<Vector3> points = getCirclePoints(rayPolyPointsNum, raySize, 0, 0.0);
                for (int i = 0; i < rayPolyPointsNum; i++) {
                    int nextInd = i + 1 < rayPolyPointsNum ? i + 1 : 0;
                    Vector3 p1 = points.get(i);
                    Vector3 p3 = points.get(nextInd);
                    // пирамида это просто треугольники от основания к вершине
                    Triangle triangle = new Triangle(p1.make4DVector(), baseRayTop.make4DVector(), p3.make4DVector(), color);
                    list.add(triangle);
                }
            }

            case CYLINDER_OR_PRISME -> {
                List<Vector3> points = getCirclePoints(rayPolyPointsNum, raySize, 0, 0.0);
                List<Vector3> points1 = getCirclePoints(rayPolyPointsNum, raySize, high, 0.0);
                for (int i = 0; i < rayPolyPointsNum; i++) {
                    int nextInd = i + 1 < rayPolyPointsNum ? i + 1 : 0;
                    // призма это два треугольника для каждой пары точек верхнего и нижнего основания
                    Triangle triangle = new Triangle(
                            points.get(i).make4DVector(),
                            points1.get(i).make4DVector(),
                            points.get(nextInd).make4DVector(), color);
                    list.add(triangle);

                    triangle = new Triangle(
                            points1.get(i).make4DVector(),
                            points1.get(nextInd).make4DVector(),
                            points.get(nextInd).make4DVector(), color);
                    list.add(triangle);
                }
            }

        }

        // не забываем повернуть и передвинуть все лучи
        List<Triangle> result = new ArrayList<>(list.size());
        for (Triangle triangle: list) {
            triangle = triangle.multiple(rotation);
            triangle = triangle.multiple(Matrix4.translation(pointOnSphere));
            result.add(triangle);
        }

        return result;
    }

    // получаем все координаты точек(x, y) лежащие на окружности с данным радиусом с заданным углом начала и
    // z координатой каждой точки(высотой)
    private static List<Vector3> getCirclePoints(int numOfVertexes, double radius, double high, double offsetAngle){
        List<Vector3> list = new ArrayList<>();
        for (int i = 0; i < numOfVertexes; i++) {
            double angle = offsetAngle + 2 * Math.PI * i / numOfVertexes;
            list.add(new Vector3(Math.cos(angle)*radius, Math.sin(angle)*radius, high));
        }
        return list;
    }


}
