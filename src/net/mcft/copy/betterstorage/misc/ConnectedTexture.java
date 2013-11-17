package net.mcft.copy.betterstorage.misc;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;

public abstract class ConnectedTexture {
	
	private static final String[] iconNames = {
		"all", "none",
		"t", "b", "l", "r",
		"tl", "tr", "bl", "br",
		"tlr", "tbl", "tbr", "blr",
		"tb", "lr"
	};
	
	private static final int[][] connectedLookup = {
		{ 2, 3, 4, 5 },
		{ 2, 3, 4, 5 },
		{ 1, 0, 5, 4 },
		{ 1, 0, 4, 5 },
		{ 1, 0, 3, 2 },
		{ 1, 0, 2, 3 },
	};
	
	private Map<String, Icon> icons = new HashMap<String, Icon>();
	
	public void registerIcons(IconRegister iconRegister, String format) {
		for (String name : iconNames)
			icons.put(name, iconRegister.registerIcon(String.format(format, name)));
	}
	
	public Icon getIcon(String name) {
		return icons.get(name);
	}
	
	public Icon getConnectedIcon(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
		boolean top    = canConnect(world, x, y, z, side, ForgeDirection.getOrientation(connectedLookup[side.ordinal()][0]));
		boolean bottom = canConnect(world, x, y, z, side, ForgeDirection.getOrientation(connectedLookup[side.ordinal()][1]));
		boolean left   = canConnect(world, x, y, z, side, ForgeDirection.getOrientation(connectedLookup[side.ordinal()][2]));
		boolean right  = canConnect(world, x, y, z, side, ForgeDirection.getOrientation(connectedLookup[side.ordinal()][3]));
		
		StringBuilder iconName = new StringBuilder();
		if (!top) iconName.append('t');
		if (!bottom) iconName.append('b');
		if (!left) iconName.append('l');
		if (!right) iconName.append('r');
		
		return getIcon((iconName.length() <= 0 ? "none" : (iconName.length() >= 4 ? "all" : iconName.toString())));
	}
	
	public abstract boolean canConnect(IBlockAccess world, int x, int y, int z, ForgeDirection side, ForgeDirection connected);
	
}
