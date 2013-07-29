package net.mcft.copy.betterstorage.misc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import net.mcft.copy.betterstorage.BetterStorage;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class Localizations {
	
	private Localizations() {  }
	
	public static void load() {
		
		File source = Loader.instance().getReversedModObjectList().get(BetterStorage.instance).getSource();
		Pattern pattern = Pattern.compile(".*(assets/" + Constants.modId + "/lang/(.+)\\.(.+))");
		for (String file : getResources(source, pattern)) {
			Matcher matcher = pattern.matcher(file);
			matcher.find();
			String resource = "/" + matcher.group(1);
			String lang = matcher.group(2);
			boolean isXML = matcher.group(3).equalsIgnoreCase("xml");
			LanguageRegistry.instance().loadLocalization(resource, lang, isXML);
			BetterStorage.log.info("Loaded localization file for '" + lang + "'.");
		}
		
	}
	
	private static Collection<String> getResources(File file, Pattern pattern) {
		ArrayList<String> retval = new ArrayList<String>();
		if (file.isDirectory())
			retval.addAll(getResourcesFromDirectory(file, pattern));
		else retval.addAll(getResourcesFromJarFile(file, pattern));
		return retval;
	}
	
	private static Collection<String> getResourcesFromJarFile(File file, Pattern pattern) {
		ArrayList<String> retval = new ArrayList<String>();
		ZipFile zf;
		try { zf = new ZipFile(file); }
		catch (ZipException e) { throw new Error(e); }
		catch (IOException e) { throw new Error(e); }
		Enumeration<? extends ZipEntry> entries = zf.entries();
		while (entries.hasMoreElements()) {
			ZipEntry ze = entries.nextElement();
			String fileName = ze.getName();
			boolean accept = pattern.matcher(fileName).matches();
			if (accept) retval.add(fileName);
		}
		try { zf.close(); }
		catch (IOException e) { throw new Error(e); }
		return retval;
	}
	
	private static Collection<String> getResourcesFromDirectory(File directory, Pattern pattern) {
		ArrayList<String> retval = new ArrayList<String>();
		File[] fileList = directory.listFiles();
		for (File file : fileList) {
			if (file.isDirectory()) {
				retval.addAll(getResourcesFromDirectory(file, pattern));
			} else try {
				String fileName = file.getCanonicalPath().replace('\\', '/');
				boolean accept = pattern.matcher(fileName).matches();
				if (accept) retval.add(fileName);
			} catch (final IOException e) { throw new Error(e); }
		}
		return retval;
	}
	
}
