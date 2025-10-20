package org.kurodev.rp.service.discord;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;

public abstract class DiscordCommand extends ListenerAdapter {

    private final String commandName;

    public DiscordCommand(String commandName) {
        this.commandName = commandName;
    }

    public CommandData getCommand() {
        SlashCommandData cmd = Commands.slash(commandName, "No description provided");
        configCommand(cmd);
        return cmd;
    }

    protected abstract void configCommand(SlashCommandData cmd);

    @Override
    public final void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equalsIgnoreCase(commandName)) {
            executeCommand(event);
        }
    }

    @Override
    public final void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        if (event.getName().equalsIgnoreCase(commandName)) {
            onAutoCompleteInteraction(event);
        }
    }

    protected abstract void executeCommand(@NotNull SlashCommandInteractionEvent event);

    protected abstract void onAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event);


}
