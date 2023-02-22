package com.teamresourceful.resourcefulbees.common.setup.data.beedata.rendering;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.teamresourceful.resourcefulbees.api.data.bee.render.BeeLayerTexture;
import com.teamresourceful.resourcefulbees.client.util.ClientRenderUtils;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.NeutralMob;

public record LayerTexture(ResourceLocation texture, ResourceLocation angryTexture) implements BeeLayerTexture {

    private static final String PNG_SUFFIX = ".png";
    private static final String ANGRY_PNG_SUFFIX = "_angry.png";
    private static final ResourceLocation MISSING_LOCATION = new ResourceLocation(ModConstants.MOD_ID, "textures/entity/missing_texture.png");
    public static final BeeLayerTexture MISSING_TEXTURE = new LayerTexture(MISSING_LOCATION, MISSING_LOCATION);

    public static final Codec<BeeLayerTexture> CODEC = Codec.STRING.comapFlatMap(LayerTexture::readTextureLocation, BeeLayerTexture::id);

    private static DataResult<BeeLayerTexture> readTextureLocation(String s) {
        ResourceLocation normalTexture = ResourceLocation.tryParse(ModConstants.MOD_ID + ":" + BeeConstants.ENTITY_TEXTURES_DIR + s + PNG_SUFFIX);
        ResourceLocation angryTexture = ResourceLocation.tryParse(ModConstants.MOD_ID + ":" + BeeConstants.ENTITY_TEXTURES_DIR + s + ANGRY_PNG_SUFFIX);
        return normalTexture != null && angryTexture != null
                ? DataResult.success(new LayerTexture(normalTexture, angryTexture))
                : DataResult.error("Not a valid resource location: " + s);
    }

    @Environment(EnvType.CLIENT)
    public ResourceLocation getTexture(NeutralMob neutralMob) {
        return ClientRenderUtils.DEFAULT_TEXTURER.apply(neutralMob.isAngry() ? angryTexture() : texture(), texture());
    }

    @Override
    public String toString() {
        return texture().getPath().substring("textures/entity/".length(), texture().getPath().length() - 4);
    }
}
