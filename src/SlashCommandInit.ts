import getConfig from './Config';
import { Client } from 'discord-slash-commands-client'
import Command from './Models/Command';
import * as fs from 'fs'
import { getLogger } from 'log4js';
const logger = getLogger();

/**
 * Slash Commands variables
 */
const awaitTimer = 5000; // 5000ms

const interactionsClient = new Client(
    getConfig().token,
    getConfig().bot_id
);

/**
 * bot commands
 */
const commands: Array<Command> = [];

/**
 * Loads every command from commands
 * @param {string} path 
 */
 async function loadCommandsFromDir(path: string) {
    const content = fs.readdirSync(path);
    for (var file of content) {
        if (file.endsWith(".ts")) {
            const command = await import(`${path.replace("./src", ".")}/${file}`);
            commands.push(new command.default);
        } else {
            await loadCommandsFromDir(`${path}/${file}`);
        }
    }
}

// Delays function, sleeps for amount of ms
function delay(ms: number) {
    return new Promise( resolve => setTimeout(resolve, ms) );
}


/**
 * Check for existence of command that corresponds to the given interaction
 * If found and command contains interaction json, then do nothing
 * If command not found then delete interaciton
 * 
 * @param interaction Interaction JSON
 * @returns If interaction was deleted
 */
async function deleteIrrelevantCommand(interaction: any) {
    // If the connection between the interaction and command has been found, assuming false
    var found = false;

    // Loops through every command
    var index = 0;
    for (var cmd of commands) {

        // If one of the commands aliases is same as interaction name
        // If true, the connection has been found and variable "found" can be set to true
        if (cmd.aliases.includes(interaction.name)) {
            found = true;

            // If command doesn't have interaction json the just delete the existing interaction on it
            if (cmd.getInteraction() == null) {
                await interactionsClient.deleteCommand(interaction.id)
                await delay(awaitTimer)
                commands.slice(index, 1);
                return true;
            } else {
                // Found and nothing has changed
                commands.slice(index, 1);
                return false;
            }
        }
        index++;
    }

    // Connection was not found and the interaction can be deleted
    if (!found) {
        await interactionsClient.deleteCommand(interaction.id)
        await delay(awaitTimer)
        return true;
    }
    
    // The function should not be able to go here because of optimizations
    throw "This should not be able to be executed"
}

async function initSlashCommands() {
    // Load commands
    logger.debug("Loading commands for interactions loading")
    await loadCommandsFromDir('./src/Commands');

    logger.debug(`Due to Discord API Rate limits, this might take a while!`)

    const existingCommands = await interactionsClient.getCommands({});
    var i = 0
    if (existingCommands instanceof Array) {
        for (var t of existingCommands) {
            const deleted = await deleteIrrelevantCommand(t)
            const clm = deleted ? `Removed ${t.name} interaction` : `Interaction ${t.name} is ok`
            logger.debug(`${clm}, ${Math.round(Number(100.0 / commands.length * i))}%`)
            i++;
        }
    } else {
        const deleted = await deleteIrrelevantCommand(existingCommands)
        const clm = deleted ? `Removed ${existingCommands.name} interaction` : `Interaction ${existingCommands.name} is ok`
        logger.debug(clm)
    }

    logger.debug(`Creating ${commands.length} new slash commands`)
    var i = 0
    for (var temp of commands) {
        if (temp.getInteraction() == null) continue;
        await interactionsClient.createCommand(temp.getInteraction()).catch((err) => {
            logger.error(`Error occurred: ${err.response.data.message}`)
        })
        await delay(awaitTimer);
        i++;
        logger.debug(`Creating ${temp.name} interaction, ${Math.round(Number(100.0 / commands.length * i))}%`)
    }
    logger.debug("Done...")
}

export { initSlashCommands }