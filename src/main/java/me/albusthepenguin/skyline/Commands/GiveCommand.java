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

import me.albusthepenguin.skyline.API.SubCommand;
import me.albusthepenguin.skyline.Skyline;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GiveCommand extends SubCommand {

    private final Skyline skyline;

    public GiveCommand(Skyline skyline) {
        this.skyline = skyline;
    }

    @Override
    public String getName() {
        return "give";
    }

    @Override
    public String getPermission() {
        return skyline.getAdminPermission();
    }

    @Override
    public String getSyntax() {
        return "/skyline give <power> <player>";
    }

    @Override
    public void perform(Player player, String[] args) {
        String syntax = skyline.getMessage("error_syntax")
                .replace("%syntax%", getSyntax());

        Player target = null;

        if(args.length == 2) {
            target = player;
        } else if(args.length == 3) {
            String playerName = args[2];
            target = Bukkit.getPlayer(playerName);
        } else {
            player.sendMessage(skyline.color(syntax));
        }

        if(target == null) {
            assert player != null;
            player.sendMessage(skyline.color(skyline.getMessage("error_player")));
            return;
        }

        int value;

        try {
            value = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(skyline.color(syntax));
            return;
        }

        player.sendMessage(skyline.color(skyline.getMessage("success_given").replace("%player%", target.getName())));
        skyline.getHookHandler().giveHook(target, value);
    }

    @Override
    public void perform(ConsoleCommandSender console, String[] args) {
        if(args.length != 3) {
            console.sendMessage("Incorrect syntax: " + getSyntax());
            console.sendMessage("As console you need an online player target.");
            return;
        }

        String playerName = args[2];
        Player player = Bukkit.getPlayer(playerName);
        if(player == null) {
            console.sendMessage("Cannot find " + playerName + " online.");
            return;
        }

        int value;

        try {
            value = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            console.sendMessage("Invalid number format: " + args[1]);
            return;
        }

        skyline.getHookHandler().giveHook(player, value);
        console.sendMessage("You gave " + playerName + " a skyline.");
    }
    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        List<String> each = new ArrayList<>();
        if(args.length == 2) {
            for (int i = 1; i <= 10; i++) {
                each.add(String.valueOf(i));
            }
        } else if(args.length == 3) {
            for(Player online : Bukkit.getOnlinePlayers()) {
                each.add(online.getName());
            }
        }
        return each;
    }
}
