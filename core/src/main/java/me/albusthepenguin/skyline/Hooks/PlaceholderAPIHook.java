package me.albusthepenguin.skyline.Hooks;

import lombok.NonNull;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;

public class PlaceholderAPIHook {

    /**
     * Replaces both custom placeholders and PlaceholderAPI placeholders
     * in the given message.
     *
     * @param message The message containing placeholders.
     * @param offlinePlayer The player context for PlaceholderAPI placeholders, or null for global context.
     * @return The message with all placeholders replaced.
     */
    public String set(@NonNull String message, @NonNull OfflinePlayer offlinePlayer) {
        return PlaceholderAPI.setPlaceholders(offlinePlayer, message);
    }
}