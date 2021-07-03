import getConfig from './Config';
import * as interactions from 'discord-slash-commands-client'
import Command from './Models/Command';
import * as fs from 'fs'

const interactionsClient = new interactions.Client(
    getConfig().token,
    getConfig().bot_id
);

const commands: Command[] = [];

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

function delay(ms: number) {
    return new Promise( resolve => setTimeout(resolve, ms) );
}

async function run() {
    console.log("Loading commands...")
    await loadCommandsFromDir('./src/Commands');
    console.log(`Creating ${commands.length} slash commands`)
    console.log(`Due to Discord API Rate limits, this might take a while!`)

    var i = 0
    for (var temp of commands) {
        var delayTime = 1000;
        await interactionsClient.createCommand(temp.getInteraction()).catch((err) => {
            if (err.response.data.message == 'You are being rate limited.') {
                delayTime += err.response.data.retry_after;
            }
        })
        await delay(delayTime);
        i++;
        console.log(`Creating ${temp.name} interaction, ${Math.round(Number(100.0 / commands.length * i))}%`)
    }
}

run()
