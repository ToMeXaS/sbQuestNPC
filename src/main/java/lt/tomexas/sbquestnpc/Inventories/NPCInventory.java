package lt.tomexas.sbquestnpc.Inventories;

import com.guillaumevdn.questcreator.QuestCreator;
import com.guillaumevdn.questcreator.data.quest.BoardQuests;
import com.guillaumevdn.questcreator.data.user.UserQC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class NPCInventory {

    String title =
            "\uF80C\uF809\uF804\uE015\uF829\uF826\uE019\uF823\uE020\uF822\uE021\uF829\uF828\uE018" + // Control buttons
            "\uF80C\uF826"; // Wall of text starting point


    public void openInventory(Player player, int i) {
        String[] fileList = new File("plugins/QuestCreator/quest_models").list();
        if (fileList == null) return;
        int filesCount = fileList.length;

        File file = new File("plugins/QuestCreator/quest_models/" + i + ".yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        String desc = convertToUni(config.getString("display_name"));

        Inventory inv = Bukkit.createInventory(player, 54, "§f\uF808\uE013" + controlButtons(title, i, filesCount) + desc  + "\uF82F[" + i + "]");

        if (i > 1)
            inv.setItem(1, createItem(Material.PAPER, "&cPraeitas puslapis", null, 1));
        if (i < filesCount )
            inv.setItem(7, createItem(Material.PAPER, "&aSekantis puslapis", null, 1));

        inv.setItem(3, createItem(Material.PAPER, "&a&lPradėti užduotį", null, 1));
        inv.setItem(4, createItem(Material.PAPER, "&c&lAtšaukti užduotį", null, 1));
        inv.setItem(5, createItem(Material.PAPER, "&e&lUžduoties informacija",
                Arrays.asList(
                        "&fGreitu metu bus pridėta",
                        "&fvisa reikalinga informacija..."
                ),
                1));
        player.openInventory(inv);
    }

    private ItemStack createItem(Material material, String name, List<String> lore, int customModelData) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (name != null)
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        if (lore != null) {
            for (int i = 0; i < lore.size(); i++)
                lore.set(i, ChatColor.translateAlternateColorCodes('&', lore.get(i)));
        }

        meta.setLore(lore);
        meta.setCustomModelData(customModelData);
        item.setItemMeta(meta);
        return item;
    }

    private String controlButtons(String title, int i, int filesCount) {
        if (i > 1 && i < filesCount) {
            title = title.replace("\uE015", "\uE016");
            title = title.replace("\uE017", "\uE018");
        } else if (i == 1) {
            title = title.replace("\uE016", "\uE015");
            title = title.replace("\uE017", "\uE018");
        } else if (i == filesCount) {
            title = title.replace("\uE015", "\uE016");
            title = title.replace("\uE018", "\uE017");
        }
        return title;
    }

    private String convertToUni(String desc) {
        desc = desc.toLowerCase(Locale.ROOT);
        desc = desc
                .replaceAll("a", "\uE023")
                .replaceAll("ą", "\uE024")
                .replaceAll("b", "\uE025")
                .replaceAll("c", "\uE026")
                .replaceAll("č", "\uE027")
                .replaceAll("d", "\uE028")
                .replaceAll("e", "\uE029")
                .replaceAll("ę", "\uE030")
                .replaceAll("ė", "\uE031")
                .replaceAll("f", "\uE032")
                .replaceAll("g", "\uE033")
                .replaceAll("h", "\uE034")
                .replaceAll("i", "\uE035")
                .replaceAll("į", "\uE036")
                .replaceAll("y", "\uE037")
                .replaceAll("j", "\uE038")
                .replaceAll("k", "\uE039")
                .replaceAll("l", "\uE040")
                .replaceAll("m", "\uE041")
                .replaceAll("n", "\uE042")
                .replaceAll("o", "\uE043")
                .replaceAll("p", "\uE044")
                .replaceAll("r", "\uE045")
                .replaceAll("s", "\uE046")
                .replaceAll("š", "\uE047")
                .replaceAll("t", "\uE048")
                .replaceAll("u", "\uE049")
                .replaceAll("ų", "\uE050")
                .replaceAll("ū", "\uE051")
                .replaceAll("v", "\uE052")
                .replaceAll("z", "\uE053")
                .replaceAll("ž", "\uE054")
                .replaceAll("0", "\uE055")
                .replaceAll("1", "\uE056")
                .replaceAll("2", "\uE057")
                .replaceAll("3", "\uE058")
                .replaceAll("4", "\uE059")
                .replaceAll("5", "\uE060")
                .replaceAll("6", "\uE061")
                .replaceAll("7", "\uE062")
                .replaceAll("8", "\uE063")
                .replaceAll("9", "\uE064")
                .replaceAll("\\.", "\uF802\uE065\uF821")
                .replaceAll(" ", "\uF823");
        return desc;
    }
}
