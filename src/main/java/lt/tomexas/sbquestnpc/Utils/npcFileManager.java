package lt.tomexas.sbquestnpc.Utils;

import com.bgsoftware.superiorskyblock.api.island.Island;
import lt.tomexas.sbquestnpc.Skyblock;
import net.minecraft.world.entity.npc.EntityVillager;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.logging.Level;

public class npcFileManager {

    private FileConfiguration config = null;
    private File configFile = null;
    private final Skyblock plugin;

    private final String dataFolder = "plugins/SkyblockData";

    public npcFileManager(Skyblock plugin) {
        this.plugin = plugin;
        saveDefaultConfig();
    }

    public void reloadConfig() {
        if (this.configFile == null)
            this.configFile = new File(dataFolder, "QuestNPCs.yml");

        this.config = YamlConfiguration.loadConfiguration(this.configFile);

        InputStream defaultStream = this.plugin.getResource("QuestNPCs.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            this.config.setDefaults(defaultConfig);
        }
    }

    public FileConfiguration getConfig() {
        if(this.config == null)
            reloadConfig();

        return this.config;
    }

    public void saveConfig() {
        if (this.config == null || this.configFile == null)
            return;

        try {
            this.getConfig().save(this.configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config to " + this.configFile, e);
        }
    }

    public void saveDefaultConfig() {
        if(this.configFile == null)
            this.configFile = new File(dataFolder, "QuestNPCs.yml");

        if (!this.configFile.exists()) {
            this.plugin.saveResource("QuestNPCs.yml", false);
        }
    }

    public void saveNPCdata(Island island) {
        if (this.plugin.itemCreated.get(island) != null) {
            getConfig().set("npc." + island.getOwner().getName() + ".itemCreated", this.plugin.itemCreated.get(island));
            saveConfig();
        }

        if (this.plugin.NPCs.get(island) == null) return;
        Map<EntityVillager, Location> map = this.plugin.NPCs.get(island);
        for (Location location : map.values())
            getConfig().set("npc." + island.getOwner().getName() + ".location", location);

        saveConfig();
    }

    public void restoreNPCdata(Island island) {
        if (getConfig().getLocation("npc." + island.getOwner().getName() + ".location") == null) return;
        boolean itemCreated = getConfig().getBoolean("npc." + island.getOwner().getName() + ".itemCreated");
        if (itemCreated) return;

        Location loc = getConfig().getLocation("npc." + island.getOwner().getName() + ".location");
        this.plugin.questNPC.createNPC(island, loc);
        this.plugin.questNPC.addNPCPacket(island);
        getConfig().set("npc." + island.getOwner().getName(), null);
        saveConfig();
    }
}
