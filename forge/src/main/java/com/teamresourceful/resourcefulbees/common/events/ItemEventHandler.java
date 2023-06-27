package com.teamresourceful.resourcefulbees.common.events;

import com.teamresourceful.resourcefulbees.platform.common.events.BlockBonemealedEvent;
import com.teamresourceful.resourcefulbees.platform.common.events.RegisterBurnablesEvent;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.concurrent.atomic.AtomicBoolean;

public class ItemEventHandler {

    private static final Object2IntMap<Item> BURNABLES = new Object2IntOpenHashMap<>();

    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ItemEventHandler::commonSetup);
        MinecraftForge.EVENT_BUS.addListener(ItemEventHandler::onBurnTimeEvent);
        MinecraftForge.EVENT_BUS.addListener(ItemEventHandler::onBonemeal);
    }

    public static void commonSetup(FMLCommonSetupEvent event) {
        RegisterBurnablesEvent.EVENT.fire(new RegisterBurnablesEvent((item, value) -> BURNABLES.put(item.asItem(), value)));
    }

    public static void onBurnTimeEvent(FurnaceFuelBurnTimeEvent event) {
        if (BURNABLES.containsKey(event.getItemStack().getItem())) {
            event.setBurnTime(BURNABLES.getInt(event.getItemStack().getItem()));
        }
    }

    public static void onBonemeal(BonemealEvent event) {
        BlockBonemealedEvent newEvent = new BlockBonemealedEvent(
                event.getEntity(),
                event.getLevel(),
                event.getPos(),
                event.getBlock(),
                event.getStack(),
                new AtomicBoolean(event.isCanceled())
        );
        BlockBonemealedEvent.EVENT.fire(newEvent);
        if (newEvent.isCanceled()) {
            event.setCanceled(true);
        }
    }

}
