package com.teamresourceful.resourcefulbees.centrifuge.client;

import com.teamresourceful.resourcefulbees.centrifuge.client.screens.*;
import com.teamresourceful.resourcefulbees.centrifuge.common.registries.CentrifugeMenus;
import com.teamresourceful.resourcefulbees.client.ClientHandler;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD, modid = ModConstants.MOD_ID)
public class CentrifugeClient {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        MenuScreens.register(CentrifugeMenus.CENTRIFUGE_INPUT_CONTAINER.get(), CentrifugeInputScreen::new);
        MenuScreens.register(CentrifugeMenus.CENTRIFUGE_ITEM_OUTPUT_CONTAINER.get(), CentrifugeItemOutputScreen::new);
        MenuScreens.register(CentrifugeMenus.CENTRIFUGE_FLUID_OUTPUT_CONTAINER.get(), CentrifugeFluidOutputScreen::new);
        MenuScreens.register(CentrifugeMenus.CENTRIFUGE_VOID_CONTAINER.get(), CentrifugeVoidScreen::new);
        MenuScreens.register(CentrifugeMenus.CENTRIFUGE_TERMINAL_CONTAINER.get(), CentrifugeTerminalScreen::new);
    }
}
