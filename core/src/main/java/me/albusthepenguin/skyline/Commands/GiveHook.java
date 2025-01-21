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

import me.albusthepenguin.api.commands.MinecraftSubCommand;
import me.albusthepenguin.skyline.Configs.Message;
import me.albusthepenguin.skyline.Skyline;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GiveHook extends MinecraftSubCommand {

    private final Skyline skyline;

    private final Message message;

    protected GiveHook(Skyline skyline, Message message) {
        super(skyline);
        this.skyline = skyline;
        this.message = message;
    }



    @Override
    public String getName() {
        return "give";
    }

    @Override
    public String getPermission() {
        return this.skyline.getAdminPermission();
    }

    @Override
    public String getSyntax() {
        return "/" + this.skyline.getCommandLabel() + " give <power> <player>";
    }

    @Override
    public void perform(Player player, String[] args) {
        if (args.length < 2 || args.length > 3) {
            player.sendMessage(this.message.get("error_syntax", Map.of("{syntax}", getSyntax()), null,true));
            return;
        }

        Player target = getTargetPlayer(player, args);
        if (target == null) {
            player.sendMessage(this.message.get("error_player", null, null,true));
            return;
        }

        Integer value = getValueFromArgs(args[1]);
        if (value == null) {
            player.sendMessage(this.message.get("error_syntax", Map.of("{syntax}", getSyntax()), null,true));
            return;
        }

        this.skyline.getHandler().getGrappler().createGrappler(target, value);
        player.sendMessage(this.message.get("success_given", Map.of("{player}", target.getName()), null, true));
    }

    @Override
    public void perform(ConsoleCommandSender console, String[] args) {
        if(args.length != 3) {
            console.sendMessage("Invalid syntax: " + getSyntax());
            return;
        }

        String playerName = args[2];
        Player player = Bukkit.getPlayer(playerName);
        if(player == null) {
            console.sendMessage("Cannot find " + playerName + " online.");
            return;
        }

        Integer value = getValueFromArgs(args[1]);
        if (value == null) {
            console.sendMessage("Invalid syntax: " + getSyntax());
            return;
        }

        this.skyline.getHandler().getGrappler().createGrappler(player, value);
        console.sendMessage("You gave " + playerName + " a skyline.");
    }

    private Player getTargetPlayer(Player player, String[] args) {
        if (args.length == 2) {
            return player;
        }
        return Bukkit.getPlayer(args[2]); // Retrieves target player by name
    }

    private Integer getValueFromArgs(String arg) {
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            return null;
        }
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
