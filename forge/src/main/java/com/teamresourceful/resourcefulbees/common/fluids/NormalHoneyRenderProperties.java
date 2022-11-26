package com.teamresourceful.resourcefulbees.common.fluids;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Vector3f;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.client.utils.ClientUtils;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NormalHoneyRenderProperties implements IClientFluidTypeExtensions {

    private static final ResourceLocation FLUID_STILL = new ResourceLocation(ResourcefulBees.MOD_ID, "block/honey/honey_still");
    private static final ResourceLocation FLUID_FLOWING = new ResourceLocation(ResourcefulBees.MOD_ID, "block/honey/honey_flow");
    private static final ResourceLocation HONEY_UNDERWATER = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/block/honey/honey_underwater.png");

    public static final NormalHoneyRenderProperties INSTANCE = new NormalHoneyRenderProperties();

    private NormalHoneyRenderProperties() {
    }

    @Override
    public ResourceLocation getStillTexture() {
        return FLUID_STILL;
    }

    @Override
    public ResourceLocation getFlowingTexture() {
        return FLUID_FLOWING;
    }

    //texture used against transparent blocks placed next to the fluid
    @Override
    public @Nullable ResourceLocation getOverlayTexture() {
        return FLUID_FLOWING;
    }

    //screen overlay when underwater... though this texture isn't working for some reason...
    @Override
    public @Nullable ResourceLocation getRenderOverlayTexture(Minecraft mc) {
        return HONEY_UNDERWATER;
    }

    @Override
    public @NotNull Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
        BlockPos blockpos = new BlockPos(camera.getEntity().getX(), camera.getEntity().getEyeY(), camera.getEntity().getZ());
        float brightnessAtEyes = LightTexture.getBrightness(camera.getEntity().level.dimensionType(), camera.getEntity().level.getMaxLocalRawBrightness(blockpos));
        float brightness = (float)Math.max(Math.pow(ClientUtils.getDimensionBrightnessAtEyes(camera.getEntity()), 2.0), brightnessAtEyes);
        float fogRed = 0.945F * brightness;
        float fogGreen = 0.67F * brightness;
        float fogBlue = 0.231F * brightness;
        return new Vector3f(fogRed, fogGreen, fogBlue);
    }

    @Override
    public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape) {
        RenderSystem.setShaderFogStart(3F);
        RenderSystem.setShaderFogEnd(7F);
    }
}
