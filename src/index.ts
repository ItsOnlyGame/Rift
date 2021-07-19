import * as fs from 'fs'
import { Channel, Client, GuildMember, MessageEmbed } from "discord.js";
import GuildSettings from './Guilds/GuildSettings';
import { ErelaManager, initErela } from './Models/LavaplayerManager';
import getConfig from './Config';
import Command from './Models/Command'
import MessageCtx from './Models/MessageCtx';
import * as interactions from 'discord-slash-commands-client'
import { configure, getLogger } from 'log4js';
import { initSlashCommands } from './SlashCommandInit';

const logger = getLogger();
configure({
    appenders: { 
        files: { type: "dateFile", maxLogSize: 10485760, filename: "logs/out.log" },
        console: {type: 'console' }
    },
    categories: { 
        default: { appenders: ["console", "files"], level: "all" } 
    }
});

const client = new Client();
initErela(client)

const commands: Command[] = [];

/**
 * Loads every command from commands
 * @param {string} path 
 */
async function loadCommandsFromDir(path: string) {
    const content = fs.readdirSync(path);
    for (var file of content) {
        if (file.endsWith(".ts")) {
            const ICommand = await import(`${path.replace("./src", ".")}/${file}`);
            const command: Command = new ICommand.default
            commands.push(command);

        } else {
            await loadCommandsFromDir(`${path}/${file}`);
        }
    }
}

client.once('ready', async () => {
    // If slash commands are enabled to be initiated
    if (getConfig().enableSlashCommands) {
        initSlashCommands();
    }

    ErelaManager.init(client.user.id);
    await loadCommandsFromDir('./src/Commands');
    logger.info('Rift is ready!');
});

client.once('reconnecting', () => {
    logger.info('Reconnecting!');
});

client.once('disconnect', () => {
    logger.info('Disconnect!');
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
        if (commandName == "") return;
        message.channel.send(`Command ${commandName} doesn't exist!`)
        return;
    }


    if (command.permissionRequired != null) {
        for (const req of command.permissionRequired) {
            if (!message.member.hasPermission(req)) {
                message.channel.send("You don't have the required permissions to execute this command!");
                return;
            }
        }
    }

    try {
        await command.execute(new MessageCtx(args, message.channel, message.member, undefined, guild));
    } catch (error) {
        message.channel.send('There was an error trying to execute that command!');
        message.channel.send(error)
        message.channel.send('If this error persists, please report it on github \nhttps://github.com/ItsOnlyGame/Rift/issues')
        console.error(error);
    }

    if (guild.autoclean) {
        if (command.name != "Delete") {
            message.delete();
        }
    }

});

//@ts-ignore
client.ws.on('INTERACTION_CREATE', async interaction => {
    const args: string[] = [];

    if (interaction.data.options != undefined) {
        for (var temp of interaction.data.options) {
            args.push(temp.value)
        }
    }


    const channel: Channel = client.channels.cache.get(interaction.channel_id);
    const member: GuildMember = client.guilds.cache.get(interaction.guild_id).members.cache.get(interaction.member.user.id)
    
    const ctx = new MessageCtx(args, channel, member, interaction)

    const commandName = interaction.data.name;   
    const command = commands.filter(c => c.aliases != undefined).filter(c => c.aliases.includes(commandName))[0];

    if (command == undefined) {
        ctx.send(`Command ${commandName} doesn't exist!`)
        return;
    }

    if (command.permissionRequired != null) {
        for (const req of command.permissionRequired) {
            if (!ctx.member.hasPermission(req)) {
                ctx.send("You don't have the required permissions to execute this command!");
                return;
            }
        }
    }

    try {
        await command.execute(ctx);
    } catch (error) {
        ctx.send('There was an error trying to execute that command!');
        ctx.send(error)
        ctx.send('If this error persists, please report it on github \nhttps://github.com/ItsOnlyGame/Rift/issues')
        console.error(error);
    }

})

client.on("raw", d => ErelaManager.updateVoiceState(d));
client.login(getConfig().token);