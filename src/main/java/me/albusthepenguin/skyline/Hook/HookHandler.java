/*
 * This file is part of Skyline, licensed under the MIT License.
 *
 *  Copyright (c) AlbusThePenguin (Albus) <SlapTheTroll@spigot>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package me.albusthepenguin.skyline.Hook;

import lombok.Getter;
import me.albusthepenguin.skyline.API.ConfigType;
import me.albusthepenguin.skyline.Skyline;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@SuppressWarnings("unused")
public class HookHandler {

    private final Skyline skyline;

    private final Map<Player, Long> cooldown = new ConcurrentHashMap<>();

    private final Map<UUID, HookData> arrows = new ConcurrentHashMap<>();

    public HookHandler(Skyline skyline) {
        this.skyline = skyline;
    }

    public void setCooldown(Player player, long cd) {
        cooldown.put(player, System.currentTimeMillis() + cd);
    }

    public long getCooldown(Player player) {
        if (cooldown.containsKey(player)) {
            return cooldown.get(player) - System.currentTimeMillis(); // Return remaining time
        }
        return 0;
    }

    public boolean hasCooldown(Player player) {
        if (!cooldown.containsKey(player)) return false;
        long remainingCooldown = cooldown.get(player) - System.currentTimeMillis();
        return remainingCooldown > 0; // Check if remaining time is positive
    }

    public int getTimeleft(Player player) {
        if (cooldown.containsKey(player)) {
            long remainingCooldown = cooldown.get(player) - System.currentTimeMillis();
            int remainingCooldownSeconds = (int) (remainingCooldown / 1000); // Convert to seconds
            return Math.max(1, remainingCooldownSeconds);
        }
        return 0;
    }

    private NamespacedKey powerKey() {
        return new NamespacedKey(skyline, "power");
    }

    public boolean isHook(ItemStack itemStack) {
        if(itemStack == null || !itemStack.hasItemMeta()) return false;
        ItemMeta meta = itemStack.getItemMeta();
        if(meta == null) return false;
        PersistentDataContainer container = meta.getPersistentDataContainer();
        return container.has(powerKey());
    }

    public int getPower(ItemStack itemStack) {
        if(itemStack == null || !itemStack.hasItemMeta()) return 0;
        ItemMeta meta = itemStack.getItemMeta();
        if(meta == null) return 0;
        PersistentDataContainer container = meta.getPersistentDataContainer();
        return container.getOrDefault(powerKey(), PersistentDataType.INTEGER, 0);
    }

    public void sendArrow(Player player, ItemStack item) {
        Location location = player.getEyeLocation();
        World world = location.getWorld();
        Vector direction = player.getEyeLocation().getDirection();

        int power = getPower(item);
        if (power < 1) {
            return;
        }

        ConfigurationSection section = skyline.getConfiguration().getConfig(ConfigType.Config).getConfigurationSection("Settings");
        if(section == null) return;

        float speed = (float) section.getDouble("speed");
        float increment = (float) section.getDouble("speed_increment");

        float finalSpeed = speed + (increment * power);
        float spread = 0.0f;

        if (world == null) return;
        Arrow arrow = world.spawnArrow(location, direction, finalSpeed, spread, Arrow.class);

        if(section.getBoolean("particles")) {
            Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(50, 155, 168), 2);
            int startOffset = 5;

            for (int i = startOffset; i < 10 + startOffset; i++) {
                Location particleLocation = location.clone().add(direction.clone().multiply(i));
                player.spawnParticle(Particle.REDSTONE, particleLocation, 1, dustOptions);
            }
        }

        long defaultCooldown = section.getLong("cooldown") * 1000;
        int ticks = (int) (defaultCooldown / 50);
        setCooldown(player, defaultCooldown);
        player.setCooldown(hook(1).getType(), ticks);

        HookData data = new HookData(player, power);

        String soundName = section.getString("use_sound");
        if(soundName != null && !soundName.equalsIgnoreCase("NONE")) {
            Sound sound = Sound.valueOf(soundName);
            player.playSound(location, sound, 1f, 1f);
        }

        arrows.put(arrow.getUniqueId(), data);
    }

    public ItemStack hook(int power) {
        ConfigurationSection section = skyline.getConfiguration().getConfig(ConfigType.Config).getConfigurationSection("Settings");
        if(section == null) return null;
        String materialName = section.getString("material");
        if(materialName == null) {
            skyline.getDebug().write("[NullPointer] Cannot find Settings.item material for grappling hook, please in Settings. in config.yml enter item: Material (Replace material with valid material ID).");
            return null;
        }
        Material material = Material.getMaterial(materialName);
        if(material == null) {
            skyline.getDebug().write("[NullPointer] " + materialName + " is not a valid material.");
            return null;
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if(meta == null) return null;

        int model = section.getInt("model");
        if(model > 0) {
            meta.setCustomModelData(model);
        }

        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(powerKey(), PersistentDataType.INTEGER, power);

        String display = section.getString("display");
        if(display == null) {
            skyline.getDebug().write("[NullPointer] Cannot find 'Settings.display' in config.yml. Please correct and reload the plugin.");
            return null;
        }

        List<String> lore = new ArrayList<>();

        for(String s : section.getStringList("lore")) {
            if(s.contains("%power%")) {
                s = s.replace("%power%", String.valueOf(power));
            }
            lore.add(skyline.color(s));
        }

        meta.setLore(lore);

        meta.setDisplayName(skyline.color(display));
        item.setItemMeta(meta);
        return item;
    }

    public void giveHook(Player player, int power) {
        player.getInventory().addItem(hook(power));
    }

    public void sendPlayer(Player player, UUID uuid, Location arrowLocation, Location playerLocation, int power) {
        Vector direction = arrowLocation.toVector().subtract(playerLocation.toVector()).normalize();

        ConfigurationSection section = skyline.getConfiguration().getConfig(ConfigType.Config).getConfigurationSection("Settings");
        if(section == null) return;
        float velocity = (float) section.getDouble("velocity");
        float increment = (float) section.getDouble("velocity_increment");

        Material material = hook(5).getType();

        float velocityMultiplier = velocity + (increment * power);

        // Reduce the Y component by 20-30%
        float yScaleFactor = 0.7f; // Adjust this value to control the reduction in the Y component
        direction.setY(direction.getY() * yScaleFactor);

        // Set player's velocity using the adjusted direction vector
        player.setVelocity(direction.multiply(velocityMultiplier));

        getArrows().remove(uuid);
    }
}
