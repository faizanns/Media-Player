package studiplayer.audio;
import java.util.Map;
import studiplayer.basic.TagReader;

public class TaggedFile extends SampledFile {

	private String album;
	
	public TaggedFile() {
		super();
	}
	
	public TaggedFile(String path) throws NotPlayableException {
		super(path);
		readAndStoreTags();
	}
	
	public String getAlbum() {
		return album;
	}
	
	public void readAndStoreTags() throws NotPlayableException {
		try {
			Map<String, Object> tagMap = TagReader.readTags(this.getPathname());
			if (tagMap.get("author") != null) {
				this.author = tagMap.get("author").toString().trim();
			}
			if (tagMap.get("title") != null) {
				this.title = tagMap.get("title").toString().trim();
			}
			if (tagMap.get("album") != null) {
				this.album = tagMap.get("album").toString().trim();
			}
			if (tagMap.get("duration") != null) {
				this.duration = Long.valueOf(tagMap.get("duration").toString());
			}
		} catch (Exception e) {
			throw new NotPlayableException(getPathname(), "Tagfile tags cannot be read.");
		}
			
	}
	
	@Override
	public String toString() {
		if (album != null) {
			return super.toString() +  " - " + album + " - "  + formatDuration();
		} else {
			System.out.println(formatDuration());
			return super.toString() +  " - "  + formatDuration();
		}
	}
	
}
