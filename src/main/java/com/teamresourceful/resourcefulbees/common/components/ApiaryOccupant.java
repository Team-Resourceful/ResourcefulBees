package com.teamresourceful.resourcefulbees.common.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.compat.BeeCompat;
import com.teamresourceful.resourcefulbees.common.blockentities.ApiaryBlockEntity;
import com.teamresourceful.resourcefulbees.common.blockentities.base.BeeHolderBlockEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.util.EntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityProcessor;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.storage.TagValueOutput;
import org.jspecify.annotations.NullMarked;

import javax.annotation.Nullable;
import java.util.List;

import static com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants.MIN_HIVE_TIME;

@NullMarked
public record ApiaryOccupant(
        TypedEntityData<EntityType<?>> entityData,
        int ticksInHive,
        int minOccupationTicks,
        Component displayName,
        int color,
        boolean locked,
        boolean hasNectar
) implements Occupant{

    public static final Codec<ApiaryOccupant> CODEC = RecordCodecBuilder.create(
            i -> i.group(
                            TypedEntityData.codec(EntityType.CODEC).fieldOf("entity_data").forGetter(ApiaryOccupant::entityData),
                            Codec.INT.fieldOf("ticks_in_hive").forGetter(ApiaryOccupant::ticksInHive),
                            Codec.INT.fieldOf("min_ticks_in_hive").forGetter(ApiaryOccupant::minOccupationTicks),
                            ComponentSerialization.CODEC.fieldOf("display_name").forGetter(ApiaryOccupant::displayName),
                            Codec.INT.fieldOf("color").forGetter(ApiaryOccupant::color),
                            Codec.BOOL.fieldOf("locked").forGetter(ApiaryOccupant::locked),
                            Codec.BOOL.fieldOf("has_nectar").forGetter(ApiaryOccupant::hasNectar)
                    )
                    .apply(i, ApiaryOccupant::new)
    );
    public static final Codec<List<ApiaryOccupant>> LIST_CODEC = CODEC.listOf();
    public static final StreamCodec<RegistryFriendlyByteBuf, ApiaryOccupant> STREAM_CODEC = StreamCodec.composite(
            TypedEntityData.streamCodec(EntityType.STREAM_CODEC),
            ApiaryOccupant::entityData,
            ByteBufCodecs.VAR_INT,
            ApiaryOccupant::ticksInHive,
            ByteBufCodecs.VAR_INT,
            ApiaryOccupant::minOccupationTicks,
            ComponentSerialization.STREAM_CODEC,
            ApiaryOccupant::displayName,
            ByteBufCodecs.VAR_INT,
            ApiaryOccupant::color,
            ByteBufCodecs.BOOL,
            ApiaryOccupant::locked,
            ByteBufCodecs.BOOL,
            ApiaryOccupant::hasNectar,
            ApiaryOccupant::new
    );

    public static ApiaryOccupant of(Entity entity, BeeCompat compat, BeeHolderBlockEntity holderBlock, boolean locked) {
        ApiaryOccupant occupant;
        try (var reporter = new ProblemReporter.ScopedCollector(entity.problemPath(), ModConstants.LOGGER)) {
            var output = TagValueOutput.createWithContext(reporter, entity.registryAccess());
            entity.save(output);
            BeehiveBlockEntity.IGNORED_BEE_TAGS.forEach(output::discard);
            CompoundTag entityTag = output.buildResult();
            boolean hasNectar = entityTag.getBooleanOr("HasNectar", false);
            int maxTimeInHive;
            if (holderBlock instanceof ApiaryBlockEntity apiary) {
                maxTimeInHive = apiary.getMaxTimeInHive(compat);
            } else {
                maxTimeInHive = compat.resourcefulBees$getMaxTimeInHive();
            }
            occupant = new ApiaryOccupant(
                    TypedEntityData.of(entity.getType(), entityTag),
                    0,
                    hasNectar ? maxTimeInHive : MIN_HIVE_TIME,
                    entity.getDisplayName(),
                    EntityUtils.getBeeColorOrDefault(entity),
                    locked,
                    hasNectar
            );
        }

        return occupant;
    }

    public @Nullable Entity createEntity(Level level, BlockPos hivePos) {
        CompoundTag entityTag = this.entityData.copyTagWithoutId();
        BeehiveBlockEntity.IGNORED_BEE_TAGS.forEach(entityTag::remove);
        Entity entity = EntityType.loadEntityRecursive(this.entityData.type(), entityTag, level, EntitySpawnReason.LOAD, EntityProcessor.NOP);
        if (entity != null && entity.is(EntityTypeTags.BEEHIVE_INHABITORS)) {
            entity.setNoGravity(true);
            if (entity instanceof Bee bee) {
                bee.setHivePos(hivePos);
                setBeeReleaseData(this.ticksInHive, bee);
            }

            return entity;
        } else {
            return null;
        }
    }

    public ApiaryOccupant.Mutable mutable() {
        return new ApiaryOccupant.Mutable(this);
    }

    public static class Mutable {

        private final ApiaryOccupant occupant;

        private boolean locked;
        private int ticksInHive;

        private Mutable(ApiaryOccupant occupant) {
            this.occupant = occupant;
            this.locked = occupant.locked();
            this.ticksInHive = occupant.ticksInHive();
        }

        public boolean isLocked() {
            return locked;
        }

        public void toggleLocked() {
            setLocked(!locked);
        }

        public void setLocked(boolean locked) {
            this.locked = locked;
        }

        public int getTicksInHive() {
            return ticksInHive;
        }

        //can use this for a cool enchantment or something that can accelerate or slow bees in the hive/apiary
        public boolean tick(int amount) {
            ticksInHive = Math.min(ticksInHive + amount, Integer.MAX_VALUE - amount);
            return ticksInHive >= occupant.minOccupationTicks() && !locked;
        }

        public boolean tick() {
            return tick(1);
        }

        public ApiaryOccupant immutable() {
            return new ApiaryOccupant(
                    this.occupant.entityData,
                    this.ticksInHive,
                    this.occupant.minOccupationTicks,
                    this.occupant.displayName,
                    this.occupant.color,
                    this.locked,
                    this.occupant.hasNectar
            );
        }
    }
}