package org.kurodev.rp.service.discord.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;
import org.kurodev.rp.db.entity.OriginalCharacter;
import org.kurodev.rp.db.repository.OriginalCharacterRepository;
import org.kurodev.rp.service.discord.DiscordCommand;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.List;
import java.util.Optional;

@Service
public class OcMessageCommandService extends DiscordCommand {
    private final OriginalCharacterRepository repository;

    public OcMessageCommandService(OriginalCharacterRepository repository) {
        super("oc-msg");
        this.repository = repository;
    }

    @Override
    protected void configCommand(SlashCommandData cmd) {
        cmd.setDescription("Write a message as your original Character");

        cmd.addOption(OptionType.STRING, "char", "Name of the character", true, true);
        cmd.addOption(OptionType.STRING, "desc", "What does your Character say/do?", true);

        cmd.addOption(OptionType.STRING, "location", "Location of where this takes place");
    }

    @Override
    protected void executeCommand(@NotNull SlashCommandInteractionEvent event) {
        String characterName = event.getInteraction().getOption("char").getAsString();
        String description = event.getInteraction().getOption("desc").getAsString();
        String location = event.getInteraction().getOption("location") != null
                ? event.getInteraction().getOption("location").getAsString()
                : null;

        Optional<OriginalCharacter> character = repository.findByUserIdAndNameEquals(event.getUser().getIdLong(), characterName);
        if (character.isEmpty()) {
            event.reply("Character not found!").setEphemeral(true).queue();
            return;
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(character.get().getName())
                .setColor(Color.MAGENTA);

        if (character.get().getAvatarUrl() != null && !character.get().getAvatarUrl().isBlank()) {
            embed.setThumbnail(character.get().getAvatarUrl());
        }

        if (location != null && !location.isBlank()) {
            embed.addField("Location", location, false);
        }

        embed.addField("Description", description, false);
        embed.setFooter("Written by " + event.getUser().getName(), event.getUser().getAvatarUrl());

        event.replyEmbeds(embed.build()).queue();
    }


    @Override
    protected void onAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        if (event.getFocusedOption().getName().equals("char")) {
            String query = event.getFocusedOption().getValue();
            List<OriginalCharacter> suggestions = repository.findByUserIdAndNameLike(event.getUser().getIdLong(), query);
            event.replyChoiceStrings(
                    suggestions.stream()
                            .map(OriginalCharacter::getName)
                            .toList()
            ).queue();
        }
    }
}
