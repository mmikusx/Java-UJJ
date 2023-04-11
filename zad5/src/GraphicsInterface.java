public interface GraphicsInterface {
    public class NoCanvasException extends Exception {
        private static final long serialVersionUID = 8263666547167500356L;
    }
    public class WrongStartingPosition extends Exception {
        private static final long serialVersionUID = -8582620817646059440L;
    }
    public void setCanvas(CanvasInterface canvas);
    public void fillWithColor(Position startingPosition, Color color) throws WrongStartingPosition, NoCanvasException;
}