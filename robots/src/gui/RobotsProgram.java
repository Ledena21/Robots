package gui;
import java.awt.Frame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import model.RobotModel;
public class RobotsProgram
{
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"); // кроссплатформинность (одинаковый вид на разных ос)
        } catch (Exception e) {
            e.printStackTrace();
        }
        // заменяет текст на кнопках на русский язык.
        UIManager.put("OptionPane.yesButtonText", "Да");
        UIManager.put("OptionPane.noButtonText", "Нет");
        UIManager.put("OptionPane.cancelButtonText", "Отмена");
        UIManager.put("OptionPane.okButtonText", "OK");

        SwingUtilities.invokeLater(() -> {
            RobotModel model = new RobotModel(); // создаём модель здесь
            MainApplicationFrame frame = new MainApplicationFrame(model); // передаём её в главное окно
            frame.pack();
            frame.setVisible(true);
            frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        });
    }
}
