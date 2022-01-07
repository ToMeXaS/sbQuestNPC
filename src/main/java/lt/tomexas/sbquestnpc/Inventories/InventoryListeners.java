package lt.tomexas.sbquestnpc.Inventories;

import com.guillaumevdn.questcreator.QuestCreator;
import com.guillaumevdn.questcreator.data.user.UserQC;
import lt.tomexas.sbquestnpc.Skyblock;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.io.File;
import java.util.UUID;

public class InventoryListeners implements Listener {

    private Skyblock plugin;

    public InventoryListeners(Skyblock plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (title.contains("\uE013")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();

            String[] fileList = new File("plugins/QuestCreator/quest_models").list();
            int filesCount = 0;
            if (fileList != null && fileList.length > 0)
                filesCount = fileList.length;

            if (filesCount != 0) {
                int clickedSlot = event.getRawSlot();
                String[] titleSplit = title.split("\\u005B");
                int page = Integer.parseInt(titleSplit[1].replace("]", ""));
                switch (clickedSlot) {
                    case 1: {
                        if (event.getInventory().getItem(clickedSlot) == null) break;
                        this.plugin.npcInventory.openInventory(player, page-1);
                        break;
                    }
                    case 7: {
                        if (event.getInventory().getItem(clickedSlot) == null) break;
                        this.plugin.npcInventory.openInventory(player, page+1);
                        break;
                    }
                    case 3: {
                        player.closeInventory();
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "qc start " + player.getName() + " " + page);
                        break;
                    }
                    case 4: {
                        player.closeInventory();
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "qc stop " + player.getName() + " " + page);
                        break;
                    }
                }
            }
        }
    }
}
