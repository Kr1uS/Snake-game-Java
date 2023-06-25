import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Graphics extends JFrame implements GraphicListener {

    private JTable table;
    private ScorePanel scorePanel;
    private TablePanel tablePanel;
    private Listener listener;
    private InputListener inputListener;

    public Graphics() {
        this.setSize( 720, 550);

        table = new JTable();
        table.setRowHeight(30);

        table.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                InputEvent inputEvent = new InputEvent(this);
                inputEvent.setKey(e.getKeyChar());
                inputListener.inputKey(inputEvent);
            }
        });

    }

    public void initialise(Listener listener) {
        this.listener = listener;
        this.inputListener = listener.getInputListener();
        tablePanel = new TablePanel(listener.getInputListener());
        table.setModel(listener.getTableData());
        table.setDefaultRenderer(int.class, tablePanel);
        scorePanel = new ScorePanel(listener, this);
    }

    public void openWindow() {
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        this.add(scorePanel);
        this.add(table);

        this.setVisible(true);
        table.requestFocusInWindow();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    @Override
    public void updateScore() {
        scorePanel.update();
    }

    @Override
    public void gameOver() {
        scorePanel.gameOver();
    }

    @Override
    public void bestScore(Map<String, Integer> info, String player) {
        scorePanel.bestScore(info, player);
    }

    @Override
    public int getScore() {
        return scorePanel.getScore();
    }
}

class TablePanel extends JPanel implements TableCellRenderer {
    private InputListener inputListener;

    public TablePanel(InputListener inputListener) {
        setOpaque(true);
        this.inputListener = inputListener;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        removeAll();
        if ( (int)value == -1 ) {
            ApplePanel applePanel = new ApplePanel();
            this.add(applePanel);
        } else if ( (int)value > 1 ) {
            this.setBackground(new Color(96, 231, 13));
        } else if ( (int)value == 1 ){
            SnakeHeadPanel snakeHeadPanel = new SnakeHeadPanel(inputListener.getDirection());
            this.add(snakeHeadPanel);
            this.setBackground(new Color(30, 152, 3));
        } else {
            this.setBackground(Color.BLACK);
        }
        return this;
    }


}

class ScorePanel extends JPanel {

    private int score;
    private String string;
    private Listener listener;
    private Graphics graphics;

    public ScorePanel(Listener listener, Graphics graphics) {
        this.listener = listener;
        this.graphics = graphics;
        score = 0;
        string = "Score: " + score;
    }

    public void update() {
        score = listener.getSnake().getLength() * 100;
        string = "Score: " + score;
        repaint();
    }

    public int getScore() {
        return score;
    }

    @Override
    protected void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);

        g.setFont(new Font("Arial", Font.BOLD, 23));
        g.setColor(Color.BLACK);

        g.drawString(string, 10, 30);
    }

    public void gameOver() {
        string += " GAME OVER! ";
        repaint();
    }

    public void bestScore(Map<String, Integer> info, String player) {
        Map.Entry<String, Integer> firstEntry = info.entrySet().iterator().next();

        string += " Best Score: " + firstEntry.getValue();
        if (firstEntry.getValue() == score)
            string += " NEW RECORD!";
        repaint();

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                JDialog d = new JDialog(graphics, "Top 10 Players");
                d.setLayout(new BoxLayout(d.getContentPane(), BoxLayout.Y_AXIS));

                int count = 1;
                for (Map.Entry<String, Integer> me : info.entrySet()) {
                    JTextField textField = new JTextField("# " + count + "\t"
                            + me.getKey() + "\t" + me.getValue() + "\n");
                    textField.setEditable(false);
                    if (me.getValue() == score && me.getKey() == player)
                        textField.setBackground(Color.lightGray);
                    d.add(textField);
                    count++;
                }

                d.setSize(250, 300);
                d.setVisible(true);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                ScorePanel.super.setBackground(new Color(225, 225, 225));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                ScorePanel.super.setBackground(Color.WHITE);
            }
        });
    }
}

class ApplePanel extends JPanel {

    public ApplePanel() {
        this.setBackground(Color.BLACK);
    }


    @Override
    protected void paintComponent(java.awt.Graphics g) {
        int width = getWidth();
        int height = getHeight();

        int pixelSize = Math.min(width / 12, height / 12);

        g.setColor(Color.RED);
        g.fillRect(3 * pixelSize + 4, 7 * pixelSize - 6, 6 * pixelSize, 6 * pixelSize);

        g.setColor(Color.GREEN);
        g.fillRect(6 * pixelSize + 4, 3 * pixelSize - 6, 2 * pixelSize, 4 * pixelSize);

        g.setColor(Color.GREEN.darker());
        g.fillRect(2 * pixelSize + 4, 2 * pixelSize - 6, 4 * pixelSize, 2 * pixelSize);
        g.fillRect(3 * pixelSize + 4, pixelSize - 6, 2 * pixelSize, 2 * pixelSize);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(30, 30); // Set the preferred size of the panel to 30x30
    }
}

class SnakeHeadPanel extends JPanel {
    private char direction;

    public SnakeHeadPanel(char direction) {
        this.direction = direction;
        this.setBackground(new Color(30, 152, 3));
    }

    @Override
    protected void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.BLACK);
        int width = getWidth();
        int height = getHeight();

        switch (direction) {
            case 'w':
                int circleY = height / 2 - 5;
                int circleX1 = width / 3 - 5;
                int circleX2 = 2 * width / 3 - 5;
                g.fillOval(circleX1 + 2, circleY - 9, 5, 5);
                g.fillOval(circleX2 + 2, circleY - 9 , 5, 5);
                break;
            case 's':
                circleY = height / 2 - 5;
                circleX1 = width / 3 - 5;
                circleX2 = 2 * width / 3 - 5;
                g.fillOval(circleX1 + 2, circleY - 5, 5, 5);
                g.fillOval(circleX2 + 2, circleY - 5 , 5, 5);
                break;
            case 'a':
                int circleX = width / 2 - 5;
                int circleY1 = height / 3 - 5;
                int circleY2 = 2 * height / 3 - 5;
                g.fillOval(circleX + 2, circleY1 - 7, 5, 5);
                g.fillOval(circleX + 2, circleY2 - 7 , 5, 5);
                break;
            case 'd':
                circleX = width / 2 - 5;
                circleY1 = height / 3 - 5;
                circleY2 = 2 * height / 3 - 5;
                g.fillOval(circleX + 5, circleY1 - 7, 5, 5);
                g.fillOval(circleX + 5, circleY2 - 7 , 5, 5);
                break;
        }

    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(30, 40); // Set the preferred size of the panel to 30x30
    }

}