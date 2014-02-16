package net.mcft.copy.betterstorage.addon.thaumcraft;

import java.util.Map;

import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;

public abstract class FluxEffect {
	
	public final Aspect aspect;
	
	public FluxEffect(thaumcraft.api.aspects.Aspect aspect) {
		this.aspect = aspect;
	}
	
	// Pick a random FluxEffect from the map for the flux that's currently in the aura.
	public static FluxEffect pick(Map<Aspect, FluxEffect> effects, World world, double x, double y, double z) {
		
		return null;
		/*
		// Get closest aura node.
		int auraId = ThaumcraftApi.getClosestAuraWithinRange(world, x, y, z, 640);
		if (auraId == -1) return null;
		AuraNode aura = ThaumcraftApi.getNodeCopy(auraId);
		
		// Get and count flux.
		Aspect[] aspects = aura.flux.getAspectsSortedAmount();
		int total = 0;
		for (int i = 0; i < aspects.length; i++)
			total += aura.flux.tags.get(aspects[i]);
		
		// The higher the flux is, the bigger the chance for a random effect.
		if (RandomUtils.getInt(48, 512) > total) return null;
		
		ObjectTags fluxReduce = new ObjectTags();
		FluxEffect effect = null;
		
		// Go through all flux aspects in the aura, from highest to lowest.
		for (int i = 0; i < aspects.length; i++) {
			Aspect aspect = aspects[i];
			int amount = aura.flux.tags.get(aspect);
			// If the flux from that aspect is high enough,
			// use the specific effect for it, if there is one.
			if (RandomUtils.getInt(48, 128) < amount)
				if ((effect = effects.get(aspect)) != null) {
					fluxReduce = (new ObjectTags()).add(aspect, -8);
					break;
				}
			fluxReduce.add(aspect, -Math.min(2, amount));
			if (fluxReduce.tags.size() >= 4) break;
		}
		
		// If we don't have an effect yet, pick a random one.
		if (effect == null) {
			int index = RandomUtils.getInt(effects.size()), i = 0;
			for (FluxEffect e : effects.values())
				if (i++ == index) effect = e;
		}
		
		// Remove some flux from the aura.
		ThaumcraftApi.queueNodeChanges(auraId, 0, 0, false, fluxReduce, 0, 0, 0);
		
		return effect;
		*/
		
	}
	
}
