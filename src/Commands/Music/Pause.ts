import { Message } from "discord.js";
import GuildSettings from "../../Guilds/GuildSettings";
import { ErelaManager } from "../../Models/LavaplayerManager";

export default {
    name: "Pause",
    description: "Pause track",
    aliases: ["pause"],
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

        const player = ErelaManager.get(message.guild.id)   
        if (player == undefined) {
            message.channel.send("Nothing is currenly playing");
            return;
        }

        if (player.queue.length == 0 && player.queue.current == undefined) {
            message.channel.send("Queue is empty!");
            return;
        }

        if (player.voiceChannel != message.member.voice.channel.id) {
            message.channel.send("You need to be in the same voice channel as I")
            return;
        }

        if (player.paused) {
            player.pause(false);
        } else {
            player.pause(true)
        }
    },
};