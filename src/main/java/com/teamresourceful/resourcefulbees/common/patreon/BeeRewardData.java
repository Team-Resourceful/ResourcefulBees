package com.teamresourceful.resourcefulbees.common.patreon;

import com.teamresourceful.resourcefulbees.common.utils.color.Color;
import net.minecraft.util.ResourceLocation;

import java.util.Locale;

public class BeeRewardData {

    private final Color color;
    private final BeeTextures textures;

    public BeeRewardData(Color colors, BeeTextures textures) {
        this.color = colors;
        this.textures = textures;
    }

    public Color getColor() {
        return color;
    }

    public BeeTextures getTexture() {
        return textures;
    }

    public enum BeeTextures {
        SLIME(new ResourceLocation("resourcefulbees:textures/entity/slime/slime_bee.png")),
        OREO(new ResourceLocation("resourcefulbees:textures/entity/oreo/oreo_bee.png")),
        PIGMAN(new ResourceLocation("resourcefulbees:textures/entity/pigman/pigman_bee.png")),
        SKELETON(new ResourceLocation("resourcefulbees:textures/entity/skeleton/skeleton_bee.png")),
        WITHER(new ResourceLocation("resourcefulbees:textures/entity/wither/wither_bee.png")),
        CREEPER(new ResourceLocation("resourcefulbees:textures/entity/creeper/creeper_bee.png")),
        VANILLA(new ResourceLocation("minecraft:textures/entity/bee/bee.png")),
        BASE(new ResourceLocation("resourcefulbees:textures/entity/custom/bee.png"),
                new ResourceLocation("resourcefulbees:textures/entity/custom/primary_layer.png")),
        KITTEN(new ResourceLocation("resourcefulbees:textures/entity/kitten/kitten_base.png")),
        GILDED(new ResourceLocation("resourcefulbees:textures/entity/gilded/gilded_alchemist_bee.png")),
        GOLDEN(new ResourceLocation("resourcefulbees:textures/entity/gilded/golden_flower_bee.png")),
        NETHER(new ResourceLocation("resourcefulbees:textures/entity/mushroom/nether_wart_bee.png")),
        CRIMSON(new ResourceLocation("resourcefulbees:textures/entity/mushroom/crimson_alchemist_bee.png")),
        WARPED(new ResourceLocation("resourcefulbees:textures/entity/mushroom/warped_alchemist_bee.png")),
        GHAST(new ResourceLocation("resourcefulbees:textures/entity/ghast/ghast_bee.png"));

        final ResourceLocation resourceLocation;
        ResourceLocation secondaryResourceLocation;
        final boolean hasSecondaryLayer;

        BeeTextures(ResourceLocation resourceLocation){
            this.resourceLocation = resourceLocation;
            this.hasSecondaryLayer = false;
        }

        BeeTextures(ResourceLocation resourceLocation, ResourceLocation secondaryResourceLocation){
            this.resourceLocation = resourceLocation;
            this.secondaryResourceLocation = secondaryResourceLocation;
            this.hasSecondaryLayer = true;
        }

        public ResourceLocation getResourceLocation() { return resourceLocation; }

        public ResourceLocation getSecondaryResourceLocation() { return secondaryResourceLocation; }

        public boolean hasSecondaryLayer() { return hasSecondaryLayer; }

        public static BeeTextures getTexture(String input){
            try{
                return BeeTextures.valueOf(input.toUpperCase(Locale.ENGLISH));
            }catch (Exception ignored){
                return BASE;
            }
        }

    }
}
