import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Random;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Plant implements Entity{
	private float x,y,radius = 20f;
	ImageView sprite;
	Image plant;
	Image plantsmall;
	Image eaten;
	int size;
	
	public Plant(int width, int height) {
		size = 15;
		try {
			FileInputStream plantLink = new FileInputStream("Plant.png");
			FileInputStream eatenLink = new FileInputStream("PlantEaten.png");
			FileInputStream smallLink = new FileInputStream("PlantYoung.png");
			plant = new Image(plantLink);
			eaten = new Image(eatenLink);
			plantsmall = new Image(smallLink);
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found");
			e.printStackTrace();
		}
		sprite = new ImageView(plant);
		sprite.setFitHeight(radius);
		sprite.setFitWidth(radius);
		sprite.setPreserveRatio(true);
		
		int randX = new Random().nextInt((int)(width - radius * 4)) + (int)(BugWorld.buttonSpace + radius);
		int randY = new Random().nextInt((int)(height - radius * 4)) + (int)(BugWorld.buttonSpace + radius);
		this.x=randX;
		this.y=randY;
		sprite.setX(0);
		sprite.setY(0);
		sprite.setTranslateX(randX);
		sprite.setTranslateY(randY);
		
	}
	int frame = 0;
	int growCounter = 0;
	//plant will grow back over time, using grow counter to delay each growth
	//sprite will change depending on how big the plant is
	//there is a delay when the size is 0 to stop it from growing for a little bit longer
	public void Update() {
		frame++;
		if(frame >= 32) {
			growCounter++;
			if(size <= 0) {
				if(growCounter >= 32) {
					growCounter = 0;
					size++;
				}
			}else {
				if(growCounter >= 16) {
					growCounter = 0;
					if(size <15) {
						size++;
					}
				}
			}

			frame = 0;

		}
		if(size > 5) {
			sprite.setImage(plant);
		}
		else if(size > 0 && size < 5) {
			sprite.setImage(plantsmall);
		}
		else if(size <= 0) {
			sprite.setImage(eaten);
		}
	}
	
	public int GetSize() {
		return size;
	}
	public void Eaten() {
		size--;
	}
	public ImageView GetSprite() {
		return sprite;
	}
	public float GetX() {
		return x;
	}
	public float GetY() {
		return y;
	}
}
