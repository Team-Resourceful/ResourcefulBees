package com.teamresourceful.resourcefulbees.api.beedata;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.lib.constants.BeeConstants;
import com.teamresourceful.resourcefulbees.utils.color.Color;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.Style;
import org.jetbrains.annotations.Unmodifiable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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
                Codec.STRING.fieldOf("name").orElse(name).forGetter(CoreData::getName),
                CodecUtils.BLOCK_SET_CODEC.fieldOf("flower").orElse(Sets.newHashSet(Blocks.POPPY)).forGetter(CoreData::getBlockFlowers),
                Registry.ENTITY_TYPE.optionalFieldOf("entityFlower").forGetter(CoreData::getEntityFlower),
                Codec.intRange(600, Integer.MAX_VALUE).fieldOf("maxTimeInHive").orElse(2400).forGetter(CoreData::getMaxTimeInHive),
                Codec.STRING.optionalFieldOf("lore").forGetter(CoreData::getLore),
                Codec.STRING.optionalFieldOf("creator").forGetter(CoreData::getCreator),
                Color.CODEC.fieldOf("loreColor").orElse(Color.DEFAULT).forGetter(CoreData::getLoreColor)
        ).apply(instance, CoreData::new));
    }

    protected Set<Block> blockFlowers;
    protected Optional<EntityType<?>> entityFlower;
    protected Optional<String> lore;
    protected Optional<String> creator;
    protected int maxTimeInHive;
    protected String name;
    protected Color loreColor;

    private CoreData(String name, Set<Block> blockFlowers, Optional<EntityType<?>> entityFlower, int maxTimeInHive, Optional<String> lore, Optional<String> creator, Color loreColor){
        this.name = name;
        this.blockFlowers = blockFlowers;
        this.entityFlower = entityFlower;
        this.maxTimeInHive = maxTimeInHive;
        this.lore = lore;
        this.creator = creator;
        this.loreColor = loreColor;
    }

    private CoreData(String name) {
        this.name = name;
        this.blockFlowers = new HashSet<>();
        this.entityFlower = Optional.empty();
        this.maxTimeInHive = BeeConstants.MAX_TIME_IN_HIVE;
        this.lore = Optional.empty();
        this.creator = Optional.empty();
        this.loreColor = Color.DEFAULT;
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
     * @return Returns an {@link Optional<String>} containing extra bee information.
     */
    public Optional<String> getLore() {
        return lore;
    }

    /**
     * Gets the name of the person who originally created this bee if one was provided
     *
     * @return Returns an {@link Optional<String>} containing the name of the bee creator.
     */
    public Optional<String> getCreator() {
        return creator;
    }

    /**
     * Gets the color used when displaying the lore text
     *
     * @return Returns a {@link Color} object that defaults to white.
     */
    public Color getLoreColor() {
        return loreColor;
    }

    /**
     * Creates a {@link Style} component using the lore {@link Color} that can be
     * applied to implementations of {@link net.minecraft.util.text.TextComponent}s.
     *
     * @return Returns a {@link Style} component using the lore {@link Color}.
     */
    public Style getLoreColorStyle() {
        return Style.EMPTY.withColor(loreColor.getTextColor());
    }

    public CoreData toImmutable() {
        return this;
    }

    public static class Mutable extends CoreData {

        public Mutable(String name, Set<Block> blockFlowers, Optional<EntityType<?>> entityFlower, int maxTimeInHive, Optional<String> lore, Optional<String> creator, Color loreColor) {
            super(name, blockFlowers, entityFlower, maxTimeInHive, lore, creator, loreColor);
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

        public Mutable setLore(Optional<String> lore) {
            this.lore = lore;
            return this;
        }

        public Mutable setCreator(Optional<String> creator) {
            this.creator = creator;
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

        public Mutable setLoreColor(Color loreColor) {
            this.loreColor = loreColor;
            return this;
        }

        @Override
        public CoreData toImmutable() {
            return new CoreData(this.name, this.blockFlowers, this.entityFlower, this.maxTimeInHive, this.lore, this.creator, this.loreColor);
        }
    }
}
