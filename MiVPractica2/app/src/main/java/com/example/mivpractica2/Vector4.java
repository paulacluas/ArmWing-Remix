package com.example.mivpractica2;

public class Vector4 {

    public static final Vector4 ZERO = new Vector4(0.0f, 0.0f, 0.0f, 0.0f);

    private float values[];

    public Vector4() {
        values = new float[4];
    }

    // Constructor para vectores 3D (asigna automáticamente w = 1.0f)
    public Vector4(float x, float y, float z) {
        values = new float[] {x, y, z, 1.0f};
    }

    public Vector4(float x, float y, float z, float w) {
        values = new float[] {x, y, z, w};
    }

    public float get(int index) {
        return values[index];
    }

    public void set(int index, float value) {
        values[index] = value;
    }

    public float module() {
        float length = 0.0f;
        for (int i = 0; i < values.length; i++)
            length += values[i] * values[i];
        return (float) Math.sqrt(length);
    }

    public Vector4 normalize() {
        float l = module();
        if (l == 0)
            return this;
        else
            return new Vector4(values[0] / l, values[1] / l, values[2] / l, values[3] / l);
    }

    public Vector4 cross3(Vector4 o) {
        return new Vector4(
                values[1] * o.values[2] - values[2] * o.values[1],
                values[2] * o.values[0] - values[0] * o.values[2],
                values[0] * o.values[1] - values[1] * o.values[0],
                0.0f);
    }

    public Vector4 add(Vector4 o) {
        Vector4 v = new Vector4();
        for (int i = 0; i < 4; i++)
            v.set(i, values[i] + o.values[i]);
        return v;
    }

    public Vector4 add(float x, float y, float z, float w) {
        return add(new Vector4(x, y, z, w));
    }

    public Vector4 mult(float d) {
        Vector4 v = new Vector4();
        for (int i = 0; i < 4; i++)
            v.set(i, values[i] * d);
        return v;
    }

    // Nuevo método: resta de vectores
    public Vector4 subtract(Vector4 o) {
        return new Vector4(
                this.values[0] - o.values[0],
                this.values[1] - o.values[1],
                this.values[2] - o.values[2],
                this.values[3] - o.values[3]
        );
    }

    // Nuevo método: distancia entre dos vectores (3D)
    public float distanceTo(Vector4 o) {
        float dx = this.values[0] - o.values[0];
        float dy = this.values[1] - o.values[1];
        float dz = this.values[2] - o.values[2];
        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    // Nuevo método: devuelve un vector solo con los primeros 3 componentes
    public float[] to3DArray() {
        return new float[]{values[0], values[1], values[2]};
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        for (int i = 0; i < values.length; i++) {
            sb.append(values[i]);
            if (i != values.length - 1)
                sb.append(", ");
        }
        sb.append(')');
        return sb.toString();
    }
}
