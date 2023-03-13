package object_data;

import transforms.Col;
import transforms.Point3D;

import java.util.List;

public class Cube implements Solid{

    private final List<Vertex> vertices;
    private final List<Integer> indices;
    private final List<Part> parts;

    public Cube(boolean isWired) {
        if(isWired){
            vertices = List.of(
                    new Vertex(new Point3D(0, 0, 0), new Col(255, 255, 0)), // 0
                    new Vertex(new Point3D(0, 1, 0), new Col(255, 255, 0)), // 1
                    new Vertex(new Point3D(1, 0, 0), new Col(255, 255, 0)), // 2
                    new Vertex(new Point3D(1, 1, 0), new Col(255, 255, 0)), // 3

                    new Vertex(new Point3D(0, 0, 1), new Col(255, 255, 0)), // 4
                    new Vertex(new Point3D(0, 1, 1), new Col(255, 255, 0)), // 5
                    new Vertex(new Point3D(1, 0, 1), new Col(255, 255, 0)), // 6
                    new Vertex(new Point3D(1, 1, 1), new Col(255, 255, 0))  // 7
            );
            indices = List.of(0,1,1,3,3,2,2,0,0,4,2,6,1,5,3,7,7,6,6,4,4,5,5,7
            );

            parts = List.of(
                    new Part(Topology.LINE_LIST, 0, 12)
            );
        }else {
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
                    0, 2, 3, 1, 5, 4,
                    6, 2, 7, 3, 1, 5, 4, 6, 2, 3
            );

            parts = List.of(
                    new Part(Topology.TRIANGLE_FAN, 0, 8),
                    new Part(Topology.TRIANGLE_FAN, 8, 8)
            );
        }
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
