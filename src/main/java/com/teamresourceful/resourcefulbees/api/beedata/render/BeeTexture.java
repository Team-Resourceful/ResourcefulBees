package com.teamresourceful.resourcefulbees.api.beedata.render;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.client.utils.ClientUtils;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.NeutralMob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public record BeeTexture(ResourceLocation normalTexture, ResourceLocation angryTexture) {

    private static final String PNG_SUFFIX = ".png";
    private static final String ANGRY_PNG_SUFFIX = "_angry.png";
    private static final ResourceLocation MISSING_LOCATION = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/entity/missing_texture.png");
    public static final BeeTexture MISSING_TEXTURE = new BeeTexture(MISSING_LOCATION, MISSING_LOCATION);

    /**
     * A {@link Codec<BeeTexture>} that can be parsed to create a {@link BeeTexture} object.
     */
    public static final Codec<BeeTexture> CODEC = Codec.STRING.comapFlatMap(BeeTexture::readTextureLocation, BeeTexture::toString);

    private static DataResult<BeeTexture> readTextureLocation(String s) {
        ResourceLocation normalTexture = ResourceLocation.tryParse(ResourcefulBees.MOD_ID + ":" + BeeConstants.ENTITY_TEXTURES_DIR + s + PNG_SUFFIX);
        ResourceLocation angryTexture = ResourceLocation.tryParse(ResourcefulBees.MOD_ID + ":" + BeeConstants.ENTITY_TEXTURES_DIR + s + ANGRY_PNG_SUFFIX);
        return normalTexture != null && angryTexture != null
                ? DataResult.success(new BeeTexture(normalTexture, angryTexture))
                : DataResult.error("Not a valid resource location: " + s);
    }

    @OnlyIn(Dist.CLIENT)
    public ResourceLocation getTexture(NeutralMob neutralMob) {
        // Note: This optimization does mean that if a texture was missing and the user reloaded textures, and it's now not missing it will still return the default
        //       while this is very annoying this optimization should be done because if not it will cause a lot of unneeded resource lookups.
        return ClientUtils.DEFAULT_TEXTURER.apply(neutralMob.isAngry() ? angryTexture : normalTexture, normalTexture);
    }

    @Override
    public String toString() {
        return normalTexture.getPath().substring("textures/entity/".length(), normalTexture.getPath().length() - 4);
    }
}
