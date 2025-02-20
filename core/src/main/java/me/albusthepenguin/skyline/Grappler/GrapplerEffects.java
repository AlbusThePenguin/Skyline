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

import lombok.NonNull;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

public class GrapplerEffects {

    public void sendParticles(@NonNull Player player, @NonNull Vector direction, @NonNull Location location, @NonNull ConfigurationSection section) {
        if (section.getBoolean("particles.enabled", true)) {
            int particleAmount = section.getInt("particles.amount", 1);
            int particleSize = section.getInt("particles.size", 1);

            int r = section.getInt("particles.color_r", 50);
            int g = section.getInt("particles.color_b", 155);
            int b = section.getInt("particles.color_g", 168);

            Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(r, g, b), particleSize);

            int startOffset = 5;

            for (int i = startOffset; i < 10 + startOffset; i++) {
                Location particleLocation = location.clone().add(direction.clone().multiply(i));
                player.spawnParticle(Particle.REDSTONE, particleLocation, particleAmount, dustOptions);
            }
        }
    }

    public void sendSound(@NonNull Player player, @NonNull Location location, @NonNull ConfigurationSection section) {
        String soundName = section.getString("use_sound", "ENTITY_ARROW_SHOOT");
        try {
            Sound sound = Sound.valueOf(soundName.toUpperCase());
            player.playSound(location, sound, 1f, 1f);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid sound name in config: " + soundName);
        }
    }
}
