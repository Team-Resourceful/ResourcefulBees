package com.teamresourceful.resourcefulbees.client.screen.beecon;

import com.mojang.blaze3d.platform.InputConstants;
import com.teamresourceful.resourcefulbees.client.component.TankWidget;
import com.teamresourceful.resourcefulbees.client.screen.base.CustomContainerScreen;
import com.teamresourceful.resourcefulbees.client.screen.base.CustomWidgetRenderers;
import com.teamresourceful.resourcefulbees.common.blockentities.EnderBeeconBlockEntity;
import com.teamresourceful.resourcefulbees.common.blocks.EnderBeeconBlock;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.BeeconTranslations;
import com.teamresourceful.resourcefulbees.common.lib.enums.BeeconPacketOption;
import com.teamresourceful.resourcefulbees.common.menus.EnderBeeconMenu;
import com.teamresourceful.resourcefulbees.common.networking.NetworkHandler;
import com.teamresourceful.resourcefulbees.common.networking.packets.client.BeeconEffectPacket;
import com.teamresourceful.resourcefulbees.common.networking.packets.client.BeeconSettingPacket;
import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.components.base.renderer.WidgetRenderer;
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers;
import earth.terrarium.olympus.client.layouts.Layouts;
import earth.terrarium.olympus.client.utils.ListenableState;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.TriState;
import net.minecraft.world.entity.player.Inventory;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;

public class EnderBeeconScreen extends CustomContainerScreen<EnderBeeconMenu, EnderBeeconBlockEntity> {

    private final ListenableState<Integer> range;

    public EnderBeeconScreen(EnderBeeconMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 180, 200);

        this.background = ModIdentifier.of("textures/gui/containers/beecon.png");
        this.titleLabelX = 7;
        this.inventoryLabelX = 7;
        this.inventoryLabelY = 103;

        this.range = ListenableState.of(menu.getEntity().getRange());
        this.range.registerListener(value ->
                NetworkHandler.NETWORK.sendToServer(new BeeconSettingPacket(BeeconPacketOption.RANGE, value, EnderBeeconScreen.this.menu.getEntity().getBlockPos()))
        );
    }

    @Override
    protected void init() {
        super.init();

        this.addRenderableWidget(TankWidget.single(leftPos + 20, topPos + 27, 12, 54, this.getEntity()::tankData));

        Layouts.row()
            .withPosition(this.leftPos + 76, this.topPos + 21)
            .withGap(1)
            .withChildren(Arrays.stream(Option.values())
                .map(option -> {
                    var state = ListenableState.of(option.isEnabled(this.getEntity()));
                    state.registerListener(selected -> option.set(this.getEntity(), selected));

                    return Widgets.button()
                            .withSize(16, 19)
                            .withCallback(InputConstants.MOUSE_BUTTON_LEFT, () -> state.set(!state.get()))
                            .withBackground(CustomWidgetRenderers.toggleable(state, option.sprites()));
                })
                .toArray(LayoutElement[]::new)
            )
            .build(this::addRenderableWidget);

        this.addRenderableWidget(Widgets
            .slider(this.range, 10, 40)
            .withBackground(WidgetRenderer.empty())
            .withBar(WidgetRenderer.empty())
            .withThumb(WidgetRenderers.sprite(BeeconScreenConstants.SLIDER).withPadding(0, 2))
            .withPosition(this.leftPos + 80, this.topPos + 67)
            .withSize(26, 7)
        );

        this.addRenderableWidget(Widgets.list(list -> {
            list.withPosition(this.leftPos + 43, this.topPos + 26);
            list.withScrollableY(TriState.TRUE);
            list.withSize(33, 56);
            list.withScrollbarWidth(5);
            list.withScrollbarMargin(2);
            list.withContentMargin(1);
            list.withScrollbarYRenderer((graphics, context, _) -> {
                var widget = context.getWidget();

                int scrollHeight = (int) (context.getHeight() * (context.getHeight() / (float) widget.getContentHeight())) + (widget.getViewHeight() - context.getHeight());
                int scrollY = (int) ((widget.getYScroll() + widget.getOverscrollY()) / (float) widget.getContentHeight() * context.getHeight());

                graphics.blitSprite(
                        RenderPipelines.GUI_TEXTURED, BeeconScreenConstants.SCROLLBAR,
                        context.getX(), context.getY() + scrollY,
                        context.getWidth(), scrollHeight
                );
            });

            list.withContents(content -> {
                content.withGap(1);

                for (var effect : this.getEntity().availableEffects()) {
                    var state = ListenableState.of(this.getEntity().isEffectActive(effect));
                    var button = Widgets.button();
                    var holder = effect.getFirst();
                    var sprite = new WidgetSprites(Hud.getMobEffectSprite(holder));

                    state.registerListener(selected -> NetworkHandler.NETWORK.sendToServer(new BeeconEffectPacket(
                            selected ? BeeconPacketOption.EFFECT_ON : BeeconPacketOption.EFFECT_OFF,
                            effect,
                            this.getEntity().getBlockPos()
                    )));

                    button.withSize(22, 23);
                    button.withCallback(InputConstants.MOUSE_BUTTON_LEFT, () -> state.set(!state.get()));
                    button.withBackground(CustomWidgetRenderers.toggleable(state, BeeconScreenConstants.EFFECT_BUTTON));
                    button.withForeground(CustomWidgetRenderers.hoverable(
                            WidgetRenderers.sprite(sprite).withPadding(2, 2, 3, 2),
                            WidgetRenderers.sprite(sprite).withPadding(3, 2, 2, 2)
                    ));
                    button.withTooltip(Component.empty()
                            .append(holder.value().getDisplayName())
                            .append(CommonComponents.NEW_LINE)
                            .append(BeeconTranslations.DRAIN_LABEL)
                            .append(effect.getSecond().toString())
                            .append("mb/t")
                    );

                    content.withChild(button);
                }
            });
        }));
    }

    @Override
    protected void extractLabels(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);

        graphics.text(this.font, this.range.get().toString(), 96, 78, 0xFF995b35, false);
        graphics.text(this.font, this.range.get().toString(), 96, 77, 0xFF683e36, false);
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractBackground(graphics, mouseX, mouseY, partialTicks);

        var state = this.menu.getEntity().getBlockState();
        var target = this.menu.getEntity().target();

        graphics.pose().pushMatrix();
        graphics.pose().translate(this.leftPos, this.topPos);

        if (state.getValue(EnderBeeconBlock.SOUND)) graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BeeconScreenConstants.PREVIEW_SOUND, 139, 47, 17, 6);
        if (state.getValue(EnderBeeconBlock.BEAM)) graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BeeconScreenConstants.PREVIEW_BEAM, 143, 21, 9, 36);
        if (target == EnderBeeconBlockEntity.Target.BEE) graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BeeconScreenConstants.PREVIEW_BEE, 153, 63, 10, 14);
        if (target == EnderBeeconBlockEntity.Target.PLAYER) graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BeeconScreenConstants.PREVIEW_PLAYER, 132, 63, 10, 14);

        graphics.pose().popMatrix();
    }

    private enum Option {
        SOUND,
        BEAM,
        TARGET,
        ;

        public WidgetSprites sprites() {
            return switch (this) {
                case SOUND -> BeeconScreenConstants.TOGGLE_SOUND;
                case BEAM -> BeeconScreenConstants.TOGGLE_BEAM;
                case TARGET -> BeeconScreenConstants.TOGGLE_TARGET;
            };
        }

        public void set(EnderBeeconBlockEntity entity, boolean selected) {
            var option = switch (this) {
                case SOUND -> BeeconPacketOption.SOUND;
                case BEAM -> BeeconPacketOption.BEAM;
                case TARGET -> BeeconPacketOption.TARGET;
            };
            NetworkHandler.NETWORK.sendToServer(new BeeconSettingPacket(option, selected ? 1 : 0, entity.getBlockPos()));
        }

        public boolean isEnabled(EnderBeeconBlockEntity entity) {
            var state = entity.getBlockState();
            return switch (this) {
                case SOUND -> state.hasProperty(EnderBeeconBlock.SOUND) && state.getValue(EnderBeeconBlock.SOUND);
                case BEAM -> state.hasProperty(EnderBeeconBlock.BEAM) && state.getValue(EnderBeeconBlock.BEAM);
                case TARGET -> entity.target() == EnderBeeconBlockEntity.Target.BEE;
            };
        }
    }
}
