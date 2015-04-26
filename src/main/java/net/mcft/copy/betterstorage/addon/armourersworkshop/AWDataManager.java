package net.mcft.copy.betterstorage.addon.armourersworkshop;

import riskyken.armourersWorkshop.api.client.IArmourersClientManager;
import riskyken.armourersWorkshop.api.client.render.ISkinRenderHandler;
import riskyken.armourersWorkshop.api.common.IArmourersCommonManager;
import riskyken.armourersWorkshop.api.common.skin.ISkinDataHandler;
import riskyken.armourersWorkshop.api.common.skin.entity.IEntitySkinHandler;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinTypeRegistry;

public class AWDataManager implements IArmourersClientManager, IArmourersCommonManager {
	
	@Override
	public void onLoad(ISkinDataHandler dataHandler, ISkinTypeRegistry skinRegistry, IEntitySkinHandler entityHandler) {
		AWAddon.dataHandler = dataHandler;
		AWAddon.skinRegistry = skinRegistry;
		AWAddon.registerSkinTypes();
	}

	@Override
	public void onLoad(ISkinRenderHandler renderHandler) {
		AWAddon.renderHandler = renderHandler;
	}
}
