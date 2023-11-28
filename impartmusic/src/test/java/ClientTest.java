import javafx.embed.swing.JFXPanel;

import java.util.Scanner;

import com.tfs.client.Client;

public class ClientTest {
    public static void main(String[] args) {

        new JFXPanel();
        new Client();
        // Scanner sc = new Scanner(System.in);
        // final String name = sc.nextLine();
        // // new Thread(() -> new Client(name)).start();
        // while(true){
        //     String in = sc.nextLine();
        //     if(in.equals("OK")){
        //         break;
        //     }
        //     if(in.equals("test0")){
        //         Client.INSTANCE().getConnection().setUserInfo(in);
        //     }
        // }
        // sc.close();
    }
}