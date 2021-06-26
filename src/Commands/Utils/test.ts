import { Message } from 'discord.js'

export default {
    name: "test",
    description: "Test command",
    aliases: ["test"],
    permissionReq: ['ADMINISTRATOR'],
    /**
     * Executes the command
     * @param {Message} message 
     * @param {Array<string>} args
     */
    execute: function(message: Message, args: Array<string>) {
        message.reply("Test! " + args)
    },
};