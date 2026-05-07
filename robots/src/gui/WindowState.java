package gui;

public record WindowState(int x, int y, int width, int height, int state, boolean closed) {

    public WindowStateType getType() {
        return WindowStateType.fromInteger(state);
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getState() { return state; }
    public boolean isClosed() { return closed; }
}
