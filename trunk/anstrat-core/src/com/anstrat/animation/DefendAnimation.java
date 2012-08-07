package com.anstrat.animation;

import com.anstrat.core.GameInstance;
import com.anstrat.gameCore.Fog;
import com.anstrat.gameCore.UnitType;
import com.anstrat.gui.GUnit;

public class DefendAnimation extends Animation {

	private static float SHIELD_END_DELAY = 1f;
	
	private GUnit gUnit;
	
	public DefendAnimation(GUnit attacker, GUnit defender, float timeTillImpact){
		this.gUnit = defender;
		
		switch(gUnit.unit.getUnitType()){
			case SWORD: length = timeTillImpact + SHIELD_END_DELAY;
			default:
				//Blood moved to AttackAnimaiton for proper timing
				//boolean directionLeft = attacker.getPosition().x > defender.getPosition().x;
				//GEngine.getInstance().animationHandler.runParalell(new BloodAnimation(defender,directionLeft));
		}
		
		lifetimeLeft = length;
	}
	
	@Override
	public void run(float deltaTime) {
		if(lifetimeLeft <= 0 && gUnit.unit.getUnitType() == UnitType.SWORD && gUnit.unit.currentHP > 0){
			gUnit.playIdle();
		}
	}

	@Override
	public boolean isVisible() {
		// TODO Auto-generated method stub
		return Fog.isVisible(gUnit.unit.tileCoordinate,  GameInstance.activeGame.getUserPlayer().playerId);
	}
}