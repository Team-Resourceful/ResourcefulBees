package com.resourcefulbees.resourcefulbees.data;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.config.BeeInfo;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import com.resourcefulbees.resourcefulbees.recipe.CentrifugeRecipe;
import com.resourcefulbees.resourcefulbees.registry.RegistryHandler;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.NBTIngredient;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class RecipeBuilder implements IResourceManagerReloadListener {
    private static RecipeManager recipeManager;

    @Override
    public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {
        for (Map.Entry<String, BeeData> bee : BeeInfo.getBees().entrySet()){
            if (bee.getValue().getHoneycombColor() != null && !bee.getValue().getHoneycombColor().isEmpty()) {
                if (!bee.getValue().getMainOutput().isEmpty()) {
                    if (Config.CENTRIFUGE_RECIPES.get()) {
                        IRecipe<?> honeycombCentrifuge = this.centrifugeRecipe(bee.getValue().getName(), bee.getValue().getHoneycombColor());
                        IRecipe<?> honeycombBlockCentrifuge = this.centrifugeHoneyCombBlockRecipe(bee.getValue().getName(), bee.getValue().getHoneycombColor());
                        getRecipeManager().recipes.computeIfAbsent(honeycombCentrifuge.getType(), t -> new HashMap<>()).put(honeycombCentrifuge.getId(), honeycombCentrifuge);
                        getRecipeManager().recipes.computeIfAbsent(honeycombBlockCentrifuge.getType(), t -> new HashMap<>()).put(honeycombBlockCentrifuge.getId(), honeycombBlockCentrifuge);
                    }
                }
                if (Config.HONEYCOMB_BLOCK_RECIPES.get()) {
                    IRecipe<?> honeycombBlock = this.makeHoneycombRecipe(bee.getKey(), bee.getValue().getHoneycombColor());
                    IRecipe<?> honeycomb = this.blockToHoneycombRecipe(bee.getKey(), bee.getValue().getHoneycombColor());
                    getRecipeManager().recipes.computeIfAbsent(honeycombBlock.getType(), t -> new HashMap<>()).put(honeycombBlock.getId(), honeycombBlock);
                    getRecipeManager().recipes.computeIfAbsent(honeycomb.getType(), t -> new HashMap<>()).put(honeycomb.getId(), honeycomb);
                }
            }
        }
    }

    @SubscribeEvent
    public void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(this);
        recipeManager = event.getDataPackRegistries().getRecipeManager();
    }

    private IRecipe<?> makeHoneycombRecipe(String BeeType, String Color) {
        ItemStack honeycombItemStack = new ItemStack(RegistryHandler.RESOURCEFUL_HONEYCOMB.get());
        ItemStack honeycombOutput = new ItemStack(RegistryHandler.HONEYCOMB_BLOCK_ITEM.get());

        CompoundNBT rbNBT = new CompoundNBT();
        CompoundNBT btcNBT = new CompoundNBT();
        btcNBT.putString(NBTConstants.NBT_COLOR, Color);
        btcNBT.putString(NBTConstants.NBT_BEE_TYPE, BeeType);
        rbNBT.put(NBTConstants.NBT_ROOT,btcNBT);

        honeycombItemStack.setTag(rbNBT);
        honeycombOutput.setTag(rbNBT);

        Ingredient honeycombItem = new CustomNBTIngredient(honeycombItemStack);

        NonNullList<Ingredient> inputs = NonNullList.from(Ingredient.EMPTY,
                honeycombItem, honeycombItem, honeycombItem,
                honeycombItem, honeycombItem, honeycombItem,
                honeycombItem, honeycombItem, honeycombItem
        );

        ResourceLocation name = new ResourceLocation(ResourcefulBees.MOD_ID, BeeType + "_honeycomb_block");

        return new ShapedRecipe(name, "", 3, 3, inputs, honeycombOutput);
    }

    private IRecipe<?> centrifugeRecipe(String BeeType, String Color) {
        BeeData info = BeeInfo.getInfo(BeeType);
        ItemStack honeycombItemStack = new ItemStack(RegistryHandler.RESOURCEFUL_HONEYCOMB.get(), info.getMainInputCount());
        final CompoundNBT honeycombItemStackTag = honeycombItemStack.getOrCreateChildTag(NBTConstants.NBT_ROOT);
        honeycombItemStackTag.putString(NBTConstants.NBT_COLOR, Color);
        honeycombItemStackTag.putString(NBTConstants.NBT_BEE_TYPE, BeeType);

        Ingredient honeycombItem = new CustomNBTIngredient(honeycombItemStack);

        NonNullList<Pair<ItemStack,Double>> outputs = NonNullList.from(
                Pair.of(ItemStack.EMPTY, 0.0),
                Pair.of(new ItemStack(BeeInfoUtils.getItem(info.getMainOutput()), info.getMainOutputCount()), info.getMainOutputWeight()),
                Pair.of(new ItemStack(BeeInfoUtils.getItem(info.getSecondaryOutput()), info.getSecondaryOutputCount()), info.getSecondaryOutputWeight()),
                Pair.of(new ItemStack(BeeInfoUtils.getItem(info.getBottleOutput()), info.getBottleOutputCount()), info.getBottleOutputWeight())
        );

        ResourceLocation name = new ResourceLocation(ResourcefulBees.MOD_ID, BeeType + "_honeycomb_centrifuge");

        return new CentrifugeRecipe(name,honeycombItem,outputs, Config.CENTRIFUGE_RECIPE_TIME.get() * 20, false);
    }

    private IRecipe<?> centrifugeHoneyCombBlockRecipe(String BeeType, String Color) {
        BeeData info = BeeInfo.getInfo(BeeType);
        ItemStack honeycombBlockItemStack = new ItemStack(RegistryHandler.HONEYCOMB_BLOCK_ITEM.get(), info.getMainInputCount());
        final CompoundNBT honeycombBlockItemStackTag = honeycombBlockItemStack.getOrCreateChildTag(NBTConstants.NBT_ROOT);
        honeycombBlockItemStackTag.putString(NBTConstants.NBT_COLOR, Color);
        honeycombBlockItemStackTag.putString(NBTConstants.NBT_BEE_TYPE, BeeType);

        Ingredient honeycombblockItem = new CustomNBTIngredient(honeycombBlockItemStack);

        NonNullList<Pair<ItemStack,Double>> outputs = NonNullList.from(
                Pair.of(ItemStack.EMPTY, 0.0),
                Pair.of(new ItemStack(BeeInfoUtils.getItem(info.getMainOutput()), info.getMainOutputCount() * 9), info.getMainOutputWeight()),
                Pair.of(new ItemStack(BeeInfoUtils.getItem(info.getSecondaryOutput()), info.getSecondaryOutputCount() * 9), info.getSecondaryOutputWeight()),
                Pair.of(new ItemStack(BeeInfoUtils.getItem(info.getBottleOutput()), info.getBottleOutputCount() * 9), info.getBottleOutputWeight())
        );

        ResourceLocation name = new ResourceLocation(ResourcefulBees.MOD_ID, BeeType + "_honeycomb_block_centrifuge");

        return new CentrifugeRecipe(name,honeycombblockItem,outputs, Config.CENTRIFUGE_RECIPE_TIME.get() * 20, true);
    }

    private IRecipe<?> blockToHoneycombRecipe(String BeeType, String Color) {
        ItemStack honeycombItemStack = new ItemStack(RegistryHandler.HONEYCOMB_BLOCK_ITEM.get());
        ItemStack honeycombOutput = new ItemStack(RegistryHandler.RESOURCEFUL_HONEYCOMB.get());

        CompoundNBT rbNBT = new CompoundNBT();
        CompoundNBT btcNBT = new CompoundNBT();
        btcNBT.putString(NBTConstants.NBT_COLOR, Color);
        btcNBT.putString(NBTConstants.NBT_BEE_TYPE, BeeType);
        rbNBT.put(NBTConstants.NBT_ROOT,btcNBT);

        honeycombItemStack.setTag(rbNBT);
        honeycombOutput.setTag(rbNBT);
        honeycombOutput.setCount(9);

        Ingredient honeycombItem = new CustomNBTIngredient(honeycombItemStack);

        ResourceLocation name = new ResourceLocation(ResourcefulBees.MOD_ID, BeeType + "_block_to_honeycomb");

        return new ShapelessRecipe(name, "",honeycombOutput, NonNullList.from(Ingredient.EMPTY, honeycombItem));
    }

    private static class CustomNBTIngredient extends NBTIngredient
    {
        public CustomNBTIngredient(ItemStack stack)
        {
            super(stack);
        }
    }

    public static RecipeManager getRecipeManager() {
        if (!recipeManager.recipes.getClass().equals(HashMap.class)) {
            recipeManager.recipes = new HashMap<>(recipeManager.recipes);
            recipeManager.recipes.replaceAll((t, v) -> new HashMap<>(recipeManager.recipes.get(t)));
        }

        return recipeManager;
    }
}
