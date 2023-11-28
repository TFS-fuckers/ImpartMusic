import javafx.application.Application;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

public class JavaFX_Sample extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("music_table.fxml"));
        primaryStage.setTitle("hello world");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    @Override
    public void init() throws Exception{
        super.init();
        System.out.println("init()...");
    }

    @Override
    public void stop() throws Exception{
        super.stop();
        System.out.println("stop()...");
    }
    
    public static void main(String[] args) {
        new JFXPanel();
        Application.launch(JavaFX_Sample.class);
    }
}