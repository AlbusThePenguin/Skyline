package me.albusthepenguin.skyline.Grappler;

import lombok.Getter;
import me.albusthepenguin.skyline.Config.ConfigType;
import me.albusthepenguin.skyline.Misc.Cooldowns;
import me.albusthepenguin.skyline.Misc.Data;
import me.albusthepenguin.skyline.Skyline;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Handler {

    @Getter
    private final Map<UUID, Data> arrows = new HashMap<>();
    private final Skyline skyline;
    @Getter
    private final Grappler grappler;
    @Getter
    private final Cooldowns cooldowns;

    public Handler(Skyline skyline) {
        this.skyline = skyline;
        this.grappler = new Grappler(skyline, skyline.getMessage());
        this.cooldowns = new Cooldowns();
    }

    /**
     * Helper to get the configuration section safely.
     */
    private ConfigurationSection getSettings() {
        ConfigurationSection section = skyline.getConfiguration().getConfig(ConfigType.Config).getConfigurationSection("Settings");
        if (section == null) {
            throw new IllegalArgumentException("Could not use Skyline because the config.yml is invalid.");
        }
        return section;
    }

    /**
     * Handles the player being launched using the grappling hook.
     */
    public void yeetPlayer(Player player, UUID arrowUUID, Location playerLocation, Location arrowLocation, int power) {
        Vector path = arrowLocation.toVector().subtract(playerLocation.toVector()).normalize();
        ConfigurationSection section = getSettings();

        float velocity = (float) section.getDouble("velocity");
        float increment = (float) section.getDouble("velocity_increment");
        float multiplier = velocity + (increment * power);

        path.setY(path.getY() * 0.7f);
        player.setVelocity(path.multiply(multiplier));

        arrows.remove(arrowUUID);
    }

    /**
     * Launches the grappling hook arrow and applies necessary cooldowns, particles, and sounds.
     */
    public void yeetArrow(Player player, ItemStack itemStack) {
        Location location = player.getEyeLocation();
        World world = location.getWorld();
        if (world == null) return;

        Vector direction = player.getEyeLocation().getDirection();
        int power = grappler.power(itemStack);

        ConfigurationSection section = getSettings();
        float speed = (float) section.getDouble("speed");
        float increment = (float) section.getDouble("speed_increment");
        float finalSpeed = speed + (increment * power);
        float spread = 0.0f;

        // Spawn arrow
        Arrow arrow = world.spawnArrow(location, direction, finalSpeed, spread, Arrow.class);

        // Display particles
        if (section.getBoolean("particles")) {
            Particle.DustTransition dustOptions = new Particle.DustTransition(Color.fromRGB(50, 155, 168), Color.WHITE, 2);
            int startOffset = 5;
            for (int i = startOffset; i < 10 + startOffset; i++) {
                Location particleLocation = location.clone().add(direction.clone().multiply(i));
                player.spawnParticle(Particle.DUST_COLOR_TRANSITION, particleLocation, 1, dustOptions);
            }
        }

        // Set cooldown
        long cooldownMs = section.getLong("cooldown") * 1000;
        int ticks = (int) (cooldownMs / 50);
        Material material = getValidMaterial(section.getString("material"));

        player.setCooldown(material, ticks);
        cooldowns.set(player, cooldownMs);

        // Store arrow data
        Data data = new Data(player, power);
        arrows.put(arrow.getUniqueId(), data);

        // Play sound
        String soundName = section.getString("use_sound");
        if (soundName != null && !soundName.equalsIgnoreCase("NONE")) {
            try {
                Sound sound = Sound.valueOf(soundName.toUpperCase());
                player.playSound(location, sound, 1f, 1f);
            } catch (IllegalArgumentException e) {
                skyline.getLogger().warning("Invalid sound name in config: " + soundName);
            }
        }
    }

    /**
     * Helper to get and validate a material.
     */
    private Material getValidMaterial(String materialName) {
        if (materialName == null) {
            throw new IllegalArgumentException("'Settings.material' in config.yml is null.");
        }
        Material material = Material.getMaterial(materialName);
        if (material == null) {
            throw new IllegalArgumentException("'Settings.material' in config.yml is not a valid material.");
        }
        return material;
    }
}
