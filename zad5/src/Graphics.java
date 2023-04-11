import java.util.ArrayList;
import java.util.Objects;

public class Graphics implements GraphicsInterface{
    static class Position2D implements Position {

        private final int col;
        private final int row;

        public Position2D( int col, int row ) {
            this.col = col;
            this.row = row;
        }

        @Override
        public int getRow() {
            return row;
        }

        @Override
        public int getCol() {
            return col;
        }

        @Override
        public String toString() {
            return "Position2D [col=" + col + ", row=" + row + "]";
        }

        @Override
        public int hashCode() {
            return Objects.hash(col, row);
        }

        @Override
        public boolean equals( Object obj ) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Position2D other = (Position2D) obj;
            return col == other.col && row == other.row;
        }
    }

    CanvasInterface setedCanvas = null;
    ArrayList<Position> visited = null;

    @Override
    public void setCanvas(CanvasInterface canvas) {
        setedCanvas = canvas;
    }

    protected void tryCanvas(CanvasInterface canvas) throws NoCanvasException {
        if(canvas == null){
            throw new NoCanvasException();
        }
    }

    protected void fillStart(Position startingPosition, Color color) throws WrongStartingPosition {
        if(!tryToFillPosition(startingPosition, color)){
            throw new WrongStartingPosition();
        }
    }

    protected boolean tryToFillPosition(Position pos, Color color) {
        boolean possible = false;
        try {
            setedCanvas.setColor(pos, color);
            possible = true;
        } catch (CanvasInterface.CanvasBorderException ignored) {
        } catch (CanvasInterface.BorderColorException colorException) {
            try {
                setedCanvas.setColor(pos, colorException.previousColor);
            } catch (CanvasInterface.BorderColorException | CanvasInterface.CanvasBorderException ignored) {
            }
        }
        return possible;
    }

    protected void colorNeighbours(int col, int row, Color color){
        Position up = new Position2D(col, row + 1);
        Position down = new Position2D(col, row - 1);
        Position right = new Position2D(col + 1, row);
        Position left = new Position2D(col - 1, row);

        insideColorNeighbours(color, up, down, right, left);
    }

    private void insideColorNeighbours(Color color, Position y1, Position y2, Position x1, Position x2) {
        upDownAndRightLeft(color, y1, y2);
        upDownAndRightLeft(color, x1, x2);
    }

    private void upDownAndRightLeft(Color color, Position y1, Position y2) {
        if (!visited.contains(y1)){
            visited.add(y1);
            if(tryToFillPosition(y1, color)){
                colorNeighbours(y1.getCol(), y1.getRow(), color);
            }
        }
        if (!visited.contains(y2)) {
            visited.add(y2);
            if(tryToFillPosition(y2, color)){
                colorNeighbours(y2.getCol(), y2.getRow(), color);
            }
        }
    }

    @Override
    public void fillWithColor(Position startingPosition, Color color) throws GraphicsInterface.WrongStartingPosition, GraphicsInterface.NoCanvasException {
        visited = new ArrayList<>();
        tryCanvas(setedCanvas);
        visited.add(startingPosition);
        fillStart(startingPosition, color);
        colorNeighbours(startingPosition.getCol(), startingPosition.getRow(), color);
    }
}