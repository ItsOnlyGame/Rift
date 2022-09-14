import { SlashCommandBuilder, CacheType, Interaction } from 'discord.js'
import GuildSettings from '../../Guilds/GuildSettings';
import {  } from '../../Config'
import Command from '../../Models/Command'

export default class Settings extends Command {
	constructor() {
		super(
            new SlashCommandBuilder()
                .setName('settings')
                .setDescription('Guild settings')
                .addSubcommand(sub => 
                    sub.setName('auto-role')
                        .setDescription('Add/Remove automated roles on guild join')
                        .addStringOption(option => 
                            option.setName('action')
                                .setDescription('Something')
                                .addChoices({ name: 'Add', value: 'add' }, {name: 'Remove', value: 'remove'})
                                .setRequired(true)
                        )
                        .addRoleOption(option => option.setName('role').setDescription('The role to be added/removed to auto role').setRequired(true))
                )
        )
	}

	public async execute(interaction: Interaction<CacheType>): Promise<void> {
        if (!interaction.isCommand()) return;
        if (!interaction.isChatInputCommand()) return

        const guild = GuildSettings.getGuildSettings(interaction.guildId, interaction.client)

        switch (interaction.options.getSubcommand()) {

            case 'auto-role': {
                const action = interaction.options.get('action').value as 'add' | 'remove'
                const role = interaction.options.get('role').role

                if (action == 'add') {
                    guild.autoRoles.push(role.id)
                    interaction.editReply("Added role to automated roles")
                } else {
                    guild.autoRoles.slice(guild.autoRoles.findIndex(value => value == role.id), 1)
                    interaction.editReply("Removed role from automated roles")
                }

                break
            }

            default: {
                interaction.editReply("This subcommand doesn't exist")
                break
            }
        }
	}
}
