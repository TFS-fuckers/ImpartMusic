import java.util.Scanner;

import com.tfs.datapack.Datapack;
import com.tfs.server.ServerHandler;

public class ServerTest {
    public static void main(String[] args){
        new Thread(() -> new ServerHandler(25585, null)).start();
        Scanner sc = new Scanner(System.in);
        while(true){
            String in = sc.nextLine();
            if(in.equals("ok")){
                break;
            }
            if(in.equals("stop")) {
                ServerHandler.instance().kill();
            }
            ServerHandler.instance().sendToAll(new Datapack("SimpleString", in));
        }
        sc.close();
    }
}
