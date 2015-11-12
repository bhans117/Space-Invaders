package com.ben.game;

public class FastAlienEntity extends AlienEntity {
	
	private int moveSpeed = 500;
	
	private Game game;
	
	public FastAlienEntity(Game game, String ref, int x, int y) {
		super(game, ref, x, y);
		dx = -moveSpeed;
	}
	

}
