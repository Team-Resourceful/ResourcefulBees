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
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@ApiStatus.NonExtendable
public class InitializerApi {

    private Initializers.DataInitializer data;
    private Initializers.CombatInitializer combat;
    private Initializers.CoreInitializer core;
    private Initializers.TraitInitializer traits;
    private Initializers.BeekeeperTradeInitializer beekeeperTrade;
    //Rendering
    private Initializers.RenderInitializer render;
    private Initializers.LayerTextureInitializer layerTexture;
    private Initializers.LayerInitializer layer;
    private Initializers.ColorInitializer color;
    //Mutations
    private Initializers.MutationInitializer mutation;
    private Initializers.BlockMutationInitializer blockMutation;
    private Initializers.ItemMutationInitializer itemMutation;
    private Initializers.FluidMutationInitializer fluidMutation;
    private Initializers.EntityMutationInitializer entityMutation;
    //Breeding
    private Initializers.BreedInitializer breeding;
    private Initializers.FamilyUnitInitializer familyUnit;

    public CustomBeeData data(String name, Map<ResourceLocation, BeeData<?>> data) {
        return this.data.create(name, data);
    }

    public BeeCombatData combat(boolean isPassive, boolean removeStingerOnAttack, boolean inflictsPoison, boolean isInvulnerable, Map<Attribute, Double> attributes) {
        return this.combat.create(isPassive, removeStingerOnAttack, inflictsPoison, isInvulnerable, attributes);
    }

    public BeeCoreData core(String honeycomb, HolderSet<Block> flowers, HolderSet<EntityType<?>> entityFlowers, int maxTimeInHive, List<MutableComponent> lore) {
        return this.core.create(honeycomb, flowers, entityFlowers, maxTimeInHive, lore);
    }

    public BeekeeperTradeData beekeeperTrade(UniformInt amount, ItemStack secondaryItem, UniformInt secondaryItemCost, float priceMultiplier, int maxTrades, int xp) {
        return this.beekeeperTrade.create(amount, secondaryItem, secondaryItemCost, priceMultiplier, maxTrades, xp);
    }

    public BeeTraitData trait(int range, Set<String> traits) {
        return this.traits.create(range, traits);
    }

    public BeeRenderData render(Set<BeeLayerData> layers, BeeColorData colorData, ResourceLocation model, BeeLayerTexture texture, ResourceLocation animations, float sizeModifier) {
        return this.render.create(layers, colorData, model, texture, animations, sizeModifier);
    }

    public BeeLayerTexture layerTexture(ResourceLocation texture, ResourceLocation angryTexture) {
        return this.layerTexture.create(texture, angryTexture);
    }

    public BeeLayerData layer(Color color, BeeLayerTexture texture, LayerEffect effect, boolean pollenLayer, float pulseFrequency) {
        return this.layer.create(color, texture, effect, pollenLayer, pulseFrequency);
    }

    public BeeColorData color(Color primarySpawnEggColor, Color secondarySpawnEggColor, Color jarColor) {
        return this.color.create(primarySpawnEggColor, secondarySpawnEggColor, jarColor);
    }

    public BeeMutationData mutation(int count, Map<MutationType, WeightedCollection<MutationType>> mutations) {
        return this.mutation.create(count, mutations);
    }

    public MutationType blockMutation(RestrictedBlockPredicate block, double chance, double weight) {
        return this.blockMutation.create(block, chance, weight);
    }

    public MutationType itemMutation(RestrictedItemPredicate item, double chance, double weight) {
        return this.itemMutation.create(item, chance, weight);
    }

    public MutationType fluidMutation(Fluid fluid, double chance, double weight) {
        return this.fluidMutation.create(fluid, chance, weight);
    }

    public MutationType entityMutation(RestrictedEntityPredicate entity, double chance, double weight) {
        return this.entityMutation.create(entity, chance, weight);
    }

    public BeeBreedData breeding(Set<FamilyUnit> families, HolderSet<Item> feedItems, Optional<ItemStack> feedReturnItem, int feedAmount, int childGrowthDelay, int breedDelay) {
        return this.breeding.create(families, feedItems, feedReturnItem, feedAmount, childGrowthDelay, breedDelay);
    }

    public FamilyUnit familyUnit(double weight, double chance, String parent1, String parent2, String childName) {
        return this.familyUnit.create(weight, chance, parent1, parent2, childName);
    }

    @ApiStatus.Internal
    public void setData(Initializers.DataInitializer data) {
        this.data = data;
    }

    @ApiStatus.Internal
    public void setCombat(Initializers.CombatInitializer combat) {
        this.combat = combat;
    }

    @ApiStatus.Internal
    public void setCore(Initializers.CoreInitializer core) {
        this.core = core;
    }

    @ApiStatus.Internal
    public void setBeekeeperTrade(Initializers.BeekeeperTradeInitializer beekeeperTrade) {
        this.beekeeperTrade = beekeeperTrade;
    }

    @ApiStatus.Internal
    public void setTraits(Initializers.TraitInitializer traits) {
        this.traits = traits;
    }

    @ApiStatus.Internal
    public void setRender(Initializers.RenderInitializer render) {
        this.render = render;
    }

    @ApiStatus.Internal
    public void setLayerTexture(Initializers.LayerTextureInitializer layerTexture) {
        this.layerTexture = layerTexture;
    }

    @ApiStatus.Internal
    public void setLayer(Initializers.LayerInitializer layer) {
        this.layer = layer;
    }

    @ApiStatus.Internal
    public void setColor(Initializers.ColorInitializer color) {
        this.color = color;
    }

    @ApiStatus.Internal
    public void setMutation(Initializers.MutationInitializer mutation) {
        this.mutation = mutation;
    }

    @ApiStatus.Internal
    public void setBlockMutation(Initializers.BlockMutationInitializer blockMutation) {
        this.blockMutation = blockMutation;
    }

    @ApiStatus.Internal
    public void setItemMutation(Initializers.ItemMutationInitializer itemMutation) {
        this.itemMutation = itemMutation;
    }

    @ApiStatus.Internal
    public void setFluidMutation(Initializers.FluidMutationInitializer fluidMutation) {
        this.fluidMutation = fluidMutation;
    }

    @ApiStatus.Internal
    public void setEntityMutation(Initializers.EntityMutationInitializer entityMutation) {
        this.entityMutation = entityMutation;
    }

    @ApiStatus.Internal
    public void setBreeding(Initializers.BreedInitializer breeding) {
        this.breeding = breeding;
    }

    @ApiStatus.Internal
    public void setFamilyUnit(Initializers.FamilyUnitInitializer familyUnit) {
        this.familyUnit = familyUnit;
    }
}
