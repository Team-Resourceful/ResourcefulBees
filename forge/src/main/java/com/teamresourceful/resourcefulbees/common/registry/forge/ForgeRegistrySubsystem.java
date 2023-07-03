package com.teamresourceful.resourcefulbees.common.registry.forge;

import com.teamresourceful.resourcefulbees.common.subsystems.RegistrySubsystem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ForgeRegistrySubsystem implements RegistrySubsystem {
    @Override
    public void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBiomeModifiers.MODIFIERS.register(bus);
    }
}
