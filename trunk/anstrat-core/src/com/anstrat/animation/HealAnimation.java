package com.anstrat.animation;

import com.anstrat.core.Assets;
import com.anstrat.gameCore.Unit;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.GUnit;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class HealAnimation extends Animation{

	private boolean started;
	private float animationStateTime;
	private GUnit source, target;
	private com.badlogic.gdx.graphics.g2d.Animation sourceAnimation, targetAnimation;
	
	private static final float START_DELAY = 0.5f;
	
	public HealAnimation(Unit source, Unit target){
		sourceAnimation = Assets.getAnimation("shaman-heal");
		targetAnimation = Assets.getAnimation("target-heal");
		
		GEngine engine = GEngine.getInstance();
		this.source = engine.getUnit(source);
		this.target = engine.getUnit(target);
		length = sourceAnimation.animationDuration + targetAnimation.animationDuration;
		lifetimeLeft = length;
	}
	
	@Override
	public void run(float deltaTime) {
		
		// Run once
		if(!started){
			source.playCustom(Assets.getAnimation("shaman-magic"), false);
			started = true;	
		}
		
		if(lifetimeLeft <= 0f){
			target.updateHealthbar();
		}
	}
	
	@Override
	public void draw(float deltaTime, SpriteBatch batch){
		super.draw(deltaTime, batch);
		float timePassed = length - lifetimeLeft;
		
		com.badlogic.gdx.graphics.g2d.Animation animation = null;
		Vector2 position = null;
		
		if(timePassed >= START_DELAY + sourceAnimation.animationDuration){
			animation = targetAnimation;
			position = target.getPosition();
			
			TextureRegion region = animation.getKeyFrame(animationStateTime, true);
			batch.draw(region, position.x - region.getRegionWidth() / 2f, position.y + region.getRegionHeight() / 2f);
		}
		else if(timePassed >= START_DELAY){
			animation = sourceAnimation;
			position = source.getPosition();
			
			TextureRegion region = animation.getKeyFrame(animationStateTime, true);
			batch.draw(region, position.x - 3f - region.getRegionWidth() / 2f, position.y + region.getRegionHeight());
		}
		
		animationStateTime += deltaTime;
	}
}
