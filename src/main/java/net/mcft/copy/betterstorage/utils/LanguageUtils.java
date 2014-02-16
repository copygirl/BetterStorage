package net.mcft.copy.betterstorage.utils;

import java.util.Arrays;
import java.util.List;

import net.mcft.copy.betterstorage.misc.Constants;
import net.minecraft.util.StatCollector;

public final class LanguageUtils {
	
	private LanguageUtils() {  }
	
	public static String translateTooltip(String thing, String... replacements) {
		if (thing == null) return null;
		if ((replacements.length % 2) != 0)
			throw new IllegalArgumentException("replacements must contain an even number of elements.");
		String translated = StatCollector.translateToLocal("tooltip." + Constants.modId + "." + thing);
		for (int i = 0; i < replacements.length; i += 2)
			translated = translated.replace(replacements[i], replacements[i + 1]);
		return translated;
	}
	
	public static void translateTooltip(List list, String thing, String... replacements) {
		if (thing == null) return;
		String translated = translateTooltip(thing, replacements);
		list.addAll(Arrays.asList(translated.split("#")));
	}
	
}
