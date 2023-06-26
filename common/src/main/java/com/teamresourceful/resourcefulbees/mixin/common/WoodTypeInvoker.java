package com.teamresourceful.resourcefulbees.mixin.common;

import com.teamresourceful.resourcefullib.common.exceptions.NotImplementedException;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(WoodType.class)
public interface WoodTypeInvoker {

    @Invoker("register")
    static WoodType register(WoodType type) {
        throw new NotImplementedException();
    }
}
