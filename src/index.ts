import * as fs from 'fs'
import { Client } from "discord.js";
import GuildSettings from './Guilds/GuildSettings';
import { ErelaManager, initErela } from './Models/LavaplayerManager';
import getConfig from './Config';

const client = new Client();
initErela(client)

const commands: any[] = [];

/**
 * Loads every command from commands
 * @param {string} path 
 */
async function loadCommandsFromDir(path: string) {
    const content = fs.readdirSync(path);
    for (var file of content) {
        if (file.endsWith(".ts")) {
            const command = await import(`${path.replace("./src", ".")}/${file}`);
            commands.push(command.default);
        } else {
            loadCommandsFromDir(`${path}/${file}`);
        }
    }
}


client.once('ready', () => {
    ErelaManager.init(client.user.id);
    loadCommandsFromDir('./src/Commands');
    console.log('Rift is ready!');
});

client.once('reconnecting', () => {
    console.log('Reconnecting!');
});

client.once('disconnect', () => {
    console.log('Disconnect!');
});

client.on('message', async message => {
    if (message.author.bot) return;
    if (!message.guild) return;
    if (!message.member) return;

    var guild = GuildSettings.getGuildSettings(message.guild.id, client);
    if (!message.content.startsWith(guild.prefix)) return;

    const args = message.content.slice(guild.prefix.length).split(/ +/);
    const commandName = args.shift().toLowerCase();    
    const command = commands.filter(c => c.aliases != undefined).filter(c => c.aliases.includes(commandName))[0];

    if (command == undefined) {
        message.channel.send(`Command ${commandName} doesn't exist!`)
        return;
    }


    if (command.permissionReq != null) {
        for (const req of command.permissionReq) {
            if (!message.member.hasPermission(req)) {
                message.channel.send('You dont have the required permissions to execute this command!');
                return;
            }
        }
    }

    try {
        await command.execute(message, args);
    } catch (error) {
        message.channel.send('There was an error trying to execute that command!');
        message.channel.send(error)
        message.channel.send('If this error persists, please report it on github \nhttps://github.com/ItsOnlyGame/Rift/issues')
        console.error(error);
    }

    guild = GuildSettings.getGuildSettings(message.guild.id, message.client);
    if (guild.autoclean) {
        if (command.name != "Delete")
            message.delete();
    }

});

client.on("raw", d => ErelaManager.updateVoiceState(d));
client.login(getConfig().token);