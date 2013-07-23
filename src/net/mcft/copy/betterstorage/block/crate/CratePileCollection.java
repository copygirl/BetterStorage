package net.mcft.copy.betterstorage.block.crate;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.utils.RandomUtils;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

/** Holds all CratePileData objects for one world / dimension. */
public class CratePileCollection {
	
	private static final int maxCount = Short.MAX_VALUE;
	private static Map<Integer, CratePileCollection> collectionMap = new HashMap<Integer, CratePileCollection>();
	
	private final World world;
	private final int dimension;
	
	private int count = RandomUtils.getInt(maxCount);
	private Map<Integer, CratePileData> pileDataMap = new HashMap<Integer, CratePileData>();
	private Set<CratePileData> dirtyPiles = new HashSet<CratePileData>();
	
	/** Hands out the set of dirty piles */
	public Set<CratePileData> getDirtyPiles() { return dirtyPiles; }
	
	public CratePileCollection(World world) {
		this.world = world;
		this.dimension = world.provider.dimensionId;
	}
	
	/** Gets or creates a CratePileCollection for the world. */
	public static CratePileCollection getCollection(World world) {
		int dimension = world.provider.dimensionId;
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
			BetterStorage.log.warning("Error saving CratePileData: " + e);
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
			BetterStorage.log.warning("Error loading CratePileData: " + e);
			e.printStackTrace();
			return null;
		}
	}
	
	private File getSaveDirectory() {
		File saveFolder = DimensionManager.getCurrentSaveRootDirectory();
		String dimensionFolderName = world.provider.getSaveFolder();
		if (dimensionFolderName != null)
			saveFolder = new File(DimensionManager.getCurrentSaveRootDirectory(), dimensionFolderName);
		return new File(saveFolder, "data" + File.separator + "crates");
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
		int dimension = world.provider.dimensionId;
		if (!collectionMap.containsKey(dimension)) return;
		CratePileCollection collection = getCollection(world);
		for (CratePileData pileData : collection.dirtyPiles)
			collection.save(pileData);
		collection.dirtyPiles.clear();
	}
	
	/** Called when the world unloads, removes the
	 *  crate pile connection from the collection map. */
	public static void unload(World world) {
		// Unload is called before save. This breaks saving.
		// Disabling this until there is something better isn't too bad.
		//int dimension = world.provider.dimensionId;
		//collectionMap.remove(dimension);
	}
	
}
