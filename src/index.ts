import * as fs from 'fs'
import { Client, GuildChannel, Intents } from "discord.js";
import GuildSettings from './Guilds/GuildSettings';
import Command from './Models/Command'
import winston from 'winston';
import Config from './Config';
import { distube, initDisTube } from './Models/AudioManager';

const myformat = winston.format.combine(
    winston.format.timestamp(),
    winston.format.printf(info => `${info.timestamp} [${info.level}]: ${info.message}`)
);

export const logger = winston.createLogger({
    level: 'info',
    format: myformat,
    defaultMeta: { service: 'user-service' },
    transports: [
        new winston.transports.Console(),
        new winston.transports.File({ filename: 'logs/out.log', level: 'debug' }),
    ]
});


const client = new Client({ intents: [Intents.FLAGS.GUILDS, Intents.FLAGS.GUILD_MEMBERS, Intents.FLAGS.GUILD_MESSAGES, Intents.FLAGS.GUILD_BANS, Intents.FLAGS.GUILD_INTEGRATIONS, Intents.FLAGS.GUILD_VOICE_STATES] });
const commands: Command[] = [];
initDisTube(client)

/**
 * Loads every command from commands
 * @param {string} path 
 */
async function loadCommandsFromDir(path: string) {
    const content = fs.readdirSync(path);

    var ext = process.env.NODE_ENV == 'development' ? '.ts' : '.js'

    for (var file of content) {
        if (file.endsWith(ext)) {
            const ICommand = await import(`${path.replace("./src", ".").replace("./dist", ".")}/${file}`);
            const command: Command = new ICommand.default
            commands.push(command);

        } else if (file.endsWith('.js.map')) {
            continue
        } else {
            await loadCommandsFromDir(`${path}/${file}`);
        }
    }
}


client.once('ready', async () => {
    var ext = process.env.NODE_ENV == 'development' ? 'src' : 'dist'
    await loadCommandsFromDir('./'+ext+'/Commands');
    logger.info('Rift is ready!');
});

client.once('reconnecting', () => {
    logger.info('Reconnecting!');
});

client.once('disconnect', () => {
    logger.info('Disconnect!');
});

client.on('messageCreate', async message => {
    if (message.author.bot) return;
    if (!message.guild) return;
    if (!message.member) return;

    var guild = GuildSettings.getGuildSettings(message.guild.id, client);
    if (!guild) {
        message.channel.send('Creating guild config file.')
        return;
    }
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
        for (const perm of command.permissionRequired) {
            if (!message.member.permissions.has(perm, true)) {
                message.channel.send("You don't have the required permissions to execute this command!");
                return;
            }
        }
    }

    try {
        command.execute(message, args).then(() => {
            if (guild.autoclean) {
                if (command.name != "Delete") {
                    setTimeout(() => message.delete(), 3000)
                }
            }
        }).catch();
    } catch (error) {
        message.channel.send('There was an error trying to execute that command!');
        message.channel.send(error)
        message.channel.send('If this error persists, please report it on github \nhttps://github.com/ItsOnlyGame/Rift/issues')
        logger.error(error);
    }

});


client.on('voiceStateUpdate', (old, current) => {
    if (old.channelId != null) {
        const channel = current.guild.channels.cache.get(old.channelId) as GuildChannel;
        if (channel.members.size == 1) {
            if (channel.members.first().user.id == old.client.user.id) {
                distube.voices.leave(old.guild)
            }
        }
    } 
})

const config = Config.getConfig();
client.login(config.token);