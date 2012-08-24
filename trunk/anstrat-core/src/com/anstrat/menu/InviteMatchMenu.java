package com.anstrat.menu;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.gameCore.playerAbilities.PlayerAbilityType;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.popup.InvitePopup;
import com.anstrat.popup.InvitePopup.InvitePopupHandler;
import com.anstrat.popup.Popup;
import com.anstrat.popup.TeamPopup;
import com.anstrat.popup.TeamPopup.TeamPopupListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;

public class InviteMatchMenu extends MenuScreen {
	private static InviteMatchMenu me;
	private TextButton friendButton;
	
	private int god = PlayerAbilityType.GOD_ODIN, team = TeamPopup.TEAM_VV;
	private MapSelecter mapSelecter;
	
	private InviteMatchMenu(){        
		Table settings = new Table(Assets.SKIN);
		settings.setBackground(Assets.SKIN.getPatch("single-border"));
		
		mapSelecter = new MapSelecter();

		CheckBox fog = ComponentFactory.createCheckBox("Fog of War");
		fog.setChecked(true);
				
		Button godButton = ComponentFactory.createButton("God and team", new ClickListener() {
			@Override
			public void click(Actor actor, float x, float y) {
				Popup popup = new TeamPopup(god, team, "Select your team and god", new TeamPopupListener() {

					@Override
					public void onChosen(int godChosen, int teamChosen) {
						god = godChosen;
						team = teamChosen;
					}
				});
				
				popup.show();
			}
		});
		
		friendButton = ComponentFactory.createButton("No friend :(", new ClickListener() {
			@Override
			public void click(Actor actor, float x, float y) {
				Popup popup = new InvitePopup(new InvitePopupHandler() {
					@Override
					public void friendSelected(String friend) {
						// TODO something with this friend of ours.
						Main.getInstance().friends.createFriend(friend);
						Main.getInstance().friends.saveFriends();
						friendButton.setText(friend);
					}
					
				}, "Select or write friend's username");
				popup.show();	
			}
		});
		
		settings.add("Invite friend");
		settings.row();
		settings.add(new MapSelecter()).fillX().expandX();
		settings.row();
		settings.add(friendButton).height(BUTTON_HEIGHT).fillX().expandX();
		settings.row();
		settings.add(fog).fillX().expandX();
		settings.row();
		settings.add(godButton).height(BUTTON_HEIGHT).fillX().expandX();
		        
		TextButton goButton = ComponentFactory.createMenuButton( "GO!",new ClickListener() {
			@Override
		    public void click(Actor actor,float x,float y ){
				
				int mapChoice = mapSelecter.getMapSelection();
				
				switch(mapSelecter.getMapSelection()){
					case MapSelecter.GENERATED_MAP: {
						//Main.getInstance().games.createHotseatGame(null, HotseatMenu.player1god, HotseatMenu.player1team, HotseatMenu.player2god, HotseatMenu.player2team).showGame(true);
						break;
					}
					case MapSelecter.RANDOM_MAP: {
						String[] maps = Assets.getMapList(false, true);
						//Main.getInstance().games.createHotseatGame(Assets.loadMap(HotseatMenu.getRandom(maps)), HotseatMenu.player1god, HotseatMenu.player1team, HotseatMenu.player2god, HotseatMenu.player2team).showGame(true);
						break;
					}
					case MapSelecter.SPECIFIC_MAP: {
						String mapName = mapSelecter.getMapName();
						
						//Main.getInstance().games.createHotseatGame(Assets.loadMap(mapName), HotseatMenu.player1god, HotseatMenu.player1team, HotseatMenu.player2god, HotseatMenu.player2team).showGame(true);
						break;
					}
					default: {
						// No map set, potentially show error message
						break;
					}
				}
		   }
		});
		
		contents.padTop((int) (3*Main.percentHeight)).center();
		contents.defaults().center().space((int)Main.percentWidth).pad(0).top().width(BUTTON_WIDTH);
		contents.add(settings);
		contents.row();
		contents.add(goButton).height(BUTTON_HEIGHT).width(BUTTON_WIDTH).padBottom((int) (BUTTON_HEIGHT*1.3));
		contents.row();
		Table centerLogin = new Table(Assets.SKIN);
		centerLogin.add(ComponentFactory.createLoginLabel());
		contents.row();
		contents.add().expandY();
		contents.row();
		contents.add(centerLogin);
	}
	
	public static synchronized InviteMatchMenu getInstance() {
		if(me == null){
			me = new InviteMatchMenu();
		}
		return me;
	}
	
	@Override
	public void dispose() {
		super.dispose();
		friendButton = null;
		me = null;
	}
	
	public void setFriend(String text) {
		friendButton.setText(text);
	}
}
