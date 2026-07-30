package com.teamresourceful.resourcefulbees.common.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.tiers.BeehiveTier;
import com.teamresourceful.resourcefulbees.common.blockentities.TieredBeehiveBlockEntity;
import com.teamresourceful.resourcefulbees.common.lib.defaults.DefaultBeehiveTiers;
import com.teamresourceful.resourcefullib.common.codecs.EnumCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public record BeehiveUpgrade(Tier tier) {
    public static final Codec<BeehiveUpgrade> CODEC = RecordCodecBuilder.create(i -> i.group(
            Tier.CODEC.fieldOf("tier").forGetter(BeehiveUpgrade::tier)
    ).apply(i, BeehiveUpgrade::new));

    public static final StreamCodec<FriendlyByteBuf, BeehiveUpgrade> STREAM_CODEC =
            StreamCodec.composite(
                    Tier.STREAM_CODEC,
                    BeehiveUpgrade::tier,
                    BeehiveUpgrade::new
            );

    public static BeehiveUpgrade create(Tier tier) {
        return new BeehiveUpgrade(tier);
    }

    public enum Tier {
        T1_TO_T2(DefaultBeehiveTiers.T1_NEST, (state, level, pos) -> performUpgrade(state, level, pos, block -> getUpdateFor(block, '2'))),
        T2_TO_T3(DefaultBeehiveTiers.T2_NEST, (state, level, pos) -> performUpgrade(state, level, pos, block -> getUpdateFor(block, '3'))),
        T3_TO_T4(DefaultBeehiveTiers.T3_NEST, (state, level, pos) -> performUpgrade(state, level, pos, block -> getUpdateFor(block, '4')));

        private static final Codec<Tier> CODEC = EnumCodec.of(Tier.class);
        private static final StreamCodec<FriendlyByteBuf, Tier> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(Tier.class);

        public final BeehiveTier from;
        public final NestUpgrader upgrader;

        Tier(BeehiveTier from, NestUpgrader upgrader) {
            this.from = from;
            this.upgrader = upgrader;
        }

        private static InteractionResult performUpgrade(BlockState state, Level level, BlockPos pos, NestGetter getter) {
            if (state.isAir()) return InteractionResult.FAIL;
            Block nest = getter.getNest(state.getBlock());
            if (nest == null) return InteractionResult.FAIL;
            if (performBlockReplacementAndDataMerge(nest, state, level, pos)) return InteractionResult.SUCCESS_SERVER;
            return InteractionResult.FAIL;
        }

        public static boolean performBlockReplacementAndDataMerge(Block newBlock, BlockState old, Level level, BlockPos pos) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof TieredBeehiveBlockEntity tieredBeehiveBlockEntity) {
                var oldComponents = tieredBeehiveBlockEntity.collectComponents();
                Collection<ItemStack> honeycombs = tieredBeehiveBlockEntity.getHoneycombs();
                BlockState newBlockState = newBlock.withPropertiesOf(old);
                level.setBlock(pos, newBlockState, Block.UPDATE_ALL);
                if (newBlock instanceof EntityBlock entityBlock) {
                    BlockEntity newBlockEntity = entityBlock.newBlockEntity(pos, newBlockState);
                    if (newBlockEntity != null) {
                        newBlockEntity.applyComponents(oldComponents, DataComponentPatch.EMPTY);
                        newBlockEntity.setChanged();
                        level.setBlockEntity(newBlockEntity);
                        if (newBlockEntity instanceof TieredBeehiveBlockEntity hiveBlockEntity) {
                            hiveBlockEntity.upgradeHive(honeycombs);
                        }
                        return true;
                    }
                }
            }
            return false;
        }

        public static Block getUpdateFor(Block block, char i) {
            Identifier id = BuiltInRegistries.BLOCK.getKey(block);
            return BuiltInRegistries.BLOCK.getOptional(Identifier.fromNamespaceAndPath(id.getNamespace(), id.getPath().substring(0, id.getPath().length() - 1) + i)).orElse(null);
        }

        @FunctionalInterface
        public interface NestUpgrader {
            InteractionResult performUpgrade(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos);
        }

        @FunctionalInterface
        private interface NestGetter {
            @Nullable Block getNest(Block block);
        }
    }
}
