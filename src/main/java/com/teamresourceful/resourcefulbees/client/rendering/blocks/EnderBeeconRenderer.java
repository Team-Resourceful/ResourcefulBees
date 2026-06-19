//package com.teamresourceful.resourcefulbees.client.rendering.blocks;
//
//
//import com.mojang.blaze3d.vertex.PoseStack;
//import com.teamresourceful.resourcefulbees.common.blockentities.EnderBeeconBlockEntity;
//import net.minecraft.client.renderer.SubmitNodeCollector;
//import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
//import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
//import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
//import net.minecraft.client.renderer.state.level.CameraRenderState;
//import net.minecraft.resources.Identifier;
//import org.jspecify.annotations.NonNull;
//
//public class EnderBeeconRenderer implements BlockEntityRenderer<EnderBeeconBlockEntity, BlockEntityRenderState> {
//
//    public static final Identifier TEXTURE_BEACON_BEAM = Identifier.tryParse("textures/entity/beacon_beam.png");
//
//    public EnderBeeconRenderer(BlockEntityRendererProvider.Context renderer) {}
//
//    /*@Override
//    public void render(EnderBeeconBlockEntity tile, float partialTick, @NotNull PoseStack matrix, @NotNull MultiBufferSource renderer, int light, int overlayLight) {
//        if (tile.getLevel() == null) return;
//        *//*FluidHolder holder = tile.getFluid();
//        long gameTime = tile.getLevel().getGameTime();
//
//        if (!holder.isEmpty()) {
//
//            BlockState state = tile.getBlockState();
//            boolean showBeam = state.hasProperty(EnderBeeconBlock.BEAM) && state.getValue(EnderBeeconBlock.BEAM);
//
//            // render tank
//            float percentage = tile.getFluid().getFluidAmount() / (float)tile.getFluidContainer().getTankCapacity(0);
//            int color = ClientFluidHooks.getFluidColor(holder);
//            VertexConsumer builder = renderer.getBuffer(Sheets.translucentCullBlockSheet());
//            AABB box = new AABB(0.26, 0.25, 0.26, 0.74, 0.25 + percentage * 0.375, 0.74);
//            RenderCuboid.renderCube(box, ClientFluidHooks.getFluidSprite(holder), matrix, builder, color, light, overlayLight);
//            // render beam
//            if (!showBeam) return;
//            float red = (color >> 16 & 255) / 255f;
//            float green = (color >> 8 & 255) / 255f;
//            float blue = (color & 255) / 255f;
//            float alpha = (color >> 24 & 255) / 255f;
//            float[] afloats = {red, green, blue, alpha};
//            BeaconRenderer.renderBeaconBeam(matrix, renderer, TEXTURE_BEACON_BEAM, partialTick, 1.0F, gameTime, 0, 1024, afloats, 0.2F, 0.25F);
//        }*//*
//    }
//
//    @Override
//    public boolean shouldRenderOffScreen(@NotNull EnderBeeconBlockEntity tile) {
//        return true;
//    }*/
//
//    @Override
//    public @NonNull BlockEntityRenderState createRenderState() {
//        return null;
//    }
//
//    @Override
//    public void submit(@NonNull BlockEntityRenderState state, @NonNull PoseStack poseStack, @NonNull SubmitNodeCollector submitNodeCollector, @NonNull CameraRenderState camera) {
//
//    }
//}
