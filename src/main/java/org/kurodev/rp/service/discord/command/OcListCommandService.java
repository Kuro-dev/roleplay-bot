package org.kurodev.rp.service.discord.command;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;
import org.kurodev.rp.db.repository.OriginalCharacterRepository;
import org.kurodev.rp.service.discord.DiscordCommand;
import org.springframework.stereotype.Service;

@Service
public class OcListCommandService extends DiscordCommand {
    private final OriginalCharacterRepository repository;

    public OcListCommandService(OriginalCharacterRepository repository) {
        super("oc-list");
        this.repository = repository;
    }

    @Override
    protected void configCommand(SlashCommandData cmd) {
        cmd.setDescription("Lists all your Ocs");
    }

    @Override
    protected void executeCommand(@NotNull SlashCommandInteractionEvent event) {
        var result = repository.findAllByUserId(event.getUser().getIdLong());

        if (result.isEmpty()) {
            event.reply("You don't have any characters yet")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        StringBuilder reply = new StringBuilder("Here are all your Characters:\n");
        result.forEach(c -> {
            reply.append("* ").append(c.getName()).append("\n");
        });
        event.reply(reply.toString())
                .setEphemeral(true)
                .queue();
    }

    @Override
    protected void onAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {

    }
}
