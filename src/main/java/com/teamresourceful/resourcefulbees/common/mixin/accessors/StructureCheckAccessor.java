package com.teamresourceful.resourcefulbees.common.mixin.accessors;

import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.StructureCheck;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(StructureCheck.class)
public interface StructureCheckAccessor {

    @Accessor("chunkGenerator")
    ChunkGenerator getChunkGenerator();
}
