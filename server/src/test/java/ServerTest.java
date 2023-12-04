import java.util.Scanner;

import com.tfs.datapack.Datapack;
import com.tfs.datapack.MusicProgress;
import com.tfs.server.Server;
import com.tfs.server.ServerHandler;


public class ServerTest {
    public static void main(String[] args){
        new Thread(() -> new Server(25585)).start();
    }
}
