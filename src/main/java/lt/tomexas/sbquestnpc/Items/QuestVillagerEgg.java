package lt.tomexas.sbquestnpc.Items;

import com.bgsoftware.superiorskyblock.api.island.Island;
import lt.tomexas.sbquestnpc.PacketsEvents.QuestNPC;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.UUID;

public class QuestVillagerEgg extends CreateNewItem {

    public QuestVillagerEgg (Island island, UUID uuid) {
        super(Material.VILLAGER_SPAWN_EGG, 1, "§eKeliautojas Jonas Havaras", Arrays.asList(
                "&8Entity ID " + uuid,
                "&8Island Owner: " + island.getOwner().getName(),
                "",
                "&fŠis kiaušinis yra skirtas, jog",
                "&fsavoje saloje galėtumėte atspawninti NPC,",
                "&fkuris yra reikalingas norint sklandžiai vykdyti",
                "&fvisas jums paruošas užduotis.",
                ""
        ));
    }
}
