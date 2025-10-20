package org.kurodev.rp.service.discord.command;

import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.modals.Modal;
import org.jetbrains.annotations.NotNull;
import org.kurodev.rp.db.entity.OriginalCharacter;
import org.kurodev.rp.db.repository.OriginalCharacterRepository;
import org.kurodev.rp.service.discord.DiscordCommand;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CreateOcCommandService extends DiscordCommand {
    private final OriginalCharacterRepository repository;

    public CreateOcCommandService(OriginalCharacterRepository repository) {
        super("oc-create");
        this.repository = repository;
    }

    @Override
    public void configCommand(SlashCommandData cmd) {
        cmd.setNSFW(false);
        cmd.setDescription("Create a new Character");
    }

    private Modal createModal() {
        TextInput nameInput = TextInput.create("name", TextInputStyle.SHORT)
                .setRequired(true)
                .build();

        TextInput ageInput = TextInput.create("age", TextInputStyle.SHORT)
                .setRequired(true)
                .build();

        TextInput genderInput = TextInput.create("gender", TextInputStyle.SHORT)
                .setRequired(false)
                .build();

        TextInput avatarInput = TextInput.create("avatarUrl", TextInputStyle.SHORT)
                .setRequired(false)
                .setPlaceholder("https://example.com/avatar.png")
                .build();

        TextInput descriptionInput = TextInput.create("description", TextInputStyle.PARAGRAPH)
                .setRequired(true)
                .build();

        return Modal.create("create_character_modal", "Create New Character")
                .addComponents(
                        Label.of("Name", nameInput),
                        Label.of("Age", ageInput),
                        Label.of("Gender", genderInput),
                        Label.of("Avatar Url", "URL of your avatar.", avatarInput),
                        Label.of("Description", descriptionInput)
                )
                .build();
    }

    @Override
    public void onModalInteraction(@Nonnull ModalInteractionEvent event) {
        if (event.getModalId().equals("create_character_modal")) {
            var oc = OriginalCharacter.builder();
            try {
                oc.age(Integer.parseInt(event.getValue("age").getAsString()));
            } catch (NumberFormatException e) {
                event.reply("Age must be a valid number!").setEphemeral(true).queue();
                return;
            }
            oc.name(event.getValue("name").getAsString());
            oc.gender(event.getValue("gender") != null ? event.getValue("gender").getAsString() : "Undefined");
            oc.avatarUrl(event.getValue("avatarUrl") != null ? event.getValue("avatarUrl").getAsString() : null);
            oc.description(event.getValue("description").getAsString());
            oc.userId(event.getUser().getIdLong());

            OriginalCharacter chara = repository.save(oc.build());
            log.info("User {} Successfully created a new OC: {}", event.getUser().getName(), chara.getName());
            event.reply("Character created!").setEphemeral(true).queue();
        }
    }

    @Override
    protected void executeCommand(@NotNull SlashCommandInteractionEvent event) {
        Modal modal = createModal();
        event.replyModal(modal).queue();
    }

    @Override
    protected void onAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {

    }
}
