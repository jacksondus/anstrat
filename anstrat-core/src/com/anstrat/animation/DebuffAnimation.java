package com.anstrat.animation;

import com.anstrat.core.Assets;
import com.anstrat.core.GameInstance;
import com.anstrat.gameCore.Fog;
import com.anstrat.gameCore.Unit;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.GMap;
import com.anstrat.gui.GUnit;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class DebuffAnimation extends Animation {

	private GUnit target;
	private boolean started = false;
	private Color debuffColor;
	private float timePassed = 0;
	com.badlogic.gdx.graphics.g2d.Animation animation = null;
	
	public DebuffAnimation(Unit target, Color debuffColor) {
		animation = Assets.getAnimation("speedup");
		this.debuffColor = debuffColor;
		
		this.target = GEngine.getInstance().getUnit(target);
		length = animation.animationDuration;
		lifetimeLeft = length;
	}
	
	@Override
	public void run(float deltaTime) {
		// Run once
		if(!started){
			started = true;	
		}
			
		if(lifetimeLeft <= 0f){
			target.updateHealthbar();
		}
		
		timePassed += deltaTime;
	}
	
	@Override
	public void draw(float deltaTime, SpriteBatch batch){
		super.draw(deltaTime, batch);
		
		GMap map = GEngine.getInstance().map;
		Vector2 position = target.getPosition();
		
		float width = map.TILE_WIDTH;
		float height = map.TILE_HEIGHT;
		
		TextureRegion region = this.animation.getKeyFrame(length-timePassed, true);
		batch.setColor(debuffColor);
		batch.draw(region, position.x-(width/2), position.y-(height/2), width, height);
		batch.setColor(Color.WHITE);
	}

	@Override
	public boolean isVisible() {
		// TODO Auto-generated method stub
		return Fog.isVisible(target.unit.tileCoordinate,  GameInstance.activeGame.getUserPlayer().playerId);
	}
	

	
}
