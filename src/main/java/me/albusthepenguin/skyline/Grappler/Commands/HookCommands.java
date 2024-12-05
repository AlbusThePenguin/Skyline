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

import me.albusthepenguin.skyline.Commands.MinecraftCommand;
import me.albusthepenguin.skyline.Commands.MinecraftSubCommand;
import me.albusthepenguin.skyline.Skyline;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class HookCommands extends MinecraftCommand {

    private final ArrayList<MinecraftSubCommand> subcommands = new ArrayList<>();

    /**
     * Constructor for creating a new command.
     *
     * @param skyline       the plugin.
     * @param name         The name of the command.
     * @param permission   The permission for the index command. For sub commands a player will need both this + the sub command permission.
     * @param description  The description of the command.
     * @param usageMessage The usage message for the command.
     * @param aliases      A list of aliases for the command.
     */
    public HookCommands(@Nonnull Skyline skyline, @Nonnull String name, @Nonnull String permission, @Nonnull String description, @Nonnull String usageMessage, @Nonnull List<String> aliases) {
        super(skyline, name, permission, description, usageMessage, aliases);

        this.subcommands.add(new GiveHookCommand(skyline));
        this.subcommands.add(new ReloadConfigurations(skyline));

        this.register(super.getSkyline());
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
        if (args.length > 0 && !subcommands.isEmpty()) {
            for (MinecraftSubCommand subCommand : subcommands) {
                // If the command matches, handle it
                if (args[0].equalsIgnoreCase(subCommand.getName())) {
                    if (sender instanceof Player player) {
                        if (player.hasPermission(subCommand.getPermission())) {
                            subCommand.perform(player, args);
                        } else {
                            player.sendMessage(super.getMessage().get("error_permission", null, true));
                        }
                        return true; // Command handled successfully
                    } else if (sender instanceof ConsoleCommandSender consoleCommandSender) {
                        subCommand.perform(consoleCommandSender, args);
                        return true; // Command handled successfully
                    }
                }
            }
            return false;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String alias, @Nonnull String[] args) {
        if(sender instanceof Player player) {
            if (!player.hasPermission(Objects.requireNonNull(getPermission()))) {
                return Collections.emptyList();
            }

            List<String> arguments = new ArrayList<>();

            if (args.length == 0) {
                return arguments;
            }

            for (MinecraftSubCommand subCommand : subcommands) {
                if (subCommand == null) {
                    throw new IllegalArgumentException("Failed to execute '" + args[0] + "' because sub command is null.");
                }

                if (!player.hasPermission(subCommand.getPermission())) {
                    continue;
                }

                if (args.length == 1) {
                    arguments.add(subCommand.getName());
                } else if (args[0].equalsIgnoreCase(subCommand.getName())) {
                    List<String> subArgs = subCommand.getSubcommandArguments(player, args);
                    arguments = subArgs != null ? subArgs : Collections.emptyList();
                    break;
                }
            }

            return arguments;
        }
        return Collections.emptyList();
    }
}
