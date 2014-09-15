package net.mcft.copy.betterstorage.client.gui;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiItemList extends Gui implements IItemDnD {
	
	private ItemDnDHandler handler;
	public int xCoord, yCoord, width, height;
	private int offset;
	private boolean itemHover;
	private List<Item> items;
	private Minecraft mc;
	
	public GuiItemList(ItemDnDHandler handler, List<Item> items, int xCoord, int yCoord, int width, int height) {
		this.xCoord = xCoord;
		this.yCoord = yCoord;
		this.width = width;
		this.height = height;
		this.items = items;
		this.handler = handler;
		this.handler.addItemDnd(this);
		this.mc = Minecraft.getMinecraft();
	}
	
	public void draw(int x, int y) {
		
		drawHorizontalLine(xCoord, xCoord + width, yCoord, 0xFF000000);
		drawHorizontalLine(xCoord, xCoord + width, yCoord + height, 0xFF000000);
		drawVerticalLine(xCoord, yCoord, yCoord + height, 0xFF000000);
		drawVerticalLine(xCoord + width, yCoord, yCoord + height, 0xFF000000);
		
		ScaledResolution scaledresolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
		int guiScale = scaledresolution.getScaleFactor();
		
		int itemsPerRow = width / 16;
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		RenderHelper.enableGUIStandardItemLighting();
		GL11.glScissor(xCoord * guiScale, yCoord * guiScale, width * guiScale, height * guiScale);
		offset = 0;
		for(int i = 0; i < items.size(); i++) {
			int y2 = i / itemsPerRow * 16;
			int x2 = i % itemsPerRow * 16;
			try {
				RenderItem.getInstance().renderItemIntoGUI(mc.fontRenderer, mc.renderEngine, new ItemStack(items.get(i)), xCoord + x2, yCoord + y2 - offset);
			} catch (Exception e) {}
		}
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}
	
	@Override
	public boolean onItemDropped(int x, int y, Item item) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onItemPicked(int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Item getPickedItem(int x, int y) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onHover(int x, int y, Item item) {
		if(checkHover(x, y) && canDropItem(item)) {
			itemHover = true;
		}
		itemHover = false;
	}
	
	private boolean checkHover(int x, int y) {
		if(x >= xCoord && y >= yCoord && x <= xCoord + width && y <= yCoord + height) return true;
		else return false;
	}
	
	private boolean canDropItem(Item item) {
		if(item == null) return false;
		return !items.contains(item);
	}
	
	@SideOnly(Side.CLIENT)
	public static class ItemDnDHandler {
		
		private LinkedList<IItemDnD> children = new LinkedList<IItemDnD>();
		private Item current;
		private Minecraft mc;
		
		public ItemDnDHandler() {
			this.mc = Minecraft.getMinecraft();
		}
		
		public void addItemDnd(IItemDnD dnd) {
			children.add(dnd);
		}
		
		public void draw(int x, int y) {
			if(current != null) {
				RenderHelper.enableGUIStandardItemLighting();
				RenderItem.getInstance().renderItemIntoGUI(mc.fontRenderer, mc.renderEngine, new ItemStack(current), x, y);
				RenderHelper.disableStandardItemLighting();
			}
			for(IItemDnD dnd : children) {
				dnd.onHover(x, y, current);
			}
		}
		
		public void onMousePressed(int x, int y) {
			for(IItemDnD dnd : children) {
				Item item = dnd.getPickedItem(x, y);
				if(item != null) {
					current = item;
				}
			}
		}
		
		public void onMouseReleased(int x, int y) {
			if(current != null) {
				for(IItemDnD dnd : children) {
					if(dnd.onItemDropped(x, y, current)) {
						current = null;
					}
				}
			}
		}
	}
}
