import javax.swing.table.AbstractTableModel;
import java.util.Arrays;

public class Logic extends Thread implements Listener {

    private int[][] tableInt;
    private Snake snake;
    TableData tableData;
    private final int gameTick = 150;
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
        graphicListener.bestScore(fileManager.getInfo(), fileManager.getPlayer());
        this.stop();        //FIX!!!
        snake.stop();       //FIX!!!
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

    @Override
    public char getDirection() {
        return direction;
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
