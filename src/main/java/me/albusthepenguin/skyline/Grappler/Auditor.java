package me.albusthepenguin.skyline.Grappler;

import me.albusthepenguin.skyline.Config.ConfigType;
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
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

public class Auditor implements Listener {

    private final Skyline skyline;

    private final Handler handler;

    public Auditor(Skyline skyline, Handler handler) {
        this.skyline = skyline;
        this.handler = handler;
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

        if(!handler.getGrappler().valid(itemStack)) {
            return;
        }

        if(handler.getCooldowns().has(player)) {
            List<String> types = skyline.getConfiguration().getConfig(ConfigType.Config).getStringList("Settings.cooldown_messages");

            String message = skyline.color(
                    skyline.getMessage("cooldown")
                            .replace("%seconds%", String.valueOf(handler.getCooldowns().timeLeft(player)))
            );

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
        if(itemStack.hasItemMeta() && handler.getGrappler().valid(itemStack)) {
            event.setCancelled(true);
            player.updateInventory(); //This is a version issue.
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player && event.getCause() == EntityDamageEvent.DamageCause.FALL && handler.getCooldowns().has(player)) {
            long cooldownEndTime = handler.getCooldowns().timeLeft(player);
            long currentTime = System.currentTimeMillis();

            if (currentTime > cooldownEndTime + 10000) {
                handler.getCooldowns().remove(player);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void projectileHit(ProjectileHitEvent event) {
        if (event.isCancelled()) return;

        if (event.getEntity() instanceof Arrow arrow) {
            UUID uuid = arrow.getUniqueId();
            Data data = handler.getArrows().getOrDefault(uuid, null);
            if (data == null) return;

            Player player = data.player();
            if (player == null) return;


            Location arrowLocation = arrow.getLocation();
            Location playerLocation = player.getLocation();

            handler.yeetPlayer(player, uuid, playerLocation, arrowLocation, data.power());
            arrow.remove();
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        handler.getCooldowns().remove(event.getPlayer());
    }

}
