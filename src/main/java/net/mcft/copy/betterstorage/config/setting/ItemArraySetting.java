package net.mcft.copy.betterstorage.config.setting;

import static cpw.mods.fml.client.config.GuiUtils.RESET_CHAR;
import static cpw.mods.fml.client.config.GuiUtils.UNDO_CHAR;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.mcft.copy.betterstorage.client.gui.GuiItemList;
import net.mcft.copy.betterstorage.client.gui.GuiItemList.ItemDnDHandler;
import net.mcft.copy.betterstorage.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.Constants.NBT;
import cpw.mods.fml.client.config.ConfigGuiType;
import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.GuiConfigEntries;
import cpw.mods.fml.client.config.GuiConfigEntries.ListEntryBase;
import cpw.mods.fml.client.config.GuiUnicodeGlyphButton;
import cpw.mods.fml.client.config.HoverChecker;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemArraySetting extends ArrayPropertySetting<String> {

	public ItemArraySetting(Config config, String fullName, boolean isListFixedLength, int maxListLength) {
		super(config, fullName, new String[0], ConfigGuiType.STRING, isListFixedLength, maxListLength, null);
		setCustomListEntryClass(ItemConfigEntry.class);
	}

	@Override
	public void load(Configuration config) {
		values = config.get(category, langKey, defaultValues, comment).getStringList();
	}

	@Override
	public void save(Configuration config) {
		config.get(category, langKey, defaultValues, comment).set(values);
	}	

	@Override
	public void read(NBTTagCompound compound) {
		NBTTagList list = compound.getTagList(fullName, NBT.TAG_STRING);
		values = new String[list.tagCount()];
		for(int i = 0; i < list.tagCount(); i++) {
			values[i] = list.getStringTagAt(i);
		}
	}

	@Override
	public void write(NBTTagCompound compound) {
		NBTTagList list = compound.getTagList(fullName, NBT.TAG_STRING);
		for(String s : values) {
			list.appendTag(new NBTTagString(s));
		}
		compound.setTag(fullName, list);
	}
	
	public boolean contains(Item item) {
		String name = item.getUnlocalizedName();
		for(String s : values) {
			if(s.equals(name)) return true;
		}
		return false;
	}
	
	public void add(Item item) {
		if(contains(item)) return;
		values = Arrays.copyOf(values, values.length + 1);
		values[values.length - 1] = GameData.getItemRegistry().getNameForObject(item);
	}
	
	public void remove(Item item) {
		if(!contains(item)) return;
		String name = GameData.getItemRegistry().getNameForObject(item);
		String[] tmp = new String[values.length - 1];
		int i = 0;
		int j = 0;
		while(i < values.length) {
			if(!values[i].equals(name)) {
				tmp[j] = values[i];
				j++;
			}
			i++;
		}
		values = tmp;
	}
	
	public static class ItemConfigEntry extends ListEntryBase {
		
		private ItemArraySetting currentSetting;
		private String[] currentValue;
		private int offset;
		
		private GuiButtonExt buttonInc;
		private GuiButtonExt buttonDec;
		
		public ItemConfigEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) {
			super(owningScreen, owningEntryList, configElement);
			this.currentSetting = (ItemArraySetting) configElement;
			this.currentValue = currentSetting.values.clone();
			
			buttonInc = new GuiButtonExt(0, 0, 0, 10, 18, ">");
			buttonDec = new GuiButtonExt(0, 0, 0, 10, 18, "<");
			refresh();
		}
		
		private int width, amount;
		
		@Override
		public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, Tessellator tessellator, int mouseX, int mouseY, boolean isSelected) {
			super.drawEntry(slotIndex, x, y, listWidth, slotHeight, tessellator, mouseX, mouseY, isSelected);
			
			buttonDec.xPosition = owningEntryList.controlX;
			buttonDec.yPosition = y;
			
			buttonInc.xPosition = owningEntryList.controlX + owningEntryList.controlWidth - 10;
			buttonInc.yPosition = y;
			
			buttonInc.drawButton(mc, mouseX, mouseY);
			buttonDec.drawButton(mc, mouseX, mouseY);
			
			width = owningEntryList.controlWidth - 22;
			amount = width / 16;
			int o = width % 16 / 2 + owningEntryList.controlX;
			
			String ttp = null;
			for(int i = offset; (i < amount + offset && i < currentValue.length); i++) {
				Item item = GameData.getItemRegistry().getObject(currentValue[i]);
				int x2 = o + 11 + (i - offset) * 16;
				if(item != null && item.getCreativeTab() != null) {
					try {
						RenderHelper.enableGUIStandardItemLighting();
						RenderItem.getInstance().renderItemIntoGUI(mc.fontRenderer, mc.renderEngine, new ItemStack(item), x2, y);
						RenderHelper.disableStandardItemLighting();
					} catch (Exception e) {}
				}
				if(mouseX >= x2 && mouseY >= y && mouseX <= x2 + 16 && mouseY <= y + 16) {
					ttp = item != null ? I18n.format(item.getUnlocalizedName() + ".name") : currentValue[i];
				}
			}
			if(ttp != null) owningScreen.drawToolTip(Arrays.asList(ttp), mouseX, mouseY);
			RenderHelper.disableStandardItemLighting();
		}
		@Override
		public boolean mousePressed(int index, int x, int y, int mouseEvent, int relativeX, int relativeY) {
			if(buttonInc.mousePressed(mc, x, y)) {
				buttonInc.func_146113_a(mc.getSoundHandler());
				offset++;
				int max = currentValue.length - amount;
				if(offset > max) offset = max;
				if(offset == max) buttonInc.enabled = false;
				if(offset != 0) buttonDec.enabled = true;
			} else if(buttonDec.mousePressed(mc, x, y)) {
				buttonInc.func_146113_a(mc.getSoundHandler());
				offset--;
				int max = currentValue.length - amount;
				if(offset < 0) offset = 0;
				if(offset == 0) buttonDec.enabled = false;
				if(offset != max) buttonInc.enabled = true;
			} else {
				mc.displayGuiScreen(new ItemArrayGui(owningScreen, this));
			}
			return super.mousePressed(index, x, y, mouseEvent, relativeX, relativeY);
		}

		@Override
		public void mouseReleased(int index, int x, int y, int mouseEvent, int relativeX, int relativeY) {
			buttonInc.mouseReleased(x, y);
			buttonDec.mouseReleased(x, y);
			super.mouseReleased(index, x, y, mouseEvent, relativeX, relativeY);
		}

		@Override
		public boolean isDefault() {
			return Arrays.equals(currentValue, currentSetting.defaultValues);
		}

		@Override
		public void setToDefault() {
			currentValue = currentSetting.defaultValues.clone();
			currentSetting.values = currentSetting.defaultValues.clone();
		}

		@Override
		public boolean isChanged() {
			return !Arrays.equals(currentValue, currentSetting.values);
		}

		@Override
		public void undoChanges() {
			currentValue = currentSetting.values.clone();
		}

		@Override
		public boolean saveConfigElement() {
			if(isChanged() && currentSetting.requiresMcRestart) {
				currentSetting.values = currentValue.clone();
				return true;
			}
			return false;
		}

		@Override
		public Object getCurrentValue() {
			return null;
		}

		@Override
		public Object[] getCurrentValues() {
			return currentValue;
		}

		@Override
		public void keyTyped(char eventChar, int eventKey) { }

		@Override
		public void updateCursorCounter() { }

		@Override
		public void mouseClicked(int x, int y, int mouseEvent) {}
		
		public void refresh() {
			offset = 0;
			buttonDec.enabled = false;	
			width = owningEntryList.controlWidth - 22;
			amount = width / 16;
			int max = currentValue.length - amount;
			if(max <= 0) buttonInc.enabled = false;
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static class ItemArrayGui extends GuiScreen {
		
		private GuiConfig owningScreen;
		
		private ItemDnDHandler dndHandler;
		private GuiItemList itemList;
		private GuiItemList selectedList;
		private ItemConfigEntry entry;
		
		private ArrayList<Item> selectedItems;
		private ArrayList<Item> allItems;
		
		private List toolTip;
		private String title;
		private String titleLine2;
		private String titleLine3;
		private HoverChecker tooltipHoverChecker;
		private GuiButtonExt btnUndoChanges, btnDefault, btnDone;
		
		public ItemArrayGui(GuiConfig owningScreen, ItemConfigEntry setting) {
			this.entry = setting;
			this.owningScreen = owningScreen;
			this.mc = Minecraft.getMinecraft();
			
			dndHandler = new ItemDnDHandler();
			
			selectedItems = new ArrayList<Item>();
			allItems = new ArrayList<Item>();
			
			for(String s : this.entry.currentValue) {	
				selectedItems.add(GameData.getItemRegistry().getObject(s));
			}
			for(Object o : GameData.getItemRegistry().getKeys()) {
				Item item = (Item) GameData.getItemRegistry().getObject(o);
				System.out.println(item.getUnlocalizedName());
				if(item.getCreativeTab() != null) allItems.add(item);
			}
			
			this.toolTip = new ArrayList();
    
			String propName = I18n.format(entry.currentSetting.getLanguageKey());
			String comment = I18n.format(entry.currentSetting.getLanguageKey() + ".tooltip");

			if(!comment.equals(entry.currentSetting.getLanguageKey() + ".tooltip"))
				toolTip = mc.fontRenderer.listFormattedStringToWidth(
					EnumChatFormatting.GREEN + propName + "\n" + EnumChatFormatting.YELLOW + comment, 300);
			else if(entry.currentSetting.getComment() != null && !entry.currentSetting.getComment().trim().isEmpty())
				toolTip = mc.fontRenderer.listFormattedStringToWidth(
					EnumChatFormatting.GREEN + propName + "\n" + EnumChatFormatting.YELLOW + entry.currentSetting.getComment(), 300);
			else
				toolTip = mc.fontRenderer.listFormattedStringToWidth(
					EnumChatFormatting.GREEN + propName + "\n" + EnumChatFormatting.RED + "No tooltip defined.", 300);
		
		
			this.title = owningScreen.title;
			if(owningScreen.titleLine2 != null) {
				this.titleLine2 = owningScreen.titleLine2;
				this.titleLine3 = I18n.format(entry.currentSetting.getLanguageKey());
			} else this.titleLine2 = I18n.format(entry.currentSetting.getLanguageKey());
			
			this.tooltipHoverChecker = new HoverChecker(28, 37, 0, owningScreen.width, 800);
		}

		@Override
		public void initGui() {
			
			int undoGlyphWidth = mc.fontRenderer.getStringWidth(UNDO_CHAR) * 2;
			int resetGlyphWidth = mc.fontRenderer.getStringWidth(RESET_CHAR) * 2;
			int doneWidth = Math.max(mc.fontRenderer.getStringWidth(I18n.format("gui.done")) + 20, 100);
			int undoWidth = mc.fontRenderer.getStringWidth(" " + I18n.format("fml.configgui.tooltip.undoChanges")) + undoGlyphWidth + 20;
			int resetWidth = mc.fontRenderer.getStringWidth(" " + I18n.format("fml.configgui.tooltip.resetToDefault")) + resetGlyphWidth + 20;
			int buttonWidthHalf = (doneWidth + 5 + undoWidth + 5 + resetWidth) / 2;
			this.buttonList.add(btnDone = new GuiButtonExt(2000, this.width / 2 - buttonWidthHalf, this.height - 29, doneWidth, 20, I18n.format("gui.done")));
			this.buttonList.add(btnDefault = new GuiUnicodeGlyphButton(2001, this.width / 2 - buttonWidthHalf + doneWidth + 5 + undoWidth + 5,
				this.height - 29, resetWidth, 20, " " + I18n.format("fml.configgui.tooltip.resetToDefault"), RESET_CHAR, 2.0F));
			this.buttonList.add(btnUndoChanges = new GuiUnicodeGlyphButton(2002, this.width / 2 - buttonWidthHalf + doneWidth + 5,
				this.height - 29, undoWidth, 20, " " + I18n.format("fml.configgui.tooltip.undoChanges"), UNDO_CHAR, 2.0F));
			
			itemList = new GuiItemList(dndHandler, allItems, 20, 50, width / 2 - 25, height - 100);
			selectedList = new GuiItemList(dndHandler, selectedItems,  width / 2 + 1, 50, width / 2 - 25, height - 100);
		}

		@Override
		public void drawScreen(int x, int y, float partialTicks) {
			
			this.btnDefault.enabled = !this.entry.isDefault();
			this.btnUndoChanges.enabled = this.entry.isChanged();
			
	        drawDefaultBackground();
			super.drawScreen(x, y, partialTicks);
			
			this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 8, 0xFFFFFF);
			
			if(this.titleLine2 != null)
				this.drawCenteredString(this.fontRendererObj, this.titleLine2, this.width / 2, 18, 0xFFFFFF);
			if(this.titleLine3 != null)
				this.drawCenteredString(this.fontRendererObj, this.titleLine3, this.width / 2, 28, 0xFFFFFF);
			
			itemList.draw(x, y);
			selectedList.draw(x, y);
			dndHandler.draw(x, y);
			
			if(this.tooltipHoverChecker != null && this.tooltipHoverChecker.checkHover(x, y))
				this.func_146283_a(this.toolTip, x, y);
		}
		
		@Override
		protected void actionPerformed(GuiButton button) {
			if(button.id == 2000) {
				String[] list = new String[selectedItems.size()];
				for(int i = 0; i < selectedItems.size(); i++) {
					list[i] = GameData.getItemRegistry().getNameForObject(selectedItems.get(i));
				}
				this.entry.currentValue = list;
				this.entry.refresh();
				this.entry.saveConfigElement();
				this.mc.displayGuiScreen(this.owningScreen);
			} else if(button.id == 2001) {
				this.entry.setToDefault();
				this.entry.refresh();
				selectedItems.clear();
				for(String s : this.entry.currentValue) {	
					selectedItems.add(GameData.getItemRegistry().getObject(s));
				}
			} else if (button.id == 2002) {
				this.entry.undoChanges();
				this.entry.refresh();
				selectedItems.clear();
				for(String s : this.entry.currentValue) {	
					selectedItems.add(GameData.getItemRegistry().getObject(s));
				}
			}
		}

		@Override
		protected void keyTyped(char c, int key) {
			if(key == 1) {
				mc.displayGuiScreen(owningScreen);
			}
		}
	}
}
