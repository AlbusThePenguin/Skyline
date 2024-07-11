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
package me.albusthepenguin.skyline.Hook;

import me.albusthepenguin.skyline.API.ConfigType;
import me.albusthepenguin.skyline.Skyline;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class HookListener implements Listener {

    private final Skyline skyline;

    private final HookHandler hookHandler;

    public HookListener(Skyline skyline, HookHandler hookHandler) {
        this.skyline = skyline;
        this.hookHandler = hookHandler;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if(event.getAction() == Action.RIGHT_CLICK_AIR) {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (!item.hasItemMeta()) return;

            if (hookHandler.isHook(item)) {
                if (!player.hasPermission(skyline.getUsePermission())) {
                    player.sendMessage(skyline.color(skyline.getMessage("error_permission")));
                    return;
                }

                if (hookHandler.hasCooldown(player)) {

                    List<String> types = skyline.getConfiguration().getConfig(ConfigType.Config).getStringList("Settings.cooldown_messages");

                    String message = skyline.color(
                            skyline.getMessage("cooldown")
                                    .replace("%seconds%", String.valueOf(hookHandler.getTimeleft(player)))
                    );

                    if(types.contains("CHAT")) {
                        player.sendMessage(message);
                    }

                    if(types.contains("TITLE")) {
                        player.sendTitle("", message, 15, 15, 15);
                    }

                    if(types.contains("ACTIONBAR")) {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(message));
                    }
                    return;
                }
                hookHandler.sendArrow(player, item);
            }
        }
    }

    @SuppressWarnings("all")
    @EventHandler
    public void onLeash(PlayerLeashEntityEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if(itemStack.hasItemMeta() && hookHandler.isHook(itemStack)) {
            event.setCancelled(true);
            player.updateInventory(); //This is a version issue.
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player && event.getCause() == EntityDamageEvent.DamageCause.FALL && hookHandler.getCooldown().containsKey(player)) {
            long cooldownEndTime = hookHandler.getCooldown(player);
            long currentTime = System.currentTimeMillis();

            if (currentTime > cooldownEndTime + 10000) {
                hookHandler.getCooldown().remove(player);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void projectileHit(ProjectileHitEvent event) {
        if (event.isCancelled()) return;

        if (event.getEntity() instanceof Arrow arrow) {
            UUID uuid = arrow.getUniqueId();
            HookData data = hookHandler.getArrows().getOrDefault(uuid, null);
            if (data == null) return;

            Player player = data.player();
            if (player == null) return;


            Location arrowLocation = arrow.getLocation();
            Location playerLocation = player.getLocation();

            hookHandler.sendPlayer(player, uuid, arrowLocation, playerLocation, data.power());
            arrow.remove();
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        hookHandler.getCooldown().remove(event.getPlayer());
    }
}