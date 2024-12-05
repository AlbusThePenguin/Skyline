package me.albusthepenguin.skyline.Config;

import me.albusthepenguin.skyline.Hooks.PlaceholderAPIHook;
import me.albusthepenguin.skyline.Skyline;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * This class ONLY handle item display name and lore.
 */
public class Message {

    private final Configuration configuration;

    private final boolean placeholderAPIHooked;

    private final PlaceholderAPIHook placeholder;

    public Message(Skyline skyline) {
        this.configuration = skyline.getConfiguration();
        this.placeholderAPIHooked = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
        this.placeholder = this.placeholderAPIHooked ? new PlaceholderAPIHook() : null;
    }


    public String setPlaceholders(String message, @Nullable Map<String, String> placeholders, @Nullable Player player, boolean colored) {
        if(placeholderAPIHooked) {
            message = this.placeholder.set(message, player);
        }

        if(placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (key != null && value != null) {
                    message = message.replace(key, value);
                }
            }
        }

        if(colored) return this.color(message);
        return message;
    }


    public String get(@Nonnull String path, @Nullable Map<String, String> replacements, boolean colored) {
        ConfigurationSection section = this.configuration.getConfig(ConfigType.Messages).getConfigurationSection("Messages");
        if(section == null) {
            throw new IllegalArgumentException("configuration is null. Please verify that messages.yml is located in /plugins/skyline folder.");
        }

        String message = section.getString(path);
        if(message == null) {
            message = path;
        }

        if(replacements != null && !replacements.isEmpty()) {
            for(Map.Entry<String, String> entry : replacements.entrySet()) {
                if(message.contains(entry.getKey())) {
                    message = message.replace(entry.getKey(), entry.getValue());
                }
            }
        }

        if(colored) return this.color(message);
        return message;
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
