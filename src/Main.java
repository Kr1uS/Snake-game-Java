import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }

    //---MAIN---//
    public Main() {

        Logic logic = new Logic();

        Graphics graphics = new Graphics();

        graphics.initialise(logic);
        logic.initialise(graphics);

        graphics.openWindow();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        logic.start();

    }

}