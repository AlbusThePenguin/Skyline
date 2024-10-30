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
package me.albusthepenguin.skyline.Hook.Commands;

import me.albusthepenguin.skyline.API.ConfigType;
import me.albusthepenguin.skyline.API.MinecraftSubCommand;
import me.albusthepenguin.skyline.Misc.Configuration;
import me.albusthepenguin.skyline.Skyline;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ReloadConfigurations extends MinecraftSubCommand {
    private final Skyline skyline;

    private final Configuration configuration;

    public ReloadConfigurations(Skyline skyline) {
        this.skyline = skyline;
        this.configuration = skyline.getConfiguration();
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getPermission() {
        return skyline.getAdminPermission();
    }

    @Override
    public String getSyntax() {
        return "/skyline reload";
    }

    @Override
    public void perform(Player player, String[] args) {
        skyline.getLogger().info("issued console reload?");

        String syntax = skyline.getMessage("error_syntax")
                .replace("%syntax%", getSyntax());

        if(args.length != 1) {
            player.sendMessage(skyline.color(syntax));
            return;
        }

        String reloadMessage = skyline.getMessage("success_reload");

        for(ConfigType configType : ConfigType.values()) {
            configuration.reload(configType);
            configuration.save(configType);
            player.sendMessage(skyline.color(reloadMessage).replace("%type%", configType.name()));
        }
    }

    @Override
    public void perform(ConsoleCommandSender console, String[] args) {

        skyline.getLogger().info("issued console reload?");

        if(args.length != 1) {
            console.sendMessage("Incorrect syntax: " + getSyntax());
            return;
        }

        String message = "%type% has been reloaded.";
        for(ConfigType configType : ConfigType.values()) {
            configuration.reload(configType);
            configuration.save(configType);
            console.sendMessage(message.replace("%type%", configType.name()));
        }
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }
}
