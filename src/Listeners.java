import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JFrame;

public class Listeners {
	
	boolean[] keyPressed = new boolean[255];
	
	int mouseX = -1;
	int mouseY = -1;
	boolean drag = false;
	
	boolean hasPressed = false;
	long lastPressed = 0;
	
	boolean hasReleased = false;
	long lastReleased = 0;
	
	boolean hasClicked = false;
	long lastClicked = 0;
	
	
	//key and mouse listeners for the game (dhuu)
	Listeners(JFrame frame){
		
		
		frame.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e) {
				keyPress(e.getKeyCode());
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				keyRelease(e.getKeyCode());
			}

		});
		
		frame.addMouseMotionListener(new MouseMotionAdapter(){
			public void mouseDragged(MouseEvent e){
				drag = true;
				mouseX = e.getX() - 9;
				mouseY = e.getY() - 38;
			}
			
			public void	mouseMoved(MouseEvent e){
				drag = false;
				mouseX = e.getX() - 9;
				mouseY = e.getY() - 38;
			}
			
		});
		
		frame.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e){
				hasPressed = true;
				lastPressed = System.currentTimeMillis();
			}
			
			public void mouseReleased(MouseEvent e){
				hasReleased = true;
				lastReleased = System.currentTimeMillis();
			}
			
			public void mouseClicked(MouseEvent e){
				hasClicked = true;
				lastClicked = System.currentTimeMillis();
			}
		});
		
	}
	//return the time since the key was pressed
	
	long hasPressed(){
		if(hasPressed){
			long milisSinceEvent = System.currentTimeMillis() - lastPressed;
			hasPressed = false;
			return milisSinceEvent;
		
		}
		return 99999999;
	}
	
	long hasReleased(){
		if(hasReleased){
			long milisSinceEvent = System.currentTimeMillis() - lastReleased;
			hasReleased = false;
			return milisSinceEvent;
		
		}
		return 99999999;
	}
	
	long hasClicked(){
		if(hasClicked){
			long milisSinceEvent = System.currentTimeMillis() - lastClicked;
			hasClicked = false;
			return milisSinceEvent;
		
		}
		return 99999999;
	}
	
	void keyPress(int keyCode){
		if(keyCode < keyPressed.length){
			keyPressed[keyCode] = true;
		}
	}
	
	void keyRelease(int keyCode){
		if(keyCode < keyPressed.length){
			keyPressed[keyCode] = false;
		}
	}
}
