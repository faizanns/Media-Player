package studiplayer.audio;
import studiplayer.basic.BasicPlayer;

public abstract class SampledFile extends AudioFile {
	
	protected long duration;
	
	public SampledFile() {
		super();
	}
	
	public SampledFile(String path) throws NotPlayableException {
		super(path);
	}

	public void play() throws NotPlayableException{
		try {
			BasicPlayer.play(getPathname());
		} catch (Exception e) {
			throw new NotPlayableException(getPathname(), "Error occured when playing the song.");
		}
	}
	
	public void togglePause() {
		BasicPlayer.togglePause();
	}
	
	public void stop() {
		BasicPlayer.stop();
	}
	
	public String formatDuration() {
		return timeFormatter(duration);
	}
	public String formatPosition() {
		return timeFormatter(BasicPlayer.getPosition());
	}
	
	public static String timeFormatter(long timeInMicroSeconds) { 
        
        // Convert milliseconds to minutes and seconds
        long minutes = (int) (timeInMicroSeconds / 1000000 / 60);
        long seconds = timeInMicroSeconds / 1000000 - minutes * 60;
        
        // Throw RuntimeException if invalid parameter values
        if (timeInMicroSeconds < 0 || minutes < 0 || seconds < 0 || seconds > 59 || minutes > 99) {
        	throw new RuntimeException("Invalid parameter values: " + String.format("%02d:%02d", minutes, seconds));
        }

        // Format the minutes and seconds into a string using String.format()
        return String.format("%02d:%02d", minutes, seconds);
	}
	
	public long getDuration() {
		return duration;
	}
	
}
