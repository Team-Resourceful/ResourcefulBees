package com.teamresourceful.resourcefulbees.common.setup.data.beedata.mutation.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.bee.mutation.MutationType;
import com.teamresourceful.resourcefulbees.client.util.displays.ItemDisplay;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.ModTranslations;
import com.teamresourceful.resourcefulbees.common.util.GenericSerializer;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import com.teamresourceful.resourcefullib.common.codecs.predicates.RestrictedBlockPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record BlockMutation(RestrictedBlockPredicate predicate, double chance, double weight) implements MutationType, ItemDisplay {

    public static final GenericSerializer<MutationType> SERIALIZER = new Serializer();

    @Override
    public @Nullable BlockPos check(ServerLevel level, BlockPos pos) {
        for (int i = 0; i < 2; i++) {
            pos = pos.below(1);
            if (predicate.matches(level, pos)) {
                level.removeBlock(pos, false);
                return pos;
            }
        }
        return null;
    }

    @Override
    public boolean activate(ServerLevel level, BlockPos pos) {
        if (!level.getBlockState(pos).canBeReplaced()) return false;
        BlockState state = this.predicate.properties().construct(this.predicate.block(), level.random);
        BlockState blockState = Block.updateFromNeighbourShapes(state, level, pos);
        if (blockState.isAir()) {
            blockState = state;
        }

        if (!level.setBlock(pos, blockState, Block.UPDATE_ALL)) {
            return false;
        } else {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity != null && tag().isPresent()) {
                entity.load(tag().get());
            }
        }
        level.blockUpdated(pos, blockState.getBlock());
        return true;
    }

    @Override
    public Optional<CompoundTag> tag() {
        return predicate.getTag();
    }

    @Override
    public GenericSerializer<MutationType> serializer() {
        return SERIALIZER;
    }

    @Override
    public ItemStack displayedItem() {
        ItemStack stack = new ItemStack(Items.BARRIER);
        Item item = predicate.block().asItem();
        if (item.equals(Items.AIR)) {
            stack.setHoverName(Component.translatable(ModTranslations.MUTATION_BLOCK, BuiltInRegistries.BLOCK.getKey(predicate.block())));
        } else {
            stack = new ItemStack(item);
        }
        return stack;
    }


    private static class Serializer implements GenericSerializer<MutationType> {

        public static final Codec<BlockMutation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                RestrictedBlockPredicate.CODEC.fieldOf("block").forGetter(BlockMutation::predicate),
                CodecExtras.DOUBLE_UNIT_INTERVAL.optionalFieldOf("chance", 1D).forGetter(BlockMutation::chance),
                CodecExtras.NON_NEGATIVE_DOUBLE.optionalFieldOf("weight", 10D).forGetter(BlockMutation::weight)
        ).apply(instance, BlockMutation::new));

        @Override
        public Codec<BlockMutation> codec() {
            return CODEC;
        }

        @Override
        public String id() {
            return "block";
        }
    }
}
