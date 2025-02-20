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
import me.albusthepenguin.skyline.Configs.Message;
import me.albusthepenguin.skyline.Skyline;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class Grappler {

    private final Skyline skyline;

    private final Message message;

    private final NamespacedKey key;

    public Grappler(Skyline skyline, Message message) {
        this.skyline = skyline;
        this.message = message;

        this.key = new NamespacedKey(this.skyline, "skyline");
    }

    /**
     * @param itemStack the item in question.
     * @return if it is a valid grappling hook.
     */
    public boolean isGrappler(@NonNull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return false;

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        return container.has(this.key, PersistentDataType.INTEGER);
    }

    /**
     *
     * @param itemStack the item in question
     * @return the power of the item.
     */
    public int getPower(@NonNull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return 0;

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        return container.getOrDefault(this.key, PersistentDataType.INTEGER, 0);
    }

    /**
     * Creates the grappler
     * @param player player
     * @param power power
     * <p>Place's it in the player's inventory.</p>
     */
    public void createGrappler(Player player, int power) {
        ConfigurationSection section = skyline.getConfiguration().getYamlConfiguration().getConfigurationSection("Settings");

        if(section == null) {
            throw new IllegalArgumentException("Could not create a skyline cause the config.yml is invalid. Please delete config.yml and backup the old one.");
        }

        String materialName = section.getString("material", "LEAD");

        Material material = Material.getMaterial(materialName);

        if(material == null) {
            throw new IllegalArgumentException("'Settings.material' in config.yml is not a valid material.");
        }

        String display = section.getString("display", "&#329ba8&lSk&#55bdc9&lyli&#329ba8&lne");

        ItemStack itemStack;

        if(material == Material.PLAYER_HEAD) {
            itemStack = this.getPlayerHead(player);
        } else {
            itemStack = new ItemStack(material);
        }

        ItemMeta itemMeta = itemStack.getItemMeta();
        assert itemMeta != null;

        int model = section.getInt("model", 0);
        itemMeta.setCustomModelData(model);

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        container.set(this.key, PersistentDataType.INTEGER, power);

        List<String> lore = section.getStringList("lore").stream()
                .map(s -> message.get(s, Map.of("%power%", String.valueOf(power)), player, true))
                .toList();

        itemMeta.setLore(lore);

        itemMeta.setDisplayName(this.message.color(display));

        itemStack.setItemMeta(itemMeta);

        player.getInventory().addItem(itemStack);
    }

    private ItemStack getPlayerHead(Player player) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skull = (SkullMeta) item.getItemMeta();
        assert skull != null;
        skull.setOwningPlayer(player);
        item.setItemMeta(skull);
        return item;
    }
}