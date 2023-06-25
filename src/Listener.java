import javax.swing.table.AbstractTableModel;
import java.util.Map;

public interface Listener {

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
    char getDirection();
}

interface GraphicListener {
    void updateScore();
    void gameOver();
    void bestScore(Map<String, Integer> info, String player);
    int getScore();
}