import { Message } from "discord.js";
import { ErelaManager } from "../../Models/LavaplayerManager";

export default {
    name: "Join",
    description: "Join the voice channel",
    aliases: ["join", "connect", "c"],
    permissionReq: null,
    /**
     * Executes the command
     * @param {Message} message 
     * @param {Array<string>} args
     */
    execute: async function(message: Message, args: Array<string>) {
        if (!ErelaManager.get(message.guild.id)) {
            const player = ErelaManager.create({
                guild: message.guild.id,
                voiceChannel: message.member.voice.channel.id,
                textChannel: message.channel.id,
            });

            player.connect();
            message.channel.send(`Connecting to ${message.member.voice.channel.name}`)

        } else {
            message.channel.send(`Already connected to a voice channel`)
        }
    },
};