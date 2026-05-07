package gui;
/**
 * Типы состояний окна: нормальное, развёрнутое, свёрнутое.
 * Вынесен в отдельный класс для удобства переиспользования.
 */
public enum WindowStateType {
    NORMAL(0),      // обычный режим
    MAXIMIZED(1),   // во весь экран
    ICONIFIED(2);   // свёрнуто в значок

    public final int number;

    WindowStateType(int number) {
        this.number = number;
    }

    public static WindowStateType fromInteger(int number) {
        for (WindowStateType type : values()) {
            if (type.number == number) {
                return type;
            }
        }
        return NORMAL; // значение по умолчанию
    }
}
