/*
 * This file is part of Skyline, licensed under the MIT License.
 *
 *  Copyright (c) AlbusThePenguin (Albus) <SlapTheTroll@Spigot>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
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
