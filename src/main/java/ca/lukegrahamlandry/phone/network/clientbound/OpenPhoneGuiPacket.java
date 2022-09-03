package ca.lukegrahamlandry.phone.network.clientbound;

import ca.lukegrahamlandry.phone.data.MessageData;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class OpenPhoneGuiPacket {
    final int id;
    final String channel;

    public OpenPhoneGuiPacket(int id, String channel){
        this.id = id;
        this.channel = channel;
    }

    public static OpenPhoneGuiPacket decode(PacketBuffer buf) {
        return new OpenPhoneGuiPacket(buf.readInt(), buf.readUtf(32767));
    }

    public static void encode(OpenPhoneGuiPacket packet, PacketBuffer buf) {
        buf.writeInt(packet.id);
        buf.writeUtf(packet.channel);
    }

    public static void handle(OpenPhoneGuiPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientHelper.openGui(packet.channel, packet.id, false);
        });
        ctx.get().setPacketHandled(true);
    }
}
