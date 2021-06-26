import { Message, TextChannel } from "discord.js";

export default {
    name: "Delete",
    description: "Delete an amount of messages",
    aliases: ["delete", "del"],
    permissionReq: null,
    /**
     * Executes the command
     * @param {Message} message 
     * @param {Array<string>} args
     */
    execute: async function(message: Message, args: Array<string>) {

        var deleteAmount = 1;
        if (args.length >= 1) {
            deleteAmount = Number(args[0]);
            if (isNaN(deleteAmount)) {
                message.channel.send(`Not a valid amount to delete: ${args[0]}`)
                return;
            }
        }

        (message.channel as TextChannel).bulkDelete(deleteAmount + 1)
    },
};