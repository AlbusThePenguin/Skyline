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
import me.albusthepenguin.skyline.API.ConfigType;
import me.albusthepenguin.skyline.Commands.CommandManager;
import me.albusthepenguin.skyline.Hook.HookHandler;
import me.albusthepenguin.skyline.Hook.HookListener;
import me.albusthepenguin.skyline.Misc.Configuration;
import me.albusthepenguin.skyline.Misc.Debug;
import net.md_5.bungee.api.ChatColor;
import org.bstats.bukkit.Metrics;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class Skyline extends JavaPlugin {

    private Debug debug;

    private Configuration configuration;

    private HookHandler hookHandler;

    private final String usePermission = "skyline.use";

    private final String adminPermission = "skyline.admin";

    @Override
    public void onEnable() {
        debug = new Debug(this);

        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        configuration = new Configuration(this);
        configuration.load();

        hookHandler = new HookHandler(this);

        PluginCommand skylineCommand = getCommand("skyline");
        if(skylineCommand != null) {
            skylineCommand.setExecutor(new CommandManager(this));
        }

        getServer().getPluginManager().registerEvents(new HookListener(this, hookHandler), this);

        new Metrics(this, 22149);
    }

    public String getMessage(String path) {
        ConfigurationSection section = configuration.getConfig(ConfigType.Messages).getConfigurationSection("Messages");
        if(section == null) {
            debug.write("[NullPointer] section in 'getMessage' returns null. Please notify developer.");
            return "";
        }

        String message = section.getString(path);
        if(message == null) {
            debug.write("[NullPointer] " + path + " is not available in messages.yml. Please add " + path + ": <message> in messages.yml to correct this.");
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
