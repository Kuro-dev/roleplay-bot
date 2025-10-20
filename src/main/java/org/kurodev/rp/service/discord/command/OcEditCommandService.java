package org.kurodev.rp.service.discord.command;

import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.modals.Modal;
import org.jetbrains.annotations.NotNull;
import org.kurodev.rp.db.entity.OriginalCharacter;
import org.kurodev.rp.db.repository.OriginalCharacterRepository;
import org.kurodev.rp.service.discord.DiscordCommand;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.List;
import java.util.Optional;

@Service
public class OcEditCommandService extends DiscordCommand {
    private final OriginalCharacterRepository repository;

    public OcEditCommandService(OriginalCharacterRepository repository) {
        super("oc-edit");
        this.repository = repository;
    }

    @Override
    protected void configCommand(SlashCommandData cmd) {
        cmd.setDescription("Edit a character. Any non-specified option will not be overridden");
        cmd.addOption(OptionType.INTEGER, "char-id", "Id of the character you want to edit. You can find it using `/oc-info`", true);
        cmd.addOption(OptionType.STRING, "name", "Change the name of the character", false);
        cmd.addOption(OptionType.STRING, "gender", "Change the gender of the character", false);
        cmd.addOption(OptionType.INTEGER, "age", "Change the age of the character", false);
        cmd.addOption(OptionType.STRING, "avatar-url", "Change the avatar url of the character", false);
        cmd.addOption(OptionType.STRING, "color", "Change the favourite color of the character", false);
        cmd.addOption(OptionType.BOOLEAN, "description", "Change the description of the character (opens a modal)", false);
    }

    @Override
    protected void executeCommand(@NotNull SlashCommandInteractionEvent event) {
        long id = event.getOption("char-id").getAsLong();

        Optional<OriginalCharacter> optional = repository.findByCharacterIdAndUserId(id, event.getUser().getIdLong());
        if (optional.isEmpty()) {
            event.reply("Character not found")
                    .setEphemeral(true)
                    .queue();
            return;
        }
        OriginalCharacter character = optional.get();
        if (event.getOption("gender") != null) {
            character.setGender(event.getOption("gender").getAsString());
        }
        if (event.getOption("age") != null) {
            character.setAge(event.getOption("age").getAsInt());
        }
        if (event.getOption("avatar-url") != null) {
            character.setAvatarUrl(event.getOption("avatar-url").getAsString());
        }
        if (event.getOption("color") != null) {

            String color = event.getOption("color").getAsString();
            Color colorObj = getColor(color);
            character.setColor(colorObj);
        }
        //save all the data until now
        repository.save(character);

        if (event.getOption("description") != null) {
            TextInput.Builder field = TextInput.create("backstory-edit", TextInputStyle.PARAGRAPH);
            field.setRequired(true);
            field.setValue(character.getBackstory());
            Modal m = Modal.create("edit-description-modal", "Edit Backstory")
                    .addComponents(TextDisplay.of(String.valueOf(character.getCharacterId())))
                    .addComponents(Label.of("Backstory", field.build()))
                    .build();
            event.replyModal(m)
                    .queue();
            return;
        }
        event.reply("Character edited").queue();

    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        if (event.getInteraction().getModalId().equals("backstory-edit")) {
            System.out.println(event.getInteraction().getValues());
        }
    }

    private Color getColor(String color) {
        try {
            return Color.getColor(color);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        if (event.getFocusedOption().getName().equals("char-id")) {
            String query = event.getFocusedOption().getValue();
            List<Command.Choice> choices = repository.findAllByUserId(event.getUser().getIdLong()).stream()
                    .filter(c -> String.valueOf(c.getCharacterId()).contains(query))
                    .map(c -> new Command.Choice(c.getName(), c.getCharacterId())).toList();
            event.replyChoices(choices).queue();
        }
    }
}
