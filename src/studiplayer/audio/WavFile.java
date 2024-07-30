package studiplayer.audio;
import studiplayer.basic.WavParamReader;

public class WavFile extends SampledFile {
	
	public WavFile() {
		super();
	}
	
	public WavFile(String path) throws NotPlayableException {
		super(path);
		readAndSetDurationFromFile();
	}
	
	public void readAndSetDurationFromFile() throws NotPlayableException {
		try {
			WavParamReader.readParams(getPathname());
			this.duration = computeDuration(WavParamReader.getNumberOfFrames(), WavParamReader.getFrameRate());
		} catch (Exception e) {
			throw new NotPlayableException(getPathname(), "Unsupported format for Wav file.");
		}
	}
	
	@Override
	public String toString() {
		return super.toString() + " - " + formatDuration();
	}
	
	public static long computeDuration(long numberOfFrames, float frameRate) {
		long duration = (long) (numberOfFrames / frameRate * 1000000);
		return duration;
	}
	
}
