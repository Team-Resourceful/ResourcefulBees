package com.teamresourceful.resourcefulbees.common.setup.data.beedata.mutation.types;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.bee.mutation.MutationType;
import com.teamresourceful.resourcefulbees.client.util.displays.ItemDisplay;
import com.teamresourceful.resourcefulbees.common.lib.codecs.RestrictedBlockPredicate;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.ModTranslations;
import com.teamresourceful.resourcefulbees.common.lib.util.GenericSerializer;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
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

    public static final GenericSerializer<BlockMutation> SERIALIZER = new Serializer();

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
        BlockState state = this.predicate.properties().construct(this.predicate.block(), level.getRandom());
        BlockState blockState = Block.updateFromNeighbourShapes(state, level, pos);
        if (blockState.isAir()) {
            blockState = state;
        }

        if (!level.setBlock(pos, blockState, Block.UPDATE_ALL)) {
            return false;
        } else {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity != null && components().isPresent()) {
                var current = entity.collectComponents();
                entity.applyComponents(current, components().get());
            }
        }
        level.setBlockAndUpdate(pos, blockState);
        return true;
    }

    @Override
    public Optional<DataComponentPatch> components() {
        return predicate.getComponents();
    }

    @Override
    public GenericSerializer<BlockMutation> serializer() {
        return SERIALIZER;
    }

    @Override
    public ItemStack displayedItem() {
        ItemStack stack = new ItemStack(Items.BARRIER);
        Item item = predicate.block().asItem();
        if (item.equals(Items.AIR)) {
            stack.set(DataComponents.CUSTOM_NAME, Component.translatable(ModTranslations.MUTATION_BLOCK, BuiltInRegistries.BLOCK.getKey(predicate.block())));
            //stack.setHoverName(Component.translatable(ModTranslations.MUTATION_BLOCK, BuiltInRegistries.BLOCK.getKey(predicate.block())));
        } else {
            stack = new ItemStack(item);
        }
        return stack;
    }


    private static class Serializer implements GenericSerializer<BlockMutation> {

        private static final MapCodec<BlockMutation> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                RestrictedBlockPredicate.CODEC.fieldOf("block").forGetter(BlockMutation::predicate),
                CodecExtras.DOUBLE_UNIT_INTERVAL.optionalFieldOf("chance", 1D).forGetter(BlockMutation::chance),
                CodecExtras.NON_NEGATIVE_DOUBLE.optionalFieldOf("weight", 10D).forGetter(BlockMutation::weight)
        ).apply(instance, BlockMutation::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, BlockMutation> STREAM_CODEC = StreamCodec.composite(


                RestrictedBlockPredicate.STREAM_CODEC,
                BlockMutation::predicate,
                ByteBufCodecs.DOUBLE,
                BlockMutation::chance,
                ByteBufCodecs.DOUBLE,
                BlockMutation::weight,
                BlockMutation::new
        );

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BlockMutation> streamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public MapCodec<BlockMutation> codec() {
            return CODEC;
        }

        @Override
        public String id() {
            return "block";
        }
    }
}
