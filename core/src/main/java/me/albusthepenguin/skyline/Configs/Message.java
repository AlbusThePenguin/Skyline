/*
 * This file is part of Skyline.
 *
 * Skyline is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Skyline is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Skyline. If not, see <http://www.gnu.org/licenses/>.
 */
package me.albusthepenguin.skyline.Configs;

import lombok.NonNull;
import me.albusthepenguin.skyline.Hooks.PlaceholderAPIHook;
import me.albusthepenguin.skyline.Skyline;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * This class ONLY handle item display name and lore.
 */
public class Message {

    private final Skyline skyline;

    private final boolean placeholderAPIHooked;

    private final PlaceholderAPIHook placeholder;

    private String lang;

    private YamlConfiguration config;

    private File langFile;

    public Message(Skyline skyline) {

        this.skyline = skyline;

        this.lang = this.skyline.getConfiguration().getYamlConfiguration().getString("language", "en_US.yml");

        this.placeholderAPIHooked = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
        this.placeholder = this.placeholderAPIHooked ? new PlaceholderAPIHook() : null;

        this.load();
    }

    public void reload() {
        this.lang = this.skyline.getConfiguration().getYamlConfiguration().getString("language", "en_US.yml");

        this.load();
    }

    private void load() {
        File langDirectory = new File(this.skyline.getDataFolder(), "lang");

        if (!langDirectory.exists()) {
            boolean created = langDirectory.mkdirs();

            if (created) {
                ArrayList<String> files = new ArrayList<>(Arrays.asList(
                        "da_DK", "de_DE", "en_US",
                        "es_ES", "fr_FR", "no_NO",
                        "pl_PL", "ru_RU", "sv_SE"
                ));

                for (String languageName : files) {
                    File file = new File(langDirectory, languageName + ".yml"); // Use the correct directory here

                    if(!file.exists()) {
                        this.skyline.saveResource("lang" + File.separator + languageName + ".yml", false); // Save to the 'lang' folder inside dataFolder
                    }
                }
            }
        }

        if(this.lang == null) {
            throw new IllegalArgumentException("Found no valid language file configured in config.yml example: language: 'EN_US'");
        }

        this.langFile = new File(langDirectory, this.lang + ".yml");

        this.loadYaml();
    }

    private void loadYaml() {
        if(this.langFile == null || !this.langFile.exists()) {
            throw new IllegalArgumentException("The language file is null or does not exist.");
        }

        this.skyline.getLogger().info("Loaded language: " + this.lang);
        this.config = YamlConfiguration.loadConfiguration(this.langFile);
    }

    public String get(@NonNull String path, @Nullable Map<String, String> placeholders, @Nullable Player player, boolean colored) {
        if(path.equalsIgnoreCase("") || path.equalsIgnoreCase(" ")) return path;
        String message = this.config.getString(path);
        if(message == null) {
            message = path;
        }

        if(this.placeholderAPIHooked) {
            message = this.placeholder.set(message, player);
        }

        if(placeholders != null && !placeholders.isEmpty()) {
            for(Map.Entry<String, String> entry : placeholders.entrySet()) {
                if(message.contains(entry.getKey())) {
                    message = message.replace(entry.getKey(), entry.getValue());
                }
            }
        }

        return colored ? this.color(message) : message;
    }

    public String color(String text) {
        String WITH_DELIMITER = "((?<=%1$s)|(?=%1$s))";
        String[] texts = text.split(String.format(WITH_DELIMITER, "&"));

        StringBuilder finalText = new StringBuilder();

        for (int i = 0; i < texts.length; i++) {
            if (texts[i].equalsIgnoreCase("&")) {
                i++;
                if (texts[i].charAt(0) == '#') {
                    finalText.append(ChatColor.of(texts[i].substring(0, 7))).append(texts[i].substring(7));
                } else {
                    finalText.append(ChatColor.translateAlternateColorCodes('&', "&" + texts[i]));
                }
            } else {
                finalText.append(texts[i]);
            }
        }
        return finalText.toString();
    }
}