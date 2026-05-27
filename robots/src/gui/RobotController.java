package gui;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;
import model.RobotModel;

public class RobotController {
    private final RobotModel model;
    private final Timer timer;
    private static final int UPDATE_PERIOD_MS = 10;

    public RobotController(RobotModel model, Component view) {
        this.model = model;
        view.addMouseListener(new MouseAdapter() { // слушаем мышь, передает модели новое положение цели
            @Override
            public void mouseClicked(MouseEvent e) {
                model.setTargetPosition(e.getPoint().x, e.getPoint().y);
            }
        });

        timer = new Timer("Model update timer", true); // планировщик задач
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                model.updatePosition(UPDATE_PERIOD_MS);
            }
        }, 0, UPDATE_PERIOD_MS);
    }
}