package com.ben.game;

public class ScoreEntity extends Entity {

	private Game game;
	private int moveSpeed = 200;
	
	public ScoreEntity(Game game, String ref, int x, int y) {
		super(ref, x, y);
		this.game = game;
		
		dy = -moveSpeed;
	}
	
	public void move(long delta) {
		// proceed with normal move

		super.move(delta);
		
		// if we shot off the screen, remove ourselves

		if (y < -100) {
			game.removeEntity(this);
		}
	}

	@Override
	public void collidedWith(Entity other) {
		
	}

}
