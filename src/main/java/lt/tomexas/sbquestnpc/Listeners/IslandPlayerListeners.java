package lt.tomexas.sbquestnpc.Listeners;

import com.bgsoftware.superiorskyblock.Locale;
import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import lt.tomexas.sbquestnpc.Items.QuestVillagerEgg;
import lt.tomexas.sbquestnpc.PacketsEvents.QuestNPC;
import lt.tomexas.sbquestnpc.Skyblock;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityHeadRotation;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.npc.EntityVillager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class IslandPlayerListeners implements Listener {

    private final Skyblock plugin;
    private final Map<Player, Integer> tasks = new HashMap<>();

    public IslandPlayerListeners (Skyblock plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onNPCRightClick(RightClickNPC event) {
        Player player = event.getPlayer();
        Island island = SuperiorSkyblockAPI.getPlayer(player).getIsland();
        Island islandAt = SuperiorSkyblockAPI.getIslandAt(event.getNPC().getBukkitEntity().getLocation());
        boolean bypassMode = SuperiorSkyblockAPI.getPlayer(player).hasBypassModeEnabled();

        if (island == null) return;

        if (!bypassMode && islandAt != null && !island.getOwner().equals(islandAt.getOwner())) {
            event.setCancelled(true);
            Locale.ISLAND_PROTECTED.send(player);
            return;
        }
        if (player.hasPermission("questnpc.completed"))
            if (!player.isSneaking())
                player.performCommand("quests");
            else {
                createNPCSpawnItem(player);
            }
        else {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 99999, 255, true, false));

            Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_AMBIENT, 1, 1);
                player.sendMessage("");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "\uE006   &e&lJonas &8» &fSveikas atvykęs.. Kaip aš tavęs laukiau!"));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f              Turbūt reikėtu pristatyti.. tai aš esu vardu Jonas!"));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f              Ir aš būsiu tavo kompanijonas leidžiant laiką čia"));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f              padangėse! Jei kils kokių nors klausimų prisijunk"));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f              prie mūsų discord - &e/discord&f. Mes esame pasiruošę"));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f              tau padėti, perprasti šios kelionės padangėse tikslą!"));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f              Na, o dabar linkiu tau sekmės nuotykiuose."));
                player.sendMessage("");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7                       [Spausk 'SHIFT', jog tęstum]"));
            }, 10L);

            tasks.put(player, Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, () -> {
                if (player.isSneaking()) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "lp user " + player.getName() + " permission set questnpc.completed");
                    player.removePotionEffect(PotionEffectType.SLOW);
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        PlayerConnection connection = ((CraftPlayer) p).getHandle().b;
                        connection.sendPacket(new PacketPlayOutEntityDestroy(event.getNPC().getId()));
                        event.getNPC().setCustomName(new ChatComponentText("§eKeliautojas Jonas Havaras"));
                        connection.sendPacket(new PacketPlayOutSpawnEntity(event.getNPC()));
                        connection.sendPacket(new PacketPlayOutEntityMetadata(event.getNPC().getId(), event.getNPC().getDataWatcher(), true));
                        connection.sendPacket(new PacketPlayOutEntityHeadRotation(event.getNPC(), (byte) (event.getNPC().getBukkitYaw() * 256 / 360)));
                        Bukkit.getScheduler().cancelTask(tasks.get(player));
                    }
                }
            }, 0L, 1L));
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getPlayer().getInventory().getItemInMainHand().getItemMeta() != null
                && event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().equalsIgnoreCase("§eKeliautojas Jonas Havaras")) {
            event.setCancelled(true);

            Block block = event.getClickedBlock();
            Island island = SuperiorSkyblockAPI.getPlayer(event.getPlayer()).getIsland();
            Island islandAt = SuperiorSkyblockAPI.getIslandAt(block.getLocation());

            if (event.getClickedBlock() == null)
                return;
            if (!event.hasBlock())
                return;
            if (!event.getPlayer().getWorld().getName().equalsIgnoreCase("superiorworld"))
                return;
            if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
                return;
            if (islandAt != null && !island.getOwner().equals(islandAt.getOwner())) {
                event.setCancelled(true);
                Locale.ISLAND_PROTECTED.send(event.getPlayer());
                return;
            }

            Location loc = new Location(
                    block.getWorld(),
                    block.getLocation().getX()+0.5,
                    block.getLocation().getY()+1,
                    block.getLocation().getZ()+0.5,
                    event.getPlayer().getLocation().getYaw()+180.f,
                    0
                    );


            if (this.plugin.NPCs.containsKey(island))
                this.plugin.questNPC.removeNPCPacket(island);

            this.plugin.npcLocations.put(island, loc);
            this.plugin.questNPC.createNPC(island);

            event.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        }
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        if (event.getFrom().getName().equalsIgnoreCase("superiorworld"))
            return;

        Island island = SuperiorSkyblockAPI.getPlayer(event.getPlayer()).getIsland();
        if (this.plugin.NPCs.containsKey(island))
            this.plugin.questNPC.addNPCPacket(event.getPlayer(), this.plugin.NPCs.get(island));
    }

    private void createNPCSpawnItem(Player player) {
        Island island = SuperiorSkyblockAPI.getPlayer(player).getIsland();
        if (this.plugin.NPCs.containsKey(island)) {
            this.plugin.questNPC.removeNPCPacket(island);
            player.getInventory().addItem(new QuestVillagerEgg(island, this.plugin.NPCs.get(island).getUniqueID()));
        }
    }

    private void completeQuestNPC(Player player) {
        HashMap<UUID, PermissionAttachment> perms = new HashMap<>();
        PermissionAttachment attachment = player.addAttachment(this.plugin);
        perms.put(player.getUniqueId(), attachment);
        PermissionAttachment pperms = perms.get(player.getUniqueId());
        pperms.unsetPermission("questnpc.completed");
    }
}
