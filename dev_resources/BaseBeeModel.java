// Made with Blockbench 4.0.5
// Exported for Minecraft version 1.15 - 1.16 with Mojang mappings
// Paste this class into your mod and generate all required imports


public class BaseBeeModel extends EntityModel<Entity> {
	private final ModelRenderer body;
	private final ModelRenderer torso;
	private final ModelRenderer betterdefault;
	private final ModelRenderer leftAntenna2;
	private final ModelRenderer rightAntenna2;
	private final ModelRenderer rightWing2;
	private final ModelRenderer leftWing2;
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
	private final ModelRenderer witch;
	private final ModelRenderer hat1;
	private final ModelRenderer hat2;
	private final ModelRenderer hat3;
	private final ModelRenderer bunny;
	private final ModelRenderer kitten;
	private final ModelRenderer baseKitten;
	private final ModelRenderer frontLegs2;
	private final ModelRenderer middleLegs2;
	private final ModelRenderer backLegs2;
	private final ModelRenderer oldKitten;
	private final ModelRenderer frontLegs3;
	private final ModelRenderer middleLegs3;
	private final ModelRenderer backLegs3;
	private final ModelRenderer kitten2;

	public BaseBeeModel() {
		texWidth = 64;
		texHeight = 64;

		body = new ModelRenderer(this);
		body.setPos(0.0F, 19.0F, 0.0F);
		

		torso = new ModelRenderer(this);
		torso.setPos(0.0F, 0.0F, 0.0F);
		body.addChild(torso);
		torso.texOffs(0, 0).addBox(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 10.0F, 0.0F, false);

		betterdefault = new ModelRenderer(this);
		betterdefault.setPos(0.0F, 5.0F, 0.0F);
		torso.addChild(betterdefault);
		

		leftAntenna2 = new ModelRenderer(this);
		leftAntenna2.setPos(0.0F, 0.0F, 0.0F);
		betterdefault.addChild(leftAntenna2);
		leftAntenna2.texOffs(0, 0).addBox(1.5F, -9.0F, -8.0F, 1.0F, 2.0F, 3.0F, 0.0F, false);

		rightAntenna2 = new ModelRenderer(this);
		rightAntenna2.setPos(0.0F, 0.0F, 0.0F);
		betterdefault.addChild(rightAntenna2);
		rightAntenna2.texOffs(0, 5).addBox(-2.5F, -9.0F, -8.0F, 1.0F, 2.0F, 3.0F, 0.0F, false);

		rightWing2 = new ModelRenderer(this);
		rightWing2.setPos(-1.5F, -9.0F, -3.0F);
		betterdefault.addChild(rightWing2);
		rightWing2.texOffs(34, 30).addBox(-9.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, 0.0F, false);

		leftWing2 = new ModelRenderer(this);
		leftWing2.setPos(1.5F, -9.0F, -3.0F);
		betterdefault.addChild(leftWing2);
		leftWing2.texOffs(34, 36).addBox(0.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, 0.0F, true);

		stinger = new ModelRenderer(this);
		stinger.setPos(0.0F, 0.0F, 0.0F);
		torso.addChild(stinger);
		stinger.texOffs(24, 7).addBox(0.0F, -1.0F, 5.0F, 0.0F, 1.0F, 2.0F, 0.0F, false);

		leftAntenna = new ModelRenderer(this);
		leftAntenna.setPos(0.0F, -2.0F, -5.0F);
		torso.addChild(leftAntenna);
		leftAntenna.texOffs(2, 0).addBox(1.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F, false);

		rightAntenna = new ModelRenderer(this);
		rightAntenna.setPos(0.0F, -2.0F, -5.0F);
		torso.addChild(rightAntenna);
		rightAntenna.texOffs(2, 3).addBox(-2.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F, false);

		rightWing = new ModelRenderer(this);
		rightWing.setPos(-1.5F, -4.0F, -3.0F);
		body.addChild(rightWing);
		rightWing.texOffs(0, 18).addBox(-9.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, 0.0F, false);

		leftWing = new ModelRenderer(this);
		leftWing.setPos(1.5F, -4.0F, -3.0F);
		body.addChild(leftWing);
		leftWing.texOffs(0, 18).addBox(0.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, 0.0F, true);

		frontLegs = new ModelRenderer(this);
		frontLegs.setPos(1.5F, 3.0F, -2.0F);
		body.addChild(frontLegs);
		frontLegs.texOffs(28, 1).addBox(-3.0F, 0.0F, 0.0F, 3.0F, 2.0F, 0.0F, 0.0F, false);

		middleLegs = new ModelRenderer(this);
		middleLegs.setPos(1.5F, 3.0F, 0.0F);
		body.addChild(middleLegs);
		middleLegs.texOffs(27, 3).addBox(-4.0F, 0.0F, 0.0F, 5.0F, 2.0F, 0.0F, 0.0F, false);

		backLegs = new ModelRenderer(this);
		backLegs.setPos(1.5F, 3.0F, 2.0F);
		body.addChild(backLegs);
		backLegs.texOffs(27, 5).addBox(-4.0F, 0.0F, 0.0F, 5.0F, 2.0F, 0.0F, 0.0F, false);

		crystals = new ModelRenderer(this);
		crystals.setPos(-0.25F, 1.0F, -6.5F);
		body.addChild(crystals);
		setRotationAngle(crystals, 0.3927F, 0.0F, 0.0F);
		crystals.texOffs(48, 48).addBox(1.0F, -3.8582F, 5.7674F, 2.0F, 2.0F, 2.0F, 0.0F, false);
		crystals.texOffs(48, 52).addBox(-1.0F, -6.0F, 4.0F, 2.0F, 3.0F, 2.0F, 0.0F, false);

		bone = new ModelRenderer(this);
		bone.setPos(0.0F, 0.2242F, 3.1543F);
		crystals.addChild(bone);
		setRotationAngle(bone, -0.3927F, 0.0F, 0.0F);
		bone.texOffs(48, 57).addBox(-2.0F, -7.7242F, 1.8457F, 3.0F, 4.0F, 3.0F, 0.0F, true);

		bone2 = new ModelRenderer(this);
		bone2.setPos(-1.0F, -0.5412F, 1.45F);
		crystals.addChild(bone2);
		setRotationAngle(bone2, -0.3927F, 0.0F, 0.0F);
		bone2.texOffs(48, 48).addBox(-1.5F, -5.8588F, 2.6934F, 2.0F, 2.0F, 2.0F, 0.0F, false);
		bone2.texOffs(48, 52).addBox(1.0F, -6.8588F, 5.6934F, 2.0F, 3.0F, 2.0F, 0.0F, true);

		bone5 = new ModelRenderer(this);
		bone5.setPos(2.0F, 0.0F, 0.0F);
		bone2.addChild(bone5);
		setRotationAngle(bone5, 0.0F, 0.0F, -0.3927F);
		bone5.texOffs(48, 52).addBox(-1.5F, -6.6588F, 5.6934F, 2.0F, 3.0F, 2.0F, 0.0F, true);

		bone3 = new ModelRenderer(this);
		bone3.setPos(3.0F, -4.4588F, -3.3066F);
		bone2.addChild(bone3);
		setRotationAngle(bone3, 0.0F, 0.0F, 0.5236F);
		bone3.texOffs(56, 51).addBox(-0.7321F, -2.0F, 10.0F, 2.0F, 4.0F, 2.0F, 0.0F, false);

		bone4 = new ModelRenderer(this);
		bone4.setPos(-5.0981F, 0.634F, 0.0F);
		bone3.addChild(bone4);
		setRotationAngle(bone4, 0.0F, 0.0F, -1.3963F);
		bone4.texOffs(56, 51).addBox(-1.1252F, 1.9F, 11.0F, 2.0F, 4.0F, 2.0F, 0.0F, true);

		dragon = new ModelRenderer(this);
		dragon.setPos(0.0F, -4.0F, -4.0F);
		body.addChild(dragon);
		dragon.texOffs(0, 61).addBox(-0.5F, -1.0F, 0.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
		dragon.texOffs(6, 61).addBox(-0.5F, -1.0F, 3.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
		dragon.texOffs(12, 61).addBox(-0.5F, -1.0F, 6.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);

		horns = new ModelRenderer(this);
		horns.setPos(0.0F, 1.0F, -2.0F);
		dragon.addChild(horns);
		setRotationAngle(horns, -0.6109F, 0.0F, 0.0F);
		horns.texOffs(6, 55).addBox(1.75F, -6.0F, 1.5F, 1.0F, 4.0F, 2.0F, 0.0F, false);
		horns.texOffs(0, 55).addBox(-2.75F, -6.0F, 1.5F, 1.0F, 4.0F, 2.0F, 0.0F, false);

		gel = new ModelRenderer(this);
		gel.setPos(0.0F, 0.0F, 0.0F);
		body.addChild(gel);
		gel.texOffs(0, 25).addBox(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 10.0F, 0.7F, false);

		queen = new ModelRenderer(this);
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

		nose = new ModelRenderer(this);
		nose.setPos(0.0F, -21.0F, 0.0F);
		body.addChild(nose);
		nose.texOffs(26, 25).addBox(-1.0F, 21.0F, -6.5F, 2.0F, 4.0F, 2.0F, 0.0F, false);

		mushroom = new ModelRenderer(this);
		mushroom.setPos(0.0F, 0.0F, 0.0F);
		body.addChild(mushroom);
		

		frontMushroom1 = new ModelRenderer(this);
		frontMushroom1.setPos(-1.5F, -4.0F, 0.0F);
		mushroom.addChild(frontMushroom1);
		frontMushroom1.texOffs(32, 48).addBox(-8.0F, -14.0F, -4.0F, 16.0F, 16.0F, 0.0F, -2.0F, false);

		frontMushroom2 = new ModelRenderer(this);
		frontMushroom2.setPos(0.0F, 0.0F, -2.0F);
		frontMushroom1.addChild(frontMushroom2);
		setRotationAngle(frontMushroom2, 0.0F, -1.5708F, 0.0F);
		frontMushroom2.texOffs(32, 48).addBox(-8.0F, -14.0F, -2.0F, 16.0F, 16.0F, 0.0F, -2.0F, false);

		backMushroom1 = new ModelRenderer(this);
		backMushroom1.setPos(2.5F, -4.0F, 4.0F);
		mushroom.addChild(backMushroom1);
		setRotationAngle(backMushroom1, 0.0F, -0.7854F, 0.0F);
		backMushroom1.texOffs(0, 48).addBox(-8.0F, -14.0F, -2.0F, 16.0F, 16.0F, 0.0F, -2.0F, false);

		backMushroom2 = new ModelRenderer(this);
		backMushroom2.setPos(0.0F, 0.0F, 0.0F);
		backMushroom1.addChild(backMushroom2);
		setRotationAngle(backMushroom2, 0.0F, -1.5708F, 0.0F);
		backMushroom2.texOffs(0, 48).addBox(-8.0F, -14.0F, -2.0F, 16.0F, 16.0F, 0.0F, -2.0F, false);

		crop = new ModelRenderer(this);
		crop.setPos(0.0F, 0.0F, 0.0F);
		body.addChild(crop);
		

		crop2 = new ModelRenderer(this);
		crop2.setPos(-1.6F, -4.0F, 1.5F);
		crop.addChild(crop2);
		

		crop3 = new ModelRenderer(this);
		crop3.setPos(0.0F, -9.0F, -2.0F);
		crop2.addChild(crop3);
		setRotationAngle(crop3, 0.0F, -1.5708F, 0.0F);
		crop3.texOffs(0, 46).addBox(-8.3F, -3.9F, -7.9F, 18.0F, 18.0F, 0.0F, -4.0F, true);
		crop3.texOffs(0, 46).addBox(-8.3F, -3.9F, -3.4F, 18.0F, 18.0F, 0.0F, -4.0F, true);

		crop3_r1 = new ModelRenderer(this);
		crop3_r1.setPos(2.5F, 5.1F, -1.625F);
		crop3.addChild(crop3_r1);
		setRotationAngle(crop3_r1, 0.0F, -1.5708F, 0.0F);
		crop3_r1.texOffs(0, 46).addBox(-9.0F, -9.0F, 0.0F, 18.0F, 18.0F, 0.0F, -4.0F, true);

		crop3_r2 = new ModelRenderer(this);
		crop3_r2.setPos(6.85F, 5.1F, -1.625F);
		crop3.addChild(crop3_r2);
		setRotationAngle(crop3_r2, 0.0F, -1.5708F, 0.0F);
		crop3_r2.texOffs(0, 46).addBox(-9.0F, -9.0F, 0.0F, 18.0F, 18.0F, 0.0F, -4.0F, true);

		armored = new ModelRenderer(this);
		armored.setPos(0.0F, 0.0F, 0.0F);
		body.addChild(armored);
		armored.texOffs(34, 3).addBox(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 7.0F, 0.75F, false);
		armored.texOffs(0, 25).addBox(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 10.0F, 0.25F, false);

		guardian = new ModelRenderer(this);
		guardian.setPos(0.0F, 5.0F, 0.0F);
		body.addChild(guardian);
		

		cube_r1 = new ModelRenderer(this);
		cube_r1.setPos(3.5F, -5.5F, 5.0F);
		guardian.addChild(cube_r1);
		setRotationAngle(cube_r1, 0.0F, 2.3562F, 0.0F);
		cube_r1.texOffs(40, 24).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.25F, false);

		cube_r2 = new ModelRenderer(this);
		cube_r2.setPos(-3.5F, -5.5F, -5.0F);
		guardian.addChild(cube_r2);
		setRotationAngle(cube_r2, 0.0F, -0.7854F, 0.0F);
		cube_r2.texOffs(40, 22).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.25F, false);

		cube_r3 = new ModelRenderer(this);
		cube_r3.setPos(3.5F, -5.5F, -4.75F);
		guardian.addChild(cube_r3);
		setRotationAngle(cube_r3, 0.0F, -2.3562F, 0.0F);
		cube_r3.texOffs(40, 20).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.25F, false);

		cube_r4 = new ModelRenderer(this);
		cube_r4.setPos(-3.5F, -5.5F, 5.0F);
		guardian.addChild(cube_r4);
		setRotationAngle(cube_r4, 0.0F, 0.7854F, 0.0F);
		cube_r4.texOffs(40, 18).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.25F, false);

		cube_r5 = new ModelRenderer(this);
		cube_r5.setPos(-3.5F, -2.0F, 0.0F);
		guardian.addChild(cube_r5);
		setRotationAngle(cube_r5, 0.0F, 0.0F, -0.7854F);
		cube_r5.texOffs(32, 24).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.25F, false);

		cube_r6 = new ModelRenderer(this);
		cube_r6.setPos(0.0F, -2.0F, 5.0F);
		guardian.addChild(cube_r6);
		setRotationAngle(cube_r6, 1.5708F, 2.3562F, 1.5708F);
		cube_r6.texOffs(32, 22).addBox(-2.5F, -0.75F, -0.5F, 3.0F, 1.0F, 1.0F, 0.25F, false);

		cube_r7 = new ModelRenderer(this);
		cube_r7.setPos(0.0F, -2.0F, -5.0F);
		guardian.addChild(cube_r7);
		setRotationAngle(cube_r7, -1.5708F, -2.3562F, 1.5708F);
		cube_r7.texOffs(32, 20).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.25F, false);

		cube_r8 = new ModelRenderer(this);
		cube_r8.setPos(3.5F, -2.0F, 0.0F);
		guardian.addChild(cube_r8);
		setRotationAngle(cube_r8, 0.0F, 3.1416F, 0.7854F);
		cube_r8.texOffs(32, 18).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.25F, false);

		cube_r9 = new ModelRenderer(this);
		cube_r9.setPos(0.0F, -9.0F, 5.0F);
		guardian.addChild(cube_r9);
		setRotationAngle(cube_r9, 1.5708F, 0.7854F, 1.5708F);
		cube_r9.texOffs(24, 24).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.25F, false);

		cube_r10 = new ModelRenderer(this);
		cube_r10.setPos(0.0F, -9.0F, -5.0F);
		guardian.addChild(cube_r10);
		setRotationAngle(cube_r10, -1.5708F, -0.7854F, 1.5708F);
		cube_r10.texOffs(24, 22).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.25F, false);

		cube_r11 = new ModelRenderer(this);
		cube_r11.setPos(3.5F, -9.0F, 0.0F);
		guardian.addChild(cube_r11);
		setRotationAngle(cube_r11, 3.1416F, 0.0F, 2.3562F);
		cube_r11.texOffs(24, 20).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.25F, false);

		cube_r12 = new ModelRenderer(this);
		cube_r12.setPos(-3.5F, -9.0F, 0.0F);
		guardian.addChild(cube_r12);
		setRotationAngle(cube_r12, 0.0F, 0.0F, 0.7854F);
		cube_r12.texOffs(24, 18).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, 0.25F, false);

		yeti_horns = new ModelRenderer(this);
		yeti_horns.setPos(0.0F, 0.0F, 0.0F);
		body.addChild(yeti_horns);
		

		cube_r13 = new ModelRenderer(this);
		cube_r13.setPos(-2.0F, -4.0F, -4.0F);
		yeti_horns.addChild(cube_r13);
		setRotationAngle(cube_r13, 0.0F, 0.0F, -0.9599F);
		cube_r13.texOffs(34, 12).addBox(-2.0F, -3.0F, -0.5F, 2.0F, 3.0F, 2.0F, 0.0F, true);

		cube_r14 = new ModelRenderer(this);
		cube_r14.setPos(2.0F, -4.0F, -4.0F);
		yeti_horns.addChild(cube_r14);
		setRotationAngle(cube_r14, 0.0F, 0.0F, 0.9599F);
		cube_r14.texOffs(34, 12).addBox(0.0F, -3.0F, -0.5F, 2.0F, 3.0F, 2.0F, 0.0F, false);

		cloaked = new ModelRenderer(this);
		cloaked.setPos(0.0F, 5.0F, 0.0F);
		body.addChild(cloaked);
		cloaked.texOffs(0, 25).addBox(-3.5F, -9.0F, -5.0F, 7.0F, 7.0F, 10.0F, 0.4F, false);

		witch = new ModelRenderer(this);
		witch.setPos(0.0F, 0.0F, 0.0F);
		body.addChild(witch);
		witch.texOffs(0, 57).addBox(-3.0F, -5.0F, -4.5F, 6.0F, 1.0F, 6.0F, 0.0F, false);

		hat1 = new ModelRenderer(this);
		hat1.setPos(0.0F, 0.0F, 0.0F);
		witch.addChild(hat1);
		setRotationAngle(hat1, 0.0F, 0.0F, -0.3054F);
		hat1.texOffs(24, 55).addBox(1.8F, -9.6F, -2.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);

		hat2 = new ModelRenderer(this);
		hat2.setPos(0.0F, 0.0F, 0.0F);
		witch.addChild(hat2);
		setRotationAngle(hat2, 0.0F, 0.0F, -0.1745F);
		hat2.texOffs(16, 53).addBox(0.2F, -8.9F, -2.5F, 2.0F, 2.0F, 2.0F, 0.0F, false);

		hat3 = new ModelRenderer(this);
		hat3.setPos(0.0F, 0.0F, 0.0F);
		witch.addChild(hat3);
		setRotationAngle(hat3, 0.0F, 0.0F, -0.0698F);
		hat3.texOffs(0, 50).addBox(-1.6F, -7.3F, -3.5F, 4.0F, 3.0F, 4.0F, 0.0F, false);

		bunny = new ModelRenderer(this);
		bunny.setPos(0.0F, 5.0F, 0.0F);
		body.addChild(bunny);
		bunny.texOffs(0, 59).addBox(-2.75F, -13.0F, -4.0F, 2.0F, 4.0F, 1.0F, 0.0F, false);
		bunny.texOffs(6, 59).addBox(0.75F, -13.0F, -4.0F, 2.0F, 4.0F, 1.0F, 0.0F, false);

		kitten = new ModelRenderer(this);
		kitten.setPos(0.0F, 5.0F, 0.0F);
		body.addChild(kitten);
		kitten.texOffs(0, 61).addBox(1.5F, -10.0F, -4.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
		kitten.texOffs(12, 61).addBox(-1.5F, -4.0F, -6.0F, 3.0F, 2.0F, 1.0F, 0.0F, false);
		kitten.texOffs(6, 61).addBox(-2.5F, -10.0F, -4.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);

		baseKitten = new ModelRenderer(this);
		baseKitten.setPos(0.0F, 0.0F, 0.0F);
		body.addChild(baseKitten);
		

		frontLegs2 = new ModelRenderer(this);
		frontLegs2.setPos(1.5F, 3.0F, -2.0F);
		baseKitten.addChild(frontLegs2);
		frontLegs2.texOffs(24, 0).addBox(-3.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
		frontLegs2.texOffs(28, 0).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);

		middleLegs2 = new ModelRenderer(this);
		middleLegs2.setPos(1.5F, 3.0F, 0.0F);
		baseKitten.addChild(middleLegs2);
		middleLegs2.texOffs(24, 3).addBox(-4.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
		middleLegs2.texOffs(28, 3).addBox(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);

		backLegs2 = new ModelRenderer(this);
		backLegs2.setPos(1.5F, 3.0F, 2.0F);
		baseKitten.addChild(backLegs2);
		backLegs2.texOffs(24, 6).addBox(-4.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
		backLegs2.texOffs(28, 6).addBox(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);

		oldKitten = new ModelRenderer(this);
		oldKitten.setPos(0.0F, 0.0F, 0.0F);
		body.addChild(oldKitten);
		

		frontLegs3 = new ModelRenderer(this);
		frontLegs3.setPos(1.5F, 3.0F, -2.0F);
		oldKitten.addChild(frontLegs3);
		frontLegs3.texOffs(24, 0).addBox(-3.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
		frontLegs3.texOffs(24, 0).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);

		middleLegs3 = new ModelRenderer(this);
		middleLegs3.setPos(1.5F, 3.0F, 0.0F);
		oldKitten.addChild(middleLegs3);
		middleLegs3.texOffs(24, 0).addBox(-4.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
		middleLegs3.texOffs(24, 0).addBox(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);

		backLegs3 = new ModelRenderer(this);
		backLegs3.setPos(1.5F, 3.0F, 2.0F);
		oldKitten.addChild(backLegs3);
		backLegs3.texOffs(24, 0).addBox(-4.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
		backLegs3.texOffs(24, 0).addBox(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);

		kitten2 = new ModelRenderer(this);
		kitten2.setPos(0.0F, 5.0F, 0.0F);
		oldKitten.addChild(kitten2);
		kitten2.texOffs(24, 3).addBox(1.5F, -10.0F, -4.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
		kitten2.texOffs(24, 6).addBox(-1.5F, -4.0F, -6.0F, 3.0F, 2.0F, 1.0F, 0.0F, false);
		kitten2.texOffs(24, 3).addBox(-2.5F, -10.0F, -4.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		//previously the render function, render code was moved to a method below
	}

	@Override
	public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		body.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
}