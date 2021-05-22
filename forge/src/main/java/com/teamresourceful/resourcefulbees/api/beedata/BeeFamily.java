package com.teamresourceful.resourcefulbees.api.beedata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.lib.BeeConstants;
import com.teamresourceful.resourcefulbees.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.utils.BeeInfoUtils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.concurrent.Immutable;
import java.util.Locale;
import java.util.Set;

@Immutable
public class BeeFamily {

    public static Codec<BeeFamily> codec(String name) {
        return RecordCodecBuilder.create(instance -> instance.group(
                Codec.doubleRange(0.0d, Double.MAX_VALUE).fieldOf("weight").orElse(BeeConstants.DEFAULT_BREED_WEIGHT).forGetter(BeeFamily::getWeight),
                Codec.doubleRange(0.0d, 1.0d).fieldOf("chance").orElse(BeeConstants.DEFAULT_BREED_CHANCE).forGetter(BeeFamily::getChance),
                Codec.STRING.fieldOf("parent1").orElse("").forGetter(BeeFamily::getParent1),
                Codec.STRING.fieldOf("parent2").orElse("").forGetter(BeeFamily::getParent2)
        ).apply(instance, (weight, chance, parent1, parent2) -> new BeeFamily(weight, chance, parent1, parent2, name)));
    }



    /**
     * The weight specified for that bee. The weight is calculated against all the other bees the bees parents can make with the same item.
     */
    private final double weight;

    /**
     * The the chance that when trying to breed this bee the breed will succeed, if the breed fails, items will be consumed but no bee will be made.
     */
    private final double chance;

    /**
     * Strings of the parent bees needed to be breed.
     */
    private final Pair<String, String> parents;

    private CustomBeeData parent1Data = CustomBeeData.DEFAULT;
    private CustomBeeData parent2Data = CustomBeeData.DEFAULT;
    private CustomBeeData childData = CustomBeeData.DEFAULT;
    private final String childName;

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

    public Entity createParent1DisplayEntity(ClientLevel level) {
        return parent1Data.getEntityType().create(level);
    }

    public Entity createParent2DisplayEntity(ClientLevel level) {
        return parent2Data.getEntityType().create(level);
    }

    public Entity createChildDisplayEntity(ClientLevel level) {
        return childData.getEntityType().create(level);
    }

    public CustomBeeData getChildData() {
        return childData;
    }

    public boolean hasValidParents() {
        return !getParent1().isEmpty() && !getParent2().isEmpty() && BeeRegistry.containsBeeType(getParent1()) && BeeRegistry.containsBeeType(getParent2());
    }
}
