package com.resourcefulbees.resourcefulbees;

import com.resourcefulbees.resourcefulbees.api.IBeepediaData;
import com.resourcefulbees.resourcefulbees.capabilities.BeepediaData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;


//TODO find a better location for this
public class PlayerEvents {


    public PlayerEvents() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void attachCaps(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity) {
            BeepediaData.Provider beepediaProvider = new BeepediaData.Provider();
            event.addCapability(BeepediaData.CAPABILITY_ID, beepediaProvider);
            event.addListener(beepediaProvider::invalidate);
        }
    }

    @SubscribeEvent
    public void cloneEvent(PlayerEvent.Clone event) {
        IBeepediaData data = event.getOriginal().getCapability(BeepediaData.Provider.BEEPEDIA_DATA).orElse(new BeepediaData());
        event.getPlayer().getCapability(BeepediaData.Provider.BEEPEDIA_DATA).ifPresent(c -> c.deserializeNBT(data.serializeNBT()));

    }

    @SubscribeEvent
    public void onPlayerLoginEvent(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEntity player = event.getPlayer();
        if (!player.level.isClientSide) {
            event.getPlayer().getCapability(BeepediaData.Provider.BEEPEDIA_DATA).ifPresent(c ->
                    BeepediaData.sync((ServerPlayerEntity) event.getPlayer(), c));
        }
    }

    @SubscribeEvent
    public void onPlayerDimChangedEvent(PlayerEvent.PlayerChangedDimensionEvent event) {
        event.getPlayer().getCapability(BeepediaData.Provider.BEEPEDIA_DATA).ifPresent(c ->
                BeepediaData.sync((ServerPlayerEntity) event.getPlayer(), c));
    }

    @SubscribeEvent
    public void respawnEvent(PlayerEvent.PlayerRespawnEvent event) {
        event.getPlayer().getCapability(BeepediaData.Provider.BEEPEDIA_DATA).ifPresent(c ->
                BeepediaData.sync((ServerPlayerEntity) event.getPlayer(), c));
    }
}
