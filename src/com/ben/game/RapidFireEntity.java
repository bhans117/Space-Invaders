package com.ben.game;

public class RapidFireEntity extends PowerUpEntity {

	private Game game;
	
	public RapidFireEntity(Game game, String ref, int x, int y) {
		super(game,ref, x, y);
		this.game = game;
	}
	
}
