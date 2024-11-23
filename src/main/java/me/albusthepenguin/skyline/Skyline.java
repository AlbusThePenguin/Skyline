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
package me.albusthepenguin.skyline;

import lombok.Getter;
import me.albusthepenguin.skyline.Config.ConfigType;
import me.albusthepenguin.skyline.Grappler.Auditor;
import me.albusthepenguin.skyline.Grappler.Commands.HookCommands;
import me.albusthepenguin.skyline.Grappler.Handler;
import me.albusthepenguin.skyline.Config.Configuration;
import me.albusthepenguin.skyline.Config.Message;
import net.md_5.bungee.api.ChatColor;
import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

@Getter
public final class Skyline extends JavaPlugin {

    private Configuration configuration;

    private Handler handler;

    private final String usePermission = "skyline.use";

    private final String adminPermission = "skyline.admin";

    private Message message;

    private String commandLabel;
    //update.

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults(true);

        this.configuration = new Configuration(this);
        this.configuration.load();

        this.message = new Message();

        this.handler = new Handler(this);

        this.getServer().getPluginManager().registerEvents(new Auditor(this, handler), this);

        this.buildInGameCommand();

        new Metrics(this, 22149);
    }

    private void buildInGameCommand() {
        ConfigurationSection section = this.configuration.getConfig(ConfigType.Config).getConfigurationSection("Command");

        if (section == null) {
            throw new IllegalArgumentException("Could not find Commands section in config.yml. Cannot load default commands.");
        }

        this.commandLabel = section.getString("name");
        if (this.commandLabel == null || this.commandLabel.isEmpty()) {
            throw new IllegalArgumentException("The 'name' field is missing or empty in the Commands section of config.yml.");
        }

        String description = section.getString("description");
        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("The 'description' field is missing or empty in the Commands section of config.yml.");
        }

        String usageMessage = section.getString("usage");
        if (usageMessage == null || usageMessage.isEmpty()) {
            throw new IllegalArgumentException("The 'usage' field is missing or empty in the Commands section of config.yml.");
        }

        List<String> aliases = section.getStringList("aliases");

        new HookCommands(
                this, this.commandLabel, getAdminPermission(), description, usageMessage, aliases, this
        );
    }

    public String getMessage(String path) {
        ConfigurationSection section = configuration.getConfig(ConfigType.Messages).getConfigurationSection("Messages");
        if(section == null) {
            this.getLogger().warning("[NullPointer] section in 'getMessage' returns null. Please notify developer.");
            return "";
        }

        String message = section.getString(path);
        if(message == null) {
            this.getLogger().warning("[NullPointer] " + path + " is not available in messages.yml. Please add " + path + ": <message> in messages.yml to correct this.");
            return "";

        }
        return message;
    }

    public String color(String text) {
        String WITH_DELIMITER = "((?<=%1$s)|(?=%1$s))";
        String[] texts = text.split(String.format(WITH_DELIMITER, "&"));

        StringBuilder finalText = new StringBuilder();

        for (int i = 0; i < texts.length; i++) {
            if (texts[i].equalsIgnoreCase("&")) {
                i++;
                if (texts[i].charAt(0) == '#') {
                    finalText.append(ChatColor.of(texts[i].substring(0, 7))).append(texts[i].substring(7));
                } else {
                    finalText.append(ChatColor.translateAlternateColorCodes('&', "&" + texts[i]));
                }
            } else {
                finalText.append(texts[i]);
            }
        }
        return finalText.toString();
    }
}
