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
package me.albusthepenguin.skyline.Grappler;

import lombok.Getter;
import me.albusthepenguin.api.events.SkylineSendArrowEvent;
import me.albusthepenguin.api.events.SkylineSendPlayerEvent;
import me.albusthepenguin.skyline.Misc.Cooldown;
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

public class GrappleHandler {

    @Getter
    private final Map<UUID, Data> arrows = new HashMap<>();
    private final Skyline skyline;
    @Getter
    private final Grappler grappler;
    @Getter
    private final Cooldown cooldown;

    private final GrapplerEffects grapplerEffects;

    public GrappleHandler(Skyline skyline) {
        this.skyline = skyline;
        this.grappler = new Grappler(skyline, skyline.getMessage());
        this.grapplerEffects = new GrapplerEffects();
        this.cooldown = new Cooldown();
    }

    /**
     * Helper to get the configuration section safely.
     */
    private ConfigurationSection getSettings() {
        ConfigurationSection section = skyline.getConfiguration().getYamlConfiguration().getConfigurationSection("Settings");
        if (section == null) {
            throw new IllegalArgumentException("Could not use Skyline because the config.yml is invalid.");
        }
        return section;
    }

    /**
     * Handles the player being launched using the grappling hook.
     */
    public void yeetPlayer(Player player, UUID arrowUUID, Location playerLocation, Location arrowLocation, int power) {

        SkylineSendPlayerEvent event = new SkylineSendPlayerEvent(player);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) {
            return;
        }

        Vector path = arrowLocation.toVector().subtract(playerLocation.toVector()).normalize();
        ConfigurationSection section = getSettings();

        float velocity = (float) section.getDouble("velocity", 0.3);
        float increment = (float) section.getDouble("velocity_increment", 0.3);
        float multiplier = velocity + (increment * power);

        double teleport_y = section.getDouble("teleport_y", 0.10);

        player.teleport(player.getLocation().add(0, teleport_y, 0));
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
        int power = grappler.getPower(itemStack);

        ConfigurationSection section = getSettings();

        float speed = (float) section.getDouble("speed", 0.2);
        float increment = (float) section.getDouble("speed_increment", 0.15);
        float finalSpeed = speed + (increment * power);
        float spread = 0.0f;

        // Spawn arrow
        Arrow arrow = world.spawnArrow(location, direction, finalSpeed, spread, Arrow.class);

        SkylineSendArrowEvent event = new SkylineSendArrowEvent(player, arrow);
        Bukkit.getPluginManager().callEvent(event);

        if(event.isCancelled()) {
            arrow.remove(); //Remove the arrow.
            return;
        }

        this.grapplerEffects.sendParticles(player, direction, location, section);
        this.grapplerEffects.sendSound(player, location, section);

        long cooldownMs = section.getLong("cooldown", 5) * 1000;
        int ticks = (int) (cooldownMs / 50);
        Material material = getValidMaterial(section.getString("material", "LEAD"));

        player.setCooldown(material, ticks);
        cooldown.set(player.getUniqueId(), cooldownMs);

        Data data = new Data(player, power);
        arrows.put(arrow.getUniqueId(), data);
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
