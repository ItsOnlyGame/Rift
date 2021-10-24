import { Message, Permissions } from 'discord.js'
import GuildSettings from '../../Guilds/GuildSettings';
import Command from '../../Models/Command';

export default class AutoClean extends Command {
    constructor() {
        super(
            "Auto Clean",
            "Automatically clean the command messages",
            ["autoclean"],
            [Permissions.FLAGS.ADMINISTRATOR]
        )
    }

    public async execute(message: Message, args: string[]): Promise<void> {
        const guildSettings = GuildSettings.getGuildSettings(message.guildId, message.client)

        if (args.length == 0) {
            message.channel.send("Current Auto Clean mode is ``" + guildSettings.autoclean + "``")
            return
        }

        var newAutoClean = args[0].trim().toLowerCase();
        if (newAutoClean == "true") {
            message.channel.send("Auto Clean mode set to ``" + newAutoClean + "``")
            guildSettings.autoclean = true
            GuildSettings.saveGuildSettings(guildSettings)
            return;

        } else if (newAutoClean == "false") {

            message.channel.send("Auto Clean mode set to ``" + newAutoClean + "``")
            guildSettings.autoclean = false
            GuildSettings.saveGuildSettings(guildSettings)
            return;

        }

        message.channel.send("Auto clean can only be set to true or false")
    }

}
