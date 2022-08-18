package com.teamresourceful.resourcefulbees.common.capabilities;

import com.teamresourceful.resourcefulbees.common.capabilities.base.CapabilityInformation;
import com.teamresourceful.resourcefulbees.common.capabilities.beepedia.BeepediaCapabilityProvider;
import com.teamresourceful.resourcefulbees.common.capabilities.beepedia.BeepediaData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ModCapabilities {

    public static final Capability<BeepediaData> BEEPEDIA_DATA = CapabilityInformation.registerSynced(BeepediaCapabilityProvider.ID,
            CapabilityInformation.registerSaved(CapabilityManager.get(new CapabilityToken<>() {})));

    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ModCapabilities::onRegister);
        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, ModCapabilities::onAttachToEntity);

        MinecraftForge.EVENT_BUS.addListener(CapabilityInformation::cloneEvent);
    }

    public static void onAttachToEntity(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(BeepediaCapabilityProvider.ID, new BeepediaCapabilityProvider());
        }
    }

    public static void onRegister(RegisterCapabilitiesEvent event) {
        event.register(BeepediaData.class);
    }
}
