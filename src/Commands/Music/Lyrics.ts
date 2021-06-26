import { Message } from "discord.js";
import { ErelaManager } from "../../Models/LavaplayerManager";
import lyricsFinder from 'lyrics-finder';

export default {
    name: "Lyrics",
    description: "Fetches lyrics from google",
    aliases: ["lyrics"],
    permissionReq: null,
    
    /**
     * Executes the command
     * @param {Message} message 
     * @param {Array<string>} args
     */
    execute: async function(message: Message, args: Array<string>) { 
        const player = ErelaManager.get(message.guild.id)   
        if (player == undefined) {
            if (args.length == 0) {
                message.channel.send("No search query given nor was nothing is playing!");
                return;
            }

            const lyrics: string = await lyricsFinder(args.join("").trim()) || 'No lyrics found!'
            var splits = splitLyrics(lyrics)
            for (var i = 0; i < splits.length; i++) {
                var text = "```";
                if (i == 0) {
                    text += "\nLyrics for "+ args.join("").trim() + "\n\n";
                } else {
                    text += "\n"
                }
                text += splits[i] + "```"
                message.channel.send(text)
            }

            return;
        }

        if (player.queue.length == 0 && player.queue.current == undefined) {
            message.channel.send("Nothing is playing!");
            return;
        }
        

        const trackTitle = `${player.queue.current.title}`;
        const lyrics: string = await lyricsFinder(trackTitle) || 'No lyrics found!'

        var splits = splitLyrics(lyrics)
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
    },
};

function splitLyrics(lyrics: string) {
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