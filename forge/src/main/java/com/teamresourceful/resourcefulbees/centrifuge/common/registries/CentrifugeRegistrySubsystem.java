package com.teamresourceful.resourcefulbees.centrifuge.common.registries;

import com.teamresourceful.resourcefulbees.common.subsystems.RegistrySubsystem;

public class CentrifugeRegistrySubsystem implements RegistrySubsystem {

    @Override
    public void init() {
        CentrifugeCreativeTabs.register();
        CentrifugeItems.CENTRIFUGE_ITEMS.init();
        CentrifugeBlocks.CENTRIFUGE_BLOCKS.init();
        CentrifugeBlockEntities.CENTRIFUGE_BLOCK_ENTITY_TYPES.init();
        CentrifugeMenus.CENTRIFUGE_MENUS.init();
    }
}
