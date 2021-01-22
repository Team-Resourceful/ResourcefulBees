// Made with Blockbench 3.7.5
// Exported for Minecraft version 1.15
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
		mushroom.setTextureOffset(0, 25).addBox(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 10.0F, 0.4F, false);

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