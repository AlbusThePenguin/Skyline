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
package me.albusthepenguin.skyline.Hooks;

import lombok.NonNull;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

public class PlaceholderAPIHook {

    /**
     * Replaces both custom placeholders and PlaceholderAPI placeholders
     * in the given message.
     *
     * @param message The message containing placeholders.
     * @param offlinePlayer The player context for PlaceholderAPI placeholders, or null for global context.
     * @return The message with all placeholders replaced.
     */
    public String set(@NonNull String message, @Nullable OfflinePlayer offlinePlayer) {
        return PlaceholderAPI.setPlaceholders(offlinePlayer, message);
    }
}