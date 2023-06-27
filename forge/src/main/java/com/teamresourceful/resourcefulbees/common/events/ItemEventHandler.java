package com.teamresourceful.resourcefulbees.common.events;

import com.teamresourceful.resourcefulbees.platform.common.events.RegisterBurnablesEvent;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ItemEventHandler {

    private static final Object2IntMap<Item> BURNABLES = new Object2IntOpenHashMap<>();

    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ItemEventHandler::commonSetup);
        MinecraftForge.EVENT_BUS.addListener(ItemEventHandler::onBurnTimeEvent);
    }

    public static void commonSetup(FMLCommonSetupEvent event) {
        RegisterBurnablesEvent.EVENT.fire(new RegisterBurnablesEvent((item, value) -> BURNABLES.put(item.asItem(), value)));
    }

    public static void onBurnTimeEvent(FurnaceFuelBurnTimeEvent event) {
        if (BURNABLES.containsKey(event.getItemStack().getItem())) {
            event.setBurnTime(BURNABLES.getInt(event.getItemStack().getItem()));
        }
    }

}
