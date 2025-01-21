package me.albusthepenguin.api.commands;

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

import lombok.Getter;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * Abstract base class representing a subcommand for a Bukkit plugin command.
 * <p>
 * Subclasses of this class should define the specific behavior, permissions,
 * and argument handling for each subcommand.
 */
@Getter
public abstract class MinecraftSubCommand {

    private final Plugin plugin;

    protected MinecraftSubCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Gets the name of the subcommand.
     * <p>
     * This is the identifier used to trigger the subcommand (e.g., "/command <subcommand>").
     *
     * @return The name of the subcommand.
     */
    public abstract String getName();

    /**
     * Gets the permission required to execute the subcommand.
     * <p>
     * This permission string is used to check if the player has the necessary rights
     * to use the subcommand.
     *
     * @return The permission string required to execute the subcommand, or null if no permission is required.
     */
    public abstract String getPermission();

    /**
     * Gets the syntax or usage message for the subcommand.
     * <p>
     * This syntax string typically explains how to use the subcommand and what arguments it expects.
     *
     * @return The syntax message for the subcommand.
     */
    public abstract String getSyntax();

    /**
     * Executes the subcommand when invoked by a player.
     * <p>
     * This method contains the logic to be executed when the subcommand is run by a player.
     *
     * @param player The player who executed the subcommand.
     * @param args   The arguments passed to the subcommand.
     */
    public abstract void perform(Player player, String[] args);

    /**
     * Executes the subcommand when invoked by the console.
     * <p>
     * This method contains the logic to be executed when the subcommand is run by the console.
     *
     * @param console The console command sender who executed the subcommand.
     * @param args    The arguments passed to the subcommand.
     */
    public abstract void perform(ConsoleCommandSender console, String[] args);

    /**
     * Provides tab completion suggestions for the subcommand's arguments.
     * <p>
     * This method returns a list of suggested completions based on the current input provided
     * by the player.
     *
     * @param player The player who is typing the command.
     * @param args   The arguments that have been typed so far.
     * @return A list of suggested completions for the subcommand's arguments.
     */
    public abstract List<String> getSubcommandArguments(Player player, String[] args);
}