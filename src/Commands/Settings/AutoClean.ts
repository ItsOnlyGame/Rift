import { Message } from 'discord.js'
import GuildSettings from '../../Guilds/GuildSettings';

export default {
    name: "Audo clean",
    description: "Automatically clean the command messages",
    aliases: ["autoclean"],
    permissionReq:  null,
    /**
     * Executes the command
     * @param {Message} message 
     * @param {Array<string>} args
     */
    execute: async function(message: Message, args: Array<string>) {
        const guildSettings = GuildSettings.getGuildSettings(message.guild.id, message.client)

        if (args.length == 0) {
            message.channel.send("Current Auto Clean mode is ``"+guildSettings.autoclean+"``")
            return
        }

        const newAutoClean = args[0].trim().toLowerCase();
        if (newAutoClean == "true") {
            message.channel.send("Auto Clean mode set to ``"+newAutoClean+"``")
            guildSettings.autoclean = Boolean(newAutoClean);
            guildSettings.save()

        } else if (newAutoClean == "false") {

            message.channel.send("Auto Clean mode set to ``"+newAutoClean+"``")
            guildSettings.autoclean = Boolean(newAutoClean);
            guildSettings.save()
        }

        throw "Auto clean can only be set to true or false"
    },
};