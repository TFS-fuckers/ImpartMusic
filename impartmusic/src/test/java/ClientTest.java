import javafx.embed.swing.JFXPanel;

import java.util.Scanner;

import com.tfs.client.Client;
import com.tfs.datapack.Datapack;
import com.tfs.datapack.MusicProgress;

public class ClientTest {
    public static void main(String[] args) {
        new JFXPanel();
        // new Client();
        Scanner sc = new Scanner(System.in);
        // final String name = sc.nextLine();
        new Thread(() -> new Client()).start();
        while(true){
            String in = sc.nextLine();
            if(in.equals("OK")){
                break;
            }
            if(in.equals("set")){
                Client.INSTANCE().getConnection().sendMessage(new Datapack("SetMusic", new MusicProgress("436346833", 0, "play")));
            }
        }
        sc.close();
    }
}
