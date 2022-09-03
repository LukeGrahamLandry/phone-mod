package ca.lukegrahamlandry.phone.network.serverbound;

import ca.lukegrahamlandry.phone.data.MessageData;
import ca.lukegrahamlandry.phone.data.PhoneDataStorage;
import ca.lukegrahamlandry.phone.network.NetworkHandler;
import ca.lukegrahamlandry.phone.network.clientbound.SyncPhoneMessagesPacket;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.CommandBlockTileEntity;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
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
            ServerPlayerEntity player = ctx.get().getSender();
            MessageData msg = new MessageData(packet.phoneID, packet.message);
            PhoneDataStorage.get(player.getLevel()).addMessage(packet.channel, msg);
            NetworkHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new SyncPhoneMessagesPacket(Collections.singletonList(msg), packet.channel, false));

            // not using the player cause dont want it to chat & idk about permissions
            CommandSource source = new CommandSource(ICommandSource.NULL, Vector3d.atCenterOf(player.blockPosition()), Vector2f.ZERO, player.getLevel(), 2,"phone", new StringTextComponent("phone"), player.getLevel().getServer(), (Entity)null);
            player.server.getCommands().performCommand(source, "/tag @e[tag=incomingmessages] add " + packet.channel);
        });
        ctx.get().setPacketHandled(true);
    }
}
