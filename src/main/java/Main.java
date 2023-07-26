
	
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    private static final int versionNumber = 13;

	// this is the playBtn, xBtn, title, and described for 

	
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
    	    	
        final Painter gamescreen = new Painter();
                
        primaryStage.setScene(gamescreen);
        primaryStage.setTitle("Painter V2." + versionNumber);
        primaryStage.show();
    }
}
