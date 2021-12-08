package com.teamresourceful.resourcefulbees.api.beedata;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import com.teamresourceful.resourcefulbees.common.utils.TextComponentCodec;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

@Unmodifiable
public class CoreData {
    /**
     * A default instance of {@link CoreData} that can be
     * used to minimize {@link NullPointerException}'s. This implementation sets the
     * bees name/type to "error".
     */
    public static final CoreData DEFAULT = new CoreData("error");

    /**
     * Returns a {@link Codec<CoreData>} that can be parsed to create a
     * {@link CoreData} object. The name value passed in is a fallback value
     * usually obtained from the bee json file name.
     * <i>Note: Name is synonymous with "bee type"</i>
     *
     * @param name The name (or "bee type") for the bee.
     * @return Returns a {@link Codec<CoreData>} that can be parsed to
     * create a {@link CoreData} object.
     */
    public static Codec<CoreData> codec(String name) {
        return RecordCodecBuilder.create(instance -> instance.group(
                MapCodec.of(Encoder.empty(), Decoder.unit(() -> name)).forGetter(CoreData::getName),
                CodecUtils.BLOCK_SET_CODEC.fieldOf("flower").orElse(Sets.newHashSet(Blocks.POPPY)).forGetter(CoreData::getBlockFlowers),
                Registry.ENTITY_TYPE.byNameCodec().optionalFieldOf("entityFlower").forGetter(CoreData::getEntityFlower),
                Codec.intRange(600, Integer.MAX_VALUE).fieldOf("maxTimeInHive").orElse(2400).forGetter(CoreData::getMaxTimeInHive),
                TextComponentCodec.CODEC.listOf().fieldOf("lore").orElse(Lists.newArrayList()).forGetter(CoreData::getLore)
        ).apply(instance, CoreData::new));
    }

    protected Set<Block> blockFlowers;
    protected Optional<EntityType<?>> entityFlower;
    protected List<Component> lore;
    protected int maxTimeInHive;
    protected String name;

    private CoreData(String name, Set<Block> blockFlowers, Optional<EntityType<?>> entityFlower, int maxTimeInHive, List<Component> lore){
        this.name = name;
        this.blockFlowers = blockFlowers;
        this.entityFlower = entityFlower;
        this.maxTimeInHive = maxTimeInHive;
        this.lore = lore;
    }

    private CoreData(String name) {
        this.name = name;
        this.blockFlowers = new HashSet<>();
        this.entityFlower = Optional.empty();
        this.maxTimeInHive = BeeConstants.MAX_TIME_IN_HIVE;
        this.lore = new ArrayList<>();
    }

    /**
     * These blocks represent the flowers the bee uses for pollinating.
     * The default value for this is an empty set.
     *
     * @return Returns a {@link Set<Block>} backed by a {@link HashSet}
     * representing the bees pollination flowers.
     */
    public Set<Block> getBlockFlowers() {
        return blockFlowers;
    }

    /**
     * Gets an optional entity the bee can use for pollinating
     * if one was specified in the bee json. The default value
     * is an optional of null.
     *
     * @return Returns an {@link Optional<EntityType>} for pollinating.
     */
    public Optional<EntityType<?>> getEntityFlower() {
        return entityFlower;
    }

    /**
     * Gets the registry ID for the entity flower if one is present.
     * If one is not present the value returned is null.
     *
     * @return Returns the entity flower registry ID as a {@link String}.
     */
    public String getEntityFlowerRegistryID() {
        return entityFlower.map(entityType -> entityType.getRegistryName() != null ? entityType.getRegistryName().toString() : null)
                .orElse(null);
    }

    /**
     * Gets the maximum time a bee can spend in the hive before time
     * modifications are performed.
     *
     * @return Returns the maximum time in hive as an {@link Integer}.
     */
    public int getMaxTimeInHive() {
        return maxTimeInHive;
    }

    /**
     * The name value passed into the constructor which is
     * usually obtained from the bee json file name.
     * <i>Note: Name is synonymous with "bee type"</i>
     *
     * @return Returns the name (or "bee type") for the bee.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets any information or lore data optionally provided to the bee.
     * This data is useful for pack devs to add extra notes about a bee.
     *
     * @return Returns an {@link Optional<List<Component>>} containing extra bee information.
     */
    public List<Component> getLore() {
        return lore;
    }

    public CoreData toImmutable() {
        return this;
    }

    public static class Mutable extends CoreData {

        public Mutable(String name, Set<Block> blockFlowers, Optional<EntityType<?>> entityFlower, int maxTimeInHive, List<Component> lore) {
            super(name, blockFlowers, entityFlower, maxTimeInHive, lore);
        }

        public Mutable(String name) {
            super(name);
        }

        public Mutable setBlockFlowers(Set<Block> blockFlowers) {
            this.blockFlowers = blockFlowers;
            return this;
        }

        public Mutable setEntityFlower(Optional<EntityType<?>> entityFlower) {
            this.entityFlower = entityFlower;
            return this;
        }

        public Mutable setLore(List<Component> lore) {
            this.lore = lore;
            return this;
        }

        public Mutable setMaxTimeInHive(int maxTimeInHive) {
            this.maxTimeInHive = maxTimeInHive;
            return this;
        }

        public Mutable setName(String name) {
            this.name = name;
            return this;
        }

        @Override
        public CoreData toImmutable() {
            return new CoreData(this.name, this.blockFlowers, this.entityFlower, this.maxTimeInHive, this.lore);
        }
    }
}
