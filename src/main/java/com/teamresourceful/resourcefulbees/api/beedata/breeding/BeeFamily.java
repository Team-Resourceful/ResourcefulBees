package com.teamresourceful.resourcefulbees.api.beedata.breeding;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.lib.constants.BeeConstants;
import com.teamresourceful.resourcefulbees.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.utils.BeeInfoUtils;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Locale;
import java.util.Set;

@Unmodifiable
public class BeeFamily {

    public static Codec<BeeFamily> codec(String name) {
        return RecordCodecBuilder.create(instance -> instance.group(
                Codec.doubleRange(0.0d, Double.MAX_VALUE).fieldOf("weight").orElse(BeeConstants.DEFAULT_BREED_WEIGHT).forGetter(BeeFamily::getWeight),
                Codec.doubleRange(0.0d, 1.0d).fieldOf("chance").orElse(BeeConstants.DEFAULT_BREED_CHANCE).forGetter(BeeFamily::getChance),
                Codec.STRING.fieldOf("parent1").orElse("").forGetter(BeeFamily::getParent1),
                Codec.STRING.fieldOf("parent2").orElse("").forGetter(BeeFamily::getParent2)
        ).apply(instance, (weight, chance, parent1, parent2) -> new BeeFamily(weight, chance, parent1, parent2, name)));
    }

    protected double weight;
    protected double chance;
    protected Pair<String, String> parents;
    private CustomBeeData parent1Data = CustomBeeData.DEFAULT;
    private CustomBeeData parent2Data = CustomBeeData.DEFAULT;
    private CustomBeeData childData = CustomBeeData.DEFAULT;
    protected String childName;

    private BeeFamily(double weight, double chance, String parent1, String parent2, String childName) {
        this.weight = weight;
        this.chance = chance;
        this.parents = BeeInfoUtils.sortParents(parent1, parent2);
        this.childName = childName.toLowerCase(Locale.ENGLISH).replace(" ", "_");
    }

    public void postInit() {
        this.parent1Data = BeeRegistry.getRegistry().getBeeData(parents.getLeft());
        this.parent2Data = BeeRegistry.getRegistry().getBeeData(parents.getRight());
        this.childData = BeeRegistry.getRegistry().getBeeData(childName);
    }

    public double getWeight() {
        return weight;
    }

    public double getChance() {
        return chance;
    }

    public Pair<String, String> getParents() {
        return parents;
    }

    public String getParent1() {
        return parents.getLeft();
    }

    public String getParent2() {
        return parents.getRight();
    }

    public String getChild() {
        return childName;
    }

    public CustomBeeData getParent1Data() {
        return parent1Data;
    }

    public CustomBeeData getParent2Data() {
        return parent2Data;
    }

    public Set<ItemStack> getParent1FeedItemStacks() {
        return parent1Data.getBreedData().getFeedItemStacks();
    }

    public Set<ItemStack> getParent2FeedItemStacks() {
        return parent2Data.getBreedData().getFeedItemStacks();
    }

    public Entity createParent1DisplayEntity(ClientWorld level) {
        return parent1Data.getEntityType().create(level);
    }

    public Entity createParent2DisplayEntity(ClientWorld level) {
        return parent2Data.getEntityType().create(level);
    }

    public Entity createChildDisplayEntity(ClientWorld level) {
        return childData.getEntityType().create(level);
    }

    public CustomBeeData getChildData() {
        return childData;
    }

    public boolean hasValidParents() {
        return !getParent1().isEmpty() && !getParent2().isEmpty() && BeeRegistry.containsBeeType(getParent1()) && BeeRegistry.containsBeeType(getParent2());
    }

    public BeeFamily toImmutable() {
        return this;
    }

    public static class Mutable extends BeeFamily {
        public Mutable(double weight, double chance, String parent1, String parent2, String childName) {
            super(weight, chance, parent1, parent2, childName);
        }

        public Mutable() {
            super(1.0d, 1.0d, "", "", "");
        }

        public BeeFamily setWeight(double weight) {
            this.weight = weight;
            return this;
        }

        public BeeFamily setChance(double chance) {
            this.chance = chance;
            return this;
        }

        public BeeFamily setParents(Pair<String, String> parents) {
            this.parents = parents;
            return this;
        }

        public BeeFamily setChildName(String childName) {
            this.childName = childName;
            return this;
        }

        @Override
        public BeeFamily toImmutable() {
            return new BeeFamily(this.weight, this.chance, this.getParent1(), this.getParent2(), this.childName);
        }
    }
}
