package net.mcft.copy.betterstorage.misc;


//TODO (1.8): IIcons? What's that?
public class ConnectedTexture {
	
	/*private static final String[] iconNames = {
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
		{ 1, 0, 2, 3 },
		{ 1, 0, 3, 2 },
	};
	
	private Map<String, IIcon> icons = new HashMap<String, IIcon>();
	
	public void registerIcons(IIconRegister iconRegister, String format) {
		for (String name : iconNames)
			icons.put(name, iconRegister.registerIcon(String.format(format, name)));
	}
	
	public IIcon getIcon(String name) {
		return icons.get(name);
	}
	
	public IIcon getConnectedIcon(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
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
	*/
}
