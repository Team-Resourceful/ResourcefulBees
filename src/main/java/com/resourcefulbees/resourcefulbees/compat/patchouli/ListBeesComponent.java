package com.resourcefulbees.resourcefulbees.compat.patchouli;


import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.IVariable;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class ListBeesComponent implements ICustomComponent {

    transient float defaultRotation = -45.0F;
    transient float renderScale;
    transient float offset;
    transient int page;
    transient int pageCount;
    transient Button nextPage;
    transient Button prevPage;
    transient int xOffset;
    transient int yOffset;

    transient List<Pair<EntityType<?>, Optional<Entity>>> bees = new LinkedList<>();

    @Override
    public void build(int xOffset, int yOffset, int page) {
        BeeRegistry.getRegistry().getBees().forEach((s, b) -> bees.add(Pair.of(ForgeRegistries.ENTITIES.getValue(b.getEntityTypeRegistryID()), Optional.empty())));
        pageCount = bees.size();
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    @Override
    public void render(MatrixStack matrixStack, IComponentRenderContext context, float pticks, int mouseX, int mouseY) {
        prevPage.active = page != 0;
        nextPage.active = page != pageCount;
        renderEntity(matrixStack, context, page);
    }

    private void renderEntity(MatrixStack matrixStack, IComponentRenderContext context, int page) {
        Pair<EntityType<?>, Optional<Entity>> bee = bees.get(page);
        if (bee.getRight().isPresent()) {
            renderEntity(matrixStack, bee.getValue().get(), context.getGui().getMinecraft().world, xOffset, yOffset, this.defaultRotation, this.renderScale, this.offset);
        } else {
            Entity entity = initEntity(bee.getLeft(), context.getGui().getMinecraft().world);
            if (entity == null) {
                bees.remove(page);
                pageCount = bees.size();
            } else {
                bees.set(page, Pair.of(bee.getLeft(), Optional.of(entity)));
            }
        }
    }

    public static void renderEntity(MatrixStack ms, Entity entity, World world, float x, float y, float rotation, float renderScale, float offset) {
        entity.world = world;
        ms.push();
        ms.translate(x, y, 50.0D);
        ms.scale(renderScale, renderScale, renderScale);
        ms.translate(0.0D, offset, 0.0D);
        ms.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0F));
        ms.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(rotation));
        EntityRendererManager erd = Minecraft.getInstance().getRenderManager();
        IRenderTypeBuffer.Impl immediate = Minecraft.getInstance().getBufferBuilders().getEntityVertexConsumers();
        erd.setRenderShadow(false);
        erd.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, ms, immediate, 15728880);
        erd.setRenderShadow(true);
        immediate.draw();
        ms.pop();
    }

    private Entity initEntity(EntityType<?> left, ClientWorld world) {
        return left.create(world);
    }

    @Override
    public void onDisplayed(IComponentRenderContext context) {
        this.prevPage = new Button(0, 50, 10, 10, new StringTextComponent("<"), b -> {
            page++;
            if (page > pageCount) page = pageCount;
        });
        this.nextPage = new Button(20, 50, 10, 10, new StringTextComponent(">"), b -> {
            page--;
            if (page < 0) page = 0;
        });
        context.registerButton(this.prevPage, 0, () -> {
        });
        context.registerButton(this.nextPage, 1, () -> {
        });
    }


    @Override
    public void onVariablesAvailable(UnaryOperator<IVariable> unaryOperator) {

    }
}
