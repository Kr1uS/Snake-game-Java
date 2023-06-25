import java.io.*;
import java.util.*;

public class FileManager {

    private String player;
    private int score;
    private Map<String, Integer> info;

    public FileManager(int score) {
        player = "@" + (char)(65 + Math.random()*25) + (char)(65 + Math.random()*25)  + (char)(65 + Math.random()*25);
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

    public Map<String, Integer> getInfo() {
        return info;
    }

    public String getPlayer() {
        return player;
    }
}
