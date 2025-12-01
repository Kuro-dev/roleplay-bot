package org.kurodev.rp.service.discord.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;
import org.kurodev.rp.db.entity.OriginalCharacter;
import org.kurodev.rp.db.repository.OriginalCharacterRepository;
import org.kurodev.rp.service.discord.DiscordCommand;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OcMessageCommandService extends DiscordCommand {

    private final OriginalCharacterRepository repository;

    // Store pending interactions: userId -> Interaction metadata
    private final Map<Long, PendingInteraction> pending = new ConcurrentHashMap<>();

    public OcMessageCommandService(OriginalCharacterRepository repository) {
        super("oc-msg");
        this.repository = repository;
    }

    @Override
    protected void configCommand(SlashCommandData cmd) {
        cmd.setDescription("Write a message as your original Character");

        cmd.addOption(OptionType.STRING, "char", "Name of the character", true, true);
        cmd.addOption(OptionType.STRING, "location", "Location of where this takes place");
    }

    @Override
    protected void executeCommand(@NotNull SlashCommandInteractionEvent event) {
        String characterName = event.getOption("char").getAsString();
        String location = event.getOption("location") != null
                ? event.getOption("location").getAsString()
                : null;

        Optional<OriginalCharacter> character =
                repository.findByUserIdAndNameEquals(event.getUser().getIdLong(), characterName);

        if (character.isEmpty()) {
            event.reply("Character not found!").setEphemeral(true).queue();
            return;
        }

        event.reply("Okay! Now type your message below in chat. I'll format it for you.")
                .setEphemeral(true)
                .queue(hook -> hook.retrieveOriginal().queue(msg -> {
                    PendingInteraction data = new PendingInteraction(
                            event.getUser().getIdLong(),
                            event.getChannel().getIdLong(),
                            character.get().getCharacterId(),
                            location,
                            msg.getIdLong()
                    );
                    pending.put(event.getUser().getIdLong(), data);
                }));
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        User user = event.getAuthor();
        if (user.isBot()) return;

        PendingInteraction pendingData = pending.get(user.getIdLong());
        if (pendingData == null) return; // user not in pending state

        // must be in same channel as the pending interaction
        if (event.getChannel().getIdLong() != pendingData.channelId) return;

        long charId = pendingData.characterId;
        String location = pendingData.location;
        Message message = event.getMessage();

        // Retrieve character from database
        Optional<OriginalCharacter> character =
                repository.findById(charId);
        if (character.isEmpty()) {
            event.getChannel().sendMessage("Couldn't find your character anymore.").queue();
            pending.remove(user.getIdLong());
            return;
        }

        OriginalCharacter oc = character.get();

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(oc.getName())
                .setFooter("Written by " + user.getName(), user.getAvatarUrl());

        if (oc.getColor() != null) {
            embed.setColor(oc.getColor());
        }
        if (oc.getAvatarUrl() != null && !oc.getAvatarUrl().isBlank()) {
            embed.setThumbnail(oc.getAvatarUrl());
        }

        if (!message.getMentions().getUsers().isEmpty()) {
            String mentions = message.getMentions().getUsers().stream()
                    .map(User::getAsMention)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
            embed.addField("Mentions", mentions, false);
        }

        if (location != null && !location.isBlank()) {
            embed.addField("Location", location, false);
        }

        String cleanedMsg = message.getContentDisplay()
                .replaceAll("@\\S+", "");

        embed.addField("Description", cleanedMsg, false);

        if (event.isFromGuild() && event.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_MANAGE)) {

            event.getMessage().delete().queue();
            event.getChannel().sendMessageEmbeds(embed.build())
                    .queue();
        } else {
            event.getMessage()
                    .replyEmbeds(embed.build())
                    .queue();
        }

        pending.remove(user.getIdLong());
    }

    @Override
    protected void onAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        switch (event.getFocusedOption().getName()) {
            case "char" -> {
                String query = event.getFocusedOption().getValue();
                List<OriginalCharacter> suggestions =
                        repository.findByUserIdAndNameLike(event.getUser().getIdLong(), query);
                event.replyChoiceStrings(
                        suggestions.stream()
                                .limit(25)
                                .map(OriginalCharacter::getName)
                                .toList()
                ).queue();
            }
        }
    }

    // simple record to store state temporarily
    private record PendingInteraction(
            long userId,
            long channelId,
            long characterId,
            String location,
            long promptMessageId
    ) {
    }
}
