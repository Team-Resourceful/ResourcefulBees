package com.teamresourceful.resourcefulbees.centrifuge.client.screens;

import com.teamresourceful.resourcefulbees.centrifuge.client.components.TerminalToastWidget;
import com.teamresourceful.resourcefulbees.centrifuge.client.components.buttons.CloseButton;
import com.teamresourceful.resourcefulbees.centrifuge.client.components.buttons.HelpButton;
import com.teamresourceful.resourcefulbees.centrifuge.client.components.controlpanels.NavigableControlPanel;
import com.teamresourceful.resourcefulbees.centrifuge.client.components.infopanels.AbstractInfoPanel;
import com.teamresourceful.resourcefulbees.centrifuge.common.containers.CentrifugeContainer;
import com.teamresourceful.resourcefulbees.centrifuge.common.entities.base.AbstractCentrifugeOutputEntity;
import com.teamresourceful.resourcefulbees.centrifuge.common.entities.base.AbstractGUICentrifugeEntity;
import com.teamresourceful.resourcefulbees.centrifuge.common.helpers.CentrifugeTier;
import com.teamresourceful.resourcefulbees.centrifuge.common.network.client.PurgeContentsPacket;
import com.teamresourceful.resourcefulbees.centrifuge.common.network.client.SwitchGuiPacket;
import com.teamresourceful.resourcefulbees.centrifuge.common.network.client.VoidExcessPacket;
import com.teamresourceful.resourcefulbees.centrifuge.common.states.CentrifugeState;
import com.teamresourceful.resourcefulbees.client.util.TextUtils;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.CentrifugeTranslations;
import com.teamresourceful.resourcefulbees.common.lib.enums.ControlPanelTabs;
import com.teamresourceful.resourcefulbees.common.lib.enums.TerminalPanels;
import com.teamresourceful.resourcefulbees.common.networking.NetworkHandler;
import com.teamresourceful.resourcefulbees.common.util.WorldUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseCentrifugeScreen<T extends CentrifugeContainer<?>> extends AbstractContainerScreen<T> {

    protected final CentrifugeTier tier;
    protected CentrifugeState centrifugeState;
    protected @Nullable AbstractInfoPanel<?> infoPanel;
    protected @Nullable NavigableControlPanel<?> navPanel;
    protected ControlPanelTabs controlPanelTab = defaultControlPanelTab();
    protected ControlPanelTabs navPanelTab = defaultNavPanelTab();
    protected TerminalPanels currentInfoPanel = defaultInfoPanelTab();
    protected int selectionIndex = 0;
    protected TerminalToastWidget toastWidget;

    protected BaseCentrifugeScreen(T pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.tier = pMenu.getTier();
        this.imageWidth = 360;
        this.imageHeight = 228;
        this.centrifugeState = pMenu.getCentrifugeState();
    }

    protected abstract ControlPanelTabs defaultControlPanelTab();

    protected abstract ControlPanelTabs defaultNavPanelTab();

    protected abstract TerminalPanels defaultInfoPanelTab();

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(new CloseButton(leftPos+345, topPos+2, this::closeScreen));
        addRenderableOnly(new HelpButton(leftPos+331, topPos+2));
        this.toastWidget = addRenderableOnly(new TerminalToastWidget(leftPos+21, topPos+212));
    }

    public CentrifugeState centrifugeState() {
        return centrifugeState;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int pX, int pY) {
        TextUtils.tf12DrawCenteredStringNoShadow(graphics, this.title, 166, 5, TextUtils.FONT_COLOR_1);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float pPartialTicks, int pX, int pY) {
        this.renderBackground(graphics);
        if (minecraft == null) return;
        graphics.blit(CentrifugeTextures.BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, 360, 228);
        graphics.blit(CentrifugeTextures.COMPONENTS, leftPos+102, topPos+39, 19, 0, 237, 164);
    }

    private void closeScreen() {
        if (minecraft != null) minecraft.setScreen(null);
    }

    public void setToastText(Component toastText) {
        setToastText(toastText, 100);
    }

    //retaining just in case it's needed
    public void setToastText(Component toastText, int displayTime) {
        if (toastWidget != null) toastWidget.setToastText(toastText, displayTime);
    }

    /* SAVING THIS UNTIL IT IS NO LONGER NECESSARY
     * Screen Loads -> click on inputs -> info panel doesn't update
     * this is because the init() in TerminalInputNavPanel is only run once
     * when the nav buttons are added to the screen, thus clicking on the buttons
     * doesn't rerun the init in the nav panel. when the screen is resized however
     * the panel object is recreated and the init gets called. creating a state
     * system and implementing it means we can pass all logic through a singular
     * communication module and guarantee the sequence of events when a screen
     * is loaded, changed, or resized. having a state system with a screen state
     * object means the state object needs to be accessible by all classes such that
     * they can access default data in addition to user manipulated data.
     * this still needs to be finished. currently anytime the screen is resized everything will go back to default options.
     * these methods need to take into account a "light" state saving option utilizing the defaults at declaration for initial
     * screen setup.
     * also need to verify x/y coords for all panels now that everything is controlled from the screen class
     * in addition most of these methods can be moved directly to BaseCentrifugeScreen with abstract versions declared
     * where necessary */

    protected abstract void switchControlPanelTab(ControlPanelTabs controlPanelTab, boolean initialize);

    protected abstract void setNavPanelTab(boolean initialize);

    protected abstract void updateInfoPanel(@NotNull TerminalPanels newInfoPanel);

    public void setControlPanelTab(ControlPanelTabs controlPanelTab) {
        switchControlPanelTab(controlPanelTab, false);
    }

    protected void initializeControlPanelTab() {
        switchControlPanelTab(this.controlPanelTab, true);
    }

    protected void setNavPanelAndUpdate(@Nullable NavigableControlPanel<?> newNavPanel, boolean initialize) {
        removeNavPanelIfExists();
        navPanel = newNavPanel;
        updateNavPanelSelection(initialize);
        setNavPanelTab(initialize);
        if (newNavPanel != null) addRenderableWidget(newNavPanel);
    }

    private void updateNavPanelSelection(boolean initialize) {
        if (navPanel == null) {
            resetSelectionIndex();
            return;
        }
        if (!initialize) {
            resetSelectionIndex();
        }
        navPanel.initializeSelection();
    }

    private void resetSelectionIndex() {
        selectionIndex = 0;
    }

    protected void removeNavPanelIfExists() {
        if (navPanel != null) removeWidget(navPanel);
    }

    protected void setDefaultNavPanelTab(TerminalPanels newInfoPanel) {
        switchNavPanelTab(ControlPanelTabs.HOME, newInfoPanel);
    }

    public void switchNavPanelTab(ControlPanelTabs navPanelTab, TerminalPanels newInfoPanel) {
        this.navPanelTab = navPanelTab;
        this.currentInfoPanel = newInfoPanel;
        updateInfoPanel(newInfoPanel);
    }

    protected void updateInfoPanel(@NotNull AbstractInfoPanel<?> newInfoPanel) {
        removeInfoPanelIfExists();
        infoPanel = addRenderableWidget(newInfoPanel);
        setFocused(infoPanel);
        notifyInfoPanelOfEntitySelection();
    }

    protected void removeInfoPanelIfExists() {
        if (infoPanel != null) removeWidget(infoPanel);
        infoPanel = null;
        setFocused(null);
    }

    public void notifyInfoPanelOfEntitySelection() {
        if (infoPanel == null || navPanel == null) return;
        infoPanel.updateSelectedEntity(navPanel.selectedEntity());
    }

    public ControlPanelTabs controlPanelTab() {
        return this.controlPanelTab;
    }

    public ControlPanelTabs navPanelTab() {
        return this.navPanelTab;
    }

    public int selectionIndex() {
        return this.selectionIndex;
    }

    public void rotateSelection(int max, boolean reverse) {
        if (reverse) {
            selectionIndex = selectionIndex == 0 ? max : --selectionIndex;
        } else {
            selectionIndex = selectionIndex == max ? 0 : ++selectionIndex;
        }
    }
    //does not work on output screen
    public void voidExcess() {
        AbstractGUICentrifugeEntity selectedEntity = navPanel == null ? menu.getEntity() : navPanel.selectedEntity();
        if (selectedEntity instanceof AbstractCentrifugeOutputEntity<?, ?> outputEntity) {
            boolean voidsExcess = !outputEntity.voidsExcess();
            setToastText(voidsExcess ? CentrifugeTranslations.VOIDING_EXCESS : CentrifugeTranslations.NOT_VOIDING_EXCESS);
            NetworkHandler.CHANNEL.sendToServer(new VoidExcessPacket(selectedEntity.getBlockPos(), voidsExcess));
        }
    }

    @Contract("null, null -> null")
    public @Nullable <A extends AbstractGUICentrifugeEntity> A getBlockEntity(@Nullable BlockPos pos, Class<A> clazz) {
        if (minecraft == null || minecraft.level == null || pos == null) return null;
        return WorldUtils.getTileEntity(clazz, minecraft.level, pos);
    }
    //does not work on output screen
    public void purgeContents() {
        AbstractGUICentrifugeEntity selectedEntity = navPanel == null ? menu.getEntity() : navPanel.selectedEntity();
        if (selectedEntity instanceof AbstractCentrifugeOutputEntity<?,?>) {
            setToastText(CentrifugeTranslations.CONTENTS_PURGED);
            NetworkHandler.CHANNEL.sendToServer(new PurgeContentsPacket(selectedEntity.getBlockPos()));
        }
    }

    protected final Runnable switchGui(BlockPos newGuiPos) {
        return () -> NetworkHandler.CHANNEL.sendToServer(new SwitchGuiPacket(newGuiPos));
    }

    public @Nullable AbstractInfoPanel<?> getInfoPanel() {
        return infoPanel;
    }
}
