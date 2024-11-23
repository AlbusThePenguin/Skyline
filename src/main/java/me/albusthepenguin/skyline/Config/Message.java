package me.albusthepenguin.skyline.Config;

import me.albusthepenguin.skyline.Hooks.PlaceholderAPIHook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * This class ONLY handle item display name and lore.
 */
public class Message {


    private final boolean placeholderAPIHooked;

    private final PlaceholderAPIHook placeholder;

    public Message() {
        this.placeholderAPIHooked = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
        this.placeholder = this.placeholderAPIHooked ? new PlaceholderAPIHook() : null;
    }


    public String setPlaceholders(String message, @Nullable Map<String, String> placeholders, @Nullable Player player) {
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
        return message;
    }

}
