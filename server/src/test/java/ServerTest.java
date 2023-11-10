import java.util.Scanner;

import com.tfs.datapack.Datapack;
import com.tfs.server.Server;

public class ServerTest {
    public static void main(String[] args){
        new Thread(() -> new Server(25585)).start();
        Scanner sc = new Scanner(System.in);
        while(true){
            String in = sc.nextLine();
            if(in.equals("ok")){
                break;
            }
            Server.instance().sendToAll(new Datapack("SimpleString", in));
        }
        sc.close();
    }
}
