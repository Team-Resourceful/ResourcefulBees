package com.teamresourceful.resourcefulbees.api.intializers;

import com.teamresourceful.resourcefulbees.api.data.BeekeeperTradeData;
import com.teamresourceful.resourcefulbees.api.data.bee.BeeCombatData;
import com.teamresourceful.resourcefulbees.api.data.bee.BeeCoreData;
import com.teamresourceful.resourcefulbees.api.data.bee.BeeTraitData;
import com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.data.bee.base.BeeData;
import com.teamresourceful.resourcefulbees.api.data.bee.breeding.BeeBreedData;
import com.teamresourceful.resourcefulbees.api.data.bee.breeding.FamilyUnit;
import com.teamresourceful.resourcefulbees.api.data.bee.mutation.BeeMutationData;
import com.teamresourceful.resourcefulbees.api.data.bee.mutation.MutationType;
import com.teamresourceful.resourcefulbees.api.data.bee.render.BeeColorData;
import com.teamresourceful.resourcefulbees.api.data.bee.render.BeeLayerData;
import com.teamresourceful.resourcefulbees.api.data.bee.render.BeeLayerTexture;
import com.teamresourceful.resourcefulbees.api.data.bee.render.BeeRenderData;
import com.teamresourceful.resourcefulbees.common.lib.enums.LayerEffect;
import com.teamresourceful.resourcefullib.common.codecs.predicates.RestrictedBlockPredicate;
import com.teamresourceful.resourcefullib.common.codecs.predicates.RestrictedEntityPredicate;
import com.teamresourceful.resourcefullib.common.codecs.predicates.RestrictedItemPredicate;
import com.teamresourceful.resourcefullib.common.collections.WeightedCollection;
import com.teamresourceful.resourcefullib.common.color.Color;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class Initializers {

    @FunctionalInterface
    public interface MutationInitializer {
        BeeMutationData create(int count, Map<MutationType, WeightedCollection<MutationType>> mutations);
    }

    @FunctionalInterface
    public interface BlockMutationInitializer {
        MutationType create(RestrictedBlockPredicate predicate, double chance, double weight);
    }

    @FunctionalInterface
    public interface EntityMutationInitializer {
        MutationType create(RestrictedEntityPredicate predicate, double chance, double weight);
    }

    @FunctionalInterface
    public interface ItemMutationInitializer {
        MutationType create(RestrictedItemPredicate predicate, double chance, double weight);
    }

    @FunctionalInterface
    public interface FluidMutationInitializer {
        MutationType create(Fluid fluid, double chance, double weight);
    }

    @FunctionalInterface
    public interface ColorInitializer {
        BeeColorData create(Color primarySpawnEggColor, Color secondarySpawnEggColor, Color jarColor);
    }

    @FunctionalInterface
    public interface CombatInitializer {
        BeeCombatData create(boolean isPassive, boolean removeStingerOnAttack, boolean inflictsPoison, boolean isInvulnerable, Map<Attribute, Double> attributes);
    }

    @FunctionalInterface
    public interface CoreInitializer {
        BeeCoreData create(String honeycomb, HolderSet<Block> flowers, HolderSet<EntityType<?>> entityFlowers, int maxTimeInHive, List<MutableComponent> lore);
    }

    @FunctionalInterface
    public interface BeekeeperTradeInitializer {
        BeekeeperTradeData create(UniformInt amount, ItemStack secondaryItem, UniformInt secondaryItemCost, float priceMultiplier, int maxTrades, int xp);
    }

    @FunctionalInterface
    public interface LayerInitializer {
        BeeLayerData create(Color color, BeeLayerTexture texture, LayerEffect effect, boolean pollenLayer, float pulseFrequency);
    }

    @FunctionalInterface
    public interface LayerTextureInitializer {
        BeeLayerTexture create(ResourceLocation texture, ResourceLocation angryTexture);
    }

    @FunctionalInterface
    public interface RenderInitializer {
        BeeRenderData create(Set<BeeLayerData> layers, BeeColorData colorData, ResourceLocation model, BeeLayerTexture texture, ResourceLocation animations, float sizeModifier);
    }

    @FunctionalInterface
    public interface TraitInitializer {
        BeeTraitData create(int range, Set<String> traits);
    }

    @FunctionalInterface
    public interface BreedInitializer {
        BeeBreedData create(Set<FamilyUnit> families, HolderSet<Item> feedItems, Optional<ItemStack> feedReturnItem, int feedAmount, int childGrowthDelay, int breedDelay);
    }

    @FunctionalInterface
    public interface FamilyUnitInitializer {
        FamilyUnit create(double weight, double chance, String parent1, String parent2, String childName);
    }

    @FunctionalInterface
    public interface DataInitializer {
        CustomBeeData create(String name, Map<ResourceLocation, BeeData<?>> data);
    }

    @FunctionalInterface
    public interface TradeInitializer {
        BeekeeperTradeData create(UniformInt amount, ItemStack secondaryItem, UniformInt secondaryItemCost, float priceMultiplier, int maxTrades, int xp);
    }

}
