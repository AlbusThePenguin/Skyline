package me.albusthepenguin.skyline.Hooks;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PlaceholderAPIHook {

    /**
     * Replaces both custom placeholders and PlaceholderAPI placeholders
     * in the given message.
     *
     * @param message The message containing placeholders.
     * @param offlinePlayer The player context for PlaceholderAPI placeholders, or null for global context.
     * @return The message with all placeholders replaced.
     */
    public String set(@Nonnull String message, @Nullable OfflinePlayer offlinePlayer) {
        return PlaceholderAPI.setPlaceholders(offlinePlayer, message);
    }
}