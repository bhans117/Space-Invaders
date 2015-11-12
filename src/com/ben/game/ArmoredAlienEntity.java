package com.ben.game;

public class ArmoredAlienEntity extends AlienEntity {

	private boolean armorOn;
	private String normalRef;
	
	public ArmoredAlienEntity(Game game, String armorRef, String normalRef, int x, int y) {
		super(game, armorRef, x, y);
		armorOn = true;
		this.normalRef = normalRef;
	}
	
	public void BreakArmor() {
		armorOn = false;
		this.sprite = SpriteStore.get().getSprite(normalRef);
	}
	
	public boolean ArmorStatus() {
		return armorOn;
	}

}
