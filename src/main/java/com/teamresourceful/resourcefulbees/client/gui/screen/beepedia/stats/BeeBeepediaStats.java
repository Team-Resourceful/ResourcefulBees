package com.teamresourceful.resourcefulbees.client.gui.screen.beepedia.stats;

import com.teamresourceful.resourcefulbees.api.beedata.CustomBeeData;
import com.teamresourceful.resourcefulbees.client.gui.widget.BeepediaListButton;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class BeeBeepediaStats extends BeepediaStats {

    private final CustomBeeData beeData;
    private Entity bee = null;
    private List<String> searchBiomes = new LinkedList<>();
    private List<String> searchBeeTags = new LinkedList<>();
    private List<String> searchTraits = new LinkedList<>();
    private List<String> searchBees = new LinkedList<>();
    private List<String> searchExtra = new LinkedList<>();
    private List<String> searchItems = new LinkedList<>();
    private List<String> searchEntity = new LinkedList<>();
    private List<String> searchAll = new LinkedList<>();

    BeepediaListButton button;

    /**
     * Generates all of the valid search values for each bee
     */
    public BeeBeepediaStats(CustomBeeData beeData) {
        this.beeData = beeData;
        initSearchTerms();
        // reduce lists
        searchBees = searchBees.stream().distinct().collect(Collectors.toList());
        searchEntity = searchEntity.stream().distinct().collect(Collectors.toList());
        searchItems = searchItems.stream().distinct().collect(Collectors.toList());
        searchBiomes = searchBiomes.stream().distinct().collect(Collectors.toList());
        searchBeeTags = searchBeeTags.stream().distinct().collect(Collectors.toList());
        searchTraits = searchTraits.stream().distinct().collect(Collectors.toList());
        searchExtra = searchExtra.stream().distinct().collect(Collectors.toList());
        // set up prefixless searching
        searchAll.addAll(searchBees);
        searchAll.addAll(searchEntity);
        searchAll.addAll(searchItems);
        searchAll.addAll(searchBiomes);
        searchAll.addAll(searchBeeTags);
        searchAll.addAll(searchTraits);
        searchAll.addAll(searchExtra);
        searchAll = searchAll.stream().distinct().collect(Collectors.toList());
    }

    public Entity getBee() {
        if (Minecraft.getInstance().level == null) throw new IllegalStateException("Attempted to get bee entity before minecraft loaded");
        if (bee == null) {
            bee = beeData.getEntityType().create(Minecraft.getInstance().level);
        }
        return bee;
    }

    @Override
    public void initSearchTerms() {
        //TODO consolidate all of the bee search stuff into here.
    }

    public boolean isValidSearch(String search){
        String[] params = search.split(" ");
        for (String param : params) {
            param = param.trim();
            switch (param.charAt(0)) {
                case '$':
                    if (!getSearch(searchBees, param.replaceFirst("\\$", ""))) return false;
                    break;
                case '#':
                    if (!getSearch(searchBeeTags, param.replaceFirst("#", ""))) return false;
                    break;
                case '!':
                    if (!getSearch(searchTraits, param.replaceFirst("!", ""))) return false;
                    break;
                case '&':
                    if (!getSearch(searchItems, param.replaceFirst("&", ""))) return false;
                    break;
                case '@':
                    if (!getSearch(searchBiomes, param.replaceFirst("@", ""))) return false;
                    break;
                case '-':
                    if (!getSearch(searchEntity, param.replaceFirst("-", ""))) return false;
                    break;
                default:
                    if (!getSearch(searchAll, param)) return false;
                    break;
            }
        }
        return true;
    }
}
