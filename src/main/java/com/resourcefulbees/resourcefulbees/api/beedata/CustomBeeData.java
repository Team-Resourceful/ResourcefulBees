package com.resourcefulbees.resourcefulbees.api.beedata;

import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.entity.passive.ResourcefulBee;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;

public class CustomBeeData {

    private final String flower;
    private final String baseLayerTexture;
    private final int maxTimeInHive;
    private final Float attackDamage;
    private final float sizeModifier;
    private final String[] traits;
    private transient String name;
    private final boolean hasHoneycomb;
    public final MutationData MutationData;
    public final ColorData ColorData;
    public final CentrifugeData CentrifugeData;
    public final BreedData BreedData;
    public final SpawnData SpawnData;
    public final TraitData TraitData;
    private transient RegistryObject<Item> combRegistryObject;
    private transient RegistryObject<Block> combBlockRegistryObject;
    private transient RegistryObject<Item> combBlockItemRegistryObject;
    private transient RegistryObject<EntityType<ResourcefulBee>> entityTypeRegistryObject;
    private transient RegistryObject<Item> spawnEggItemRegistryObject;
    //private ResourceLocation registryID; not sure if we could use this

    private CustomBeeData(String flower, String baseLayerTexture, int maxTimeInHive, Float attackDamage, float sizeModifier, String[] traits, String name, boolean hasHoneycomb, MutationData MutationData, ColorData ColorData, CentrifugeData CentrifugeData, BreedData BreedData, SpawnData SpawnData, TraitData TraitData) {
        this.flower = flower;
        this.baseLayerTexture = baseLayerTexture;
        this.maxTimeInHive = maxTimeInHive;
        this.attackDamage = attackDamage;
        this.sizeModifier = sizeModifier;
        this.traits = traits;
        this.name = name;
        this.hasHoneycomb = hasHoneycomb;
        this.MutationData = MutationData;
        this.ColorData = ColorData;
        this.CentrifugeData = CentrifugeData;
        this.BreedData = BreedData;
        this.SpawnData = SpawnData;
        this.TraitData = TraitData;
    }

    public String getFlower() { return flower == null ? BeeConstants.FLOWER_TAG_ALL : flower.toLowerCase(); }

    public boolean hasHoneycomb() { return hasHoneycomb; }

    public String getBaseLayerTexture() { return baseLayerTexture == null ? "/custom/bee" : baseLayerTexture.toLowerCase(); }

    public int getMaxTimeInHive() { return maxTimeInHive < BeeConstants.MIN_HIVE_TIME ? maxTimeInHive == 0 ? BeeConstants.MAX_TIME_IN_HIVE : BeeConstants.MIN_HIVE_TIME : maxTimeInHive; }

    public float getAttackDamage() { return attackDamage == null ? 1.0f : attackDamage; }

    public RegistryObject<Item> getCombRegistryObject() { return combRegistryObject; }

    public void setCombRegistryObject(RegistryObject<Item> combRegistryObject) { this.combRegistryObject = this.combRegistryObject == null ? combRegistryObject : this.combRegistryObject; }

    public RegistryObject<Block> getCombBlockRegistryObject() { return combBlockRegistryObject; }

    public void setCombBlockRegistryObject(RegistryObject<Block> combBlockRegistryObject) { this.combBlockRegistryObject = this.combBlockRegistryObject == null ? combBlockRegistryObject : this.combBlockRegistryObject; }

    public RegistryObject<Item> getCombBlockItemRegistryObject() { return combBlockItemRegistryObject; }

    public void setCombBlockItemRegistryObject(RegistryObject<Item> combBlockItemRegistryObject) { this.combBlockItemRegistryObject = this.combBlockItemRegistryObject == null ? combBlockItemRegistryObject : this.combBlockItemRegistryObject; }

    public RegistryObject<EntityType<ResourcefulBee>> getEntityTypeRegistryObject() {
        return entityTypeRegistryObject;
    }

    public void setEntityTypeRegistryObject(RegistryObject<EntityType<ResourcefulBee>> entityTypeRegistryObject) {
        this.entityTypeRegistryObject = this.entityTypeRegistryObject == null ? entityTypeRegistryObject : this.entityTypeRegistryObject;
    }

    public RegistryObject<Item> getSpawnEggItemRegistryObject() {
        return spawnEggItemRegistryObject;
    }

    public void setSpawnEggItemRegistryObject(RegistryObject<Item> spawnEggItemRegistryObject) {
        this.spawnEggItemRegistryObject = this.spawnEggItemRegistryObject == null ? spawnEggItemRegistryObject : this.spawnEggItemRegistryObject;
    }

    public void setName(String name) {
        this.name = this.name == null ? name.toLowerCase() : this.name;
    }

    public float getSizeModifier() { return sizeModifier != 0 ? sizeModifier : Config.BEE_SIZE_MODIFIER.get().floatValue(); }

    public String[] getTraitNames() { return traits; }

    public boolean hasTraitNames() { return traits != null && traits.length > 0;}

    public String getName() { return name.toLowerCase(); }

    public static class Builder {
        private final String flower;
        private String baseLayerTexture;
        private int maxTimeInHive;
        private Float attackDamage;
        private float sizeModifier;
        private String[] traits;
        private final String name;
        private final boolean hasHoneycomb;
        private final MutationData MutationData;
        private final ColorData ColorData;
        private final CentrifugeData CentrifugeData;
        private final BreedData BreedData;
        private final SpawnData SpawnData;
        private final TraitData TraitData;

        public Builder(String name, String flower, boolean hasHoneycomb, MutationData mutationData, ColorData colorData, CentrifugeData centrifugeData, BreedData breedData, SpawnData spawnData, TraitData traitData) {
            this.name = name;
            this.flower = flower;
            this.hasHoneycomb = hasHoneycomb;
            this.MutationData = mutationData;
            this.ColorData = colorData;
            this.CentrifugeData = centrifugeData;
            this.BreedData = breedData;
            this.SpawnData = spawnData;
            this.TraitData = traitData;
        }

        public Builder setBaseLayerTexture(String baseLayerTexture) {
            this.baseLayerTexture = baseLayerTexture;
            return this;
        }

        public Builder setMaxTimeInHive(int maxTimeInHive) {
            this.maxTimeInHive = maxTimeInHive;
            return this;
        }

        public Builder setAttackDamage(float attackDamage) {
            this.attackDamage = attackDamage;
            return this;
        }

        public Builder setSizeModifier(float sizeModifier) {
            this.sizeModifier = sizeModifier;
            return this;
        }

        public Builder setTraits(String[] traits){
            this.traits = traits;
            return this;
        }

        public CustomBeeData createCustomBee() {
            return new CustomBeeData(flower, baseLayerTexture, maxTimeInHive, attackDamage, sizeModifier, traits, name, hasHoneycomb, MutationData, ColorData, CentrifugeData, BreedData, SpawnData, TraitData);
        }
    }
}
