// Made with Blockbench 4.0.5
// Exported for Minecraft version 1.17 with Mojang mappings
// Paste this class into your mod and generate all required imports


public class BaseBeeModel<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "basebeemodel"), "main");
	private final ModelPart body;

	public BaseBeeModel(ModelPart root) {
		this.body = root.getChild("body");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 19.0F, 0.0F));

		PartDefinition torso = body.addOrReplaceChild("torso", CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition stinger = torso.addOrReplaceChild("stinger", CubeListBuilder.create().texOffs(24, 7).addBox(0.0F, -1.0F, 5.0F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition leftAntenna = torso.addOrReplaceChild("leftAntenna", CubeListBuilder.create().texOffs(56, 0).addBox(1.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(2, 0).addBox(1.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, -5.0F));

		PartDefinition rightAntenna = torso.addOrReplaceChild("rightAntenna", CubeListBuilder.create().texOffs(56, 5).addBox(-2.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(2, 3).addBox(-2.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, -5.0F));

		PartDefinition rightWing = body.addOrReplaceChild("rightWing", CubeListBuilder.create().texOffs(34, 26).addBox(-9.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(0, 18).addBox(-9.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, -4.0F, -3.0F));

		PartDefinition leftWing = body.addOrReplaceChild("leftWing", CubeListBuilder.create().texOffs(34, 32).mirror().addBox(0.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false)
		.texOffs(0, 18).mirror().addBox(0.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(1.5F, -4.0F, -3.0F));

		PartDefinition frontLegs = body.addOrReplaceChild("frontLegs", CubeListBuilder.create().texOffs(28, 1).addBox(-3.0F, 0.0F, 0.0F, 3.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(60, 17).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(56, 17).addBox(-3.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, 3.0F, -2.0F));

		PartDefinition middleLegs = body.addOrReplaceChild("middleLegs", CubeListBuilder.create().texOffs(27, 3).addBox(-4.0F, 0.0F, 0.0F, 5.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(60, 20).addBox(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(56, 20).addBox(-4.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, 3.0F, 0.0F));

		PartDefinition backLegs = body.addOrReplaceChild("backLegs", CubeListBuilder.create().texOffs(27, 5).addBox(-4.0F, 0.0F, 0.0F, 5.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(56, 23).addBox(-4.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(60, 23).addBox(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, 3.0F, 2.0F));

		PartDefinition crystals = body.addOrReplaceChild("crystals", CubeListBuilder.create().texOffs(48, 48).addBox(1.0F, -3.8582F, 5.7674F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(48, 52).addBox(-1.0F, -6.0F, 4.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.25F, 1.0F, -6.5F, 0.3927F, 0.0F, 0.0F));

		PartDefinition bone = crystals.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(48, 57).mirror().addBox(-2.0F, -7.7242F, 1.8457F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.2242F, 3.1543F, -0.3927F, 0.0F, 0.0F));

		PartDefinition bone2 = crystals.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(48, 48).addBox(-1.5F, -5.8588F, 2.6934F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(48, 52).mirror().addBox(1.0F, -6.8588F, 5.6934F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-1.0F, -0.5412F, 1.45F, -0.3927F, 0.0F, 0.0F));

		PartDefinition bone5 = bone2.addOrReplaceChild("bone5", CubeListBuilder.create().texOffs(48, 52).mirror().addBox(-1.5F, -6.6588F, 5.6934F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(2.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.3927F));

		PartDefinition bone3 = bone2.addOrReplaceChild("bone3", CubeListBuilder.create().texOffs(56, 51).addBox(-0.7321F, -2.0F, 10.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, -4.4588F, -3.3066F, 0.0F, 0.0F, 0.5236F));

		PartDefinition bone4 = bone3.addOrReplaceChild("bone4", CubeListBuilder.create().texOffs(56, 51).mirror().addBox(-1.1252F, 1.9F, 11.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-5.0981F, 0.634F, 0.0F, 0.0F, 0.0F, -1.3963F));

		PartDefinition dragon = body.addOrReplaceChild("dragon", CubeListBuilder.create().texOffs(0, 61).addBox(-0.5F, -1.0F, 0.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(6, 61).addBox(-0.5F, -1.0F, 3.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(12, 61).addBox(-0.5F, -1.0F, 6.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.0F, -4.0F));

		PartDefinition horns = dragon.addOrReplaceChild("horns", CubeListBuilder.create().texOffs(6, 55).addBox(1.75F, -6.0F, 1.5F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 55).addBox(-2.75F, -6.0F, 1.5F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, -2.0F, -0.6109F, 0.0F, 0.0F));

		PartDefinition gel = body.addOrReplaceChild("gel", CubeListBuilder.create().texOffs(0, 25).addBox(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 10.0F, new CubeDeformation(0.7F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition queen = body.addOrReplaceChild("queen", CubeListBuilder.create().texOffs(0, 42).addBox(-2.5F, -5.0F, -4.0F, 5.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(0, 45).addBox(-2.5F, -6.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 45).addBox(1.5F, -6.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 45).addBox(1.5F, -6.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 45).addBox(-0.5F, -6.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 45).addBox(-2.5F, -6.0F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 45).addBox(1.5F, -6.0F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 45).addBox(-2.5F, -6.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 42).addBox(-0.5F, -6.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition nose = body.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(26, 25).addBox(-1.0F, 21.0F, -6.5F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -21.0F, 0.0F));

		PartDefinition mushroom = body.addOrReplaceChild("mushroom", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition frontMushroom1 = mushroom.addOrReplaceChild("frontMushroom1", CubeListBuilder.create().texOffs(32, 48).addBox(-8.0F, -14.0F, -4.0F, 16.0F, 16.0F, 0.0F, new CubeDeformation(-2.0F)), PartPose.offset(-1.5F, -4.0F, 0.0F));

		PartDefinition frontMushroom2 = frontMushroom1.addOrReplaceChild("frontMushroom2", CubeListBuilder.create().texOffs(32, 48).addBox(-8.0F, -14.0F, -2.0F, 16.0F, 16.0F, 0.0F, new CubeDeformation(-2.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -2.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition backMushroom1 = mushroom.addOrReplaceChild("backMushroom1", CubeListBuilder.create().texOffs(0, 48).addBox(-8.0F, -14.0F, -2.0F, 16.0F, 16.0F, 0.0F, new CubeDeformation(-2.0F)), PartPose.offsetAndRotation(2.5F, -4.0F, 4.0F, 0.0F, -0.7854F, 0.0F));

		PartDefinition backMushroom2 = backMushroom1.addOrReplaceChild("backMushroom2", CubeListBuilder.create().texOffs(0, 48).addBox(-8.0F, -14.0F, -2.0F, 16.0F, 16.0F, 0.0F, new CubeDeformation(-2.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition crop = body.addOrReplaceChild("crop", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition crop2 = crop.addOrReplaceChild("crop2", CubeListBuilder.create(), PartPose.offset(-1.6F, -4.0F, 1.5F));

		PartDefinition crop3 = crop2.addOrReplaceChild("crop3", CubeListBuilder.create().texOffs(0, 46).mirror().addBox(-8.3F, -3.9F, -7.9F, 18.0F, 18.0F, 0.0F, new CubeDeformation(-4.0F)).mirror(false)
		.texOffs(0, 46).mirror().addBox(-8.3F, -3.9F, -3.4F, 18.0F, 18.0F, 0.0F, new CubeDeformation(-4.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -9.0F, -2.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition crop3_r1 = crop3.addOrReplaceChild("crop3_r1", CubeListBuilder.create().texOffs(0, 46).mirror().addBox(-9.0F, -9.0F, 0.0F, 18.0F, 18.0F, 0.0F, new CubeDeformation(-4.0F)).mirror(false), PartPose.offsetAndRotation(2.5F, 5.1F, -1.625F, 0.0F, -1.5708F, 0.0F));

		PartDefinition crop3_r2 = crop3.addOrReplaceChild("crop3_r2", CubeListBuilder.create().texOffs(0, 46).mirror().addBox(-9.0F, -9.0F, 0.0F, 18.0F, 18.0F, 0.0F, new CubeDeformation(-4.0F)).mirror(false), PartPose.offsetAndRotation(6.85F, 5.1F, -1.625F, 0.0F, -1.5708F, 0.0F));

		PartDefinition armored = body.addOrReplaceChild("armored", CubeListBuilder.create().texOffs(34, 3).addBox(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 7.0F, new CubeDeformation(0.75F))
		.texOffs(0, 25).addBox(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 10.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition guardian = body.addOrReplaceChild("guardian", CubeListBuilder.create(), PartPose.offset(0.0F, 5.0F, 0.0F));

		PartDefinition cube_r1 = guardian.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(40, 24).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(3.5F, -5.5F, 5.0F, 0.0F, 2.3562F, 0.0F));

		PartDefinition cube_r2 = guardian.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(40, 22).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(-3.5F, -5.5F, -5.0F, 0.0F, -0.7854F, 0.0F));

		PartDefinition cube_r3 = guardian.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(40, 20).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(3.5F, -5.5F, -4.75F, 0.0F, -2.3562F, 0.0F));

		PartDefinition cube_r4 = guardian.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(40, 18).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(-3.5F, -5.5F, 5.0F, 0.0F, 0.7854F, 0.0F));

		PartDefinition cube_r5 = guardian.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(32, 24).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(-3.5F, -2.0F, 0.0F, 0.0F, 0.0F, -0.7854F));

		PartDefinition cube_r6 = guardian.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(32, 22).addBox(-2.5F, -0.75F, -0.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.0F, -2.0F, 5.0F, 1.5708F, 2.3562F, 1.5708F));

		PartDefinition cube_r7 = guardian.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(32, 20).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.0F, -2.0F, -5.0F, -1.5708F, -2.3562F, 1.5708F));

		PartDefinition cube_r8 = guardian.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(32, 18).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(3.5F, -2.0F, 0.0F, 0.0F, 3.1416F, 0.7854F));

		PartDefinition cube_r9 = guardian.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(24, 24).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.0F, -9.0F, 5.0F, 1.5708F, 0.7854F, 1.5708F));

		PartDefinition cube_r10 = guardian.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(24, 22).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.0F, -9.0F, -5.0F, -1.5708F, -0.7854F, 1.5708F));

		PartDefinition cube_r11 = guardian.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(24, 20).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(3.5F, -9.0F, 0.0F, 3.1416F, 0.0F, 2.3562F));

		PartDefinition cube_r12 = guardian.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(24, 18).addBox(-2.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(-3.5F, -9.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition yeti_horns = body.addOrReplaceChild("yeti_horns", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r13 = yeti_horns.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(34, 12).mirror().addBox(-2.0F, -3.0F, -0.5F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-2.0F, -4.0F, -4.0F, 0.0F, 0.0F, -0.9599F));

		PartDefinition cube_r14 = yeti_horns.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(34, 12).addBox(0.0F, -3.0F, -0.5F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -4.0F, -4.0F, 0.0F, 0.0F, 0.9599F));

		PartDefinition cloaked = body.addOrReplaceChild("cloaked", CubeListBuilder.create().texOffs(0, 25).addBox(-3.5F, -9.0F, -5.0F, 7.0F, 7.0F, 10.0F, new CubeDeformation(0.4F)), PartPose.offset(0.0F, 5.0F, 0.0F));

		PartDefinition witch = body.addOrReplaceChild("witch", CubeListBuilder.create().texOffs(0, 57).addBox(-3.0F, -5.0F, -4.5F, 6.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition hat1 = witch.addOrReplaceChild("hat1", CubeListBuilder.create().texOffs(24, 55).addBox(1.8F, -9.6F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.3054F));

		PartDefinition hat2 = witch.addOrReplaceChild("hat2", CubeListBuilder.create().texOffs(16, 53).addBox(0.2F, -8.9F, -2.5F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1745F));

		PartDefinition hat3 = witch.addOrReplaceChild("hat3", CubeListBuilder.create().texOffs(0, 50).addBox(-1.6F, -7.3F, -3.5F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.0698F));

		PartDefinition bunny = body.addOrReplaceChild("bunny", CubeListBuilder.create().texOffs(0, 59).addBox(-2.75F, -13.0F, -4.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(6, 59).addBox(0.75F, -13.0F, -4.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.0F, 0.0F));

		PartDefinition kitten = body.addOrReplaceChild("kitten", CubeListBuilder.create().texOffs(0, 61).addBox(1.5F, -10.0F, -4.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(12, 61).addBox(-1.5F, -4.0F, -6.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(6, 61).addBox(-2.5F, -10.0F, -4.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		body.render(poseStack, buffer, packedLight, packedOverlay);
	}
}