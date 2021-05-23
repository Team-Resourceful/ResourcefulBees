// Made with Blockbench 3.8.4
// Exported for Minecraft version 1.15 - 1.16
// Paste this class into your mod and generate all required imports


public class BaseBeeModel extends EntityModel<Entity> {
	private final ModelRenderer body;
	private final ModelRenderer torso;
	private final ModelRenderer stinger;
	private final ModelRenderer leftAntenna;
	private final ModelRenderer rightAntenna;
	private final ModelRenderer rightWing;
	private final ModelRenderer leftWing;
	private final ModelRenderer frontLegs;
	private final ModelRenderer middleLegs;
	private final ModelRenderer backLegs;
	private final ModelRenderer crystals;
	private final ModelRenderer bone;
	private final ModelRenderer bone2;
	private final ModelRenderer bone5;
	private final ModelRenderer bone3;
	private final ModelRenderer bone4;
	private final ModelRenderer dragon;
	private final ModelRenderer horns;
	private final ModelRenderer gel;
	private final ModelRenderer queen;
	private final ModelRenderer nose;
	private final ModelRenderer mushroom;
	private final ModelRenderer frontMushroom1;
	private final ModelRenderer frontMushroom2;
	private final ModelRenderer backMushroom1;
	private final ModelRenderer backMushroom2;
	private final ModelRenderer crop;
	private final ModelRenderer crop2;
	private final ModelRenderer crop3;
	private final ModelRenderer crop3_r1;
	private final ModelRenderer crop3_r2;
	private final ModelRenderer armored;
	private final ModelRenderer guardian;
	private final ModelRenderer cube_r1;
	private final ModelRenderer cube_r2;
	private final ModelRenderer cube_r3;
	private final ModelRenderer cube_r4;
	private final ModelRenderer cube_r5;
	private final ModelRenderer cube_r6;
	private final ModelRenderer cube_r7;
	private final ModelRenderer cube_r8;
	private final ModelRenderer cube_r9;
	private final ModelRenderer cube_r10;
	private final ModelRenderer cube_r11;
	private final ModelRenderer cube_r12;
	private final ModelRenderer yeti_horns;
	private final ModelRenderer cube_r13;
	private final ModelRenderer cube_r14;
	private final ModelRenderer cloaked;

	public BaseBeeModel() {
		textureWidth = 64;
		textureHeight = 64;

		body = new ModelRenderer(this);
		body.setRotationPoint(0.0F, 19.0F, 0.0F);
		

		torso = new ModelRenderer(this);
		torso.setRotationPoint(0.0F, 0.0F, 0.0F);
		body.addChild(torso);
		torso.setTextureOffset(0, 0).addBox(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 10.0F, 0.0F, false);

		stinger = new ModelRenderer(this);
		stinger.setRotationPoint(0.0F, 0.0F, 0.0F);
		torso.addChild(stinger);
		stinger.setTextureOffset(24, 7).addBox(0.0F, -1.0F, 5.0F, 0.0F, 1.0F, 2.0F, 0.0F, false);

		leftAntenna = new ModelRenderer(this);
		leftAntenna.setRotationPoint(0.0F, -2.0F, -5.0F);
		torso.addChild(leftAntenna);
		leftAntenna.setTextureOffset(2, 0).addBox(1.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F, false);

		rightAntenna = new ModelRenderer(this);
		rightAntenna.setRotationPoint(0.0F, -2.0F, -5.0F);
		torso.addChild(rightAntenna);
		rightAntenna.setTextureOffset(2, 3).addBox(-2.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F, false);

		rightWing = new ModelRenderer(this);
		rightWing.setRotationPoint(-1.5F, -4.0F, -3.0F);
		body.addChild(rightWing);
		rightWing.setTextureOffset(0, 18).addBox(-9.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, 0.0F, false);

		leftWing = new ModelRenderer(this);
		leftWing.setRotationPoint(1.5F, -4.0F, -3.0F);
		body.addChild(leftWing);
		leftWing.setTextureOffset(0, 18).addBox(0.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, 0.0F, true);

		frontLegs = new ModelRenderer(this);
		frontLegs.setRotationPoint(1.5F, 3.0F, -2.0F);
		body.addChild(frontLegs);
		frontLegs.setTextureOffset(28, 1).addBox(-3.0F, 0.0F, 0.0F, 3.0F, 2.0F, 0.0F, 0.0F, false);

		middleLegs = new ModelRenderer(this);
		middleLegs.setRotationPoint(1.5F, 3.0F, 0.0F);
		body.addChild(middleLegs);
		middleLegs.setTextureOffset(27, 3).addBox(-4.0F, 0.0F, 0.0F, 5.0F, 2.0F, 0.0F, 0.0F, false);

		backLegs = new ModelRenderer(this);
		backLegs.setRotationPoint(1.5F, 3.0F, 2.0F);
		body.addChild(backLegs);
		backLegs.setTextureOffset(27, 5).addBox(-4.0F, 0.0F, 0.0F, 5.0F, 2.0F, 0.0F, 0.0F, false);

		crystals = new ModelRenderer(this);
		crystals.setRotationPoint(-0.25F, 1.0F, -6.5F);
		body.addChild(crystals);
		setRotationAngle(crystals, 0.3927F, 0.0F, 0.0F);
		crystals.setTextureOffset(48, 48).addBox(1.0F, -3.8582F, 5.7674F, 2.0F, 2.0F, 2.0F, 0.0F, false);
		crystals.setTextureOffset(48, 52).addBox(-1.0F, -6.0F, 4.0F, 2.0F, 3.0F, 2.0F, 0.0F, false);

		bone = new ModelRenderer(this);
		bone.setRotationPoint(0.0F, 0.2242F, 3.1543F);
		crystals.addChild(bone);
		setRotationAngle(bone, -0.3927F, 0.0F, 0.0F);
		bone.setTextureOffset(48, 57).addBox(-2.0F, -7.7242F, 1.8457F, 3.0F, 4.0F, 3.0F, 0.0F, true);

		bone2 = new ModelRenderer(this);
		bone2.setRotationPoint(-1.0F, -0.5412F, 1.45F);
		crystals.addChild(bone2);
		setRotationAngle(bone2, -0.3927F, 0.0F, 0.0F);
		bone2.setTextureOffset(48, 48).addBox(-1.5F, -5.8588F, 2.6934F, 2.0F, 2.0F, 2.0F, 0.0F, false);
		bone2.setTextureOffset(48, 52).addBox(1.0F, -6.8588F, 5.6934F, 2.0F, 3.0F, 2.0F, 0.0F, true);

		bone5 = new ModelRenderer(this);
		bone5.setRotationPoint(2.0F, 0.0F, 0.0F);
		bone2.addChild(bone5);
		setRotationAngle(bone5, 0.0F, 0.0F, -0.3927F);
		bone5.setTextureOffset(48, 52).addBox(-1.5F, -6.6588F, 5.6934F, 2.0F, 3.0F, 2.0F, 0.0F, true);

		bone3 = new ModelRenderer(this);
		bone3.setRotationPoint(3.0F, -4.4588F, -3.3066F);
		bone2.addChild(bone3);
		setRotationAngle(bone3, 0.0F, 0.0F, 0.5236F);
		bone3.setTextureOffset(56, 51).addBox(-0.7321F, -2.0F, 10.0F, 2.0F, 4.0F, 2.0F, 0.0F, false);

		bone4 = new ModelRenderer(this);
		bone4.setRotationPoint(-5.0981F, 0.634F, 0.0F);
		bone3.addChild(bone4);
		setRotationAngle(bone4, 0.0F, 0.0F, -1.3963F);
		bone4.setTextureOffset(56, 51).addBox(-1.1252F, 1.9F, 11.0F, 2.0F, 4.0F, 2.0F, 0.0F, true);

		dragon = new ModelRenderer(this);
		dragon.setRotationPoint(0.0F, -4.0F, -4.0F);
		body.addChild(dragon);
		dragon.setTextureOffset(0, 61).addBox(-0.5F, -1.0F, 0.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
		dragon.setTextureOffset(6, 61).addBox(-0.5F, -1.0F, 3.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
		dragon.setTextureOffset(12, 61).addBox(-0.5F, -1.0F, 6.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);

		horns = new ModelRenderer(this);
		horns.setRotationPoint(0.0F, 1.0F, -2.0F);
		dragon.addChild(horns);
		setRotationAngle(horns, -0.6109F, 0.0F, 0.0F);
		horns.setTextureOffset(6, 55).addBox(1.75F, -6.0F, 1.5F, 1.0F, 4.0F, 2.0F, 0.0F, false);
		horns.setTextureOffset(0, 55).addBox(-2.75F, -6.0F, 1.5F, 1.0F, 4.0F, 2.0F, 0.0F, false);

		gel = new ModelRenderer(this);
		gel.setRotationPoint(0.0F, 0.0F, 0.0F);
		body.addChild(gel);
		gel.setTextureOffset(0, 25).addBox(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 10.0F, 0.7F, false);

		queen = new ModelRenderer(this);
		queen.setRotationPoint(0.0F, 0.0F, 0.0F);
		body.addChild(queen);
		queen.setTextureOffset(0, 42).addBox(-2.5F, -5.0F, -4.0F, 5.0F, 1.0F, 5.0F, 0.0F, false);
		queen.setTextureOffset(0, 45).addBox(-2.5F, -6.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
		queen.setTextureOffset(0, 45).addBox(1.5F, -6.0F, -4.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
		queen.setTextureOffset(0, 45).addBox(1.5F, -6.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
		queen.setTextureOffset(0, 45).addBox(-0.5F, -6.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
		queen.setTextureOffset(0, 45).addBox(-2.5F, -6.0F, -2.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
		queen.setTextureOffset(0, 45).addBox(1.5F, -6.0F, -2.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
		queen.setTextureOffset(0, 45).addBox(-2.5F, -6.0F, -4.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
		queen.setTextureOffset(0, 42).addBox(-0.5F, -6.0F, -4.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);

		nose = new ModelRenderer(this);
		nose.setRotationPoint(0.0F, -21.0F, 0.0F);
		body.addChild(nose);
		nose.setTextureOffset(26, 25).addBox(-1.0F, 21.0F, -6.5F, 2.0F, 4.0F, 2.0F, 0.0F, false);

		mushroom = new ModelRenderer(this);
		mushroom.setRotationPoint(0.0F, 0.0F, 0.0F);
		body.addChild(mushroom);
		

		frontMushroom1 = new ModelRenderer(this);
		frontMushroom1.setRotationPoint(-1.5F, -4.0F, 0.0F);
		mushroom.addChild(frontMushroom1);
		frontMushroom1.setTextureOffset(32, 48).addBox(-8.0F, -14.0F, -4.0F, 16.0F, 16.0F, 0.0F, -2.0F, false);

		frontMushroom2 = new ModelRenderer(this);
		frontMushroom2.setRotationPoint(0.0F, 0.0F, -2.0F);
		frontMushroom1.addChild(frontMushroom2);
		setRotationAngle(frontMushroom2, 0.0F, -1.5708F, 0.0F);
		frontMushroom2.setTextureOffset(32, 48).addBox(-8.0F, -14.0F, -2.0F, 16.0F, 16.0F, 0.0F, -2.0F, false);

		backMushroom1 = new ModelRenderer(this);
		backMushroom1.setRotationPoint(2.5F, -4.0F, 4.0F);
		mushroom.addChild(backMushroom1);
		setRotationAngle(backMushroom1, 0.0F, -0.7854F, 0.0F);
		backMushroom1.setTextureOffset(0, 48).addBox(-8.0F, -14.0F, -2.0F, 16.0F, 16.0F, 0.0F, -2.0F, false);

		backMushroom2 = new ModelRenderer(this);
		backMushroom2.setRotationPoint(0.0F, 0.0F, 0.0F);
		backMushroom1.addChild(backMushroom2);
		setRotationAngle(backMushroom2, 0.0F, -1.5708F, 0.0F);
		backMushroom2.setTextureOffset(0, 48).addBox(-8.0F, -14.0F, -2.0F, 16.0F, 16.0F, 0.0F, -2.0F, false);

		crop = new ModelRenderer(this);
		crop.setRotationPoint(0.0F, 0.0F, 0.0F);
		body.addChild(crop);
		

		crop2 = new ModelRenderer(this);
		crop2.setRotationPoint(-1.6F, -4.0F, 1.5F);
		crop.addChild(crop2);
		

		crop3 = new ModelRenderer(this);
		crop3.setRotationPoint(0.0F, -9.0F, -2.0F);
		crop2.addChild(crop3);
		setRotationAngle(crop3, 0.0F, -1.5708F, 0.0F);
		crop3.setTextureOffset(0, 46).addBox(-8.3F, -3.9F, -7.9F, 18.0F, 18.0F, 0.0F, -4.0F, true);
		crop3.setTextureOffset(0, 46).addBox(-8.3F, -3.9F, -3.4F, 18.0F, 18.0F, 0.0F, -4.0F, true);

		crop3_r1 = new ModelRenderer(this);
		crop3_r1.setRotationPoint(2.5F, 5.1F, -1.625F);
		crop3.addChild(crop3_r1);
		setRotationAngle(crop3_r1, 0.0F, -1.5708F, 0.0F);
		crop3_r1.setTextureOffset(0, 46).addBox(-9.0F, -9.0F, 0.0F, 18.0F, 18.0F, 0.0F, -4.0F, true);

		crop3_r2 = new ModelRenderer(this);
		crop3_r2.setRotationPoint(6.85F, 5.1F, -1.625F);
		crop3.addChild(crop3_r2);
		setRotationAngle(crop3_r2, 0.0F, -1.5708F, 0.0F);
		crop3_r2.setTextureOffset(0, 46).addBox(-9.0F, -9.0F, 0.0F, 18.0F, 18.0F, 0.0F, -4.0F, true);

		armored = new ModelRenderer(this);
		armored.setRotationPoint(0.0F, 0.0F, 0.0F);
		body.addChild(armored);
		armored.setTextureOffset(34, 3).addBox(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 7.0F, 0.75F, false);
		armored.setTextureOffset(0, 25).addBox(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 10.0F, 0.25F, false);

		guardian = new ModelRenderer(this);
		guardian.setRotationPoint(0.0F, 5.0F, 0.0F);
		body.addChild(guardian);
		

		cube_r1 = new ModelRenderer(this);
		cube_r1.setRotationPoint(3.5F, -5.5F, 5.0F);
		guardian.addChild(cube_r1);
		setRotationAngle(cube_r1, 0.0F, 2.3562F, 0.0F);
		cube_r1.setTextureOffset(40, 24).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.25F, false);

		cube_r2 = new ModelRenderer(this);
		cube_r2.setRotationPoint(-3.5F, -5.5F, -5.0F);
		guardian.addChild(cube_r2);
		setRotationAngle(cube_r2, 0.0F, -0.7854F, 0.0F);
		cube_r2.setTextureOffset(40, 22).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.25F, false);

		cube_r3 = new ModelRenderer(this);
		cube_r3.setRotationPoint(3.5F, -5.5F, -4.75F);
		guardian.addChild(cube_r3);
		setRotationAngle(cube_r3, 0.0F, -2.3562F, 0.0F);
		cube_r3.setTextureOffset(40, 20).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.25F, false);

		cube_r4 = new ModelRenderer(this);
		cube_r4.setRotationPoint(-3.5F, -5.5F, 5.0F);
		guardian.addChild(cube_r4);
		setRotationAngle(cube_r4, 0.0F, 0.7854F, 0.0F);
		cube_r4.setTextureOffset(40, 18).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.25F, false);

		cube_r5 = new ModelRenderer(this);
		cube_r5.setRotationPoint(-3.5F, -2.0F, 0.0F);
		guardian.addChild(cube_r5);
		setRotationAngle(cube_r5, 0.0F, 0.0F, -0.7854F);
		cube_r5.setTextureOffset(32, 24).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.25F, false);

		cube_r6 = new ModelRenderer(this);
		cube_r6.setRotationPoint(0.0F, -2.0F, 5.0F);
		guardian.addChild(cube_r6);
		setRotationAngle(cube_r6, 1.5708F, 2.3562F, 1.5708F);
		cube_r6.setTextureOffset(32, 22).addBox(-2.5F, -0.75F, -0.5F, 3.0F, 1.0F, 1.0F, 0.25F, false);

		cube_r7 = new ModelRenderer(this);
		cube_r7.setRotationPoint(0.0F, -2.0F, -5.0F);
		guardian.addChild(cube_r7);
		setRotationAngle(cube_r7, -1.5708F, -2.3562F, 1.5708F);
		cube_r7.setTextureOffset(32, 20).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.25F, false);

		cube_r8 = new ModelRenderer(this);
		cube_r8.setRotationPoint(3.5F, -2.0F, 0.0F);
		guardian.addChild(cube_r8);
		setRotationAngle(cube_r8, 0.0F, 3.1416F, 0.7854F);
		cube_r8.setTextureOffset(32, 18).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.25F, false);

		cube_r9 = new ModelRenderer(this);
		cube_r9.setRotationPoint(0.0F, -9.0F, 5.0F);
		guardian.addChild(cube_r9);
		setRotationAngle(cube_r9, 1.5708F, 0.7854F, 1.5708F);
		cube_r9.setTextureOffset(24, 24).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.25F, false);

		cube_r10 = new ModelRenderer(this);
		cube_r10.setRotationPoint(0.0F, -9.0F, -5.0F);
		guardian.addChild(cube_r10);
		setRotationAngle(cube_r10, -1.5708F, -0.7854F, 1.5708F);
		cube_r10.setTextureOffset(24, 22).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.25F, false);

		cube_r11 = new ModelRenderer(this);
		cube_r11.setRotationPoint(3.5F, -9.0F, 0.0F);
		guardian.addChild(cube_r11);
		setRotationAngle(cube_r11, 3.1416F, 0.0F, 2.3562F);
		cube_r11.setTextureOffset(24, 20).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.25F, false);

		cube_r12 = new ModelRenderer(this);
		cube_r12.setRotationPoint(-3.5F, -9.0F, 0.0F);
		guardian.addChild(cube_r12);
		setRotationAngle(cube_r12, 0.0F, 0.0F, 0.7854F);
		cube_r12.setTextureOffset(24, 18).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.25F, false);

		yeti_horns = new ModelRenderer(this);
		yeti_horns.setRotationPoint(0.0F, 0.0F, 0.0F);
		body.addChild(yeti_horns);
		

		cube_r13 = new ModelRenderer(this);
		cube_r13.setRotationPoint(-2.0F, -4.0F, -4.0F);
		yeti_horns.addChild(cube_r13);
		setRotationAngle(cube_r13, 0.0F, 0.0F, -0.9599F);
		cube_r13.setTextureOffset(34, 12).addBox(-2.0F, -3.0F, -0.5F, 2.0F, 3.0F, 2.0F, 0.0F, true);

		cube_r14 = new ModelRenderer(this);
		cube_r14.setRotationPoint(2.0F, -4.0F, -4.0F);
		yeti_horns.addChild(cube_r14);
		setRotationAngle(cube_r14, 0.0F, 0.0F, 0.9599F);
		cube_r14.setTextureOffset(34, 12).addBox(0.0F, -3.0F, -0.5F, 2.0F, 3.0F, 2.0F, 0.0F, false);

		cloaked = new ModelRenderer(this);
		cloaked.setRotationPoint(0.0F, 5.0F, 0.0F);
		body.addChild(cloaked);
		cloaked.setTextureOffset(0, 25).addBox(-3.5F, -9.0F, -5.0F, 7.0F, 7.0F, 10.0F, 0.4F, false);
	}

	@Override
	public void setRotationAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		//previously the render function, render code was moved to a method below
	}

	@Override
	public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		body.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}