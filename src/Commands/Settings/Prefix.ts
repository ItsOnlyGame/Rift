import { Message } from 'discord.js'
import GuildSettings from '../../Guilds/GuildSettings';

export default {
    name: "Prefix",
    description: "Change prefix",
    aliases: ["prefix"],
    permissionReq:  null,
    /**
     * Executes the command
     * @param {Message} message 
     * @param {Array<string>} args
     */
    execute: async function(message: Message, args: Array<string>) {
        const guildSettings = GuildSettings.getGuildSettings(message.guild.id, message.client)

        if (args.length == 0) {
            message.channel.send("Current prefix is ``"+guildSettings.prefix+"``")
            return
        }

        const newprefix = args[0].trim();
        message.channel.send("Prefix changed from ``"+guildSettings.prefix+"`` to ``"+newprefix+"``")
        guildSettings.prefix = newprefix;
        guildSettings.save()
    },
};