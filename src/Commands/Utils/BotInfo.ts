import { Message } from 'discord.js'
import Utils from '../../Utils';

export default {
    name: "Bot Info",
    description: "Bot information",
    aliases: ["bot"],
    permissionReq: null,
    /**
     * Executes the command
     * @param {Message} message 
     * @param {Array<string>} args
     */
    execute: function(message: Message, args: Array<string>) {
        const str = 
            "```"+
            "Rift: "+"\n"+
            "Version: " + Utils.Version + "\n" +
            "ID: " + message.client.user.id+ "\n" +
            "Github: " + "**https://github.com/ItsOnlyGame/Rift**" + "\n\n" +
            "You can report you issues to github\n```";
            "````"

        message.channel.send(str)
    },
};