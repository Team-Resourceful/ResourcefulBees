package com.teamresourceful.resourcefulbees.api.beedata.mutation.types;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.beedata.mutation.types.display.IItemRender;
import com.teamresourceful.resourcefulbees.common.utils.predicates.RestrictedItemPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record ItemMutation(RestrictedItemPredicate predicate, double chance, double weight) implements IMutation, IItemRender {

    public static final Codec<ItemMutation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RestrictedItemPredicate.CODEC.fieldOf("item").forGetter(ItemMutation::predicate),
            Codec.doubleRange(0D, 1D).fieldOf("chance").orElse(1D).forGetter(ItemMutation::chance),
            Codec.doubleRange(0, Double.MAX_VALUE).fieldOf("weight").orElse(10D).forGetter(ItemMutation::weight)
    ).apply(instance, ItemMutation::new));

    @Override
    public @Nullable BlockPos check(ServerLevel level, BlockPos pos) {
        AABB box = new AABB(pos).expandTowards(new Vec3(0, -2, 0));
        List<Entity> entityList = level.getEntities((Entity) null, box,
                entity -> entity instanceof ItemEntity itemEntity && predicate.matches(itemEntity.getItem()));
        if (entityList.isEmpty()) return null;
        BlockPos entityPos = entityList.get(0).blockPosition();
        entityList.get(0).discard();
        return entityPos;
    }

    @Override
    public boolean activate(ServerLevel level, BlockPos pos) {
        ItemStack stack = new ItemStack(predicate.item());
        predicate.getTag().ifPresent(stack::setTag);
        level.addFreshEntity(new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), stack));
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
        jsonObject.addProperty("type", "item");
        return jsonObject;
    }

    @Override
    public ItemStack itemRender() {
        return new ItemStack(predicate.item(), 1, predicate.getTag().orElse(null));
    }
}
