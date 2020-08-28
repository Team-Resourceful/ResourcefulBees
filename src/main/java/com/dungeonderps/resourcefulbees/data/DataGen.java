package com.dungeonderps.resourcefulbees.data;

import com.dungeonderps.resourcefulbees.config.BeeBuilder;
import com.dungeonderps.resourcefulbees.config.BeeInfo;
import com.dungeonderps.resourcefulbees.config.Config;
import com.dungeonderps.resourcefulbees.lib.BeeConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static com.dungeonderps.resourcefulbees.ResourcefulBees.LOGGER;

public class DataGen {
    public static void GenerateEnglishLang() {
        if (Config.GENERATE_ENGLISH_LANG.get()) {
            StringBuilder builder = new StringBuilder();
            builder.append("{\n");
            for (Map.Entry<String, BeeData> bee : BeeInfo.getBees().entrySet()) {
                if (!bee.getKey().equals(BeeConstants.DEFAULT_BEE_TYPE)) {
                    String displayName = StringUtils.replace(bee.getKey(), "_", " ");
                    displayName = WordUtils.capitalizeFully(displayName);

                    //block
                    builder.append("\"block.resourcefulbees.");
                    builder.append(bee.getKey());
                    builder.append("_honeycomb_block\" : \"");
                    builder.append(displayName);
                    builder.append(" Honeycomb Block\",\n");
                    //comb
                    builder.append("\"item.resourcefulbees.");
                    builder.append(bee.getKey());
                    builder.append("_honeycomb\" : \"");
                    builder.append(displayName);
                    builder.append(" Honeycomb\",\n");
                    //spawn egg
                    builder.append("\"item.resourcefulbees.");
                    builder.append(bee.getKey());
                    builder.append("_spawn_egg\" : \"");
                    builder.append(displayName);
                    builder.append(" Bee Spawn Egg\",\n");
                    //entity
                    builder.append("\"entity.resourcefulbees.");
                    builder.append(bee.getKey());
                    builder.append("_bee\" : \"");
                    builder.append(displayName);
                    builder.append(" Bee\",\n");
                }
            }

            builder.deleteCharAt(builder.lastIndexOf(","));
            builder.append("}");

            String langPath = BeeBuilder.RESOURCE_PATH.toString() + "/assets/resourcefulbees/lang/";
            String langFile = "en_us.json";
            try {
                Files.createDirectories(Paths.get(langPath));
                FileWriter writer = new FileWriter(Paths.get(langPath, langFile).toFile());
                writer.write(builder.toString());
                writer.close();
                LOGGER.info("Language File Generated!");
            } catch (IOException e) {
                LOGGER.error("Could not generate language file!");
                e.printStackTrace();
            }
        }
    }
}
