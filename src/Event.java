import java.util.EventObject;

public class Event extends EventObject {
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
