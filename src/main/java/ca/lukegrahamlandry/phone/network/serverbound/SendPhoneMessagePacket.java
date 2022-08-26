package ca.lukegrahamlandry.phone.network.serverbound;

import ca.lukegrahamlandry.phone.data.MessageData;
import ca.lukegrahamlandry.phone.data.PhoneDataStorage;
import ca.lukegrahamlandry.phone.network.NetworkHandler;
import ca.lukegrahamlandry.phone.network.clientbound.SyncPhoneMessagesPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.Collections;
import java.util.function.Supplier;

public class SendPhoneMessagePacket {
    private final String message;
    private final String channel;
    private final int phoneID;

    public SendPhoneMessagePacket(String message, String channel, int phoneID){
        this.message = message;
        this.channel = channel;
        this.phoneID = phoneID;
    }

    public static SendPhoneMessagePacket decode(PacketBuffer buf) {
        return new SendPhoneMessagePacket(buf.readUtf(32767), buf.readUtf(32767), buf.readInt());
    }

    public static void encode(SendPhoneMessagePacket packet, PacketBuffer buf) {
        buf.writeUtf(packet.message);
        buf.writeUtf(packet.channel);
        buf.writeInt(packet.phoneID);
    }

    public static void handle(SendPhoneMessagePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            MessageData msg = new MessageData(packet.phoneID, packet.message);
            PhoneDataStorage.get(ctx.get().getSender().getLevel()).getMessages(packet.channel).add(msg);
            NetworkHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new SyncPhoneMessagesPacket(Collections.singletonList(msg), packet.channel, false));
        });
        ctx.get().setPacketHandled(true);
    }
}
