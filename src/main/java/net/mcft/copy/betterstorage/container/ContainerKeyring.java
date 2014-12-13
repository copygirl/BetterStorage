package net.mcft.copy.betterstorage.container;

import net.mcft.copy.betterstorage.inventory.InventoryKeyring;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

//@InventoryContainer
public class ContainerKeyring extends ContainerBetterStorage {
	
	private final int protectedIndex;
	
	public ContainerKeyring(EntityPlayer player, String title, int protectedIndex) {
		super(player, new InventoryKeyring(player, title), 9, 1);
		this.protectedIndex = protectedIndex;
		// We need to do this here instead of the super constructor,
		// because otherwise we wouldn't be able to set protectedIndex.
		super.setupInventoryContainer();
		super.setupInventoryPlayer();
	}
	
	@Override
	protected void setupInventoryContainer() {  }
	@Override
	protected void setupInventoryPlayer() {  }
	
	@Override
	protected Slot addSlotToContainer(Slot slot) {
		if ((slot.inventory == player.inventory) &&
		    (slot.getSlotIndex() == protectedIndex))
			((SlotBetterStorage)slot).setProtected();
		return super.addSlotToContainer(slot);
	}
	
}
