package com.resourcefulbees.resourcefulbees.client.render.patreon;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.resourcefulbees.resourcefulbees.patreon.BeeRewardData;
import com.resourcefulbees.resourcefulbees.patreon.PatreonInfo;
import com.resourcefulbees.resourcefulbees.utils.color.RainbowColor;
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



        this.body.setRotationPoint(0.0F, 19.0F, 0.0F);
        torso.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.body.addChild(torso);
        torso.setTextureOffset(0, 0).addCuboid(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 10.0F, 0.0F);

        stinger.addCuboid(0.0F, -1.0F, 5.0F, 0.0F, 1.0F, 2.0F, 0.0F);
        torso.addChild(stinger);

        this.leftAntenna.setRotationPoint(0.0F, -2.0F, -5.0F);
        this.leftAntenna.addCuboid(1.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F);

        this.rightAntenna.setRotationPoint(0.0F, -2.0F, -5.0F);
        this.rightAntenna.addCuboid(-2.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F);
        torso.addChild(this.leftAntenna);
        torso.addChild(this.rightAntenna);

        this.rightWing.setRotationPoint(-1.5F, -4.0F, -3.0F);
        this.setRotationAngle(rightWing, -0.2618F);
        this.body.addChild(this.rightWing);
        this.rightWing.addCuboid(-9.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, 0.001F);

        this.leftWing.setRotationPoint(1.5F, -4.0F, -3.0F);
        this.setRotationAngle(leftWing, 0.2618F);
        this.leftWing.mirror = true;
        this.body.addChild(this.leftWing);
        this.leftWing.addCuboid(0.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, 0.001F);

        this.frontLegs.setRotationPoint(1.5F, 3.0F, -2.0F);
        this.body.addChild(this.frontLegs);
        this.frontLegs.func_217178_a("frontLegBox", -5.0F, 0.0F, 0.0F, 7, 2, 0, 0.0F, 26, 1);

        this.middleLegs.setRotationPoint(1.5F, 3.0F, 0.0F);
        this.body.addChild(this.middleLegs);
        this.middleLegs.func_217178_a("midLegBox", -5.0F, 0.0F, 0.0F, 7, 2, 0, 0.0F, 26, 3);

        this.backLegs.setRotationPoint(1.5F, 3.0F, 2.0F);
        this.body.addChild(this.backLegs);
        this.backLegs.func_217178_a("backLegBox", -5.0F, 0.0F, 0.0F, 7, 2, 0, 0.0F, 26, 5);
    }

    private void setRotationAngle(ModelRenderer modelRenderer, float y) {
        modelRenderer.rotateAngleX = 0f;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = 0f;
    }

    private void updateAngles(float ticks){
        this.leftAntenna.rotateAngleX = 0.0F;
        this.rightAntenna.rotateAngleX = 0.0F;
        this.body.rotateAngleX = 0.0F;
        this.body.rotationPointY = 19.0F;
        this.rightWing.rotateAngleY = 0.0F;
        this.rightWing.rotateAngleZ = MathHelper.cos((ticks % 98000 * 2.1F)) * (float) Math.PI * 0.15F;
        this.leftWing.rotateAngleX = this.rightWing.rotateAngleX;
        this.leftWing.rotateAngleY = this.rightWing.rotateAngleY;
        this.leftWing.rotateAngleZ = -this.rightWing.rotateAngleZ;
        this.frontLegs.rotateAngleX = ((float) Math.PI / 4F);
        this.middleLegs.rotateAngleX = ((float) Math.PI / 4F);
        this.backLegs.rotateAngleX = ((float) Math.PI / 4F);
        setRotationAngle(body, 0);

        float f1 = MathHelper.cos(ticks % 1143333 * 0.18F);
        this.body.rotateAngleX = 0.1F + f1 * (float) Math.PI * 0.025F;
        this.leftAntenna.rotateAngleX = f1 * (float) Math.PI * 0.03F;
        this.rightAntenna.rotateAngleX = f1 * (float) Math.PI * 0.03F;
        this.frontLegs.rotateAngleX = -f1 * (float) Math.PI * 0.1F + ((float) Math.PI / 8F);
        this.backLegs.rotateAngleX = -f1 * (float) Math.PI * 0.05F + ((float) Math.PI / 4F);
        this.body.rotationPointY = 19.0F - MathHelper.cos(ticks % 1143333 * 0.18F) * 0.9F;
    }

    @Override
    public void render(@NotNull MatrixStack stack, @NotNull IRenderTypeBuffer buffer, int packedLightIn, @NotNull AbstractClientPlayerEntity playerEntity,  float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!PatreonInfo.isPatreon(playerEntity.getUniqueID())) return;
        BeeRewardData data = PatreonInfo.getPatreon(playerEntity.getUniqueID());
        updateAngles(ageInTicks);
        stack.push();

        stack.scale(0.25f,0.25f,0.25f);
        stack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((ageInTicks * 0.01F /2f)* 360f));
        stack.translate(0f,(1.5 * MathHelper.sin(ageInTicks/10 - 30f)),3f);
        stack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-90));


        stack.push();
        IVertexBuilder ivertexbuilder = buffer.getBuffer(RenderType.getEntityCutoutNoCull(data.getTextures().getResourceLocation()));
        body.render(stack, ivertexbuilder, packedLightIn, OverlayTexture.DEFAULT_UV);
        stack.pop();

        if (data.getTextures().hasSecondaryLayer()) {
            stack.push();
            IVertexBuilder ivertexbuilder2 = buffer.getBuffer(RenderType.getEntityCutoutNoCull(data.getTextures().getSecondaryResourceLocation()));
            float r = data.isRainbow() ? RainbowColor.getColorFloats()[0] : data.getColor().getR();
            float g = data.isRainbow() ? RainbowColor.getColorFloats()[1] : data.getColor().getG();
            float b = data.isRainbow() ? RainbowColor.getColorFloats()[2] : data.getColor().getB();
            body.render(stack, ivertexbuilder2, packedLightIn, LivingRenderer.getOverlay(playerEntity, 0.0F), r, g, b, 1.0f);
            stack.pop();
        }


        stack.pop();
    }
}
