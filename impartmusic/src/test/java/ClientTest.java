import com.tfs.client.Connection;

public class ClientTest {
    public static void main(String[] args){
        Connection c = new Connection("localhost", 25585);
        while(true){
            try {
                Thread.sleep(50);
                String res = c.popReceive();
                if(res != null){
                    System.out.println(res);
                }
            } catch (Exception e) {
                // TODO: handle exception
            }

        }
    }
}
