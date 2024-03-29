package com.anstrat.gui;

import java.util.ArrayList;

import com.anstrat.core.Assets;
import com.anstrat.core.Assets.HexagonMesh;
import com.anstrat.geography.TerrainType;
import com.anstrat.geography.Tile;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Will not include calculation of bounds.
 */
public class GTile extends GObject {
	
	//Colors for different kinds of highlights
	public static final Color COLOR_TILE  = new Color(0.6f, 0.6f, 0.6f, 1f);
	public static final Color COLOR_ENEMY = Color.WHITE; //Color.RED; //Mak enemies with red?
	public static final Color COLOR_OFF   = Color.WHITE;
	public static final boolean RENDER_OUTLINE = true;
	
	public static final float ANIMATION_SPEED = 0.4f;
	
	public Color highlightColor;
	public final Tile tile;
	private HexagonMesh mesh;
	
	private final Rectangle bounds;
	private final float sideLength;
	
	public GTile(Vector2 screenPos, Tile tile, float sideLength, float width, float height){
		this.bounds = new Rectangle();
		this.sideLength = sideLength;
		this.tile = tile;
		this.bounds.width = width;
		this.bounds.height = height;
		setPosition(screenPos);
		highlightColor = COLOR_OFF;
		
		setTexture(tile.terrain);
	}
	
	/**
	 * Changes GTile texture. Note: Does not change the tile itself.
	 */
	public void setTexture(TerrainType terrainType){
		mesh = Assets.getHexagonMesh(terrainType, GEngine.FLAT_TILE_ORIENTATION);
	}
		
	public void render(GL10 gl){
		gl.glColor4f(highlightColor.r, highlightColor.g, highlightColor.b, highlightColor.a);
		
		gl.glPushMatrix();
		gl.glTranslatef(this.bounds.width/2f + this.bounds.x, this.bounds.height/2f + this.bounds.y, 0f);
		gl.glScalef(sideLength, sideLength, sideLength);
		
		gl.glEnable(GL10.GL_TEXTURE_2D);
		
		mesh.texture.bind();
		mesh.render(GL10.GL_TRIANGLES, 0, 12);
		
		gl.glPopMatrix();
	}
	
	public void setHighlight(int type){
		switch(type){
		case Highlighter.HIGHLIGHT_OFF: 
			highlightColor = COLOR_OFF;
			break;
		case Highlighter.HIGHLIGHT_TILE:
			highlightColor = COLOR_TILE;
			break;
		case Highlighter.HIGHLIGHT_ENEMY:
			highlightColor = COLOR_ENEMY;
			break;
		default:
			highlightColor = COLOR_OFF;
		}
	}
	
	/**
	 * Renders the outline of a hexagonal tile
	 * @param gl		Renderer
	 * @param color		Color of outline
	 * @param width		Width of outline
	 */
	public void renderOutline(GL10 gl, Color color, float thickness)
	{
		if(RENDER_OUTLINE){
			gl.glPushMatrix();
			gl.glTranslatef(this.bounds.width/2f + this.bounds.x, this.bounds.height/2f + this.bounds.y, 0f);
			gl.glScalef(sideLength, sideLength, sideLength);
			
			gl.glLineWidth(thickness*Gdx.graphics.getDensity());
			if(GEngine.getInstance().USE_SMOOTH_LINES){
				gl.glEnable(GL10.GL_LINE_SMOOTH);
			}else{
				gl.glDisable(GL10.GL_LINE_SMOOTH);
			}
			gl.glColor4f(color.r, color.g, color.b, color.a);
			gl.glDisable(GL10.GL_TEXTURE_2D); 
	
			mesh.render(GL10.GL_LINES, 12, 12);
			gl.glEnable(GL10.GL_TEXTURE_2D); 
			gl.glPopMatrix();
		}
	}

	/**
	 * Renders an individual edge outline of the tile.
	 * The edge param specifies the edge id (0-5).
	 * The number 0-5 corresponds to the edge according to the following schema: <br>
	 * Flat: SW, S, SE, NE, N, NW <br>
	 * Pointy: SW, W, SE, NE, E, SW
	 * @param edge
	 * @param gl the OpenGL context
	 * @param color
	 * @param thickness
	 */
	public void renderEdgeOutline(int edge, GL10 gl, Color color, float thickness){
		gl.glPushMatrix();
		gl.glTranslatef(this.bounds.width/2f + this.bounds.x, this.bounds.height/2f + this.bounds.y, 0f);
		gl.glScalef(sideLength, sideLength, sideLength);
		
		gl.glColor4f(color.r, color.g, color.b, color.a);
		gl.glLineWidth(thickness*Gdx.graphics.getDensity());
		if(GEngine.getInstance().USE_SMOOTH_LINES){
			gl.glEnable(GL10.GL_LINE_SMOOTH);
		}else{
			gl.glDisable(GL10.GL_LINE_SMOOTH);
		}
		gl.glDisable(GL10.GL_TEXTURE_2D); 
		//Assets.HEXAGON_MESH.render(GL10.GL_LINES, 12 + edge, 2);
		mesh.render(GL10.GL_LINES, 12 + edge * 2, 2);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glPopMatrix();
	}
	
	/**
	 * Sets the position of this tile's upper left corner to the given pixel coordinates.
	 * @param position
	 */
	public void setPosition(Vector2 position){
		this.bounds.x = position.x;
		this.bounds.y = position.y;
		boundingBoxOutdated = true;
	}
	
	/**
	 * @return the x,y coordinates of this tile's upper left corner.
	 */
	public Vector2 getPosition(){
		return new Vector2(this.bounds.x, this.bounds.y);
	}
	
	/**
	 * @return the x,y coordinates of this tile's center.
	 */
	public Vector2 getCenter(){
		return new Vector2(this.bounds.x + this.bounds.width / 2f, this.bounds.y + this.bounds.height / 2f);
	}
	
	/**
	 * Returns a list of all textures for a terrain type.
	 * @author Kalle
	 * @param terrainType
	 * @return
	 */
	public static TextureRegion[] getTextures(TerrainType terrainType) {
		ArrayList<TextureRegion> result = new ArrayList<TextureRegion>();
		
		for(String tex : terrainType.textures)
			result.add(Assets.getTextureRegion(tex));
				
		return result.toArray(new TextureRegion[result.size()]);
	}

	@Override
	protected Rectangle getBoundingRectangle() {
		return this.bounds;
	}
}
