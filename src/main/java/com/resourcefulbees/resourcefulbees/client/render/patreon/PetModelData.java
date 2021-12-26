package com.resourcefulbees.resourcefulbees.client.render.patreon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.resourcefulbees.resourcefulbees.lib.ModelTypes;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

public class PetModelData implements IAnimatable {

    public static <A> Codec<Set<A>> createLinkedSetCodec(Codec<A> codec) {
        return codec.listOf().xmap(LinkedHashSet::new, LinkedList::new);
    }

    public static final Codec<PetModelData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("version").orElse(-1).forGetter(PetModelData::getVersion),
            Codec.STRING.fieldOf("id").orElse("error").forGetter(PetModelData::getId),
            ResourceLocation.CODEC.fieldOf("model").orElse(ModelTypes.DEFAULT.model).forGetter(PetModelData::getModelLocation),
            ResourceLocation.CODEC.fieldOf("texture").orElse(new ResourceLocation("textures/entity/bee/bee.png")).forGetter(PetModelData::getTexture),
            createLinkedSetCodec(LayerData.CODEC).fieldOf("layers").orElse(new LinkedHashSet<>()).forGetter(PetModelData::getLayers)
    ).apply(instance, PetModelData::new));

    static {
        //noinspection unchecked
        AnimationController.addModelFetcher((IAnimatable object) -> object instanceof PetModelData ? ((PetModelData) object).getModel() : null);
    }

    private final PetBeeModel<PetModelData> model = new PetBeeModel<>();
    private final AnimationFactory factory = new AnimationFactory(this);

    private final int version;
    private final String id;
    private final ResourceLocation modelLocation;
    private final ResourceLocation texture;
    private final Set<LayerData> layers;

    public PetModelData(int version, String id, ResourceLocation modelLocation, ResourceLocation texture, Set<LayerData> layers) {
        this.version = version;
        this.id = id;
        this.modelLocation = modelLocation;
        this.texture = texture;
        this.layers = layers;
    }

    public int getVersion() {
        return version;
    }

    public String getId() {
        return id;
    }

    @SuppressWarnings("rawtypes")
    public PetBeeModel getModel() {
        return model;
    }

    public ResourceLocation getModelLocation() {
        return modelLocation;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    public Set<LayerData> getLayers() {
        return layers;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "bee_controller", 0, event -> {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.bee.fly", true).addAnimation("animation.bee.fly.bobbing", true));
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
