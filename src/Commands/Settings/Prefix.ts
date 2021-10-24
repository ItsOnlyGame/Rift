import { Message, Permissions } from 'discord.js'
import GuildSettings from '../../Guilds/GuildSettings';
import Command from '../../Models/Command';

export default class Prefix extends Command {
    constructor() {
        super(
            "Prefix",
            "Change prefix",
            ["prefix"],
            [Permissions.FLAGS.ADMINISTRATOR]
        )
    }

    public async execute(message: Message, args: string[]): Promise<void> {
        const guildSettings = GuildSettings.getGuildSettings(message.guildId, message.client)
        if (args.length == 0) {
            message.channel.send("Current prefix is ``" + guildSettings.prefix + "``")
            return
        }

        var newprefix = args[0].trim();
        message.channel.send("Prefix changed from ``" + guildSettings.prefix + "`` to ``" + newprefix + "``")
        guildSettings.prefix = newprefix;
        GuildSettings.saveGuildSettings(guildSettings)
    }

}
