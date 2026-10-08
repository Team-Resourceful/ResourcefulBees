package com.teamresourceful.resourcefulbees.client.screen.beepedia;

import com.teamresourceful.resourcefulbees.api.data.bee.CustomBeeData;
import com.teamresourceful.resourcefulbees.api.data.honey.CustomHoneyData;
import com.teamresourceful.resourcefulbees.api.data.trait.Trait;
import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.api.registry.HoneyRegistry;
import com.teamresourceful.resourcefulbees.api.registry.TraitRegistry;
import com.teamresourceful.resourcefulbees.client.screen.beepedia.component.bee.BeepediaTickable;
import com.teamresourceful.resourcefulbees.client.screen.beepedia.view.bee.*;
import com.teamresourceful.resourcefulbees.client.screen.beepedia.view.honey.HoneyOverview;
import com.teamresourceful.resourcefulbees.client.screen.beepedia.view.trait.TraitOverview;
import com.teamresourceful.resourcefulbees.client.util.ClientRenderUtils;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModAttachments;
import com.teamresourceful.resourcefulbees.common.resources.storage.beepedia.BeeDiscoveryData;
import com.teamresourceful.resourcefulbees.common.resources.storage.beepedia.DiscoveredBee;
import com.teamresourceful.resourcefullib.common.color.Color;
import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.components.base.renderer.WidgetRenderer;
import earth.terrarium.olympus.client.components.compound.LayoutWidget;
import earth.terrarium.olympus.client.components.compound.radio.RadioState;
import earth.terrarium.olympus.client.components.dropdown.DropdownState;
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers;
import earth.terrarium.olympus.client.components.string.TextWidget;
import earth.terrarium.olympus.client.layouts.LinearViewLayout;
import earth.terrarium.olympus.client.utils.ListenableState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import net.minecraft.util.TriState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.NonNull;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public final class BeepediaScreen extends Screen implements BeepediaNavigator {

    private static final int MAX_WIDTH = 520;
    private static final int MAX_HEIGHT = 300;
    private static final int SCREEN_MARGIN = 4;
    private static final int MAIN_GAP = 8;
    private static final int CONTROL_HEIGHT = 20;
    private static final int CONTROL_GAP = 4;
    private static final int ENTRY_HEIGHT = 18;
    private static final int DROPDOWN_HEIGHT = 100;
    private static final int CONTENT_INSET = 10;

    public static final Identifier BACKGROUND = ModIdentifier.of("beepedia/background");
    private static final Identifier DROPDOWN = ModIdentifier.of("beepedia/dropdown");
    private static final Identifier LOGO = ModIdentifier.of("beepedia/logo");

    private static final WidgetSprites BUTTON = new WidgetSprites(
            ModIdentifier.of("beepedia/button/normal"),
            ModIdentifier.of("beepedia/button/normal"),
            ModIdentifier.of("beepedia/button/selected")
    );

    private static final WidgetSprites BUTTON_SOLID = new WidgetSprites(
            ModIdentifier.of("beepedia/button/solid/normal"),
            ModIdentifier.of("beepedia/button/solid/normal"),
            ModIdentifier.of("beepedia/button/solid/selected")
    );

    private final ListenableState<String> search = ListenableState.of("");
    private final RadioState<BeepediaCategory> category = RadioState.ofEnum(BeepediaCategory.BEES);
    private final ListenableState<BeepediaSelection> selected = ListenableState.of(BeepediaSelection.Home.INSTANCE);
    private final RadioState<BeeSection> section = RadioState.ofEnum(BeeSection.OVERVIEW);
    private final DropdownState<SortMode> sort = DropdownState.of(SortMode.ALPHABETICAL);

    private LayoutWidget<LinearViewLayout> content;
    private LayoutWidget<LinearViewLayout> results;

    private int beepediaX;
    private int beepediaY;
    private int beepediaWidth;
    private int beepediaHeight;
    private int contentWidth;
    private int contentHeight;
    private int resultsHeight;
    private int sidebarWidth;

    private final boolean creative;

    public BeepediaScreen(boolean creative) {
        super(Component.empty());
        search.registerListener(_ -> rebuildResults());
        selected.registerListener(_ -> rebuildContent());
        this.creative = creative;
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND, beepediaX, beepediaY, beepediaWidth, beepediaHeight);
    }

    @Override
    protected void init() {
        super.init();

        beepediaWidth = Math.min(MAX_WIDTH, width - SCREEN_MARGIN * 2);
        beepediaHeight = Math.min(MAX_HEIGHT, height - SCREEN_MARGIN * 2);
        beepediaX = Math.max(SCREEN_MARGIN, (width - beepediaWidth) / 2);
        beepediaY = Math.max(SCREEN_MARGIN, (height - beepediaHeight) / 2);
        int innerWidth = beepediaWidth - CONTENT_INSET * 2;
        int innerHeight = beepediaHeight - CONTENT_INSET * 2;
        sidebarWidth = Math.min(140, innerWidth * 30 / 100);
        contentWidth = innerWidth - sidebarWidth - MAIN_GAP;
        contentHeight = innerHeight - 20;
        resultsHeight = contentHeight - CONTROL_HEIGHT * 3 - CONTROL_GAP * 3;

        LinearLayout root = LinearLayout.horizontal().spacing(MAIN_GAP);

        root.addChild(createSidebar());
        root.addChild(createContent());

        root.arrangeElements();

        root.setPosition(beepediaX+CONTENT_INSET, beepediaY+CONTENT_INSET);
        //FrameLayout.centerInRectangle(root, beepediaX-2, beepediaY-10, this.width, this.height);

        root.visitWidgets(this::addRenderableWidget);
    }

    private LinearLayout createSidebar() {
        LinearLayout sidebar = LinearLayout.vertical().spacing(CONTROL_GAP);

        sidebar.addChild(createCategorySelector());
        sidebar.addChild(Widgets.textInput(search, input -> input.withSize(sidebarWidth, CONTROL_HEIGHT)));
        sidebar.addChild(createSortControls());
        sidebar.addChild(createResults());

        return sidebar;
    }

    private LayoutWidget<LinearViewLayout> createResults() {
        results = Widgets.list(list -> {
            list.withSize(sidebarWidth, resultsHeight);
            list.withScrollableY(TriState.DEFAULT);
            list.withContentFillWidth();
        });

        rebuildResults();

        return results;
    }

    private List<Trait> visibleTraits() {
        String query = search.get().trim().toLowerCase(Locale.ROOT);

        return TraitRegistry.get().getStreamOfTraits()
                .filter(trait ->
                        query.isEmpty()
                                || trait.name()
                                .toLowerCase(Locale.ROOT)
                                .contains(query)
                                || trait.getDisplayName()
                                .getString()
                                .toLowerCase(Locale.ROOT)
                                .contains(query)
                )
                .sorted(traitComparator())
                .toList();
    }

    private List<CustomHoneyData> visibleHoneys() {
        String query = search.get().trim().toLowerCase(Locale.ROOT);

        return HoneyRegistry.get().getStreamOfHoney()
                .filter(honey ->
                        query.isEmpty()
                                || honey.name()
                                .toLowerCase(Locale.ROOT)
                                .contains(query)
                                || honey.displayName()
                                .getString()
                                .toLowerCase(Locale.ROOT)
                                .contains(query)
                )
                .sorted(honeyComparator())
                .toList();
    }

    private List<CustomBeeData> visibleBees() {
        String query = search.get().trim().toLowerCase(Locale.ROOT);

        return BeeRegistry.get().getStreamOfBees()
                .filter(bee -> {
                    if (query.isEmpty()) {
                        return true;
                    }
                    if (!isDiscovered(bee)) {
                        return false;
                    }

                    return bee.id().toString().contains(query) || bee.displayName().getString().toLowerCase(Locale.ROOT).contains(query);
                })
                .sorted(beeComparator())
                .toList();
    }

    private Comparator<Trait> traitComparator() {
        return switch (sort.get()) {
            case ALPHABETICAL -> Comparator.comparing(trait -> trait.getDisplayName().getString().toLowerCase(Locale.ROOT));
            case DISCOVERED, ID -> Comparator.comparing(Trait::name);
        };
    }

    private Comparator<CustomHoneyData> honeyComparator() {
        return switch (sort.get()) {
            case ALPHABETICAL -> Comparator.comparing(honey -> honey.displayName().getString().toLowerCase(Locale.ROOT));
            case DISCOVERED, ID -> Comparator.comparing(CustomHoneyData::name);
        };
    }

    private Comparator<CustomBeeData> beeComparator() {
        Comparator<CustomBeeData> selectedSort = switch (sort.get()) {
            case ALPHABETICAL -> Comparator.comparing(bee -> bee.displayName().getString().toLowerCase(Locale.ROOT));
            case ID -> Comparator.comparing(CustomBeeData::id);
            case DISCOVERED -> Comparator.comparingLong(this::discoveredAt).reversed();
        };

        return (a, b) -> {
            boolean aDiscovered = isDiscovered(a);
            boolean bDiscovered = isDiscovered(b);

            if (aDiscovered != bDiscovered) {
                return aDiscovered ? -1 : 1;
            }

            if (!aDiscovered) {
                return 0;
            }

            return selectedSort.compare(a, b);
        };
    }

    private BeeDiscoveryData discoveryData() {
        return minecraft.player == null ? null : minecraft.player.getData(ModAttachments.BEE_DISCOVERY);
    }

    private Optional<DiscoveredBee> discovery(CustomBeeData bee) {
        BeeDiscoveryData data = discoveryData();

        if (data == null) {
            return Optional.empty();
        }

        return data.get(bee.id());
    }

    private long discoveredAt(CustomBeeData bee) {
        return discovery(bee).map(DiscoveredBee::discoveredAt).orElse(0L);
    }

    private boolean isDiscovered(CustomBeeData bee) {
        return creative || discovery(bee).isPresent();
    }

    private LayoutWidget<LinearViewLayout> createContent() {
        content = Widgets.list(widget -> {
            widget.withSize(contentWidth, contentHeight);
            widget.withScrollableY(TriState.DEFAULT);
            widget.withContentFillWidth();
        });

        rebuildContent();

        return content;
    }

    private void select(BeepediaSelection selection) {
        resetBeeSection();
        selected.set(selection);
    }

    private void resetBeeSection() {
        section.set(BeeSection.OVERVIEW);
        section.setIndex(BeeSection.OVERVIEW.ordinal());
    }

    private void rebuildContent() {
        if (content == null) {
            return;
        }

        content.withContents(layout -> {
            layout.removeChildren();

            switch (selected.get()) {
                case BeepediaSelection.Home _ -> buildHome(layout);

                case BeepediaSelection.Bee(Identifier id) -> BeeRegistry.get().getOptionalBeeData(id)
                        .ifPresentOrElse(bee -> buildBeePage(layout, bee), () -> buildMissingEntry(layout, id.toString()));

                case BeepediaSelection.Trait(String id) -> buildTraitPage(layout, id);

                case BeepediaSelection.Honey(String id) -> buildHoneyPage(layout, id);
            }
        });
    }

    private void rebuildResults() {
        if (results == null) {
            return;
        }

        results.withContents(layout -> {
            layout.removeChildren();

            switch (category.get()) {
                case BEES -> visibleBees().forEach(bee -> layout.withChild(createBeeEntry(bee)));
                case TRAITS -> visibleTraits().forEach(trait -> layout.withChild(createTraitEntry(trait)));
                case HONEY -> visibleHoneys().forEach(honey -> layout.withChild(createHoneyEntry(honey)));
            }
        });
    }

    private void buildBeePage(LinearViewLayout layout, CustomBeeData bee) {
        if (!isDiscovered(bee)) {
            buildUndiscoveredBeePage(layout);
            return;
        }

        buildDiscoveredBeePage(layout, bee);
    }

    private void buildUndiscoveredBeePage(LinearViewLayout layout) {
        layout.withChild(Widgets.text("This bee has not been discovered"));
    }

    private void buildDiscoveredBeePage(LinearViewLayout layout, CustomBeeData bee) {
        layout.withGap(6);

        BeeHeader.build(layout, bee, contentWidth);
        layout.withChild(createBeeTabs());
        buildBeeSectionContent(layout, bee);
    }

    private void buildBeeSectionContent(LinearViewLayout layout, CustomBeeData bee) {
        switch (section.get()) {
            case OVERVIEW -> {
                BeeDiscoveryData data = discoveryData();

                if (data != null) {
                    data.get(bee.id()).ifPresentOrElse(discovered -> BeeOverview.build(layout, bee, discovered, contentWidth), () -> BeeOverview.build(layout, bee, new DiscoveredBee(0), contentWidth));
                }
            }
            case PRODUCTION -> BeeProduction.build(layout, bee, contentWidth);
            case BREEDING -> BeeBreeding.build(layout,bee, contentWidth);
            case TRAITS -> BeeTraits.build(layout, bee, contentWidth, this);
        }
    }

    private AbstractWidget createCategorySelector() {
        return Widgets.radio(category, radio -> radio
                        .withOptions(List.of(BeepediaCategory.values()))
                        .withSize(sidebarWidth, CONTROL_HEIGHT)
                        .withGap(2)
                        .withEntrySprites(BUTTON)
                        .withRenderer((value, _) -> WidgetRenderers.text(categoryName(value)).withColor(Color.DEFAULT))
                        .withCallback(_ -> rebuildResults()),
                _ -> {}
        );
    }

    private Component categoryName(BeepediaCategory category) {
        return switch (category) {
            case BEES -> Component.literal("Bees");
            case HONEY -> Component.literal("Honey");
            case TRAITS -> Component.literal("Traits");
        };
    }

    private AbstractWidget createTraitEntry(Trait trait) {
        ItemStack stack = new ItemStack(trait.displayItem());

        return Widgets.button()
                .withSize(sidebarWidth, ENTRY_HEIGHT)
                .withTexture(BUTTON)
                .withRenderer(itemButton(trait.getDisplayName(), stack))
                .withCallback(() -> select(new BeepediaSelection.Trait(trait.name())));
    }

    private AbstractWidget createHoneyEntry(CustomHoneyData honey) {
        ItemStack stack = new ItemStack(honey.getBottleData().bottle().get());

        return Widgets.button()
                .withSize(sidebarWidth, ENTRY_HEIGHT)
                .withTexture(BUTTON)
                .withRenderer(itemButton(honey.displayName(), stack))
                .withCallback(() -> select(new BeepediaSelection.Honey(honey.name())));
    }

    private static WidgetRenderer<AbstractWidget> itemButton(
            Component text,
            ItemStack stack
    ) {
        return WidgetRenderers.layered(
                (graphics, widget, _) -> graphics.item(stack, widget.getX(), widget.getY()),
                WidgetRenderers.text(text)
                        .withColor(Color.DEFAULT)
                        .withPaddingLeft(20)
                        .withPaddingRight(5)
        );
    }

    private AbstractWidget createBeeEntry(CustomBeeData bee) {
        if (!isDiscovered(bee)) {
            return undiscoveredBeeEntry();
        }

        var entity = bee.entityType().create(Minecraft.getInstance().level, EntitySpawnReason.COMMAND);
        ClientRenderUtils.preparePreviewEntity(entity);
        return Widgets.button()
                .withSize(sidebarWidth, ENTRY_HEIGHT)
                .withTexture(BUTTON)
                .withRenderer(entityButton(bee.displayName(), entity))
                .withCallback(() -> select(new BeepediaSelection.Bee(bee.id())));
    }

    private AbstractWidget undiscoveredBeeEntry() {
        return Widgets.text(Component.literal("Undiscovered"), widget -> widget
                .withCenterAlignment()
                .withSize(sidebarWidth, ENTRY_HEIGHT)
        );
    }

    private static WidgetRenderer<AbstractWidget> entityButton(Component text, Entity entity) {
        return WidgetRenderers.layered(
                (graphics, widget, _) -> ClientRenderUtils.renderEntity(graphics, entity, widget.getX(), widget.getY(), 20, 20, -135f, 0.65f),
                WidgetRenderers.text(text).withColor(Color.DEFAULT).withPaddingLeft(20).withPaddingRight(5)
        );
    }

    private AbstractWidget createSortControls() {
        return Widgets.dropdown(sort, List.of(SortMode.values()), SortMode::displayName,
                button -> button.withSize(sidebarWidth, CONTROL_HEIGHT).withTexture(BUTTON),
                dropdown -> dropdown
                        .withTexture(DROPDOWN)
                        .withEntrySprites(BUTTON_SOLID)
                        .withSize(sidebarWidth, DROPDOWN_HEIGHT)
                        .withCallback(_ -> rebuildResults())
        );
    }

    private AbstractWidget createBeeTabs() {
        return Widgets.radio(section, radio -> radio
                        .withOptions(List.of(BeeSection.values()))
                        .withSize(contentWidth - 10, CONTROL_HEIGHT)
                        .withGap(2)
                        .withEntrySprites(BUTTON)
                        .withRenderer((value, _) -> WidgetRenderers.text(sectionName(value))) //(graphics, widget, _) -> graphics.item(sectionIcon(value), widget.getX(), widget.getY()))
                        .withCallback(_ -> rebuildContent()),
                _ -> {}
        );
    }

    private ItemStack sectionIcon(BeeSection section) {
        return switch (section) {
            case OVERVIEW -> new ItemStack(Items.BOOK);
            case PRODUCTION -> new ItemStack(Items.HONEYCOMB);
            case BREEDING -> new ItemStack(Items.BEE_SPAWN_EGG);
            case TRAITS -> new ItemStack(Items.NETHER_STAR);
        };
    }

    private Component sectionName(BeeSection section) {
        return switch (section) {
            case OVERVIEW -> Component.literal("Overview").withColor(TextColor.WHITE);
            case PRODUCTION -> Component.literal("Production").withColor(TextColor.WHITE);
            case BREEDING -> Component.literal("Breeding").withColor(TextColor.WHITE);
            case TRAITS -> Component.literal("Traits").withColor(TextColor.WHITE);
        };
    }

    private void buildHome(LinearViewLayout layout) {
        FrameLayout home = new FrameLayout(contentWidth, contentHeight);

        LinearLayout branding = LinearLayout.vertical().spacing(3);

        FrameLayout titleRow = new FrameLayout(contentWidth, 9);
        titleRow.addChild(Widgets.text(Component.literal("Resourceful Bees")), LayoutSettings::alignHorizontallyCenter);

        FrameLayout logoRow = new FrameLayout(contentWidth, 16);
        logoRow.addChild(Widgets.renderable((graphics, widget, _) -> graphics.blitSprite(RenderPipelines.GUI_TEXTURED, LOGO, widget.getX() + (widget.getWidth() - 104) / 2, widget.getY(), 104, 16), widget -> widget.setSize(contentWidth, 16)));

        FrameLayout versionRow = new FrameLayout(contentWidth, 9);
        versionRow.addChild(Widgets.text(Component.literal("Beepedia v3.0")), LayoutSettings::alignHorizontallyCenter);

        branding.addChild(titleRow);
        branding.addChild(logoRow);
        branding.addChild(versionRow);

        home.addChild(branding, LayoutSettings::alignVerticallyMiddle);
        home.addChild(beesFoundWidget(), settings -> settings.alignHorizontallyRight().alignVerticallyBottom().paddingRight(8));

        layout.withChild(home);
    }

    private AbstractWidget beesFoundWidget() {
        long discovered = BeeRegistry.get().getStreamOfBees().filter(this::isDiscovered).count();
        long total = BeeRegistry.get().getStreamOfBees().count();

        return Widgets.text(Component.literal("Bees Found: " + discovered + " / " + total + "  "), TextWidget::withRightAlignment); //two spaces at the end are intentional padding bc padding right doesnt work in this case
    }

    private void buildMissingEntry(LinearViewLayout layout, String id) {
        layout.withGap(6);

        layout.withChild(Widgets.text(Component.literal("Entry Not Found"), text -> text.withShadow().withLeftAlignment()));
        layout.withChild(Widgets.textarea(Component.literal("The Beepedia entry \"" + id + "\" is no longer available."), contentWidth));
    }

    private void buildTraitPage(LinearViewLayout layout, String id) {
        Trait trait = TraitRegistry.get().getTrait(id);

        if (trait == null) {
            buildMissingEntry(layout, id);
            return;
        }

        TraitOverview.build(layout, trait, contentWidth);
    }

    private void buildHoneyPage(LinearViewLayout layout, String id) {
        var honey = HoneyRegistry.get().getOptionalHoneyData(id);

        honey.ifPresentOrElse(data -> HoneyOverview.build(layout, data, contentWidth), () -> buildMissingEntry(layout, id));
    }

    @Override
    public void tick() {
        super.tick();

        children().forEach(BeepediaScreen::tickWidget);
    }

    private static void tickWidget(GuiEventListener listener) {
        if (listener instanceof BeepediaTickable tickable) {
            tickable.tick();
        }

        if (listener instanceof ContainerEventHandler container) {
            container.children().forEach(BeepediaScreen::tickWidget);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean isInGameUi() {
        return true;
    }

    public static void open(boolean creative) {
        Minecraft.getInstance().gui.setScreen(new BeepediaScreen(creative));
    }

    @Override
    public void openBee(Identifier id) {
        select(new BeepediaSelection.Bee(id));
    }

    @Override
    public void openTrait(String id) {
        select(new BeepediaSelection.Trait(id));
    }

    @Override
    public void openHoney(String id) {
        select(new BeepediaSelection.Honey(id));
    }
}
