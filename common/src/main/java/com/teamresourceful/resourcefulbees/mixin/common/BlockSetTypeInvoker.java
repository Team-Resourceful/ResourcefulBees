package com.teamresourceful.resourcefulbees.mixin.common;

import com.teamresourceful.resourcefullib.common.exceptions.NotImplementedException;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlockSetType.class)
public interface BlockSetTypeInvoker {

    @Invoker("register")
    static BlockSetType register(BlockSetType type) {
        throw new NotImplementedException();
    }
}
