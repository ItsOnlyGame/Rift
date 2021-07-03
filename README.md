# Rift
This is a Discord bot mostly focused on music, with support for Youtube, SoundCloud and Spotify (and others).
Feature suggestions and bug reports are appreciated

## Installation
Because of the language change from Java to JavaScript the dependecies have changed. 
Because Lavaplayer is no longer available, this bot will use lavalink which is a seperate server-side program that will have to be run beside Rift.

### Lavalink
- You need to have Java JRE 13 installed on your machine ([Here's a download link](https://adoptopenjdk.net/archive.html?variant=openjdk13))
- You can download the latest Lavalink release [here](https://github.com/freyacodes/Lavalink/releases)
- Create a file named ``application.yml`` and put it in the same folder as lavalink. 
Add [this](https://github.com/freyacodes/Lavalink/blob/master/LavalinkServer/application.yml.example) to the file and modify the required things
- Run it with ``java -jar Lavalink.jar``

### Rift
- Clone the repo
- Open a terminal and go to that directory
- Run ``npm start``
- On the first run Rift should just create a config file and close, change token in the config and run it again
- Default prefix for the bot is ```r!```

# Slash Commands
If you want to use slash commands, just give the bot permissions in the discord developer portal
https://discord.com/developers/docs/interactions/slash-commands

When you're done with the bot permissions, open the directory in terminal ``npm run loadSlashCommands``

# Commands
I'm just assuming the prefix is default: ``r!``

## Music Commands
### Play
- **Aliases:** ``play``, ``p``
- **Usage:** ``r!play [track url / search word]``
- Searches tracks from Youtube, SoundCloud and Spotify
- Play track


### Lyrics
- **Aliases:** ``lyrics``
- **Usage:** ``r!lyrics [song title]`` searches lyrics depending on the title given or just ``r!lyrics`` that will get the lyrics for the current playing track
- Fetches lyrics from google

### Clear Queue
- **Aliases:** ``clearqueue``, ``clear``
- Leaves the current voice channel

### Resume
- **Aliases:** ``resume``
- Resumes the current track paused

### Pause
- **Aliases:** ``pause``
- Pauses the current track

### Track Info
- **Aliases:** ``info``
- Gives info about the track playing

### Queue
- **Aliases:** ``queue``, ``q``
- Gets the queue

### Remove
- **Aliases:** ``remove``, ``rm``
- **Usage:** ``r!remove [queue index]``. You can get the queue index with the queue command
- Remove item from queue

### Stop
- **Aliases:** ``stop``
- Stops playing music and clears the queue

### Skip
- **Aliases:** ``skip``, ``s``
- **Usage:** ``r!skip [index]``. Amount of tracks to skip
- Skip(s) track

### Loop
- **Aliases:** ``loop``, ``l``
- **Usage:** ``r!loop``
- Loops the currently playing track

### Queue Loop
- **Aliases:** ``queueloop``, ``ql``
- **Usage:** ``r!queueloop``
- Repeats queue


## Voice Connection Commands
### Join
-  **Aliases:** ``join``, ``connect``, ``c``, ``j``
- Joins a voice channel

### Leave
- **Aliases:** ``leave``, ``disconnect``, ``dc``, ``dis``
- Leaves the current voice channel


## Settings
### Auto Clean
- **Aliases:** ``autoclean``
- If the setting is true, it will automatically delete the user-made command message

### Prefix
- **Aliases:** ``prefix``
- Change prefix

### Notify Voice Connection
- **Aliases:** ``notifyvc``
- Whether to tell if bot is (dis)connecting from/to a voice channel


## Admin Commands
### Delete
- **Aliases:** ``delete``, ``del``
- **Usage:** ``r!delete [amount]`` deletes the give amount of messages. ``r!delete`` only deletes one message
- Deletes messages


## Other
### Bot Info
- **Aliases:** ``botinfo``, ``bot``
- Gives some small information about the bot

### Help
- **Aliases:** ``help``
- Gives small information about commands


# License
Released under the [GNU GPL v3](https://www.gnu.org/licenses/gpl-3.0.en.html) license.
