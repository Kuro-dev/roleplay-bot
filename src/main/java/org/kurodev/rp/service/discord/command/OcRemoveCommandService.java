package org.kurodev.rp.service.discord.command;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;
import org.kurodev.rp.db.entity.OriginalCharacter;
import org.kurodev.rp.db.repository.OriginalCharacterRepository;
import org.kurodev.rp.service.discord.DiscordCommand;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OcRemoveCommandService extends DiscordCommand {
    private final OriginalCharacterRepository repository;

    public OcRemoveCommandService(OriginalCharacterRepository repository) {
        super("oc-remove");
        this.repository = repository;
    }

    @Override
    protected void configCommand(SlashCommandData cmd) {
        cmd.setDescription("Irreversibly Delete all characters. ");
        cmd.addOption(OptionType.STRING, "char", "Name of the character to delete. Does nothing if `remove-all` is used", false, true);
        cmd.addOption(OptionType.BOOLEAN, "remove-all", "remove all characters. This will take precedence over the `char` argument.");
    }

    @Override
    protected void executeCommand(@NotNull SlashCommandInteractionEvent event) {
        OptionMapping character = event.getInteraction().getOption("char");
        OptionMapping removeAll = event.getInteraction().getOption("remove-all");

        if (removeAll != null && removeAll.getAsBoolean()) {
            repository.deleteAllByUserId(event.getUser().getIdLong());
            event.reply("All characters have been deleted successfully.")
                    .setEphemeral(true)
                    .queue();
        } else if (character != null && !character.getAsString().isBlank()) {
            repository.findByUserIdAndNameEquals(event.getUser().getIdLong(), character.getAsString())
                    .ifPresent(repository::delete);
            event.reply("Character has been deleted successfully.")
                    .setEphemeral(true)
                    .queue();
        } else {
            event.reply("At least one argument is required!")
                    .setEphemeral(true)
                    .queue();
        }
    }


    @Override
    protected void onAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        if (event.getFocusedOption().getName().equals("char")) {
            String query = event.getFocusedOption().getValue();
            List<OriginalCharacter> suggestions = repository.findByUserIdAndNameLike(event.getUser().getIdLong(), query);
            event.replyChoiceStrings(
                    suggestions.stream()
                            .limit(25)
                            .map(OriginalCharacter::getName)
                            .toList()
            ).queue();
        }
    }
}
