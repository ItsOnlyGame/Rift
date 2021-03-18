package com.iog.Commands.Other;

import com.iog.Handlers.Commands.BasicCommand;
import com.iog.Handlers.GuildSettings;
import com.iog.Utils.Format;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import reactor.core.publisher.Flux;

public class Delete extends BasicCommand {

    public Delete() {
        super(
                new String[]{"delete", "del"},
                "Deletes messages"
        );
    }

    @Override
    public void run(Message message, String[] args) {
        Integer count;
        if (args.length == 0) {
            count = 1;
        } else {
            count = Format.toInteger(args[0]);

            if (count == null) {
                message.getChannel().subscribe(channel -> channel.createMessage("Given argument wasn't a valid number").subscribe());
                return;
            }
        }

        if (count == 1) {
            message.getChannel().cast(TextChannel.class).subscribe(channel -> Flux.just(message.getId())
                    .flatMap(channel::getMessagesBefore)
                    .take(1)
                    .flatMap(Message::delete)
                    .subscribe());

        } else {
            message.getChannel().cast(TextChannel.class).subscribe(channel -> channel.getMessagesBefore(message.getId())
                    .take(count)
                    .transform(channel::bulkDeleteMessages)
                    .subscribe());
        }
        if (!GuildSettings.of(message.getGuildId().orElseThrow().asLong()).isAutoClean()) {
            message.delete().subscribe();
        }
    }
}
