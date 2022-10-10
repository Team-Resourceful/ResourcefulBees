package com.teamresourceful.resourcefulbees.common.fluids;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Vector3f;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.telepathicgrunt.the_bumblezone.client.rendering.FluidClientOverlay;
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

    public static final ResourceLocation FLUID_STILL = new ResourceLocation(ResourcefulBees.MOD_ID, "block/honey/honey_still");
    public static final ResourceLocation FLUID_FLOWING = new ResourceLocation(ResourcefulBees.MOD_ID, "block/honey/honey_flow");

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

    @Override
    public @Nullable ResourceLocation getOverlayTexture() {
        return FLUID_FLOWING;
    }

    @Override
    public @Nullable ResourceLocation getRenderOverlayTexture(Minecraft mc) {
        return null; //Bc this doesn't do shit
    }

    @Override
    public Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
        BlockPos blockpos = new BlockPos(camera.getEntity().getX(), camera.getEntity().getEyeY(), camera.getEntity().getZ());
        float brightnessAtEyes = LightTexture.getBrightness(camera.getEntity().level.dimensionType(), camera.getEntity().level.getMaxLocalRawBrightness(blockpos));
        float brightness = (float)Math.max(Math.pow(FluidClientOverlay.getDimensionBrightnessAtEyes(camera.getEntity()), 2.0), brightnessAtEyes);
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
