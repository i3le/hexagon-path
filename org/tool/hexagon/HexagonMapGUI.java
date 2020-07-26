package org.tool.hexagon;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.util.List;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * 可视化操作窗口
 */
public class HexagonMapGUI {

    // 根据分类定义速度（值越小耗时越少，值越大耗时越多）
    private static final int SPEED_BLOCK    = 0;
    private static final int SPEED_FAST     = 5;
    private static final int SPEED_NORMAL   = 10;
    private static final int SPEED_SLOW     = 15;

    private static final Color COLOR_G2D = new Color(0, 0, 0);

    private static final Color COLOR_OPEN = new Color(233, 157, 101);
    private static final Color COLOR_CLOSE = new Color(123, 70, 188);
    private static final Color COLOR_PATH = new Color(0, 0, 255);
    private static final Color COLOR_POINT = new Color(255, 0, 0);

    private static final Color COLOR_BLOCK = new Color(0, 0, 0);
    private static final Color COLOR_FAST = new Color(222, 255, 222);
    private static final Color COLOR_SLOW = new Color(255, 255, 0);
    private static final Color COLOR_NORMAL = new Color(0, 255, 0);

    private Hexagon[][] map = new Hexagon[64][40];
    private Random random = new Random();

    private JFrame frame;
    private JLabel lblMouse;
    private TileCanvas canvas;
    private JTextField txtX1, txtY1, txtX2, txtY2;

    // 当前操作的点（起点或终点）
    private JTextField setX, setY;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                HexagonMapGUI window = new HexagonMapGUI();
                window.frame.setLocationRelativeTo(null);
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public HexagonMapGUI() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(10, 10, 1260, 930);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        frame.setResizable(false);

        int x = 100, y = 5;
        JLabel lblX1 = new JLabel("起点");
        lblX1.setBounds(x, y, 30, 25);
        frame.getContentPane().add(lblX1);
        txtX1 = new JTextField("0");
        x += 35;
        txtX1.setBounds(x, y, 50, 25);
        frame.getContentPane().add(txtX1);
        x += 50;
        JLabel lblP1 = new JLabel("，");
        lblP1.setBounds(x, y, 20, 25);
        frame.getContentPane().add(lblP1);
        x += 20;
        txtY1 = new JTextField("0");
        txtY1.setBounds(x, y, 50, 25);
        frame.getContentPane().add(txtY1);

        x += 80;
        JLabel lblX2 = new JLabel("终点");
        lblX2.setBounds(x, y, 30, 25);
        frame.getContentPane().add(lblX2);
        txtX2 = new JTextField(String.valueOf(map.length - 1));
        x += 35;
        txtX2.setBounds(x, y, 50, 25);
        frame.getContentPane().add(txtX2);
        x += 50;
        JLabel lblP2 = new JLabel("，");
        lblP2.setBounds(x, y, 20, 25);
        frame.getContentPane().add(lblP2);
        x += 20;
        txtY2 = new JTextField(String.valueOf(map[0].length - 1));
        txtY2.setBounds(x, y, 50, 25);
        frame.getContentPane().add(txtY2);

        Runnable onX1 = () -> {
            lblX1.setForeground(Color.RED);
            lblX2.setForeground(Color.BLACK);
            setX = txtX1;
            setY = txtY1;
        };
        lblX1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onX1.run();
            }
        });
        Runnable onX2 = () -> {
            lblX2.setForeground(Color.RED);
            lblX1.setForeground(Color.BLACK);
            setX = txtX2;
            setY = txtY2;
        };
        lblX2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onX2.run();
            }
        });
        onX2.run();

        canvas = new TileCanvas();
        canvas.setBounds(45, 35, 1160, 847);
        canvas.setBackground(new Color(244, 244, 244));
        frame.getContentPane().add(canvas);

        x += 80;
        JButton btnNear = new JButton("附近的点");
        btnNear.addActionListener(e -> {
            canvas.path = null;
            canvas.repaint();
        });
        btnNear.setBounds(x, y, 100, 25);
        frame.getContentPane().add(btnNear);

        x += 110;
        JButton btnFind = new JButton("开始寻路");
        btnFind.addActionListener(e -> {
            canvas.findPath();
            canvas.repaint();
        });
        btnFind.setBounds(x, y, 100, 25);
        frame.getContentPane().add(btnFind);

        x += 110;
        JButton btnRandom = new JButton("随机路点");
        btnRandom.addActionListener(e ->{
            setX.setText(String.valueOf(random.nextInt(map.length)));
            setY.setText(String.valueOf(random.nextInt(map[0].length)));
            canvas.findPath();
            canvas.repaint();
        });
        btnRandom.setBounds(x, y, 100, 25);
        frame.getContentPane().add(btnRandom);

        x += 110;
        JButton btnRefresh = new JButton("刷新地图");
        btnRefresh.addActionListener(e ->{
            canvas.buildMap();
            canvas.repaint();
        });
        btnRefresh.setBounds(x, y, 100, 25);
        frame.getContentPane().add(btnRefresh);

        x += 150;
        lblMouse = new JLabel("");
        lblMouse.setBounds(x, y, 150, 25);
        frame.getContentPane().add(lblMouse);

        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                onMouseEvent(e);
            }
        });
        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                onMouseEvent(e);
            }
            @Override
            public void mouseMoved(MouseEvent e) {
            	Hexagon node = onMouseNode(e);
				if (canvas.mouse != node) {
					canvas.mouse = node;
					canvas.repaint();
				}
            }
        });
    }

	private Hexagon onMouseNode(MouseEvent e) {
		Hexagon node = canvas.getNode(e.getX(), e.getY());
		if (node != null) {
			lblMouse.setText(String.format("(%s, %s) (%s, %s)", e.getX(), e.getY(), node.x, node.y));
		}
		return node;
	}

    private void onMouseEvent(MouseEvent e) {
        Hexagon node = onMouseNode(e);;
        if (node != null) {
            setX.setText(String.valueOf(node.x));
            setY.setText(String.valueOf(node.y));
        }
        canvas.findPath();
        canvas.repaint();
    }

    public class TileCanvas extends JPanel {
		private static final long serialVersionUID = 8127261336405853104L;
		
		double outR = 12;
        double innerR = outR / 2 * Math.sqrt(3);

        HexagonPath hPath = new HexagonPath(map);
        public TileCanvas() {
            buildMap();
        }

        void buildMap() {
            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[i].length; j++) {
                    Hexagon node = new Hexagon(i, j);
                    double x = outR + node.x * (outR * 1.5);
                    double y = outR + node.y * (innerR * 2);
                    if (node.x % 2 == 1) {
                        y += innerR;
                    }
                    double x1 = x + (outR / 2), y1 = y + innerR;
                    double x2 = x + outR, y2 = y;
                    double x3 = x + (outR / 2), y3 = y - innerR;
                    double x4 = x - (outR / 2), y4 = y - innerR;
                    double x5 = x - outR, y5 = y;
                    double x6 = x - (outR / 2), y6 = y + innerR;

                    GeneralPath gp = new GeneralPath();
                    gp.append(new Line2D.Double(x1, y1, x2, y2), true);
                    gp.lineTo(x3, y3);
                    gp.lineTo(x4, y4);
                    gp.lineTo(x5, y5);
                    gp.lineTo(x6, y6);
                    gp.closePath();
                    node.gp = gp;

                    node.speed = SPEED_NORMAL;
                    map[i][j] = node;
                }
            }

            // 障碍点
            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[i].length; j++) {
                    Hexagon node = map[i][j];
                    if (random.nextFloat() < 0.2) {
                        node.speed = SPEED_BLOCK;
                        node.isBlock = true;
                    }
                }
            }

            // 慢行点
            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[i].length; j++) {
                    Hexagon node = map[i][j];
                    if (random.nextFloat() < 0.4) {
                        node.speed = SPEED_SLOW;
                        node.isBlock = false;
                    }
                }
            }
            int sizeY = map[0].length;
            int y1 = random.nextInt(sizeY);
            int y2 = random.nextInt(sizeY);
            int y3 = random.nextInt(sizeY);
            // 快进点
            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[i].length; j++) {
                    Hexagon node = map[i][j];
                    if (node.y == y1 || node.x == y1 || node.y == y2 || node.x == y2 || node.y == y3) {
                        node.speed = SPEED_FAST;
                        node.isBlock = false;
                    }
                }
            }
            findPath();
        }

        Hexagon begin, end, mouse;
        List<Hexagon> path;
        public void findPath() {
            begin = hPath.find(Integer.valueOf(txtX1.getText()), Integer.valueOf(txtY1.getText()));
            end = hPath.find(Integer.valueOf(txtX2.getText()), Integer.valueOf(txtY2.getText()));
            long startTime = System.currentTimeMillis();
            path = hPath.findPath(begin, end);
            System.out.println("find path use " + (System.currentTimeMillis() - startTime) + "ms");
            if (path == null) {
                System.out.println("无法通过！");
            }
        }

        public void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(COLOR_G2D);
            g2d.clearRect(0, 0, this.getWidth(), this.getHeight());

            int maxx = Math.min (map.length, (int) (this.getWidth() / (outR * 1.5)));
            int maxy =  Math.min (map[0].length, (int) (this.getHeight() / (innerR * 2)));
            // 画地图
            for (int i = 0; i < maxx; i++) {
                for (int j = 0; j < maxy; j++) {
                    Hexagon node = map[i][j];
                    switch (node.speed) {
                        case SPEED_BLOCK:
                            drawNode(g2d, node, COLOR_BLOCK);
                            continue;
                        case SPEED_FAST:
                            drawNode(g2d, node, COLOR_FAST);
                            continue;
                        case SPEED_SLOW:
                            drawNode(g2d, node, COLOR_SLOW);
                            continue;
                        case SPEED_NORMAL:
                            if(node.isBlock) {
                                drawNode(g2d, node, COLOR_BLOCK);
                            } else {
                                drawNode(g2d, node, COLOR_NORMAL);
                            }
                            continue;
                    }
                }
            }

            if (path != null) {
                for (Hexagon node : hPath.openList) {
                    drawNode(g2d, node, COLOR_OPEN);
                }
                for (Hexagon node : hPath.closeList) {
                    drawNode(g2d, node, COLOR_CLOSE);
                }
                for (Hexagon node : path) {
                    drawNode(g2d, node, COLOR_PATH);
                }
                System.out.println("路径画好啦~");
            } else {
                drawNear(g2d, begin);
                drawNear(g2d, end);
            }

            drawNode(g2d, begin, COLOR_POINT);
            drawNode(g2d, end, COLOR_POINT);

			if (mouse != null) {
				drawNode(g2d, mouse, COLOR_POINT);
			}
        }

        void drawNear(Graphics2D g2d, Hexagon node) {
            drawNode(g2d, node, new Color(255, 81, 185));
            List<Hexagon> nears = node.findNear();
            for (Hexagon near : nears) {
                drawNode(g2d, hPath.find(near), new Color(206, 89, 255));
            }
        }

        void drawNode(Graphics2D g2d, Hexagon node, Color color) {
            if(node == null || node.x > this.getWidth() / (outR * 1.5) || node.y > this.getHeight() / (innerR * 2)) {
                return;
            }
            Color temp = g2d.getColor();
            g2d.setColor(color);
            g2d.fill(node.gp);
            g2d.setColor(temp);
            g2d.draw(node.gp);
        }

        Hexagon getNode(double x, double y) {
            int tx = (int) ((x) / ((outR * 1.5)));
            int ty = (int) ((y) / ((innerR * 2)));
            if (tx < 0 || ty < 0 || tx > map.length || ty > map[0].length) {
                return null;
            }
            int x1 = Math.max(0, tx - 1);
            int x2 = Math.min(map.length, tx + 1);
            int y1 = Math.max(0, ty - 1);
            int y2 = Math.min(map[0].length, ty + 1);
            for (int i = x1; i < x2; i++) {
                for (int j = y1; j < y2; j++) {
                    if (map[i][j].gp.contains(x, y)) {
                        return map[i][j];
                    }
                }
            }
            return map[Math.min(tx, map.length - 1)][Math.min(ty, map[0].length - 1)];
        }

    }

}