package com.resourcefulbees.resourcefulbees.api.honeydata;

import com.resourcefulbees.resourcefulbees.registry.ModBlocks;
import com.resourcefulbees.resourcefulbees.registry.ModFluids;
import com.resourcefulbees.resourcefulbees.registry.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.item.Foods;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

public class VanillaHoneyBottleData extends HoneyBottleData {

    @Override
    public boolean doGenerateHoneyBlock() {
        return true;
    }

    @Override
    public boolean doGenerateHoneyFluid() {
        return true;
    }

    @Override
    public String getName() {
        return "honey";
    }

    @Override
    public Item getHoneyBottle() {
        return Items.HONEY_BOTTLE;
    }

    @Override
    public Item getHoneyBlockItem() {
        return Items.HONEY_BLOCK;
    }

    @Override
    public Block getHoneyBlock() {
        return Blocks.HONEY_BLOCK;
    }

    @Override
    public FlowingFluid getHoneyStillFluid() {
        return ModFluids.HONEY_STILL.get();
    }

    @Override
    public FlowingFluid getHoneyFlowingFluid() {
        return ModFluids.HONEY_FLOWING.get();
    }

    @Override
    public Item getHoneyBucketItem() {
        return ModItems.HONEY_FLUID_BUCKET.get();
    }

    @Override
    public FlowingFluidBlock getHoneyFluidBlock() {
        return ModBlocks.HONEY_FLUID_BLOCK.get();
    }

    @Override
    public int getHunger() {
        return Foods.HONEY_BOTTLE.getNutrition();
    }

    @Override
    public float getSaturation() {
        return Foods.HONEY_BOTTLE.getSaturationModifier();
    }

    @Override
    public TranslationTextComponent getFluidTranslation() {
        return new TranslationTextComponent("fluid.resourcefulbees.honey");
    }

    @Override
    public TranslationTextComponent getBottleTranslation() {
        return new TranslationTextComponent(getHoneyBottle().getDescriptionId());
    }

    @Override
    public TranslationTextComponent getBlockTranslation() {
        return new TranslationTextComponent(getHoneyBlockItem().getDescriptionId());
    }

    @Override
    public TranslationTextComponent getBucketTranslation() {
        return new TranslationTextComponent(getHoneyBucketItem().getDescriptionId());
    }
}
