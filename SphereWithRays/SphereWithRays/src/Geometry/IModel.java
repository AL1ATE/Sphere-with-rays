package Geometry;

import Geometry.Math.*;

import java.util.ArrayList;
import java.util.List;

public class IModel {
    public List<Triangle> list = new ArrayList<>();

    public IModel() {
    }

    protected Vector3 position = new Vector3(0.0);

    // метод умножающий каждый треугольник на матрицу трансформации
    public void transform(Matrix4 input){
        List<Triangle> newList = new ArrayList<>();
        for (Triangle triangle : list) {
            newList.add(triangle.multiple(input));
        }
        list = newList;
    }
}