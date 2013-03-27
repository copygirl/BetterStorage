package net.mcft.copy.betterstorage.block.crate;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.mcft.copy.betterstorage.BetterStorage;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/** Holds all CratePileData objects for one world / dimension. */
public class CratePileCollection {
	
	// FIXME: Forge "detected leaking worlds in memory". Investigate.
	
	private static final int maxCount = Short.MAX_VALUE;
	private static Map<Integer, CratePileCollection> collectionMap = new HashMap<Integer, CratePileCollection>();
	
	private final World world;
	private final int dimension;
	
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
	
	/** Saves the pile data to file. */
	// Gets saved to <world>[/<dimension>]/crates/<id>.dat in uncompressed NBT.
	public void save(CratePileData pileData) {
		try {
			File file = getSaveFile(pileData.id);
			File tempFile = getTempSaveFile(pileData.id);
			file.getParentFile().mkdir();
			NBTTagCompound root = new NBTTagCompound();
			root.setCompoundTag("data", pileData.toCompound());
			CompressedStreamTools.write(root, tempFile);
			if (file.exists() && !file.delete())
				throw new Exception(file + " could not be deleted.");
			tempFile.renameTo(file);
		} catch (Exception e) {
			System.err.println("[BetterStorage] Error saving CratePileData: " + e);
			e.printStackTrace();
		}
	}
	
	/** Try to load a pile data from a file. */
	public CratePileData load(int id) {
		try {
			File file = getSaveFile(id);
			if (!file.exists()) return null;
			NBTTagCompound root = CompressedStreamTools.read(file);
			return CratePileData.fromCompound(this, root.getCompoundTag("data"));
		} catch (Exception e) {
			System.err.println("[BetterStorage] Error loading CratePileData: " + e);
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
	private File getTempSaveFile(int id) {
		return new File(getSaveDirectory(), id + ".tmp");
	}
	
	/** Marks the pile data as dirty so it gets saved. */
	public void markDirty(CratePileData pileData) {
		dirtyPiles.add(pileData);
	}
	
	/** Saves all piles which are marked as dirty. */
	public static void saveAll(World world) {
		int dimension = world.getWorldInfo().getDimension();
		if (!collectionMap.containsKey(dimension)) return;
		CratePileCollection collection = getCollection(world);
		for (CratePileData pileData : collection.dirtyPiles)
			collection.save(pileData);
		collection.dirtyPiles.clear();
	}
	
	/** Called when the world unloads, removes the
	 *  crate pile connection from the collection map. */
	public static void unload(World world) {
		int dimension = world.getWorldInfo().getDimension();
		collectionMap.remove(dimension);
	}
	
}
