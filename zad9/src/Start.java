import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

public class Start {
    JButton loadButton;
    JFrame frame;
    MyCanvas canvas;
    double scaleDef;

    SetSpace space = new SetSpace();

    List<Node> nodeList = new LinkedList<>();
    List<Edge> edgeList = new LinkedList<>();

    class MyCanvas extends JPanel {
        public void paintComponent(Graphics grap) {
            super.paintComponent(grap);

            double canvasProportion, graphProportion, xCoeff, yCoeff;
            Node nodee;

            Graphics2D grap2d = (Graphics2D) grap;

            canvasProportion = proportion(1);
            graphProportion = proportion(2);
            nodee = new Node(0, 0);

            if (graphProportion <= canvasProportion) {
                nodee.setX((int) ((canvas.getWidth() - (canvas.getHeight() * graphProportion)) * 0.5) + (int) ((double) canvas.getWidth() * 0.05));
                nodee.setY(setXorY(1));

                xCoeff = xAndYcoeffs1(graphProportion, 1);
                yCoeff = xAndYcoeffs2(1);
            } else {
                nodee.setX(setXorY(2));
                nodee.setY((int) ((canvas.getHeight() - (canvas.getWidth() / graphProportion)) * 0.5) + (int) ((double) canvas.getHeight() / 10.0 * 0.5));

                xCoeff = xAndYcoeffs2(2);
                yCoeff = xAndYcoeffs1(graphProportion, 2);
            }

            edgeAndNodeScales(nodee, grap2d, xCoeff, yCoeff);
        }
    }

    protected double proportion(int which) {
        int a, b;
        if (which == 1) {
            a = canvas.getWidth();
            b = canvas.getHeight();
        } else {
            a = space.getRight() - space.getLeft();
            b = space.getUp() - space.getDown();
        }

        return (double) a / (double) b;
    }

    protected void edgeAndNodeScales(Node nodee, Graphics2D grap2d, double xCoeff, double yCoeff) {
        float edgeScale = (float) (canvas.getHeight() * 0.002);
        forEachEdge(edgeScale, grap2d, nodee, xCoeff, yCoeff);

        float nodeScale = (float) (canvas.getHeight() * 0.04);
        forEachNode(nodeScale, grap2d, xCoeff, yCoeff, nodee);
    }

    protected int setXorY(int which) {
        int a;
        if (which == 1) {
            a = canvas.getHeight();
        } else {
            a = canvas.getWidth();
        }

        return (int) ((double) a * 0.05);
    }

    protected double xAndYcoeffs1(double graphProportion, int which) {
        int a;
        if (which == 1) {
            a = canvas.getHeight();
        } else {
            a = canvas.getWidth();
        }

        return (a / graphProportion) / (double) (space.getUp() + 1) * 0.9;
    }

    protected double xAndYcoeffs2(int which) {
        int a;
        if (which == 1) {
            a = canvas.getHeight();
        } else {
            a = canvas.getWidth();
        }

        return a / (double) (space.getRight() + 1) * 0.9;
    }

    protected void forEachEdge(float edgeScale, Graphics2D grap2d, Node nodee, double xCoeff, double yCoeff) {
        edgeList.forEach(edge -> {
            float edgeStroke = (float) (edgeScale * ((float) edge.getScale() / (float) scaleDef) / 0.1);
            grap2d.setColor(Color.BLACK);
            grap2d.setStroke(new BasicStroke(edgeStroke));
            grap2d.drawLine((int) (xCoeff * edge.startingNode.getX()) + nodee.getX(),
                    (int) (yCoeff * edge.startingNode.getY()) + nodee.getY(),
                    (int) (xCoeff * edge.endingNode.getX()) + nodee.getX(),
                    (int) (yCoeff * edge.endingNode.getY()) + nodee.getY());
        });
    }

    protected void forEachNode(float nodeScale, Graphics2D grap2d, double xCoeff, double yCoeff, Node nodee) {
        for (AtomicReference<Iterator<Node>> iterator = new AtomicReference<>(nodeList.iterator()); iterator.get().hasNext(); ) {
            Node node = iterator.get().next();
            grap2d.setColor(Color.BLACK);
            grap2d.setStroke(new BasicStroke((float) (nodeScale * 0.25)));

            Node draw = new Node((int) (xCoeff * node.getX()) + nodee.getX() - (int) (nodeScale * 0.5),
                    (int) (yCoeff * node.getY()) + nodee.getY() - (int) (nodeScale * 0.5));

            grap2d.drawOval(draw.getX(), draw.getY(), (int) nodeScale, (int) nodeScale);

            grap2d.setColor(Color.WHITE);

            draw = new Node((int) (xCoeff * node.getX()) + nodee.getX() - (int) (nodeScale * (float) (5.0 / 12.0)),
                    (int) (yCoeff * node.getY()) + nodee.getY() - (int) (nodeScale * (float) (5.0 / 12.0)));

            grap2d.fillOval(draw.getX(), draw.getY(), (int) (nodeScale * (float) (5.0 / 6.0)), (int) (nodeScale * (float) (5.0 / 6.0)));
        }
    }

    protected void makeLoadButton() {
        this.loadButton = new JButton();
        this.loadButton.setText("Load");
        this.loadButton.setFocusable(false);
        Border blackline = BorderFactory.createLineBorder(Color.black);
        this.loadButton.setBorder(blackline);
        this.loadButton.addActionListener(edge -> {
            if (edge.getSource() == loadButton) {
                AtomicReference<JFileChooser> jfc;
                jfc = new AtomicReference<>(new JFileChooser());
                File file = new File(System.getProperty("user.dir"));
                jfc.get().setCurrentDirectory(file);

                if (jfc.get().showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    file = new File(jfc.get().getSelectedFile().getAbsolutePath());
                    try {
                        readDataFromFile(file);
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
                    this.canvas.repaint();
                }
            }
        });
    }

    protected void readDataFromFile(File _file) throws FileNotFoundException {
        nodeList = new LinkedList<>();
        edgeList = new LinkedList<>();
        scaleDef = 1.0;

        Scanner scan = new Scanner(_file);
        scannerActions(scan);
        scan.close();
    }

    protected void scannerActions(Scanner scan) {
        int cond = scan.nextInt(), x = scan.nextInt(), y = scan.nextInt();

        Node _node = new Node(x, y);
        nodeList.add(_node);

        whilesSettersAndFors(_node, scan, cond);

        space.setUp(space.getUp() - 1);

        edgeList.stream().filter(edge -> scaleDef < edge.scale).forEach(edge -> scaleDef = edge.scale);
    }

    protected void whilesSettersAndFors(Node _node, Scanner scan, int cond) {
        spaceSetts(1, _node);

        whilesInRead(scan, cond);

        forEach(1);

        spaceSetts(2, _node);

        forEach(2);
    }

    protected void spaceSetts(int which, Node _node) {
        int a, b, c, d;
        if (which == 1) {
            a = c = _node.getX();
            b = d = _node.getY();
        } else {
            a = space.getRight() - 1 - space.getLeft();
            b = space.getUp() - space.getDown();
            c = d = 0;
        }
        space.setUp(b);
        space.setRight(a);
        space.setDown(d);
        space.setLeft(c);
    }

    protected void forEach(int which) {
        if (which == 1) {
            nodeList.forEach(node -> {
                node.setX(node.getX() - space.getLeft());
                node.setY(node.getY() - space.getDown());
            });
        } else {
            nodeList.forEach(node -> node.setY(node.getY() - (int) ((double) node.getY() * 2 - (double) (space.getUp() - space.getDown()) / 2 * 2)));
        }
    }

    protected void whilesInRead(Scanner scan, int cond) {
        int i = 1;
        while1(i, scan, cond);
        cond = scan.nextInt();
        i = 0;
        while2(i, scan, cond);
    }

    protected void while1(int i, Scanner scan, int cond) {
        while (true) {
            if (i < cond) {
                int x = scan.nextInt(), y = scan.nextInt();

                Node _node = new Node(x, y);
                nodeList.add(_node);

                if (_node.getX() > space.right) {
                    space.right = _node.getX();
                }
                Ifes(_node);
                i++;
            } else {
                break;
            }
        }
    }

    protected void Ifes(Node _node) {
        if (_node.getY() <= space.up) {
            if (_node.getX() < space.left) {
                space.left = _node.getX();
            }
            if (_node.getY() >= space.down) {
                return;
            }
            space.down = _node.getY();
        } else {
            space.up = _node.getY();
            if (_node.getX() < space.left) {
                space.left = _node.getX();
                if (_node.getY() < space.down) {
                    space.down = _node.getY();
                }
            } else {
                if (_node.getY() >= space.down) {
                    return;
                }
                space.down = _node.getY();
            }
        }
    }

    protected void while2(int i, Scanner scan, int cond) {
        while (true) {
            if (i < cond) {
                int x = scan.nextInt(), y = scan.nextInt(), scale = scan.nextInt();

                Edge _edge = new Edge(nodeList.get(x - 1), nodeList.get(y - 1), scale);
                edgeList.add(_edge);
                i++;
            } else {
                break;
            }
        }
    }

    protected void setCanvas() {
        this.canvas = new MyCanvas();
        this.canvas.setBorder(BorderFactory.createEtchedBorder());
    }

    protected void setFrame() {
        this.frame = new JFrame();
        this.frame.setSize(500, 500);
        this.frame.setTitle("Rysowanie grafow");
        this.frame.setLocationRelativeTo(null);
        BorderLayout layout = new BorderLayout();
        this.frame.setLayout(layout);
        this.frame.add(BorderLayout.CENTER, this.canvas);
        this.frame.add(BorderLayout.SOUTH, this.loadButton);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setVisible(true);
    }

    static class Node {
        protected int x, y;

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public Node(int _x, int _y) {
            x = _x;
            y = _y;
        }
    }

    static class Edge {
        protected Node startingNode, endingNode;
        protected int scale;

        protected int getScale() {
            return scale;
        }

        public Edge(Node _startingNode, Node _endingNode, int _scale) {
            startingNode = _startingNode;
            endingNode = _endingNode;
            scale = _scale;
        }
    }

    static class SetSpace {
        protected int up, right, down, left;

        public int getUp() {
            return up;
        }

        public void setUp(int up) {
            this.up = up;
        }

        public int getRight() {
            return right;
        }

        public void setRight(int right) {
            this.right = right;
        }

        public int getDown() {
            return down;
        }

        public void setDown(int down) {
            this.down = down;
        }

        public int getLeft() {
            return left;
        }

        public void setLeft(int left) {
            this.left = left;
        }
    }

    public static void main(String[] argv) {
        Start start = new Start();
        start.makeLoadButton();
        start.setCanvas();
        start.setFrame();
    }
}
