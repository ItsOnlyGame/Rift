import { Message } from 'discord.js';
import lyricsFinder from 'lyrics-finder';
import { distube } from '../../Utils/AudioManager';
import Command from "../../Models/Command";

export default class Lyrics extends Command {
    constructor() {
        super(
            "Lyrics", 
            "Fetches track lyrics from google",
            ["lyrics"],
            null
        )
    }

    public async execute(message: Message, args: string[]): Promise<void> {
        const queue = distube.getQueue(message);
        
        if (queue == undefined) {
            if (args.length == 0) {
                message.channel.send("No search query given nor was nothing is playing!");
                return;
            }

            const lyrics: string = await lyricsFinder(args.join(" ").trim()) || 'No lyrics found!'

            var splits = this.splitLyrics(lyrics)
            for (var i = 0; i < splits.length; i++) {
                var text = "```";
                if (i == 0) {
                    text += "\nLyrics for "+ args.join(" ").trim() + "\n\n";
                } else {
                    text += "\n"
                }
                text += splits[i] + "```"
                message.channel.send(text)
            }

            return;
        }

        if (queue.songs.length == 0) {
            message.channel.send("Nothing is playing!");
            return;
        }
        

        const trackTitle = `${queue.songs[0].name}`;
        const lyrics: string = await lyricsFinder(trackTitle) || 'No lyrics found!'

        var splits = this.splitLyrics(lyrics)
        for (var i = 0; i < splits.length; i++) {
            var text = "```";
            if (i == 0) {
                text += "\nLyrics for "+ trackTitle + "\n\n";
            } else {
                text += "\n"
            }
            text += splits[i] + "```"
            message.channel.send(text)
        }
    }

    
    splitLyrics(lyrics: string) {
        var partitionSize = 2000;
        const parts = []

        var array = lyrics.split("\n\n");
        if (array.length == 1)
            array = lyrics.split("\n");

        var temp = ""
        var len = 0;

        for (var str of array) {
            len += str.length + 4;

            if (len >= partitionSize) {
                parts.push(temp);
                temp = ""
                temp += str;
                len = 0;
            } else {
                temp += `${str} \n\n`;
            }
        }
        if (len != 0) {
            parts.push(temp);
        }

        return parts;
    }

}