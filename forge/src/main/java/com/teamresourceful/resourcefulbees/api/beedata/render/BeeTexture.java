package com.teamresourceful.resourcefulbees.api.beedata.render;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.lib.constants.BeeConstants;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Unmodifiable;

@Unmodifiable
public class BeeTexture {

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

    private final ResourceLocation normalTexture;
    private final ResourceLocation angryTexture;

    private BeeTexture(ResourceLocation normalTexture, ResourceLocation angryTexture) {
        this.normalTexture = normalTexture;
        this.angryTexture = angryTexture;
    }

    /**
     * Gets the normal texture for the bee. This is the texture that is
     * most often seen by players. This texture can be colored or made emissive.
     *
     * @return Returns a {@link ResourceLocation} for the normal bee texture.
     */
    public ResourceLocation getNormalTexture() {
        return normalTexture;
    }

    /**
     * Gets the angry texture for the bee. This texture can only seen by players
     * when bees are angry. This texture can be colored or made emissive.
     *
     * @return Returns a {@link ResourceLocation} for the angry bee texture.
     */
    public ResourceLocation getAngryTexture() {
        return angryTexture;
    }

    @Override
    public String toString() {
        return normalTexture.getPath().substring("textures/entity/".length(), normalTexture.getPath().length() - 4);
    }
}
