import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
public class Canvas extends JPanel{
	
	JFrame frame;
	Game game;
	Listeners actionListeners;
	
	//to time the frames and keep track of the FPS
	double prefFrame = 0;
	static double fps = 0;
	double startTime = getMilis();
	int frames = 0;
	
	//create the frame
	Canvas(int width, int height, Game game){
			
		frame = new JFrame();
		frame.setBounds(200, 0, width + 18, height + 47);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(this);
		this.game = game;
	
		   
		
		// Transparent 16 x 16 pixel cursor image.
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

		// Create a new blank cursor.
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
		    cursorImg, new Point(0, 0), "blank cursor");

		// Set the blank cursor to the JFrame.
		frame.getContentPane().setCursor(blankCursor);
		
		actionListeners = new Listeners(frame);
		game.listeners = actionListeners;
	
		
	}
	
	//the game is being painted
	  @Override
	  public void paintComponent(Graphics g) {
		  super.paintComponent(g);
		  
		  //make sure the framerate is about 60
		  if(prefFrame != 0){
	    		
			  while(getMilis() - prefFrame < 15){
				  try {
					  //wait some microseconds
					  Thread.sleep(0,500000);
				  } catch (InterruptedException e) {
					  e.printStackTrace();
				  }
			  }

		  }
	    	
		  //calculate an average FPS over 30 frames
		  if(frames >= 30){
			  fps = 1000.0 / ((getMilis() - startTime)/frames);
			  frames = 0;
			  startTime = getMilis();
		  }
		  frames++;
	    	
		  prefFrame = getMilis();
	    	
	    	
		  game.paintGame( (Graphics2D) g);

		  repaint();
	    	
	    }
	    
	    double getMilis(){
	    	return (double) (System.nanoTime() / 1000000.0);
	    }
   
}
