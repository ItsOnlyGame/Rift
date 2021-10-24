import { getVoiceConnection } from "@discordjs/voice";
import { Message, TextChannel } from "discord.js";
import { distube } from "../../Models/AudioManager";
import Command from "../../Models/Command";

export default class Join extends Command {
    constructor() {
        super(
            "Join", 
            "Join the voice channel",
            ["join", "connect", "c"],
            null
        )
    }

    public async execute(message: Message, args: string[]): Promise<void> {
        const memberVoiceConnection = message.member.voice;
        
        if (!memberVoiceConnection) {
            message.channel.send('You have to be in a voice channel!')
            return;
        }

        if (!memberVoiceConnection.channel) {
            message.channel.send('You have to be in a voice channel!')
            return;
        }
    
        var connection = getVoiceConnection((message.channel as TextChannel).guildId)
        
        if (connection != null) {
            if (connection.joinConfig.channelId != memberVoiceConnection.channel.id) {
                message.channel.send('Currently connected to another voice channel!')
                return;
            }
            return null;
        } 

        distube.voices.join(memberVoiceConnection.channel).then(() => {
            message.channel.send(`Joined ${memberVoiceConnection.channel.name}!`)
        });
    }

}
