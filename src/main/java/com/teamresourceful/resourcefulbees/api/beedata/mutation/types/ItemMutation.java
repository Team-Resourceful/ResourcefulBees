package com.teamresourceful.resourcefulbees.api.beedata.mutation.types;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.beedata.mutation.types.display.IItemRender;
import com.teamresourceful.resourcefulbees.common.utils.ModUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record ItemMutation(Item item, Optional<CompoundTag> tag, double chance, double weight) implements IMutation, IItemRender {

    public static final Codec<ItemMutation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Registry.ITEM.byNameCodec().fieldOf("item").forGetter(ItemMutation::item),
            CompoundTag.CODEC.optionalFieldOf("tag").forGetter(ItemMutation::tag),
            Codec.doubleRange(0D, 1D).fieldOf("chance").orElse(1D).forGetter(ItemMutation::chance),
            Codec.doubleRange(0, Double.MAX_VALUE).fieldOf("weight").orElse(10D).forGetter(ItemMutation::weight)
    ).apply(instance, ItemMutation::new));

    @Override
    public @Nullable BlockPos check(ServerLevel level, Bee bee, BlockPos pos) {
        AABB box = bee.getBoundingBox().expandTowards(new Vec3(0, -2, 0));
        List<Entity> entityList = level.getEntities(bee, box, entity ->
                entity instanceof ItemEntity itemEntity &&
                itemEntity.getItem().is(item) &&
                (tag.isEmpty() || ModUtils.fuzzyMatchTag(tag.get(), itemEntity.getItem().getTag()))
        );
        if (entityList.isEmpty()) return null;
        BlockPos entityPos = entityList.get(0).blockPosition();
        entityList.get(0).discard();
        return entityPos;
    }

    @Override
    public boolean activate(ServerLevel level, Bee bee, BlockPos pos) {
        ItemStack stack = new ItemStack(item);
        tag.ifPresent(stack::setTag);
        level.addFreshEntity(new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), stack));
        return true;
    }

    @Override
    public JsonElement toJson() {
        Optional<JsonElement> json = CODEC.encodeStart(JsonOps.INSTANCE, this).result();
        if (json.isEmpty() || !(json.get() instanceof JsonObject jsonObject)) return JsonNull.INSTANCE;
        jsonObject.addProperty("type", "item");
        return jsonObject;
    }

    @Override
    public ItemStack itemRender() {
        return new ItemStack(item, 1, tag.orElse(null));
    }
}
