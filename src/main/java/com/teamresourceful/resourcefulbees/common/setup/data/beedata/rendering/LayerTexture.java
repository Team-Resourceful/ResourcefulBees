package com.teamresourceful.resourcefulbees.common.setup.data.beedata.rendering;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.teamresourceful.resourcefulbees.api.data.bee.render.BeeLayerTexture;
import com.teamresourceful.resourcefulbees.client.util.ClientRenderUtils;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.NeutralMob;
import org.jspecify.annotations.NonNull;

public record LayerTexture(Identifier texture, Identifier angryTexture) implements BeeLayerTexture {

    private static final String PNG_SUFFIX = ".png";
    private static final String ANGRY_PNG_SUFFIX = "_angry.png";
    private static final Identifier MISSING_LOCATION = ModIdentifier.of("textures/entity/missing_texture.png");
    public static final BeeLayerTexture MISSING_TEXTURE = new LayerTexture(MISSING_LOCATION, MISSING_LOCATION);

    public static final Codec<BeeLayerTexture> CODEC = Codec.STRING.comapFlatMap(LayerTexture::readTextureLocation, BeeLayerTexture::id);

    private static DataResult<BeeLayerTexture> readTextureLocation(String s) {
        Identifier normalTexture = Identifier.tryParse(ModConstants.MOD_ID + ":" + BeeConstants.ENTITY_TEXTURES_DIR + s + PNG_SUFFIX);
        Identifier angryTexture = Identifier.tryParse(ModConstants.MOD_ID + ":" + BeeConstants.ENTITY_TEXTURES_DIR + s + ANGRY_PNG_SUFFIX);
        return normalTexture != null && angryTexture != null
            ? DataResult.success(new LayerTexture(normalTexture, angryTexture))
            : DataResult.error(() -> "Not a valid resource location: " + s);
    }


    public Identifier getTexture(NeutralMob neutralMob) {
        return ClientRenderUtils.DEFAULT_TEXTURER.apply(neutralMob.isAngry() ? angryTexture() : texture(), texture());
    }

    @Override
    public @NonNull String toString() {
        return texture().getPath().substring("textures/entity/".length(), texture().getPath().length() - 4);
    }
}
