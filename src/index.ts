import * as fs from 'fs'
import { Client, GuildChannel, GatewayIntentBits, REST, Routes, ActivityType } from 'discord.js'
import Command from './Models/Command'
import winston from 'winston'
import { distube, initDisTube } from './Utils/AudioManager'
import { getConfig, saveConfig } from './Config'

const config = getConfig()
const LOAD_SLASH_COMMANDS = config.REFRESH_SLASH_COMMANDS
const LOAD_SLASH_COMMANDS_GUILD = config.LOAD_SLASH_COMMANDS_GUILD
const LOAD_SLASH_COMMANDS_GUILD_ID = config.GUILD_SLASH_COMMAND_ID

const myformat = winston.format.combine(
	winston.format.timestamp(),
	winston.format.printf((info) => `${info.timestamp} [${info.level}]: ${info.message}`)
)

export const logger = winston.createLogger({
	level: 'info',
	format: myformat,
	defaultMeta: { service: 'user-service' },
	transports: [new winston.transports.Console(), new winston.transports.File({ filename: 'logs/out.log', level: 'debug' })]
})

const client = new Client({
	intents: [
		GatewayIntentBits.Guilds,
		GatewayIntentBits.MessageContent,
		GatewayIntentBits.DirectMessages,
		GatewayIntentBits.GuildVoiceStates,
		GatewayIntentBits.GuildMessages,
		GatewayIntentBits.GuildVoiceStates,
        GatewayIntentBits.GuildMembers
	]
})
const commands: Map<string, Command> = new Map<string, Command>()
initDisTube(client)

/**
 * Loads every command from commands
 * @param {string} path
 */
async function loadCommandsFromDir(path: string) {
	const content = fs.readdirSync(path)

	
    for (var file of content) {
        if (file.endsWith('.ts')) {
            const ICommand = await import(`${path.replace("./src", ".")}/${file}`);
            const command: Command = new ICommand.default
            commands.set(command.data.name, command);

        } else {
            await loadCommandsFromDir(`${path}/${file}`);
        }
    }
}

/**
* Loads every command to discord as slash commands 
*/
async function loadSlashCommands() {
    const rest = new REST({ version: '10' }).setToken(config.token)
    try {
        logger.info('Started refreshing application (/) commands.')

        // Delete all slash commands
        const promises = [];
        const data = await rest.get(Routes.applicationCommands(client.user.id)) as any[]
        for (const command of data) {
            const deleteUrl = `${Routes.applicationCommands(client.user.id)}/${command.id}`;
            promises.push(rest.delete(deleteUrl as any));
        }
        await Promise.all(promises);
        
        // Add all slash commands
        await rest.put(Routes.applicationCommands(client.user.id), {
            body: Array.from(commands.values()).map((value) => value.data.toJSON())
        })
        
        
        logger.info('Successfully reloaded application (/) commands.')
    } catch (error) {
        logger.error(error)
    }

    config.REFRESH_SLASH_COMMANDS = false
    saveConfig(config)
}


/**
* Loads every command to discord as slash commands 
*/
async function loadSlashCommandsGuild(guildId: string) {
    const rest = new REST({ version: '10' }).setToken(config.token)
    try {
        logger.info(`Started refreshing application (/) commands for ${guildId}.`)

        // Delete all slash commands
        const promises = [];
        const data = await rest.get(Routes.applicationGuildCommands(client.user.id, guildId)) as any[]
        for (const command of data) {
            const deleteUrl = `${Routes.applicationGuildCommands(client.user.id, guildId)}/${command.id}`;
            promises.push(rest.delete(deleteUrl as any));
        }
        await Promise.all(promises);
        
        const commandArray = Array.from(commands.values())
        // Add all slash commands
        await rest.put(Routes.applicationGuildCommands(client.user.id, guildId), {
            body: commandArray.map((value) => {
                console.log(value.data)
                return value.data.toJSON()
            })
        })
        
        
        logger.info(`Successfully reloaded application (/) commands for ${guildId}.`)
    } catch (error) {
        logger.error(error)
    }
}
client.once('ready', async () => {
	await loadCommandsFromDir('./src/Commands')

    if (LOAD_SLASH_COMMANDS) {
        await loadSlashCommands()
    }

    if (LOAD_SLASH_COMMANDS_GUILD) {
        await loadSlashCommandsGuild(LOAD_SLASH_COMMANDS_GUILD_ID)
    }

	logger.info('Rift is ready!')
    client.user.setActivity('/play', { type: ActivityType.Listening })
})

client.once('reconnecting', () => {
	logger.info('Reconnecting!')
})

client.once('disconnect', () => {
	logger.info('Disconnect!')
})

client.on('voiceStateUpdate', (old, current) => {
	if (old.channelId != null) {
		const channel = current.guild.channels.cache.get(old.channelId) as GuildChannel
		const botAmount = channel.members.filter((member) => member.user.bot).size
		if (channel.members.size - botAmount == 0) {
			distube.voices.leave(old.guild)
		}
	}
})

client.on('interactionCreate', async (interaction) => {
	if (!interaction.isCommand()) return

	const command = commands.get(interaction.commandName)
	if (!command) interaction.reply('Not a valid command')

	await interaction.deferReply()
	await command.execute(interaction)
})

client.login(config.token)
