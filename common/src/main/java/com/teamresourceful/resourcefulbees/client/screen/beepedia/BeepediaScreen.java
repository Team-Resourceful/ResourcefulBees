package com.teamresourceful.resourcefulbees.client.screen.beepedia;

import com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.data.honey.CustomHoneyData;
import com.teamresourceful.resourcefulbees.api.data.trait.Trait;
import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.api.registry.HoneyRegistry;
import com.teamresourceful.resourcefulbees.api.registry.TraitRegistry;
import com.teamresourceful.resourcefulbees.client.component.BeepediaMainButton;
import com.teamresourceful.resourcefulbees.client.component.search.SearchBar;
import com.teamresourceful.resourcefulbees.client.component.search.SearchBox;
import com.teamresourceful.resourcefulbees.client.component.selection.BeeEntry;
import com.teamresourceful.resourcefulbees.client.component.selection.ItemEntry;
import com.teamresourceful.resourcefulbees.client.component.selection.SelectionButtons;
import com.teamresourceful.resourcefulbees.client.screen.base.SubdividedScreen;
import com.teamresourceful.resourcefulbees.client.screen.beepedia.pages.HomePage;
import com.teamresourceful.resourcefulbees.client.screen.beepedia.pages.bees.BeePage;
import com.teamresourceful.resourcefulbees.client.screen.beepedia.pages.honeys.HoneyPage;
import com.teamresourceful.resourcefulbees.client.screen.beepedia.pages.traits.TraitPage;
import com.teamresourceful.resourcefulbees.client.screen.beepedia.state.BeepediaState;
import com.teamresourceful.resourcefulbees.client.screen.beepedia.state.StringPageState;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.resources.storage.beepedia.BeepediaData;
import com.teamresourceful.resourcefulbees.common.resources.storage.beepedia.CreativeBeepediaData;
import com.teamresourceful.resourcefullib.client.components.selection.ListEntry;
import com.teamresourceful.resourcefullib.client.components.selection.SelectionList;
import com.teamresourceful.resourcefullib.client.screens.state.ScreenStateManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

public class BeepediaScreen extends SubdividedScreen {

    public static final ResourceLocation STATE_ID = new ResourceLocation(ModConstants.MOD_ID, "beepedia");
    public static final ResourceLocation BACKGROUND = new ResourceLocation(ModConstants.MOD_ID, "textures/gui/beepedia/screen.png");

    private BeepediaData data;
    private int ticks;
    private SelectionList<ListEntry> selectionList;

    public BeepediaScreen() {
        super(CommonComponents.EMPTY, 328, 200, 133, 9, screen -> screen instanceof BeepediaScreen beepedia ? new HomePage(beepedia) : null);
    }

    @Override
    protected void init() {
        super.init();

        addRenderableWidget(new BeepediaMainButton(124, 175, 0, button -> getState().setSearch(getState().getSearch() == null ? "" : null)));
        addRenderableWidget(new BeepediaMainButton(154, 175, 1, button -> this.setSubScreenNow(new HomePage(this)), button -> !(this.getSubScreen() instanceof HomePage)));
        addRenderableWidget(new BeepediaMainButton(184, 175, 2, button -> this.goBack(), button -> this.canGoBack()));

        this.selectionList = addRenderableWidget(new SelectionList<>(9, 31, 119, 125, 20, this::updateSelection));

        addRenderableWidget(new SelectionButtons(9, 9, this));
        addRenderableWidget(new SearchBar(22, 159, this));
        addRenderableWidget(new SearchBox(font, 23, 179, this));

        updateSelections();
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderScreen(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.blit(BACKGROUND, 0, 0, 0, 0, 328, 200, 328, 200);
        super.renderScreen(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public void forceSubScreen(@Nullable Screen subScreen) {
        super.forceSubScreen(subScreen);
        if (subScreen instanceof HoneyPage page) {
            getState().setPage(StringPageState.createHoneyPageState(page.getData().name()));
        } else if (subScreen instanceof BeePage page) {
            getState().setPage(StringPageState.createBeePageState(page.getData().name(), this::openTraitPage));
        } else if (subScreen instanceof TraitPage page) {
            getState().setPage(StringPageState.createTraitPageState(page.getData().name()));
        } else {
            getState().setPage(null);
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
        List<? extends ListEntry> entries = switch (getState().getType()) {
            case BEES -> BeeRegistry.get()
                    .getStreamOfBees()
                    .filter(BeepediaSearchHandler.search(getState().getSearch()))
                    .sorted(
                        sortBee(getState(), BeepediaState.Sorting.FOUND, a -> this.data == null || !this.data.hasBee(a.name()))
                        .thenComparing(sortBee(getState(), BeepediaState.Sorting.ALPHABETICAL, CustomBeeData::name))
                        .thenComparing(sortBee(getState(), BeepediaState.Sorting.TRAITS, trait -> trait.getTraitData().hasTraits()))
                        .thenComparing(sortBee(getState(), BeepediaState.Sorting.MUTATION, mutation -> minecraft != null && !mutation.getMutationData().hasMutation(minecraft.level)))
                    )
                    .map(bee -> new BeeEntry(bee, () -> this.data != null && this.data.hasBee(bee.name())))
                    .toList();
            case TRAITS -> TraitRegistry.get()
                    .getStreamOfTraits()
                    .filter(trait -> getState().getSearch() == null || trait.name().toLowerCase(Locale.ROOT).contains(getState().getSearch().toLowerCase(Locale.ROOT)))
                    .sorted((o1, o2) -> getState().getSorting(BeepediaState.Sorting.ALPHABETICAL).isUndefined() ? 0 : o1.name().compareTo(o2.name()) * (getState().getSorting(BeepediaState.Sorting.ALPHABETICAL).isFalse() ? -1 : 1))
                    .map(trait -> new ItemEntry<>(trait, a -> new ItemStack(a.displayItem()), Trait::getDisplayName))
                    .toList();
            case HONEY -> HoneyRegistry.get()
                    .getStreamOfHoney()
                    .filter(honey -> getState().getSearch() == null || honey.name().toLowerCase(Locale.ROOT).contains(getState().getSearch().toLowerCase(Locale.ROOT)))
                    .sorted((o1, o2) -> getState().getSorting(BeepediaState.Sorting.ALPHABETICAL).isUndefined() ? 0 : o1.name().compareTo(o2.name()) * (getState().getSorting(BeepediaState.Sorting.ALPHABETICAL).isFalse() ? -1 : 1))
                    .map(honey -> new ItemEntry<>(honey, a -> new ItemStack(honey.getBottleData().bottle().get()), a -> honey.displayName()))
                    .toList();
        };
        selectionList.updateEntries(entries);
    }

    public void openTraitPage(Trait trait) {
        this.getState().setType(BeepediaState.Type.TRAITS);
        updateSelections();
        for (ListEntry child : selectionList.children()) {
            if (child instanceof ItemEntry<?> entry && entry.getData() == trait) {
                selectionList.setSelected(entry);
                selectionList.ensureVisible(entry);
                break;
            }
        }
    }

    public static <T extends Comparable<T>> Comparator<CustomBeeData> sortBee(BeepediaState state, BeepediaState.Sorting sorting, Function<CustomBeeData, T> comparator) {
        return (o1, o2) -> state.getSorting(sorting).isUndefined() ? 0 : comparator.apply(o1).compareTo(comparator.apply(o2)) * (state.getSorting(sorting).isFalse() ? -1 : 1);
    }

    public void updateSelection(ListEntry entry) {
        if (entry instanceof BeeEntry beeEntry) {
            setSubScreen(new BeePage(beeEntry.getData(), this::openTraitPage));
        } else if (entry instanceof ItemEntry<?> itemEntry) {
            Object entryData = itemEntry.getData();
            if (entryData instanceof Trait trait) {
                setSubScreen(new TraitPage(trait));
            } else if (entryData instanceof CustomHoneyData honey) {
                setSubScreen(new HoneyPage(honey));
            }
        }
    }

    public void updateData(BeepediaData data) {
        if (this.data instanceof CreativeBeepediaData) return; //Do not replace the creative Beepedia.
        this.data = data;
    }

    public int getTicks() {
        return ticks;
    }
}
