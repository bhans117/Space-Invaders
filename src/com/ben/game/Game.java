/**
 * 
 */
package com.ben.game;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * The main hook of our game. This class with both act as a manager
 * for the display and central mediator for the game logic. 
 * 
 * Display management will consist of a loop that cycles round all
 * entities in the game asking them to move and then drawing them
 * in the appropriate place. With the help of an inner class it
 * will also allow the player to control the main ship.
 * 
 * As a mediator it will be informed when entities within our game
 * detect events (e.g. alient killed, played died) and will take
 * appropriate game actions.
 * 
 * @author Kevin Glass
 */
public class Game extends Canvas {
	
	//////////GAME TOOLS///////////////////////////////////
	
	/** The stragey that allows us to use accelerate page flipping */
	private BufferStrategy strategy;
	/** True if the game is currently "running", i.e. the game loop is looping */
	private boolean gameRunning = true;
	
	///////////ENTITIES////////////////////////////////////////
	/** The list of all the entities that exist in our game */
	ArrayList entities = new ArrayList();
	/** The list of entities that need to be removed from the game this loop */
	ArrayList removeList = new ArrayList();
	
	//////////SHIP SETTINGS//////////////////////////////////
	/** The entity representing the player */
	Entity ship;
	/** The entity representing the player support in level 4*/
	Entity coShip;
	public boolean hasCoShip; // is the coShip used
	public int shipSpread = 50; // gap between the two ships
	public final double DEFAULT_SHIP_SPEED = 300; // Normal Ship Speed
	public final double DEFAULT_SHOT_SPEED = 300; // Normal speed of shot
	public final double DEFAULT_SHOT_INTERVAL = 500; // Normal fire interval
	/** The speed at which the player's ship should move (pixels/sec) */
	private double shipSpeed = 300; // actual current ship speed
	private double shotSpeed = 300; // actual current shot speed
	/** The time at which last fired a shot */
	long lastFire = 0;
	/** The interval between our players shot (ms) */
	double firingInterval = 500;


	//////////////////GAME STATUS/////////////////////////////
	private boolean gameOver = false;
	/** Manages the state of the game (ie. levels and game over)*/
	private GameStateManager gameStateManager;
	/** Manages the score of the game*/
	private ScoreKeeper scoreKeeper;
	private String score = "Score: ";
	private String killed = "Killed: ";
	/** The number of aliens left on the screen */
	int alienCount;
	/** The message to display which waiting for a key press */
	public String message = "";
	/** True if we're holding up game play until a key has been pressed */
	private boolean waitingForKeyPress = true;
	/** True if up arrow is pressed */
	private boolean upPressed = false; // used to continue from dialogue 
	/** True if the left cursor key is currently pressed */
	boolean leftPressed = false;
	/** True if the right cursor key is currently pressed */
	boolean rightPressed = false;
	/** True if we are firing */
	boolean firePressed = false;
	/** True if game logic needs to be applied this loop, normally as a result of a game event */
	private boolean logicRequiredThisLoop = false;

	/**state debugging variables*/
	boolean enterPressed = false;


	/**
	 * Construct our game and set it running.
	 */
	public Game() {
		// create a frame to contain our game

		JFrame container = new JFrame("Space Invaders 101");

		// get hold the content of the frame and set up the resolution of the game

		JPanel panel = (JPanel) container.getContentPane();
		panel.setPreferredSize(new Dimension(800,600));
		panel.setLayout(null);

		// setup our canvas size and put it into the content of the frame

		setBounds(0,0,800,600);
		panel.add(this);

		// Tell AWT not to bother repainting our canvas since we're

		// going to do that our self in accelerated mode

		setIgnoreRepaint(true);

		// finally make the window visible 

		container.pack();
		container.setResizable(false);
		container.setVisible(true);

		// add a listener to respond to the user closing the window. If they

		// do we'd like to exit the game

		container.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		// add a key input system (defined below) to our canvas

		// so we can respond to key pressed

		addKeyListener(new KeyInputHandler());

		// request the focus so key events come to us

		requestFocus();

		// create the buffering strategy which will allow AWT

		// to manage our accelerated graphics

		createBufferStrategy(2);
		strategy = getBufferStrategy();


		// create the manager and the score keeper
		gameStateManager = new GameStateManager(this);
		scoreKeeper = new ScoreKeeper(this);
		gameStateManager.startGame();
	}

	/**
	 * Notification from a game entity that the logic of the game
	 * should be run at the next opportunity (normally as a result of some
	 * game event)
	 */
	public void updateLogic() {
		logicRequiredThisLoop = true;
	}

	/**
	 * Remove an entity from the game. The entity removed will
	 * no longer move or be drawn.
	 * 
	 * @param entity The entity that should be removed
	 */
	public void removeEntity(Entity entity) {
		removeList.add(entity);
	}

	/**
	 * Notification that the player has died. 
	 *  set game state to game over
	 */
	public void notifyDeath() {
		gameStateManager.GameOverState();
		gameOver = true;
		waitingForKeyPress = true;
	}

	/**
	 * Notification that the player has won since all the aliens
	 * are dead.
	 * set game state to next state
	 */
	public void notifyWin() {
		gameStateManager.NextState();
		waitingForKeyPress = true;
	}

	/**
	 * Notification that an alien has been killed
	 */
	public void notifyAlienKilled(Entity e) {
		// add alien to current score
		scoreKeeper.Score(e);
		// update score message
		score = "Score: " + scoreKeeper.getScore();
		killed = "Killed: " + scoreKeeper.getTotalKilled();

		// if there are still some aliens left then they all need to get faster, so
		
		// speed up all the existing aliens

		for (int i=0;i<entities.size();i++) {
			Entity entity = (Entity) entities.get(i);
			if (entity instanceof AlienEntity || entity instanceof PowerUpEntity) {
				// speed up by 2%
				entity.setHorizontalMovement(entity.getHorizontalMovement() * 1.02);
			}
		}
	}

	/**
	 * Notify game that a power up has been shot
	 * change the game according to power up shot
	*/
	public void notifyPowerUp(Entity e) {
		//check for rapid fire 
		if (e instanceof RapidFireEntity) {
			scoreKeeper.Score(e); // add to score and create score entity
			shotSpeed *= 1.2; // increase speed of shot by 20%
			firingInterval *= .8; // decrease interval length by 20%
		}
		// check for slowdown
		else if (e instanceof SlowDownEntity){
			scoreKeeper.Score(e); // add to score and create score entity
			for (int i=0;i<entities.size();i++) {
				Entity entity = (Entity) entities.get(i);
				if (entity instanceof AlienEntity || entity instanceof PowerUpEntity) {
					// slow down by 50%

					entity.setHorizontalMovement(entity.getHorizontalMovement() * 0.50);
				}
			}
		}
	}

	/**
	 * Attempt to fire a shot from the player. Its called "try"
	 * since we must first check that the player can fire at this 
	 * point, i.e. has he/she waited long enough between shots
	 */
	public void tryToFire() {
		// check that we have waiting long enough to fire

		if (System.currentTimeMillis() - lastFire < firingInterval) {
			return;
		}

		// if we waited long enough, create the shot entity, and record the time.

		lastFire = System.currentTimeMillis();
		ShotEntity shot = new ShotEntity(this,"sprites/shot.gif",ship.getX()+10,ship.getY()-30, shotSpeed);
		entities.add(shot);
		if (hasCoShip){
			ShotEntity coShot = new ShotEntity(this,"sprites/shot.gif",coShip.getX()+10,coShip.getY()-30, shotSpeed);
			entities.add(coShot);
		}
	}

	/**
	 * The main game loop. This loop is running during all game
	 * play as is responsible for the following activities:
	 * <p>
	 * - Working out the speed of the game loop to update moves
	 * - Moving the game entities
	 * - Drawing the screen contents (entities, text)
	 * - Updating game events
	 * - Checking Input
	 * <p>
	 */
	public void gameLoop() {
		long lastLoopTime = System.currentTimeMillis();

		// keep looping round til the game ends

		while (gameRunning) {
			// work out how long its been since the last update, this

			// will be used to calculate how far the entities should

			// move this loop

			long delta = System.currentTimeMillis() - lastLoopTime;
			lastLoopTime = System.currentTimeMillis();

			// Get hold of a graphics context for the accelerated 

			// surface and blank it out

			Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
			g.setColor(Color.black);
			g.fillRect(0,0,800,600);


			// Make sure the game only changes status when we are not on dialogue
			if (!waitingForKeyPress) {
				for (int i=0;i<entities.size();i++) {
					Entity entity = (Entity) entities.get(i);

					entity.move(delta);
				}

				boolean alienOnScreen = false;
				for (int i=0;i<entities.size();i++) {
					Entity entity = (Entity) entities.get(i);
					if (entity instanceof AlienEntity) {
						alienOnScreen = true;
					}
				}
				if (!alienOnScreen) {
					notifyWin();
				}
				// resolve the movement of the ship. First assume the ship 

				// isn't moving. If either cursor key is pressed then

				// update the movement appropraitely	
				ship.setHorizontalMovement(0);

				if ((leftPressed) && (!rightPressed)) {
					ship.setHorizontalMovement(-shipSpeed);
				} else if ((rightPressed) && (!leftPressed)) {
					ship.setHorizontalMovement(shipSpeed);
				}

				if (hasCoShip) {
					coShip.setHorizontalMovement(0);

					if ((leftPressed) && (!rightPressed)) {
						coShip.setHorizontalMovement(-shipSpeed);
					} else if ((rightPressed) && (!leftPressed)) {
						coShip.setHorizontalMovement(shipSpeed);
					}
				}

				// if we're pressing fire, attempt to fire

				if (firePressed) {
					tryToFire();
				}

			}
			g.setColor(Color.white);
			g.drawString(score,15,15);
			g.drawString(killed,15, 30);


			// cycle round drawing all the entities we have in the game

			for (int i=0;i<entities.size();i++) {
				Entity entity = (Entity) entities.get(i);

				entity.draw(g);
			}

			// brute force collisions, compare every entity against

			// every other entity. If any of them collide notify 

			// both entities that the collision has occured

			for (int p=0;p<entities.size();p++) {
				for (int s=p+1;s<entities.size();s++) {
					Entity me = (Entity) entities.get(p);
					Entity him = (Entity) entities.get(s);

					if (me.collidesWith(him)) {
						me.collidedWith(him);
						him.collidedWith(me);
					}
				}
			}

			// remove any entity that has been marked for clear up

			entities.removeAll(removeList);
			removeList.clear();

			// if a game event has indicated that game logic should

			// be resolved, cycle round every entity requesting that

			// their personal logic should be considered.


			if (logicRequiredThisLoop) {
				for (int i=0;i<entities.size();i++) {
					Entity entity = (Entity) entities.get(i);
					entity.doLogic();
				}

				logicRequiredThisLoop = false;
			}

			// if we're waiting for an "any key" press then draw the 
			// current message 

			if (waitingForKeyPress) {
				
				leftPressed = false;
				rightPressed = false;
				firePressed = false;
				g.setColor(Color.white);
				g.drawString(message,(800-g.getFontMetrics().stringWidth(message))/2, 400);
				g.drawString("Press the up arrow to continue",(800-g.getFontMetrics().stringWidth("Press the up arrow to contiune"))/2,450);
			}




			// finally, we've completed drawing so clear up the graphics

			// and flip the buffer over

			g.dispose();
			strategy.show();



			// finally pause for a bit. Note: this should run us at about

			// 100 fps but on windows this might vary each loop due to

			// a bad implementation of timer

			try { Thread.sleep(10); } catch (Exception e) {}
		}
	}

	/**
	 * A class to handle keyboard input from the user. The class
	 * handles both dynamic input during game play, i.e. left/right 
	 * and shoot, and more static type input (i.e. press any key to
	 * continue)
	 * 
	 * This has been implemented as an inner class more through 
	 * habbit then anything else. Its perfectly normal to implement
	 * this as seperate class if slight less convienient.
	 * 
	 * @author Kevin Glass
	 */
	private class KeyInputHandler extends KeyAdapter {
		/** The number of key presses we've had while waiting for an "any key" press */
		private int pressCount = 1;

		/**
		 * Notification from AWT that a key has been pressed. Note that
		 * a key being pressed is equal to being pushed down but *NOT*
		 * released. Thats where keyTyped() comes in.
		 *
		 * @param e The details of the key that was pressed 
		 */
		public void keyPressed(KeyEvent e) {
			// need to do this first so we always catch it
			if (e.getKeyCode() == KeyEvent.VK_UP) {
				upPressed = true;
			}

			// check it we are in dialogue and only accept the continue key
			if (waitingForKeyPress) {
				if (upPressed){
					waitingForKeyPress = false; //switch out of dialogue
					upPressed = false; //reset this because release is dumb
					// are we in game over state? need to use same level again if so
					if (gameOver) {
						gameStateManager.SetStateNumber(gameStateManager.getStateNumber());
						gameOver = false;
					}
				}
				return;	
			}


			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				leftPressed = true;
			}
			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				rightPressed = true;
			}
			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				firePressed = true;
			}

			// for debugging purposes (allows to trigger a win without actually winning)
			if (e.getKeyCode() == KeyEvent.VK_ENTER && !enterPressed) {
				enterPressed = true;
				notifyWin();
				System.out.println("enter");
			}

		} 

		/**
		 * Notification from AWT that a key has been released.
		 *
		 * @param e The details of the key that was released 
		 */
		public void keyReleased(KeyEvent e) {
			// if we're waiting for an "any key" typed then we don't 
			// want to do anything with just a "released"
			if (waitingForKeyPress) {
				return;
			}

			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				leftPressed = false;
			}
			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				rightPressed = false;
			}
			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				firePressed = false;
			}

			// for debugging purposes 
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				enterPressed = false;
			}
		}

		/**
		 * Notification from AWT that a key has been typed. Note that
		 * typing a key means to both press and then release it.
		 *
		 * @param e The details of the key that was typed. 
		 */
		/*
		public void keyTyped(KeyEvent e) {
			// if we're waiting for a "any key" type then
			// check if we've recieved any recently. We may

			// have had a keyType() event from the user releasing

			// the shoot or move keys, hence the use of the "pressCount"
			// counter.


			if (waitingForKeyPress) {
				if (upPressed) {
					// since we've now recieved our key typed

					// event we can mark it as such and start 

					// our new game

					waitingForKeyPress = false;
					//pressCount = 0;
					upPressed = false;
					if (gameOver) {
						gameStateManager.SetStateNumber(gameStateManager.getStateNumber());
						gameOver = false;
					}
				} else {
					//	pressCount = 1;
				}
			}

			// if we hit escape, then quit the game

			if (e.getKeyChar() == 27) {
				System.exit(0);
			}
		}
		*/
	}
	


	public void setShipSpeed(double i) {
		shipSpeed = i;
	}

	public void setShotSpeed(double i, double j) {
		shotSpeed = i;
		firingInterval = j;
	}

	/**
	 * The entry point into the game. We'll simply create an
	 * instance of class which will start the display and game
	 * loop.
	 * 
	 * @param argv The arguments that are passed into our game
	 */
	public static void main(String argv[]) {
		Game g =new Game();

		// Start the main game loop, note: this method will not

		// return until the game has finished running. Hence we are

		// using the actual main thread to run the game.

		g.gameLoop();
	}

}
