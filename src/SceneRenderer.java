import objectOps.Renderer;
import object_data.*;
import raster_data.ColorRaster;
import raster_data.ZBuffer;
import transforms.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

public class SceneRenderer {
    private final JFrame frame;
    private final JPanel panel;
    private final ColorRaster img;
    private ZBuffer zBuffer;
    private Renderer renderer;
    private final Scene scene = new Scene();
    Camera camera = new Camera(new Vec3D(-10, 10, 5), 0, 0, 1, true);
    private final Double CAMERA_SPEED = 1d;
    private Point2D mousePos;
    private final AxisRGB axisRGB = new AxisRGB();
    private final Mat4Transl arrowMat = new Mat4Transl(1, 1, 1);
    private final Mat4Transl cubeMat = new Mat4Transl(2,9,5);
    private final Mat4Transl prismMat = new Mat4Transl(8, 1, 8);
    private Mat4RotXYZ rotateCubeMat = new Mat4RotXYZ(8, 8, 8);
    private Mat4RotXYZ rotatePrismMat = new Mat4RotXYZ(6,6,6);
    private double gammaRotation = 1;
    private int selectedSolid = 1;
    private final Mat4Transl coonsMat = new Mat4Transl(0, 8, -2);
    private final Mat4Transl fergusonMat = new Mat4Transl(0, -4, -6);
    private final ArrayList<Mat4Transl> objects = new ArrayList<>(List.of(arrowMat, cubeMat, prismMat, coonsMat, fergusonMat));

    private boolean wired = false;

    public SceneRenderer(int width, int height) {
        frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        img = new ColorRaster(width, height, new Col(0, 0, 0));

        panel = new JPanel() {

            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                img.present(g);
            }
        };
        panel.setPreferredSize(new Dimension(width, height));

        System.out.println("""
                Controls:
                Move: WASD | EQ
                Camera angle: LMB
                Wired models: I
                Select Arrow: J
                Select Cube: K
                Select Prism: L
                Move selected: 8546
                Close app: ESC
                """);

        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }

    public void start() {
        zBuffer = new ZBuffer(img);
        renderer = new Renderer(zBuffer);

        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W -> camera = camera.forward(CAMERA_SPEED);
                    case KeyEvent.VK_S -> camera = camera.backward(CAMERA_SPEED);
                    case KeyEvent.VK_A -> camera = camera.left(CAMERA_SPEED);
                    case KeyEvent.VK_D -> camera = camera.right(CAMERA_SPEED);
                    case KeyEvent.VK_E -> camera = camera.up(CAMERA_SPEED);
                    case KeyEvent.VK_Q -> camera = camera.down(CAMERA_SPEED);
                    case KeyEvent.VK_J -> selectedSolid = 0;
                    case KeyEvent.VK_K -> selectedSolid = 1;
                    case KeyEvent.VK_L -> selectedSolid = 2;
                    case KeyEvent.VK_I -> {
                        wired = !wired;
                    }
                    case KeyEvent.VK_NUMPAD8 -> {
                        objects.set(selectedSolid, moveObject(1));
                    }
                    case KeyEvent.VK_NUMPAD6 -> {
                        objects.set(selectedSolid, moveObject(2));
                    }
                    case KeyEvent.VK_NUMPAD5 -> {
                        objects.set(selectedSolid, moveObject(3));
                    }
                    case KeyEvent.VK_NUMPAD4 -> {
                        objects.set(selectedSolid, moveObject(4));
                    }
                    case KeyEvent.VK_ESCAPE -> {
                        System.exit(0);
                    }
                }
            }
        });

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                mousePos = new Point2D(e.getX(), e.getY());
                panel.addMouseMotionListener(new MouseAdapter() {
                    @Override
                    public void mouseDragged(MouseEvent f) {
                        super.mouseDragged(f);
                        double dx = f.getX() - mousePos.getX();
                        double dy = f.getY() - mousePos.getY();

                        camera = camera.addAzimuth(-(dx) * Math.PI / 360);
                        camera = camera.addZenith(-(dy) * Math.PI / 360);

                        mousePos = new Point2D(f.getX(), f.getY());
                        render();
                    }
                });
            }
        });

        final double ZOOM_MODIFIER = 1.2;
        final double UNZOOM_MODIFIER = -1.2;

        panel.addMouseWheelListener(e -> {
            if (e.getWheelRotation() < 0) {
                camera = camera.move(camera.getViewVector().mul(ZOOM_MODIFIER));
            } else {
                camera = camera.move(camera.getViewVector().mul(UNZOOM_MODIFIER));
            }
            render();
        });

        render();

        Runnable rotate = () -> {
            rotateCubeMat = new Mat4RotXYZ(rotateCubeMat.get(3, 0), rotateCubeMat.get(3, 1), rotateCubeMat.get(3,2) + gammaRotation);
            rotatePrismMat = new Mat4RotXYZ(rotatePrismMat.get(3, 0), rotatePrismMat.get(3, 1), rotatePrismMat.get(3,2) + gammaRotation);
            returnSolids();
        };

        Thread thread = new Thread(() -> {
           while (true){
               rotate.run();
               gammaRotation += 0.01;
               try {
                   Thread.sleep(1000 / 60);
               } catch (InterruptedException e) {
                   throw new RuntimeException(e);
               }
           }
        });
        thread.start();
    }

    public void render() {
        zBuffer.clear();
        renderer.drawScene(scene, camera.getViewMatrix(), new Mat4PerspRH(Math.PI / 2, (double) zBuffer.getColRaster().getHeight() / zBuffer.getColRaster().getWidth(), 0.1, 200));
        img.present(panel.getGraphics());
    }

    public Mat4Transl moveObject(int direction) {
        Mat4Transl originalMatrix = objects.get(selectedSolid);

        switch (direction) {
            case 1 -> {
                return new Mat4Transl(originalMatrix.get(3, 0), originalMatrix.get(3, 1), originalMatrix.get(3, 2) + 1);
            }
            case 2 -> {
                return new Mat4Transl(originalMatrix.get(3, 0), originalMatrix.get(3, 1) - 1, originalMatrix.get(3, 2));
            }
            case 3 -> {
                return new Mat4Transl(originalMatrix.get(3, 0), originalMatrix.get(3, 1), originalMatrix.get(3, 2) - 1);
            }
            case 4 -> {
                return new Mat4Transl(originalMatrix.get(3, 0), originalMatrix.get(3, 1) + 1, originalMatrix.get(3, 2));
            }
            case 7 -> {
                return new Mat4Transl(originalMatrix.get(3, 0) - 1, originalMatrix.get(3, 1), originalMatrix.get(3, 2));
            }
            case 9 -> {
                return new Mat4Transl(originalMatrix.get(3, 0) + 1, originalMatrix.get(3, 1), originalMatrix.get(3, 2));
            }
            default -> {
                return new Mat4Transl(0, 0, 0);
            }
        }
    }

    public void returnSolids(){
        scene.clearScene();
        scene.addSolid(axisRGB, new Mat4Scale(2).mul(new Mat4Transl(0, 0, 0)));

        Arrow arrow = new Arrow();
        scene.addSolid(arrow, new Mat4Scale(10).mul(objects.get(0)));

        Cube cube = new Cube(wired);
        scene.addSolid(cube, new Mat4Scale(5).mul(objects.get(1)));

        Prism prism = new Prism(wired);
        scene.addSolid(prism, new Mat4Scale(10).mul(objects.get(2)));

        Cube cubeRotate = new Cube(wired);
        scene.addSolid(cubeRotate, new Mat4Scale(1).mul(new Mat4Transl(1, 1, 0).mul(rotateCubeMat)));

        Prism prismRotate = new Prism(wired);
        scene.addSolid(prismRotate, new Mat4Scale(4).mul(new Mat4Transl(0, 0, 0).mul(rotatePrismMat)));

        scene.addSolid(new BicubicArea(Cubic.COONS, new Col(255,128,255)), new Mat4Scale(4).mul(objects.get(3)));
        scene.addSolid(new BicubicArea(Cubic.FERGUSON, new Col(255,255,80)), new Mat4Scale(3).mul(objects.get(4)));
        render();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SceneRenderer(800, 600).start());
    }


}
