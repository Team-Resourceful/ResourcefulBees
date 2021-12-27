package com.resourcefulbees.resourcefulbees.client.render.patreon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.resourcefulbees.resourcefulbees.lib.ModelTypes;
import net.minecraft.util.ResourceLocation;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

public class PetModelData extends PetData {

    public static <A> Codec<Set<A>> createLinkedSetCodec(Codec<A> codec) {
        return codec.listOf().xmap(LinkedHashSet::new, LinkedList::new);
    }

    public static final Codec<PetModelData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("id").orElse("error").forGetter(PetModelData::getId),
            ResourceLocation.CODEC.fieldOf("model").orElse(ModelTypes.DEFAULT.model).forGetter(PetModelData::getModelLocation),
            ResourceLocation.CODEC.fieldOf("texture").orElse(new ResourceLocation("textures/entity/bee/bee.png")).forGetter(PetModelData::getTexture),
            createLinkedSetCodec(LayerData.CODEC).fieldOf("layers").orElse(new LinkedHashSet<>()).forGetter(PetModelData::getLayers)
    ).apply(instance, PetModelData::new));

    private final String id;

    private final Set<LayerData> layers;

    public PetModelData(String id, ResourceLocation modelLocation, ResourceLocation texture, Set<LayerData> layers) {
        super(modelLocation, texture);
        this.id = id;
        this.layers = layers;
    }

    public String getId() {
        return id;
    }

    public Set<LayerData> getLayers() {
        return layers;
    }


}
