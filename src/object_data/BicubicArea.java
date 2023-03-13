package object_data;

import transforms.Bicubic;
import transforms.Col;
import transforms.Mat4;
import transforms.Point3D;

import java.util.ArrayList;
import java.util.List;

public class BicubicArea implements Solid{

    private final List<Vertex> vertices = new ArrayList<>();
    private final List<Integer> indices = new ArrayList<>();
    private final List<Part> parts;

    public BicubicArea(Mat4 cubicType, Col color) {
        Point3D[] points = new Point3D[]{
                new Point3D(1, 0, 0),
                new Point3D(1, -0.3, 0),
                new Point3D(0, 0, 1),
                new Point3D(-0.4, -0.3, 0),
                new Point3D(0.8, -0.5, -1),
                new Point3D(1, 0.9, -1.1),
                new Point3D(0.5, -0.8, -0.4),
                new Point3D(0.9, 0.5, -0.5),
                new Point3D(1.2, -0.1, -0.6),
                new Point3D(-1, 1, 1),
                new Point3D(-0.2, -1, 0.5),
                new Point3D(0.2, -0.4, 1.2),
                new Point3D(-0.2, 1, 0),
                new Point3D(-0.3, -0.8, 0.5),
                new Point3D(0, -0.2, 0.5),
                new Point3D(-0.2, 0.8, -1)
        };

        Bicubic bicubic = new Bicubic(cubicType, points);

        int ACCURACY = 30;
        for (int i = 0; i < ACCURACY; i++) {
            for (int j = 0; j < ACCURACY; j++) {
                this.vertices.add(new Vertex(new Point3D(bicubic.compute((double) i / ACCURACY, (double) j / ACCURACY)), color));
                if (j != 0) {
                    this.indices.add((j - 1) + (ACCURACY * i));
                    this.indices.add((j) + (ACCURACY * i));
                }
                if (i != 0) {
                    this.indices.add(j + (ACCURACY) * (i - 1));
                    this.indices.add(j + (ACCURACY * i));
                }
            }
        }
        this.parts = List.of(new Part(Topology.LINE_LIST, 0, 1700));
    }
    @Override
    public List<Vertex> getVertices() {
        return this.vertices;
    }

    @Override
    public List<Integer> getIndices() {
        return this.indices;
    }

    @Override
    public List<Part> getParts() {
        return this.parts;
    }
}
