package com.teamresourceful.resourcefulbees.client.screens.centrifuge;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.client.components.centrifuge.TerminalToastWidget;
import com.teamresourceful.resourcefulbees.client.components.centrifuge.buttons.CloseButton;
import com.teamresourceful.resourcefulbees.client.components.centrifuge.buttons.HelpButton;
import com.teamresourceful.resourcefulbees.client.components.centrifuge.controlpanels.NavigableControlPanel;
import com.teamresourceful.resourcefulbees.client.components.centrifuge.infopanels.AbstractInfoPanel;
import com.teamresourceful.resourcefulbees.client.utils.ClientUtils;
import com.teamresourceful.resourcefulbees.client.utils.TextUtils;
import com.teamresourceful.resourcefulbees.common.lib.enums.ControlPanelTabs;
import com.teamresourceful.resourcefulbees.common.lib.enums.TerminalPanels;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers.CentrifugeContainer;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.AbstractGUICentrifugeEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.base.ICentrifugeOutput;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.helpers.CentrifugeTier;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.states.CentrifugeState;
import com.teamresourceful.resourcefulbees.common.network.NetPacketHandler;
import com.teamresourceful.resourcefulbees.common.network.packets.client.PurgeContentsPacket;
import com.teamresourceful.resourcefulbees.common.network.packets.client.VoidExcessPacket;
import com.teamresourceful.resourcefulbees.common.utils.WorldUtils;
import com.teamresourceful.resourcefullib.client.screens.TooltipProvider;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class BaseCentrifugeScreen<T extends CentrifugeContainer<?>> extends AbstractContainerScreen<T> {

    //TODO add back button to inventory screens
    //protected static final Rectangle BACK = new Rectangle(2, 2, 13, 13);

    protected final CentrifugeTier tier;
    protected CentrifugeState centrifugeState;
    protected @Nullable AbstractInfoPanel<?> infoPanel;
    protected @Nullable NavigableControlPanel<?> navPanel;
    protected ControlPanelTabs controlPanelTab = ControlPanelTabs.HOME;
    protected ControlPanelTabs navPanelTab = ControlPanelTabs.HOME;
    protected TerminalPanels currentInfoPanel = TerminalPanels.TERMINAL_HOME;
    protected int selectionIndex = 0;
    protected TerminalToastWidget toastWidget;

    protected BaseCentrifugeScreen(T pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.tier = pMenu.getTier();
        this.imageWidth = 360;
        this.imageHeight = 228;
        this.centrifugeState = pMenu.getCentrifugeState();
    }

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
    public void render(@NotNull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        renderTooltip(matrixStack, TooltipProvider.getTooltips(this.renderables, mouseX, mouseY), Optional.empty(), mouseX, mouseY);
    }

    @Override
    protected void renderLabels(@NotNull PoseStack matrix, int pX, int pY) {
        TextUtils.tf12DrawCenteredStringNoShadow(matrix, this.title, 166, 5.5f, TextUtils.FONT_COLOR_1);
    }

    @Override
    protected void renderBg(@NotNull PoseStack matrix, float pPartialTicks, int pX, int pY) {
        this.renderBackground(matrix);
        if (minecraft == null) return;
        //BACKGROUND
        ClientUtils.bindTexture(CentrifugeTextures.BACKGROUND);
        blit(matrix, leftPos, topPos, 0, 0, imageWidth, imageHeight, 360, 228);
        //COMPONENTS
        ClientUtils.bindTexture(CentrifugeTextures.COMPONENTS);
        blit(matrix, leftPos+102, topPos+39, 19, 0, 237, 164);
    }

    private void closeScreen() {
        if (minecraft != null) minecraft.setScreen(null);
    }

    public void setToastText(Component toastText) {
        if (toastWidget != null) toastWidget.setToastText(toastText);
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
            /* else {
            removeInfoPanelIfExists();
        }*/
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
        notifyInfoPanelOfEntitySelection();
    }

    protected void removeInfoPanelIfExists() {
        if (infoPanel != null) removeWidget(infoPanel);
        infoPanel = null;
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

    public void voidExcess() {
        if (navPanel == null) return;
        AbstractGUICentrifugeEntity selectedEntity = navPanel.selectedEntity();
        if (selectedEntity instanceof ICentrifugeOutput<?> outputEntity) {
            boolean voidsExcess = !outputEntity.voidsExcess();
            //TODO make translatable
            setToastText(Component.literal(voidsExcess ? "Excess contents will be voided for output" : "Excess contents will not be voided for output"));
            NetPacketHandler.CHANNEL.sendToServer(new VoidExcessPacket(selectedEntity.getBlockPos(), voidsExcess));
        }
    }

    public @Nullable <A extends AbstractGUICentrifugeEntity> A getBlockEntity(BlockPos pos, Class<A> clazz) {
        if (minecraft == null || minecraft.level == null) return null;
        return WorldUtils.getTileEntity(clazz, minecraft.level, pos);
    }

    public void purgeContents() {
        if (navPanel == null) return;
        AbstractGUICentrifugeEntity selectedEntity = navPanel.selectedEntity();
        if (selectedEntity instanceof ICentrifugeOutput<?>) {
            //TODO make translatable
            setToastText(Component.literal("Output contents purged!"));
            NetPacketHandler.CHANNEL.sendToServer(new PurgeContentsPacket(selectedEntity.getBlockPos()));
        }
    }
}
