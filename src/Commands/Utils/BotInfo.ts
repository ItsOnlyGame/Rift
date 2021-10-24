import { Message } from 'discord.js'
import Command from '../../Models/Command';

export default class BotInfo extends Command {
    constructor() {
        super(
            "Bot Info",
            "Bot information",
            ["bot"],
            null
        )
    }

    public async execute(message: Message, args: string[]): Promise<void> {
        const str =
            "```" +
            "Rift: " + "\n" +
            "Version: " + process.env.npm_package_version + "\n" +
            "ID: " + message.channel.client.user.id + "\n" +
            "Github: " + "https://github.com/ItsOnlyGame/Rift" + "\n\n" +
            "You can report you issues to github\n```";

        message.channel.send(str)
    }

}