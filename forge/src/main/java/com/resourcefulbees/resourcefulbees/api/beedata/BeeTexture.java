package com.resourcefulbees.resourcefulbees.api.beedata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.concurrent.Immutable;

@Immutable
public class BeeTexture {

    private static final String PNG_SUFFIX = ".png";
    private static final String ANGRY_PNG_SUFFIX = "_angry.png";
    private static final ResourceLocation MISSING_LOCATION = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/entity/missing_texture.png");
    public static final Codec<BeeTexture> CODEC = Codec.STRING.comapFlatMap(BeeTexture::readTextureLocation, BeeTexture::toString).stable();
    private final ResourceLocation normalTexture;
    private final ResourceLocation angryTexture;

    private BeeTexture(ResourceLocation normalTexture, ResourceLocation angryTexture) {
        this.normalTexture = normalTexture;
        this.angryTexture = angryTexture;
    }

    public ResourceLocation getNormalTexture() {
        return normalTexture;
    }

    public ResourceLocation getAngryTexture() {
        return angryTexture;
    }

    private static DataResult<BeeTexture> readTextureLocation(String s) {
        ResourceLocation normalTexture = ResourceLocation.tryParse(ResourcefulBees.MOD_ID + ":" + BeeConstants.ENTITY_TEXTURES_DIR + s + PNG_SUFFIX);
        ResourceLocation angryTexture = ResourceLocation.tryParse(ResourcefulBees.MOD_ID + ":" + BeeConstants.ENTITY_TEXTURES_DIR + s + ANGRY_PNG_SUFFIX);
        return normalTexture != null && angryTexture != null
                ? DataResult.success(new BeeTexture(normalTexture, angryTexture))
                : DataResult.error("Not a valid resource location: " + s);
    }

    @Override
    public String toString() {
        return normalTexture.getPath().substring("textures/entity/".length(), normalTexture.getPath().length() - 4);
    }

    public static final BeeTexture MISSING_TEXTURE = new BeeTexture(MISSING_LOCATION, MISSING_LOCATION);
}
