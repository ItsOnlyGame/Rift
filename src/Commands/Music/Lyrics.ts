import { Message } from "discord.js";
import { ErelaManager } from "../../Models/LavaplayerManager";
import lyricsFinder from 'lyrics-finder';
import Command from "../../Models/Command";
import MessageCtx from "../../Models/MessageCtx";

export default class Lyrics extends Command {
    constructor() {
        super(
            "Lyrics", 
            "Fetches track lyrics from google",
            ["lyrics"],
            null
        )
    }

    public async execute(ctx: MessageCtx): Promise<void> {
        const player = ErelaManager.get(ctx.channel.guild.id)   
        if (player == undefined) {
            if (ctx.args.length == 0) {
                ctx.send("No search query given nor was nothing is playing!");
                return;
            }

            const lyrics: string = await lyricsFinder(ctx.args.join("").trim()) || 'No lyrics found!'
            var splits = this.splitLyrics(lyrics)
            for (var i = 0; i < splits.length; i++) {
                var text = "```";
                if (i == 0) {
                    text += "\nLyrics for "+ ctx.args.join("").trim() + "\n\n";
                } else {
                    text += "\n"
                }
                text += splits[i] + "```"
                ctx.send(text)
            }

            return;
        }

        if (player.queue.length == 0 && player.queue.current == undefined) {
            ctx.send("Nothing is playing!");
            return;
        }
        

        const trackTitle = `${player.queue.current.title}`;
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
            ctx.send(text)
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

    public getInteraction() {
        return {
            "name": "lyrics",
            "description": "Fetches track lyrics from google",
            "options": [
              {
                "type": 3,
                "name": "title",
                "description": "Title of the track",
                "required": false
              }
            ]
          }
          
    }
}