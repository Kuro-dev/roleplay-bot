package org.kurodev.rp.service.discord.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;
import org.kurodev.rp.db.entity.OriginalCharacter;
import org.kurodev.rp.db.repository.OriginalCharacterRepository;
import org.kurodev.rp.service.discord.DiscordCommand;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OcInfoCommandService extends DiscordCommand {
    private OriginalCharacterRepository repository;

    public OcInfoCommandService(OriginalCharacterRepository repository) {
        super("oc-info");
        this.repository = repository;
    }

    @Override
    protected void configCommand(SlashCommandData cmd) {
        cmd.setDescription("Shows information about an OC in the server");
        cmd.addOption(OptionType.STRING, "char", "Name of the character (OC)", true, true);
    }

    @Override
    protected void executeCommand(@NotNull SlashCommandInteractionEvent event) {
        String optionVal = event.getOption("char").getAsString();
        Optional<OriginalCharacter> optional = repository.findById(Long.parseLong(optionVal));
        if (optional.isPresent()) {
            OriginalCharacter character = optional.get();
            User creator = event.getJDA().retrieveUserById(character.getUserId()).complete();
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("Character Information")
                    .addField("ID", String.valueOf(character.getCharacterId()), true)
                    .addField("Name", character.getName(), true)
                    .addBlankField(false)
                    .addField("Gender", character.getGender(), true)
                    .addField("Age", String.valueOf(character.getAge()), true)
                    .addBlankField(false)
                    .addField("Backstory", character.getBackstory(), false)
                    .setFooter("Author: " + creator.getName(), creator.getAvatarUrl());
            if (character.getColor() != null) {
                embed.setColor(character.getColor());
            }

            if (character.getAvatarUrl() != null && !character.getAvatarUrl().isBlank())
                embed.setImage(character.getAvatarUrl());

            event.replyEmbeds(embed.build())
                    .setEphemeral(true)
                    .queue();
        } else {
            event.reply("Character not found")
                    .setEphemeral(true)
                    .queue();
        }
    }

    @Override
    protected void onAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        if (event.getFocusedOption().getName().equals("char")) {
            String query = event.getFocusedOption().getValue();
            List<Command.Choice> choices = new ArrayList<>();
            repository.findByUserIdAndNameLike(event.getUser().getIdLong(), query)
                    .forEach(suggestion -> choices.add(new Command.Choice("(you)" + suggestion.getName(), suggestion.getCharacterId())));

            if (event.isGuildCommand()) {
                event.getGuild().getMembers()
                        .parallelStream()
                        .forEach(member -> {
                            repository.findAllByUserId(member.getIdLong())
                                    .stream().filter(originalCharacter -> originalCharacter.getName().contains(query))
                                    .forEach(originalCharacter -> {
                                        var choice = new Command.Choice("(" + member.getNickname() + ")" + originalCharacter.getName(), originalCharacter.getCharacterId());
                                        choices.add(choice);
                                    });

                        });
            }
            event.replyChoices(choices.subList(0, Math.min(25, choices.size()))).queue();
        }
    }
}
