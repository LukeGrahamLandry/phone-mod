package ca.lukegrahamlandry.phone.objects;

import ca.lukegrahamlandry.phone.data.MessageData;
import ca.lukegrahamlandry.phone.data.PhoneDataStorage;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ClearPhoneCommand {
    public static LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("admin")
                .requires(cs-> cs.hasPermission(2))
                .then(Commands.literal("phone")
                        .then(Commands.argument("channel", new PhoneChannelArgumentType())
                                .executes(ClearPhoneCommand::handle))
                );
    }

    private static int handle(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        String channel = PhoneChannelArgumentType.get(ctx, "channel");
        PhoneDataStorage data = PhoneDataStorage.get((ctx.getSource()).getLevel());
        List<MessageData> messages = data.getMessages(channel);
        ctx.getSource().sendSuccess(new StringTextComponent("cleared " + messages.size() + " messages from " + channel), true);
        messages.clear();
        data.setDirty();
        return Command.SINGLE_SUCCESS;
    }

    public static class PhoneChannelArgumentType extends ResourceLocationArgument {
        private static final DynamicCommandExceptionType INVALID = new DynamicCommandExceptionType((p_106991_) -> {
            return new TranslationTextComponent("invalid.phone.channel", p_106991_);
        });

        public static String get(CommandContext<CommandSource> p_107002_, String p_107003_) throws CommandSyntaxException {
            String result = p_107002_.getArgument(p_107003_, String.class);
            Collection<String> options = PhoneDataStorage.get((p_107002_.getSource()).getLevel()).messages.keySet();

            if (!options.contains(result)) {
                throw INVALID.create(result);
            } else {
                return result;
            }
        }

        public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
            StringReader stringreader = new StringReader(builder.getInput());
            stringreader.setCursor(builder.getStart());
            String s = stringreader.getRemaining();
            stringreader.setCursor(stringreader.getTotalLength());

            stringreader.skipWhitespace();

            Collection<String> options = PhoneDataStorage.get(((CommandSource) context.getSource()).getLevel()).messages.keySet();
            for (String check : options){
                if (check.startsWith(s)) builder.suggest(check);
            }

            for (String check : options){
                if (check.startsWith(s)) builder.suggest(check);
            }
            return builder.buildFuture();
        }

        public Collection<String> getExamples() {
            return new ArrayList<>();
        }
    }
}
