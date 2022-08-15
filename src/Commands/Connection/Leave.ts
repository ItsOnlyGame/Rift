import { getVoiceConnection } from "@discordjs/voice";
import { Message, TextChannel } from "discord.js";
import { distube } from "../../Utils/AudioManager";
import Command from "../../Models/Command";

export default class Leave extends Command {
    constructor() {
        super(
            "Leave", 
            "Leave the voice channel",
            ["leave", "disconnect", "dc", "dis"],
            null
        )
    }

    public async execute(message: Message, args: string[]): Promise<void> {
        const memberVoiceConnection = message.member.voice;
        const existingConnection = getVoiceConnection((message.channel as TextChannel).guildId)
    
        if (existingConnection != null) {
    
            if (!memberVoiceConnection) {
                message.channel.send('You have to be in the same voice channel as I to do that!')
                return;
            }
    
            if (existingConnection.joinConfig.channelId != memberVoiceConnection.channel.id) {
                message.channel.send('You have to be in the same voice channel as I to do that!')
                return;
            }
    
            distube.voices.leave(message);
            message.channel.send(`Leaving voice channel`)
            return;
        }
    
        message.channel.send('Not connected to a voice channel')
    }
}