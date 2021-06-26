import { Message } from "discord.js";
import { ErelaManager } from "../../Models/LavaplayerManager";

export default {
    name: "Leave",
    description: "Leave the voice channel",
    aliases: ["leave", "disconnect", "dc", "dis"],
    permissionReq: null,
    /**
     * Executes the command
     * @param {Message} message 
     * @param {Array<string>} args
     */
    execute: async function(message: Message, args: Array<string>) {
        const player = ErelaManager.get(message.guild.id)
        if (player) {
            player.disconnect();
            player.destroy();
            message.channel.send(`Disconnecting from ${message.member.voice.channel.name}`)

        } else {
            message.channel.send(`I'm not connected to any voice channel`)
        }
    },
};