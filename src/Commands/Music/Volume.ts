import { Message } from 'discord.js'
import GuildSettings from '../../Guilds/GuildSettings';
import { ErelaManager } from '../../Models/LavaplayerManager';

export default {
    name: "Volume",
    description: "Change volume",
    aliases: ["volume", "vol", "v"],
    permissionReq: null,
    /**
     * Executes the command
     * @param {Message} message 
     * @param {Array<string>} args
     */
    execute: async function(message: Message, args: Array<string>) {
        const guildSettings = GuildSettings.getGuildSettings(message.guild.id, message.client)

        if (guildSettings.dj_role != null) {
            if (message.member.roles.cache.get(guildSettings.dj_role) == undefined) {
                return message.channel.send("You are not a dj")
            }
        }

        const player = ErelaManager.get(message.guild.id);

        if (args.length >= 1) {
            var newvolume = Number(args[0]);
            if (isNaN(newvolume)) {
                message.channel.send(`Not a valid volume: ${args[0]}`)
                return;
            }

            if (player != undefined) {
                player.setVolume(newvolume)
                if (player.voiceChannel != message.member.voice.channel.id) {
                    message.channel.send("You need to be in the same voice channel as I")
                    return;
                }
            }

            guildSettings.volume = newvolume;
            guildSettings.save();

            message.channel.send(`Volume set to ${newvolume}`)
            return;
        }

        message.channel.send(`Volume is ${guildSettings.volume}`)
    },
};