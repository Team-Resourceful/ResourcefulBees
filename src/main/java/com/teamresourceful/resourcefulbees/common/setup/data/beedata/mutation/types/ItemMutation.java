package com.teamresourceful.resourcefulbees.common.setup.data.beedata.mutation.types;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.bee.mutation.MutationType;
import com.teamresourceful.resourcefulbees.client.util.displays.ItemDisplay;
import com.teamresourceful.resourcefulbees.common.lib.codecs.RestrictedItemPredicate;
import com.teamresourceful.resourcefulbees.common.util.GenericSerializer;
import com.teamresourceful.resourcefulbees.common.util.bytecodecs.StreamCodecExtras;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
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

    public static final GenericSerializer<ItemMutation> SERIALIZER = new Serializer();

    @Override
    public @Nullable BlockPos check(ServerLevel level, BlockPos pos) {
        AABB box = new AABB(pos).expandTowards(new Vec3(0, -2, 0));
        List<Entity> entityList = level.getEntities((Entity) null, box,
                entity -> entity instanceof ItemEntity itemEntity && predicate.matches(itemEntity.getItem()));
        if (entityList.isEmpty()) return null;
        BlockPos entityPos = entityList.getFirst().blockPosition();
        entityList.getFirst().discard();
        return entityPos;
    }

    @Override
    public boolean activate(ServerLevel level, BlockPos pos) {
        ItemStack stack = new ItemStack(predicate.item());
        components().ifPresent(stack::applyComponentsAndValidate);
        level.addFreshEntity(new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), stack));
        return true;
    }

    @Override
    public Optional<DataComponentPatch> components() {
        return predicate.components();
    }

    @Override
    public GenericSerializer<ItemMutation> serializer() {
        return SERIALIZER;
    }

    @Override
    public ItemStack displayedItem() {
        ItemStack stack = new ItemStack(predicate.item());
        components().ifPresent(stack::applyComponentsAndValidate);
        return stack;
    }

    private static class Serializer implements GenericSerializer<ItemMutation> {

        private static final MapCodec<ItemMutation> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                RestrictedItemPredicate.CODEC.fieldOf("item").forGetter(ItemMutation::predicate),
                CodecExtras.DOUBLE_UNIT_INTERVAL.optionalFieldOf("chance", 1D).forGetter(ItemMutation::chance),
                CodecExtras.NON_NEGATIVE_DOUBLE.optionalFieldOf("weight", 10D).forGetter(ItemMutation::weight)
        ).apply(instance, ItemMutation::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, ItemMutation> STREAM_CODEC =
                StreamCodec.composite(
                        RestrictedItemPredicate.STREAM_CODEC,
                        ItemMutation::predicate,
                        ByteBufCodecs.DOUBLE,
                        ItemMutation::chance,
                        ByteBufCodecs.DOUBLE,
                        ItemMutation::weight,
                        ItemMutation::new
                );

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ItemMutation> streamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public MapCodec<ItemMutation> codec() {
            return CODEC;
        }

        @Override
        public String id() {
            return "item";
        }
    }
}
