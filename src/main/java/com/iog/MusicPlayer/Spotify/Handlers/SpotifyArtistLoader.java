package com.iog.MusicPlayer.Spotify.Handlers;
import com.iog.MusicPlayer.Spotify.AudioTrackFactory;
import com.iog.MusicPlayer.Spotify.GetAudioTrackInfo;
import com.iog.MusicPlayer.Spotify.SpotifyAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.track.BasicAudioPlaylist;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.Artist;
import com.wrapper.spotify.model_objects.specification.Track;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class SpotifyArtistLoader extends GetAudioTrackInfo {

	public AudioItem handle(URL url, AudioTrackFactory audioTrackFactory, SpotifyAudioSourceManager sourceManager, SpotifyApi api) throws Exception {
		if (!url.getPath().startsWith("/artist/")) return null;

		Artist artist = api.getArtist(getId(url.getPath())).build().execute();

		Track[] artistTopTracks = null;
		try {
			artistTopTracks = api.getArtistsTopTracks(this.getId(url.getPath()), sourceManager.getCountryCode()).build().execute();
		} catch (IOException | SpotifyWebApiException | ParseException e) {
			e.printStackTrace();
		}

		if (artistTopTracks == null) return null;

		List<AudioTrackInfo> songMetadata = getAudioTrack(Arrays.asList(artistTopTracks));
		List<AudioTrack> audioTracks = audioTrackFactory.getAudioTracks(songMetadata, sourceManager);

		return new BasicAudioPlaylist(artist.getName(), audioTracks, audioTracks.getFirst(), false);
	}

}
