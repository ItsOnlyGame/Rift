package com.iog.MusicPlayer;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrackQueue {
	private final List<AudioTrack> list = new ArrayList<>();
	private boolean looping;
	
	public TrackQueue() {
		looping = false;
	}
	
	public TrackQueue(TrackQueue queue) {
		this.list.addAll(queue.getList());
		looping = false;
	}
	
	public void add(AudioTrack item) {
		list.add(item);
	}
	
	public void addFirst(AudioTrack item) {
		for (int i = list.size() - 1; i > 1; i--) {
			list.set(i + 1, list.get(i));
		}
		list.add(0, item);
	}
	
	public int size() {
		return list.size();
	}
	
	public AudioTrack pull() {
		if (list.isEmpty()) return null;
		
		if (looping) {
			
			
			return list.get(0).makeClone();
		} else {
			return list.remove(0);
		}
	}
	
	public boolean isEmpty() {
		return list.isEmpty();
	}
	
	public List<AudioTrack> getList() {
		return list;
	}
	
	public AudioTrack get(int index) {
		return list.get(index);
	}
	
	public void remove(int index) {
		list.remove(index);
	}
	
	public void clear() {
		list.clear();
	}
	
	public void shuffle() {
		List<AudioTrack> temp = new ArrayList<>(list);
		Collections.shuffle(temp);
		list.clear();
		list.addAll(temp);
	}
	
	public void skip(int number) {
		if (number > 0) {
			list.subList(0, number).clear();
		}
	}
	
	public boolean isLooping() {
		return looping;
	}
	
	public void setLooping(boolean looping) {
		this.looping = looping;
	}
}
