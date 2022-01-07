package lt.tomexas.sbquestnpc;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import lt.tomexas.sbquestnpc.Inventories.InventoryListeners;
import lt.tomexas.sbquestnpc.Inventories.NPCInventory;
import lt.tomexas.sbquestnpc.Listeners.IslandEventsListener;
import lt.tomexas.sbquestnpc.Listeners.IslandPlayerListeners;
import lt.tomexas.sbquestnpc.Listeners.PlayerQuitJoinListeners;
import lt.tomexas.sbquestnpc.PacketsEvents.PacketReader;
import lt.tomexas.sbquestnpc.PacketsEvents.QuestNPC;
import lt.tomexas.sbquestnpc.Utils.npcFileManager;
import net.minecraft.world.entity.npc.EntityVillager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public final class Skyblock extends JavaPlugin implements CommandExecutor {

    public npcFileManager file;

    public Map<Island, Map<EntityVillager, Location>> NPCs = new HashMap<>();
    public Map<Island, Boolean> itemCreated = new HashMap<>();

    public QuestNPC questNPC;

    public NPCInventory npcInventory;

    @Override
    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new IslandEventsListener(this), this);
        pm.registerEvents(new IslandPlayerListeners(this), this);
        pm.registerEvents(new PlayerQuitJoinListeners(this), this);
        pm.registerEvents(new InventoryListeners(this), this);

        this.file = new npcFileManager(this);
        this.questNPC = new QuestNPC(this);
        this.npcInventory = new NPCInventory();

        for (Island island : SuperiorSkyblockAPI.getGrid().getIslands())
            this.file.restoreNPCdata(island);

        if (!Bukkit.getOnlinePlayers().isEmpty()) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                PacketReader reader = new PacketReader();
                reader.inject(player);
            }
        }
    }

    @Override
    public void onDisable() {
        for (Island island : SuperiorSkyblockAPI.getGrid().getIslands()) {
            this.file.saveNPCdata(island);
            this.questNPC.removeNPCPacket(island);
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            PacketReader reader = new PacketReader();
            reader.uninject(player);
        }
    }
}
