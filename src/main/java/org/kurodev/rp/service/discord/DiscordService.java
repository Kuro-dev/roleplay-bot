package org.kurodev.rp.service.discord;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DiscordService extends ListenerAdapter {

    private final JDA jda;
    private final List<DiscordCommand> commands;


    public DiscordService(@Value("${discord.token}") String token, List<DiscordCommand> commands) {
        jda = JDABuilder.createDefault(token).build();
        this.commands = commands;
    }

    @PostConstruct
    public void init() {
        commands.forEach(c -> {
            log.info("Registered '{}' Command listener", c.getCommand().getName());
            jda.upsertCommand(c.getCommand()).queue();
            jda.addEventListener(c);
        });
    }


}
