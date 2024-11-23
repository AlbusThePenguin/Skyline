package me.albusthepenguin.skyline.Misc;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Cooldowns {

    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public void remove(Player player) {
        this.cooldowns.remove(player.getUniqueId());
    }

    public void set(Player player, long cooldown) {
        this.cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + cooldown);
    }

    private long get(Player player) {
        return cooldowns.getOrDefault(player.getUniqueId(), -1L);
    }

    public boolean has(Player player) {
        long cooldownEnd = get(player);
        if (cooldownEnd == -1L) {
            return false;
        }
        return System.currentTimeMillis() < cooldownEnd;
    }

    // Gets the time left on the cooldown for a player in seconds
    public int timeLeft(Player player) {
        long expirationTime = cooldowns.get(player.getUniqueId());
        long timeLeft = expirationTime - System.currentTimeMillis();

        // If timeLeft is negative, it means the cooldown has expired
        if (timeLeft <= 0) {
            return 0;  // Cooldown expired
        }

        // Return the time left in seconds
        return (int) (timeLeft / 1000);  // Convert from milliseconds to seconds
    }
}
