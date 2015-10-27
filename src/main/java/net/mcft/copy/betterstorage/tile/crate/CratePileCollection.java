package net.mcft.copy.betterstorage.tile.crate;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.utils.RandomUtils;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/** Holds all CratePileData objects for one world / dimension. */
public class CratePileCollection {
	
	private static final int maxCount = Short.MAX_VALUE;
	private static Map<Integer, CratePileCollection> collectionMap = new HashMap<Integer, CratePileCollection>();
	
	public final World world;
	public final int dimension;
	
	private int count = RandomUtils.getInt(maxCount);
	private Map<Integer, CratePileData> pileDataMap = new HashMap<Integer, CratePileData>();
	
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
	
	/** Removes the crate pile from the collection, deletes the pile's file. */
	public void removeCratePile(CratePileData pileData) {
		pileDataMap.remove(pileData.id);
		getSaveFile(pileData.id).delete();
		if (pileDataMap.size() <= 0)
			collectionMap.remove(dimension);
	}
	
	/** Saves the pile data to file. */
	// Gets saved to <world>[/<dimension>]/data/crates/<id>.dat in uncompressed NBT.
	public void save(CratePileData pileData) {
		try {
			File file = getSaveFile(pileData.id);
			File tempFile = getTempSaveFile(pileData.id);
			file.getParentFile().mkdir();
			NBTTagCompound root = new NBTTagCompound();
			root.setTag("data", pileData.toCompound());
			CompressedStreamTools.write(root, tempFile);
			if (file.exists() && !file.delete())
				throw new Exception(file + " could not be deleted.");
			tempFile.renameTo(file);
		} catch (Exception e) {
			BetterStorage.log.warn("Error saving CratePileData: " + e);
			e.printStackTrace();
		}
	}
	
	/** Try to load a pile data from a file. */
	public CratePileData load(int id) {
		try {
			File file = getSaveFile(id);
			if (!file.exists()) return null;
			NBTTagCompound root = CompressedStreamTools.read(file);
			return CratePileData.fromCompound(this, id, root.getCompoundTag("data"));
		} catch (Exception e) {
			BetterStorage.log.warn("Error loading CratePileData: " + e);
			e.printStackTrace();
			return null;
		}
	}
	
	private File getSaveDirectory() {
		File saveFolder = world.getSaveHandler().getWorldDirectory();
		return new File(saveFolder, "data" + File.separator + "crates");
	}
	private File getSaveFile(int id) {
		return new File(getSaveDirectory(), id + ".dat");
	}
	private File getTempSaveFile(int id) {
		return new File(getSaveDirectory(), id + ".tmp");
	}
	
	/** Called when the world unloads, removes the
	 *  crate pile connection from the collection map. */
	public static void unload(World world) {
		int dimension = world.provider.dimensionId;
		collectionMap.remove(dimension);
	}
	
	/** Called every tick if the world is loaded. */
	public void onTick() {
		for (CratePileData data : pileDataMap.values())
			data.blockView.onUpdate();
	}
	
}
