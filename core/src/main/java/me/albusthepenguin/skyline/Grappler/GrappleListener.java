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
package me.albusthepenguin.skyline.Grappler;

import me.albusthepenguin.skyline.Configs.Message;
import me.albusthepenguin.skyline.Misc.Cooldown;
import me.albusthepenguin.skyline.Misc.Data;
import me.albusthepenguin.skyline.Skyline;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GrappleListener implements Listener {


    private final Skyline skyline;

    private final Message message;

    private final GrappleHandler handler;

    private final Cooldown cooldown;

    public GrappleListener(Skyline skyline, GrappleHandler handler) {
        this.skyline = skyline;
        this.message = this.skyline.getMessage();
        this.handler = handler;
        this.cooldown = this.handler.getCooldown();
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        ItemStack itemStack = event.getItemInHand();
        ItemMeta itemMeta = itemStack.getItemMeta();

        if(itemMeta == null) {
            return;
        }

        if(handler.getGrappler().isGrappler(itemStack)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEat(PlayerItemConsumeEvent event) {
        ItemStack itemStack = event.getItem();
        ItemMeta itemMeta = itemStack.getItemMeta();

        if(itemMeta == null) {
            return;
        }

        if(handler.getGrappler().isGrappler(itemStack)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();

        if(event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }

        ItemStack itemStack = player.getInventory().getItemInMainHand();

        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) {
            return;
        }

        if(!handler.getGrappler().isGrappler(itemStack)) {
            return;
        }

        if(!player.hasPermission(this.skyline.getUsePermission())) {
            player.sendMessage(this.message.get("error_permission", null, player, true));
            return;
        }

        UUID uuid = player.getUniqueId();

        if(this.cooldown.has(uuid)) {
            List<String> types = skyline.getConfiguration().getYamlConfiguration().getStringList("Settings.cooldown_messages");

            String message = this.message.get("cooldown", Map.of("{cooldown}", String.valueOf(this.cooldown.timeLeft(uuid))), null, true);

            if(types.contains("CHAT")) {
                player.sendMessage(message);
            }

            if(types.contains("TITLE")) {
                player.sendTitle("", message, 15, 15, 15);
            }

            if(types.contains("ACTIONBAR")) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
            }
            return;
        }

        handler.yeetArrow(player, itemStack);
    }

    @SuppressWarnings("all")
    @EventHandler
    public void onLeash(PlayerLeashEntityEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if(itemStack.hasItemMeta() && handler.getGrappler().isGrappler(itemStack)) {
            event.setCancelled(true);
            player.updateInventory(); //This is a version issue.
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            UUID uuid = player.getUniqueId();
            if(this.cooldown.has(uuid)) {
                long cooldownEndTime = this.cooldown.timeLeft(uuid);
                long currentTime = System.currentTimeMillis();

                if (currentTime > cooldownEndTime + 10000) {
                    this.cooldown.remove(uuid);
                    event.setCancelled(true);
                }
            }
        }
    }


    @EventHandler
    public void projectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow arrow) {
            UUID uuid = arrow.getUniqueId();
            Data data = handler.getArrows().getOrDefault(uuid, null);
            if (data == null) return;

            Player player = data.player();
            if (player == null) return;


            Location arrowLocation = arrow.getLocation();
            Location playerLocation = player.getLocation();

            if(data.display() != null) data.display().remove();

            handler.yeetPlayer(player, uuid, playerLocation, arrowLocation, data.power());
            arrow.remove();
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.cooldown.remove(event.getPlayer().getUniqueId());
    }
}
