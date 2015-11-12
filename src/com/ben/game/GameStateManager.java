package com.ben.game;

/**
 * Handles the state of the game
 * 
 * Level progression, GameOver, Game Complete
 * 
 * @author Ben Hansen
 */

public class GameStateManager {

	private Game game;

	private int stateNumber; 
	private int numberStates;

	public GameStateManager(Game game) {
		stateNumber = 1;
		numberStates = 6;
		this.game = game;
	}
	public void NextState(){
		stateNumber++;
		CreateState();
	}

	public void SetStateNumber(int i) {
		stateNumber = i;
		CreateState(); 
	}

	public void CreateState() {
		game.entities.clear();
		/* 
		 * check if the stateNumber is
		 * a valid state  by comparing it to
		 * the number of states (not including the game over state)
		 */
		if (stateNumber <= numberStates - 1){
			switch (stateNumber) {
			case 1: SetLevelOneState();
			break;
			case 2: SetLevelTwoState();
			break;
			case 3: SetLevelThreeState();
			break;
			case 4: SetLevelFourState();
			break;
			case 5: GameCompleteState();
			break;
			}
		} else {
			/*if the state is not valid, restart the game*/
			System.out.println("State not valid");
			startGame(); //return to begining
		}

	}

	/**
	 * Start a fresh game, this should clear out any old data and
	 * create a new set.
	 */
	public void startGame() {
		// clear out any existing entities and intialise a new set

		SetStateNumber(1);
		CreateState();

		// blank out any keyboard settings we might currently have

		game.leftPressed = false;
		game.rightPressed = false;
		game.firePressed = false;
	}

	public void GameOverState() {
		game.entities.clear();
		game.message = "Noooo!!!!! We are on our way to rescue you!... There! Now GO DEFFEND EARTH!";
		game.hasCoShip = false;
	}	

	///////////////////////Levels/////////////////////////



	/**                  Level 1
	 * 
	 * Initialise the starting state of the entities (ship and aliens). Each
	 * entitiy will be added to the overall list of entities in the game.
	 */
	public void SetLevelOneState() {

		game.message = "Defend the earth from these invaders quick!";
		game.ship = new ShipEntity(game,"sprites/ship.gif",370,550, false);
		game.entities.add(game.ship);

		// make sure the ship has all default settings
		game.setShotSpeed(game.DEFAULT_SHOT_SPEED, game.DEFAULT_SHOT_INTERVAL);
		game.setShipSpeed(game.DEFAULT_SHIP_SPEED);
		int row;
		int x;
		for (row=0;row<5;row++) {
			for (x=0;x<12;x++) {
				Entity alien = new AlienEntity(game,"sprites/alien.gif",100+(x*50),(50)+row*30);
				game.entities.add(alien);
			}
		}
	}

	/**                  Level 2
	 * 
	 *  Stronger aliens in the first 3 rows and normal in the back 3
	 *  Ship upgrades its fire rate
	 *  
	 */
	public void SetLevelTwoState() {
		game.message = "Good Work! We have upgraded your ship to a faster blaster. They brought reinforcements with armor!";
		game.message += " Good Luck!";
		game.ship = new ShipEntity(game,"sprites/shipBlue.gif",370,550, false);
		game.entities.add(game.ship);

		game.setShotSpeed(500, 200);

		game.alienCount = 0;
		int row;
		int x;
		int chain = 0;
		for (row=chain;row<chain+3;row++) {
			for (x=0;x<12;x++) {
				if (x == 2 && row == 2 || x == 10 && row == 1 ){
					Entity rapid = new RapidFireEntity(game,"sprites/rapidFire.gif",100+(x*50),(50)+row*30);
					game.entities.add(rapid);
				}else{
					Entity alien = new AlienEntity(game,"sprites/alien.gif",100+(x*50),(50)+row*30);
					game.entities.add(alien);
					game.alienCount++;
				}
			}
		}
		chain+=3;
		for (row =chain;row<chain+5;row++) {
			for (x=0;x<12;x++) {
				Entity armoredAlien = new ArmoredAlienEntity(game,"sprites/armoredAlien.gif","sprites/alien.gif",100+(x*50),(50)+row*30);
				game.entities.add(armoredAlien);
				game.alienCount++;
			}
		}
	}

	/**
	 * faster aliens and there are 2 rows of strong in front,
	 * 3 rows of normal, then 2 more rows of strong in back
	 * 
	 * upgrade to faster ship
	 */
	public void SetLevelThreeState() {
		game.message = "Fantastic! We noticed your ship is slow so we fitted you with a faster ship. Keep it up!";
		game.ship = new ShipEntity(game,"sprites/shipPurple.gif",370,550, false);
		game.entities.add(game.ship);

		game.setShipSpeed(1000);
		game.setShotSpeed(500, 200);

		game.alienCount = 0;
		int row;
		int x;
		int rowLength = 15;
		int chain = 0;
		for (row =chain;row<chain+2;row++) {
			for (x=0;x<rowLength;x++) {

				Entity armoredAlien = new ArmoredAlienEntity(game,"sprites/armoredAlien.gif","sprites/alien.gif",100+(x*50),(50)+row*30);
				game.entities.add(armoredAlien);
				game.alienCount++;

			}
		}
		chain+=2;
		for (row=chain;row<chain+3;row++) {
			for (x=0;x<rowLength;x++) {
				if (x == 4 && row == 2 || x == 10 && row == 2 ){
					Entity slow = new SlowDownEntity(game,"sprites/slowDown.gif",100+(x*50),(50)+row*30);
					game.entities.add(slow);
				}else{
					Entity alien = new AlienEntity(game,"sprites/alien.gif",100+(x*50),(50)+row*30);
					game.entities.add(alien);
					game.alienCount++;
				}
			}
		}
		chain+=3;
		for (row =chain;row<chain+2;row++) {
			for (x=0;x<rowLength;x++) {
				Entity armoredAlien = new ArmoredAlienEntity(game,"sprites/armoredAlien.gif","sprites/alien.gif",100+(x*50),(50)+row*30);
				game.entities.add(armoredAlien);
				game.alienCount++;
			}
		}
	}

	/**
	 * faster aliens and all strong, the top row also fires back
	 * 
	 * upgrade to having a second ship
	 */
	public void SetLevelFourState() {
		game.message = "That was close! Our scouts report a huge wave of invaders. We sent in another crew member for acistance. You're our last hope...";
		game.ship = new ShipEntity(game,"sprites/shipPurple.gif",370,550, false);
		game.entities.add(game.ship);
		game.coShip = new ShipEntity(game,"sprites/shipGold.gif",370 - game.shipSpread, 550, true);
		game.entities.add(game.coShip);

		game.setShotSpeed(500, 200);
		game.alienCount = 0;
		int row;
		int x;
		int chain = 0;
		for (row =chain;row<chain+10;row++) {
			for (x=0;x<15;x++) {
				if (x == 3 && row == 4 ||  x == 10 && row == 3 || x == 7 && row == 0 ){
					Entity rapid = new SlowDownEntity(game,"sprites/slowDown.gif",100+(x*50),(50)+row*30);
					game.entities.add(rapid);
				}else if (x == 5 && row == 8 || x == 12 && row == 6) {
					Entity rapid = new RapidFireEntity(game,"sprites/rapidFire.gif",100+(x*50),(50)+row*30);
					game.entities.add(rapid);
				}else{
					Entity armoredAlien = new ArmoredAlienEntity(game,"sprites/armoredAlien.gif","sprites/alien.gif",100+(x*50),(50)+row*30);
					game.entities.add(armoredAlien);
					game.alienCount++;
				}
			}
		}
	}

	public void GameCompleteState() {
		game.message = "You did it! We are all saved thanks to you! Your name will be forever remembered. Wait... are there more?";
		game.ship = new ShipEntity(game,"sprites/shipPurple.gif",370,550, false);
		game.entities.add(game.ship);
		game.hasCoShip = false;
		stateNumber++;

	}
	///////////////////////////////////////////////////////

	public int getStateNumber() { return stateNumber;}
}


