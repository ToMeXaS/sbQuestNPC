package lt.tomexas.sbquestnpc.Utils;

import com.bgsoftware.superiorskyblock.api.island.Island;
import lt.tomexas.sbquestnpc.Skyblock;

public class Creator {

    private final Skyblock plugin;

    public Creator (Skyblock plugin) {
        this.plugin = plugin;
    }

    public void spawnQuestNPC(Island island) {
        this.plugin.questNPC.createNPC(island);
    }

    public void despawnQuestNPC(Island island) {
        this.plugin.questNPC.removeNPCPacket(island);
    }
}
