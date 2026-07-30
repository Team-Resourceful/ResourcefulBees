package com.teamresourceful.resourcefulbees.common.setup.data.beedata.mutation.types;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.data.bee.mutation.MutationType;
import com.teamresourceful.resourcefulbees.client.util.displays.EntityDisplay;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.util.EntityUtils;
import com.teamresourceful.resourcefulbees.common.util.GenericSerializer;
import com.teamresourceful.resourcefulbees.common.util.bytecodecs.StreamCodecExtras;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import com.teamresourceful.resourcefullib.common.codecs.predicates.RestrictedEntityPredicate;
import com.teamresourceful.resourcefullib.common.nbt.TagUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record EntityMutation(RestrictedEntityPredicate predicate, double chance, double weight) implements MutationType, EntityDisplay {

    public static final GenericSerializer<EntityMutation> SERIALIZER = new Serializer();

    @Nullable
    @Override
    public BlockPos check(ServerLevel level, BlockPos pos) {
        AABB box = new AABB(pos).expandTowards(0, -2, 0);
        List<Entity> entityList = level.getEntities((Entity) null, box, entity -> predicate().matches(level, entity));
        if (entityList.isEmpty()) return null;
        BlockPos entityPos = entityList.getFirst().blockPosition();
        entityList.getFirst().discard();
        return entityPos;
    }

    //todo tweak the location/angle setting if possible
    @Override
    public boolean activate(ServerLevel level, BlockPos pos) {
        CompoundTag entityTag = predicate().getTag().orElse(new CompoundTag());
        var type = predicate().entityType();
        var entity = EntityType.loadEntityRecursive(type, entityTag, level, EntitySpawnReason.CONVERSION, EntityProcessor.NOP);
        if (entity != null) {
            EntityUtils.setEntityLocationAndAngle(pos, Direction.NORTH, entity);
            level.addFreshEntity(entity);
            level.levelEvent(2005, pos.below(), 0);
        }
        return true;
    }

    @Override
    public Optional<DataComponentPatch> components() {
        return Optional.empty();
    }

    @Override
    public GenericSerializer<EntityMutation> serializer() {
        return SERIALIZER;
    }

    @Override
    public EntityType<?> displayedEntity() {
        return predicate().entityType();
    }

    private static class Serializer implements GenericSerializer<EntityMutation> {

        private static final MapCodec<EntityMutation> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                RestrictedEntityPredicate.CODEC.fieldOf("entity").forGetter(EntityMutation::predicate),
                CodecExtras.DOUBLE_UNIT_INTERVAL.optionalFieldOf("chance", 1D).forGetter(EntityMutation::chance),
                CodecExtras.NON_NEGATIVE_DOUBLE.optionalFieldOf("weight", 10D).forGetter(EntityMutation::weight)
        ).apply(instance, EntityMutation::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, EntityMutation> STREAM_CODEC = StreamCodec.composite(
                StreamCodecExtras.RESTRICTED_ENTITY_PREDICATE_STREAM_CODEC,
                EntityMutation::predicate,
                ByteBufCodecs.DOUBLE,
                EntityMutation::chance,
                ByteBufCodecs.DOUBLE,
                EntityMutation::weight,
                EntityMutation::new
        );

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, EntityMutation> streamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public MapCodec<EntityMutation> codec() {
            return CODEC;
        }

        @Override
        public String id() {
            return "entity";
        }
    }
}
