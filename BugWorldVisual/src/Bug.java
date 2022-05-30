import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Bug implements Entity{
	private float x,y,radius = 30f;
	private float speed = 1;
	private float dx = -1.5f, dy = -1.5f;


	private int range = ((int)radius * 3);
	boolean atDest = true;
	ImageView sprite;
//	ImageView sprite;
	private int desX, desY;

	Image s_Idle;
	Image s_Left;
	Image s_Right;
	Image s_Eating;
	int currentFrame = 0;

	boolean eating = false;
	private int width;
	private int height;
	
	List<Plant> nearbyPlants;
	boolean stopMoving = false;
	boolean foundPlant = false;
	int eatingFrames = 0;
	
	public Bug(int width, int height){
		this.width = width;
		this.height = height;

		try {
			FileInputStream idleLink = new FileInputStream("Bug_Idle.png");
			FileInputStream leftLink = new FileInputStream("Bug_Left.png");
			FileInputStream rightLink = new FileInputStream("Bug_Right.png");
			FileInputStream eatingLink = new FileInputStream("BugEating.png");
			
			s_Idle = new Image(idleLink);
			s_Left = new Image(leftLink);
			s_Right = new Image(rightLink);
			s_Eating = new Image(eatingLink);
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found");
			e.printStackTrace();
		}

		//setting a random starting animation point so bugs are not all in unison
		int startingFrame = new Random().nextInt((int)4) + (int)1;
		switch(startingFrame) {
		case 1:
			currentFrame = 0;
			break;
		case 2:
			currentFrame = 32;
			break;
		case 3:
			currentFrame = 64;
			break;
		case 4:
			currentFrame = 96;
			break;
		}
		int randX = new Random().nextInt((int)(width - radius)) + (int)BugWorld.buttonSpace - (int)radius;
		int randY = new Random().nextInt((int)(height - radius)) + (int)BugWorld.buttonSpace - (int)radius;

		
		this.x=randX;
		this.y=randY;
		sprite = new ImageView();
		sprite.setFitHeight(radius);
		sprite.setFitWidth(radius);
		sprite.setPreserveRatio(true);
		sprite.setX(0+radius);
		sprite.setY(0+radius);

	}
	
	public void SetUpSprite(ImageView newSprite) {
		sprite = newSprite;
	}
	
	public void Update(Scene scene, List<Entity> entities) {
		SetDestination(scene, entities);
		UpdateAnimation();
		Move(scene);
	}

	private void eating(Plant p) {
		if(p.GetSize() <= 0) {
			return;
		}
		eatingFrames ++;
		if(eatingFrames >= 120) {
			eatingFrames = 0;
			p.Eaten();
		}
		
	}

	//updating the animation of the bugs, depending if moving or eating
	private void UpdateAnimation() {

		currentFrame ++;

		if(eating) {
			if(currentFrame >= 32) {
				currentFrame = 0;
			}
			if(currentFrame > 0 && currentFrame < 16) {
				sprite.setImage(s_Idle);
			}
			else if (currentFrame > 16 && currentFrame < 32) {
				sprite.setImage(s_Eating);
			}
			return;
		}
		if(currentFrame >= 128) {
			currentFrame = 0;
		}
		if(currentFrame > 0 && currentFrame < 32) {
			sprite.setImage(s_Idle);
		}
		else if (currentFrame > 32 && currentFrame < 64) {
			sprite.setImage(s_Right);
		}
		else if (currentFrame > 64 && currentFrame < 96) {
			sprite.setImage(s_Idle);
		}
		else if(currentFrame > 96 && currentFrame < 128) {
			sprite.setImage(s_Left);
		}
	}


	
	//check if there are any plants nearby, if not set a new destination
	public void SetDestination(Scene scene, List<Entity> entities) {
		nearbyPlants = NearbyPlants(entities);
		if(nearbyPlants.size() <= 0) {
			nearbyPlants.clear();
		}
		else if(nearbyPlants.size() != 0) {
			float plantX = Math.abs(x);
			float plantY = Math.abs(y);
			for(Plant p : nearbyPlants) {
				if(Math.abs(p.GetX() - x) + Math.abs(p.GetY() - y) <= range) {
					if(p.GetSize()<= 0) {
						nearbyPlants.clear();
						eating = false;
						foundPlant = false;
						atDest = false;
						return;
					}
					desX = (int)plantX;
					desY = (int)plantY;
					foundPlant = true;
					return;
				}
			}
		}
		if(atDest) {//create a new destination
			desX = new Random().nextInt((int)(width - radius * 2)) + (int)BugWorld.buttonSpace - (int)radius;
			desY = new Random().nextInt((int)(height - radius * 2)) + (int)BugWorld.buttonSpace - (int)radius;
			atDest=false;
		}
	}
	public void Move(Scene scene) {

		//checking if the bug is within the bounds of the screen
		if(sprite.getTranslateX() < 0||
				sprite.getTranslateX() + sprite.getFitWidth() > scene.getWidth()) 
		{
			dx = -dx;
		}
		if(sprite.getTranslateY() < 0||
				sprite.getTranslateY() + sprite.getFitHeight() > scene.getHeight()) 
		{
			dy = -dy;
		}
		
		//checking to see if the bug is at the destination coordinates
		if(( x > desX - sprite.getFitWidth() / 4 && x < desX + sprite.getFitWidth() / 4) && 
				(y > desY - sprite.getFitHeight() / 4 && y < desY + sprite.getFitHeight() / 4)) {
			if(foundPlant) {//checking if the bugs destination points are on a plant, and then eating it
				for(Plant p : nearbyPlants) {
					if((x > p.GetX() - sprite.getFitWidth() /4 && x < p.GetX() + sprite.getFitWidth() /4) && 
							(y > p.GetY() - sprite.getFitHeight() /4 && y < p.GetY() + sprite.getFitHeight() /4)) {
						eating(p);
						eating = true;
						nearbyPlants.clear();
						return;
					}
				}
			}
			eating = false;
			atDest=true;
		}
		else //function to move the bug towards its destination
		{
			if(x >= desX) {
				dx = -speed;
			}
			if(x < desX) {
				dx = speed;
			}
			if(y >= desY) {
				dy = -speed;
			}
			if(y < desY) {
				dy = speed;
			}//if either it's x or y coordinate are right it won't change them
			if(x > desX - sprite.getFitWidth() / 4 && x < desX + sprite.getFitWidth() / 4) {
				dx = 0;
			}
			if(y > desY - sprite.getFitHeight() / 4 && y < desY + sprite.getFitHeight() / 4) {
				dy = 0;
			}
		}
		if(!stopMoving) {
			sprite.setTranslateX(x += dx);
			sprite.setTranslateY(y += dy);
		}
	}
	//Used for creating a list of nearby plants that have a size greater than 1
	public List<Plant> NearbyPlants(List<Entity> entities){
		List<Plant> ret = new ArrayList<Plant>();
		for(Entity e : entities) {
			if(e instanceof Plant) {
				Plant p = (Plant)e;
				if(Math.abs(p.GetX() - x) + Math.abs(p.GetY() - y) <= range) {
					if(p.GetSize() > 0) {
						ret.add(p);
					}
				}
			}
		}
		return ret;
	}
	public float getSpeed() {
		return speed;
	}
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
	public float getRadius() {
		return radius;
	}
	public int getDesX() {
		return desX;
	}

	public void setDesX(int desX) {
		this.desX = desX;
	}

	public int getDesY() {
		return desY;
	}

	public void setDesY(int desY) {
		this.desY = desY;
	}
	public ImageView GetSprite() {
		return sprite;
	}
	public int GetRange() {
		return range;
	}
	public float GetX() {
		return x;
	}
	public float GetY() {
		return y;
	}
	public float getDx() {
		return dx;
	}

	public void setDx(float dx) {
		this.dx = dx;
	}

	public float getDy() {
		return dy;
	}

	public void setDy(float dy) {
		this.dy = dy;
	}
	public float addX(float val) {
		return x += val;
	}
	public float addY(float val) {
		return y += val;
	}
}

