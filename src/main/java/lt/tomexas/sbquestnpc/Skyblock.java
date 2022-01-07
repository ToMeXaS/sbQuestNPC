package lt.tomexas.sbquestnpc;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import lt.tomexas.sbquestnpc.Inventories.Dictionary;
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
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.ChatPaginator;
import org.davidmoten.text.utils.WordWrap;

import java.io.File;
import java.util.*;

public final class Skyblock extends JavaPlugin implements CommandExecutor {

    public npcFileManager file;
    public Dictionary dictionary;

    public Map<Island, Map<EntityVillager, Location>> NPCs = new HashMap<>();
    public Map<Island, Boolean> itemCreated = new HashMap<>();

    public QuestNPC questNPC;

    public NPCInventory npcInventory;
    public List<String> QCconvertedNames = new ArrayList<>();
    public List<String> QCconvertedDesc = new ArrayList<>();

    @Override
    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new IslandEventsListener(this), this);
        pm.registerEvents(new IslandPlayerListeners(this), this);
        pm.registerEvents(new PlayerQuitJoinListeners(this), this);
        pm.registerEvents(new InventoryListeners(this), this);

        this.file = new npcFileManager(this);
        this.dictionary = new Dictionary();
        startUnicodeConversion();
        this.questNPC = new QuestNPC(this);
        this.npcInventory = new NPCInventory(this);

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

    private void startUnicodeConversion() {
        String dataFolder = "plugins/QuestCreator/quest_models/";
        String[] fileList = new File(dataFolder).list();
        int filesCount;
        if (fileList != null && fileList.length > 0) {
            filesCount = fileList.length;
            for (int i = 1; i <= filesCount; i++) {
                File file = new File(dataFolder + "" + i + ".yml");
                YamlConfiguration data = YamlConfiguration.loadConfiguration(file);
                String name = convertToUni(data.getString("display_name"));
                QCconvertedNames.add(name);
                String desc = convertToLightUni(data.getString("description").replaceAll("\\[", "").replaceAll("]", ""));
                QCconvertedDesc.add(desc);
            }
        }
    }

    private String convertToUni(String title) {
        title = title.toLowerCase(Locale.ROOT);
        for(int i = 0; i < title.length(); i++) {
            if (this.dictionary.getDarkLetters(String.valueOf(title.charAt(i))) == null) continue;
            title = title.replace(String.valueOf(title.charAt(i)), this.dictionary.getDarkLetters(String.valueOf(title.charAt(i))));
        }
        return title;
    }

    private String convertToLightUni(String desc) {
        desc = desc.toLowerCase(Locale.ROOT);
        String[] lines = ChatPaginator.wordWrap(desc, 23);
        String line1 = lines[0];
        StringBuilder alignment = new StringBuilder();
        for(int i = 0; i < line1.length(); i++) {
            alignment.append("\uF805");
            if (this.dictionary.getLightLetters(String.valueOf(line1.charAt(i))) == null) continue;
            line1 = line1.replace(String.valueOf(line1.charAt(i)), this.dictionary.getLightLetters(String.valueOf(line1.charAt(i))));
        }


        String line2 = lines[1];
        for(int i = 0; i < line2.length(); i++) {
            if (this.dictionary.getLowerLightLetters(String.valueOf(line2.charAt(i))) == null) continue;
            line2 = line2.replace(String.valueOf(line2.charAt(i)), this.dictionary.getLowerLightLetters(String.valueOf(line2.charAt(i))));
        }
        return line1 + "\uF806" + alignment + line2;
    }
}
