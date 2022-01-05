package lt.tomexas.sbquestnpc;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import lt.tomexas.sbquestnpc.Listeners.IslandEventsListener;
import lt.tomexas.sbquestnpc.Listeners.IslandPlayerListeners;
import lt.tomexas.sbquestnpc.Listeners.PlayerQuitJoinListeners;
import lt.tomexas.sbquestnpc.PacketsEvents.PacketReader;
import lt.tomexas.sbquestnpc.PacketsEvents.QuestNPC;
import lt.tomexas.sbquestnpc.Utils.npcFileManager;
import lt.tomexas.sbquestnpc.Utils.Creator;
import net.minecraft.world.entity.npc.EntityVillager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public final class Skyblock extends JavaPlugin {

    public npcFileManager file;

    public Map<Island, EntityVillager> NPCs = new HashMap<>();
    public Map<Island, Location> npcLocations = new HashMap<>();

    public Creator creator;
    public QuestNPC questNPC;

    @Override
    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new IslandEventsListener(this), this);
        pm.registerEvents(new IslandPlayerListeners(this), this);
        pm.registerEvents(new PlayerQuitJoinListeners(this), this);

        this.file = new npcFileManager(this);
        this.creator = new Creator(this);
        this.questNPC = new QuestNPC(this);

        for (Island island : SuperiorSkyblockAPI.getGrid().getIslands()) {
            this.restoreNPCLoc(island);
            this.creator.spawnQuestNPC(island);
        }

        if (!Bukkit.getOnlinePlayers().isEmpty()) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                PacketReader reader = new PacketReader();
                reader.inject(player);
            }
        }
    }

    @Override
    public void onDisable() {
        for (Map.Entry<Island, EntityVillager> set : this.NPCs.entrySet())
            saveNPCdata(set.getKey(), set.getValue());

        for (Island island : SuperiorSkyblockAPI.getGrid().getIslands())
            this.creator.despawnQuestNPC(island);

        for (Player player : Bukkit.getOnlinePlayers()) {
            PacketReader reader = new PacketReader();
            reader.uninject(player);
        }
    }

    private void saveNPCdata(Island island, EntityVillager npc) {
        this.file.getConfig().set("npcList." + island.getOwner().getName() + ".ID", npc.getId());
        this.file.getConfig().set("npcList." + island.getOwner().getName() + ".npcLocation", this.npcLocations.get(island));
        this.file.saveConfig();
    }

    private void restoreNPCLoc(Island island) {
        if (this.file.getConfig().get("npcList." + island.getOwner().getName() + ".npcLocation") == null) return;
        this.npcLocations.put(island, this.file.getConfig().getLocation("npcList." + island.getOwner().getName() + ".npcLocation"));
    }
}
