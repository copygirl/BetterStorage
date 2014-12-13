package net.mcft.copy.betterstorage.item.cardboard;

import java.util.List;

import net.mcft.copy.betterstorage.api.crafting.ContainerInfo;
import net.mcft.copy.betterstorage.api.crafting.IRecipeInput;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RecipeInputCardboard implements IRecipeInput {
	
	public static final RecipeInputCardboard instance = new RecipeInputCardboard();
	
	private RecipeInputCardboard() {  }
	
	@Override
	public int getAmount() { return 1; }
	
	@Override
	public boolean matches(ItemStack stack) {
		return (stack.getItem() instanceof ICardboardItem);
	}
	
	@Override
	public void craft(ItemStack input, ContainerInfo containerInfo) {  }
	
	@Override
	@SideOnly(Side.CLIENT)
	public List<ItemStack> getPossibleMatches() { return null; }
	
}