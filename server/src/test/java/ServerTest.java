import java.util.Scanner;

import com.tfs.datapack.Datapack;
import com.tfs.datapack.MusicProgress;
import com.tfs.datapack.PlayMusicInstruction;
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
            if(in.equals("pause")) {
                ServerHandler.instance().sendToAllImmediately(new Datapack("PlayMusic", new PlayMusicInstruction(in,null)));
            }
            if(in.equals("continue")) {
                ServerHandler.instance().sendToAllImmediately(new Datapack("PlayMusic", new PlayMusicInstruction(in,null)));
            }
            if(in.equals("change")){
                ServerHandler.instance().sendToAllImmediately(new Datapack("PlayMusic", new PlayMusicInstruction(in,"436346833")));
            }
            if(in.equals("set")){
                ServerHandler.instance().sendToAllImmediately(new Datapack("SynchronizeMusic", new MusicProgress("436346833", 10)));
            }
            // ServerHandler.instance().sendToAll(new Datapack("SimpleString", in));

        }
        sc.close();
    }
}
