import java.util.ArrayList;
import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;


public class BugWorld extends Application{
//	public static int animationSpeed = 1;
	private List<Entity> entities =  new ArrayList<Entity>();
	private int height = 400;
	private int width = 400;

	Button playBtn = new Button("Play");
	
	Button pauseBtn = new Button("Pause");
	Button stopBtn = new Button("Stop");
	Text speedText = new Text("WorldSpeed");
	Slider worldSpeed = new Slider(1,8,1);
	
	public static int buttonSpace = 40;

	@Override
	public void start(Stage primaryStage) throws Exception {

		VBox worldSpeedBox = new VBox(speedText, worldSpeed);
		worldSpeedBox.setAlignment(Pos.TOP_CENTER);
		HBox buttons = new HBox(playBtn, pauseBtn, stopBtn,worldSpeedBox);
		buttons.setSpacing(5);
		buttons.setPadding(new Insets(5f,0,0,50));
		buttons.setAlignment(Pos.TOP_CENTER);

		for(int i = 0; i < 10; i++) {
			Plant e1 = new Plant(width, height);
			entities.add(e1);
		}
		for(int i = 0; i < 5; i++) {
			Bug e1 = new Bug(width, height);
			entities.add(e1);
		}

		Rectangle background = new Rectangle(width, height, Color.DARKGREY);

		Rectangle ground = new Rectangle(width, height - buttonSpace, Color.ROSYBROWN);
		ground.setY(buttonSpace);
		Group root = new Group();
		root.getChildren().add(background);
		root.getChildren().add(ground);
		root.getChildren().add(buttons);
		final Scene scene = new Scene(root,width, height);


		for(Entity b : entities) {
			root.getChildren().add(b.GetSprite());

		}

		KeyFrame frame = new KeyFrame(Duration.millis(16), new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent t) {
				for(Entity e : entities) {
					if(e instanceof Plant) {
						Plant p = (Plant)e;
						p.Update();
					}
					if(e instanceof Bug) {
						Bug a = (Bug)e;
						a.Update(scene, entities);
					}

				}
			}
		});
		Timeline timeline = new Timeline();
		timeline.setCycleCount(javafx.animation.Animation.INDEFINITE);
		timeline.getKeyFrames().add(frame);
		timeline.play();

		pauseBtn.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				timeline.pause();
			}
		});
		playBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				timeline.play();
			}
		});
		stopBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				timeline.stop();
			}
		});
		
		worldSpeed.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				timeline.setRate(worldSpeed.getValue());
			}
			
		});
		primaryStage.setTitle("Bug World");
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
	}

	
	public static void main(String[] args) {

		launch();

	}
}
