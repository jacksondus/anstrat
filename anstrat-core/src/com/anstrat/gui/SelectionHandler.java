package com.anstrat.gui;

import java.util.ArrayList;
import java.util.List;

import com.anstrat.animation.Animation;
import com.anstrat.animation.MoveCameraAnimation;
import com.anstrat.audio.AudioAssets;
import com.anstrat.core.GameInstance;
import com.anstrat.gameCore.Building;
import com.anstrat.gameCore.State;
import com.anstrat.gameCore.StateUtils;
import com.anstrat.gameCore.Unit;
import com.anstrat.gameCore.UnitType;
import com.anstrat.gameCore.abilities.TargetedAbility;
import com.anstrat.gameCore.playerAbilities.DoubleTargetedPlayerAbility;
import com.anstrat.gameCore.playerAbilities.TargetedPlayerAbility;
import com.anstrat.geography.Map;
import com.anstrat.geography.Tile;
import com.anstrat.geography.TileCoordinate;

public class SelectionHandler {
	public static final int SELECTION_UNIT = 1;
	public static final int SELECTION_EMPTY = 2;
	public static final int SELECTION_SPAWN = 3;
	/** Used for targeted abilities */
	public static final int SELECTION_TARGETED_ABILITY = 5;
	public static final int SELECTION_BUILDING = 6;
	public static final int SELECTION_TARGETED_PLAYER_ABILITY = 7;
	public static final int SELECTION_DOUBLE_TARGETED_PLAYER_ABILITY = 8;
	
	public GTile gTile;
	public Unit selectedUnit = null;
	public UnitType spawnUnitType = null;
	public Building selectedBuilding = null;
	public TargetedAbility selectedTargetedAbility = null;
	public TargetedPlayerAbility selectedTargetedPlayerAbility = null;
	public DoubleTargetedPlayerAbility selectedDoubleTargetedPlayerAbility = null;
	public int selectionType = SELECTION_EMPTY;
	public String abilityName;
	
	
	public void selectUnit(Unit unit){
		GEngine gEngine = GEngine.getInstance();
		if(unit != null && unit.currentHP > 0){
			gEngine.highlighter.clearHighlights();
			selectedUnit = unit;
			int prevSel = selectionType;
			selectionType = SELECTION_UNIT;
			gEngine.userInterface.showUnit(unit);
			
			
			if(unit.ownerId == State.activeState.currentPlayerId && unit.ownerId == GameInstance.activeGame.getUserPlayer().playerId){
				if(prevSel == SELECTION_EMPTY)
					AudioAssets.playSound("selectUnit");
				gEngine.highlighter.showRange(unit.tileCoordinate, unit.getMaxAttackRange());
				gEngine.actionMap.prepare(unit);
				gEngine.highlighter.highlightTiles(gEngine.actionMap.getAllowedTiles(), true);//Pathfinding.getUnitRange(unit), false);
				gEngine.actionHandler.refreshHighlight(unit);
			}
			else
				gEngine.highlighter.highlightTile(unit.tileCoordinate, false);
		} else{
			deselect();
		}
		
	}
	public void selectAbility(Unit source, TargetedAbility ability, String abilName){
		selectedUnit = source;
		selectedTargetedAbility = ability;
		selectionType = SELECTION_TARGETED_ABILITY;
		
		List<TileCoordinate> highlights = ability.getValidTiles(source);
		if(highlights.isEmpty()){
			deselect();
			return;
		}
		GEngine.getInstance().highlighter.highlightTiles(highlights, true);
		GEngine.getInstance().highlighter.setOutline(highlights, Highlighter.BORDER_ABILITY);	
	}
	
	public void selectPlayerAbility(TargetedPlayerAbility ability, String abilName){
		selectedTargetedPlayerAbility = ability;
		selectionType = SELECTION_TARGETED_PLAYER_ABILITY;
		
		List<TileCoordinate> highlights = ability.getValidTiles(ability.player);
		if(highlights.isEmpty()){
			deselect();
			return;
		}
		selectedUnit = null;
		GEngine.getInstance().highlighter.highlightTiles(highlights, true);
		GEngine.getInstance().highlighter.setOutline(highlights, Highlighter.BORDER_ABILITY);
	}
	
	public void selectPlayerAbility(DoubleTargetedPlayerAbility ability, String abilName){
		selectedDoubleTargetedPlayerAbility = ability;
		selectionType = SELECTION_DOUBLE_TARGETED_PLAYER_ABILITY;
		
		List<TileCoordinate> highlights = ability.getValidTiles(ability.player);
		if(highlights.isEmpty()){
			deselect();
			return;
		}
		selectedUnit = null;
		GEngine.getInstance().highlighter.highlightTiles(highlights, true);
		GEngine.getInstance().highlighter.setOutline(highlights, Highlighter.BORDER_ABILITY);
	}
	
	public void selectBuilding(Building building){
		if(building != null){
			selectedBuilding = building;
			selectionType = SELECTION_BUILDING;
		}
	}
	public void selectSpawn(UnitType unitType){
		spawnUnitType = unitType;
		selectionType = SELECTION_SPAWN;
		
		GEngine.getInstance().userInterface.showUnitType(unitType);
		
		Map map = State.activeState.map;
		
		List<Tile> adjacent = map.getNeighbors(StateUtils.getCurrentPlayerCastle().tileCoordinate);
		adjacent.add(map.getTile(StateUtils.getCurrentPlayerCastle().tileCoordinate));
		
		// Remove invalid tiles.
		List<TileCoordinate> highlights = new ArrayList<TileCoordinate>();
		for(Tile t : adjacent){
			if(unitType.getTerrainPenalty(t.terrain)!=Integer.MAX_VALUE
					&& StateUtils.getUnitByTile(t.coordinates)==null)
				highlights.add(t.coordinates);
		}
		
		GEngine.getInstance().highlighter.highlightTiles(highlights, false);
		GEngine.getInstance().highlighter.setOutline(highlights, Highlighter.BORDER_SPAWN);
		
		Animation animation = new MoveCameraAnimation(GEngine.getInstance().map.getTile(StateUtils.getCurrentPlayerCastle().tileCoordinate).getCenter());
		GEngine.getInstance().animationHandler.runParalell(animation);
	}
	public void deselect(){
		GEngine gEngine = GEngine.getInstance();
		gEngine.highlighter.clearHighlights();
		selectionType = SELECTION_EMPTY;
		gEngine.actionMap.clear();
		gEngine.userInterface.showUnit(null);
	}
	
	
}
