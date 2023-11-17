import com.tfs.client.Client;

public class ClientTest {
    public static void main(String[] args){
        // Connection c = new Connection("localhost", 25585);
        // Client.getClient();
        // while(true){
        //     try {
        //         Thread.sleep(20);
        //     } catch (Exception e) {}
        //     Datapack pack = c.popReceive();
        //     if(pack == null){
        //         continue;
        //     }
        //     if(pack.identifier.equals("SimpleString")){
        //         Logger.logInfo(pack.content);
        //     }
        // }
        new Client();
    }
}