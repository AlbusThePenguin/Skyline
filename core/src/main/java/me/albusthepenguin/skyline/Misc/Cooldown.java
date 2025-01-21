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
package me.albusthepenguin.skyline.Misc;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Cooldown {

    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public void remove(UUID uuid) {
        this.cooldowns.remove(uuid);
    }

    public void set(UUID uuid, long cooldown) {
        this.cooldowns.put(uuid, System.currentTimeMillis() + cooldown);
    }

    private long get(UUID uuid) {
        return cooldowns.getOrDefault(uuid, -1L);
    }

    public boolean has(UUID uuid) {
        long cooldownEnd = get(uuid);
        if (cooldownEnd == -1L) {
            return false;
        }
        return System.currentTimeMillis() < cooldownEnd;
    }

    public int timeLeft(UUID uuid) {
        long expirationTime = cooldowns.get(uuid);
        long timeLeft = expirationTime - System.currentTimeMillis();

        // If timeLeft is negative, it means the cooldown has expired
        if (timeLeft <= 0) {
            return 0;  // Cooldown expired
        }

        // Return the time left in seconds
        return (int) (timeLeft / 1000);  // Convert from milliseconds to seconds
    }
}