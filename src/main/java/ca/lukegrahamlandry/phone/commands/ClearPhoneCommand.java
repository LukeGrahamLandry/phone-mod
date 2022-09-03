package ca.lukegrahamlandry.phone.commands;

import ca.lukegrahamlandry.phone.data.MessageData;
import ca.lukegrahamlandry.phone.data.PhoneDataStorage;
import ca.lukegrahamlandry.phone.network.NetworkHandler;
import ca.lukegrahamlandry.phone.network.clientbound.SyncPhoneMessagesPacket;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.List;

public class ClearPhoneCommand {
    public static LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("clearphone")
                .requires(cs-> cs.hasPermission(2))
                .then(Commands.argument("channel", new PhoneChannelArgumentType())
                        .executes(ClearPhoneCommand::handle)).executes((ctx) -> {
                            ctx.getSource().sendFailure(new StringTextComponent("must specify channel name to clear"));
                            return Command.SINGLE_SUCCESS;
                });
    }

    private static int handle(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        String channel = PhoneChannelArgumentType.get(ctx, "channel");
        PhoneDataStorage data = PhoneDataStorage.get((ctx.getSource()).getLevel());
        List<MessageData> messages = data.getMessages(channel);
        ctx.getSource().sendSuccess(new StringTextComponent("cleared " + messages.size() + " messages from " + channel), true);
        messages.clear();
        data.setDirty();
        NetworkHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new SyncPhoneMessagesPacket(messages, channel, true));
        return Command.SINGLE_SUCCESS;
    }
}
