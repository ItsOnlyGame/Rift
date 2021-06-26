import { Client, TextChannel } from "discord.js";
import { Manager } from "erela.js";
import Spotify from '@kaname-png/erela.js-spotify';


// Define some options for the node
const nodes = [{
    host: "localhost",
    password: "youshallnotpass",
    port: 2333,
}];

var ErelaManager: Manager = null;

function initErela(client: Client) {
    ErelaManager = new Manager({
        // The nodes to connect to, optional if using default lavalink options
        nodes,
        // Method to send voice data to Discord
        send: (id, payload) => {
            const guild = client.guilds.cache.get(id);
            // NOTE: FOR ERIS YOU NEED JSON.stringify() THE PAYLOAD
            if (guild) guild.shard.send(payload);
        },
        plugins: [
            // Initiate the plugin and pass the two required options.
            new Spotify({
                limitTracks: Infinity
            })
        ]
    });


    // Emitted whenever a node connects
    ErelaManager.on("nodeConnect", node => {
        console.log(`Node "${node.options.identifier}" connected.`)
    })

    // Emitted whenever a node encountered an error
    ErelaManager.on("nodeError", (node, error) => {
        console.log(`Node "${node.options.identifier}" encountered an error: ${error.message}.`)
    })

    ErelaManager.on("trackError", (player, track, payload) => {
        const channel = client.channels.cache.get(player.textChannel);
        (channel as TextChannel).send(payload.error);
    });

    ErelaManager.on("trackStuck", (player, track) => {
        const channel = client.channels.cache.get(player.textChannel);
        (channel as TextChannel).send(`Track got stuck | ${track.title} - ${track.author}`);
    });
}

export { initErela, ErelaManager }