package lt.tomexas.sbquestnpc.Listeners;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import lt.tomexas.sbquestnpc.PacketsEvents.PacketReader;
import lt.tomexas.sbquestnpc.Skyblock;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitJoinListeners implements Listener {

    private final Skyblock plugin;

    public PlayerQuitJoinListeners (Skyblock plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Island island = SuperiorSkyblockAPI.getPlayer(player).getIsland();

        if (island != null ) {
            this.plugin.creator.spawnQuestNPC(island);
            SuperiorPlayer sPlayer = SuperiorSkyblockAPI.getPlayer(player);
            sPlayer.teleport(sPlayer.getIsland());
            Bukkit.getConsoleSender().sendMessage("NPC spawned!");

            PacketReader reader = new PacketReader();
            reader.inject(player);
        }
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Island island = SuperiorSkyblockAPI.getPlayer(player).getIsland();
        if (island != null) {
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                for (SuperiorPlayer superiorPlayer : island.getIslandMembers(true)) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(superiorPlayer.getUniqueId());
                    if (offlinePlayer.isOnline()) return;
                }

                this.plugin.creator.despawnQuestNPC(island);
                Bukkit.getConsoleSender().sendMessage("NPC despawned!");

            }, 1);
            PacketReader reader = new PacketReader();
            reader.uninject(player);
        }
    }
}
