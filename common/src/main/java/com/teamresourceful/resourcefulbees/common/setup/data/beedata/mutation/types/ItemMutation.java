package com.teamresourceful.resourcefulbees.common.setup.data.beedata.mutation.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.bee.mutation.MutationType;
import com.teamresourceful.resourcefulbees.client.util.displays.ItemDisplay;
import com.teamresourceful.resourcefulbees.common.util.GenericSerializer;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import com.teamresourceful.resourcefullib.common.codecs.predicates.RestrictedItemPredicate;
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

public record ItemMutation(RestrictedItemPredicate predicate, double chance, double weight) implements MutationType, ItemDisplay {

    public static final GenericSerializer<MutationType> SERIALIZER = new Serializer();

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
    public GenericSerializer<MutationType> serializer() {
        return SERIALIZER;
    }

    @Override
    public ItemStack displayedItem() {
        ItemStack stack = new ItemStack(predicate.item());
        predicate.getTag().ifPresent(stack::setTag);
        return stack;
    }

    private static class Serializer implements GenericSerializer<MutationType> {

        public static final Codec<ItemMutation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                RestrictedItemPredicate.CODEC.fieldOf("item").forGetter(ItemMutation::predicate),
                CodecExtras.DOUBLE_UNIT_INTERVAL.optionalFieldOf("chance", 1D).forGetter(ItemMutation::chance),
                CodecExtras.NON_NEGATIVE_DOUBLE.optionalFieldOf("weight", 10D).forGetter(ItemMutation::weight)
        ).apply(instance, ItemMutation::new));

        @Override
        public Codec<? extends MutationType> codec() {
            return CODEC;
        }

        @Override
        public String id() {
            return "item";
        }
    }
}
