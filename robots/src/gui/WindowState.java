package gui;

public class WindowState {
    private final int x, y, width, height, state;
    private final boolean closed;

    public WindowState(int x, int y, int width, int height, int state, boolean isClosed) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.state = state;
        this.closed = isClosed;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getState() { return state; }

    public WindowStateType getType() {
        return WindowStateType.fromNum(state);
    }

    public boolean isClosed() { return closed; }
}