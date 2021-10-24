import { HexColorString, Message, MessageEmbed, TextChannel } from "discord.js";
import Command from "../../Models/Command";
import { distube } from "../../Models/AudioManager";
import Config from "../../Config";

export default class TrackInfo extends Command {
    constructor() {
        super(
            "Track Info",
            "Get the info of the track thats currently playing",
            ["trackinfo", "tf", "info"],
            null
        )
    }

    public async execute(message: Message, args: string[]): Promise<void> {
        
        const queue = distube.getQueue(message);

        if (!queue) {
            message.channel.send("Queue is empty!");
            return;
        }

        if (queue.songs.length == 0) {
            message.channel.send("Queue is empty!");
            return;
        }

        if (queue.voiceChannel.id != message.member.voice.channel.id) {
            message.channel.send("You need to be in the same voice channel as I")
            return;
        }

        const track = queue.songs[0];

        const embed = new MessageEmbed()
            .setColor(Config.getConfig().defaultColors.success as HexColorString)
            .setAuthor('Info', message.member.user.avatarURL(), null)
            .addField('Title', track.name,  false)
            .addField('Authors', track.uploader.name,  false)
            .addField('Duration', `${track.duration}`,  false)
            .addField('Url', track.url,  false)

        if (track.thumbnail)
            embed.setThumbnail(track.thumbnail)
    
        message.channel.send({embeds: [embed]});
        
    }

}