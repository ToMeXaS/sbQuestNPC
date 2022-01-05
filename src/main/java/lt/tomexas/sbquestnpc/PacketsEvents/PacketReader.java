package lt.tomexas.sbquestnpc.PacketsEvents;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lt.tomexas.sbquestnpc.Listeners.RightClickNPC;
import lt.tomexas.sbquestnpc.Skyblock;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.entity.npc.EntityVillager;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import io.netty.channel.Channel;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PacketReader {

    Channel channel;
    public static Map<UUID, Channel> channels = new HashMap<>();

    public void inject(Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        channel = craftPlayer.getHandle().b.a().k;
        channels.put(player.getUniqueId(), channel);

        if (channel.pipeline().get("PacketInjector") != null)
            return;

        channel.pipeline().addAfter("decoder", "PacketInjector", new MessageToMessageDecoder<Packet<?>>() {

            @Override
            protected void decode(ChannelHandlerContext channel, Packet<?> packet, List<Object> arg) throws Exception {
                arg.add(packet);
                readPacket(player, packet);
            }

        });
    }

    public void uninject(Player player) {
        channel = channels.get(player.getUniqueId());
        if (channel.pipeline().get("PacketInjector") != null)
            channel.pipeline().remove("PacketInjector");
    }

    public void readPacket(Player player, Packet<?> packet) {
        if (packet.getClass().getSimpleName().equalsIgnoreCase("PacketPlayInUseEntity")) {

            int id = (int) getValue(packet, "a");

            if (getValue(packet, "b").toString().contains("PacketPlayInUseEntity$d")) {
                for (EntityVillager npc : Skyblock.getPlugin(Skyblock.class).NPCs.values()) {
                    if (id == npc.getId()) {
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Skyblock.getPlugin(Skyblock.class), () -> {
                                Bukkit.getPluginManager().callEvent(new RightClickNPC(player, npc));
                        }, 0);
                    }
                }
            }
        }
    }

    private Object getValue(Object instance, String name) {
        Object result = null;

        try {
            Field field = instance.getClass().getDeclaredField(name);

            field.setAccessible(true);

            result = field.get(instance);

            field.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
