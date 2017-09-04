import java.awt.Color;
import java.io.File;
import java.io.IOException;

public class Main {
	//some important static variables
	
	static int width = 1300;
	static int height = 900;
	
	static int GUIWidth = 400;
	
	static int gameWidth = width - GUIWidth;
	
	//width of one block in px
	static int blockWidth = 10;
	
	//player dimensions
	static int playerWidth = 37;
	static int playerHeight = 50;
	
	//block types, if color matches a color in the array, the type is the index of the array
	//							 black = 0		 red = 1		   green = 2		 orange = 3			 cyan = 4			 gray = 5			   blue = 6
	static Color[] blockTypes = {color(0, 0, 0), color(255, 0 ,0), color(0, 255, 0), color(255, 127, 0), color(0, 255, 255), color(127, 127, 127), color(0, 0, 255),
//  dark red = 7
	color(127, 0, 0)};
	
	//entity types, if color matches a color in the array, the type is the index of the array
	//							 orange = 0			 yellow = 1			  yellow(2) = 2		  pink = 3
	static Color[] entityTypes = {color(255, 127, 0), color(250, 255, 0), color(250, 255, 1), color(255, 127, 255)};
	
	static Game game;
	static Canvas canvas;
	
	public static void main(String[] arg){
		//load all textures in RAM
		try {
			Textures.loadTextures();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//initialize important game variables
		game = new Game();
		canvas = new Canvas(width, height, game);
		game.callLoop(100);
	}
	
	static Color color(int r, int g, int b){
		return new Color(r, g, b);
	}
}
