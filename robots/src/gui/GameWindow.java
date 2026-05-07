package gui;
import java.awt.BorderLayout;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import model.RobotModel;

public class GameWindow extends JInternalFrame implements Saveable {
    private final GameVisualizer m_visualizer;
    private final RobotController m_controller;

    public GameWindow(RobotModel model) {
        super("Игровое поле", true, true, true, true);
        m_visualizer = new GameVisualizer(model);
        m_controller = new RobotController(model, m_visualizer);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }

    @Override
    public String getWindowName() {
        return "GameWindow";
    }

    @Override
    public WindowState getWindowState() {
        return WindowStateManager.createWindowState(this);
    }
}
