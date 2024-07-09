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
package me.albusthepenguin.skyline.Commands;

import lombok.Getter;
import me.albusthepenguin.skyline.API.SubCommand;
import me.albusthepenguin.skyline.Skyline;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class CommandManager implements TabExecutor {

    private final Skyline skyline;

    private final ArrayList<SubCommand> subcommands = new ArrayList<>();

    public CommandManager(Skyline skyline){
        this.skyline = skyline;

        subcommands.add(new GiveCommand(skyline));
        subcommands.add(new ReloadCommand(skyline));
    }
    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, String[] args) {
        if(args.length > 0) {
            for(int i = 0; i < getSubcommands().size(); i++) {
                if(args[0].equalsIgnoreCase(getSubcommands().get(i).getName())) {
                    if(sender instanceof Player player) {
                        String permission = getSubcommands().get(i).getPermission();
                        if(player.hasPermission(permission) || player.isOp()) {
                            subcommands.get(i).perform(player, args);
                        } else {
                            player.sendMessage(skyline.color(skyline.getMessage("error_permission")));
                        }
                    } else if(sender instanceof ConsoleCommandSender console) {
                        subcommands.get(i).perform(console, args);
                    }
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String alias, @Nonnull String[] args) {
        if (sender instanceof Player player) {
            List<String> arguments = new ArrayList<>();
            if (args.length == 1) {
                for (int i = 0; i < getSubcommands().size(); i++) {
                    if (player.hasPermission(getSubcommands().get(i).getPermission())) {
                        arguments.add(getSubcommands().get(i).getName());
                    }
                }
            } else if (args.length > 1) {
                for (int i = 0; i < getSubcommands().size(); i++) {
                    if (args[0].equalsIgnoreCase(getSubcommands().get(i).getName())) {
                        if (player.hasPermission(getSubcommands().get(i).getPermission()) || player.isOp()) {
                            arguments = getSubcommands().get(i).getSubcommandArguments(player, args);
                        }
                    }
                }
            }
            return arguments;
        }
        return Collections.emptyList();
    }
}
