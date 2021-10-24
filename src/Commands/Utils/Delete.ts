import { Message, TextChannel } from "discord.js";
import Command from "../../Models/Command";

export default class BotInfo extends Command {
    constructor() {
        super(
            "Delete",
            "Delete an amount of messages",
            ["delete", "del"],
            null
        )
    }

    public async execute(message: Message, args: string[]): Promise<void> {
        var deleteAmount = 1;
        if (args.length >= 1) {
            deleteAmount = Number(args[0]);
            if (isNaN(deleteAmount)) {
                message.channel.send(`Not a valid amount to delete: ${args[0]}`)
                return;
            }
        }
        deleteAmount += 1;
        const times = Math.floor(deleteAmount / 100);

        for (var i = 0; i < times; i++) {
            (message.channel as TextChannel).bulkDelete(100)
        }

        deleteAmount -= times * 100;
        (message.channel as TextChannel).bulkDelete(deleteAmount);
    }
}
