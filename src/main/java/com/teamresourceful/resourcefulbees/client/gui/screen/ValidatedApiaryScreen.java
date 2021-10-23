package com.teamresourceful.resourcefulbees.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.client.gui.widget.TabImageButton;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.container.ValidatedApiaryContainer;
import com.teamresourceful.resourcefulbees.common.lib.enums.ApiaryTab;
import com.teamresourceful.resourcefulbees.common.network.NetPacketHandler;
import com.teamresourceful.resourcefulbees.common.network.packets.ApiaryTabMessage;
import com.teamresourceful.resourcefulbees.common.network.packets.ExportBeeMessage;
import com.teamresourceful.resourcefulbees.common.network.packets.ImportBeeMessage;
import com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import com.teamresourceful.resourcefulbees.common.tileentity.multiblocks.apiary.ApiaryTileEntity;
import com.teamresourceful.resourcefulbees.common.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ValidatedApiaryScreen extends ContainerScreen<ValidatedApiaryContainer> {

    private static final ResourceLocation VALIDATED_TEXTURE = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/apiary/validated.png");
    private static final ResourceLocation TABS_BG = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/apiary/apiary_gui_tabs.png");
    private static final TranslationTextComponent MAIN_SCREEN = new TranslationTextComponent("gui.resourcefulbees.apiary.button.main_screen");
    private static final TranslationTextComponent STORAGE_SCREEN = new TranslationTextComponent("gui.resourcefulbees.apiary.button.storage_screen");
    private static final ItemStack HONEYCOMB = new ItemStack(Items.HONEYCOMB);
    private static final ItemStack BEE_JAR = new ItemStack(ModItems.BEE_JAR.get());
    private int beeIndexOffset;
    private float sliderProgress;
    private boolean clickedOnScroll;
    private final ApiaryTileEntity apiaryTileEntity;
    private Button importButton;
    private Button exportButton;
    private TabImageButton storageTabButton;

    public ValidatedApiaryScreen(ValidatedApiaryContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        this.imageWidth = 250;
        this.imageHeight = 152;
        apiaryTileEntity = this.menu.getApiaryTileEntity();
    }

    @Override
    protected void init() {
        super.init();
        importButton = this.addButton(new Button(this.leftPos + 73, this.topPos + 10, 40, 20, new TranslationTextComponent("gui.resourcefulbees.apiary.button.import"), onPress -> this.importBee()));
        exportButton = this.addButton(new Button(this.leftPos + 159, this.topPos + 10, 40, 20, new TranslationTextComponent("gui.resourcefulbees.apiary.button.export"), onPress -> this.exportSelectedBee()));
        addTabButtons();
    }

    private void addTabButtons() {
        int i = this.leftPos;
        int j = this.topPos;
        int t = i + this.imageWidth - 25;
        addTabImageButton(j + 17, t, ApiaryTab.MAIN, MAIN_SCREEN, BEE_JAR).active = false;
        storageTabButton = addTabImageButton(j + 37, t, ApiaryTab.STORAGE, STORAGE_SCREEN, HONEYCOMB); // may need to pass in itemX so it can be 2 like the old code
    }

    //TODO ummmmm clean this up? yes/no? - epic
    private TabImageButton addTabImageButton(int j, int t, ApiaryTab tab, TextComponent text, ItemStack displayItem) {
        return this.addButton(new TabImageButton(t+1, j, 18, 18, 110, 0, 18, TABS_BG, displayItem, 1, 1,
                onPress -> this.changeScreen(tab), 128, 128) {

            @Override
            public void renderToolTip(@NotNull MatrixStack matrix, int mouseX, int mouseY) {
                ValidatedApiaryScreen.this.renderTooltip(matrix, text, mouseX, mouseY);
            }
        });
    }

    private void changeScreen(ApiaryTab tab) {
        if (tab == ApiaryTab.STORAGE && storageTabButton.active) {
            NetPacketHandler.sendToServer(new ApiaryTabMessage(apiaryTileEntity.getBlockPos(), ApiaryTab.STORAGE));
        }
    }

    private void exportSelectedBee() {
        if (apiaryTileEntity.getBeeCount() != 0) {
            NetPacketHandler.sendToServer(new ExportBeeMessage(this.menu.getPos(), this.menu.getBeeList()[this.menu.getSelectedBee()]));
            //beeIndexOffset--;
            // TODO this causes Array Out of Bounds exception.
            //  Not having it causes GUI to not update correctly when last bee in list is exported.
            //  Figure out how to make list update correctly when last bee is exported without this error.
        }
    }

    private void importBee() {
        NetPacketHandler.sendToServer(new ImportBeeMessage(this.menu.getPos()));
    }

    @Override
    public void render(@NotNull MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        if (apiaryTileEntity != null) {
            this.renderBackground(matrix);
            super.render(matrix, mouseX, mouseY, partialTicks);
            this.renderTooltip(matrix, mouseX, mouseY);
            int l = this.leftPos + 5;
            int i1 = this.topPos + 18;
            int j1 = this.beeIndexOffset + 7;
            renderBeeToolTip(matrix, mouseX, mouseY, l, i1, j1);
        }
    }

    @Override
    protected void renderBg(@NotNull MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
        Minecraft client = this.minecraft;
        if (client != null && apiaryTileEntity != null) {
            if (this.menu.getSelectedBee() > apiaryTileEntity.getBeeCount() - 1) {
                this.menu.selectBee(apiaryTileEntity.getBeeCount() - 1);
            }
            if (this.menu.getSelectedBee() == -1 && apiaryTileEntity.getBeeCount() > 0) {
                this.menu.selectBee(0);
            }
            exportButton.active = this.menu.getSelectedBee() != -1;
            importButton.active = apiaryTileEntity.getBeeCount() < CommonConfig.APIARY_MAX_BEES.get();
            storageTabButton.active = apiaryTileEntity.getApiaryStorage() != null;

            this.menu.setBeeList(Arrays.copyOf(apiaryTileEntity.bees.keySet().toArray(), apiaryTileEntity.getBeeCount(), String[].class));
            this.minecraft.getTextureManager().bind(VALIDATED_TEXTURE);
            int i = this.leftPos;
            int j = this.topPos;
            this.blit(matrix, i, j, 0, 0, this.imageWidth, this.imageHeight);
            if (!this.canScroll()) {
                this.sliderProgress = 0;
            }
            int k = (int) (99.0F * this.sliderProgress);
            this.blit(matrix, i + 44, j + 18 + k, 54 + (this.canScroll() ? 0 : 6), 152, 6, 27);
            int l = this.leftPos + 5;
            int i1 = this.topPos + 18;
            int j1 = this.beeIndexOffset + 7;
            this.drawRecipesBackground(matrix, mouseX, mouseY, l, i1, j1);
            this.drawBees(matrix, l, i1, j1);

            int t = i + this.imageWidth - 25;
            this.minecraft.getTextureManager().bind(TABS_BG);
            blit(matrix, t-1, j + 12, 0,0, 25, 68, 128, 128);
        }
    }

    @Override
    protected void renderLabels(@NotNull MatrixStack matrix, int mouseX, int mouseY) {
        String s = String.format("(%1$s/%2$s) Bees", apiaryTileEntity.getBeeCount(), CommonConfig.APIARY_MAX_BEES.get());
        this.font.draw(matrix, s, 4, 7, 0x404040);

        for (Widget widget : this.buttons) {
            if (widget.isHovered()) {
                widget.renderToolTip(matrix, mouseX - this.leftPos, mouseY - this.topPos);
                break;
            }
        }
    }

    private void renderBeeToolTip(@NotNull MatrixStack matrix, int mouseX, int mouseY, int left, int top, int beeIndexOffsetMax) {
        for (int i = this.beeIndexOffset; i < beeIndexOffsetMax && i < apiaryTileEntity.getBeeCount(); ++i) {
            int j = i - this.beeIndexOffset;
            int i1 = top + j * 18;

            if (mouseX >= left && mouseY >= i1 && mouseX < left + 16 && mouseY < i1 + 18) {
                List<ITextComponent> beeInfo = new ArrayList<>();
                ApiaryTileEntity.ApiaryBee apiaryBee = this.menu.getApiaryBee(i);

                int ticksInHive = apiaryBee.getTicksInHive();
                int minTicks = apiaryBee.minOccupationTicks;
                int ticksLeft = Math.max(minTicks - ticksInHive, 0);
                beeInfo.add(apiaryBee.displayName);
                beeInfo.add(new TranslationTextComponent("gui.resourcefulbees.apiary.bee.ticks_in_hive").append(": ").append(String.valueOf(ticksInHive)));
                beeInfo.add(new TranslationTextComponent("gui.resourcefulbees.apiary.bee.ticks_left").append(": ").append(String.valueOf(ticksLeft)));
                this.renderComponentTooltip(matrix, beeInfo, mouseX, mouseY);
            }
        }
    }

    private void drawRecipesBackground(@NotNull MatrixStack matrix, int mouseX, int mouseY, int left, int top, int beeIndexOffsetMax) {

        for (int i = this.beeIndexOffset; i < beeIndexOffsetMax && i < apiaryTileEntity.getBeeCount(); ++i) {
            int j = i - this.beeIndexOffset;
            int k = left;
            int i1 = top + j * 18;
            int j1 = this.imageHeight;
            if (i == this.menu.getSelectedBee()) {
                j1 += 18;
            } else if (mouseX >= k && mouseY >= i1 && mouseX < k + 16 && mouseY < i1 + 18) {
                j1 += 36;
            }
            this.blit(matrix, k, i1, 0, j1, 18, 18);
            int l1 = 18;
            k = k + 18;
            j1 = this.imageHeight;
            if (this.menu.getApiaryBee(i).isLocked()) {
                l1 += 18;
            }
            if (mouseX >= k && mouseY >= i1 && mouseX < k + 16 && mouseY < i1 + 18) {
                j1 += 18;
            }

            this.blit(matrix, k, i1, l1, j1, 18, 18);
        }

    }

    private void drawBees(MatrixStack matrix, int left, int top, int beeIndexOffsetMax) {
        for (int i = this.beeIndexOffset; i < beeIndexOffsetMax && i < apiaryTileEntity.getBeeCount(); ++i) {
            int j = i - this.beeIndexOffset;
            int i1 = top + j * 18 + 2;
            Entity bee = BeeRegistry.getRegistry().getBeeData(this.menu.getApiaryBee(i).beeType).getEntityType().create(Minecraft.getInstance().level);
            RenderUtils.renderEntity(matrix, bee, Minecraft.getInstance().level, left, i1, -45, 2);
/*

            ItemStack beeJar = new ItemStack(ModItems.BEE_JAR.get());
            CompoundNBT data = new CompoundNBT();

            CompoundNBT tag = this.menu.getApiaryBee(i).entityData;
            String entityID = tag.getString("id");

            data.putString(NBTConstants.NBT_ENTITY, entityID);
            data.putString(NBTConstants.NBT_COLOR, this.menu.getApiaryBee(i).beeColor);

            beeJar.setTag(data);
            if (this.minecraft != null)
                this.minecraft.getItemRenderer().renderAndDecorateItem(beeJar, left, i1);*/
        }
    }

    private boolean canScroll() {
        return apiaryTileEntity.getBeeCount() > 7;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        if (this.canScroll()) {
            int i = this.getHiddenRows();
            this.sliderProgress = (float) (this.sliderProgress - scrollAmount / i);
            this.sliderProgress = MathHelper.clamp(this.sliderProgress, 0.0F, 1.0F);
            this.beeIndexOffset = (int) ((this.sliderProgress * i) + 0.5D);
        }

        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int pMouseDragged5, double pMouseDragged6, double pMouseDragged8) {
        if (this.clickedOnScroll && this.canScroll()) {
            int i = this.topPos + 14;
            int j = i + 101;
            this.sliderProgress = ((float) mouseY - i - 7.5F) / ((j - i) - 15.0F);
            this.sliderProgress = MathHelper.clamp(this.sliderProgress, 0.0F, 1.0F);
            this.beeIndexOffset = (int) ( (this.sliderProgress * this.getHiddenRows()) + 0.5D);
            return true;
        } else {
            return super.mouseDragged(mouseX, mouseY, pMouseDragged5, pMouseDragged6, pMouseDragged8);
        }
    }

    private int getHiddenRows() { return apiaryTileEntity.getBeeCount() - 7; }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int pMouseClicked5) {
        this.clickedOnScroll = false;
        if (apiaryTileEntity.getBeeCount() > 0) {
            int i = this.leftPos + 5;
            int j = this.topPos + 18;
            int k = this.beeIndexOffset + 7;

            for (int l = this.beeIndexOffset; l < k; ++l) {
                int i1 = l - this.beeIndexOffset;
                double d0 = mouseX - (i);
                double d1 = mouseY - (j + i1 * 18);
                if (d0 >= 0.0D && d1 >= 0.0D && d0 < 17.0D && d1 < 17.0D && this.menu.selectBee(l)) {
                    Minecraft.getInstance().getSoundManager().play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    return true;
                }

                if (d0 >= 18.0D && d1 >= 0.0D && d0 < 35.0D && d1 < 17.0D && this.menu.lockOrUnlockBee(l)) {
                    Minecraft.getInstance().getSoundManager().play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    return true;
                }
            }

            i = this.leftPos + 44;
            j = this.topPos + 18;
            if (mouseX >= i && mouseX < (i + 6) && mouseY >= j && mouseY < (j + 101)) {
                this.clickedOnScroll = true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, pMouseClicked5);
    }
}
