# Rift
This is a Discord bot mostly focused on music, with support for Youtube, SoundCloud and Spotify (and others).
Feature suggestions and bug reports are appreciated

## Installation
### Requirement
- Node v16 or newer
- Python (2.6, 2.7, or 3.2+)
- youtube-dl
- FFmpeg

If the ``npm install`` doesn't work
please take a look at [the Sodium documentation](https://www.npmjs.com/package/sodium) 


### Get started
- Clone the repo
- Open a terminal in the repo directory
- Run ``npm install``
- Run ``npm run build``
- To start the bot run ``npm start``
- On the first run Rift should just create a config file and close, change token in the config and run it again

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
- This commands doesn't have a slash version


## Other
### Bot Info
- **Aliases:** ``botinfo``, ``bot``
- Gives some small information about the bot

### Help
- **Aliases:** ``help``
- Gives small information about commands


# License
Released under the [GNU GPL v3](https://www.gnu.org/licenses/gpl-3.0.en.html) license.
