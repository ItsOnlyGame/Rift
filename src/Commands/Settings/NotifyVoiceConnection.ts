import { Message, PermissionsBitField } from 'discord.js'
import GuildSettings from '../../Guilds/GuildSettings';
import Command from '../../Models/Command';

export default class NotifyVoiceConnection extends Command {
    constructor() {
        super(
            "Notify Voice Connection",
            "Whether to tell if bot is (dis)connecting from/to a voice channel",
            ["notifyvc"],
            [PermissionsBitField.Flags.Administrator]
        )
    }

    public async execute(message: Message, args: string[]): Promise<void> {
        const guildSettings = GuildSettings.getGuildSettings(message.guildId, message.client)

        if (args.length == 0) {
            message.channel.send("Notify voice connection mode is ``"+guildSettings.notifyVoiceConnection+"``")
            return
        }

        var newNotifyVoiceConnection = args[0].trim().toLowerCase();
        if (newNotifyVoiceConnection == "true") {
            message.channel.send("Notify voice connection mode set to ``"+newNotifyVoiceConnection+"``")
            guildSettings.notifyVoiceConnection = true
            GuildSettings.saveGuildSettings(guildSettings)
            return;

        } else if (newNotifyVoiceConnection == "false") {

            message.channel.send("Notify voice connection mode set to ``"+newNotifyVoiceConnection+"``")
            guildSettings.notifyVoiceConnection = false
            GuildSettings.saveGuildSettings(guildSettings)
            return;

        }

        message.channel.send("Notify voice connection can only be set to true or false")
    }

}
