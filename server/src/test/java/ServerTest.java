import com.tfs.server.Server;

public class ServerTest {
    public static void main(String[] args){
        new Thread(() -> new Server(25585)).start();
    }
}
