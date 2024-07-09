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
package me.albusthepenguin.skyline.Hook;

import me.albusthepenguin.skyline.Skyline;
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
                    String message = skyline.getMessage("cooldown")
                            .replace("%seconds%", String.valueOf(hookHandler.getTimeleft(player)));
                    player.sendTitle("", skyline.color(message), 15, 15, 15);
                    return;
                }
                hookHandler.sendArrow(player, item);
            }
        }
    }

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