import { Message, TextChannel } from "discord.js";
import Command from "../../Models/Command";
import MessageCtx from "../../Models/MessageCtx";

export default class BotInfo extends Command {
    constructor() {
        super(
            "Delete",
            "Delete an amount of messages",
            ["delete", "del"],
            null
        )
    }

    public async execute(ctx: MessageCtx): Promise<void> {
        var deleteAmount = 1;
        if (ctx.args.length >= 1) {
            deleteAmount = Number(ctx.args[0]);
            if (isNaN(deleteAmount)) {
                ctx.send(`Not a valid amount to delete: ${ctx.args[0]}`)
                return;
            }
        }
        deleteAmount += 1;
        const times = Math.floor(deleteAmount / 100);

        for (var i = 0; i < times; i++) {
            ctx.channel.bulkDelete(100)
        }

        deleteAmount -= times * 100;
        ctx.channel.bulkDelete(deleteAmount);
    }

    public getInteraction() {
        return {
            "name": "delete",
            "description": "Delete messages",
            "options": [
                {
                    "type": 4,
                    "name": "amount",
                    "description": "Amount of messages to delete, 1 if this argument isn't given",
                    "required": false
                }
            ]
        }

    }

}
