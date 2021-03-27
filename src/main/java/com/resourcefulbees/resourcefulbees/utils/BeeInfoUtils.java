package com.resourcefulbees.resourcefulbees.utils;

import com.google.common.base.Splitter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.resourcefulbees.resourcefulbees.api.ICustomBee;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import com.resourcefulbees.resourcefulbees.fluids.HoneyFlowingFluid;
import com.resourcefulbees.resourcefulbees.item.CustomHoneyBottleItem;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.lib.LightLevels;
import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.registry.ModFluids;
import com.resourcefulbees.resourcefulbees.registry.ModItems;
import com.resourcefulbees.resourcefulbees.utils.validation.ValidatorUtils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.*;
import java.util.stream.Collectors;

import static com.resourcefulbees.resourcefulbees.lib.BeeConstants.*;

public class BeeInfoUtils {

    private BeeInfoUtils() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static void buildFamilyTree(CustomBeeData bee) {
        if (bee.getBreedData().hasParents()) {
            Iterator<String> parent1 = Splitter.on(",").trimResults().split(bee.getBreedData().getParent1()).iterator();
            Iterator<String> parent2 = Splitter.on(",").trimResults().split(bee.getBreedData().getParent2()).iterator();

            while (parent1.hasNext() && parent2.hasNext()) {
                String p1 = parent1.next();
                String p2 = parent2.next();
                BeeRegistry.getRegistry().familyTree.computeIfAbsent(sortParents(p1, p2), k -> new RandomCollection<>()).add(bee.getBreedData().getBreedWeight(), bee);
            }
        }

        BeeRegistry.getRegistry().familyTree.computeIfAbsent(Pair.of(bee.getName(), bee.getName()), k -> new RandomCollection<>()).add(bee.getBreedData().getBreedWeight(), bee);
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
                if (com.resourcefulbees.resourcefulbees.registry.BiomeDictionary.getTypes().containsKey(s)) {
                    biomeSet.addAll(com.resourcefulbees.resourcefulbees.registry.BiomeDictionary.getTypes().get(s));
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
    public static ResourceLocation getResource(String resource) {
        return new ResourceLocation(resource);
    }

    public static boolean isValidBlock(Block block) {
        return block != null && block != Blocks.AIR;
    }

    public static boolean isValidFluid(Fluid fluid) {
        return fluid != null && fluid != Fluids.EMPTY;
    }

    public static boolean isValidItem(Item item) {
        return item != null && item != Items.AIR;
    }

    public static boolean isValidEntityType(EntityType<?> entityType) {
        return entityType != null;
    }

    public static Item getItem(String itemName) {
        return ForgeRegistries.ITEMS.getValue(getResource(itemName));
    }

    public static Block getBlock(String blockName) {
        return ForgeRegistries.BLOCKS.getValue(getResource(blockName));
    }

    public static Fluid getFluid(String fluidName) {
        return ForgeRegistries.FLUIDS.getValue(getResource(fluidName));
    }

    public static Biome getBiome(String biomeName) {
        return ForgeRegistries.BIOMES.getValue(getResource(biomeName));
    }

    @Nullable
    public static EntityType<?> getEntityType(String entityName) {
        return ForgeRegistries.ENTITIES.getValue(getResource(entityName));
    }

    @Nullable
    public static EntityType<?> getEntityType(ResourceLocation entityId) {
        return ForgeRegistries.ENTITIES.getValue(entityId);
    }

    public static ITag<Item> getItemTag(String itemTag) {
        return ItemTags.getAllTags().getTag(getResource(itemTag));
    }

    public static ITag<Fluid> getFluidTag(String fluidTag) {
        return FluidTags.getAllTags().getTag(getResource(fluidTag));
    }

    public static ITag<Block> getBlockTag(String blockTag) {
        return BlockTags.getAllTags().getTag(getResource(blockTag));
    }

    public static ITag<Block> getValidApiaryTag() {
        return BlockTags.getAllTags().getTag(VALID_APIARY);
    }

    public static void makeValidApiaryTag() {
        BlockTags.bind("resourcefulbees:valid_apiary");
    }

    private static final ResourceLocation VALID_APIARY = new ResourceLocation("resourcefulbees:valid_apiary");

    public static boolean isTag(String input) {
        if (ValidatorUtils.TAG_RESOURCE_PATTERN.matcher(input).matches()) {
            return true;
        } else if (input.equals(FLOWER_TAG_TALL)) {
            return true;
        } else if (input.equals(FLOWER_TAG_SMALL)) {
            return true;
        } else return input.equals(FLOWER_TAG_ALL);
    }

    public static boolean isValidBreedItem(@NotNull ItemStack stack, String validBreedItem) {
        if (ValidatorUtils.TAG_RESOURCE_PATTERN.matcher(validBreedItem).matches()) {
            ITag<Item> itemTag = getItemTag(validBreedItem.replace(TAG_PREFIX, ""));
            return itemTag != null && stack.getItem().is(itemTag);
        } else {
            switch (validBreedItem) {
                case FLOWER_TAG_ALL:
                    return stack.getItem().is(ItemTags.FLOWERS);
                case FLOWER_TAG_SMALL:
                    return stack.getItem().is(ItemTags.SMALL_FLOWERS);
                case FLOWER_TAG_TALL:
                    return stack.getItem().is(ItemTags.TALL_FLOWERS);
                default:
                    return stack.getItem().equals(getItem(validBreedItem));
            }
        }
    }


    public static void flagBeesInRange(BlockPos pos, World world) {
        MutableBoundingBox box = MutableBoundingBox.createProper(pos.getX() + 10, pos.getY() + 10, pos.getZ() + 10, pos.getX() - 10, pos.getY() - 10, pos.getZ() - 10);
        AxisAlignedBB aabb = AxisAlignedBB.of(box);
        if (world != null) {
            List<CustomBeeEntity> list = world.getEntitiesOfClass(CustomBeeEntity.class, aabb);
            list.forEach(customBeeEntity -> customBeeEntity.setHasHiveInRange(true));
        }
    }

    public static List<String> getLoreLines(CompoundNBT outputNBT) {
        if (outputNBT.isEmpty()) return new LinkedList<>();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(outputNBT.toString());
        String nbtString = "NBT: " + gson.toJson(je);
        return Arrays.asList(nbtString.split("\n"));
    }

    public static boolean isShiftPressed() {
        long windowID = Minecraft.getInstance().getWindow().getWindow();
        return InputMappings.isKeyDown(windowID, GLFW.GLFW_KEY_LEFT_SHIFT) || InputMappings.isKeyDown(windowID, GLFW.GLFW_KEY_RIGHT_SHIFT);
    }

    public static TranslationTextComponent getSizeName(float sizeModifier) {
        if (sizeModifier < 0.75) {
            return new TranslationTextComponent("bees.resourcefulbees.size.tiny");
        } else if (sizeModifier < 1) {
            return new TranslationTextComponent("bees.resourcefulbees.size.small");
        } else if (sizeModifier == 1) {
            return new TranslationTextComponent("bees.resourcefulbees.size.regular");
        } else if (sizeModifier <= 1.5) {
            return new TranslationTextComponent("bees.resourcefulbees.size.large");
        } else {
            return new TranslationTextComponent("bees.resourcefulbees.size.giant");
        }
    }

    public static List<Block> getFlowers(String flower) {
        List<Block> flowers = new LinkedList<>();
        if (flower.equals(FLOWER_TAG_ALL)) {
            ITag<Block> itemTag = BlockTags.FLOWERS;
            if (itemTag != null) flowers.addAll(itemTag.getValues());
        } else if (flower.equals(FLOWER_TAG_SMALL)) {
            ITag<Block> itemTag = BlockTags.SMALL_FLOWERS;
            if (itemTag != null) flowers.addAll(itemTag.getValues());
        } else if (flower.equals(FLOWER_TAG_TALL)) {
            ITag<Block> itemTag = BlockTags.TALL_FLOWERS;
            if (itemTag != null) flowers.addAll(itemTag.getValues());
        } else if (flower.startsWith(TAG_PREFIX)) {
            ITag<Block> itemTag = BeeInfoUtils.getBlockTag(flower.replace(TAG_PREFIX, ""));
            if (itemTag != null) flowers.addAll(itemTag.getValues());
        } else {
            flowers.add(getBlock(flower));
        }
        return flowers;
    }

    public static ITextComponent getYesNo(boolean bool) {
        if (bool) {
            return new TranslationTextComponent("gui.resourcefulbees.yes");
        } else {
            return new TranslationTextComponent("gui.resourcefulbees.no");
        }
    }

    public static TranslationTextComponent getLightName(LightLevels light) {
        switch (light) {
            case DAY:
                return new TranslationTextComponent("gui.resourcefulbees.light.day");
            case NIGHT:
                return new TranslationTextComponent("gui.resourcefulbees.light.night");
            default:
                return new TranslationTextComponent("gui.resourcefulbees.light.any");
        }
    }

    public static List<ItemStack> getBreedItems(CustomBeeData parent1Data) {
        String flower = parent1Data.getBreedData().getFeedItem();
        List<Item> flowers = new LinkedList<>();
        if (flower.equals(FLOWER_TAG_ALL)) {
            ITag<Item> itemTag = ItemTags.FLOWERS;
            if (itemTag != null) flowers.addAll(itemTag.getValues());
        } else if (flower.equals(FLOWER_TAG_SMALL)) {
            ITag<Item> itemTag = ItemTags.SMALL_FLOWERS;
            if (itemTag != null) flowers.addAll(itemTag.getValues());
        } else if (flower.equals(FLOWER_TAG_TALL)) {
            ITag<Item> itemTag = ItemTags.TALL_FLOWERS;
            if (itemTag != null) flowers.addAll(itemTag.getValues());
        } else if (flower.startsWith(TAG_PREFIX)) {
            ITag<Item> itemTag = BeeInfoUtils.getItemTag(flower.replace(TAG_PREFIX, ""));
            if (itemTag != null) flowers.addAll(itemTag.getValues());
        } else {
            flowers.add(getItem(flower));
        }
        return flowers.stream().map(f -> new ItemStack(f, parent1Data.getBreedData().getFeedAmount())).collect(Collectors.toList());
    }

    public static void ageBee(int ticksInHive, BeeEntity beeEntity) {
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
        EntitySize size = entity.getDimensions(Pose.STANDING);
        double d0 = 0.65D + size.width / 2.0F;
        double d1 = blockpos.getX() + 0.5D + d0 * direction.getStepX();
        double d2 = blockpos.getY() + Math.max(0.5D - (size.height / 2.0F), 0);
        double d3 = blockpos.getZ() + 0.5D + d0 * direction.getStepZ();
        entity.moveTo(d1, d2, d3, entity.yRot, entity.xRot);
    }

    public static @NotNull CompoundNBT createJarBeeTag(BeeEntity beeEntity, String nbtTagID) {
        String type = EntityType.getKey(beeEntity.getType()).toString();
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString(nbtTagID, type);

        beeEntity.saveWithoutId(nbt);

        String beeColor = VANILLA_BEE_COLOR;

        if (beeEntity instanceof ICustomBee) {
            ICustomBee iCustomBee = (ICustomBee) beeEntity;
            nbt.putString(NBTConstants.NBT_BEE_TYPE, iCustomBee.getBeeType());
            if (iCustomBee.getBeeData().getColorData().hasPrimaryColor()) {
                beeColor = iCustomBee.getBeeData().getColorData().getPrimaryColor();
            } else if (iCustomBee.getBeeData().getColorData().isRainbowBee()) {
                beeColor = RAINBOW_COLOR;
            } else if (iCustomBee.getBeeData().getColorData().hasHoneycombColor()) {
                beeColor = iCustomBee.getBeeData().getColorData().getHoneycombColor();
            }
        }

        nbt.putString(NBTConstants.NBT_COLOR, beeColor);

        return nbt;
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
}
