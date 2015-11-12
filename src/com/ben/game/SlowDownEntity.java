package com.ben.game;

public class SlowDownEntity extends PowerUpEntity {

	private Game game;
	
	public SlowDownEntity(Game game,String ref, int x, int y) {
		super(game,ref, x, y);
		this.game = game;
	}

}
