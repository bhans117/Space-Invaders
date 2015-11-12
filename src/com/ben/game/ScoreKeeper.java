package com.ben.game;

public class ScoreKeeper {

	private Game game;

	private int armoredAlienPoints = 100;
	private int fastAlienPoints = 100;
	private int alienPoints = 50;
	private int totalPoints = 0;
	private int armoredAlienCount = 0;
	private int fastAlienCount = 0;
	private int alienCount = 0;
	private int totalCount = 0;

	public ScoreKeeper(Game game){
		this.game = game;
	}

	public void Score(Entity e) {
		if (e instanceof ArmoredAlienEntity) {
			totalPoints += armoredAlienPoints;
			armoredAlienCount++;
			ScoreEntity score = new ScoreEntity(game,"sprites/oneHundred.gif",e.getX(),e.getY()-30);
			game.entities.add(score);
		}else if(e instanceof AlienEntity) {
			totalPoints += alienPoints;
			alienCount++;
			ScoreEntity score = new ScoreEntity(game,"sprites/fifty.gif",e.getX(),e.getY()-30);
			game.entities.add(score);
		}

		if (e instanceof RapidFireEntity){
			totalPoints += alienPoints;
			ScoreEntity rapid = new ScoreEntity(game, "sprites/rapidFireScore.gif",e.getX(),e.getY()-30);
			game.entities.add(rapid);
		}
		else if (e instanceof SlowDownEntity) {
			totalPoints += alienPoints;
			ScoreEntity slow = new ScoreEntity(game, "sprites/slowDownScore.gif",e.getX(),e.getY()-30);
			game.entities.add(slow);
		}
		totalCount = alienCount + fastAlienCount + armoredAlienCount;

	}

	public int getScore() {return totalPoints;}
	public int getTotalKilled() {return totalCount;}
	public int getArmoredAlienKilled() {return armoredAlienCount;}
	public int getFastAlienKilled() {return fastAlienCount;}
	public int getAlienKilled() {return alienCount;}

}
