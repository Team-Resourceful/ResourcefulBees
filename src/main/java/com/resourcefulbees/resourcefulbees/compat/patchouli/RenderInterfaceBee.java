package com.resourcefulbees.resourcefulbees.compat.patchouli;

import com.google.gson.JsonElement;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.serialization.JsonOps;
import com.resourcefulbees.resourcefulbees.client.render.InterfaceBeeRenderer;
import com.resourcefulbees.resourcefulbees.client.render.patreon.LayerData;
import com.resourcefulbees.resourcefulbees.client.render.patreon.PetModelData;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.IVariable;

import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class RenderInterfaceBee implements ICustomComponent {

    IVariable geoPath;
    IVariable texturePath;
    IVariable layers;
    IVariable scale;

    private transient PetModelData bee;
    private transient float modelScale;
    private transient int xOffset;
    private transient int yOffset;


    private final InterfaceBeeRenderer renderer = new InterfaceBeeRenderer();
    @Override
    public void build(int x, int y, int page) {
        this.xOffset = x;
        this.yOffset = y;
    }

    @Override
    public void render(@NotNull MatrixStack matrix, @NotNull IComponentRenderContext context, float partialTicks, int mouseX, int mouseY) {
        renderer.render(bee, matrix, partialTicks, context.getTicksInBook(), xOffset, yOffset, modelScale, -135);
    }

    @Override
    public void onVariablesAvailable(UnaryOperator<IVariable> lookup) {
        geoPath = lookup.apply(geoPath);
        texturePath = lookup.apply(texturePath);
        layers = lookup.apply(layers);
        scale = lookup.apply(scale);

        String tex = texturePath.asString();
        String geo = geoPath.asString();
        JsonElement element = layers.unwrap();

        Set<LayerData> vars = element.isJsonArray() ? layers.asStream().map(v -> LayerData.CODEC.parse(JsonOps.INSTANCE, v.unwrap()).result()).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toSet()) : new LinkedHashSet<>();

        bee = new PetModelData(0,"UI", new ResourceLocation(geo), new ResourceLocation(tex), vars);

        modelScale = scale.asNumber().floatValue();
    }
}
