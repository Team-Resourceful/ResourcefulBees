package com.teamresourceful.resourcefulbees.common.util.bytecodecs;

import com.teamresourceful.resourcefullib.common.codecs.predicates.NbtPredicate;
import com.teamresourceful.resourcefullib.common.codecs.predicates.RestrictedBlockPredicate;
import com.teamresourceful.resourcefullib.common.codecs.predicates.RestrictedEntityPredicate;
import com.teamresourceful.resourcefullib.common.codecs.predicates.RestrictedItemPredicate;
import com.teamresourceful.resourcefullib.common.codecs.predicates.properties.BlockStatePredicate;
import com.teamresourceful.resourcefullib.common.codecs.predicates.properties.ExactPropertyMatcher;
import com.teamresourceful.resourcefullib.common.codecs.predicates.properties.PropertyMatcher;
import com.teamresourceful.resourcefullib.common.codecs.predicates.properties.RangePropertyMatcher;
import com.teamresourceful.resourcefullib.common.collections.WeightedCollection;
import io.netty.buffer.ByteBuf;

import net.minecraft.advancements.predicates.*;
import net.minecraft.advancements.predicates.entity.EntityFlagsPredicate;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.function.ToDoubleFunction;

import static net.minecraft.network.codec.ByteBufCodecs.collection;

public final class StreamCodecExtras {

    private StreamCodecExtras() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static <B extends ByteBuf, V> StreamCodec.CodecOperation<B, V, Set<V>> set() {
        return original -> collection(HashSet::new, original);
    }

    public static <B extends ByteBuf, T>
    StreamCodec<B, WeightedCollection<T>> weightedCollection(StreamCodec<B, T> codec, ToDoubleFunction<T> weighter) {
        return codec.apply(ByteBufCodecs.list()).map(values -> WeightedCollection.of(values, weighter), collection -> collection.stream().toList());
    }

    public static final StreamCodec<FriendlyByteBuf, RangePropertyMatcher> RANGE_PROPERTY_MATCHER_STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8),
                    RangePropertyMatcher::min,
                    ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8),
                    RangePropertyMatcher::max,
                    RangePropertyMatcher::new
            );

    public static final StreamCodec<ByteBuf, ExactPropertyMatcher> EXACT_PROPERTY_MATCHER_STREAM_CODEC =
            ByteBufCodecs.STRING_UTF8.map(
                    ExactPropertyMatcher::new,
                    ExactPropertyMatcher::value
            );

    public static final StreamCodec<FriendlyByteBuf, PropertyMatcher> PROPERTY_MATCHER_STREAM_CODEC =
            new StreamCodec<>() {

                @Override
                public @NonNull PropertyMatcher decode(FriendlyByteBuf buffer) {
                    return switch (buffer.readByte()) {
                        case 0 -> EXACT_PROPERTY_MATCHER_STREAM_CODEC.decode(buffer);
                        case 1 -> RANGE_PROPERTY_MATCHER_STREAM_CODEC.decode(buffer);
                        default -> throw new IllegalArgumentException(
                                "Unknown PropertyMatcher type"
                        );
                    };
                }

                @Override
                public void encode(@NonNull FriendlyByteBuf buffer, @NonNull PropertyMatcher matcher) {
                    switch (matcher) {
                        case ExactPropertyMatcher exact -> {
                            buffer.writeByte(0);
                            EXACT_PROPERTY_MATCHER_STREAM_CODEC.encode(buffer, exact);
                        }
                        case RangePropertyMatcher range -> {
                            buffer.writeByte(1);
                            RANGE_PROPERTY_MATCHER_STREAM_CODEC.encode(buffer, range);
                        }
                        default -> throw new IllegalArgumentException(
                                    "Unknown PropertyMatcher type: "
                                            + matcher.getClass().getName()
                            );
                    }
                }
            };

    public static final StreamCodec<FriendlyByteBuf, BlockStatePredicate> BLOCK_STATE_PREDICATE_STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public @NonNull BlockStatePredicate decode(FriendlyByteBuf buffer) {
                    int size = buffer.readVarInt();

                    Map<String, PropertyMatcher> properties = HashMap.newHashMap(size);

                    for (int i = 0; i < size; i++) {
                        String name = ByteBufCodecs.STRING_UTF8.decode(buffer);
                        PropertyMatcher matcher =
                                PROPERTY_MATCHER_STREAM_CODEC.decode(buffer);

                        properties.put(name, matcher);
                    }

                    return new BlockStatePredicate(properties);
                }

                @Override
                public void encode(
                        FriendlyByteBuf buffer,
                        BlockStatePredicate predicate
                ) {
                    Map<String, PropertyMatcher> properties =
                            predicate.properties();

                    buffer.writeVarInt(properties.size());

                    for (Map.Entry<String, PropertyMatcher> entry :
                            properties.entrySet()) {

                        ByteBufCodecs.STRING_UTF8.encode(
                                buffer,
                                entry.getKey()
                        );

                        PROPERTY_MATCHER_STREAM_CODEC.encode(
                                buffer,
                                entry.getValue()
                        );
                    }
                }
            };

    public static final StreamCodec<ByteBuf, LightPredicate> LIGHT_PREDICATE_STREAM_CODEC =
            MinMaxBounds.Ints.STREAM_CODEC.map(
                    LightPredicate::new,
                    LightPredicate::composite
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, FluidPredicate> FLUID_PREDICATE_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ByteBufCodecs.holderSet(Registries.FLUID)),
            FluidPredicate::fluids,
            ByteBufCodecs.optional(StatePropertiesPredicate.STREAM_CODEC),
            FluidPredicate::properties,
            FluidPredicate::new
    );

    public static final StreamCodec<FriendlyByteBuf, LocationPredicate.PositionPredicate> POSITION_PREDICATE_STREAM_CODEC =
            StreamCodec.composite(
                    MinMaxBounds.Doubles.STREAM_CODEC,
                    LocationPredicate.PositionPredicate::x,

                    MinMaxBounds.Doubles.STREAM_CODEC,
                    LocationPredicate.PositionPredicate::y,

                    MinMaxBounds.Doubles.STREAM_CODEC,
                    LocationPredicate.PositionPredicate::z,

                    LocationPredicate.PositionPredicate::new
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, LocationPredicate> LOCATION_PREDICATE_STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.optional(POSITION_PREDICATE_STREAM_CODEC),
                    LocationPredicate::position,

                    ByteBufCodecs.optional(ByteBufCodecs.holderSet(Registries.BIOME)),
                    LocationPredicate::biomes,

                    ByteBufCodecs.optional(ByteBufCodecs.holderSet(Registries.STRUCTURE)),
                    LocationPredicate::structures,

                    ByteBufCodecs.optional(ResourceKey.streamCodec(Registries.DIMENSION)),
                    LocationPredicate::dimension,

                    ByteBufCodecs.optional(ByteBufCodecs.BOOL),
                    LocationPredicate::smokey,

                    ByteBufCodecs.optional(LIGHT_PREDICATE_STREAM_CODEC),
                    LocationPredicate::light,

                    ByteBufCodecs.optional(BlockPredicate.STREAM_CODEC),
                    LocationPredicate::block,

                    ByteBufCodecs.optional(FLUID_PREDICATE_STREAM_CODEC),
                    LocationPredicate::fluid,

                    ByteBufCodecs.optional(ByteBufCodecs.BOOL),
                    LocationPredicate::canSeeSky,

                    LocationPredicate::new
            );

    public static final StreamCodec<ByteBuf, NbtPredicate> NBT_PREDICATE_STREAM_CODEC =
            ByteBufCodecs.COMPOUND_TAG.map(
                    NbtPredicate::new,
                    NbtPredicate::tag
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, RestrictedBlockPredicate> RESTRICTED_BLOCK_PREDICATE_STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.registry(BuiltInRegistries.BLOCK.key()),
                    RestrictedBlockPredicate::block,

                    ByteBufCodecs.optional(NBT_PREDICATE_STREAM_CODEC),
                    RestrictedBlockPredicate::nbt,

                    ByteBufCodecs.optional(LOCATION_PREDICATE_STREAM_CODEC),
                    RestrictedBlockPredicate::location,

                    BLOCK_STATE_PREDICATE_STREAM_CODEC,
                    RestrictedBlockPredicate::properties,

                    RestrictedBlockPredicate::new
            );


    public static final StreamCodec<ByteBuf, MobEffectsPredicate.MobEffectInstancePredicate> MOB_EFFECT_INSTANCE_PREDICATE_STREAM_CODEC =
            StreamCodec.composite(
                    MinMaxBounds.Ints.STREAM_CODEC,
                    MobEffectsPredicate.MobEffectInstancePredicate::amplifier,
                    MinMaxBounds.Ints.STREAM_CODEC,
                    MobEffectsPredicate.MobEffectInstancePredicate::duration,
                    ByteBufCodecs.optional(ByteBufCodecs.BOOL),
                    MobEffectsPredicate.MobEffectInstancePredicate::ambient,
                    ByteBufCodecs.optional(ByteBufCodecs.BOOL),
                    MobEffectsPredicate.MobEffectInstancePredicate::visible,
                    MobEffectsPredicate.MobEffectInstancePredicate::new
            );

    private static final StreamCodec<RegistryFriendlyByteBuf, Map<Holder<MobEffect>, MobEffectsPredicate.MobEffectInstancePredicate>> EFFECT_MAP_STREAM_CODEC =
            ByteBufCodecs.map(
                    HashMap::new,
                    ByteBufCodecs.holderRegistry(Registries.MOB_EFFECT),
                    MOB_EFFECT_INSTANCE_PREDICATE_STREAM_CODEC
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, MobEffectsPredicate> MOB_EFFECTS_PREDICATE_STREAM_CODEC =
            EFFECT_MAP_STREAM_CODEC.map(
                    MobEffectsPredicate::new,
                    MobEffectsPredicate::effectMap
            );

    public static final StreamCodec<FriendlyByteBuf, EntityFlagsPredicate> ENTITY_FLAGS_PREDICATE_STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.optional(ByteBufCodecs.BOOL),
                    EntityFlagsPredicate::isOnGround,

                    ByteBufCodecs.optional(ByteBufCodecs.BOOL),
                    EntityFlagsPredicate::isOnFire,

                    ByteBufCodecs.optional(ByteBufCodecs.BOOL),
                    EntityFlagsPredicate::isCrouching,

                    ByteBufCodecs.optional(ByteBufCodecs.BOOL),
                    EntityFlagsPredicate::isSprinting,

                    ByteBufCodecs.optional(ByteBufCodecs.BOOL),
                    EntityFlagsPredicate::isSwimming,

                    ByteBufCodecs.optional(ByteBufCodecs.BOOL),
                    EntityFlagsPredicate::isFlying,

                    ByteBufCodecs.optional(ByteBufCodecs.BOOL),
                    EntityFlagsPredicate::isBaby,

                    ByteBufCodecs.optional(ByteBufCodecs.BOOL),
                    EntityFlagsPredicate::isInWater,

                    ByteBufCodecs.optional(ByteBufCodecs.BOOL),
                    EntityFlagsPredicate::isFallFlying,

                    EntityFlagsPredicate::new
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, RestrictedEntityPredicate> RESTRICTED_ENTITY_PREDICATE_STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.registry(BuiltInRegistries.ENTITY_TYPE.key()),
                    RestrictedEntityPredicate::entityType,

                    ByteBufCodecs.optional(LOCATION_PREDICATE_STREAM_CODEC),
                    RestrictedEntityPredicate::location,

                    ByteBufCodecs.optional(MOB_EFFECTS_PREDICATE_STREAM_CODEC),
                    RestrictedEntityPredicate::effects,

                    ByteBufCodecs.optional(NBT_PREDICATE_STREAM_CODEC),
                    RestrictedEntityPredicate::nbt,

                    ByteBufCodecs.optional(ENTITY_FLAGS_PREDICATE_STREAM_CODEC),
                    RestrictedEntityPredicate::flags,

                    //todo Fix this codec to not call fromCodecWithregistries
                    ByteBufCodecs.optional(ByteBufCodecs.fromCodecWithRegistries(EntityPredicate.CODEC)),
                    RestrictedEntityPredicate::targetedEntity,

                    RestrictedEntityPredicate::new
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, RestrictedItemPredicate> RESTRICTED_ITEM_PREDICATE_STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.registry(BuiltInRegistries.ITEM.key()),
                    RestrictedItemPredicate::item,

                    ByteBufCodecs.optional(NBT_PREDICATE_STREAM_CODEC),
                    RestrictedItemPredicate::nbt,

                    MinMaxBounds.Ints.STREAM_CODEC,
                    RestrictedItemPredicate::durability,

                    MinMaxBounds.Ints.STREAM_CODEC,
                    RestrictedItemPredicate::count,

                    RestrictedItemPredicate::new
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, DataComponentIngredient> DATA_COMPONENT_INGREDIENT_STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.holderSet(Registries.ITEM),
                    DataComponentIngredient::itemSet,
                    DataComponentPatch.STREAM_CODEC,
                    DataComponentIngredient::components,
                    ByteBufCodecs.BOOL,
                    DataComponentIngredient::componentsExhaustive,
                    DataComponentIngredient::new);
}
