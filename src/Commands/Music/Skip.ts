import { Message } from "discord.js";
import GuildSettings from "../../Guilds/GuildSettings";
import { ErelaManager } from "../../Models/LavaplayerManager";

export default {
    name: "Skip",
    description: "Skips tracks",
    aliases: ["skip", "s"],
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

        if (player == undefined) {
            message.channel.send("Queue is empty!");
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


        var skipAmount = 1;
        if (args.length >= 1) {
            skipAmount = Number(args[0]);
            if (isNaN(skipAmount)) {
                message.channel.send(`Not a valid skip amount: ${args[0]}`)
                return;
            }
        }
        
        player.stop(skipAmount)
    },
};