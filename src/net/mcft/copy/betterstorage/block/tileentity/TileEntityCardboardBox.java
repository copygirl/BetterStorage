package net.mcft.copy.betterstorage.block.tileentity;

public class TileEntityCardboardBox extends TileEntityContainer {
	
	/** Whether this cardboard box was picked up and placed down again. <br>
	 *  If so, the box won't drop any more, as it's only usable once. */
	public boolean moved = false;
	
	public boolean brokenInCreative = false;
	
	// TileEntityContainer stuff
	
	@Override
	public String getName() { return "container.cardboardbox"; }
	
	@Override
	public int getRows() { return 1; }
	
}
