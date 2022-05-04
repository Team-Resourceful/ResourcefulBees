package com.teamresourceful.resourcefulbees.api.beedata.mutation.types;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.beedata.mutation.types.display.IItemRender;
import com.teamresourceful.resourcefulbees.common.utils.predicates.RestrictedBlockPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record BlockMutation(RestrictedBlockPredicate predicate, double chance, double weight) implements IMutation, IItemRender {

    public static final Codec<BlockMutation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RestrictedBlockPredicate.CODEC.fieldOf("block").forGetter(BlockMutation::predicate),
            Codec.doubleRange(0D, 1D).fieldOf("chance").orElse(1D).forGetter(BlockMutation::chance),
            Codec.doubleRange(0, Double.MAX_VALUE).fieldOf("weight").orElse(10D).forGetter(BlockMutation::weight)
    ).apply(instance, BlockMutation::new));

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
        if (!level.getBlockState(pos).getMaterial().isReplaceable()) return false;
        //TODO see about changing this to using blockstate processor or something.
        BlockState state = this.predicate.block().defaultBlockState();
        level.setBlock(pos, state, Block.UPDATE_ALL);
        predicate.getTag().ifPresent(nbt -> BlockEntity.loadStatic(pos, state, nbt));
        return true;
    }

    @Override
    public Optional<CompoundTag> tag() {
        return predicate.getTag();
    }

    @Override
    public JsonElement toJson() {
        Optional<JsonElement> json = CODEC.encodeStart(JsonOps.INSTANCE, this).result();
        if (json.isEmpty() || !(json.get() instanceof JsonObject jsonObject)) return JsonNull.INSTANCE;
        jsonObject.addProperty("type", "block");
        return jsonObject;
    }

    @Override
    public ItemStack itemRender() {
        ItemStack stack = new ItemStack(Items.BARRIER);
        Item item = predicate.block().asItem();
        if (item.equals(Items.AIR)) {
            stack.setHoverName(new TextComponent("Block: " + predicate.block().getRegistryName()));
        } else {
            stack = new ItemStack(item);
        }
        return stack;
    }
}