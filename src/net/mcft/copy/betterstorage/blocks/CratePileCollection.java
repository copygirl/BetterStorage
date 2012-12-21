package net.mcft.copy.betterstorage.blocks;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.mcft.copy.betterstorage.BetterStorage;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

/** Holds all CratePileData objects for one world / dimension. */
public class CratePileCollection {
	
	private static final int maxCount = Short.MAX_VALUE;
	private static Map<Integer, CratePileCollection> collectionMap = new HashMap<Integer, CratePileCollection>();
	
	public final World world;
	public final int dimension;
	
	private int count = BetterStorage.random.nextInt(maxCount);
	private Map<Integer, CratePileData> pileDataMap = new HashMap<Integer, CratePileData>();
	private Set<CratePileData> dirtyPiles = new HashSet<CratePileData>();
	
	public CratePileCollection(World world) {
		this.world = world;
		this.dimension = world.getWorldInfo().getDimension();
	}
	
	/** Gets or creates a CratePileCollection for the world. */
	public static CratePileCollection getCollection(World world) {
		int dimension = world.getWorldInfo().getDimension();
		CratePileCollection collection;
		if (!collectionMap.containsKey(dimension)) {
			collection = new CratePileCollection(world);
			collectionMap.put(dimension, collection);
		} else collection = collectionMap.get(dimension);
		return collection;
	}
	
	private void removeCollectionFromMap() {
		collectionMap.remove(dimension);
	}
	
	/** Gets a crate pile from the collection, creates/loads it if necessary. */
	public CratePileData getCratePile(int id) {
		CratePileData pileData;
		if (!pileDataMap.containsKey(id)) {
			// Try to load the pile data.
			pileData = load(id);
			// If it doesn't exist or fail, create one.
			if (pileData == null)
				pileData = new CratePileData(this, id, 0);
			pileDataMap.put(id, pileData);
		} else pileData = pileDataMap.get(id);
		return pileData;
	}
	
	/** Creates and adds a new crate pile to this collection. */
	public CratePileData createCratePile() {
		int id = count;
		while (pileDataMap.containsKey(++count)) {  }
		CratePileData pileData = new CratePileData(this, id, 0);
		pileDataMap.put(id, pileData);
		return pileData;
	}
	
	/** Removes the crate pile from the collection. */
	public void removeCratePile(CratePileData pileData) {
		pileDataMap.remove(pileData.id);
		dirtyPiles.remove(pileData);
		getSaveFile(pileData.id).delete();
		if (pileDataMap.size() <= 0)
			removeCollectionFromMap();
	}
	
	/** Returns a new CratePileData with its contents read from the compound. */
	public CratePileData readPileData(NBTTagCompound compound) {
		int cratePileId = compound.getInteger("id");
		int numCrates = compound.getShort("numCrates");
		CratePileData pileData = new CratePileData(this, cratePileId, numCrates);
		NBTTagList stacks = compound.getTagList("stacks");
		for (int j = 0; j < stacks.tagCount(); j++) {
			NBTTagCompound stackCompound = (NBTTagCompound)stacks.tagAt(j);
			int id = stackCompound.getShort("id");
			int count = stackCompound.getInteger("Count");
			int damage = stackCompound.getShort("Damage");
			ItemStack stack = new ItemStack(id, count, damage);
			if (stackCompound.hasKey("tag"))
				stack.stackTagCompound = stackCompound.getCompoundTag("tag");
			if (stack.getItem() != null)
				pileData.addItems(stack);
		}
		return pileData;
	}
	
	/** Returns a new compound from the pileData and its contents. */
	public NBTTagCompound writePileData(CratePileData pileData) {
		NBTTagCompound compound = new NBTTagCompound("");
		compound.setInteger("id", pileData.id);
		compound.setShort("numCrates", (short)pileData.getNumCrates());
		NBTTagList stacks = new NBTTagList("stacks");
		for (ItemStack stack : pileData) {
			NBTTagCompound stackCompound = new NBTTagCompound("");
			stackCompound.setShort("id", (short)stack.itemID);
			stackCompound.setInteger("Count", stack.stackSize);
			stackCompound.setShort("Damage", (short)stack.getItemDamage());
			if (stack.hasTagCompound())
				stackCompound.setCompoundTag("tag", stack.getTagCompound());
			stacks.appendTag(stackCompound);
		}
		compound.setTag("stacks", stacks);
		return compound;
	}
	
	/** Saves the pile data to file. */
	// Gets saved to <world>[/<dimension>]/crates/<id>.dat in uncompressed NBT.
	public void save(CratePileData pileData) {
		try {
			File file = getSaveFile(pileData.id);
			file.getParentFile().mkdir();
			NBTTagCompound root = new NBTTagCompound();
			root.setCompoundTag("data", writePileData(pileData));
			CompressedStreamTools.write(root, file);
		} catch (Exception e) {
			System.out.println("[BetterStorage] Error saving CratePileData: " + e);
			e.printStackTrace();
		}
	}
	
	/** Try to load a pile data from a file. */
	public CratePileData load(int id) {
		try {
			File file = getSaveFile(id);
			if (!file.exists()) return null;
			NBTTagCompound root = CompressedStreamTools.read(file);
			return readPileData(root.getCompoundTag("data"));
		} catch (Exception e) {
			System.out.println("[BetterStorage] Error loading CratePileData: " + e);
			e.printStackTrace();
			return null;
		}
	}
	
	private File getSaveDirectory() {
		return new File(world.getSaveHandler().getMapFileFromName("crates").getParentFile(), "crates");
	}
	private File getSaveFile(int id) {
		return new File(getSaveDirectory(), id + ".dat");
	}
	
	/** Marks the pile data as dirty so it gets saved. */
	public void markDirty(CratePileData pileData) {
		dirtyPiles.add(pileData);
	}
	/** Saves all piles which are marked as dirty. */
	public void saveAll() {
		for (CratePileData pileData : dirtyPiles)
			save(pileData);
		dirtyPiles.clear();
	}
	/** Saves all piles which are marked as dirty. */
	public static void saveAll(World world) {
		int dimension = world.getWorldInfo().getDimension();
		if (!collectionMap.containsKey(dimension)) return;
		getCollection(world).saveAll();
	}
	
}
