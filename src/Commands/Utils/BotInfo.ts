import { Message } from 'discord.js'
import Command from '../../Models/Command';
import MessageCtx from '../../Models/MessageCtx';
import Utils from '../../Utils';

export default class BotInfo extends Command {
    constructor() {
        super(
            "Bot Info",
            "Bot information",
            ["bot"],
            null
        )
    }

    public async execute(ctx: MessageCtx): Promise<void> {
        const str =
            "```" +
            "Rift: " + "\n" +
            "Version: " + Utils.Version + "\n" +
            "ID: " + ctx.channel.client.user.id + "\n" +
            "Github: " + "**https://github.com/ItsOnlyGame/Rift**" + "\n\n" +
            "You can report you issues to github\n```";

        ctx.send(str)
    }

    public getInteraction() {
        return {
            "name": "bot",
            "description": "Bot info"
        }
    }

}