import java.util.Scanner;

import com.tfs.datapack.Datapack;
import com.tfs.datapack.MusicProgress;
import com.tfs.server.Server;
import com.tfs.server.ServerHandler;


public class ServerTest {
    public static void main(String[] args){
        new Thread(() -> new Server(25585)).start();
        Scanner sc = new Scanner(System.in);
        while(true){
            String in = sc.nextLine();
            if(in.equals("ok")){
                break;
            }
            if(in.equals("set")){
                ServerHandler.instance().sendToAll(new Datapack("SynchronizeMusic", new MusicProgress("436346833", 0, "play")));
            }

        }
        sc.close();
    }
}
