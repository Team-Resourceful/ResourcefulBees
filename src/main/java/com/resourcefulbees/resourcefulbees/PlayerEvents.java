package com.resourcefulbees.resourcefulbees;

import com.resourcefulbees.resourcefulbees.api.IBeepediaData;
import com.resourcefulbees.resourcefulbees.capabilities.BeepediaData;
import com.resourcefulbees.resourcefulbees.network.NetPacketHandler;
import com.resourcefulbees.resourcefulbees.network.packets.BeepediaSyncMessage;
import com.resourcefulbees.resourcefulbees.network.packets.SyncGUIMessage;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.LazyOptional;
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
        NetPacketHandler.sendToPlayer(new BeepediaSyncMessage(data.serializeNBT(), new PacketBuffer(Unpooled.buffer())), (ServerPlayerEntity) event.getPlayer());
    }
}
