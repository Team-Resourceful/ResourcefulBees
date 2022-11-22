package com.teamresourceful.resourcefulbees.client.screens.beepedia;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.data.honey.HoneyData;
import com.teamresourceful.resourcefulbees.api.data.trait.Trait;
import com.teamresourceful.resourcefulbees.client.components.beepedia.BeepediaMainButton;
import com.teamresourceful.resourcefulbees.client.components.beepedia.search.SearchBar;
import com.teamresourceful.resourcefulbees.client.components.beepedia.search.SearchBox;
import com.teamresourceful.resourcefulbees.client.components.beepedia.selection.BeeEntry;
import com.teamresourceful.resourcefulbees.client.components.beepedia.selection.ItemEntry;
import com.teamresourceful.resourcefulbees.client.components.beepedia.selection.SelectionButtons;
import com.teamresourceful.resourcefulbees.client.screens.base.SubdividedScreen;
import com.teamresourceful.resourcefulbees.client.screens.beepedia.pages.HomePage;
import com.teamresourceful.resourcefulbees.client.screens.beepedia.pages.bee.BeePage;
import com.teamresourceful.resourcefulbees.client.screens.beepedia.pages.honey.HoneyPage;
import com.teamresourceful.resourcefulbees.client.screens.beepedia.pages.trait.TraitPage;
import com.teamresourceful.resourcefulbees.client.screens.beepedia.state.BeepediaState;
import com.teamresourceful.resourcefulbees.client.screens.beepedia.state.StringPageState;
import com.teamresourceful.resourcefulbees.common.capabilities.beepedia.BeepediaData;
import com.teamresourceful.resourcefulbees.common.capabilities.beepedia.CreativeBeepediaData;
import com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.registry.custom.HoneyRegistry;
import com.teamresourceful.resourcefulbees.common.registry.custom.TraitRegistry;
import com.teamresourceful.resourcefullib.client.components.selection.ListEntry;
import com.teamresourceful.resourcefullib.client.components.selection.SelectionList;
import com.teamresourceful.resourcefullib.client.screens.state.ScreenStateManager;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class BeepediaScreen extends SubdividedScreen {

    public static final ResourceLocation STATE_ID = new ResourceLocation(ResourcefulBees.MOD_ID, "beepedia");
    public static final ResourceLocation BACKGROUND = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/beepedia/screen.png");

    private BeepediaData data;
    public int ticks;
    private SelectionList<ListEntry> selectionList;

    public BeepediaScreen() {
        super(CommonComponents.EMPTY, 328, 200, 133, 9, screen -> screen instanceof BeepediaScreen beepedia ? new HomePage(beepedia) : null);
    }

    @Override
    protected void init() {
        super.init();

        addRenderableWidget(new BeepediaMainButton(124, 175, 0, button -> getState().search = getState().search == null ? "" : null));
        addRenderableWidget(new BeepediaMainButton(154, 175, 1, button -> this.setSubScreenNow(new HomePage(this)), button -> !(this.getSubScreen() instanceof HomePage)));
        addRenderableWidget(new BeepediaMainButton(184, 175, 2, button -> this.goBack(), button -> this.canGoBack()));

        this.selectionList = addRenderableWidget(new SelectionList<>(9, 31, 119, 125, 20, this::updateSelection));

        addRenderableWidget(new SelectionButtons(9, 9, this));
        addRenderableWidget(new SearchBar(22, 159, this));
        addRenderableWidget(new SearchBox(font, 23, 179, this));

        updateSelections();
    }

    @Override
    public void render(@NotNull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
        renderTooltip(stack, getTooltip(mouseX, mouseY), Optional.empty(), mouseX, mouseY);
    }

    @Override
    public void renderScreen(@NotNull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        RenderUtils.bindTexture(BACKGROUND);
        blit(stack, 0, 0, 0, 0, 328, 200, 328, 200);
        super.renderScreen(stack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void forceSubScreen(@Nullable Screen subScreen) {
        super.forceSubScreen(subScreen);
        if (subScreen instanceof HoneyPage page) {
            getState().page = StringPageState.createHoneyPageState(page.getData().name());
        } else if (subScreen instanceof BeePage page) {
            getState().page = StringPageState.createBeePageState(page.getData().name());
        } else if (subScreen instanceof TraitPage page) {
            getState().page = StringPageState.createTraitPageState(page.getData().name());
        } else {
            getState().page = null;
        }
    }

    @Override
    public void tick() {
        this.ticks++;
        super.tick();
    }

    public BeepediaState getState() {
        return ScreenStateManager.getOrAddState(STATE_ID, BeepediaState::new, BeepediaState.class);
    }

    public void updateSelections() {
        List<? extends ListEntry> entries = switch (getState().type) {
            case BEES -> BeeRegistry.getRegistry()
                    .getStreamOfBees()
                    .filter(BeepediaSearchHandler.search(getState().search))
                    .sorted(
                        sortBee(getState(), BeepediaState.Sorting.FOUND, a -> this.data == null || !this.data.hasBee(a.name()))
                        .thenComparing(sortBee(getState(), BeepediaState.Sorting.ALPHABETICAL, CustomBeeData::name))
                        .thenComparing(sortBee(getState(), BeepediaState.Sorting.TRAITS, data -> data.getTraitData().hasTraits()))
                        .thenComparing(sortBee(getState(), BeepediaState.Sorting.MUTATION, data -> data.getMutationData().hasMutation()))
                    )
                    .map(data -> new BeeEntry(data, () -> this.data != null && this.data.hasBee(data.name())))
                    .toList();
            case TRAITS -> TraitRegistry.getRegistry()
                    .getStreamOfTraits()
                    .filter(trait -> getState().search == null || trait.name().toLowerCase().contains(getState().search.toLowerCase()))
                    .sorted((o1, o2) -> getState().getSorting(BeepediaState.Sorting.ALPHABETICAL).isUnset() ? 0 : o1.name().compareTo(o2.name()) * (getState().getSorting(BeepediaState.Sorting.ALPHABETICAL).isFalse() ? -1 : 1))
                    .map(data -> new ItemEntry<>(data, a -> new ItemStack(a.displayItem()), Trait::getDisplayName))
                    .toList();
            case HONEY -> HoneyRegistry.getRegistry()
                    .getStreamOfHoney()
                    .filter(honey -> getState().search == null || honey.name().toLowerCase().contains(getState().search.toLowerCase()))
                    .sorted((o1, o2) -> getState().getSorting(BeepediaState.Sorting.ALPHABETICAL).isUnset() ? 0 : o1.name().compareTo(o2.name()) * (getState().getSorting(BeepediaState.Sorting.ALPHABETICAL).isFalse() ? -1 : 1))
                    .map(data -> new ItemEntry<>(data, a -> new ItemStack(a.bottleData().honeyBottle().get()), HoneyData::getDisplayName))
                    .toList();
        };

        selectionList.updateEntries(entries);
    }

    public static <T extends Comparable<T>> Comparator<CustomBeeData> sortBee(BeepediaState state, BeepediaState.Sorting sorting, Function<CustomBeeData, T> comparator) {
        return (o1, o2) -> state.getSorting(sorting).isUnset() ? 0 : comparator.apply(o1).compareTo(comparator.apply(o2)) * (state.getSorting(sorting).isFalse() ? -1 : 1);
    }

    public void updateSelection(ListEntry entry) {
        if (entry instanceof BeeEntry beeEntry) {
            setSubScreen(new BeePage(beeEntry.getData()));
        } else if (entry instanceof ItemEntry<?> itemEntry) {
            Object data = itemEntry.getData();
            if (data instanceof Trait trait) {
                setSubScreen(new TraitPage(trait));
            } else if (data instanceof HoneyData honey) {
                setSubScreen(new HoneyPage(honey));
            }
        }
    }

    public void updateData(BeepediaData data) {
        if (this.data instanceof CreativeBeepediaData) return; //Do not replace the creative Beepedia.
        this.data = data;
    }
}
