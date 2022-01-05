package lt.tomexas.sbquestnpc.Listeners;

import com.bgsoftware.superiorskyblock.api.events.IslandChunkResetEvent;
import com.bgsoftware.superiorskyblock.api.events.IslandCreateEvent;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.nms.v1_17_R1.chunks.IslandsChunkGenerator;
import lt.tomexas.sbquestnpc.PacketsEvents.PacketReader;
import lt.tomexas.sbquestnpc.Skyblock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBase;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class IslandEventsListener implements Listener {

    private final Skyblock plugin;

    public IslandEventsListener (Skyblock plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onIslandCreate(IslandCreateEvent event) {
        if (this.plugin.NPCs.containsKey(event.getIsland())) {
            PacketReader reader = new PacketReader();
            reader.uninject(event.getPlayer().asPlayer());

            Island island = event.getIsland();
            this.plugin.creator.despawnQuestNPC(island);

            this.plugin.NPCs.remove(island);
            this.plugin.npcLocations.remove(island);
        }

        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {

            this.plugin.creator.spawnQuestNPC(event.getIsland());

            PacketReader reader = new PacketReader();
            reader.inject(event.getPlayer().asPlayer());

        }, 20);
    }
}
