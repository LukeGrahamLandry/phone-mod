package ca.lukegrahamlandry.phone.commands;

import ca.lukegrahamlandry.phone.data.MessageData;
import ca.lukegrahamlandry.phone.data.PhoneDataStorage;
import ca.lukegrahamlandry.phone.network.NetworkHandler;
import ca.lukegrahamlandry.phone.network.clientbound.OpenPhoneGuiPacket;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ViewPhoneCommand {
    public static LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("viewphone")
                // .requires(cs-> cs.hasPermission(2))
                .then(Commands.argument("channel", new PhoneChannelArgumentType())
                        .executes((ctx) -> handle(ctx, ctx.getSource().getPlayerOrException()))).executes((ctx) -> {
                            ctx.getSource().sendFailure(new StringTextComponent("must specify channel name to clear"));
                            return Command.SINGLE_SUCCESS;
                });
    }

    private static int handle(CommandContext<CommandSource> ctx, ServerPlayerEntity player) throws CommandSyntaxException {
        String channel = PhoneChannelArgumentType.get(ctx, "channel");
        PhoneDataStorage data = PhoneDataStorage.get((ctx.getSource()).getLevel());
        List<Integer> phones = new ArrayList<>();
        for (MessageData msg : data.getMessages(channel)){
            if (!phones.contains(msg.phoneId)) phones.add(msg.phoneId);
        }
        int id = phones.get(new Random().nextInt(phones.size()));
        NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new OpenPhoneGuiPacket(id, channel));
        return Command.SINGLE_SUCCESS;
    }
}
