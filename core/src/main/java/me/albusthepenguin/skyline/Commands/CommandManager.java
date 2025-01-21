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

import lombok.NonNull;
import me.albusthepenguin.api.commands.MinecraftCommand;
import me.albusthepenguin.api.commands.MinecraftSubCommand;
import me.albusthepenguin.skyline.Configs.Message;
import me.albusthepenguin.skyline.Skyline;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CommandManager extends MinecraftCommand {

    private final Message message;

    private final ArrayList<MinecraftSubCommand> subcommands = new ArrayList<>();

    /**
     * Constructor for creating a new command.
     *
     * @param plugin       the plugin.
     * @param name         The name of the command.
     * @param permission   The permission for the index command. For sub commands a player will need both this + the sub command permission.
     * @param description  The description of the command.
     * @param usageMessage The usage message for the command.
     * @param aliases      A list of aliases for the command.
     */
    public CommandManager(@NonNull Plugin plugin, @NonNull String name, @NonNull String permission, @NonNull String description, @NonNull String usageMessage, @NonNull List<String> aliases, Skyline skyline, Message message) {
        super(plugin, name, permission, description, usageMessage, aliases);
        this.message = message;

        this.subcommands.add(new GiveHook(skyline, this.message));
        this.subcommands.add(new Reload(skyline, this.message));

        this.register(skyline);
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {
        if (args.length > 0 && !subcommands.isEmpty()) {
            for (MinecraftSubCommand subCommand : subcommands) {
                // If the command matches, handle it
                if (args[0].equalsIgnoreCase(subCommand.getName())) {
                    if (sender instanceof Player player) {
                        if (player.hasPermission(subCommand.getPermission())) {
                            subCommand.perform(player, args);
                        } else {
                            player.sendMessage(this.message.get("error_permission", null, null,true));
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
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String alias, @NonNull String[] args) {
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
