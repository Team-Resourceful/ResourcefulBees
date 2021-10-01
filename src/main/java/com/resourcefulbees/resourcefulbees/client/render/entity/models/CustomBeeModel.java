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
import org.jetbrains.annotations.NotNull;

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

    private float sizeModifier = 0;

    public CustomBeeModel(ModelTypes modelType) {
        this();

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
            case ARMORED:
                addArmor();
                break;
            case CROP:
                addCrops();
                break;
            case GUARDIAN:
                addSpikes();
                break;
            case YETI:
                addYetiHorns();
                break;
            case WITCH:
                addHat();
                break;
            case DEFAULT:
                addDefaultParts();
        }
    }




    public CustomBeeModel(BaseModelTypes modelType) {
        this();


        if (modelType.equals(BaseModelTypes.KITTEN)) {
            addKittenParts();
        } else if (modelType.equals(BaseModelTypes.DEFAULT)) {
            addDefaultParts();
        }
    }

    public CustomBeeModel() {
        super(false, 24.0F, 0.0F);
        this.texWidth = 64;
        this.texHeight = 64;
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
    }

    @Override
    public void prepareMobModel(@NotNull T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
        super.prepareMobModel(entityIn, limbSwing, limbSwingAmount, partialTick);
        this.bodyPitch = entityIn.getRollAmount(partialTick);
        this.stinger.visible = !entityIn.hasStung();
    }

    /**
     * Sets this entity's model rotation angles
     */
    public void setupAnim(CustomBeeEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.leftAntenna.xRot = 0.0F;
        this.rightAntenna.xRot = 0.0F;
        this.body.xRot = 0.0F;
        this.body.y = 19.0F;
        this.rightWing.yRot = 0.0F;
        this.rightWing.zRot = MathHelper.cos((ageInTicks % 98000 * 2.1F)) * (float) Math.PI * 0.15F;
        this.leftWing.xRot = this.rightWing.xRot;
        this.leftWing.yRot = this.rightWing.yRot;
        this.leftWing.zRot = -this.rightWing.zRot;
        this.frontLegs.xRot = ((float) Math.PI / 4F);
        this.middleLegs.xRot = ((float) Math.PI / 4F);
        this.backLegs.xRot = ((float) Math.PI / 4F);
        setRotationAngle(body, 0, 0, 0);

        if (!entityIn.isAngry()) {
            float f1 = MathHelper.cos(ageInTicks * 0.18F);
            this.body.xRot = 0.1F + f1 * (float) Math.PI * 0.025F;
            this.leftAntenna.xRot = f1 * (float) Math.PI * 0.03F;
            this.rightAntenna.xRot = f1 * (float) Math.PI * 0.03F;
            this.frontLegs.xRot = -f1 * (float) Math.PI * 0.1F + ((float) Math.PI / 8F);
            this.backLegs.xRot = -f1 * (float) Math.PI * 0.05F + ((float) Math.PI / 4F);
            this.body.y = 19.0F - MathHelper.cos(ageInTicks * 0.18F) * 0.9F;
        }

        if (this.bodyPitch > 0.0F) {
            this.body.xRot = ModelUtils.rotlerpRad(this.body.xRot, 3.0915928F, this.bodyPitch);
        }

        sizeModifier = entityIn.getBeeData().getSizeModifier();
        if (young) sizeModifier *= Config.CHILD_SIZE_MODIFIER.get();
    }

    @NotNull
    protected Iterable<ModelRenderer> headParts() {
        return ImmutableList.of();
    }

    @NotNull
    protected Iterable<ModelRenderer> bodyParts() {
        return ImmutableList.of(this.body);
    }

    @Override
    public void renderToBuffer(@NotNull MatrixStack matrixStackIn, @NotNull IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        matrixStackIn.pushPose();
        matrixStackIn.translate(0, 1.5 - sizeModifier * 1.5, 0);
        matrixStackIn.scale(sizeModifier, sizeModifier, sizeModifier);
        super.renderToBuffer(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        matrixStackIn.popPose();
    }

    // base bee parts

    private void addDefaultParts() {
        this.body.setPos(0.0F, 19.0F, 0.0F);
        this.torso.setPos(0.0F, 0.0F, 0.0F);
        this.body.addChild(this.torso);
        this.torso.texOffs(0, 0).addBox(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 10.0F, 0.0F);

        this.stinger.addBox(0.0F, -1.0F, 5.0F, 0.0F, 1.0F, 2.0F, 0.0F);
        this.torso.addChild(this.stinger);

        this.leftAntenna.setPos(0.0F, -2.0F, -5.0F);
        this.leftAntenna.addBox(1.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F);

        this.rightAntenna.setPos(0.0F, -2.0F, -5.0F);
        this.rightAntenna.addBox(-2.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F);
        this.torso.addChild(this.leftAntenna);
        this.torso.addChild(this.rightAntenna);

        this.rightWing.setPos(-1.5F, -4.0F, -3.0F);
        this.setRotationAngle(rightWing, 0, -0.2618F, 0);
        this.body.addChild(this.rightWing);
        this.rightWing.addBox(-9.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, 0.001F);

        this.leftWing.setPos(1.5F, -4.0F, -3.0F);
        this.setRotationAngle(leftWing, 0, 0.2618F, 0);
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

    private void addKittenParts() {
        body.setPos(0.0F, 19.0F, 0.0F);

        torso.setPos(0.0F, 0.0F, 0.0F);
        body.addChild(torso);
        torso.texOffs(24, 6).addBox(-1.5F, 1.0F, -6.0F, 3.0F, 2.0F, 1.0F, 0.0F, false);
        torso.texOffs(24, 3).addBox(-2.5F, -5.0F, -4.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        torso.texOffs(24, 3).addBox(1.5F, -5.0F, -4.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        torso.texOffs(0, 0).addBox(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 10.0F, 0.0F, false);

        stinger.setPos(0.0F, 0.0F, 0.0F);
        torso.addChild(this.stinger);
        stinger.addBox(0.0F, -1.0F, 5.0F, 0.0F, 1.0F, 2.0F, 0.0F);

        leftAntenna.setPos(0.0F, -2.0F, -5.0F);
        torso.addChild(leftAntenna);
        leftAntenna.texOffs(2, 0).addBox(1.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F, false);

        rightAntenna.setPos(0.0F, -2.0F, -5.0F);
        torso.addChild(rightAntenna);
        rightAntenna.texOffs(2, 3).addBox(-2.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F, false);

        rightWing.setPos(-1.5F, -4.0F, -3.0F);
        body.addChild(rightWing);
        rightWing.texOffs(0, 18).addBox(-9F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, 0.0F, false);

        leftWing.setPos(1.5F, -4.0F, -3.0F);
        body.addChild(leftWing);
        leftWing.texOffs(0, 18).addBox(0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, 0.0F, true);

        frontLegs.setPos(1.5F, 3.0F, -2.0F);
        body.addChild(frontLegs);
        frontLegs.texOffs(24, 0).addBox(-3F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
        frontLegs.texOffs(24, 0).addBox(-1F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);

        middleLegs.setPos(1.5F, 3.0F, 0.0F);
        body.addChild(middleLegs);
        middleLegs.texOffs(24, 0).addBox(-4F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
        middleLegs.texOffs(24, 0).addBox(0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
        backLegs.setPos(1.5F, 3.0F, 2.0F);
        body.addChild(backLegs);
        backLegs.texOffs(24, 0).addBox(-4F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
        backLegs.texOffs(24, 0).addBox(0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
    }

    // extra parts

    private void addGelatinousParts() {
        this.body.setPos(0.0F, 19.0F, 0.0F);
        this.torso.setPos(0.0F, 0.0F, 0.0F);
        this.body.addChild(this.torso);
        this.torso.texOffs(0, 25).addBox(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 10.0F, 0.7F);
    }

    private void addOreCrystals() {
        ModelRenderer crystals = new ModelRenderer(this);
        crystals.setPos(-0.25F, 1.0F, -6.5F);
        body.addChild(crystals);
        setRotationAngle(crystals, 0.3927F, 0.0F, 0.0F);
        crystals.texOffs(48, 48).addBox(1.0F, -3.8582F, 5.7674F, 2.0F, 2.0F, 2.0F, 0.0F, false);
        crystals.texOffs(48, 52).addBox(-1.0F, -6.0F, 4.0F, 2.0F, 3.0F, 2.0F, 0.0F, false);

        ModelRenderer bone = new ModelRenderer(this);
        bone.setPos(0.0F, 0.2242F, 3.1543F);
        crystals.addChild(bone);
        setRotationAngle(bone, -0.3927F, 0.0F, 0.0F);
        bone.texOffs(48, 57).addBox(-2.0F, -7.7242F, 1.8457F, 3.0F, 4.0F, 3.0F, 0.0F, true);

        ModelRenderer bone2 = new ModelRenderer(this);
        bone2.setPos(-1.0F, -0.5412F, 1.45F);
        crystals.addChild(bone2);
        setRotationAngle(bone2, -0.3927F, 0.0F, 0.0F);
        bone2.texOffs(48, 48).addBox(-1.5F, -5.8588F, 2.6934F, 2.0F, 2.0F, 2.0F, 0.0F, false);
        bone2.texOffs(48, 52).addBox(1.0F, -6.8588F, 5.6934F, 2.0F, 3.0F, 2.0F, 0.0F, true);

        ModelRenderer bone5 = new ModelRenderer(this);
        bone5.setPos(2.0F, 0.0F, 0.0F);
        bone2.addChild(bone5);
        setRotationAngle(bone5, 0.0F, 0.0F, -0.3927F);
        bone5.texOffs(48, 52).addBox(-1.5F, -6.6588F, 5.6934F, 2.0F, 3.0F, 2.0F, 0.0F, true);

        ModelRenderer bone3 = new ModelRenderer(this);
        bone3.setPos(3.0F, -4.4588F, -3.3066F);
        bone2.addChild(bone3);
        setRotationAngle(bone3, 0.0F, 0.0F, 0.5236F);
        bone3.texOffs(56, 51).addBox(-0.7321F, -2.0F, 10.0F, 2.0F, 4.0F, 2.0F, 0.0F, false);

        ModelRenderer bone4 = new ModelRenderer(this);
        bone4.setPos(-5.0981F, 0.634F, 0.0F);
        bone3.addChild(bone4);
        setRotationAngle(bone4, 0.0F, 0.0F, -1.3963F);
        bone4.texOffs(56, 51).addBox(-1.1252F, 1.9F, 11.0F, 2.0F, 4.0F, 2.0F, 0.0F, true);
    }

    private void addDragonParts() {
        ModelRenderer dragon = new ModelRenderer(this);
        dragon.setPos(0.0F, -4.0F, -4.0F);
        body.addChild(dragon);
        dragon.texOffs(0, 61).addBox(-0.5F, -1.0F, 0.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        dragon.texOffs(6, 61).addBox(-0.5F, -1.0F, 3.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        dragon.texOffs(12, 61).addBox(-0.5F, -1.0F, 6.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);

        ModelRenderer horns = new ModelRenderer(this);
        horns.setPos(0.0F, 1.0F, -2.0F);
        dragon.addChild(horns);
        setRotationAngle(horns, -0.6109F, 0.0F, 0.0F);
        horns.texOffs(6, 55).addBox(1.75F, -6.0F, 1.5F, 1.0F, 4.0F, 2.0F, 0.0F, false);
        horns.texOffs(0, 55).addBox(-2.75F, -6.0F, 1.5F, 1.0F, 4.0F, 2.0F, 0.0F, false);

    }

    private void addQueenParts() {
        ModelRenderer queen = new ModelRenderer(this);
        queen.setPos(0.0F, 0.0F, 0.0F);
        body.addChild(queen);
        queen.texOffs(0, 42).addBox(-2.5F, -5.0F, -4.0F, 5.0F, 1.0F, 5.0F, 0.0F, false);
        queen.texOffs(0, 45).addBox(-2.5F, -6.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        queen.texOffs(0, 45).addBox(1.5F, -6.0F, -4.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        queen.texOffs(0, 45).addBox(1.5F, -6.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        queen.texOffs(0, 45).addBox(-0.5F, -6.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        queen.texOffs(0, 45).addBox(-2.5F, -6.0F, -2.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        queen.texOffs(0, 45).addBox(1.5F, -6.0F, -2.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        queen.texOffs(0, 45).addBox(-2.5F, -6.0F, -4.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        queen.texOffs(0, 42).addBox(-0.5F, -6.0F, -4.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
    }

    private void addVillagerNose() {
        ModelRenderer nose = new ModelRenderer(this);
        nose.setPos(0.0F, -21.0F, 0.0F);
        body.addChild(nose);
        nose.texOffs(26, 25).addBox(-1.0F, 21.0F, -6.5F, 2.0F, 4.0F, 2.0F, 0.0F, false);
    }

    private void addMushrooms() {

        ModelRenderer mushroom = new ModelRenderer(this);
        mushroom.setPos(0.0F, 0.0F, 0.0F);
        body.addChild(mushroom);
        mushroom.texOffs(0, 25).addBox(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 10.0F, 0.4F, false);

        ModelRenderer frontMushroom1 = new ModelRenderer(this);
        frontMushroom1.setPos(-1.5F, -4.0F, 0.0F);
        mushroom.addChild(frontMushroom1);
        frontMushroom1.texOffs(32, 48).addBox(-8.0F, -14.0F, -4.0F, 16.0F, 16.0F, 0.0F, -2.0F, false);

        ModelRenderer frontMushroom2 = new ModelRenderer(this);
        frontMushroom2.setPos(0.0F, 0.0F, -2.0F);
        frontMushroom1.addChild(frontMushroom2);
        setRotationAngle(frontMushroom2, 0.0F, -1.5708F, 0.0F);
        frontMushroom2.texOffs(32, 48).addBox(-8.0F, -14.0F, -2.0F, 16.0F, 16.0F, 0.0F, -2.0F, false);

        ModelRenderer backMushroom1 = new ModelRenderer(this);
        backMushroom1.setPos(2.5F, -4.0F, 4.0F);
        mushroom.addChild(backMushroom1);
        setRotationAngle(backMushroom1, 0.0F, -0.7854F, 0.0F);
        backMushroom1.texOffs(0, 48).addBox(-8.0F, -14.0F, -2.0F, 16.0F, 16.0F, 0.0F, -2.0F, false);

        ModelRenderer backMushroom2 = new ModelRenderer(this);
        backMushroom2.setPos(0.0F, 0.0F, 0.0F);
        backMushroom1.addChild(backMushroom2);
        setRotationAngle(backMushroom2, 0.0F, -1.5708F, 0.0F);
        backMushroom2.texOffs(0, 48).addBox(-8.0F, -14.0F, -2.0F, 16.0F, 16.0F, 0.0F, -2.0F, false);
    }

    private void addCrops() {
        ModelRenderer crop = new ModelRenderer(this);
        crop.setPos(0.0F, 0.0F, 0.0F);
        body.addChild(crop);
        crop.texOffs(0, 25).addBox(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 10.0F, 0.4F, false);

        ModelRenderer crop2 = new ModelRenderer(this);
        crop2.setPos(-1.6F, -4.0F, 1.5F);
        crop.addChild(crop2);


        ModelRenderer crop3 = new ModelRenderer(this);
        crop3.setPos(0.0F, -9.0F, -2.0F);
        crop2.addChild(crop3);
        setRotationAngle(crop3, 0.0F, -1.5708F, 0.0F);
        crop3.texOffs(0, 46).addBox(-8.3F, -3.9F, -7.9F, 18.0F, 18.0F, 0.0F, -4.0F, true);
        crop3.texOffs(0, 46).addBox(-8.3F, -3.9F, -3.4F, 18.0F, 18.0F, 0.0F, -4.0F, true);

        ModelRenderer crop4 = new ModelRenderer(this);
        crop4.setPos(2.5F, 5.1F, -1.625F);
        crop3.addChild(crop4);
        setRotationAngle(crop4, 0.0F, -1.5708F, 0.0F);
        crop4.texOffs(0, 46).addBox(-9.0F, -9.0F, 0.0F, 18.0F, 18.0F, 0.0F, -4.0F, true);

        ModelRenderer crop5 = new ModelRenderer(this);
        crop5.setPos(6.85F, 5.1F, -1.625F);
        crop3.addChild(crop5);
        setRotationAngle(crop5, 0.0F, -1.5708F, 0.0F);
        crop5.texOffs(0, 46).addBox(-9.0F, -9.0F, 0.0F, 18.0F, 18.0F, 0.0F, -4.0F, true);
    }

    private void addArmor() {
        ModelRenderer armored = new ModelRenderer(this);
        armored.setPos(0.0F, 0.0F, 0.0F);
        body.addChild(armored);
        armored.texOffs(34, 3).addBox(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 7.0F, 0.6F, false);
        armored.texOffs(0, 25).addBox(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 10.0F, 0.25F, false);
    }

    private void addSpikes() {
        ModelRenderer guardian = new ModelRenderer(this);
        guardian.setPos(0.0F, 5.0F, 0.0F);
        body.addChild(guardian);
        guardian.texOffs(0, 25).addBox(-3.5F, -9.0F, -5.0F, 7.0F, 7.0F, 10.0F, 0.4F, false);

        ModelRenderer spike1 = new ModelRenderer(this);
        spike1.setPos(3.5F, -5.5F, 5.0F);
        guardian.addChild(spike1);
        setRotationAngle(spike1, 0.0F, 2.3562F, 0.0F);
        spike1.texOffs(40, 24).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.25F, false);

        ModelRenderer spike2 = new ModelRenderer(this);
        spike2.setPos(-3.5F, -5.5F, -5.0F);
        guardian.addChild(spike2);
        setRotationAngle(spike2, 0.0F, -0.7854F, 0.0F);
        spike2.texOffs(40, 22).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.25F, false);

        ModelRenderer spike3 = new ModelRenderer(this);
        spike3.setPos(3.5F, -5.5F, -4.75F);
        guardian.addChild(spike3);
        setRotationAngle(spike3, 0.0F, -2.3562F, 0.0F);
        spike3.texOffs(40, 20).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.25F, false);

        ModelRenderer spike4 = new ModelRenderer(this);
        spike4.setPos(-3.5F, -5.5F, 5.0F);
        guardian.addChild(spike4);
        setRotationAngle(spike4, 0.0F, 0.7854F, 0.0F);
        spike4.texOffs(40, 18).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.25F, false);

        ModelRenderer spike5 = new ModelRenderer(this);
        spike5.setPos(-3.5F, -2.0F, 0.0F);
        guardian.addChild(spike5);
        setRotationAngle(spike5, 0.0F, 0.0F, -0.7854F);
        spike5.texOffs(32, 24).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.25F, false);

        ModelRenderer spike6 = new ModelRenderer(this);
        spike6.setPos(0.0F, -2.0F, 5.0F);
        guardian.addChild(spike6);
        setRotationAngle(spike6, 1.5708F, 2.3562F, 1.5708F);
        spike6.texOffs(32, 22).addBox(-2.5F, -0.75F, -0.5F, 3.0F, 1.0F, 1.0F, 0.25F, false);

        ModelRenderer spike7 = new ModelRenderer(this);
        spike7.setPos(0.0F, -2.0F, -5.0F);
        guardian.addChild(spike7);
        setRotationAngle(spike7, -1.5708F, -2.3562F, 1.5708F);
        spike7.texOffs(32, 20).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.25F, false);

        ModelRenderer spike8 = new ModelRenderer(this);
        spike8.setPos(3.5F, -2.0F, 0.0F);
        guardian.addChild(spike8);
        setRotationAngle(spike8, 0.0F, 3.1416F, 0.7854F);
        spike8.texOffs(32, 18).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.25F, false);

        ModelRenderer spike9 = new ModelRenderer(this);
        spike9.setPos(0.0F, -9.0F, 5.0F);
        guardian.addChild(spike9);
        setRotationAngle(spike9, 1.5708F, 0.7854F, 1.5708F);
        spike9.texOffs(24, 24).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.25F, false);

        ModelRenderer spike10 = new ModelRenderer(this);
        spike10.setPos(0.0F, -9.0F, -5.0F);
        guardian.addChild(spike10);
        setRotationAngle(spike10, -1.5708F, -0.7854F, 1.5708F);
        spike10.texOffs(24, 22).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.25F, false);

        ModelRenderer spike11 = new ModelRenderer(this);
        spike11.setPos(3.5F, -9.0F, 0.0F);
        guardian.addChild(spike11);
        setRotationAngle(spike11, 3.1416F, 0.0F, 2.3562F);
        spike11.texOffs(24, 20).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.25F, false);

        ModelRenderer spike12 = new ModelRenderer(this);
        spike12.setPos(-3.5F, -9.0F, 0.0F);
        guardian.addChild(spike12);
        setRotationAngle(spike12, 0.0F, 0.0F, 0.7854F);
        spike12.texOffs(24, 18).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.25F, false);
    }

    private void addYetiHorns() {
        ModelRenderer yetiHorns = new ModelRenderer(this);
        yetiHorns.setPos(0.0F, 0.0F, 0.0F);
        body.addChild(yetiHorns);


        ModelRenderer rightHorn = new ModelRenderer(this);
        rightHorn.setPos(-2.0F, -4.0F, -4.0F);
        yetiHorns.addChild(rightHorn);
        setRotationAngle(rightHorn, 0.0F, 0.0F, -0.9599F);
        rightHorn.texOffs(34, 12).addBox(-2.0F, -3.0F, -0.5F, 2.0F, 3.0F, 2.0F, 0.0F, true);

        ModelRenderer leftHorn = new ModelRenderer(this);
        leftHorn.setPos(2.0F, -4.0F, -4.0F);
        yetiHorns.addChild(leftHorn);
        setRotationAngle(leftHorn, 0.0F, 0.0F, 0.9599F);
        leftHorn.texOffs(34, 12).addBox(0.0F, -3.0F, -0.5F, 2.0F, 3.0F, 2.0F, 0.0F, false);
    }

    private void addHat() {
        ModelRenderer witch = new ModelRenderer(this);
        witch.setPos(0.0F, 0.0F, 0.0F);
        body.addChild(witch);
        witch.texOffs(0, 25).addBox(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 10.0F, 0.4F, false);

        ModelRenderer hat1 = new ModelRenderer(this);
        hat1.setPos(0.0F, 0.0F, 0.0F);
        witch.addChild(hat1);
        witch.texOffs(0, 57).addBox(-3.0F, -5.0F, -4.5F, 6.0F, 1.0F, 6.0F, 0.0F, false);

        ModelRenderer hat2 = new ModelRenderer(this);
        hat2.setPos(0.0F, 0.0F, 0.0F);
        witch.addChild(hat2);
        setRotationAngle(hat2, 0.0F, 0.0F, -0.3054F);
        hat2.texOffs(24, 55).addBox(1.8F, -9.6F, -2.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);

        ModelRenderer hat3 = new ModelRenderer(this);
        hat3.setPos(0.0F, 0.0F, 0.0F);
        witch.addChild(hat3);
        setRotationAngle(hat3, 0.0F, 0.0F, -0.1745F);
        hat3.texOffs(16, 53).addBox(0.2F, -8.9F, -2.5F, 2.0F, 2.0F, 2.0F, 0.0F, false);

        ModelRenderer hat4 = new ModelRenderer(this);
        hat4.setPos(0.0F, 0.0F, 0.0F);
        witch.addChild(hat4);
        setRotationAngle(hat4, 0.0F, 0.0F, -0.0698F);
        hat4.texOffs(0, 50).addBox(-1.6F, -7.3F, -3.5F, 4.0F, 3.0F, 4.0F, 0.0F, false);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
}
