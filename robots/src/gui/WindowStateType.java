package gui;

import java.util.Optional;

/**
 * Типы состояний окна: нормальное, развёрнутое, свёрнутое.
 */
public enum WindowStateType {
    NORMAL(0),      // обычный режим
    MAXIMIZED(1),   // во весь экран
    ICONIFIED(2);   // свёрнуто в значок

    private final int num;

    WindowStateType(int num) {
        this.num = num;
    }

    public int getNum() {
        return num;
    }

    public static Optional<WindowStateType> fromNum(int num) {
        for (WindowStateType type : values()) {
            if (type.num == num) {
                return Optional.of(type);
            }
        }
        return Optional.empty(); // Возвращаем пустой Optional, если ничего не нашли
    }
}