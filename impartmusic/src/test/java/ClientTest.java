import javafx.embed.swing.JFXPanel;
import com.tfs.client.Client;
import com.tfs.dxconfig.ImpartConfigReader;

public class ClientTest {
    public static void main(String[] args) {
        ImpartConfigReader.initialize();
        new JFXPanel();
        new Client();
    }
}