package object_data;

import transforms.Col;
import transforms.Point3D;

import java.util.List;

public class Cube implements Solid{

    private final List<Vertex> vertices;
    private final List<Integer> indices;
    private final List<Part> parts;

    public Cube() {
        vertices = List.of(
                new Vertex(new Point3D(0, 0, 0), new Col(255, 0, 0)), // 0
                new Vertex(new Point3D(0, 1, 0), new Col(0, 255, 0)), // 1
                new Vertex(new Point3D(1, 0, 0), new Col(0, 0, 255)), // 2
                new Vertex(new Point3D(1, 1, 0), new Col(127, 127, 127)), // 3

                new Vertex(new Point3D(0, 0, 1), new Col(127, 127, 127)), // 4
                new Vertex(new Point3D(0, 1, 1), new Col(0, 0, 255)), // 5
                new Vertex(new Point3D(1, 0, 1), new Col(0, 255, 0)), // 6
                new Vertex(new Point3D(1, 1, 1), new Col(255, 0, 0))  // 7
        );
        indices = List.of(
                0, 2, 3, 1, // bottom
                5, 4, // left
                6, 2, // up
                7, 3, 1, 5, // down
                4, 6, // top
                2, 3  // right
        );

        parts = List.of(
                new Part(Topology.TRIANGLE_FAN, 0, 8),
                new Part(Topology.TRIANGLE_FAN, 8, 8)
        );
    }
    @Override
    public List<Vertex> getVertices() {
        return vertices;
    }

    @Override
    public List<Integer> getIndices() {
        return indices;
    }

    @Override
    public List<Part> getParts() {
        return parts;
    }

}