package com.teamresourceful.resourcefulbees.client.render.entity.models;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.teamresourceful.resourcefulbees.config.Config;
import com.teamresourceful.resourcefulbees.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.lib.BaseModelTypes;
import com.teamresourceful.resourcefulbees.lib.ModelTypes;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.ModelUtils;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class CustomBeeModel<T extends CustomBeeEntity> extends AgeableListModel<T> {
    private final ModelPart body;
    private final ModelPart torso;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    private final ModelPart frontLegs;
    private final ModelPart middleLegs;
    private final ModelPart backLegs;
    private final ModelPart stinger;
    private final ModelPart leftAntenna;
    private final ModelPart rightAntenna;
    private float bodyPitch;

    private float sizeModifier = 0;

    public CustomBeeModel(ModelTypes modelType) {
        this();

        switch (modelType) {
            case SLIME:
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
            case CLOAKED:
                addCloak();
                break;
            case YETI:
                addHorns();
                break;
            default:
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
        this.body = new ModelPart(this);
        this.torso = new ModelPart(this);
        this.stinger = new ModelPart(this, 26, 7);
        this.leftAntenna = new ModelPart(this, 2, 0);
        this.rightAntenna = new ModelPart(this, 2, 3);
        this.rightWing = new ModelPart(this, 0, 18);
        this.leftWing = new ModelPart(this, 0, 18);
        this.frontLegs = new ModelPart(this);
        this.middleLegs = new ModelPart(this);
        this.backLegs = new ModelPart(this);
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
        this.rightWing.zRot = Mth.cos((ageInTicks % 98000 * 2.1F)) * (float) Math.PI * 0.15F;
        this.leftWing.xRot = this.rightWing.xRot;
        this.leftWing.yRot = this.rightWing.yRot;
        this.leftWing.zRot = -this.rightWing.zRot;
        this.frontLegs.xRot = ((float) Math.PI / 4F);
        this.middleLegs.xRot = ((float) Math.PI / 4F);
        this.backLegs.xRot = ((float) Math.PI / 4F);
        setRotationAngle(body, 0, 0, 0);

        if (!entityIn.isAngry()) {
            float f1 = Mth.cos(ageInTicks * 0.18F);
            this.body.xRot = 0.1F + f1 * (float) Math.PI * 0.025F;
            this.leftAntenna.xRot = f1 * (float) Math.PI * 0.03F;
            this.rightAntenna.xRot = f1 * (float) Math.PI * 0.03F;
            this.frontLegs.xRot = -f1 * (float) Math.PI * 0.1F + ((float) Math.PI / 8F);
            this.backLegs.xRot = -f1 * (float) Math.PI * 0.05F + ((float) Math.PI / 4F);
            this.body.y = 19.0F - Mth.cos(ageInTicks * 0.18F) * 0.9F;
        }

        if (this.bodyPitch > 0.0F) {
            this.body.xRot = ModelUtils.rotlerpRad(this.body.xRot, 3.0915928F, this.bodyPitch);
        }

        sizeModifier = entityIn.getRenderData().getSizeModifier();
        if (young) sizeModifier *= Config.CHILD_SIZE_MODIFIER.get();
    }

    @Nonnull
    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of();
    }

    @Nonnull
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(this.body);
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack matrixStackIn, @NotNull VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
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
        ModelPart crystals = new ModelPart(this);
        crystals.setPos(-0.25F, 1.0F, -6.5F);
        body.addChild(crystals);
        setRotationAngle(crystals, 0.3927F, 0.0F, 0.0F);
        crystals.texOffs(48, 48).addBox(1.0F, -3.8582F, 5.7674F, 2.0F, 2.0F, 2.0F, 0.0F, false);
        crystals.texOffs(48, 52).addBox(-1.0F, -6.0F, 4.0F, 2.0F, 3.0F, 2.0F, 0.0F, false);

        ModelPart bone = new ModelPart(this);
        bone.setPos(0.0F, 0.2242F, 3.1543F);
        crystals.addChild(bone);
        setRotationAngle(bone, -0.3927F, 0.0F, 0.0F);
        bone.texOffs(48, 57).addBox(-2.0F, -7.7242F, 1.8457F, 3.0F, 4.0F, 3.0F, 0.0F, true);

        ModelPart bone2 = new ModelPart(this);
        bone2.setPos(-1.0F, -0.5412F, 1.45F);
        crystals.addChild(bone2);
        setRotationAngle(bone2, -0.3927F, 0.0F, 0.0F);
        bone2.texOffs(48, 48).addBox(-1.5F, -5.8588F, 2.6934F, 2.0F, 2.0F, 2.0F, 0.0F, false);
        bone2.texOffs(48, 52).addBox(1.0F, -6.8588F, 5.6934F, 2.0F, 3.0F, 2.0F, 0.0F, true);

        ModelPart bone5 = new ModelPart(this);
        bone5.setPos(2.0F, 0.0F, 0.0F);
        bone2.addChild(bone5);
        setRotationAngle(bone5, 0.0F, 0.0F, -0.3927F);
        bone5.texOffs(48, 52).addBox(-1.5F, -6.6588F, 5.6934F, 2.0F, 3.0F, 2.0F, 0.0F, true);

        ModelPart bone3 = new ModelPart(this);
        bone3.setPos(3.0F, -4.4588F, -3.3066F);
        bone2.addChild(bone3);
        setRotationAngle(bone3, 0.0F, 0.0F, 0.5236F);
        bone3.texOffs(56, 51).addBox(-0.7321F, -2.0F, 10.0F, 2.0F, 4.0F, 2.0F, 0.0F, false);

        ModelPart bone4 = new ModelPart(this);
        bone4.setPos(-5.0981F, 0.634F, 0.0F);
        bone3.addChild(bone4);
        setRotationAngle(bone4, 0.0F, 0.0F, -1.3963F);
        bone4.texOffs(56, 51).addBox(-1.1252F, 1.9F, 11.0F, 2.0F, 4.0F, 2.0F, 0.0F, true);
    }

    private void addDragonParts() {
        ModelPart dragon = new ModelPart(this);
        dragon.setPos(0.0F, -4.0F, -4.0F);
        body.addChild(dragon);
        dragon.texOffs(0, 61).addBox(-0.5F, -1.0F, 0.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        dragon.texOffs(6, 61).addBox(-0.5F, -1.0F, 3.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
        dragon.texOffs(12, 61).addBox(-0.5F, -1.0F, 6.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);

        ModelPart horns = new ModelPart(this);
        horns.setPos(0.0F, 1.0F, -2.0F);
        dragon.addChild(horns);
        setRotationAngle(horns, -0.6109F, 0.0F, 0.0F);
        horns.texOffs(6, 55).addBox(1.75F, -6.0F, 1.5F, 1.0F, 4.0F, 2.0F, 0.0F, false);
        horns.texOffs(0, 55).addBox(-2.75F, -6.0F, 1.5F, 1.0F, 4.0F, 2.0F, 0.0F, false);

    }

    private void addQueenParts() {
        ModelPart queen = new ModelPart(this);
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
        ModelPart nose = new ModelPart(this);
        nose.setPos(0.0F, -21.0F, 0.0F);
        body.addChild(nose);
        nose.texOffs(26, 25).addBox(-1.0F, 21.0F, -6.5F, 2.0F, 4.0F, 2.0F, 0.0F, false);
    }

    private void addMushrooms() {
        ModelPart mushroom = new ModelPart(this);
        mushroom.setPos(0.0F, 0.0F, 0.0F);
        body.addChild(mushroom);

        ModelPart frontMushroom1 = new ModelPart(this);
        frontMushroom1.setPos(-1.5F, -4.0F, 0.0F);
        mushroom.addChild(frontMushroom1);
        frontMushroom1.texOffs(32, 48).addBox(-8.0F, -14.0F, -4.0F, 16.0F, 16.0F, 0.0F, -2.0F, false);

        ModelPart frontMushroom2 = new ModelPart(this);
        frontMushroom2.setPos(0.0F, 0.0F, -2.0F);
        frontMushroom1.addChild(frontMushroom2);
        setRotationAngle(frontMushroom2, 0.0F, -1.5708F, 0.0F);
        frontMushroom2.texOffs(32, 48).addBox(-8.0F, -14.0F, -2.0F, 16.0F, 16.0F, 0.0F, -2.0F, false);

        ModelPart backMushroom1 = new ModelPart(this);
        backMushroom1.setPos(2.5F, -4.0F, 4.0F);
        mushroom.addChild(backMushroom1);
        setRotationAngle(backMushroom1, 0.0F, -0.7854F, 0.0F);
        backMushroom1.texOffs(0, 48).addBox(-8.0F, -14.0F, -2.0F, 16.0F, 16.0F, 0.0F, -2.0F, false);

        ModelPart backMushroom2 = new ModelPart(this);
        backMushroom2.setPos(0.0F, 0.0F, 0.0F);
        backMushroom1.addChild(backMushroom2);
        setRotationAngle(backMushroom2, 0.0F, -1.5708F, 0.0F);
        backMushroom2.texOffs(0, 48).addBox(-8.0F, -14.0F, -2.0F, 16.0F, 16.0F, 0.0F, -2.0F, false);
    }

    private void addCrops() {
        ModelPart crop = new ModelPart(this);
        crop.setPos(0.0F, 0.0F, 0.0F);
        body.addChild(crop);

        ModelPart crop2 = new ModelPart(this);
        crop2.setPos(-1.6F, -4.0F, 1.5F);
        crop.addChild(crop2);


        ModelPart crop3 = new ModelPart(this);
        crop3.setPos(0.0F, -9.0F, -2.0F);
        crop2.addChild(crop3);
        setRotationAngle(crop3, 0.0F, -1.5708F, 0.0F);
        crop3.texOffs(0, 46).addBox(-8.3F, -3.9F, -7.9F, 18.0F, 18.0F, 0.0F, -4.0F, true);
        crop3.texOffs(0, 46).addBox(-8.3F, -3.9F, -3.4F, 18.0F, 18.0F, 0.0F, -4.0F, true);

        ModelPart crop4 = new ModelPart(this);
        crop4.setPos(2.5F, 5.1F, -1.625F);
        crop3.addChild(crop4);
        setRotationAngle(crop4, 0.0F, -1.5708F, 0.0F);
        crop4.texOffs(0, 46).addBox(-9.0F, -9.0F, 0.0F, 18.0F, 18.0F, 0.0F, -4.0F, true);

        ModelPart crop5 = new ModelPart(this);
        crop5.setPos(6.85F, 5.1F, -1.625F);
        crop3.addChild(crop5);
        setRotationAngle(crop5, 0.0F, -1.5708F, 0.0F);
        crop5.texOffs(0, 46).addBox(-9.0F, -9.0F, 0.0F, 18.0F, 18.0F, 0.0F, -4.0F, true);
    }

    private void addArmor() {
        ModelPart armored = new ModelPart(this);
        armored.setPos(0.0F, 0.0F, 0.0F);
        body.addChild(armored);
        armored.texOffs(34, 3).addBox(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 7.0F, 0.6F, false);
        armored.texOffs(0, 25).addBox(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 10.0F, 0.25F, false);
    }

    private void addSpikes() {
        ModelPart guardian = new ModelPart(this);
        guardian.setPos(0.0F, 5.0F, 0.0F);
        body.addChild(guardian);

        ModelPart cube1 = new ModelPart(this);
        cube1.setPos(3.5F, -5.5F, 5.0F);
        guardian.addChild(cube1);
        setRotationAngle(cube1, 0.0F, 2.3562F, 0.0F);
        cube1.texOffs(40, 24).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.25F, false);

        ModelPart cube2 = new ModelPart(this);
        cube2.setPos(-3.5F, -5.5F, -5.0F);
        guardian.addChild(cube2);
        setRotationAngle(cube2, 0.0F, -0.7854F, 0.0F);
        cube2.texOffs(40, 22).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.25F, false);

        ModelPart cube3 = new ModelPart(this);
        cube3.setPos(3.5F, -5.5F, -4.75F);
        guardian.addChild(cube3);
        setRotationAngle(cube3, 0.0F, -2.3562F, 0.0F);
        cube3.texOffs(40, 20).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.25F, false);

        ModelPart cube4 = new ModelPart(this);
        cube4.setPos(-3.5F, -5.5F, 5.0F);
        guardian.addChild(cube4);
        setRotationAngle(cube4, 0.0F, 0.7854F, 0.0F);
        cube4.texOffs(40, 18).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.25F, false);

        ModelPart cube5 = new ModelPart(this);
        cube5.setPos(-3.5F, -2.0F, 0.0F);
        guardian.addChild(cube5);
        setRotationAngle(cube5, 0.0F, 0.0F, -0.7854F);
        cube5.texOffs(32, 24).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.25F, false);

        ModelPart cube6 = new ModelPart(this);
        cube6.setPos(0.0F, -2.0F, 5.0F);
        guardian.addChild(cube6);
        setRotationAngle(cube6, 1.5708F, 2.3562F, 1.5708F);
        cube6.texOffs(32, 22).addBox(-2.5F, -0.75F, -0.5F, 3.0F, 1.0F, 1.0F, 0.25F, false);

        ModelPart cube7 = new ModelPart(this);
        cube7.setPos(0.0F, -2.0F, -5.0F);
        guardian.addChild(cube7);
        setRotationAngle(cube7, -1.5708F, -2.3562F, 1.5708F);
        cube7.texOffs(32, 20).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.25F, false);

        ModelPart cube8 = new ModelPart(this);
        cube8.setPos(3.5F, -2.0F, 0.0F);
        guardian.addChild(cube8);
        setRotationAngle(cube8, 0.0F, 3.1416F, 0.7854F);
        cube8.texOffs(32, 18).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.25F, false);

        ModelPart cube9 = new ModelPart(this);
        cube9.setPos(0.0F, -9.0F, 5.0F);
        guardian.addChild(cube9);
        setRotationAngle(cube9, 1.5708F, 0.7854F, 1.5708F);
        cube9.texOffs(24, 24).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.25F, false);

        ModelPart cube10 = new ModelPart(this);
        cube10.setPos(0.0F, -9.0F, -5.0F);
        guardian.addChild(cube10);
        setRotationAngle(cube10, -1.5708F, -0.7854F, 1.5708F);
        cube10.texOffs(24, 22).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.25F, false);

        ModelPart cube11 = new ModelPart(this);
        cube11.setPos(3.5F, -9.0F, 0.0F);
        guardian.addChild(cube11);
        setRotationAngle(cube11, 3.1416F, 0.0F, 2.3562F);
        cube11.texOffs(24, 20).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.25F, false);

        ModelPart cube12 = new ModelPart(this);
        cube12.setPos(-3.5F, -9.0F, 0.0F);
        guardian.addChild(cube12);
        setRotationAngle(cube12, 0.0F, 0.0F, 0.7854F);
        cube12.texOffs(24, 18).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.25F, false);
    }

    private void addCloak() {
        ModelPart cloak = new ModelPart(this);
        cloak.setPos(0.0F, 0.0F, 0.0F);
        body.addChild(cloak);
        cloak.texOffs(0, 25).addBox(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 10.0F, 0.4F, false);
    }

    private void addHorns() {
        ModelPart yeti_horns = new ModelPart(this);
        yeti_horns.setPos(0.0F, 0.0F, 0.0F);
        body.addChild(yeti_horns);


        ModelPart horn1 = new ModelPart(this);
        horn1.setPos(-2.0F, -4.0F, -4.0F);
        yeti_horns.addChild(horn1);
        setRotationAngle(horn1, 0.0F, 0.0F, -0.9599F);
        horn1.texOffs(34, 12).addBox(-2.0F, -3.0F, -0.5F, 2.0F, 3.0F, 2.0F, 0.0F, true);

        ModelPart horn2 = new ModelPart(this);
        horn2.setPos(2.0F, -4.0F, -4.0F);
        yeti_horns.addChild(horn2);
        setRotationAngle(horn2, 0.0F, 0.0F, 0.9599F);
        horn2.texOffs(34, 12).addBox(0.0F, -3.0F, -0.5F, 2.0F, 3.0F, 2.0F, 0.0F, false);
    }

    public void setRotationAngle(ModelPart modelPart, float x, float y, float z) {
        modelPart.xRot = x;
        modelPart.yRot = y;
        modelPart.zRot = z;
    }
}
