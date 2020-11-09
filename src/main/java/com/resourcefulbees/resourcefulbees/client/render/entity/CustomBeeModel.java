package com.resourcefulbees.resourcefulbees.client.render.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.resourcefulbees.resourcefulbees.api.beedata.ColorData;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import com.resourcefulbees.resourcefulbees.lib.ModelTypes;
import net.minecraft.client.renderer.entity.model.AgeableModel;
import net.minecraft.client.renderer.entity.model.ModelUtils;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class CustomBeeModel<T extends CustomBeeEntity> extends AgeableModel<T> {
    private final ModelRenderer body;
    private final ModelRenderer torso;
    private final ModelRenderer rightWing;
    private final ModelRenderer leftWing;
    private final ModelRenderer frontLegs;
    private final ModelRenderer middleLegs;
    private final ModelRenderer backLegs;
    private final ModelRenderer stinger;
    private final ModelRenderer leftAntenna;
    private final ModelRenderer rightAntenna;
    private float bodyPitch;

    private float beeSize = 0;

    public CustomBeeModel(CustomBeeData beeData) {
        super(false, 24.0F, 0.0F);
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.body = new ModelRenderer(this);
        this.body.setRotationPoint(0.0F, 19.0F, 0.0F);
        this.torso = new ModelRenderer(this);
        this.torso.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.body.addChild(this.torso);
        this.torso.setTextureOffset(0,0).addCuboid(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 10.0F, 0.0F);
        this.stinger = new ModelRenderer(this, 26, 7);
        this.stinger.addCuboid(0.0F, -1.0F, 5.0F, 0.0F, 1.0F, 2.0F, 0.0F);
        this.torso.addChild(this.stinger);
        this.leftAntenna = new ModelRenderer(this, 2, 0);
        this.leftAntenna.setRotationPoint(0.0F, -2.0F, -5.0F);
        this.leftAntenna.addCuboid(1.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F);
        this.rightAntenna = new ModelRenderer(this, 2, 3);
        this.rightAntenna.setRotationPoint(0.0F, -2.0F, -5.0F);
        this.rightAntenna.addCuboid(-2.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F);
        this.torso.addChild(this.leftAntenna);
        this.torso.addChild(this.rightAntenna);
        this.rightWing = new ModelRenderer(this, 0, 18);
        this.rightWing.setRotationPoint(-1.5F, -4.0F, -3.0F);
        this.rightWing.rotateAngleX = 0.0F;
        this.rightWing.rotateAngleY = -0.2618F;
        this.rightWing.rotateAngleZ = 0.0F;
        this.body.addChild(this.rightWing);
        this.rightWing.addCuboid(-9.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, 0.001F);
        this.leftWing = new ModelRenderer(this, 0, 18);
        this.leftWing.setRotationPoint(1.5F, -4.0F, -3.0F);
        this.leftWing.rotateAngleX = 0.0F;
        this.leftWing.rotateAngleY = 0.2618F;
        this.leftWing.rotateAngleZ = 0.0F;
        this.leftWing.mirror = true;
        this.body.addChild(this.leftWing);
        this.leftWing.addCuboid(0.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, 0.001F);
        this.frontLegs = new ModelRenderer(this);
        this.frontLegs.setRotationPoint(1.5F, 3.0F, -2.0F);
        this.body.addChild(this.frontLegs);
        this.frontLegs.func_217178_a("frontLegBox", -5.0F, 0.0F, 0.0F, 7, 2, 0, 0.0F, 26, 1);
        this.middleLegs = new ModelRenderer(this);
        this.middleLegs.setRotationPoint(1.5F, 3.0F, 0.0F);
        this.body.addChild(this.middleLegs);
        this.middleLegs.func_217178_a("midLegBox", -5.0F, 0.0F, 0.0F, 7, 2, 0, 0.0F, 26, 3);
        this.backLegs = new ModelRenderer(this);
        this.backLegs.setRotationPoint(1.5F, 3.0F, 2.0F);
        this.body.addChild(this.backLegs);
        this.backLegs.func_217178_a("backLegBox", -5.0F, 0.0F, 0.0F, 7, 2, 0, 0.0F, 26, 5);

        switch (beeData.getColorData().getModelType()) {
            case GELATINOUS:
                addGelatinousLayer();
                break;
            case ORE:
                addOreCrystals();
                break;
            case DEFAULT:
        }
    }


    public void setLivingAnimations(@Nonnull T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
        super.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTick);
        this.bodyPitch = entityIn.getBodyPitch(partialTick);
        this.stinger.showModel = !entityIn.hasStung();
    }

    /**
     * Sets this entity's model rotation angles
     */
    public void setAngles(CustomBeeEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.rightWing.rotateAngleX = 0.0F;
        this.leftAntenna.rotateAngleX = 0.0F;
        this.rightAntenna.rotateAngleX = 0.0F;
        this.body.rotateAngleX = 0.0F;
        this.body.rotationPointY = 19.0F;
        this.rightWing.rotateAngleY = 0.0F;
        this.rightWing.rotateAngleZ = MathHelper.cos((ageInTicks % 98000 * 2.1F)) * (float)Math.PI * 0.15F;
        this.leftWing.rotateAngleX = this.rightWing.rotateAngleX;
        this.leftWing.rotateAngleY = this.rightWing.rotateAngleY;
        this.leftWing.rotateAngleZ = -this.rightWing.rotateAngleZ;
        this.frontLegs.rotateAngleX = ((float)Math.PI / 4F);
        this.middleLegs.rotateAngleX = ((float)Math.PI / 4F);
        this.backLegs.rotateAngleX = ((float)Math.PI / 4F);
        this.body.rotateAngleX = 0.0F;
        this.body.rotateAngleY = 0.0F;
        this.body.rotateAngleZ = 0.0F;

        if (!entityIn.hasAngerTime()) {
            float f1 = MathHelper.cos(ageInTicks * 0.18F);
            this.body.rotateAngleX = 0.1F + f1 * (float)Math.PI * 0.025F;
            this.leftAntenna.rotateAngleX = f1 * (float)Math.PI * 0.03F;
            this.rightAntenna.rotateAngleX = f1 * (float)Math.PI * 0.03F;
            this.frontLegs.rotateAngleX = -f1 * (float)Math.PI * 0.1F + ((float)Math.PI / 8F);
            this.backLegs.rotateAngleX = -f1 * (float)Math.PI * 0.05F + ((float)Math.PI / 4F);
            this.body.rotationPointY = 19.0F - MathHelper.cos(ageInTicks * 0.18F) * 0.9F;
        }

        if (this.bodyPitch > 0.0F) {
            this.body.rotateAngleX = ModelUtils.interpolateAngle(this.body.rotateAngleX, 3.0915928F, this.bodyPitch);
        }

        beeSize = entityIn.getBeeData().getSizeModifier();
        if (isChild) beeSize *= Config.CHILD_SIZE_MODIFIER.get();
    }

    @Nonnull
    protected Iterable<ModelRenderer> getHeadParts() {
        return ImmutableList.of();
    }

    @Nonnull
    protected Iterable<ModelRenderer> getBodyParts() {
        return ImmutableList.of(this.body);
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStackIn, @Nonnull IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        matrixStackIn.push();
        matrixStackIn.translate(0, 1.5 - beeSize * 1.5, 0);
        matrixStackIn.scale(beeSize, beeSize, beeSize);
        super.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        matrixStackIn.pop();
    }

    public void addGelatinousLayer() {
        this.torso.setTextureOffset(0, 25).addCuboid(-3.5F, 4.0F, -5.0F, 7.0F, 7.0F, 10.0F, 0.7F);
    }

    private void addOreCrystals() {
        ModelRenderer crystals = new ModelRenderer(this);
        crystals.setRotationPoint(-1.0F, 3.0F, -9.0F);
        body.addChild(crystals);
        setRotationAngle(crystals, 0.3927F, 0.0F, 0.0F);
        crystals.setTextureOffset(48, 48).addCuboid(1.0F, -3.8582F, 5.7674F, 2.0F, 2.0F, 2.0F, 0.0F, false);
        crystals.setTextureOffset(48, 52).addCuboid(-1.0F, -6.0F, 4.0F, 2.0F, 3.0F, 2.0F, 0.0F, false);

        ModelRenderer bone = new ModelRenderer(this);
        bone.setRotationPoint(0.0F, 0.2242F, 3.1543F);
        crystals.addChild(bone);
        setRotationAngle(bone, -0.3927F, 0.0F, 0.0F);
        bone.setTextureOffset(48, 57).addCuboid(-2.0F, -7.7242F, 1.8457F, 3.0F, 4.0F, 3.0F, 0.0F, true);

        ModelRenderer bone2 = new ModelRenderer(this);
        bone2.setRotationPoint(-1.0F, -0.5412F, 1.3066F);
        crystals.addChild(bone2);
        setRotationAngle(bone2, -0.3927F, 0.0F, 0.0F);
        bone2.setTextureOffset(48, 48).addCuboid(-1.99F, -5.8588F, 2.6934F, 2.0F, 2.0F, 2.0F, 0.0F, false);
        bone2.setTextureOffset(48, 52).addCuboid(1.0F, -6.8588F, 5.6934F, 2.0F, 3.0F, 2.0F, 0.0F, true);

        ModelRenderer bone5 = new ModelRenderer(this);
        bone5.setRotationPoint(2.0F, 0.0F, 0.0F);
        bone2.addChild(bone5);
        setRotationAngle(bone5, 0.0F, 0.0F, -0.3927F);
        bone5.setTextureOffset(48, 52).addCuboid(-1.5F, -6.6588F, 5.6934F, 2.0F, 3.0F, 2.0F, 0.0F, true);

        ModelRenderer bone3 = new ModelRenderer(this);
        bone3.setRotationPoint(3.0F, -4.4588F, -3.3066F);
        bone2.addChild(bone3);
        setRotationAngle(bone3, 0.0F, 0.0F, 0.5236F);
        bone3.setTextureOffset(56, 51).addCuboid(-0.7321F, -2.0F, 10.0F, 2.0F, 4.0F, 2.0F, 0.0F, false);

        ModelRenderer bone4 = new ModelRenderer(this);
        bone4.setRotationPoint(-5.0981F, 0.634F, 0.0F);
        bone3.addChild(bone4);
        setRotationAngle(bone4, 0.0F, 0.0F, -1.3963F);
        bone4.setTextureOffset(56, 51).addCuboid(-1.1252F, 1.9F, 11.0F, 2.0F, 4.0F, 2.0F, 0.0F, true);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
