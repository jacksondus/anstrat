package com.anstrat.animation;

import com.anstrat.core.GameInstance;
import com.anstrat.gameCore.Fog;
import com.anstrat.gameCore.State;
import com.anstrat.gameCore.Unit;
import com.anstrat.geography.TileCoordinate;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.GUnit;
import com.anstrat.gui.SelectionHandler;
import com.badlogic.gdx.math.Vector2;

public class DeathAnimation extends Animation {

	private GUnit unit;
	private int unitOwner;
	private float timeElapsed;
	private boolean started;
	private final static float moveSpeed = 0.5f;
	private Vector2 direction;
	
	
	public DeathAnimation(Unit unit, Vector2 direction){
		this.unit = GEngine.getInstance().getUnit(unit);
		timeElapsed = 0f;
		lifetimeLeft = 2f;
		this.direction = direction;
	}
	
	/**
	 * Creates an deathAnimation for the specified and the attack from the specified tileCoordinate
	 * @param unit that dies
	 * @param coordinate of the attacker
	 */
	public DeathAnimation(Unit unit, TileCoordinate coordinate){
		this.unit = GEngine.getInstance().getUnit(unit);
		unitOwner = unit.ownerId;
		timeElapsed = 0f;
		lifetimeLeft = 2f;
		Vector2 temp = GEngine.getInstance().getMap().getTile(unit.tileCoordinate).getCenter();
		this.direction = temp.sub(GEngine.getInstance().getMap().getTile(coordinate).getCenter()).nor();
	}
	
	@Override
	public void run(float deltaTime) {
		if(!started){
			SelectionHandler sel = GEngine.getInstance().selectionHandler;
			Unit teunit = sel.selectedUnit;
			if(teunit != null && unit.unit.equals(teunit))
				sel.deselect();
			started = true;
			unit.playDeath();
			unit.unit.resolveDeath();
		}
		timeElapsed += deltaTime;
		
		unit.setPosition(new Vector2(unit.getPosition().x+direction.x*(5.37f*(float)Math.exp(-2d*timeElapsed))*moveSpeed,
				unit.getPosition().y+direction.y*(5.37f*(float)Math.exp(-2d*timeElapsed))*moveSpeed));
		float newAlpha = 2f-(float)Math.exp(timeElapsed/2.9f);
		if(newAlpha<0f)
			newAlpha = 0f;
		unit.setAlpha(newAlpha);
	}

	@Override
	public void postAnimationAction()
	{
		Fog.recalculateFog((State.activeState.currentPlayerId+1)%2, State.activeState);
		GEngine.getInstance().gUnits.remove(unit.unit.id);
	}
	
	@Override
	public boolean isVisible() {
		return Fog.isVisible(unit.unit.tileCoordinate,  GameInstance.activeGame.getUserPlayer().playerId);
	}
}