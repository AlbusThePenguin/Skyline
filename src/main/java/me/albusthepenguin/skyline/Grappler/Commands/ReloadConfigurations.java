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
package me.albusthepenguin.skyline.Grappler.Commands;

import me.albusthepenguin.skyline.Config.ConfigType;
import me.albusthepenguin.skyline.Commands.MinecraftSubCommand;
import me.albusthepenguin.skyline.Config.Configuration;
import me.albusthepenguin.skyline.Skyline;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class ReloadConfigurations extends MinecraftSubCommand {

    private final Configuration configuration;

    public ReloadConfigurations(Skyline skyline) {
        super(skyline);
        this.configuration = super.getSkyline().getConfiguration();
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getPermission() {
        return super.getSkyline().getAdminPermission();
    }

    @Override
    public String getSyntax() {
        return "/skyline reload";
    }

    @Override
    public void perform(Player player, String[] args) {
        if(args.length != 1) {
            player.sendMessage(super.getMessage().get("error_syntax", Map.of("%syntax%", getSyntax()), true));
            return;
        }

        player.sendMessage(super.getMessage().get("success_reload", Map.of("%syntax%", getSyntax()), true));

        for(ConfigType configType : ConfigType.values()) {
            configuration.reload(configType);
            configuration.save(configType);
            player.sendMessage(super.getMessage().get("success_reload", Map.of("%type%", configType.name()), true));
        }
    }

    @Override
    public void perform(ConsoleCommandSender console, String[] args) {
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
