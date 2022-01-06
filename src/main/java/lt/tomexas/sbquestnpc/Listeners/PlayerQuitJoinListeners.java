package lt.tomexas.sbquestnpc.Listeners;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import lt.tomexas.sbquestnpc.PacketsEvents.PacketReader;
import lt.tomexas.sbquestnpc.Skyblock;
import net.minecraft.network.protocol.game.PacketPlayOutEntityHeadRotation;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.npc.EntityVillager;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;

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
            this.plugin.questNPC.addNPCPacket(island);
            SuperiorPlayer sPlayer = SuperiorSkyblockAPI.getPlayer(player);
            sPlayer.teleport(sPlayer.getIsland());
            Bukkit.getConsoleSender().sendMessage("NPC despawned!");

            PacketReader reader = new PacketReader();
            reader.inject(player);

            // Send already created entity packets to joined player
            for (Map.Entry<Island, Map<EntityVillager, Location>> mapEntry : this.plugin.NPCs.entrySet()) {
                if (mapEntry.getKey().equals(island)) continue;
                Map<EntityVillager, Location> map = this.plugin.NPCs.get(mapEntry.getKey());
                for (EntityVillager npc : map.keySet()){
                    PlayerConnection connection = ((CraftPlayer) player).getHandle().b;
                    connection.sendPacket(new PacketPlayOutSpawnEntity(npc));
                    connection.sendPacket(new PacketPlayOutEntityMetadata(npc.getId(), npc.getDataWatcher(), true));
                    connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) (npc.getBukkitYaw() * 256 / 360)));
                }
            }
        }
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Island island = SuperiorSkyblockAPI.getPlayer(player).getIsland();
        if (island != null) {
            this.plugin.questNPC.removeNPCPacket(island);
            Bukkit.getConsoleSender().sendMessage("NPC despawned!");

            PacketReader reader = new PacketReader();
            reader.uninject(player);
        }
    }
}
