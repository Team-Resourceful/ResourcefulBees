package com.resourcefulbees.resourcefulbees.client.render.entity.models;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import com.resourcefulbees.resourcefulbees.lib.BaseModelTypes;
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

    public CustomBeeModel(ModelTypes modelType) {
        super(false, 24.0F, 0.0F);
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.body = new ModelRenderer(this);
        this.torso = new ModelRenderer(this);
        this.stinger = new ModelRenderer(this, 26, 7);
        this.leftAntenna = new ModelRenderer(this, 2, 0);
        this.rightAntenna = new ModelRenderer(this, 2, 3);
        this.rightWing = new ModelRenderer(this, 0, 18);
        this.leftWing = new ModelRenderer(this, 0, 18);
        this.frontLegs = new ModelRenderer(this);
        this.middleLegs = new ModelRenderer(this);
        this.backLegs = new ModelRenderer(this);

        switch (modelType) {
            case GELATINOUS:
                addGelatinousParts();
                break;
            case ORE:
                addOreCrystals();
                break;
            case DRAGON:
                addDragonParts();
                break;
            case QUEEN:
                addQueenParts();
                break;
            case VILLAGER:
                addVillagerNose();
                break;
            case MUSHROOM:
                addMushrooms();
                break;
            case DEFAULT:
                addDefaultParts();
        }
    }


    public CustomBeeModel(BaseModelTypes modelType) {
        super(false, 24.0F, 0.0F);
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.body = new ModelRenderer(this);
        this.torso = new ModelRenderer(this);
        this.stinger = new ModelRenderer(this, 26, 7);
        this.leftAntenna = new ModelRenderer(this, 2, 0);
        this.rightAntenna = new ModelRenderer(this, 2, 3);
        this.rightWing = new ModelRenderer(this, 0, 18);
        this.leftWing = new ModelRenderer(this, 0, 18);
        this.frontLegs = new ModelRenderer(this);
        this.middleLegs = new ModelRenderer(this);
        this.backLegs = new ModelRenderer(this);


        switch (modelType) {
            case KITTEN:
                addKittenParts();
                break;
            case DEFAULT:
                addDefaultParts();
                break;
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
        this.leftAntenna.rotateAngleX = 0.0F;
        this.rightAntenna.rotateAngleX = 0.0F;
        this.body.rotateAngleX = 0.0F;
        this.body.rotationPointY = 19.0F;
        this.rightWing.rotateAngleY = 0.0F;
        this.rightWing.rotateAngleZ = MathHelper.cos((ageInTicks % 98000 * 2.1F)) * (float) Math.PI * 0.15F;
        this.leftWing.rotateAngleX = this.rightWing.rotateAngleX;
        this.leftWing.rotateAngleY = this.rightWing.rotateAngleY;
        this.leftWing.rotateAngleZ = -this.rightWing.rotateAngleZ;
        this.frontLegs.rotateAngleX = ((float) Math.PI / 4F);
        this.middleLegs.rotateAngleX = ((float) Math.PI / 4F);
        this.backLegs.rotateAngleX = ((float) Math.PI / 4F);
        setRotationAngle(body, 0, 0, 0);

        if (!entityIn.hasAngerTime()) {
            float f1 = MathHelper.cos(ageInTicks * 0.18F);
            this.body.rotateAngleX = 0.1F + f1 * (float) Math.PI * 0.025F;
            this.leftAntenna.rotateAngleX = f1 * (float) Math.PI * 0.03F;
            this.rightAntenna.rotateAngleX = f1 * (float) Math.PI * 0.03F;
            this.frontLegs.rotateAngleX = -f1 * (float) Math.PI * 0.1F + ((float) Math.PI / 8F);
            this.backLegs.rotateAngleX = -f1 * (float) Math.PI * 0.05F + ((float) Math.PI / 4F);
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

    // base bee parts

    private void addDefaultParts() {
        this.body.setRotationPoint(0.0F, 19.0F, 0.0F);
        this.torso.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.body.addChild(this.torso);
        this.torso.setTextureOffset(0, 0).addCuboid(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 10.0F, 0.0F);

        this.stinger.addCuboid(0.0F, -1.0F, 5.0F, 0.0F, 1.0F, 2.0F, 0.0F);
        this.torso.addChild(this.stinger);

        this.leftAntenna.setRotationPoint(0.0F, -2.0F, -5.0F);
        this.leftAntenna.addCuboid(1.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F);

        this.rightAntenna.setRotationPoint(0.0F, -2.0F, -5.0F);
        this.rightAntenna.addCuboid(-2.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F);
        this.torso.addChild(this.leftAntenna);
        this.torso.addChild(this.rightAntenna);

        this.rightWing.setRotationPoint(-1.5F, -4.0F, -3.0F);
        this.setRotationAngle(rightWing, 0, -0.2618F, 0);
        this.body.addChild(this.rightWing);
        this.rightWing.addCuboid(-9.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, 0.001F);

        this.leftWing.setRotationPoint(1.5F, -4.0F, -3.0F);
        this.setRotationAngle(leftWing, 0, 0.2618F, 0);
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

    private void addKittenParts() {
        body.setRotationPoint(0.0F, 19.0F, 0.0F);

        torso.setRotationPoint(0.0F, 0.0F, 0.0F);
        body.addChild(torso);
        torso.setTextureOffset(24, 6).addCuboid(-1.5F, 1.0F, -6.0F, 3.0F, 2.0F, 1.0F, 0.0F, false);
        torso.setTextureOffset(24, 3).addCuboid(-2.5F, -5.0F, -4.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        torso.setTextureOffset(24, 3).addCuboid(1.5F, -5.0F, -4.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        torso.setTextureOffset(0, 0).addCuboid(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 10.0F, 0.0F, false);

        stinger.setRotationPoint(0.0F, 0.0F, 0.0F);
        torso.addChild(this.stinger);
        stinger.addCuboid(0.0F, -1.0F, 5.0F, 0.0F, 1.0F, 2.0F, 0.0F);

        leftAntenna.setRotationPoint(0.0F, -2.0F, -5.0F);
        torso.addChild(leftAntenna);
        leftAntenna.setTextureOffset(2, 0).addCuboid(1.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F, false);

        rightAntenna.setRotationPoint(0.0F, -2.0F, -5.0F);
        torso.addChild(rightAntenna);
        rightAntenna.setTextureOffset(2, 3).addCuboid(-2.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F, false);

        rightWing.setRotationPoint(-1.5F, -4.0F, -3.0F);
        body.addChild(rightWing);
        rightWing.setTextureOffset(0, 18).addCuboid(-9F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, 0.0F, false);

        leftWing.setRotationPoint(1.5F, -4.0F, -3.0F);
        body.addChild(leftWing);
        leftWing.setTextureOffset(0, 18).addCuboid(0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, 0.0F, true);

        frontLegs.setRotationPoint(1.5F, 3.0F, -2.0F);
        body.addChild(frontLegs);
        frontLegs.setTextureOffset(24, 0).addCuboid(-3F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
        frontLegs.setTextureOffset(24, 0).addCuboid(-1F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);

        middleLegs.setRotationPoint(1.5F, 3.0F, 0.0F);
        body.addChild(middleLegs);
        middleLegs.setTextureOffset(24, 0).addCuboid(-4F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
        middleLegs.setTextureOffset(24, 0).addCuboid(0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
        backLegs.setRotationPoint(1.5F, 3.0F, 2.0F);
        body.addChild(backLegs);
        backLegs.setTextureOffset(24, 0).addCuboid(-4F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
        backLegs.setTextureOffset(24, 0).addCuboid(0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
    }

    // extra parts

    private void addGelatinousParts() {
        this.body.setRotationPoint(0.0F, 19.0F, 0.0F);
        this.torso.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.body.addChild(this.torso);
        this.torso.setTextureOffset(0, 25).addCuboid(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 10.0F, 0.7F);
    }

    private void addOreCrystals() {
        ModelRenderer crystals = new ModelRenderer(this);
        crystals.setRotationPoint(-0.25F, 1.0F, -6.5F);
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
        bone2.setRotationPoint(-1.0F, -0.5412F, 1.45F);
        crystals.addChild(bone2);
        setRotationAngle(bone2, -0.3927F, 0.0F, 0.0F);
        bone2.setTextureOffset(48, 48).addCuboid(-1.5F, -5.8588F, 2.6934F, 2.0F, 2.0F, 2.0F, 0.0F, false);
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

    private void addDragonParts() {
        ModelRenderer dragon = new ModelRenderer(this);
        dragon.setRotationPoint(0.0F, -4.0F, -4.0F);
        body.addChild(dragon);
        dragon.setTextureOffset(0, 61).addCuboid(-0.5F, -1.0F, 0.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        dragon.setTextureOffset(6, 61).addCuboid(-0.5F, -1.0F, 3.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        dragon.setTextureOffset(12, 61).addCuboid(-0.5F, -1.0F, 6.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);

        ModelRenderer horns = new ModelRenderer(this);
        horns.setRotationPoint(0.0F, 1.0F, -2.0F);
        dragon.addChild(horns);
        setRotationAngle(horns, -0.6109F, 0.0F, 0.0F);
        horns.setTextureOffset(6, 55).addCuboid(1.75F, -6.0F, 1.5F, 1.0F, 4.0F, 2.0F, 0.0F, false);
        horns.setTextureOffset(0, 55).addCuboid(-2.75F, -6.0F, 1.5F, 1.0F, 4.0F, 2.0F, 0.0F, false);

    }

    private void addQueenParts() {
        ModelRenderer queen = new ModelRenderer(this);
        queen.setRotationPoint(0.0F, 0.0F, 0.0F);
        body.addChild(queen);
        queen.setTextureOffset(0, 42).addCuboid(-2.5F, -5.0F, -4.0F, 5.0F, 1.0F, 5.0F, 0.0F, false);
        queen.setTextureOffset(0, 45).addCuboid(-2.5F, -6.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        queen.setTextureOffset(0, 45).addCuboid(1.5F, -6.0F, -4.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        queen.setTextureOffset(0, 45).addCuboid(1.5F, -6.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        queen.setTextureOffset(0, 45).addCuboid(-0.5F, -6.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        queen.setTextureOffset(0, 45).addCuboid(-2.5F, -6.0F, -2.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        queen.setTextureOffset(0, 45).addCuboid(1.5F, -6.0F, -2.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        queen.setTextureOffset(0, 45).addCuboid(-2.5F, -6.0F, -4.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        queen.setTextureOffset(0, 42).addCuboid(-0.5F, -6.0F, -4.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
    }

    private void addVillagerNose() {
        ModelRenderer nose = new ModelRenderer(this);
        nose.setRotationPoint(0.0F, -21.0F, 0.0F);
        body.addChild(nose);
        nose.setTextureOffset(26, 25).addCuboid(-1.0F, 21.0F, -6.5F, 2.0F, 4.0F, 2.0F, 0.0F, false);
    }

    private void addMushrooms() {

        ModelRenderer mushroom = new ModelRenderer(this);
        mushroom.setRotationPoint(0.0F, 0.0F, 0.0F);
        body.addChild(mushroom);
        mushroom.setTextureOffset(0, 25).addCuboid(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 10.0F, 0.4F, false);

        ModelRenderer frontMushroom1 = new ModelRenderer(this);
        frontMushroom1.setRotationPoint(-1.5F, -4.0F, 0.0F);
        mushroom.addChild(frontMushroom1);
        frontMushroom1.setTextureOffset(32, 48).addCuboid(-8.0F, -14.0F, -4.0F, 16.0F, 16.0F, 0.0F, -2.0F, false);

        ModelRenderer frontMushroom2 = new ModelRenderer(this);
        frontMushroom2.setRotationPoint(0.0F, 0.0F, -2.0F);
        frontMushroom1.addChild(frontMushroom2);
        setRotationAngle(frontMushroom2, 0.0F, -1.5708F, 0.0F);
        frontMushroom2.setTextureOffset(32, 48).addCuboid(-8.0F, -14.0F, -2.0F, 16.0F, 16.0F, 0.0F, -2.0F, false);

        ModelRenderer backMushroom1 = new ModelRenderer(this);
        backMushroom1.setRotationPoint(2.5F, -4.0F, 4.0F);
        mushroom.addChild(backMushroom1);
        setRotationAngle(backMushroom1, 0.0F, -0.7854F, 0.0F);
        backMushroom1.setTextureOffset(0, 48).addCuboid(-8.0F, -14.0F, -2.0F, 16.0F, 16.0F, 0.0F, -2.0F, false);

        ModelRenderer backMushroom2 = new ModelRenderer(this);
        backMushroom2.setRotationPoint(0.0F, 0.0F, 0.0F);
        backMushroom1.addChild(backMushroom2);
        setRotationAngle(backMushroom2, 0.0F, -1.5708F, 0.0F);
        backMushroom2.setTextureOffset(0, 48).addCuboid(-8.0F, -14.0F, -2.0F, 16.0F, 16.0F, 0.0F, -2.0F, false);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

    public void addBox(ModelRenderer modelRenderer, float x, float y, float z, float xSize, float ySize, float zSize, float scale, boolean mirrored) {
        modelRenderer.addCuboid(x, y, z, xSize, ySize, zSize, scale, mirrored);
    }
}
