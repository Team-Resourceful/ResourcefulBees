package com.teamresourceful.resourcefulbees.common.blockentities.base;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.compat.BeeCompat;
import com.teamresourceful.resourcefulbees.common.blockentities.ApiaryBlockEntity;
import com.teamresourceful.resourcefulbees.common.util.EntityUtils;
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
import org.jspecify.annotations.Nullable;

import java.util.List;

import static com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants.MIN_HIVE_TIME;

public class BlockBee {
    private final Occupant occupant;

    private boolean locked;
    private int ticksInHive;

    public BlockBee(Occupant occupant) {
        this.occupant = occupant;
        this.ticksInHive = occupant.ticksInHive();
        this.locked = occupant.locked();
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

    public Occupant toOccupant() {
        return new Occupant(this.occupant.entityData, this.ticksInHive, this.occupant.minOccupationTicks, this.occupant.displayName, this.occupant.color, this.locked, this.occupant.hasNectar);
    }

    public record Occupant(TypedEntityData<EntityType<?>> entityData, int ticksInHive, int minOccupationTicks, Component displayName, String color, boolean locked, boolean hasNectar) {
        public static final Codec<Occupant> CODEC = RecordCodecBuilder.create(
                i -> i.group(
                                TypedEntityData.codec(EntityType.CODEC).fieldOf("entity_data").forGetter(Occupant::entityData),
                                Codec.INT.fieldOf("ticks_in_hive").forGetter(Occupant::ticksInHive),
                                Codec.INT.fieldOf("min_ticks_in_hive").forGetter(Occupant::minOccupationTicks),
                                ComponentSerialization.CODEC.fieldOf("display_name").forGetter(Occupant::displayName),
                                Codec.STRING.fieldOf("color").forGetter(Occupant::color),
                                Codec.BOOL.fieldOf("locked").forGetter(Occupant::locked),
                                Codec.BOOL.fieldOf("has_nectar").forGetter(Occupant::hasNectar)
                        )
                        .apply(i, Occupant::new)
        );
        public static final Codec<List<Occupant>> LIST_CODEC = CODEC.listOf();
        public static final StreamCodec<RegistryFriendlyByteBuf, Occupant> STREAM_CODEC = StreamCodec.composite(
                TypedEntityData.streamCodec(EntityType.STREAM_CODEC),
                Occupant::entityData,
                ByteBufCodecs.VAR_INT,
                Occupant::ticksInHive,
                ByteBufCodecs.VAR_INT,
                Occupant::minOccupationTicks,
                ComponentSerialization.STREAM_CODEC,
                Occupant::displayName,
                ByteBufCodecs.STRING_UTF8,
                Occupant::color,
                ByteBufCodecs.BOOL,
                Occupant::locked,
                ByteBufCodecs.BOOL,
                Occupant::hasNectar,
                Occupant::new
        );

        public static Occupant of(Entity entity, BeeCompat compat, BeeHolderBlockEntity holderBlock, boolean locked) {
            Occupant occupant;
            try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(entity.problemPath(), ResourcefulBees.LOGGER)) {
                TagValueOutput output = TagValueOutput.createWithContext(reporter, entity.registryAccess());
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
                occupant = new Occupant(
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

        private static void setBeeReleaseData(int ticksInHive, Bee bee) {
            updateBeeAge(ticksInHive, bee);
            bee.setInLoveTime(Math.max(0, bee.getInLoveTime() - ticksInHive));
        }

        private static void updateBeeAge(int ticksInHive, Bee bee) {
            if (!bee.isAgeLocked()) {
                int age = bee.getAge();
                if (age < 0) {
                    bee.setAge(Math.min(0, age + ticksInHive));
                } else if (age > 0) {
                    bee.setAge(Math.max(0, age - ticksInHive));
                }
            }
        }
    }
}
