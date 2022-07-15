package com.teamresourceful.resourcefulbees.api.honeydata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.api.honeydata.fluid.FluidAttributeData;
import com.teamresourceful.resourcefulbees.api.honeydata.fluid.FluidRenderData;
import com.teamresourceful.resourcefulbees.common.block.CustomHoneyFluidBlock;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.fluids.CustomHoneyFluid;
import com.teamresourceful.resourcefulbees.common.fluids.HoneyFluidRenderProperties;
import com.teamresourceful.resourcefulbees.common.item.CustomHoneyBucketItem;
import com.teamresourceful.resourcefulbees.common.registry.custom.HoneyRegistry;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ItemGroupResourcefulBees;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModFluids;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;

public record HoneyFluidData(boolean generate, String name, FluidRenderData renderData, FluidAttributeData attributes, Fluid stillFluid, Fluid flowingFluid, Item fluidBucket, Block fluidBlock) {

    public static HoneyFluidData getDefault(String name) {
        return new HoneyFluidData(false, name, FluidRenderData.DEFAULT, FluidAttributeData.DEFAULT, Fluids.EMPTY, Fluids.EMPTY, Items.AIR, Blocks.AIR);
    }

    public static Codec<HoneyFluidData> codec(String name) {
        return RecordCodecBuilder.create(instance -> instance.group(
                RecordCodecBuilder.point(true),
                RecordCodecBuilder.point(name),
                FluidRenderData.CODEC.fieldOf("rendering").orElse(FluidRenderData.DEFAULT).forGetter(HoneyFluidData::renderData),
                FluidAttributeData.CODEC.fieldOf("attributes").orElse(FluidAttributeData.DEFAULT).forGetter(HoneyFluidData::attributes),
                Registry.FLUID.byNameCodec().fieldOf("stillFluid").forGetter(HoneyFluidData::stillFluid),
                Registry.FLUID.byNameCodec().fieldOf("flowingFluid").forGetter(HoneyFluidData::flowingFluid),
                Registry.ITEM.byNameCodec().fieldOf("fluidBucket").forGetter(HoneyFluidData::fluidBucket),
                Registry.BLOCK.byNameCodec().fieldOf("fluidBlock").forGetter(HoneyFluidData::fluidBlock)
        ).apply(instance, HoneyFluidData::new));
    }

    public HoneyFluidData {
        if (Boolean.TRUE.equals(CommonConfig.HONEY_GENERATE_FLUIDS.get()) && generate && HoneyRegistry.getRegistry().canGenerate()) {

            ForgeFlowingFluid.Properties[] properties = {null};

            RegistryObject<FluidType> fluidType = ModFluids.FLUID_TYPES.register(name + "_honey", () -> honeyFluid(attributes.getProperties(), renderData));
            RegistryObject<FlowingFluid> stillFluidRegistry = ModFluids.STILL_HONEY_FLUIDS.register(name + "_honey", () -> new CustomHoneyFluid.Source(properties[0], this));
            RegistryObject<FlowingFluid> flowingFluidRegistry = ModFluids.FLOWING_HONEY_FLUIDS.register(name + "_honey_flowing", () -> new CustomHoneyFluid.Flowing(properties[0], this));
            RegistryObject<Item> fluidBucketRegistry = ModItems.HONEY_BUCKET_ITEMS.register(name + "_honey_bucket", () -> new CustomHoneyBucketItem(stillFluidRegistry, new Item.Properties().tab(ItemGroupResourcefulBees.RESOURCEFUL_BEES_HONEY).craftRemainder(Items.BUCKET).stacksTo(1), this));
            RegistryObject<LiquidBlock> blockFluidRegistry = ModBlocks.HONEY_FLUID_BLOCKS.register(name + "_honey", () -> new CustomHoneyFluidBlock(stillFluidRegistry, BlockBehaviour.Properties.of(Material.WATER).noCollission().strength(100.0F).noLootTable(), this));


            properties[0] = new ForgeFlowingFluid.Properties(fluidType, stillFluidRegistry, flowingFluidRegistry)
                    .bucket(fluidBucketRegistry)
                    .block(blockFluidRegistry)
                    .tickRate(20);
        }
    }

    private static FluidType honeyFluid(FluidType.Properties properties, FluidRenderData renderData) {
        // We are forced to use an anon object here because forge calls initClient in base constructor therefore making it so we cant pass data in.
        return new FluidType(properties) {
            @Override
            public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                consumer.accept(new HoneyFluidRenderProperties(renderData));
            }
        };
    }
}
