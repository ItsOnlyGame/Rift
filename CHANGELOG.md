# Change Log

## [4.0.0] | 29.8.2022
### Added
- Slash commands can now be reloaded with a config variable change

### Changed
- Normal commands have been switched to slash commands
- Updated dependencies

### Removed
- AutoClean has been removed
- NotifyVoiceConnection has been removed
- Prefix has been removed because its no longer needed


## [3.1.0] | 15.8.2022
### Changed
- Updated dependencies
- Changed file location


## [3.0.7] | 12.4.2022
### Changed
- Updated dependencies

### Fixed
- Playlist information in the "play embed" was wrong 


## [3.0.6] | 3.4.2022
### Changed
- Remove command should now be working

### Fixed
- Playing a playlist didn't send an embed


## [3.0.5] | 19.3.2022
### Changed
- Updated packages
- Changed from youtube-dl to yt-dlp

### Fixed
- Looping should now work


## [3.0.4] | 24.2.2022
### Fixed
- Fixed finding lyrics with a query didn't join query with spaces on the message


## [3.0.3] | 15.2.2022
### Fixed
- Fixed auto leaving voice channel when left alone


## [3.0.2] | 27.1.2022
### Fixed
- Finding lyrics with a query didn't join query with spaces when searching


## [3.0.1] | 23.1.2022
### Fixed
- Dependency changes


## [3.0.0] | 24.10.2021
### Added
- Change music and voice connection handling to distube

### Changed
- Changed lavalink to distube

### Removed
- Slash commands

### Fixed
- Queue command didn't respect the cap so its now limited to 40 tracks per message


## [2.2.4] | 5.8.2021
### Added
- Automatically disconnect from voice channel if alone


## [2.2.3] | 19.7.2021
### Added
- Config file now has an option to enable/disable slash commands

### Fixed
- Issue #1, GuildSettings json file would get corrupted when modified
- Issue #2, "Remove" command logic was completely wrong


## [2.2.2] | 6.7.2021
### Added
- Automatic interations handling
- Log4Js to handle logging for debug purposes

### Changed
- Delete command no longer has an interation

### Fixed
- Interaction with no arguments didn't work


## [2.2.1] | 3.7.2021
### Fixed
- Interaction replies weren't handeled correctly
- SlashCommandInit send interactions too quick and resulted in a 429 error (Too many requests)


## [2.2.0] | 3.7.2021
### Added
- Delete command is now able to delete more than 100 messages (discord limit)
- Queue looping and track looping - more info in README.md
- New Guild Settings: Notify Voice Connection - more info in README.md
- Added support for Slash Commands, ``npm run loadSlashCommands`` to activate them

### Changed
- Pausing a track will now send a message of what its doing to the text channel 
- Changed the visuals of help command to be an embed
- Every command is now contained in a class
- MessageCtx to replace commands execute arguments

### Fixed
- Changing settings didn't save or corrupted the file
- Lavalink would timeout node connection and disconnect from voice channel, while thinking it was still connected to a voice channel 
- Help command had a typo
- Anyone could change the guild settings


## [2.1.0] | 27.6.2021
### Added
- If error is thrown refer user to github
- Checking config.json file for missing variables

## Fixed
- Fixed a bug in queue command
- Guilds config directory will be created if it doesn't exist

### Changed
- Small code modifications
- Swiched spotify plugin for erela.js ( SpotifyAPI credentials required )


## [2.0.0] | 26.6.2021
### Major changes
- Swiched from java to typescript
- Dependency changes (Lavalink)


## [1.1.3] | 1.4.2021
### Fixed
- Lavaplayer Unofficial fixed playlist errors

### Gradle imports
- Updated Discord4J to 3.1.5 from 3.1.3


## [1.1.2] | 1.4.2021
### Fixed
- Lyric fetching works
- Lavaplayer Unofficial fixed playback errors (website cookies)


## [1.1.1] | 19.3.2021
### Added
- New queue commands
  - Clear Queue: Clears the whole queue
  - Remove: Remove from queue
  
- Bot leaves voice channel if its left there alone

### Changed
- Default embed colors were changed

### Fixed
- Queue command now handles the char cap that discord has



## [1.0.0] | 17.3.2021
### Added
- Added settings to manage guild
    - Prefix: To change guild prefix
    - AutoClean: To choose whether to delete the command message
  
- Added Info command to give info about the current track

### Fixed
- Lyrics command now handles the char cap that discord has

### Gradle Import
- Lavaplayer Unofficial from 1.3.75 to 1.3.76
  - Fixes the spotify authentication bug



## [0.1.0 Alpha] | 17.3.2021
### Initial release
- It should be pretty stable but not perfect.