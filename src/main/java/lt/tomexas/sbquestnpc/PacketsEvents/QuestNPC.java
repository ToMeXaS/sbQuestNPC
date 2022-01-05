package lt.tomexas.sbquestnpc.PacketsEvents;

import com.bgsoftware.superiorskyblock.api.island.Island;
import lt.tomexas.sbquestnpc.Skyblock;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityHeadRotation;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class QuestNPC {

    private final Skyblock plugin;

    public QuestNPC (Skyblock plugin) {
        this.plugin = plugin;
    }

    public void createNPC(Island island) {
        WorldServer world = ((CraftWorld) island.getCenter(World.Environment.NORMAL).getWorld()).getHandle();
        EntityVillager npc = new EntityVillager(EntityTypes.aV, world, VillagerType.c);

        Location loc;
        if (this.plugin.npcLocations.containsKey(island)) {
            loc = this.plugin.npcLocations.get(island);
        } else {
            loc = island.getCenter(World.Environment.NORMAL).add(1, 0, -3);
        }

        npc.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        npc.setCustomNameVisible(true);

        if (!island.getOwner().hasPermission("questNPC.completed"))
            npc.setCustomName(new ChatComponentText("\uF822\uE005"));
        else
            npc.setCustomName(new ChatComponentText("§eKeliautojas Jonas Havaras"));

        npc.setSilent(true);
        npc.setNoAI(true);
        npc.setInvulnerable(true);
        npc.setVillagerData(new VillagerData(VillagerType.c, VillagerProfession.d, 1));

        for (Player player : Bukkit.getOnlinePlayers())
            addNPCPacket(player, npc);

        this.plugin.npcLocations.put(island, loc);
        this.plugin.NPCs.put(island, npc);
    }

    public void addNPCPacket(Player player, EntityVillager npc) {
        PlayerConnection connection = ((CraftPlayer) player).getHandle().b;
        connection.sendPacket(new PacketPlayOutSpawnEntity(npc));
        connection.sendPacket(new PacketPlayOutEntityMetadata(npc.getId(), npc.getDataWatcher(), true));
        connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) (npc.getBukkitYaw() * 256 / 360)));
    }

    public void removeNPCPacket(Island island) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerConnection connection = ((CraftPlayer) player).getHandle().b;
            connection.sendPacket(new PacketPlayOutEntityDestroy(this.plugin.NPCs.get(island).getId()));
        }
    }
}
