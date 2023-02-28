package Geometry.Math;

public class Matrix4 {
    public final double[][] array = new double[4][4];

    public static final double PI = Math.PI;

    public Matrix4(double val){
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                array[x][y] = val;
            }
        }
    }

    public static Matrix4 zero(){
        return new Matrix4(0);
    }

    public static Matrix4 identity() {
        Matrix4 result = Matrix4.zero();

        for (int i = 0; i < 4; i++) {
            result.array[i][i] = 1.0;
        }

        return result;
    }

    public Matrix4 multiple(Matrix4 matrix){
        Matrix4 result = Matrix4.zero();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < 4; k++) {
                    result.array[i][j] += this.array[i][k] * matrix.array[k][j];
                }
            }
        }
        return result;
    }

    public Vector4 multiple(Vector4 point4D) {
        return new Vector4(
                array[0][0] * point4D.x + array[0][1] * point4D.y + array[0][2] * point4D.z + array[0][3] * point4D.w,
                array[1][0] * point4D.x + array[1][1] * point4D.y + array[1][2] * point4D.z + array[1][3] * point4D.w,
                array[2][0] * point4D.x + array[2][1] * point4D.y + array[2][2] * point4D.z + array[2][3] * point4D.w,
                array[3][0] * point4D.x + array[3][1] * point4D.y + array[3][2] * point4D.z + array[3][3] * point4D.w
        );
    }
    // метод генерирующий матрицу трансформации вращения вокруг заданного вектора
    public static Matrix4 rotation(Vector3 v, double rv) {
        Matrix4 Rv = Matrix4.zero();
        Vector3 nv = v.normalized();

        double c = Math.cos(rv), s = Math.sin(rv);

        Rv.array[0][0] = c + (1.0 - c) * nv.x * nv.x;
        Rv.array[0][1] = (1.0 - c) * nv.x * nv.y - s * nv.z;
        Rv.array[0][2] = (1.0 - c) * nv.x * nv.z + s * nv.y;

        Rv.array[1][0] = (1.0 - c) * nv.y * nv.x + s * nv.z;
        Rv.array[1][1] = c + (1.0 - c) * nv.y * nv.y;
        Rv.array[1][2] = (1.0 - c) * nv.y * nv.z - s * nv.x;

        Rv.array[2][0] = (1.0 - c) * nv.z * nv.x - s * nv.y;
        Rv.array[2][1] = (1.0 - c) * nv.z * nv.y + s * nv.x;
        Rv.array[2][2] = c + (1.0 - c) * nv.z * nv.z;

        Rv.array[3][3] = 1.0;

        return Rv;
    }

    public static Matrix4 xRotateMatrix(double rx){
        return rotation(new Vector3(1.0, 0.0, 0.0), rx);
    }

    public static Matrix4 yRotateMatrix(double ry){
        return rotation(new Vector3(0.0, 1.0, 0.0), ry);
    }


    public static Matrix4 zRotateMatrix(double rz){
        return rotation(new Vector3(0.0, 0.0, 1.0), rz);
    }

    public static Matrix4 rotateByVec(Vector3 r){
        return  Matrix4.xRotateMatrix(r.x).multiple(Matrix4.yRotateMatrix(r.y)).multiple(Matrix4.zRotateMatrix(r.z));
    }

    // просто перенос
    public static Matrix4 translation(Vector3 v) {
        Matrix4 t = Matrix4.zero();
        t.array[0][0] = 1.0;
        t.array[1][1] = 1.0;
        t.array[2][2] = 1.0;
        t.array[3][3] = 1.0;

        t.array[0][3] = v.x;
        t.array[1][3] = v.y;
        t.array[2][3] = v.z;

        return t;
    }

    public static Matrix4 projectMatrix(double fov, double aspect, double ZNear, double ZFar) {
        Matrix4 p = Matrix4.zero();

        p.array[0][0] = 1.0 / (Math.tan(PI * fov * 0.5 / 180.0) * aspect);
        p.array[1][1] = 1.0 / Math.tan(PI * fov * 0.5 / 180.0);
        p.array[2][2] = ZFar / (ZFar - ZNear);
        p.array[2][3] = -ZFar * ZNear / (ZFar - ZNear);
        p.array[3][2] = 1.0;

        return p;
    }

    public static Matrix4 screenArea(int width, int height) {
        Matrix4 s = Matrix4.zero();

        s.array[0][0] = -0.5 * width;
        s.array[1][1] = -0.5 * height;
        s.array[2][2] = 1.0;

        s.array[0][3] = 0.5 * width;
        s.array[1][3] = 0.5 * height;

        s.array[3][3] = 1.0;

        return s;
    }

    public static Matrix4 viewMatrix(Matrix4 transformMatrix){
        Matrix4 V = Matrix4.zero();

        Vector3 left      = transformMatrix.x();
        Vector3 up        = transformMatrix.y();
        Vector3 lookAt    = transformMatrix.z();
        Vector3 view       = transformMatrix.w();

        double leftSqrAbs      = left.sqrAbs();
        double upSqrAbs        = up.sqrAbs();
        double lookAtSqrAbs    = lookAt.sqrAbs();

        V.array[0][0] = left.x/leftSqrAbs;
        V.array[0][1] = left.y/leftSqrAbs;
        V.array[0][2] = left.z/leftSqrAbs;
        V.array[0][3] = -view.dot(left)/leftSqrAbs;

        V.array[1][0] = up.x/upSqrAbs;
        V.array[1][1] = up.y/upSqrAbs;
        V.array[1][2] = up.z/upSqrAbs;
        V.array[1][3] = -view.dot(up)/upSqrAbs;

        V.array[2][0] = lookAt.x/lookAtSqrAbs;
        V.array[2][1] = lookAt.y/lookAtSqrAbs;
        V.array[2][2] = lookAt.z/lookAtSqrAbs;
        V.array[2][3] = -view.dot(lookAt)/lookAtSqrAbs;

        V.array[3][3] = 1.0;

        return V;
    }

    public Vector3 x(){
        return new Vector3(array[0][0], array[1][0], array[2][0]);
    }

    public Vector3 y(){
        return new Vector3(array[0][1], array[1][1], array[2][1]);
    }

    public Vector3 z(){
        return new Vector3(array[0][2], array[1][2], array[2][2]);
    }

    public Vector3 w(){
        return new Vector3(array[0][3], array[1][3], array[2][3]);
    }

}
