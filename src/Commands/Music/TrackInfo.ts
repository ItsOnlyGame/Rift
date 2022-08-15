import { EmbedBuilder, HexColorString, Message, TextChannel } from "discord.js";
import Command from "../../Models/Command";
import { distube } from "../../Utils/AudioManager";
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

        const embed = new EmbedBuilder()
            .setColor(Config.getConfig().defaultColors.success as HexColorString)
            .setAuthor({ name: 'Info', iconURL: message.member.user.avatarURL()})
            .addFields([
                { name: 'Title', value: track.name, inline: false },
                { name: 'Duration', value: `${track.duration}`, inline: false },
                { name: 'Url', value: track.url, inline: false }
            ])

        if (track.thumbnail)
            embed.setThumbnail(track.thumbnail)
    
        message.channel.send({embeds: [embed]});
        
    }

}