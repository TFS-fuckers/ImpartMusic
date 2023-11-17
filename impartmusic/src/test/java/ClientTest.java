import com.tfs.client.Connection;
import com.tfs.datapack.Datapack;
import com.tfs.logger.Logger;

public class ClientTest {
    public static void main(String[] args){
        Connection c = new Connection("localhost", 25585);
        while(true){
            try {
                Thread.sleep(20);
            } catch (Exception e) {
                // TODO: handle exception
            }
            Datapack pack = c.popReceive();
            if(pack == null){
                continue;
            }
            if(pack.identifier.equals("SimpleString")){
                Logger.logInfo(pack.content);
            } 
        }
    }
}
