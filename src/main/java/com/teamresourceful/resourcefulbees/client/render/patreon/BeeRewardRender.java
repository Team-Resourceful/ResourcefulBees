package com.teamresourceful.resourcefulbees.client.render.patreon;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.teamresourceful.resourcefulbees.common.patreon.BeeRewardData;
import com.teamresourceful.resourcefulbees.common.patreon.PatreonInfo;
import com.teamresourceful.resourcefulbees.common.utils.color.Color;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import org.jetbrains.annotations.NotNull;


public class BeeRewardRender extends LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {

    private final ModelRenderer body;
    private final ModelRenderer leftAntenna;
    private final ModelRenderer rightAntenna;
    private final ModelRenderer rightWing;
    private final ModelRenderer leftWing;
    private final ModelRenderer frontLegs;
    private final ModelRenderer middleLegs;
    private final ModelRenderer backLegs;


    public BeeRewardRender(IEntityRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> entityRenderer) {
        super(entityRenderer);
        this.body = new ModelRenderer(64,64,0,0);
        ModelRenderer torso = new ModelRenderer(64, 64, 0, 0);
        ModelRenderer stinger = new ModelRenderer(64, 64, 26, 7);
        this.leftAntenna = new ModelRenderer(64,64, 2, 0);
        this.rightAntenna = new ModelRenderer(64,64, 2, 3);
        this.rightWing = new ModelRenderer(64,64, 0, 18);
        this.leftWing = new ModelRenderer(64,64, 0, 18);
        this.frontLegs = new ModelRenderer(64,64,0,0);
        this.middleLegs = new ModelRenderer(64,64,0,0);
        this.backLegs = new ModelRenderer(64,64,0,0);



        this.body.setPos(0.0F, 19.0F, 0.0F);
        torso.setPos(0.0F, 0.0F, 0.0F);
        this.body.addChild(torso);
        torso.texOffs(0, 0).addBox(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 10.0F, 0.0F);

        stinger.addBox(0.0F, -1.0F, 5.0F, 0.0F, 1.0F, 2.0F, 0.0F);
        torso.addChild(stinger);

        this.leftAntenna.setPos(0.0F, -2.0F, -5.0F);
        this.leftAntenna.addBox(1.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F);

        this.rightAntenna.setPos(0.0F, -2.0F, -5.0F);
        this.rightAntenna.addBox(-2.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F);
        torso.addChild(this.leftAntenna);
        torso.addChild(this.rightAntenna);

        this.rightWing.setPos(-1.5F, -4.0F, -3.0F);
        this.setRotationAngle(rightWing, -0.2618F);
        this.body.addChild(this.rightWing);
        this.rightWing.addBox(-9.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, 0.001F);

        this.leftWing.setPos(1.5F, -4.0F, -3.0F);
        this.setRotationAngle(leftWing, 0.2618F);
        this.leftWing.mirror = true;
        this.body.addChild(this.leftWing);
        this.leftWing.addBox(0.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, 0.001F);

        this.frontLegs.setPos(1.5F, 3.0F, -2.0F);
        this.body.addChild(this.frontLegs);
        this.frontLegs.addBox("frontLegBox", -5.0F, 0.0F, 0.0F, 7, 2, 0, 0.0F, 26, 1);

        this.middleLegs.setPos(1.5F, 3.0F, 0.0F);
        this.body.addChild(this.middleLegs);
        this.middleLegs.addBox("midLegBox", -5.0F, 0.0F, 0.0F, 7, 2, 0, 0.0F, 26, 3);

        this.backLegs.setPos(1.5F, 3.0F, 2.0F);
        this.body.addChild(this.backLegs);
        this.backLegs.addBox("backLegBox", -5.0F, 0.0F, 0.0F, 7, 2, 0, 0.0F, 26, 5);
    }

    private void setRotationAngle(ModelRenderer modelPart, float y) {
        modelPart.xRot = 0f;
        modelPart.yRot = y;
        modelPart.zRot = 0f;
    }

    private void updateAngles(float ticks){
        this.leftAntenna.xRot = 0.0F;
        this.rightAntenna.xRot = 0.0F;
        this.body.xRot = 0.0F;
        this.body.y = 19.0F;
        this.rightWing.yRot = 0.0F;
        this.rightWing.zRot = MathHelper.cos((ticks % 98000 * 2.1F)) * (float) Math.PI * 0.15F;
        this.leftWing.xRot = this.rightWing.xRot;
        this.leftWing.yRot = this.rightWing.yRot;
        this.leftWing.zRot = -this.rightWing.zRot;
        this.frontLegs.xRot = ((float) Math.PI / 4F);
        this.middleLegs.xRot = ((float) Math.PI / 4F);
        this.backLegs.xRot = ((float) Math.PI / 4F);
        setRotationAngle(body, 0);

        float f1 = MathHelper.cos(ticks % 1143333 * 0.18F);
        this.body.xRot = 0.1F + f1 * (float) Math.PI * 0.025F;
        this.leftAntenna.xRot = f1 * (float) Math.PI * 0.03F;
        this.rightAntenna.xRot = f1 * (float) Math.PI * 0.03F;
        this.frontLegs.xRot = -f1 * (float) Math.PI * 0.1F + ((float) Math.PI / 8F);
        this.backLegs.xRot = -f1 * (float) Math.PI * 0.05F + ((float) Math.PI / 4F);
        this.body.y = 19.0F - MathHelper.cos(ticks % 1143333 * 0.18F) * 0.9F;
    }

    @Override
    public void render(@NotNull MatrixStack stack, @NotNull IRenderTypeBuffer buffer, int packedLightIn, @NotNull AbstractClientPlayerEntity playerEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!PatreonInfo.isPatreon(playerEntity.getUUID())) return;
        BeeRewardData data = PatreonInfo.getPatreon(playerEntity.getUUID());
        updateAngles(ageInTicks);
        stack.pushPose();

        stack.scale(0.25f,0.25f,0.25f);
        stack.mulPose(Vector3f.YP.rotationDegrees((ageInTicks * 0.01F /2f)* 360f));
        stack.translate(0f,(1.5 * MathHelper.sin(ageInTicks/10 - 30f)),3f);
        stack.mulPose(Vector3f.YP.rotationDegrees(-90));


        stack.pushPose();
        IVertexBuilder ivertexbuilder = buffer.getBuffer(RenderType.entityCutoutNoCull(data.getTexture().getResourceLocation()));
        body.render(stack, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY);
        stack.popPose();

        if (data.getTexture().hasSecondaryLayer()) {
            stack.pushPose();
            IVertexBuilder ivertexbuilder2 = buffer.getBuffer(RenderType.entityCutoutNoCull(data.getTexture().getSecondaryResourceLocation()));
            Color color = data.getColor();
            body.render(stack, ivertexbuilder2, packedLightIn, LivingRenderer.getOverlayCoords(playerEntity, 0.0F), color.getFloatRed(), color.getFloatGreen(), color.getFloatBlue(), 1.0f);
            stack.popPose();
        }


        stack.popPose();
    }
}
