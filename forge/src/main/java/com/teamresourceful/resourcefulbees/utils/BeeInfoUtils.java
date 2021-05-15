package com.teamresourceful.resourcefulbees.utils;

import com.google.common.base.Splitter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.platform.InputConstants;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.ICustomBee;
import com.teamresourceful.resourcefulbees.api.beedata.BreedData;
import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.config.Config;
import com.teamresourceful.resourcefulbees.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.fluids.HoneyFlowingFluid;
import com.teamresourceful.resourcefulbees.item.BeeJar;
import com.teamresourceful.resourcefulbees.item.CustomHoneyBottleItem;
import com.teamresourceful.resourcefulbees.lib.BeeConstants;
import com.teamresourceful.resourcefulbees.lib.LightLevels;
import com.teamresourceful.resourcefulbees.lib.ModConstants;
import com.teamresourceful.resourcefulbees.lib.NBTConstants;
import com.teamresourceful.resourcefulbees.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.registry.ModFluids;
import com.teamresourceful.resourcefulbees.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.teamresourceful.resourcefulbees.lib.BeeConstants.VANILLA_BEE_COLOR;

public class BeeInfoUtils {

    private BeeInfoUtils() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static void buildFamilyTree(CustomBeeData bee) {
        if (bee.getBreedData().hasParents()) {
            Iterator<String> parent1 = bee.getBreedData().getParent1().iterator();
            Iterator<String> parent2 = bee.getBreedData().getParent2().iterator();

            while (parent1.hasNext() && parent2.hasNext()) {
                String p1 = parent1.next();
                String p2 = parent2.next();
                BeeRegistry.getRegistry().familyTree.computeIfAbsent(sortParents(p1, p2), k -> new RandomCollection<>()).add(bee.getBreedData().getBreedWeight(), bee);
            }
        }

        BeeRegistry.getRegistry().familyTree.computeIfAbsent(Pair.of(bee.getCoreData().getName(), bee.getCoreData().getName()), k -> new RandomCollection<>()).add(bee.getBreedData().getBreedWeight(), bee);
    }

    public static Pair<String, String> sortParents(String parent1, String parent2) {
        return parent1.compareTo(parent2) > 0 ? Pair.of(parent1, parent2) : Pair.of(parent2, parent1);
    }

    public static void parseBiomes(CustomBeeData bee) {
        if (!bee.getSpawnData().getBiomeWhitelist().isEmpty()) {
            Set<ResourceLocation> whitelist = new HashSet<>(getBiomeSet(bee.getSpawnData().getBiomeWhitelist()));
            Set<ResourceLocation> blacklist = new HashSet<>();
            if (!bee.getSpawnData().getBiomeBlacklist().isEmpty())
                blacklist = getBiomeSet(bee.getSpawnData().getBiomeBlacklist());
            updateSpawnableBiomes(whitelist, blacklist, bee);
        }
    }

    private static Set<ResourceLocation> getBiomeSet(String list) {
        Set<ResourceLocation> set = new HashSet<>();
        if (list.contains(BeeConstants.TAG_PREFIX))
            set.addAll(parseBiomeListFromTag(list));
        else
            set.addAll(parseBiomeList(list));
        return set;
    }

    private static Set<ResourceLocation> parseBiomeListFromTag(String list) {
        Set<ResourceLocation> biomeSet = new HashSet<>();
        if (Config.USE_FORGE_DICTIONARIES.get()) {
            Splitter.on(",").trimResults().split(list.replace(BeeConstants.TAG_PREFIX, ""))
                    .forEach(s -> BiomeDictionary.getBiomes(BiomeDictionary.Type.getType(s))
                            .forEach(biomeRegistryKey -> biomeSet.add(biomeRegistryKey.location())));
        } else {
            Splitter.on(",").trimResults().split(list.replace(BeeConstants.TAG_PREFIX, "")).forEach(s -> {
                if (com.teamresourceful.resourcefulbees.registry.BiomeDictionary.getTypes().containsKey(s)) {
                    biomeSet.addAll(com.teamresourceful.resourcefulbees.registry.BiomeDictionary.getTypes().get(s));
                }
            });
        }

        return biomeSet;
    }

    private static Set<ResourceLocation> parseBiomeList(String list) {
        Set<ResourceLocation> biomeSet = new HashSet<>();
        Splitter.on(',').trimResults().split(list).forEach(s -> biomeSet.add(new ResourceLocation(s)));

        return biomeSet;
    }

    private static void updateSpawnableBiomes(Set<ResourceLocation> whitelist, Set<ResourceLocation> blacklist, CustomBeeData bee) {
        whitelist.stream()
                .filter(resourceLocation -> !blacklist.contains(resourceLocation))
                .forEach(resourceLocation -> BeeRegistry.getSpawnableBiomes().computeIfAbsent(resourceLocation, k -> new RandomCollection<>()).add(bee.getSpawnData().getSpawnWeight(), bee));
    }

    /**
     * Returns new Resource Location with given input.
     *
     * @param resource Resource input as String in the form of "mod_id:item_or_block_id".
     * @return Returns New Resource Location for given input.
     */
    public static @Nullable ResourceLocation getResource(String resource) {
        return ResourceLocation.tryParse(resource);
    }

    public static Item getItem(String itemName) {
        return ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(itemName));
    }

    public static Block getBlock(String blockName) {
        return ForgeRegistries.BLOCKS.getValue(ResourceLocation.tryParse(blockName));
    }

    public static Fluid getFluid(String fluidName) {
        return ForgeRegistries.FLUIDS.getValue(ResourceLocation.tryParse(fluidName));
    }

    public static Biome getBiome(String biomeName) {
        return ForgeRegistries.BIOMES.getValue(ResourceLocation.tryParse(biomeName));
    }

    public static ResourceLocation getCustomBeeEntityRegistryID(String beeType) {
        return new ResourceLocation(ResourcefulBees.MOD_ID + ":" + beeType + "_bee");
    }

    public static @Nullable EntityType<?> getEntityType(String entityName) {
        return ForgeRegistries.ENTITIES.getValue(ResourceLocation.tryParse(entityName));
    }

    public static @Nullable EntityType<?> getEntityType(ResourceLocation entityId) {
        return ForgeRegistries.ENTITIES.getValue(entityId);
    }

    public static Tag<Item> getItemTag(String itemTag) {
        return ItemTags.getAllTags().getTag(ResourceLocation.tryParse(itemTag));
    }

    public static Tag<Fluid> getFluidTag(String fluidTag) {
        return FluidTags.func_226157_a_().getTag(ResourceLocation.tryParse(fluidTag));
    }

    public static Tag<Block> getBlockTag(String blockTag) {
        return BlockTags.getAllTags().getTag(ResourceLocation.tryParse(blockTag));
    }

    public static Tag<Block> getValidApiaryTag() {
        return BlockTags.getAllTags().getTag(VALID_APIARY);
    }

    public static void makeValidApiaryTag() {
        BlockTags.bind("resourcefulbees:valid_apiary");
    }

    private static final ResourceLocation VALID_APIARY = new ResourceLocation("resourcefulbees:valid_apiary");

    public static boolean isValidBreedItem(@NotNull ItemStack stack, BreedData breedData) {
        return breedData.getFeedItems().contains(stack.getItem());
    }


    public static void flagBeesInRange(BlockPos pos, Level world) {
        BoundingBox box = BoundingBox.createProper(pos.getX() + 10, pos.getY() + 10, pos.getZ() + 10, pos.getX() - 10, pos.getY() - 10, pos.getZ() - 10);
        AABB aabb = AABB.of(box);
        if (world != null) {
            List<CustomBeeEntity> list = world.getEntitiesOfClass(CustomBeeEntity.class, aabb);
            list.forEach(customBeeEntity -> customBeeEntity.setHasHiveInRange(true));
        }
    }

    public static List<String> getLoreLines(CompoundTag outputNBT) {
        if (outputNBT.isEmpty()) return new LinkedList<>();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(outputNBT.toString());
        String nbtString = "NBT: " + gson.toJson(je);
        return Arrays.asList(nbtString.split("\n"));
    }

    public static boolean isShiftPressed() {
        long windowID = Minecraft.getInstance().getWindow().getWindow();
        return InputConstants.isKeyDown(windowID, GLFW.GLFW_KEY_LEFT_SHIFT) || InputConstants.isKeyDown(windowID, GLFW.GLFW_KEY_RIGHT_SHIFT);
    }

    public static TranslatableComponent getSizeName(float sizeModifier) {
        if (sizeModifier < 0.75) {
            return new TranslatableComponent("bees.resourcefulbees.size.tiny");
        } else if (sizeModifier < 1) {
            return new TranslatableComponent("bees.resourcefulbees.size.small");
        } else if (sizeModifier == 1) {
            return new TranslatableComponent("bees.resourcefulbees.size.regular");
        } else if (sizeModifier <= 1.5) {
            return new TranslatableComponent("bees.resourcefulbees.size.large");
        } else {
            return new TranslatableComponent("bees.resourcefulbees.size.giant");
        }
    }

    public static Component getYesNo(boolean bool) {
        return bool ? new TranslatableComponent("gui.resourcefulbees.yes") : new TranslatableComponent("gui.resourcefulbees.no");
    }

    public static TranslatableComponent getLightName(LightLevels light) {
        switch (light) {
            case DAY:
                return new TranslatableComponent("gui.resourcefulbees.light.day");
            case NIGHT:
                return new TranslatableComponent("gui.resourcefulbees.light.night");
            default:
                return new TranslatableComponent("gui.resourcefulbees.light.any");
        }
    }

    public static List<ItemStack> getBreedItems(BreedData parent1Data) {
        if (!parent1Data.getFeedItems().isEmpty()) return Collections.emptyList();
        return parent1Data.getFeedItems().stream().map(f -> new ItemStack(f, parent1Data.getFeedAmount())).collect(Collectors.toList());
    }

    public static void ageBee(int ticksInHive, Bee beeEntity) {
        int i = beeEntity.getAge();
        if (i < 0) {
            beeEntity.setAge(Math.min(0, i + ticksInHive));
        } else if (i > 0) {
            beeEntity.setAge(Math.max(0, i - ticksInHive));
        }

        if (beeEntity instanceof CustomBeeEntity) {
            ((CustomBeeEntity) beeEntity).setLoveTime(Math.max(0, beeEntity.getInLoveTime() - ticksInHive));
        } else {
            beeEntity.setInLoveTime(Math.max(0, beeEntity.getInLoveTime() - ticksInHive));
        }
        beeEntity.resetTicksWithoutNectarSinceExitingHive();
    }

    public static void setEntityLocationAndAngle(BlockPos blockpos, Direction direction, Entity entity) {
        EntityDimensions size = entity.getDimensions(Pose.STANDING);
        double d0 = 0.65D + size.width / 2.0F;
        double d1 = blockpos.getX() + 0.5D + d0 * direction.getStepX();
        double d2 = blockpos.getY() + Math.max(0.5D - (size.height / 2.0F), 0);
        double d3 = blockpos.getZ() + 0.5D + d0 * direction.getStepZ();
        entity.moveTo(d1, d2, d3, entity.yRot, entity.xRot);
    }

    public static @NotNull CompoundTag createJarBeeTag(Bee beeEntity, String nbtTagID) {
        String type = EntityType.getKey(beeEntity.getType()).toString();
        CompoundTag nbt = new CompoundTag();
        nbt.putString(nbtTagID, type);

        beeEntity.saveWithoutId(nbt);

        String beeColor = VANILLA_BEE_COLOR;

        if (beeEntity instanceof ICustomBee) {
            ICustomBee iCustomBee = (ICustomBee) beeEntity;
            nbt.putString(NBTConstants.NBT_BEE_TYPE, iCustomBee.getBeeType());
            beeColor = iCustomBee.getRenderData().getColorData().getJarColor().toString();
        }

        nbt.putString(NBTConstants.NBT_COLOR, beeColor);

        return nbt;
    }

    public static boolean isBeeInJarOurs(@NotNull ItemStack stack) {
        return stack.getItem() instanceof BeeJar && BeeJar.isFilled(stack) && stack.getTag().getString(NBTConstants.NBT_ENTITY).startsWith(ResourcefulBees.MOD_ID);
    }

    public static Fluid getFluidFromBottle(ItemStack bottleOutput) {
        Item item = bottleOutput.getItem();
        if (item == Items.HONEY_BOTTLE) {
            return ModFluids.HONEY_STILL.get().getSource();
        } else if (item == ModItems.CATNIP_HONEY_BOTTLE.get()) {
            return ModFluids.CATNIP_HONEY_STILL.get().getSource();
        } else if (item instanceof CustomHoneyBottleItem) {
            CustomHoneyBottleItem honey = (CustomHoneyBottleItem) item;
            return honey.getHoneyData().getHoneyStillFluidRegistryObject().get().getSource();
        }
        return Fluids.EMPTY;
    }

    public static Item getHoneyBottle(Fluid fluid) {
        if (fluid == ModFluids.CATNIP_HONEY_STILL.get()) {
            return ModItems.CATNIP_HONEY_BOTTLE.get();
        } else if (fluid instanceof HoneyFlowingFluid) {
            HoneyFlowingFluid customfluid = (HoneyFlowingFluid) fluid;
            return customfluid.getHoneyData().getHoneyBottleRegistryObject().get();
        } else {
            return Items.HONEY_BOTTLE;
        }
    }

    public static Item getHoneyBucket(Fluid fluid) {
        if (fluid == ModFluids.CATNIP_HONEY_STILL.get()) {
            return ModItems.CATNIP_HONEY_FLUID_BUCKET.get();
        } else if (fluid instanceof HoneyFlowingFluid) {
            HoneyFlowingFluid customfluid = (HoneyFlowingFluid) fluid;
            return customfluid.getHoneyData().getHoneyBucketItemRegistryObject().get();
        } else {
            return ModItems.HONEY_FLUID_BUCKET.get();
        }
    }

    public static Item getHoneyBlock(Fluid fluid) {
        if (fluid == ModFluids.CATNIP_HONEY_STILL.get()) {
            return ModItems.CATNIP_HONEY_BLOCK_ITEM.get();
        } else if (fluid instanceof HoneyFlowingFluid) {
            HoneyFlowingFluid customfluid = (HoneyFlowingFluid) fluid;
            if (!customfluid.getHoneyData().doGenerateHoneyBlock()) return Items.AIR;
            return customfluid.getHoneyData().getHoneyBlockItemRegistryObject().get();
        } else {
            return Items.HONEY_BLOCK;
        }
    }

    public static Predicate<FluidStack> getHoneyPredicate() {
        return fluidStack -> fluidStack.getFluid().is(BeeInfoUtils.getFluidTag("forge:honey"));
    }
}
