package me.albusthepenguin.skyline.Grappler;

import me.albusthepenguin.skyline.Config.ConfigType;
import me.albusthepenguin.skyline.Config.Message;
import me.albusthepenguin.skyline.Skyline;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

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
    public boolean valid(@Nonnull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return false;

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        return container.has(this.key);
    }

    /**
     *
     * @param itemStack the item in question
     * @return the power of the item.
     */
    public int power(@Nonnull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return 0;

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        return container.getOrDefault(this.key, PersistentDataType.INTEGER, 0);
    }

    public void create(Player player, int power) {
        ConfigurationSection section = skyline.getConfiguration().getConfig(ConfigType.Config).getConfigurationSection("Settings");

        if(section == null) {
            throw new IllegalArgumentException("Could not create a skyline cause the config.yml is invalid. Please delete config.yml and backup the old one.");
        }

        String materialName = section.getString("material");
        if(materialName == null) {
            throw new IllegalArgumentException("'Settings.material' in config.yml is null.");
        }

        Material material = Material.getMaterial(materialName);
        if(material == null) {
            throw new IllegalArgumentException("'Settings.material' in config.yml is not a valid material.");
        }

        String display = section.getString("display");
        if(display == null) {
            throw new IllegalArgumentException("'Settings.display' in config.yml is null.");
        }

        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        assert itemMeta != null;

        int model = section.getInt("model", 0);
        itemMeta.setCustomModelData(model);

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        container.set(this.key, PersistentDataType.INTEGER, power);

        List<String> lore = section.getStringList("lore").stream()
                .map(s -> message.setPlaceholders(s, Map.of("%power%", String.valueOf(power)), player))
                .map(skyline::color)
                .toList();

        itemMeta.setLore(lore);

        itemMeta.setDisplayName(skyline.color(display));

        itemStack.setItemMeta(itemMeta);

        player.getInventory().addItem(itemStack);
    }
}
