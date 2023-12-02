import java.util.ArrayList;

import com.tfs.datapack.Datapack;
import com.tfs.logger.Logger;
import com.tfs.server.User;

public class GSONtest {
    public static void main(String[] args) {
        ArrayList<User> list = new ArrayList<>();
        list.add(new User("", null, null));
        Datapack pack = new Datapack("", list);
        Logger.logInfo(pack.toJson());
    }
}
