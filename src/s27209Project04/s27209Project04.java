package s27209Project04;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.*;
import java.util.List;

public class s27209Project04 {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(s27209Project04::new);
    }

    //---MAIN---//
    public s27209Project04() {

        Logic logic = new Logic();

        Graphics graphics = new Graphics();

        graphics.initialise(logic);
        logic.initialise(graphics);

        graphics.openWindow();

        logic.start();

    }
}

//---LOGIC---//
class Logic extends Thread implements Listener {

    private int[][] tableInt;
    private Snake snake;
    TableData tableData;
    private final int gameTick = 200;
    private GraphicListener graphicListener;

    public Logic() {
        tableInt = new int[16][25];
        snake = new Snake(this);
        tableData = new TableData(this);
        update();
    }

    @Override
    public void run() {
        snake.start();
        tableData.placeApple();
        try {
            this.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        while (true) {

            printTableInt();
            update();

            try {
                this.sleep(gameTick);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void update() {
        if (!checkCollision(snake.checkCollision())) {
            if (checkApple(snake.checkApple())) {
                tableData.placeApple();
                snake.toGrow = true;
                graphicListener.updateScore();
            }
            for (int row = 0; row < tableInt.length; row++)
                for (int col = 0; col < tableInt[row].length; col++)
                    if (tableInt[row][col] > 0) tableInt[row][col] = 0;
            for (int i = 0; i < snake.getLength(); i++) {
                try {
                    if (tableInt[snake.getBody()[i][0] - 1][snake.getBody()[i][1] - 1] != -1){
                        tableInt[snake.getBody()[i][0] - 1][snake.getBody()[i][1] - 1] = i + 1;
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("e");
                    endGame();
                }
            }
            tableData.updateTable();
        } else {
            endGame();
        }
    }

    public void initialise(GraphicListener graphicListener) {
        this.graphicListener = graphicListener;
    }

    private void printTableInt() {
        for (int row = 0; row < tableInt.length; row++) {
            for (int col = 0; col < tableInt[row].length; col++) {
                System.out.print("\t" + tableInt[row][col]);
            }
            System.out.print('\n');
        } System.out.print('\n');
    }

    private void endGame() {
        System.out.println("GAME OVER");
        graphicListener.gameOver();
        FileManager fileManager = new FileManager(graphicListener.getScore());
        graphicListener.bestScore(fileManager.getBestScore());
        this.stop();
        snake.stop();
    }

    @Override
    public int[][] getTableInt() {
        return tableInt;
    }

    @Override
    public void setTableInt(int[][] tableInt) {
        this.tableInt = tableInt;
    }

    @Override
    public Snake getSnake() {
        return snake;
    }

    @Override
    public int getGameTick() {
        return gameTick;
    }

    @Override
    public boolean checkCollision(Event e) {
        return e.getBool();
    }

    @Override
    public boolean checkApple(Event e) {
        return e.getBool();
    }

    @Override
    public AbstractTableModel getTableData() {
        return tableData;
    }

    @Override
    public InputListener getInputListener() {
        return snake;
    }
}
class Snake extends Thread implements InputListener {
    private int[][] body;
    private int length;
    private char direction;
    private int gameTick;
    public boolean toGrow;
    private Listener listener;

    public Snake(Listener listener) {
        this.listener = listener;
        this.gameTick = listener.getGameTick();
        body = new int[25*16][2];
        body[0][0] = (int) (5 + Math.random()* 11);
        body[0][1] = (int) (5 + Math.random()* 20);
        switch ( (int) (Math.random()* 4) ) {
            case 1 -> direction = 'w';
            case 2 -> direction = 'a';
            case 3 -> direction = 's';
            case 4 -> direction = 'd';
        }
    }

    public Event checkCollision() {
        boolean bool = false;
        Event event = new Event(this);

        int headX = body[0][0];
        int headY = body[0][1];

        switch (direction) {
            case 'w' -> headX--;
            case 'a' -> headY--;
            case 's' -> headX++;
            case 'd' -> headY++;
        }

        if (headX > 17 || headX < 0) {
            bool = true;
        }
        if (headY > 26 || headY < 0) {
            bool = true;
        }

        for (int i = 1; i < length; i++) {
            if (body[i][0] == body[0][0] && body[i][1] == body[0][1]) {
                bool = true;
            }
        }

        event.setBool(bool);
        return event;
    }

    public Event checkApple() {
        boolean bool = false;
        Event event = new Event(this);

        int headX = body[0][0];
        int headY = body[0][1];

        switch (direction) {
            case 'w' -> headX--;
            case 'a' -> headY--;
            case 's' -> headX++;
            case 'd' -> headY++;
        }

        if (headX-1 >= 0 && headX-1 < listener.getTableInt().length && headY-1 >= 0 && headY-1 < listener.getTableInt()[headX-1].length) {
            if (listener.getTableInt()[headX-1][headY-1] < 0) {
                bool = true;
            }
        }

        event.setBool(bool);
        return event;
    }

    @Override
    public void run() {
        length = getLength();
        while (true) {

            if (toGrow) {
                grow();
                toGrow = false;
            }else move();

            length = getLength();

            try {
                this.sleep(gameTick);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void move() {
        int headX = body[0][0];
        int headY = body[0][1];

        switch (direction) {
            case 'w' -> headX--;
            case 'a' -> headY--;
            case 's' -> headX++;
            case 'd' -> headY++;
        }

        int prevX = body[0][0];
        int prevY = body[0][1];
        body[0][0] = headX;
        body[0][1] = headY;

        for (int i = 1; i < length; i++) {
            int currentX = body[i][0];
            int currentY = body[i][1];
            body[i][0] = prevX;
            body[i][1] = prevY;
            prevX = currentX;
            prevY = currentY;
        }

    }

    public void grow() {
        int[][] newBody = new int[body.length][body[0].length];
        for (int i = 1; i < newBody.length-1; i++) {
            newBody[i][0] = body[i-1][0];
            newBody[i][1] = body[i-1][1];
        }

        int headX = newBody[1][0];
        int headY = newBody[1][1];

        switch (direction) {
            case 'w' -> headX--;
            case 'a' -> headY--;
            case 's' -> headX++;
            case 'd' -> headY++;
        }

        newBody[0][0] = headX;
        newBody[0][1] = headY;

        body = newBody;
    }

    public int[][] getBody() {
        return body;
    }

    public int getLength() {
        int count = 0;
        for (int[] row : body)
            if (row[0] != 0 && row[1] != 0)
                count++;
        return count;
    }

    @Override
    public void inputKey(InputEvent e) {
        switch (e.getKey()) {
            case 'w' -> direction = 'w';
            case 'a' -> direction = 'a';
            case 's' -> direction = 's';
            case 'd' -> direction = 'd';
        }
    }
}
class TableData extends AbstractTableModel {

    private int[][] tableInt;
    private Snake snake;

    private Listener listener;

    public TableData(Listener listener) {
        this.listener = listener;
        this.tableInt = listener.getTableInt();
        this.snake = listener.getSnake();
    }

    public void placeApple(){
        for (int i = 0; i < tableInt.length; i++) {
            for (int j = 0; j < tableInt[i].length; j++) {
                if (tableInt[i][j] < 0) tableInt[i][j] = 0; //remove previous apple
            }
        }
        int i = (int) (1 + Math.random()* 15);
        int j = (int) (1 + Math.random()* 24);
        if (tableInt[i][j] == 0)
            tableInt[i][j] = -1;
        else placeApple();
        listener.setTableInt(tableInt);
    }

    public void updateTable() {
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return listener.getTableInt().length;
    }

    @Override
    public int getColumnCount() {
        return listener.getTableInt()[0].length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return listener.getTableInt()[rowIndex][columnIndex];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return int.class;
    }
}
//---/LOGIC---//


//---EVENT---//
class Event extends EventObject {
    private boolean bool;

    public Event(Object source) {
        super(source);
    }

    public boolean getBool() {
        return bool;
    }
    public void setBool(boolean bool) {
        this.bool = bool;
    }
}
class InputEvent extends EventObject {
    private char key;
    public InputEvent(Object source) {
        super(source);
    }
    public char getKey() {
        return key;
    }
    public void setKey(char key) {
        this.key = key;
    }

}
//---/EVENT---//


//---LISTENER---//
interface Listener {

    InputListener getInputListener();
    int getGameTick();
    int[][] getTableInt();
    Snake getSnake();
    void setTableInt(int[][] tableInt);
    boolean checkCollision(Event e);
    boolean checkApple(Event e);
    AbstractTableModel getTableData();

}
interface InputListener {
    void inputKey(InputEvent e);
}
interface GraphicListener {
    void updateScore();
    void gameOver();
    void bestScore(int i);
    int getScore();
}
//---/LISTENER---//


//---GRAPHICS---//
class Graphics extends JFrame implements GraphicListener {

    private JTable table;
    private ScorePanel scorePanel;
    private TablePanel tablePanel;
    private Listener listener;
    private InputListener inputListener;

    public Graphics() {
        this.setSize( 720, 550);

        table = new JTable();
        table.setRowHeight(30);
        tablePanel = new TablePanel();

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
        table.setModel(listener.getTableData());
        table.setDefaultRenderer(int.class, tablePanel);
        scorePanel = new ScorePanel(listener);
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
    public void bestScore(int i) {
        scorePanel.bestScore(i);
    }

    @Override
    public int getScore() {
        return scorePanel.getScore();
    }
}
class TablePanel extends JPanel implements TableCellRenderer {

    public TablePanel() {
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if ( (int)value == -1) {
            this.setBackground(Color.RED);
        } else if ( (int)value > 1 ) {
            this.setBackground(new Color(96, 231, 13));
        } else if ( (int)value == 1 ){
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

    public ScorePanel(Listener listener) {
        this.listener = listener;
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

        g.drawString(string, 10, 25);
    }

    public void gameOver() {
        string += " GAME OVER! ";
        repaint();
    }

    public void bestScore(int i) {
        string += " Best Score: " + i;
        if (i == score)
            string += " NEW RECORD!";
        repaint();
    }
}

//---/GRAPHICS---//


//---FILE-MANAGER---//
class FileManager {

    private String player;
    private int score;
    private Map<String, Integer> info;

    public FileManager(int score) {
        player = "@" + (char)(65 + Math.random()*25) + (char)(65 + Math.random()*25)  + (char)(65 + Math.random()*25) ;
        this.score = score;
        info = new HashMap<String, Integer>();

        info.put(player, score);

        readFromFile();

        //sort map

        // Sort the map by values in descending order
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(info.entrySet());
        sortedEntries.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        // Create a new LinkedHashMap to preserve the order of sorted entries
        Map<String, Integer> infoSorted = new LinkedHashMap<>();

        // Put sorted entries into the new map
        for (Map.Entry<String, Integer> entry : sortedEntries) {
            infoSorted.put(entry.getKey(), entry.getValue());
        }

        //leave 10 top
        Map<String, Integer> first10Elements = new LinkedHashMap<>();
        int count = 0;
        for (Map.Entry<String, Integer> entry : infoSorted.entrySet()) {
            if (count >= 10) {
                break;
            }
            first10Elements.put(entry.getKey(), entry.getValue());
            count++;
        }
        info = first10Elements;
        writeToFile();

        System.out.println(info.toString());

    }

    public void writeToFile() {
        try (DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("list.bin")))) {
            for (Map.Entry<String, Integer> me : info.entrySet()) {
                outputStream.writeUTF(me.getKey());
                outputStream.writeUTF(String.valueOf(me.getValue()));
            }
            System.out.println("Data has been written to the file successfully.");
        } catch (IOException e) {
            System.out.println("Error occurred while writing data to the file.");
            e.printStackTrace();
        }
    }

    public void readFromFile() {
        try (DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream("list.bin")))) {
            while (inputStream.available() > 0) {
                info.put(
                        inputStream.readUTF(),
                        Integer.valueOf(inputStream.readUTF())
                );
            }
            System.out.println("Data has been read from the file successfully.");
        } catch (IOException e) {
            System.out.println("Error occurred while reading data from the file.");
            e.printStackTrace();
        }
    }

    public int getBestScore() {
        Map.Entry<String, Integer> firstEntry = info.entrySet().iterator().next();

        String firstKey = firstEntry.getKey();
        Integer firstValue = firstEntry.getValue();

        return firstValue;
    }

}

//---/FILE-MANAGER---//