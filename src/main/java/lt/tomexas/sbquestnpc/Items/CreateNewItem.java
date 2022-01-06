package lt.tomexas.sbquestnpc.Items;

import lt.tomexas.sbquestnpc.PacketsEvents.QuestNPC;
import lt.tomexas.sbquestnpc.Skyblock;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;

public class CreateNewItem extends ItemStack {

    protected CreateNewItem (Material material, int count, List<String> lore) {
        if (count < 1) count = 1;

        this.setType(material);
        this.setAmount(count);

        ItemMeta meta = this.getItemMeta();
        String name = new QuestNPC(Skyblock.getPlugin(Skyblock.class)).getName();
        if (!name.isEmpty())
            meta.setDisplayName(name);
        if (!lore.isEmpty())
            for (int i = 0; i < lore.size(); i++)
                lore.set(i, ChatColor.translateAlternateColorCodes('&', lore.get(i)));

            meta.setLore(lore);

        this.setItemMeta(meta);
    }
}
