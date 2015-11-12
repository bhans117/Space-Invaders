package com.ben.game;

public class PowerUpEntity extends Entity {

	private Game game;
	private double moveSpeed = 75;
	
	public PowerUpEntity(Game game,String ref, int x, int y) {
		super(ref, x, y);
		dx = -moveSpeed;
	}

	/**
	 * Request that this alien moved based on time elapsed
	 * 
	 * @param delta The time that has elapsed since last move
	 */
	public void move(long delta) {
		// if we have reached the left hand side of the screen and

		// are moving left then request a logic update 

		if ((dx < 0) && (x < 10)) {
			game.updateLogic();
		}
		// and vice vesa, if we have reached the right hand side of 

		// the screen and are moving right, request a logic update

		if ((dx > 0) && (x > 750)) {
			game.updateLogic();
		}
		
		// proceed with normal move

		super.move(delta);
	}
	
	public void setMoveSpeed(int i) {
		moveSpeed = i;
	}
	
	/**
	 * Update the game logic related to aliens
	 */
	public void doLogic() {
		// swap over horizontal movement and move down the

		// screen a bit

		dx = -dx;
		y += 10;
		
		// if we've reached the bottom of the screen then the player

		// dies

		if (y > 570) {
			game.notifyDeath();
		}
	}
	@Override
	public void collidedWith(Entity other) {

	}

}
