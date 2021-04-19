package com.resourcefulbees.resourcefulbees.init;

import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.Block;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.resourcefulbees.resourcefulbees.utils.validation.ValidatorUtils.ENTITY_RESOURCE_PATTERN;
import static com.resourcefulbees.resourcefulbees.utils.validation.ValidatorUtils.TAG_RESOURCE_PATTERN;

public class FlowerSetup {

    private FlowerSetup() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static final Logger LOGGER = LogManager.getLogger();

    public static void setupFlowers() {
        BeeRegistry.getRegistry().getBees().values().forEach(FlowerSetup::setupFlowers);
    }

    private static void setupFlowers(CustomBeeData customBeeData) {
        String flower = customBeeData.getFlower();
        if (ENTITY_RESOURCE_PATTERN.matcher(flower).matches()) {
            customBeeData.setEntityFlower(new ResourceLocation(flower.replace(BeeConstants.ENTITY_PREFIX, "")));
        } else if (TAG_RESOURCE_PATTERN.matcher(flower).matches()) {
            Tag<Block> blockTag = BeeInfoUtils.getBlockTag(flower.replace(BeeConstants.TAG_PREFIX, ""));
            if (blockTag != null) {
                blockTag.getValues().forEach(customBeeData::addBlockFlower);
            } else {
                LOGGER.warn("Could not find Tag: {}, for bee: {}", flower, customBeeData.getName());
            }
        } else {
            switch (flower) {
                case BeeConstants.FLOWER_TAG_ALL:
                    BlockTags.FLOWERS.getValues().forEach(customBeeData::addBlockFlower);
                    break;
                case BeeConstants.FLOWER_TAG_SMALL:
                    BlockTags.SMALL_FLOWERS.getValues().forEach(customBeeData::addBlockFlower);
                    break;
                case BeeConstants.FLOWER_TAG_TALL:
                    BlockTags.TALL_FLOWERS.getValues().forEach(customBeeData::addBlockFlower);
                    break;
                default:
                    Block block = BeeInfoUtils.getBlock(flower);
                    if (BeeInfoUtils.isValidBlock(block)) {
                        customBeeData.addBlockFlower(block);
                    }
            }
        }
    }
}
