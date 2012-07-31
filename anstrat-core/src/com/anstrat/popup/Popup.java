package com.anstrat.popup;

import java.util.ArrayList;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.gameCore.Player;
import com.anstrat.gameCore.UnitType;
import com.anstrat.gameCore.playerAbilities.PlayerAbilityType;
import com.anstrat.guiComponent.ComponentFactory;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.esotericsoftware.tablelayout.Cell;

/**
 * @author Kalle
 */
public class Popup extends Window {
	
	public static final int WIDTH = (int)(90*Main.percentWidth);
	
	public static Popup currentPopup = null;
	private static ArrayList<Popup> popupQueue;
	private static PopupGestureHandler gestureHandler;
	private static PopupInputMultiplexer inputMultiplexer;
	private static Stage stage;
	protected static Sprite overlay;
	
	private static Label genericPopupLabel;
	private static Popup genericPopup;
	
	private static BuyUnitPopup[] buyUnitPopups;
	private static AbilityPopup[] abilityPopups; 
	public static UnitInfoPopup unitInfoPopup;
	
	public static final ClickListener POPUP_CLOSE_BUTTON_HANDLER = new ClickListener() {
		@Override
		public void click(Actor actor, float x, float y) {
			Popup.currentPopup.close();
		}
	};
	
	public boolean handlesBackspace = false;
	public boolean drawOverlay = true;

	/**
	 * 
	 * @param handler
	 * @param title
	 * @param handlesBackspace
	 * @param actors
	 */
	public Popup(String title, boolean handlesBackspace, Actor... actors) {
		this(title, actors);
		this.handlesBackspace = handlesBackspace;
	}
	
	public Popup(Actor... actors){
		this("", actors);
	}
	
	/**
	 * 
	 * @param handler
	 * @param title Popup window title.
	 * @param actors Elements to put in the popup.
	 */
	public Popup(String title, Actor... actors) {
		super(Assets.SKIN);
		this.setTitle(title);

		top();
		setComponents(actors);
		this.layout();
	}
	
	/**
	 * Returns whether a TextField is focused or not.
	 */
	public boolean textFieldSelected(){
		return stage.getKeyboardFocus() instanceof TextField;
	}
	
	/**
	 * Adds a actors to the popup
	 * @param actors Actors to add
	 */
	public void setComponents(Actor... actors){
	    this.clear();
		for(Actor a : actors)
			add(a);
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public Cell add (Actor actor) {
		//setListeners(actor);
		row();
		Cell cell = super.add(actor).width(WIDTH).center();
		if(actor instanceof TextButton)
			cell.height((int)Main.percentHeight*8);
		else if(actor instanceof Label)
			((Label)actor).setWrap(true);
		return cell;
	}
	
	@SuppressWarnings("rawtypes")
	public Cell add (Actor actor, int height){
		return add(actor).height(height);
	}
	
	@SuppressWarnings("rawtypes")
	public Cell add(Actor actor, int height, int width) {
		return super.add(actor).width(width).center();
	}
	
	/**
	 * Sets an {@link Actor}'s listener depending on the {@link Actor} type
	 * @param actor
	 */
	/*
	private void setListeners(Actor actor){
		if(actor instanceof TextField)
			((TextField)actor).setTextFieldListener(tl);
		else if(actor instanceof Button)
			((Button)actor).setClickListener(cl);
		else if(actor instanceof Row)
			((Row)actor).setListeners(cl, tl);
	}
	*/
	/**
	 * Clears all text inputs in the popup
	 */
	public void clearInputs(){
		for(Actor a : this.children)
			if(a instanceof TextField)
				((TextField)a).setText("");
	}
	
	/**
	 * Sets popup as current, or if there already is one, adds it to the queue of waiting popups
	 */
	public void show(){
		if(currentPopup==null){
			gestureHandler.setOverridesInput(true);
			inputMultiplexer.setOverridesInput(true);
			this.showInit();
		}
		else
			popupQueue.add(this);
		
		inputMultiplexer.addProcessor(stage);
	}
	
	/**
	 * Set stuff before being shown.
	 */
	private void showInit(){
		currentPopup = this;
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		stage.addActor(this);
		stage.unfocusAll();
	}

	/**
	 * Close the popup
	 */
	public void close(){
		if(currentPopup==this){
			stage.removeActor(this);
			if(popupQueue.isEmpty()){
				//Last Popup gone - return control
				gestureHandler.setOverridesInput(false);
				inputMultiplexer.setOverridesInput(false);
				currentPopup = null;
			}
			else{
				popupQueue.remove(0).showInit();
			}
		}
		else
			popupQueue.remove(this);
		
		inputMultiplexer.removeProcessor(stage);
		Gdx.input.setOnscreenKeyboardVisible(false);
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		if(drawOverlay) overlay.draw(batch);
		super.draw(batch, parentAlpha);
		
		// Only actually draws debug if it's enabled on the table layout
		drawDebug(stage);
	}
	
	public void resize(int width, int height) {
		overlay.setSize(width, height);
		pack();
		x = (width - this.width)/2;
		y = (height - this.height)/2;
	}
	
	public static void initPopups(Stage stage){
		gestureHandler = new PopupGestureHandler();
		inputMultiplexer = new PopupInputMultiplexer();
		Main.getInstance().gestureMultiplexer.addProcessor(gestureHandler);
		Main.getInstance().addProcessor(inputMultiplexer);
		
		popupQueue = new ArrayList<Popup>();
		Popup.stage = stage;
		
		overlay = new Sprite(Assets.WHITE);
		
		Color bcolor = Color.DARK_GRAY;
		bcolor.a = 0.8f;
		overlay.setColor(bcolor);
		
		unitInfoPopup = new UnitInfoPopup();
		//buyUnitPopup = new BuyUnitPopup(UnitType.AXE_THROWER, UnitType.BERSERKER, UnitType.SHAMAN, UnitType.SWORD, UnitType.WOLF, UnitType.HAWK);
		abilityPopups = new AbilityPopup[4];

		for (int i = 0; i < abilityPopups.length; i++) {
			abilityPopups[i] = new AbilityPopup(PlayerAbilityType.getAbilitiesFromGod(i));
		}
		
		buyUnitPopups = new BuyUnitPopup[UnitType.TEAMS.length];
		for(int i = 0; i < buyUnitPopups.length; i++){
			UnitType[] team = UnitType.TEAMS[i];
			buyUnitPopups[i] = new BuyUnitPopup(team);
		}
		
		genericPopupLabel = new Label("", Assets.SKIN);
		genericPopupLabel.setWrap(true);
		
		TextButton genericPopupOK = ComponentFactory.createButton("Ok", null);
		genericPopupOK.setClickListener(new ClickListener() {
			@Override
			public void click(Actor actor, float x, float y) {
				Popup.currentPopup.close();
			}
		});
		
		genericPopup = new Popup("", genericPopupLabel, genericPopupOK);
	}
	
	public static BuyUnitPopup getBuyUnitPopup(){
		Player currentPlayer = com.anstrat.gameCore.State.activeState.players[com.anstrat.gameCore.State.activeState.currentPlayerId];
		return buyUnitPopups[currentPlayer.team];
	}
	
	public static AbilityPopup getAbilityPopup(){
		Player currentPlayer = com.anstrat.gameCore.State.activeState.players[com.anstrat.gameCore.State.activeState.currentPlayerId];
		return abilityPopups[currentPlayer.god];
	}
	
	/**
	 * Shows a generic {@link Popup}
	 * @param message The message to be shown
	 */
	public static void showGenericPopup(String title, String message){
		genericPopup.setTitle(title);
		genericPopupLabel.setText(message);
		genericPopup.layout();
		genericPopup.show();
	}
	
	public static void disposePopups(){
		
		buyUnitPopups = null;
		abilityPopups = null;
		overlay = null;
		gestureHandler = null;
		currentPopup = null;
		genericPopup = null;
		genericPopupLabel = null;
		unitInfoPopup = null;
	}
}
