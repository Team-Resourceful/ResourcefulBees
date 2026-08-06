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
import com.teamresourceful.resourcefulbees.api.data.shared.RegistryPredicate;
import com.teamresourceful.resourcefulbees.common.lib.codecs.RestrictedBlockPredicate;
import com.teamresourceful.resourcefulbees.common.lib.codecs.RestrictedItemPredicate;
import com.teamresourceful.resourcefulbees.common.lib.enums.LayerEffect;

import com.teamresourceful.resourcefullib.common.codecs.predicates.RestrictedEntityPredicate;

import com.teamresourceful.resourcefullib.common.color.Color;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@NullMarked
public final class Initializers {

    @FunctionalInterface
    public interface MutationInitializer {
        BeeMutationData create(int count, Identifier id);
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
        BeeCombatData create(boolean isPassive, boolean removeStingerOnAttack, boolean inflictsPoison, boolean isInvulnerable, Map<Holder<Attribute>, Double> attributes);
    }

    @FunctionalInterface
    public interface CoreInitializer {
        BeeCoreData create(String honeycomb, RegistryPredicate<Block> flowers, RegistryPredicate<EntityType<?>> entityFlowers, int maxTimeInHive, List<Component> lore);
    }

    @FunctionalInterface
    public interface BeekeeperTradeInitializer {
        BeekeeperTradeData create(UniformInt amount, Item secondaryItem, UniformInt secondaryItemCost, float priceMultiplier, int maxTrades, int xp);
    }

    @FunctionalInterface
    public interface LayerInitializer {
        BeeLayerData create(Color color, BeeLayerTexture texture, LayerEffect effect, boolean pollenLayer);
    }

    @FunctionalInterface
    public interface LayerTextureInitializer {
        BeeLayerTexture create(Identifier texture, Identifier angryTexture);
    }

    @FunctionalInterface
    public interface RenderInitializer {
        BeeRenderData create(Set<BeeLayerData> layers, BeeColorData colorData, Identifier model, BeeLayerTexture texture, Identifier animations, float sizeModifier, float pulseFrequency);
    }

    @FunctionalInterface
    public interface TraitInitializer {
        BeeTraitData create(int range, Set<String> traits);
    }

    @FunctionalInterface
    public interface BreedInitializer {
        BeeBreedData create(Set<FamilyUnit> families, RegistryPredicate<Item> feedItems, Optional<ItemStackTemplate> feedReturnItem, int feedAmount, int childGrowthDelay, int breedDelay);
    }

    @FunctionalInterface
    public interface FamilyUnitInitializer {
        FamilyUnit create(double weight, double chance, Identifier parent1, Identifier parent2, Identifier childName);
    }

    @FunctionalInterface
    public interface DataInitializer {
        CustomBeeData create(Identifier name, Map<Identifier, BeeData<?>> data);
    }

    @FunctionalInterface
    public interface TradeInitializer {
        BeekeeperTradeData create(UniformInt amount, ItemStack secondaryItem, UniformInt secondaryItemCost, float priceMultiplier, int maxTrades, int xp);
    }

}
