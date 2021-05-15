package com.teamresourceful.resourcefulbees.compat.patchouli;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.teamresourceful.resourcefulbees.registry.BeeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.IVariable;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class ListBeesComponent implements ICustomComponent {

    final transient float defaultRotation = -45.0F;
    transient float renderScale;
    transient float offset;
    transient int page;
    transient int pageCount;
    transient Button nextPage;
    transient Button prevPage;
    transient int xOffset;
    transient int yOffset;

    final transient List<Pair<EntityType<?>, Optional<Entity>>> bees = new LinkedList<>();

    @Override
    public void build(int xOffset, int yOffset, int page) {
        BeeRegistry.getRegistry().getBees().forEach((s, b) -> bees.add(Pair.of(b.getEntityType(), Optional.empty())));
        pageCount = bees.size();
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    @Override
    public void render(@NotNull PoseStack matrixStack, @NotNull IComponentRenderContext context, float partialTicks, int mouseX, int mouseY) {
        prevPage.active = page != 0;
        nextPage.active = page != pageCount;
        renderEntity(matrixStack, context, page);
    }

    private void renderEntity(PoseStack matrixStack, IComponentRenderContext context, int page) {
        Pair<EntityType<?>, Optional<Entity>> bee = bees.get(page);
        Optional<Entity> entityOptional = bee.getRight();
        if (entityOptional.isPresent()) {
            renderEntity(matrixStack, entityOptional.get(), context.getGui().getMinecraft().level, xOffset, yOffset, this.defaultRotation, this.renderScale, this.offset);
        } else {
            Entity entity = initEntity(bee.getLeft(), context.getGui().getMinecraft().level);
            if (entity == null) {
                bees.remove(page);
                pageCount = bees.size();
            } else {
                bees.set(page, Pair.of(bee.getLeft(), Optional.of(entity)));
            }
        }
    }

    public static void renderEntity(PoseStack ms, Entity entity, Level world, float x, float y, float rotation, float renderScale, float offset) {
        entity.level = world;
        ms.pushPose();
        ms.translate(x, y, 50.0D);
        ms.scale(renderScale, renderScale, renderScale);
        ms.translate(0.0D, offset, 0.0D);
        ms.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
        ms.mulPose(Vector3f.YP.rotationDegrees(rotation));
        EntityRenderDispatcher erd = Minecraft.getInstance().getEntityRenderDispatcher();
        MultiBufferSource.BufferSource immediate = Minecraft.getInstance().renderBuffers().bufferSource();
        erd.setRenderShadow(false);
        erd.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, ms, immediate, 15728880);
        erd.setRenderShadow(true);
        immediate.endBatch();
        ms.popPose();
    }

    private Entity initEntity(EntityType<?> left, ClientLevel world) {
        return left.create(world);
    }

    @Override
    public void onDisplayed(IComponentRenderContext context) {
        this.prevPage = new Button(0, 50, 10, 10, new TextComponent("<"), b -> {
            page++;
            if (page > pageCount) page = pageCount;
        });
        this.nextPage = new Button(20, 50, 10, 10, new TextComponent(">"), b -> {
            page--;
            if (page < 0) page = 0;
        });
        context.registerButton(this.prevPage, 0, () -> {
        });
        context.registerButton(this.nextPage, 1, () -> {
        });
    }


    @Override
    public void onVariablesAvailable(@NotNull UnaryOperator<IVariable> unaryOperator) {
        //not used
    }
}
