import Creator.RayType;
import Creator.SphereWithRays;
import Geometry.Math.*;
import Geometry.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    private JButton buildButtton;
    private JPanel canvas;
    private JPanel window;
    private JTextField radiusField;

    private JSpinner numOfRays;

    private JTextField highOfRay;

    private JTextField sizeOfRay;
    private JComboBox<RayType> typeOfRay;
    private JRadioButton rotate;
    private JSpinner polyPointsAmount;

    private final Camera camera = new Camera(800, 800, 90, 0.1, 10000000);

    private Vector3 oldMousePos = new Vector3(0.0, 0.0, 0.0);

    private IModel sphereWithRays = new IModel();

    public Main() {
        camera.move(new Vector3(0.0, 0.0, -4.0));
        window.setFocusable(true);
        window.requestFocus();
        numOfRays.setValue(20);
        polyPointsAmount.setValue(5);
        typeOfRay.setSelectedIndex(1);

        window.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                double d = 0.1;
                if(e.getKeyChar() == 'w') {
                    camera.move(new Vector3(0.0, 0.0, d));
                }
                if(e.getKeyChar() == 'a') {
                    camera.move(new Vector3(d, 0.0, 0.0));
                }
                if(e.getKeyChar() == 's') {
                    camera.move(new Vector3(0.0, 0.0, -d));
                }
                if(e.getKeyChar() == 'd') {
                    camera.move(new Vector3(-d, 0.0, 0.0));
                }

                if(e.getKeyChar() == 'q') {
                    camera.move(new Vector3(0.0, +d, 0.0));
                }
                if(e.getKeyChar() == 'z') {
                    camera.move(new Vector3(0.0, -d, 0.0));
                }

            }
        });
        window.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Vector3 now = new Vector3((double) e.getX(), (double) e.getY(), 0.0);

                Vector3 newPos = new Vector3((double) -e.getY(), (double) e.getX(), 0.0);
                Vector3 oldPos = new Vector3(-oldMousePos.y, +oldMousePos.x, 0.0);


                camera.rotate(newPos.minus(oldPos).multiple(0.007));

                oldMousePos = now;
                update();
            }
        });

        window.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                window.requestFocus();
                oldMousePos = new Vector3((double) e.getX(), (double) e.getY(), 0.0);
                update();
            }
        });
        buildButtton.addActionListener(e -> {
            double r = Double.parseDouble(radiusField.getText());
            int numOfRaysInt = (int) numOfRays.getValue();
            double high = Double.parseDouble(highOfRay.getText());
            double size = Double.parseDouble(sizeOfRay.getText());
            int rayPolyPointsNum = (int) polyPointsAmount.getValue();

            RayType rayType = (RayType) typeOfRay.getSelectedItem();
            // если точек в многоугольнике луча достаточно много, то получится круг
                sphereWithRays = new SphereWithRays(r, numOfRaysInt, high, size, rayType, rayPolyPointsNum,
                        Color.RED, Color.BLUE);


        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Main");
        Main main = new Main();
        frame.setContentPane(main.window);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);


        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(main::update, 0, 10, TimeUnit.MILLISECONDS);
    }

    public void update(){
        canvas.setFocusable(true);

         SwingUtilities.updateComponentTreeUI(canvas);

         if(rotate.isSelected()) sphereWithRays.transform(Matrix4.rotateByVec(new Vector3(0.0, -0.01, 0.0)));
    }

    private void createUIComponents() {
        canvas = new DrawPanel();
        typeOfRay = new JComboBox<>(RayType.values());
    }

    class DrawPanel extends JPanel{

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            setFocusable(true);
            Graphics2D canvas = (Graphics2D) g;

            BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D imageGraphics = image.createGraphics();

            imageGraphics.setColor(Color.WHITE);
            imageGraphics.fillRect(0, 0, image.getWidth(), image.getHeight());

            List<IModel> models = new ArrayList<>();
            models.add(sphereWithRays);
            List<Triangle> triangles2d = camera.projected(models);

            for (Triangle triangle : triangles2d) {
                imageGraphics.setColor(triangle.color);
                Polygon polygon = new Polygon();
                polygon.addPoint(triangle.getPoint(0).x.intValue(),triangle.getPoint(0).y.intValue());
                polygon.addPoint(triangle.getPoint(1).x.intValue(),triangle.getPoint(1).y.intValue());
                polygon.addPoint(triangle.getPoint(2).x.intValue(),triangle.getPoint(2).y.intValue());
                imageGraphics.fillPolygon(polygon);


                imageGraphics.setColor(triangle.color);
                imageGraphics.setStroke(new BasicStroke(2));

                imageGraphics.drawLine(triangle.getPoint(0).x.intValue(),triangle.getPoint(0).y.intValue(),
                        triangle.getPoint(1).x.intValue(), triangle.getPoint(1).y.intValue());

                imageGraphics.drawLine(triangle.getPoint(1).x.intValue(), triangle.getPoint(1).y.intValue(),
                        triangle.getPoint(2).x.intValue(), triangle.getPoint(2).y.intValue());

                imageGraphics.drawLine(triangle.getPoint(2).x.intValue(), triangle.getPoint(2).y.intValue(),
                        triangle.getPoint(0).x.intValue(), triangle.getPoint(0).y.intValue());
            }


            canvas.drawImage(image, 0, 0, null);
            canvas.dispose();
        }
    }


}
