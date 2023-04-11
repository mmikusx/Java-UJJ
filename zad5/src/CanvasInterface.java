public interface CanvasInterface {
    public class CanvasBorderException extends Exception {
        private static final long serialVersionUID = 4759606029757073905L;
    }
    public class BorderColorException extends Exception {
        private static final long serialVersionUID = -4752159948902473254L;
        public final Color previousColor;

        public BorderColorException(Color color) {
            previousColor = color;
        }
    }
    public void setColor(Position position, Color color) throws CanvasBorderException, BorderColorException;
}