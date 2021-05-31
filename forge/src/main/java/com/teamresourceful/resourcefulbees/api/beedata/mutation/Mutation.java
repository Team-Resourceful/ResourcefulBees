package com.teamresourceful.resourcefulbees.api.beedata.mutation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.beedata.CodecUtils;
import com.teamresourceful.resourcefulbees.lib.enums.MutationType;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

@Unmodifiable
public class Mutation {

    public static final Codec<Mutation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            MutationType.CODEC.fieldOf("type").orElse(MutationType.NONE).forGetter(Mutation::getType),
            CodecUtils.BLOCK_SET_CODEC.fieldOf("input").orElse(new HashSet<>()).forGetter(Mutation::getBlockInputs),
            Registry.ENTITY_TYPE.optionalFieldOf("entityInput").forGetter(Mutation::getEntityInput),
            CompoundTag.CODEC.optionalFieldOf("tag").forGetter(Mutation::getInputTag),
            Codec.doubleRange(0.0d, 1.0d).fieldOf("chance").orElse(1.0d).forGetter(Mutation::getChance),
            MutationOutput.CODEC.listOf().fieldOf("outputs").orElse(new ArrayList<>()).forGetter(Mutation::getOutputs)
    ).apply(instance, Mutation::new));

    private final MutationType type;
    private final Set<Block> blockInputs;
    private final Optional<EntityType<?>> entityInput;
    private final Optional<CompoundTag> inputTag;
    private final double chance;
    private final List<MutationOutput> outputs;

    public Mutation(MutationType type, Set<Block> blockInputs, Optional<EntityType<?>> entityInput, Optional<CompoundTag> inputTag, double chance, List<MutationOutput> outputs) {
        this.type = type;
        this.blockInputs = blockInputs;
        this.entityInput = entityInput;
        this.inputTag = inputTag;
        this.chance = chance;
        this.outputs = outputs;
    }

    public MutationType getType() {
        return this.type;
    }

    public Set<Block> getBlockInputs() {
        return blockInputs;
    }

    public Optional<EntityType<?>> getEntityInput() {
        return entityInput;
    }

    public double getChance() {
        return chance;
    }

    public List<MutationOutput> getOutputs() {
        return this.outputs;
    }

    public Optional<CompoundTag> getInputTag() {
        return inputTag;
    }
}
