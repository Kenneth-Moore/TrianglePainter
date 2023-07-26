

import java.util.ArrayList;
import java.util.Optional;

import java.util.Collections;

import geometry.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Affine;
import javafx.stage.FileChooser;

public class Painter extends Scene {
	
	private static final double OFFSET = 0.00000005;
	
    private static final int WIDTH = 1300;
    private static final int HEIGHT = 650;
    
    // colours for the sides of the triangles
    final Color[] colors = {Color.BLUE, Color.GREEN, Color.RED};
	
    // radius of the dots which appear on vertices
    double dotRad = 5;
    
    // width of the lines in triangles
    double lineWid = 1;
    
    // level of zoom
    double zoomLevel = 1;
    
    final static VBox mainVBox = new VBox();
    
    final PixelRadianMap map = new PixelRadianMap(WIDTH, HEIGHT);
    
    final Button calculateBtn = new Button();
    final TextField calculateField = new TextField();
    
    final TextField currentField = new TextField();
    final Label shownLabel = new Label();
    final Label codeWarningLabel = new Label();
    final Label codeStartLable = new Label();
    
    final Button triangleBtn = new Button();
    final TextField xField = new TextField();
    final TextField yField = new TextField();

    final Button leftBtn = new Button();
    final Button rightBtn = new Button();
    final Button backBtn = new Button();
    final Button clearBtn = new Button();
    final Button roStartLBtn = new Button();
    final Button roStartRBtn = new Button();
    final Button roCodeBtn = new Button();
    final Button reverseBtn = new Button();
    //final Button exportBtn = new Button();
    
    //final Button vary3btn = new Button();
    
    final ComboBox<String> calcWhatBtn = new ComboBox<>();
    
    final ImageView backGround = renderColor(Color.WHITE);
    final ImageView topIV = renderColor(Color.TRANSPARENT);
    
    // 'panStack' does not need to be a stack since it only ever has one item in it, but everything
    // else is a stack so I made it a stack anyway
    final StackPane backStack = new StackPane(); // for the background and guides
    final StackPane basicTriStack = new StackPane(); // for the main triangles
    final StackPane solveStack = new StackPane(); // for the solve image and lines
    final StackPane fansStack = new StackPane(); // for the fans
    final StackPane dotsStack = new StackPane(); // for the dots
    final StackPane panStack = new StackPane(); // for the pan line
    final StackPane worstStack = new StackPane(); // for the worst lines
    
    final StackPane superStack = new StackPane(); // this stacks all of the stacks
    
    final Slider sliderLength = new Slider();
    final Slider slideLineWid = new Slider();
    final Label lineWidLabel = new Label(" |     Line Size:");
    final Slider slideDotWid = new Slider();
    final Label dotWidLabel = new Label("Dot Size:");
    
    final CheckBox lightCBox = new CheckBox();
    final CheckBox dotsCBox = new CheckBox();
    final CheckBox guideCBox = new CheckBox();
    final CheckBox lastCBox = new CheckBox();
    final CheckBox editCBox = new CheckBox();
    final CheckBox fansCBox = new CheckBox();
    final CheckBox worstCBox = new CheckBox();
    
    final CheckBox colorsCBox = new CheckBox();
    
    final CheckBox posCBox = new CheckBox();
    
    final Button resetBtn = new Button();
    final RadioButton magnifyRdoBtn = new RadioButton();
    final RadioButton centerBtn = new RadioButton();
    final RadioButton demagnifyRdoBtn = new RadioButton();
    final ToggleGroup magnifyGroup = new ToggleGroup();
    
    final Label zoomScaleLabel = new Label();
    final TextField zoomScaleText = new TextField();
    
    TriangleBilliard baseBilliard;
    ArrayList<Boolean> reflectSeq = new ArrayList<>();
    
    public Painter() {
    	super(mainVBox);
    	
    	//Utils.setupCustomTooltipBehavior((int) (TipOpenDelay * 1000), (int) (TipCloseDelay * 1000), 200);
    	
    	xField.setPrefWidth(80);
    	xField.setText("30");
    	xField.setTooltip(Utils.toolTip("X coordinate for 'Make Triangle'"));
    	Utils.setUpEnterPress(xField, triangleBtn);
    	yField.setPrefWidth(80);
    	yField.setText("40");
    	yField.setTooltip(Utils.toolTip("Y coordinate for 'Make Triangle'"));
    	Utils.setUpEnterPress(yField, triangleBtn);

    	triangleBtn.setText("Make Triangle");
    	Utils.colorButton(triangleBtn, Color.LIGHTBLUE, Color.GOLD);
    	triangleBtn.setTooltip(Utils.toolTip("Change the base triangle to one made with those angles"));
    	triangleBtn.setOnAction(event -> {
    		try {
    	        baseBilliard = makeTriangle(xField.getText(), yField.getText(), zoomLevel);

    			renderScreen(reflectSeq);
    			
    		} catch (NumberFormatException e) {
    			throw new RuntimeException(e);
    		}
    	});
    	
    	currentField.setEditable(false);
    	currentField.setPrefWidth(400);
    	currentField.setTooltip(Utils.toolTip("Shows what code sequence is displayed, unrotated"));
    	
    	shownLabel.setText("|     Code Displayed:");
    	codeWarningLabel.setPrefWidth(70);
    	codeWarningLabel.setTextFill(Color.RED);
    	codeWarningLabel.setTooltip(Utils.toolTip("The first and last digits of this code will combine"));
    	
    	calculateField.setPromptText("Enter a code sequence");
    	calculateField.setPrefWidth(360);
    	calculateField.setTooltip(Utils.toolTip("Enter a code sequence you want to calculate"));
    	Utils.setUpEnterPress(calculateField, calculateBtn);
    	
    	
//    	vary3btn.setText("Vary");
//    	Utils.colorButton(vary3btn, Color.LIGHTBLUE, Color.GOLD);
//    	vary3btn.setTooltip(Utils.toolTip("Run vary3 on the given triangle"));
//    	vary3btn.setOnAction(event -> {
//            baseBilliard = makeTriangle(xField.getText(), yField.getText(), zoomLevel);
//			reflectSeq.clear();
//			
//			final Optional<String> opvars = new VaryLoad().getVaryLoad();
//			if (opvars.isEmpty()) return;
//			String[] vars = opvars.get().split("-");
//			
//			new Thread() {
//				public void run() {
//					final SideSum sideSum = new SideSum(baseBilliard.aAngle, baseBilliard.bAngle);
//			        recurseFireAway(0, Integer.parseInt(vars[0]), 0, Math.PI, 0.5, 0, sideSum, baseBilliard, 
//			        				Integer.parseInt(vars[1]));
//				}
//			}.start();
//			
//    	});
    	
    	calculateBtn.setText("Calculate");
    	Utils.colorButton(calculateBtn, Color.LIGHTBLUE, Color.GOLD);
    	calculateBtn.setTooltip(Utils.toolTip("Draw the unfolding from the code sequence"));
    	calculateBtn.setOnAction(event -> {
    		
    		String[] codeStr = calculateField.getText().split(" ");
    		
    		if (codeStr[0].equals("")) {
    			return;
    		}
			
            baseBilliard = makeTriangle(xField.getText(), yField.getText(), zoomLevel);
			reflectSeq.clear();
    		int oddCount = 0;
    		int index = 1;
    		
    		if (calcWhatBtn.getValue().equals("Standard-L")) {
    			index = -1;
    		}
    		try {
				for (String number: codeStr) {
					final int n = Integer.parseInt(number);
					oddCount += n;
					for (int i = 0; i < n; i++) {
						reflectSeq.add(index == -1);
					}
					index = index * -1;
				}
				if (oddCount % 2 == 1) {
					for (String number: codeStr) {
		    			final int n = Integer.parseInt(number);
		    			for (int i = 0; i < n; i++) {
		    				reflectSeq.add(index == -1);
		    			}
		    			index = index * -1;
		    		}
				}
    		} catch (NumberFormatException e) {
                final Alert alert = new Alert(AlertType.ERROR);
                
                alert.setTitle("Error");
                alert.setHeaderText("Number formatting error");
                alert.setContentText("Please check what you have entered into the\ntext boxes.");
                alert.showAndWait();
                
                return;
    		}
    		if (!reflectSeq.isEmpty()) {
	    		if (calcWhatBtn.getValue().equals("Rotated")) {
	    			reflectSeq = rotateCodeAction(baseBilliard, reflectSeq);
	    			renderScreen(reflectSeq);
	    		} else if (calcWhatBtn.getValue().equals("Standard-L") || 
	    				   calcWhatBtn.getValue().equals("Standard-R")) {
	    			if (renderScreen(reflectSeq)) {
	    				return;
	    			}
	    			
	    			final double x = Double.parseDouble(xField.getText());
	    			final double y = Double.parseDouble(yField.getText());
	    			final double z = 180 - x - y;
    				final double[][] coords = {{y, x}, {x, z}, {z, x}, {y, z}, {z, y}, {x, y}};
    				for (double[] coord : coords) {
    					baseBilliard = makeTriangle(coord[0] + "", coord[1] + "", 1.0);
    		    		if (renderScreen(reflectSeq)) {
    		    			xField.setText("" + coord[0]);
    		    			yField.setText("" + coord[1]);
    		    			return;
    		    		}
    				}
    		        baseBilliard = makeTriangle(xField.getText(), yField.getText(), zoomLevel);
		    		
	    		} else {
	        		renderScreen(reflectSeq);
	    		}
    		}
    	});

    	calcWhatBtn.getItems().addAll("Simple", "Rotated", "Standard-L", "Standard-R");
    	calcWhatBtn.setStyle(Utils.hex(Color.LIGHTBLUE));
    	calcWhatBtn.setValue("Rotated");
    	calcWhatBtn.setTooltip(Utils.toolTip("When you press calculate, you can calculate the standard "
    			+ "form of the unfolding or a rotated version. See instructions for details"));
    	
    	codeStartLable.setText("Start:");
    	
    	roStartRBtn.setText("->");
    	Utils.colorButton(roStartRBtn, Color.LIGHTGREEN, Color.GOLD);
    	roStartRBtn.setTooltip(Utils.toolTip("Start the unfolding from the next triangle"));
    	roStartRBtn.setOnAction(event -> {
    		final double scale;
    		if (reflectSeq.isEmpty()) {
    			return;
    		} else if (reflectSeq.get(0)) {
    			yField.setText("" + (180 - Math.toDegrees(baseBilliard.aAngle) - Math.toDegrees(baseBilliard.bAngle)));
    			scale = baseBilliard.vertexC.norm() / baseBilliard.vertexB.norm();
    		} else {
    			xField.setText("" + (180 - Math.toDegrees(baseBilliard.aAngle) - Math.toDegrees(baseBilliard.bAngle)));
    			scale = (baseBilliard.vertexC.sub(baseBilliard.vertexB).norm()) / baseBilliard.vertexB.norm();
    		}
    		zoomLevel *= scale;
            baseBilliard = makeTriangle(xField.getText(), yField.getText(), zoomLevel);
    		final boolean temp = reflectSeq.get(0);
    		reflectSeq.remove(0);
    		reflectSeq.add(temp);
    		
    		renderScreen(reflectSeq);
    	});
    	roStartLBtn.setText("<-");
    	Utils.colorButton(roStartLBtn, Color.LIGHTPINK, Color.GOLD);
    	roStartLBtn.setTooltip(Utils.toolTip("Start the unfolding from the previous triangle"));
    	roStartLBtn.setOnAction(event -> {
    		
    		final double scale;
    		if (reflectSeq.isEmpty()) {
    			return;
    		} else if (reflectSeq.get(reflectSeq.size() - 1)) {
    			yField.setText("" + (180 - Math.toDegrees(baseBilliard.aAngle) - Math.toDegrees(baseBilliard.bAngle)));
    			scale = baseBilliard.vertexC.norm() / baseBilliard.vertexB.norm();
    		} else {
    			xField.setText("" + (180 - Math.toDegrees(baseBilliard.aAngle) - Math.toDegrees(baseBilliard.bAngle)));
    			scale = (baseBilliard.vertexC.sub(baseBilliard.vertexB).norm()) / baseBilliard.vertexB.norm();
    		}
    		zoomLevel *= scale;
            baseBilliard = makeTriangle(xField.getText(), yField.getText(), zoomLevel);
    		final boolean temp = reflectSeq.remove(reflectSeq.size() - 1);
    		reflectSeq.add(0, temp);
    		
    		renderScreen(reflectSeq);
    		
    	}); 
    	
    	roCodeBtn.setText("Code ->");
    	Utils.colorButton(roCodeBtn, Color.LIGHTBLUE, Color.GOLD);
    	roCodeBtn.setTooltip(Utils.toolTip("Rotate the side sum until either a periodic path is found, "
    			+ "or you have rotated all the way through the side sum."));
    	roCodeBtn.setOnAction(event ->  { 
    		reflectSeq = rotateCodeAction(baseBilliard, reflectSeq);
    		renderScreen(reflectSeq);
    	});

    	leftBtn.setText("Left");
    	Utils.colorButton(leftBtn, Color.LIGHTPINK, Color.GOLD);
    	leftBtn.setTooltip(Utils.toolTip("Add another unfolded triangle on the left side of the last triangle"));
    	leftBtn.setOnAction(event -> {
    		if (baseBilliard != null) {
    			
	    		reflectSeq.add(true);
				renderScreen(reflectSeq);
    		}
    	});

    	rightBtn.setText("Right");
    	Utils.colorButton(rightBtn, Color.LIGHTGREEN, Color.GOLD);
    	rightBtn.setTooltip(Utils.toolTip("Add another unfolded triangle on the left side of the last triangle"));
    	rightBtn.setOnAction(event -> {
    		if (baseBilliard != null) {

    			reflectSeq.add(false);
				renderScreen(reflectSeq);
    		}
    	});

    	backBtn.setText("Back");
    	Utils.colorButton(backBtn, Color.LIGHTBLUE, Color.GOLD);
    	backBtn.setTooltip(Utils.toolTip("Delete the last triangle in the unfolding"));
    	backBtn.setOnAction(event -> {
    		if (reflectSeq.isEmpty()) {

    			baseBilliard = null;
    			renderScreen(reflectSeq);

    		} else {
    			reflectSeq.remove(reflectSeq.size() - 1);
    			renderScreen(reflectSeq);
    		}
    	});

    	clearBtn.setText("Clear");
    	Utils.colorButton(clearBtn, Color.LIGHTBLUE, Color.GOLD);
    	clearBtn.setTooltip(Utils.toolTip("Clear all triangles drawn"));
    	clearBtn.setOnAction(event -> {
            codeWarningLabel.setText("");
            currentField.setText("");
    		reflectSeq.clear();
    		baseBilliard = null;
    		clearStacks();
    		backStack.getChildren().addAll(backGround);
            renderGuides();
            renderScreen(reflectSeq);
    	});
        
    	reverseBtn.setText("Reverse");
    	Utils.colorButton(reverseBtn, Color.LIGHTBLUE, Color.GOLD);
    	reverseBtn.setTooltip(Utils.toolTip("Reverse the unfolding pattern"));
    	reverseBtn.setOnAction(event -> {
    		final double scale;
    		if (reflectSeq.isEmpty()) {
    			return;
    		} else if (reflectSeq.get(reflectSeq.size() - 1)) {
    			yField.setText("" + (180 - Math.toDegrees(baseBilliard.aAngle) - Math.toDegrees(baseBilliard.bAngle)));
    			scale = baseBilliard.vertexC.norm() / baseBilliard.vertexB.norm();
    		} else {
    			xField.setText("" + (180 - Math.toDegrees(baseBilliard.aAngle) - Math.toDegrees(baseBilliard.bAngle)));
    			scale = (baseBilliard.vertexC.sub(baseBilliard.vertexB).norm()) / baseBilliard.vertexB.norm();
    		}
    		zoomLevel *= scale;
            baseBilliard = makeTriangle(xField.getText(), yField.getText(), zoomLevel);
    		
    		Collections.reverse(reflectSeq);
    		renderScreen(reflectSeq);
    	});
    	
//    	exportBtn.setText("Export");
//    	Utils.colorButton(exportBtn, Color.LIGHTBLUE, Color.GOLD);
//    	exportBtn.setTooltip(Utils.toolTip("Export the image to a file"));
//    	exportBtn.setOnAction(event -> {
//        	if (baseBilliard == null) {
//    			final Alert alert = new Alert(AlertType.ERROR);
//                
//                alert.setTitle("Error");
//                alert.setHeaderText("Empty Image");
//                alert.setContentText("This image has no content.");
//                alert.showAndWait();
//                return;
//    		}
//    		
//    		FileChooser fileChooser = new FileChooser();
//     		 
//            //Set extension filter 
//            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
//            fileChooser.getExtensionFilters().add(extFilter);
//            fileChooser.setInitialFileName(currentField.getText().trim().replace(" ", "_"));
//            
//            //Show save file dialog
//            //File file = fileChooser.showSaveDialog(this);
//           
//            //if(file != null){
//            	/*
//            	final WritableImage fullImage = superStack.snapshot(new SnapshotParameters(), null);
//            	final PixelReader reader = fullImage.getPixelReader();
//
//            	// this image is cropped to the image we want to see
//            	final int[] b = makeBounds(fullImage);
//                final WritableImage image = new WritableImage(reader, b[0], b[1], b[2], b[3]);
//            	
//            	try {
//            		ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);    	
//            	} catch (IOException e) {
//            		throw new RuntimeException(e);
//            	}        
//            	*/
//            //}
//    	});
    	
        topIV.setPickOnBounds(true);

        topIV.setOnMousePressed(event -> {
        	if (!posCBox.isSelected() && !posCBox.isIndeterminate()) {
	            if (editCBox.isSelected()) {
	        		final double initX = map.radianX(event.getX());
		            final double initY = Math.max(0, map.radianY(event.getY()));
		            
		    		final double angleX = Math.atan2(initY, initX);
		    		final double angleY = Math.atan2(initY, zoomLevel + 0.01 - initX);
					baseBilliard = TriangleBilliard.create(angleX, angleY, zoomLevel + 0.01);
					
					renderScreen(reflectSeq);
	            }
	            topIV.setOnMouseDragged(event2 -> {
		            if (editCBox.isSelected()) {
		                final double finX = map.radianX(event2.getX());
		                final double finY = Math.max(0, map.radianY(event2.getY()));
		                
		                final double angleX2 = Math.atan2(finY, finX);
		        		final double angleY2 = Math.atan2(finY, zoomLevel + 0.01 - finX);
		    			baseBilliard = TriangleBilliard.create(
		    					angleX2, angleY2, zoomLevel + 0.01);
		    			
		    			renderScreen(reflectSeq);
		            }
	            });
	            
	            topIV.setOnMouseReleased(event2 -> {
		            if (editCBox.isSelected()) {
		            	final double finX = map.radianX(event2.getX());
		                final double finY = Math.max(0, map.radianY(event2.getY()));
		                xField.setText("" + Math.toDegrees(Math.atan2(finY, finX)));
		        		yField.setText("" + Math.toDegrees(Math.atan2(
		        				finY, zoomLevel + 0.01 - finX)));
		            }
	            }); 
        	} else if (posCBox.isIndeterminate()) {
        		final double initX = event.getX();
                final double initY = event.getY();
                final ImageView initLine = new ImageView();
                panStack.getChildren().add(initLine);

                topIV.setOnMouseDragged(event2 -> {
                    final double finX = event2.getX();
                    final double finY = event2.getY();
                    final Line panLine = new Line(initX, initY, finX, finY);
                    panLine.setStroke(Color.BLACK);
                    panStack.getChildren().clear();
                    panStack.getChildren().add(panLine);
                    panStack.getChildren().get(0).setTranslateX((finX + initX - WIDTH) / 2);
                    panStack.getChildren().get(0).setTranslateY((finY + initY - HEIGHT) / 2);
                });
                topIV.setOnMouseReleased(event2 -> {
                	panStack.getChildren().clear();
                    pan(initX, initY, event2.getX(), event2.getY());
                });
        	} else {
        		topIV.setOnMouseDragged(event2 -> {});
        		topIV.setOnMouseReleased(event2 -> {});
        	}
        	
        });
        
        lightCBox.setText("Light");
        lightCBox.setTooltip(Utils.toolTip("When you find a periodic path, you can draw the interval within"
        	+ " which it is possible to shoot that path. You can also have a yellow beam of light shown."));
        lightCBox.setPrefWidth(60);
        lightCBox.setSelected(true);
        lightCBox.setAllowIndeterminate(true);
        lightCBox.setOnAction(event -> {
        if (lightCBox.isSelected()) {
            lightCBox.setText("Light");
        } else if (lightCBox.isIndeterminate()) {
            lightCBox.setText("None");
        } else {
            lightCBox.setText("Bars");
        }
        renderScreen(reflectSeq);
        });
        
        dotsCBox.setText("Dots");
        dotsCBox.setTooltip(Utils.toolTip("Unselected = no dots. Check mark = dots colored by left and "
        		+ "rights. Dash = dots colored by vertex."));
        dotsCBox.setSelected(false);
        dotsCBox.setAllowIndeterminate(true);
        dotsCBox.setOnAction(event -> renderScreen(reflectSeq));
        
        guideCBox.setText("Guideline");
        guideCBox.setTooltip(Utils.toolTip("Show the guidelines"));
        guideCBox.setSelected(true);
        guideCBox.setOnAction(event -> renderScreen(reflectSeq));
        
        lastCBox.setText("Last Triangle");
        lastCBox.setTooltip(Utils.toolTip("Show the last triangle in the unfolding"));
        lastCBox.setSelected(true);
        lastCBox.setOnAction(event -> renderScreen(reflectSeq));
        
        fansCBox.setText("Fans");
        fansCBox.setTooltip(Utils.toolTip("Show the fans"));
        fansCBox.setSelected(false);
        fansCBox.setOnAction(event -> renderScreen(reflectSeq));
        
        worstCBox.setText("Worst");
        worstCBox.setTooltip(Utils.toolTip("Show the worst lines"));
        worstCBox.setSelected(false);
        worstCBox.setOnAction(event -> renderScreen(reflectSeq));

        editCBox.setText("Edit");
        editCBox.setTooltip(Utils.toolTip("Click on the screen to edit the base triangle"));
        editCBox.setSelected(false);
        
        colorsCBox.setText("Colors");
        colorsCBox.setTooltip(Utils.toolTip("Use colors that distinguish the sides of the triangle"
        		+ " compared to the original"));
        colorsCBox.setSelected(true);
        colorsCBox.setOnAction(event -> renderScreen(reflectSeq));

        magnifyRdoBtn.setText("Magnify");
        magnifyRdoBtn.setToggleGroup(magnifyGroup);
        magnifyRdoBtn.setSelected(true);
        demagnifyRdoBtn.setText("Demagnify");
        demagnifyRdoBtn.setToggleGroup(magnifyGroup);
        centerBtn.setText("Center");
        centerBtn.setToggleGroup(magnifyGroup);
        resetBtn.setText("Reset");
        resetBtn.setOnAction(event -> {
        	changeZoomLevel(1);
        	map.resetLocation();
            baseBilliard = makeTriangle(xField.getText(), yField.getText(), zoomLevel);
        	renderScreen(reflectSeq);
        });

        zoomScaleLabel.setText("Zoom Scale:");
        zoomScaleText.setText("2");
        zoomScaleText.setTooltip(Utils.toolTip("The scale that you magnify and demagnify by"));
        zoomScaleText.setPrefWidth(55);

        slideLineWid.setMin(1);
        slideLineWid.setMax(5);
        slideLineWid.setValue(1);
        slideLineWid.setPrefWidth(250);
        slideLineWid.setShowTickLabels(true);
        slideLineWid.setShowTickMarks(true);
        slideLineWid.setMajorTickUnit(0.5);
        slideLineWid.setOnMouseDragged(e -> renderScreen(reflectSeq));
        slideLineWid.setOnMouseReleased(e -> renderScreen(reflectSeq));
        slideLineWid.valueProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
            	lineWid = slideLineWid.getValue();
            }
        });
        slideDotWid.setMin(1);
        slideDotWid.setMax(12);
        slideDotWid.setValue(5);
        slideDotWid.setPrefWidth(250);
        slideDotWid.setShowTickLabels(true);
        slideDotWid.setShowTickMarks(true);
        slideDotWid.setMajorTickUnit(1);
        slideDotWid.setOnMouseDragged(e -> renderScreen(reflectSeq));
        slideDotWid.setOnMouseReleased(e -> renderScreen(reflectSeq));
        slideDotWid.valueProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
            	dotRad = slideDotWid.getValue();
            }
        });
        sliderLength.setMin(0);
        sliderLength.setMax(2);
        sliderLength.setValue(1);
        sliderLength.setPrefWidth(384);
        sliderLength.setShowTickLabels(true);
        sliderLength.setShowTickMarks(true);
        sliderLength.setMajorTickUnit(0.2);
        sliderLength.setOnMouseDragged(event -> {
        	try {
        		if (baseBilliard != null) {
        	        baseBilliard = makeTriangle(xField.getText(), yField.getText(), zoomLevel);
        		}
    			renderScreen(reflectSeq);

    		} catch (NumberFormatException e) {
    			throw new RuntimeException(e);
    		}
        });
        sliderLength.setOnMouseReleased(event -> {
        	try {
        		if (baseBilliard != null) {
        	        baseBilliard = makeTriangle(xField.getText(), yField.getText(), zoomLevel);
        		}
    			renderScreen(reflectSeq);

    		} catch (NumberFormatException e) {
    			throw new RuntimeException(e);
    		}
        });
       
        sliderLength.valueProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
            	zoomLevel = sliderLength.getValue();
            }
        });

    	final HBox calculateHBox = new HBox(10, xField, yField, triangleBtn, leftBtn, rightBtn, backBtn, 
    			clearBtn, calculateField, calculateBtn, calcWhatBtn, codeStartLable, roStartLBtn, roStartRBtn);
    	calculateHBox.setPadding(new Insets(10, 0, 10, 0));
        calculateHBox.setAlignment(Pos.CENTER);
        
        final HBox viewHBox = new HBox(10, colorsCBox, worstCBox, lightCBox, dotsCBox, lastCBox, guideCBox, editCBox, 
        		fansCBox, posCBox, shownLabel, currentField); //, vary3btn
        viewHBox.setPadding(new Insets(0, 0, 10, 0));
        viewHBox.setAlignment(Pos.CENTER);
        
        final HBox sliderHBox = new HBox(10, new Label("Zoom: "), sliderLength);
        sliderHBox.setPadding(new Insets(10, 0, 10, 0));
        sliderHBox.setAlignment(Pos.CENTER);
        
        final HBox extraSlidersHBox = new HBox(10, lineWidLabel, slideLineWid, dotWidLabel, slideDotWid);
        extraSlidersHBox.setPadding(new Insets(10, 0, 10, 0));
        extraSlidersHBox.setAlignment(Pos.CENTER);
        
        final HBox moveHBox = new HBox(
        		10, resetBtn, zoomScaleLabel, zoomScaleText, magnifyRdoBtn, demagnifyRdoBtn, centerBtn);
        moveHBox.setPadding(new Insets(10, 0, 10, 0));
        moveHBox.setAlignment(Pos.CENTER);
        
        final HBox varyControl = new HBox(10);
        varyControl.getChildren().addAll(sliderHBox, extraSlidersHBox);
        varyControl.setAlignment(Pos.CENTER);
        
        posCBox.setText("Standard");
        posCBox.setTooltip(Utils.toolTip("The diagram can be fixed at the bottom center (standard position) "
        		+ "on the screen, or at center screen, or it can be free to move around"));
        posCBox.setPrefWidth(80);
        posCBox.setSelected(false);
        posCBox.setAllowIndeterminate(true);
        posCBox.setOnAction(event -> {
        	varyControl.getChildren().clear();
        	if (posCBox.isIndeterminate()) {
                posCBox.setText("Free");
        		varyControl.getChildren().addAll(moveHBox, extraSlidersHBox);
        	} else if (posCBox.isSelected()){
        		posCBox.setText("Center");
        		varyControl.getChildren().addAll(sliderHBox, extraSlidersHBox);
        	} else {
        		posCBox.setText("Standard");
        		varyControl.getChildren().addAll(sliderHBox, extraSlidersHBox);
        	}
        	renderScreen(reflectSeq);	
        });
        
        superStack.getChildren().addAll(backStack, fansStack, solveStack, basicTriStack,
        								worstStack, dotsStack, panStack, topIV);

    	final Affine reflectTransform = new Affine();
        reflectTransform.setMyy(-1);
        reflectTransform.setTy(superStack.getBoundsInLocal().getHeight());
        superStack.getTransforms().add(reflectTransform);
        
        mainVBox.getChildren().addAll(calculateHBox, viewHBox, superStack, varyControl);

    	backStack.getChildren().addAll(backGround);
        renderGuides();
        
    }
    
    private void changeZoomLevel(double value) {
    	zoomLevel = value;
    	sliderLength.setValue(value);
    }
    
    private static TriangleBilliard makeTriangle(final String x, final String y, final double size) {
    	final double cx = Math.toRadians(Double.parseDouble(x));
		final double cy = Math.max(0, Math.toRadians(Double.parseDouble(y)));
		// we do size + 0.01. because we can move the slider all the way to 0 but we don't actually
		// like to make billiards with a side length of 0.
		return TriangleBilliard.create(cx, cy, size + 0.01);
    }
    
    // find the bounds of the image 
    /* private int[] makeBounds(final WritableImage im) {
    	final int offset = 10;

    	double minH = HEIGHT;
    	double maxH = 0;
    	double minW = WIDTH;
    	double maxW = 0;
    	for (Node node : basicTriStack.getChildren()) {
    		final Bounds layout = node.getLayoutBounds();
    		if (layout.getMaxX() > maxW) {
    			maxW = Math.min(im.getWidth() - offset, layout.getMaxX());
    		} if (layout.getMinX() < minW) {
    			minW = Math.max(offset, layout.getMinX());  
    		} if (layout.getMaxY() > maxH) {
    			maxH = Math.min(im.getHeight() - offset, layout.getMaxY());  
    		} if (layout.getMinY() < minH) {
    			minH = Math.max(offset, layout.getMinY());  
    		}
    	}
    	
    	final int picW = (int) (maxW - minW + 2 * offset);
    	final int picH = (int) (maxH - minH + 2 * offset);
    	final int startX = (int) minW - offset;
    	final int startY = (int) (im.getHeight() - (minH + picH - offset));
    	final int[] bounds = {startX, startY, picW, picH};
    	return bounds;
    }
    */
    private void clearStacks() {
    	backStack.getChildren().clear();
        basicTriStack.getChildren().clear();
        solveStack.getChildren().clear();
        fansStack.getChildren().clear();
        dotsStack.getChildren().clear();
        worstStack.getChildren().clear();
    }
    
    private boolean renderScreen(ArrayList<Boolean> reflectSeq) {
    	
    	clearStacks();
		
        codeWarningLabel.setText("");
        currentField.setText("");
        
        final ArrayList<Vector2> left = new ArrayList<>();
        final ArrayList<Vector2> right = new ArrayList<>();
        final ArrayList<TriangleBilliard> leftTri = new ArrayList<>();
        final ArrayList<TriangleBilliard> rightTri = new ArrayList<>();
        TriangleBilliard current = null;
        
        if (baseBilliard != null) {
        	
            final int[] code = new int[reflectSeq.size()];
        	current = baseBilliard.copy();
        	
			if (!reflectSeq.isEmpty()) {
				if (reflectSeq.get(0)) {
					leftTri.add(current);
				} else {
					rightTri.add(current);
				}
			}
			left.add(current.vertexA);
			right.add(current.vertexB);
			
			// start by iterating through and finding the vertices and triangles in this unfolding
			for (int i = 0; i < reflectSeq.size(); i++) {
			 	current = TriangleBilliard.getNext(current, reflectSeq.get(i));
			 	
				//update the code here
				if (reflectSeq.get(i)) {
					code[i] = 1;
				} else {
					code[i] = 0;
				}
				
				// update the sets of left and right fans
				if (i < reflectSeq.size() - 1) {
					if (reflectSeq.get(i + 1)) {
						leftTri.add(current);
					} else {
						rightTri.add(current);
					}
				}
				
				left.add(current.vertexA);
				right.add(current.vertexB);
			}
			
        	// Update the current code numbers field
			if (!reflectSeq.isEmpty()) {
	            String codeStr = "";
	            int counter = 1;
	            
	            for (int i = 1; i < reflectSeq.size(); i++) {
	            	if (code[i] == code[i - 1]) {
	            		counter += 1; 
	            	} else {
	            		codeStr += counter + " ";
	            		counter = 1;
	            	}
	            } 
	            
	            codeStr += counter;
	            
	            if (code[0] == code[reflectSeq.size() - 1] && codeStr.length() > 1) {
	                codeWarningLabel.setText("WARNING");
	            }
	            
	            currentField.setText(codeStr);
            }
        } else {
        	// there is no triangle yet, but we may want draw two dots on the base. This 
        	// also makes the next block work properly since we don't have empty lists.
        	left.add(Vector2.create(0, 0));
        	right.add(Vector2.create(zoomLevel, 0));
        }
        
		// check how we need to translate the image. 'bounds' represents a rectangle which 
		// we iterate though and enlarge, so it just barely contains all vertices.
		final double[] bounds = {left.get(0).x, left.get(0).x, left.get(0).y, left.get(0).y};
		for (Vector2 point : left) {
			Utils.compare(bounds, point);
		}
		for (Vector2 point : right) {
			Utils.compare(bounds, point);
		}
		if (current != null & lastCBox.isSelected()) {
			Utils.compare(bounds, current.vertexC);
		}
		if (posCBox.isSelected()) {
			map.translateX((bounds[0] - Math.abs(bounds[1])));
			map.translateY((bounds[2] - Math.abs(bounds[3])));
		} else if (!posCBox.isIndeterminate()) {
			map.resetLocation();
			map.translateX(zoomLevel + 0.01);
		}
        
        // now draw everything
		backStack.getChildren().addAll(backGround);
		renderGuides();

        if (dotsCBox.isSelected()) {
        	renderDots(left, right);
        }
        
        if (fansCBox.isSelected()) {
        	renderFans(leftTri, rightTri);
        }
        
        if (worstCBox.isSelected()) {
        	renderWorst(left, right);
        }
        
        for (TriangleBilliard billiard : leftTri) {
        	renderBilliard(billiard, false);
        }
        for (TriangleBilliard billiard : rightTri) {
        	renderBilliard(billiard, false);
        }
        
        if (current != null) {
        	renderBilliard(current, !lastCBox.isSelected());
        	return renderSolve(left, right, current);
        } else {
        	return false;
        }
    }
    
    
    private boolean checkForSolve() {
    	
        final ArrayList<Vector2> left = new ArrayList<>();
        final ArrayList<Vector2> right = new ArrayList<>();
        final ArrayList<TriangleBilliard> leftTri = new ArrayList<>();
        final ArrayList<TriangleBilliard> rightTri = new ArrayList<>();
        TriangleBilliard current = null;
    	
        final int[] code = new int[reflectSeq.size()];
    	current = baseBilliard.copy();
        
    	left.add(current.vertexA);
		right.add(current.vertexB);
		
		// start by iterating through and finding the vertices and triangles in this unfolding
		for (int i = 0; i < reflectSeq.size(); i++) {
		 	current = TriangleBilliard.getNext(current, reflectSeq.get(i));
		 	
			//update the code here
			if (reflectSeq.get(i)) {
				code[i] = 1;
			} else {
				code[i] = 0;
			}
			
			// update the sets of left and right fans
			if (i < reflectSeq.size() - 1) {
				if (reflectSeq.get(i + 1)) {
					leftTri.add(current);
				} else {
					rightTri.add(current);
				}
			}
			
			left.add(current.vertexA);
			right.add(current.vertexB);
		}
		
		
    	final Vector2 vertexA = current.vertexA;
    	final Vector2 vertexB = current.vertexB;
    	
    	if (Math.abs(vertexA.y - vertexB.y) > OFFSET || current.side != 2 || current.orient != 1) {
        	return false;
        }
    	
    	final double angle = Math.atan2(vertexA.y, vertexA.x);
    	final double base = zoomLevel + 0.01;
    	double leftSpec = 0;
    	double rightSpec = base;
    	
    	for (Vector2 point : left) {
    		final double newPoint = point.x - (point.y / Math.tan(angle));
    		if (newPoint > leftSpec) {
    			leftSpec = newPoint;
    		}
    	}

    	for (Vector2 point : right) {
    		final double newPoint = point.x - (point.y / Math.tan(angle));
    		if (newPoint < rightSpec) {
    			rightSpec = newPoint;
    		}
    	}
		
    	return leftSpec < rightSpec;
    }
    
    
    private void renderDots(ArrayList<Vector2> left, ArrayList<Vector2> right) {
    	for (Vector2 point : left) {
        	if (!(point.x < map.radianX(0) || point.x > map.radianX(WIDTH) || 
        			point.y < map.radianY(0) || point.y > map.radianY(HEIGHT))) {
	        	final Circle dot = new Circle(map.pixelX(point.x), map.pixelY(point.y), dotRad);
	        	dot.setStroke(Color.DEEPSKYBLUE);
	        	dot.setFill(Color.DEEPSKYBLUE);
	        	dotsStack.getChildren().add(0, dot);
	        	dotsStack.getChildren().get(0).setTranslateX(dot.getCenterX() - WIDTH / 2);
	        	dotsStack.getChildren().get(0).setTranslateY(dot.getCenterY() - HEIGHT / 2);
        	}
        }
        for (Vector2 point : right) {
        	if (!(point.x < map.radianX(0) || point.x > map.radianX(WIDTH) || 
        			point.y < map.radianY(0) || point.y > map.radianY(HEIGHT))) {
	        	final Circle dot = new Circle(map.pixelX(point.x), map.pixelY(point.y), dotRad);
	        	dot.setStroke(Color.BLACK);
	        	dot.setFill(Color.BLACK);
	        	dotsStack.getChildren().add(0, dot);
	        	dotsStack.getChildren().get(0).setTranslateX(dot.getCenterX() - WIDTH / 2);
	        	dotsStack.getChildren().get(0).setTranslateY(dot.getCenterY() - HEIGHT / 2);
        	}
        }
    }
    
    private void renderFans(ArrayList<TriangleBilliard> leftTri, ArrayList<TriangleBilliard> rightTri) {
    	for (TriangleBilliard billiard : leftTri) {
    		boolean outside = false;
    		for (Vector2 point : billiard.getPoints()) {
    			if (map.pixelX(point.x) < 0 || map.pixelX(point.x) > WIDTH
    				|| map.pixelY(point.y) < 0 || map.pixelY(point.y) > HEIGHT) {
    				outside = true;
    			}
    		}
    		if (outside) {
    			continue;
    		}
    		final Polygon tri = new Polygon();
    		tri.getPoints().addAll(
    				map.pixelX(billiard.vertexA.x), map.pixelY(billiard.vertexA.y),
    				map.pixelX(billiard.vertexB.x), map.pixelY(billiard.vertexB.y),
    				map.pixelX(billiard.vertexC.x), map.pixelY(billiard.vertexC.y)
    		);
    		tri.setFill(Color.LIGHTBLUE);
    		fansStack.getChildren().add(0, tri);
    		final double tX = map.pixelX(Utils.midBilliard(billiard).x);
    		final double tY = map.pixelY(Utils.midBilliard(billiard).y);
    		fansStack.getChildren().get(0).setTranslateX(tX - WIDTH / 2);
    		fansStack.getChildren().get(0).setTranslateY(tY - HEIGHT / 2);
        	
    	} for (TriangleBilliard billiard : rightTri) {
    		boolean outside = false;
    		for (Vector2 point : billiard.getPoints()) {
    			if (map.pixelX(point.x) < 0 || map.pixelX(point.x) > WIDTH
    				|| map.pixelY(point.y) < 0 || map.pixelY(point.y) > HEIGHT) {
    				outside = true;
    			}
    		}
    		if (outside) {
    			continue;
    		}
    		final Polygon tri = new Polygon();
    		tri.getPoints().addAll(
    				map.pixelX(billiard.vertexA.x), map.pixelY(billiard.vertexA.y),
    				map.pixelX(billiard.vertexB.x), map.pixelY(billiard.vertexB.y),
    				map.pixelX(billiard.vertexC.x), map.pixelY(billiard.vertexC.y)
    		);
    		tri.setFill(Color.LIGHTPINK);
    		fansStack.getChildren().add(0, tri);
    		final double tX = map.pixelX(Utils.midBilliard(billiard).x);
    		final double tY = map.pixelY(Utils.midBilliard(billiard).y);
    		fansStack.getChildren().get(0).setTranslateX(tX - WIDTH / 2);
    		fansStack.getChildren().get(0).setTranslateY(tY - HEIGHT / 2);
    	}
    }
    
    private void renderWorst(ArrayList<Vector2> lefts, ArrayList<Vector2> rights) {
    	if (baseBilliard == null) {
    		return;
    	}
    	Vector2 l1Start = lefts .get(0);
    	Vector2 l1End =   rights.get(0);
    	Vector2 l2Start = rights.get(0);
    	Vector2 l2End =   lefts .get(0);
    	double specMin = 0;
    	double specMax = Math.PI;
    	for (int i = 0; i < lefts.size(); i++) {
    		for (int j = 0; j < rights.size(); j++) {
        		if (lefts.get(i).y < rights.get(j).y) {
        			final Vector2 direct = rights.get(j).sub(lefts.get(i));
        			final double newA = Math.atan2(direct.y, direct.x);
        			if (newA > specMin) {
        				l1Start = lefts.get(i);
        				l1End = rights.get(j);
        				specMin = newA;
        			}
        		} else {
        			final Vector2 direct = lefts.get(i).sub(rights.get(j));
        			final double newA = Math.atan2(direct.y, direct.x);
        			if (newA < specMax) {
        				l2Start = lefts.get(i);
        				l2End = rights.get(j);
        				specMax = newA;
        			}
        		}
        	}
        	if (specMin > specMax) {
        		return;
        	}
    	}
    	
    	final Optional<Line> riLine = Utils.smartLine(map.pixelX(l1Start.x), map.pixelY(l1Start.y),
        		map.pixelX(l1End.x), map.pixelY(l1End.y), WIDTH, HEIGHT);
        final Optional<Line> leLine = Utils.smartLine(map.pixelX(l2Start.x), map.pixelY(l2Start.y),
        		map.pixelX(l2End.x), map.pixelY(l2End.y), WIDTH, HEIGHT);
        
        if (leLine.isPresent() && riLine.isPresent()) {
    		final Line rightLine = riLine.get();
    		final Line leftLine = leLine.get();
    		rightLine.setStrokeWidth(2);
    		rightLine.setStroke(Color.DEEPSKYBLUE);
    		leftLine.setStrokeWidth(2);
    		worstStack.getChildren().add(0, rightLine);
    		worstStack.getChildren().add(0, leftLine);
        	
            final double transX = (leftLine.getStartX() + leftLine.getEndX() - WIDTH) / 2;
            final double transY = (leftLine.getStartY() + leftLine.getEndY() - HEIGHT) / 2;
            final double transX2 = (rightLine.getStartX() + rightLine.getEndX() - WIDTH) / 2;
            final double transY2 = (rightLine.getStartY() + rightLine.getEndY() - HEIGHT) / 2;
            worstStack.getChildren().get(0).setTranslateX(transX);
            worstStack.getChildren().get(0).setTranslateY(transY);
            worstStack.getChildren().get(1).setTranslateX(transX2);
            worstStack.getChildren().get(1).setTranslateY(transY2);
    	}
    	
    } 
    
    private void renderGuides() {
    	final double base = zoomLevel + 0.01;
    	if (map.pixelX(0) < 0 || map.pixelY(0) < 0 || map.pixelX(base) > WIDTH ||  map.pixelY(0) > HEIGHT) {
    		backStack.getChildren().add(new ImageView());
    		backStack.getChildren().add(new ImageView());
    		return;
    	}
    	final Line baseLine = new Line(map.pixelX(0), map.pixelY(0), map.pixelX(base), map.pixelY(0));
    	Arc arc = new Arc();
    	if (guideCBox.isSelected()) {
	    	arc.setCenterX(map.pixelX(base / 2));
	    	arc.setCenterY(map.pixelY(0));
	    	arc.setRadiusX(map.pixelX(base / 2) - map.pixelX(0));
	    	arc.setRadiusY(map.pixelY(base / 2) - map.pixelY(0));
	    	arc.setStartAngle(180);
	    	arc.setLength(180);
    	}
    	
    	backStack.getChildren().add(baseLine);
    	backStack.getChildren().add(arc);
    	
        baseLine.setStroke(Color.GRAY);
        arc.setStroke(Color.GRAY);
        arc.setFill(null);
        final double transX = (baseLine.getStartX() + baseLine.getEndX() - WIDTH) / 2;
        final double transY = (baseLine.getStartY() + baseLine.getEndY() - HEIGHT) / 2;
        backStack.getChildren().get(1).setTranslateX(transX);
        backStack.getChildren().get(1).setTranslateY(transY);
        backStack.getChildren().get(2).setTranslateX(transX);
        backStack.getChildren().get(2).setTranslateY(transY + (map.pixelX(base / 2) - map.pixelX(0)) / 2);  
    }

    private void renderBilliard(final TriangleBilliard billiard, final boolean last) {

        final Vector2[] vertices = {billiard.vertexA, billiard.vertexB, billiard.vertexC};
        final int side = billiard.side;
        final int sides;
        if (last) {
        	sides = 1;
        } else {
        	sides = 3;
        }
        
        for (int i = 0; i < sides; ++i) {
        	final int newSide = TriangleBilliard.mod3(side + i * billiard.orient);
            final double ax = map.pixelX(vertices[i].x);
            final double bx = map.pixelX(vertices[TriangleBilliard.mod3(i + 1)].x);
            final double ay = map.pixelY(vertices[i].y);
            final double by = map.pixelY(vertices[TriangleBilliard.mod3(i + 1)].y);
            
            final Optional<Line> sideLine = Utils.smartLine(ax, ay, bx, by, WIDTH, HEIGHT);
            if (sideLine.isPresent()) {
            	final Line line = sideLine.get();
            	if (colorsCBox.isSelected()) {
            		line.setStroke(colors[newSide]);
            	}
	            line.setStrokeWidth(lineWid);
	            
	            basicTriStack.getChildren().add(0, line);
	            
	            basicTriStack.getChildren().get(0).setTranslateX((line.getStartX() + line.getEndX() - WIDTH) / 2);
	            basicTriStack.getChildren().get(0).setTranslateY((line.getStartY() + line.getEndY() - HEIGHT) / 2);
            } if (dotsCBox.isIndeterminate()) {
            	final double px;
            	final double py;
            	if (billiard.orient < 1) {
            		px = bx;
            		py = by;
            	} else {
            		px = ax;
            		py = ay;
            	}
            	if (!(px < 0 || px > WIDTH || py < 0 || py > HEIGHT)) {
		        	final Circle dot = new Circle(px, py, dotRad);
		        	dot.setStroke(colors[newSide]);
		        	dot.setFill(colors[newSide]);
		        	basicTriStack.getChildren().add(0, dot);
		        	basicTriStack.getChildren().get(0).setTranslateX(dot.getCenterX() - WIDTH / 2);
		        	basicTriStack.getChildren().get(0).setTranslateY(dot.getCenterY() - HEIGHT / 2);
	        	}
            }
        }
    }
    
    private boolean renderSolve(final ArrayList<Vector2> left, final ArrayList<Vector2> right,
    		final TriangleBilliard billiard) {

    	final Vector2 vertexA = billiard.vertexA;
    	final Vector2 vertexB = billiard.vertexB;
    	
    	if (Math.abs(vertexA.y - vertexB.y) > OFFSET || billiard.side != 2 || billiard.orient != 1) {
        	return false;
        }
    	
    	final WritableImage image = new WritableImage(WIDTH, HEIGHT);
    	final PixelWriter writer = image.getPixelWriter();
    	
    	final double angle = Math.atan2(vertexA.y, vertexA.x);
    	final double base = zoomLevel + 0.01;
    	double leftSpec = 0;
    	double rightSpec = base;
    	
    	for (Vector2 point : left) {
    		final double newPoint = point.x - (point.y / Math.tan(angle));
    		if (newPoint > leftSpec) {
    			leftSpec = newPoint;
    		}
    	}

    	for (Vector2 point : right) {
    		final double newPoint = point.x - (point.y / Math.tan(angle));
    		if (newPoint < rightSpec) {
    			rightSpec = newPoint;
    		}
    	}
    	
    	if (leftSpec < rightSpec && !lightCBox.isIndeterminate()) {
    		final double leftStep = leftSpec + (vertexA.y / Math.tan(angle));
    		final double rightStep = rightSpec + (vertexA.y / Math.tan(angle));

        	final Optional<Line> leLine = Utils.smartLine(map.pixelX(leftSpec), map.pixelY(0), 
        			map.pixelX(leftStep), map.pixelY(vertexA.y), WIDTH, HEIGHT);
        	final Optional<Line> riLine = Utils.smartLine(map.pixelX(rightSpec), map.pixelY(0), 
        			map.pixelX(rightStep), map.pixelY(vertexB.y), WIDTH, HEIGHT);
        	
        	if (leLine.isPresent() && riLine.isPresent()) {
        		final Line rightLine = riLine.get();
        		final Line leftLine = leLine.get();
	        	solveStack.getChildren().add(0, rightLine);
	        	solveStack.getChildren().add(0, leftLine);
	        	
	            final double transX = (leftLine.getStartX() + leftLine.getEndX() - WIDTH) / 2;
	            final double transY = (leftLine.getStartY() + leftLine.getEndY() - HEIGHT) / 2;
	            final double transX2 = (rightLine.getStartX() + rightLine.getEndX() - WIDTH) / 2;
	            final double transY2 = (rightLine.getStartY() + rightLine.getEndY() - HEIGHT) / 2;
	            solveStack.getChildren().get(0).setTranslateX(transX);
	            solveStack.getChildren().get(0).setTranslateY(transY);
	            solveStack.getChildren().get(1).setTranslateX(transX2);
	            solveStack.getChildren().get(1).setTranslateY(transY2);
        	}
    	}
    	
    	if (lightCBox.isSelected()) {
    		final int startI = Math.max(0, (int) map.pixelY(0));
    		final int endI = Math.min((int) map.pixelY(vertexA.y), HEIGHT);
    		final int startJ = Math.max(0, (int) Math.min(map.pixelX(0), map.pixelX(vertexA.x)));
    		final int endJ = Math.min(Math.max((int) map.pixelX(base), (int) map.pixelX(vertexB.x)), WIDTH);
	    	for (int i = startI; i < endI; i++) {
	    		for (int j = startJ; j < endJ; j++) {
	    			final double pos = map.radianX(j) - (map.radianY(i) / Math.tan(angle));
	    			if (leftSpec < pos && pos < rightSpec ) {
	    				writer.setColor(j, i, Color.YELLOW);
	    			}
	    		}
	    	}
    	}
    	//solveIV.setImage(image);
    	solveStack.getChildren().add(0, new ImageView(image));
    	
    	return leftSpec < rightSpec;
    }
    
    private void recurseFireAway(
	    final int min, final int max, final double specMin, final double specMax, final double initPosition,
	    final int depth, final SideSum sideSum, final TriangleBilliard billiard, final int speed) {
	
		if (depth >= max) {
		    return;
		}
		
		Platform.runLater(new Runnable() {
			public void run() {
				renderScreen(reflectSeq);
				
			}
		});
		
		
		if (depth > min) {
			// here we check if we have reached a periodic path
	
		    if (Math.abs(sideSum.sum()) < OFFSET && billiard.side == 2 && billiard.orient == 1) {
	
		        if (checkForSolve()) {
		        	try {
		    			Thread.sleep(400);
		    		} catch (InterruptedException e) {
		    			e.printStackTrace();
		    		}
		        }
		    }
		}
		
		try {
			Thread.sleep(speed);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
		final double specialAngle = billiard.getSpecialAngle(0.5);
		// System.out.println("max: " + specMax + " spec: " + specialAngle);

		if (specMax > specialAngle) {
		    // go left
		    final TriangleBilliard leftBilliard = TriangleBilliard.getNext(billiard, true);
		    final int leftSwap = 3 - billiard.side - leftBilliard.side;
	
		    sideSum.add(leftSwap);
		    reflectSeq.add(true);
	
		    recurseFireAway(min, max, Math.max(specialAngle, specMin), specMax, initPosition,
		                    depth + 1, sideSum, leftBilliard, speed);
	
		    sideSum.sub(leftSwap);
			reflectSeq.remove(reflectSeq.size() - 1);

		}
	
		if (specMin < specialAngle) {
		    // go right
		    final TriangleBilliard rightBilliard = TriangleBilliard.getNext(billiard, false);
		    final int rightSwap = 3 - billiard.side - rightBilliard.side;
	
		    sideSum.sub(rightSwap);
		    reflectSeq.add(false);
	
		    recurseFireAway(min, max, specMin, Math.min(specialAngle, specMax), initPosition,
		                    depth + 1, sideSum, rightBilliard, speed);
	
		    sideSum.add(rightSwap);
			reflectSeq.remove(reflectSeq.size() - 1);

		}
		
		Platform.runLater(new Runnable() {
			public void run() {
				renderScreen(reflectSeq);
			}
		});
		
    }
    
    
    
    
    private void pan(final double initX, final double initY, final double finX, final double finY) {
    	
		if (Math.abs(finX - initX) > 5 || Math.abs(finY - initY) > 5) {
		   map.panX(map.radianX(initX) - map.radianX(finX));
		   map.panY(map.radianY(initY) - map.radianY(finY));
		
		   renderScreen(reflectSeq);
		} else {
            click(initX, initY);
        }
    }
    
    private void click(final double x, final double y) {
    	
    	final double oldRadianX = map.radianX(x);
        final double oldRadianY = map.radianY(y);
        
    	double zoom = Double.parseDouble(zoomScaleText.getText());
    	final double currentZoom = zoomLevel;
    	
        if (magnifyRdoBtn.isSelected()) {
        	if (currentZoom * zoom > 6) {
        		zoom = 6 / currentZoom;
        	}
        	changeZoomLevel(currentZoom * zoom);
        } else if (demagnifyRdoBtn.isSelected()) {
        	changeZoomLevel(currentZoom / zoom);
            zoom = 1 / zoom;
        } else if (centerBtn.isSelected()) {
            map.scaleBy(1);
            zoom = 1;
        }
        
        map.panX(oldRadianX * zoom - map.radianX(WIDTH / 2));
        map.panY((oldRadianY * zoom - map.radianY(HEIGHT / 2)));
        baseBilliard = makeTriangle(xField.getText(), yField.getText(), zoomLevel);
        renderScreen(reflectSeq);
    }
    
    private ArrayList<Boolean> rotateCodeAction(
    		final TriangleBilliard base, final ArrayList<Boolean> reflectSeq) {
    	if (!reflectSeq.isEmpty()) {
			final boolean temp1 = reflectSeq.get(0);
    		reflectSeq.remove(0);
    		reflectSeq.add(temp1);
			int count = 0;
			while (!checkForSolve() && count < reflectSeq.size()) {
				final boolean temp = reflectSeq.get(0);
	    		reflectSeq.remove(0);
	    		reflectSeq.add(temp);
	    		count += 1;
			}
		}
    	return reflectSeq;
    }
    
    private static ImageView renderColor(final Color color) {
        final WritableImage image = new WritableImage(WIDTH, HEIGHT);

        setImageColor(image, color);

        final ImageView imageView = new ImageView(image);

        return imageView;
    }
    
    private static void setImageColor(final WritableImage image, final Color color) {
        final PixelWriter pixelWriter = image.getPixelWriter();

        for (int pixelX = 0; pixelX < WIDTH; pixelX += 1) {
            for (int pixelY = 0; pixelY < HEIGHT; pixelY += 1) {
                pixelWriter.setColor(pixelX, pixelY, color);
            }
        }
    }
    
}   
