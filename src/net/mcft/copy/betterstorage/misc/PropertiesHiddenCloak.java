package net.mcft.copy.betterstorage.misc;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class PropertiesHiddenCloak implements IExtendedEntityProperties {
	
	public static final String identifier = "BetterStorage.HiddenCloak";
	
	public String cloakUrl;
	
	// Since this doesn't get saved to file, we don't actually need those.
	@Override public void init(Entity entity, World world) {  }
	@Override public void saveNBTData(NBTTagCompound compound) {  }
	@Override public void loadNBTData(NBTTagCompound compound) {  }
	
}
