import { Client, TextChannel } from "discord.js";
import { Manager } from "erela.js";
import Spotify from 'erela.js-spotify';
import getConfig from "../Config";


// Define some options for the node
const nodes = [{
    host: "192.168.8.140",
    password: "youshallnotpass",
    port: 2333,
}];
const spotifyCredentials = getConfig().spotify;

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
                clientID: spotifyCredentials.clientId,
                clientSecret: spotifyCredentials.clientSecret
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

    ErelaManager.on("socketClosed", (player, payload) => {
        player.disconnect();
        player.destroy();
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