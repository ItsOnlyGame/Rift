import { Message } from 'discord.js'

export default {
    name: "Help",
    description: "Help",
    aliases: ["help"],
    permissionReq: null,
    /**
     * Executes the command
     * @param {Message} message 
     * @param {Array<string>} args
     */
    execute: function(message: Message, args: Array<string>) {
        const str = 
            "```"+
            "Help \n"+
            "Every command is listen under this link: "+
            "https://github.com/ItsOnlyGame/Rift#commands"+
            "````"
        message.channel.send(str)
    },
};