package lt.tomexas.sbquestnpc.Inventories;

import lt.tomexas.sbquestnpc.Skyblock;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class NPCInventory {

    private final Skyblock plugin;

    public NPCInventory (Skyblock plugin) {
        this.plugin = plugin;
    }

    String title =
            "\uF80C\uF809\uF804\uE015\uF829\uF826\uE019\uF823\uE020\uF822\uE021\uF829\uF828\uE018" + // Control buttons
            "\uF80C\uF826"; // Wall of text starting point


    public void openInventory(Player player, int i) {
        String[] fileList = new File("plugins/QuestCreator/quest_models").list();
        if (fileList == null) return;
        int filesCount = fileList.length;
        String name = this.plugin.QCconvertedNames.get(i-1);
        String desc = this.plugin.QCconvertedDesc.get(i-1);
        String spacer = "";
        if (name.contains("\uE065")) spacer = "\uF824";
        Inventory inv = Bukkit.createInventory(player, 54, "§f\uF808\uE013" + controlButtons(title, i, filesCount) +
                name + spacer + alignment(name) + desc
                + "\uF82F[" + i + "]");

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

    private String alignment(String name) {
        int nameLength = name.length();
        StringBuilder align = new StringBuilder();
        if (nameLength > 20) {
            for (int i = 0; i < (nameLength - 20); i++) {
                align.append("\uF806");
            }
            return "\uF80B\uF80A\uF809\uF802" + align;
        } else if (nameLength < 20) {
            for (int i = nameLength; i > 0; i--) {
                align.append("\uF806");
            }
            return "\uF823" + align;
        }
        return "\uF80B\uF80A\uF809\uF802";
    }
}
