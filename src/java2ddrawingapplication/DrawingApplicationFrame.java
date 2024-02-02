package java2ddrawingapplication;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class DrawingApplicationFrame extends JFrame {

    private final JPanel shapesPanel = new JPanel();
    private final JPanel optionsPanel = new JPanel();
    private final JPanel topPanel = new JPanel();

    String[] shapeNames = new String[]{"Line", "Oval", "Rectangle"};
    private final JComboBox<String> shapeComboBox = new JComboBox<>(shapeNames);

    private final JButton primaryColorButton = new JButton("1st Color");
    private final JButton secondaryColorButton = new JButton("2nd Color");
    private final JButton undoButton = new JButton("Undo");
    private final JButton clearButton = new JButton("Clear");

    private final JCheckBox filledCheckBox = new JCheckBox("Filled");
    private final JCheckBox gradientCheckBox = new JCheckBox("Use Gradient");
    private final JCheckBox dashCheckBox = new JCheckBox("Dashed");
    private final JSpinner lineWidthSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
    private final JSpinner dashLengthSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
    private final JLabel strokeWidthLabel = new JLabel("Line Width:");
    private final JLabel dashLengthLabel = new JLabel("Dash Length:");

    private DrawPanel drawPanel = new DrawPanel();
    private List<MyShapes> shapesList = new ArrayList<>();
    private MyShapes currentShape;
    private Point startPoint;
    private Point endPoint;
    private Paint paint = Color.BLACK;
    private Color color1 = Color.BLACK;
    private Color color2 = Color.CYAN;
    private Stroke stroke;
    private JLabel statusLabel;
    private JLabel shapesLabel = new JLabel("Shapes:");
    private JLabel optionsLabel = new JLabel("Options:");

    public DrawingApplicationFrame() {
        setTitle("Drawing Application");
        setLayout(new BorderLayout());

        shapesPanel.add(shapesLabel);
        shapesPanel.add(shapeComboBox);
        shapesPanel.add(primaryColorButton);
        shapesPanel.add(secondaryColorButton);
        shapesPanel.add(undoButton);
        shapesPanel.add(clearButton);
        shapesPanel.setBackground(Color.CYAN);

        optionsPanel.add(optionsLabel);
        optionsPanel.add(filledCheckBox);
        optionsPanel.add(gradientCheckBox);
        optionsPanel.add(dashCheckBox);
        optionsPanel.add(strokeWidthLabel);
        optionsPanel.add(lineWidthSpinner);
        optionsPanel.add(dashLengthLabel);
        optionsPanel.add(dashLengthSpinner);
        optionsPanel.setBackground(Color.CYAN);

        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.add(shapesPanel);
        topPanel.add(optionsPanel);
        add(topPanel, BorderLayout.NORTH);
        add(drawPanel, BorderLayout.CENTER);

        statusLabel = new JLabel("Mouse: (0, 0)");
        add(statusLabel, BorderLayout.SOUTH);

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                shapesList.clear();
                repaint();
            }
        });

        undoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                shapesList.remove(shapesList.size() - 1);
                repaint();
            }
        });

        primaryColorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color selectedColor = JColorChooser.showDialog(DrawingApplicationFrame.this, "Choose a Color", Color.BLACK);

                if (selectedColor == null) {
                    selectedColor = Color.BLACK;
                }
                color1 = selectedColor;
            }
        });

        secondaryColorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color selectedColor = JColorChooser.showDialog(DrawingApplicationFrame.this, "Choose a Color", Color.BLACK);

                if (selectedColor == null) {
                    selectedColor = Color.BLACK;
                }
                color2 = selectedColor;
            }
        });
    }

    private class DrawPanel extends JPanel {
        public DrawPanel() {
            setBackground(Color.WHITE);
            stroke = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            addMouseListener(new MouseHandler());
            addMouseMotionListener(new MouseHandler());
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            for (MyShapes shape : shapesList) {
                shape.draw(g2d);
            }
        }
    }

    private class MouseHandler extends MouseAdapter implements MouseMotionListener {
        public void mousePressed(MouseEvent event) {
            startPoint = event.getPoint();
            if (gradientCheckBox.isSelected()) {
                paint = new GradientPaint(0, 0, color1, 50, 50, color2, true);
            } else {
                paint = color1;
            }
            int lineWidth = (Integer) lineWidthSpinner.getValue();
            float[] dashLength = {(Integer) dashLengthSpinner.getValue()};
            if (dashCheckBox.isSelected()) {
                stroke = new BasicStroke(lineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, dashLength, 0);
            } else {
                stroke = new BasicStroke(lineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            }
            String selectedShape = (String) shapeComboBox.getSelectedItem();
            switch (selectedShape) {
                case "Line":
                    currentShape = new MyLine(startPoint, startPoint, paint, stroke);
                    break;
                case "Oval":
                    currentShape = new MyOval(startPoint, startPoint, paint, stroke, filledCheckBox.isSelected());
                    break;
                case "Rectangle":
                    currentShape = new MyRectangle(startPoint, startPoint, paint, stroke, filledCheckBox.isSelected());
                    break;
                default:
                    break;
            }
            shapesList.add(currentShape);
        }

        public void mouseReleased(MouseEvent event) {
            endPoint = event.getPoint();
            shapesList.get(shapesList.size() - 1).setEndPoint(endPoint);
            repaint();
        }

        @Override
        public void mouseDragged(MouseEvent event) {
            endPoint = event.getPoint();
            shapesList.get(shapesList.size() - 1).setEndPoint(endPoint);
            repaint();
        }

        @Override
        public void mouseMoved(MouseEvent event) {
            statusLabel.setText("Coordinates: (" + event.getX() + ", " + event.getY() + ")");
        }
    }
}
