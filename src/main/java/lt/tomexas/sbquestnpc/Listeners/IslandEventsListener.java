package lt.tomexas.sbquestnpc.Listeners;

import com.bgsoftware.superiorskyblock.api.events.IslandCreateEvent;
import com.bgsoftware.superiorskyblock.api.island.Island;
import lt.tomexas.sbquestnpc.PacketsEvents.PacketReader;
import lt.tomexas.sbquestnpc.Skyblock;
import net.minecraft.world.entity.npc.EntityVillager;
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
            this.plugin.questNPC.destroyNPC(island);
        }

        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {

            this.plugin.questNPC.createNPC(event.getIsland(), this.plugin.questNPC.defaultNPCLoc(event.getIsland()));
            this.plugin.questNPC.addNPCPacket(event.getIsland());

            PacketReader reader = new PacketReader();
            reader.inject(event.getPlayer().asPlayer());

        }, 20);
    }
}
