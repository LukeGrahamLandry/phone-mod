package ca.lukegrahamlandry.phone.commands;

import ca.lukegrahamlandry.phone.data.MessageData;
import ca.lukegrahamlandry.phone.data.PhoneDataStorage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class PhoneChannelArgumentType implements ArgumentType<String> {
    private static final DynamicCommandExceptionType INVALID = new DynamicCommandExceptionType((p_106991_) -> {
        return new TranslationTextComponent("channel.does.not.exist", p_106991_);
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

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        return reader.readString();
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        StringReader stringreader = new StringReader(builder.getInput());
        stringreader.setCursor(builder.getStart());
        String s = stringreader.getRemaining();
        stringreader.setCursor(stringreader.getTotalLength());

        stringreader.skipWhitespace();

        Collection<String> options = Arrays.asList("public", "encrypted"); // MessageData.clientMessages.keySet(); would be used if channels weren't hard coded
        for (String check : options) {
            if (check.startsWith(s)) builder.suggest(check);
        }

        for (String check : options) {
            if (check.startsWith(s)) builder.suggest(check);
        }
        return builder.buildFuture();
    }

    public Collection<String> getExamples() {
        return new ArrayList<>();
    }
}
