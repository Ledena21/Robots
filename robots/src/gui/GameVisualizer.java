package gui;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import javax.swing.JPanel;
import model.RobotModel;
/**
 * Компонент для отрисовки робота и цели.
 * Получает данные из модели RobotModel, не хранит состояние самостоятельно:
 * - Теперь не хранит координаты робота, а получает их из модели
 * - Подписывается на обновления модели через RobotModelListener
 * - Реализует паттерн MVC: View (отображение) отделена от Model (данные)
 */
public class GameVisualizer extends JPanel implements RobotModel.RobotModelListener {
    private RobotModel m_model;

    public GameVisualizer(RobotModel model) {
        m_model = model;
        m_model.addListener(this); // Подписываемся на обновления модели
        setDoubleBuffered(true);
    }

    protected void onRedrawEvent() {
        EventQueue.invokeLater(this::repaint);
    }

    private static int round(double value) {
        return (int)(value + 0.5);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D)g;
        // Данные для отрисовки получаем из модели
        drawRobot(g2d,
                round(m_model.getRobotPositionX()),
                round(m_model.getRobotPositionY()),
                m_model.getRobotDirection());
        drawTarget(g2d,
                m_model.getTargetPositionX(),
                m_model.getTargetPositionY());
    }

    @Override
    public void onRobotPositionChanged(double x, double y, double direction) {
        onRedrawEvent();
    }

    @Override
    public void onTargetPositionChanged(int x, int y) {
        onRedrawEvent();
    }

    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private void drawRobot(Graphics2D g, int x, int y, double direction) {
        int robotCenterX = round(m_model.getRobotPositionX());
        int robotCenterY = round(m_model.getRobotPositionY());
        AffineTransform t = AffineTransform.getRotateInstance(direction, robotCenterX, robotCenterY);
        g.setTransform(t);
        g.setColor(Color.MAGENTA);
        fillOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.WHITE);
        fillOval(g, robotCenterX + 10, robotCenterY, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX + 10, robotCenterY, 5, 5);
    }

    private void drawTarget(Graphics2D g, int x, int y) {
        AffineTransform t = AffineTransform.getRotateInstance(0, 0, 0);
        g.setTransform(t);
        g.setColor(Color.GREEN);
        fillOval(g, x, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 5, 5);
    }
}