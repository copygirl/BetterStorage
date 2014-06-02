package net.mcft.copy.betterstorage.tile;

import java.util.BitSet;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.utils.MiscUtils;
import net.mcft.copy.betterstorage.utils.ReflectionUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import cpw.mods.fml.common.registry.FMLControlledNamespacedRegistry;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;

public class TileBetterStorage extends Block {
	
	private String name;
	
	public TileBetterStorage(Material material) {
		
		super(material);
		
		setCreativeTab(BetterStorage.creativeTab);
		
		setBlockName(Constants.modId + "." + getTileName());
		registerBlock();
		
	}
	
	/** Returns the name of this tile, for example "craftingStation". */
	public String getTileName() {
		return ((name != null) ? name : (name = MiscUtils.getName(this)));
	}
	
	/** Returns the item class used for this block. <br>
	 *  Doesn't have to be an ItemBlock. */
	protected Class<? extends Item> getItemClass() { return ItemBlock.class; }
	
	/** Registers the block in the GameRegistry. */
	protected void registerBlock() {
		Class<? extends Item> itemClass = getItemClass();
		if (ItemBlock.class.isAssignableFrom(itemClass)) {
			GameRegistry.registerBlock(this, (Class<? extends ItemBlock>)itemClass, getTileName(), Constants.modId);
		} else {
			GameData mainData = (GameData)ReflectionUtils.get(GameData.class, null, "mainData");
			Block block = GameRegistry.registerBlock(this, null, getTileName(), Constants.modId);
			Item item;
			try { item = itemClass.getConstructor().newInstance(); }
			catch (Exception e) { throw new RuntimeException(e); }
			//FIXME: This doesn't work. The client fails to remap the ids. Possible fixes: a) make ItemBackpack extend ItemBlock or b) individually register ItemBackpack
			ReflectionUtils.invoke(FMLControlledNamespacedRegistry.class, GameData.itemRegistry, "add", new Class[]{int.class, String.class, Object.class, BitSet.class}, new Object[]{Block.getIdFromBlock(block), getTileName(), item, new BitSet()});	
		}
	}
	
}
