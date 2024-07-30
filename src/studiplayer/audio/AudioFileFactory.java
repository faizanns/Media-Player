package studiplayer.audio;

public class AudioFileFactory {
	
	public static AudioFile createAudioFile(String path) throws NotPlayableException {
		
		// create tagged file if mp3 or oog file extention
		if (path.substring(path.length() - 3).equalsIgnoreCase("mp3") || path.substring(path.length() - 3).equalsIgnoreCase("ogg"))
			return new TaggedFile(path);
		// create wav file if wav file extention
		else if (path.substring(path.length() - 3).equalsIgnoreCase("wav"))
			return new WavFile(path);
		else
			// throw exception of none of above extensions
			throw new NotPlayableException(path, "Unknown suffix for AudioFile \"" + path + "\"");
	}
	
}
