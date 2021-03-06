package hu.gpsoft.device.object.tiles;

import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.object.Game;
import com.mrcrayfish.device.object.Game.Layer;
import hu.gpsoft.device.api.utils.RenderUtil;

public class TileWheat extends Tile
{
	public TileWheat(int id, int x, int y)
	{
		super(id, x, y);
	}

	@Override
	public void render(Game game, int x, int y, Layer layer)
	{
		RenderUtil.drawRectWithTexture(game.xPosition + x * WIDTH, game.yPosition + y * HEIGHT - 6, this.x * 16, this.y * 16, WIDTH, HEIGHT + 1, 16, 16);
		RenderUtil.drawRectWithTexture(game.xPosition + x * WIDTH, game.yPosition + y * HEIGHT - 2, this.x * 16, this.y * 16, WIDTH, HEIGHT + 1, 16, 16);
	}
	
	@Override
	public boolean isFullTile()
	{
		return false;
	}
}
