

import java.util.Optional;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public final class VaryLoad {

	final TextField lengthfield = new TextField();
	final TextField speedfield = new TextField();
	final Label lengthLabel = new Label();
	final Label speedLabel = new Label();
	
	final Button loadbtn = new Button();
	
	final VBox root = new VBox();
    private final Scene scene = new Scene(root);
    private final Stage stage = new Stage();

	
	private Optional<String> result;
	
	public VaryLoad() {
		
        stage.setScene(scene);
		
        stage.setTitle("Vary 3 Info");
        stage.setOnCloseRequest(event -> {
            this.result = Optional.empty();
            stage.close();
        });
		
        lengthfield.setText("20");
        lengthfield.setPrefWidth(60);
        lengthLabel.setText("How far to search?");
        final HBox lengthhbox = new HBox();
        lengthhbox.setSpacing(10);
        lengthhbox.getChildren().addAll(lengthfield, lengthLabel);
        
        speedfield.setText("25");
        speedfield.setPrefWidth(60);
        speedLabel.setText("How fast to search?");
        final HBox speedhbox = new HBox();
        speedhbox.setSpacing(10);
        speedhbox.getChildren().addAll(speedfield, speedLabel);
        
        loadbtn.setText("Calulate");
        loadbtn.setOnAction(event -> {
        	
        	try {
        		int length = Integer.parseInt(lengthfield.getText().trim());
        		int speed = Integer.parseInt(speedfield.getText().trim());
        		
        		this.result = Optional.of(length + "-" + speed);
        	} catch (Exception e) {
        		this.result = Optional.empty();
			}
        	
        	stage.close();
        }); 
        
        root.setSpacing(10);
        root.setPadding(new Insets(10));
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(lengthhbox, speedhbox, loadbtn);

	}
	
    public Optional<String> getVaryLoad() {
        stage.showAndWait();
        return this.result;
    }

}
