package studiplayer.audio;
import java.util.Comparator;

public class AlbumComparator implements Comparator<Object>{

	@Override
	public int compare(Object o1, Object o2) {
		if (o1 == null || o2 == null)
			throw new RuntimeException();
		
		// if not a tag file, rank before other
		if (!(o1 instanceof TaggedFile) && !(o2 instanceof TaggedFile))
			return 0;
		if (!(o1 instanceof TaggedFile))
			return -1;
		if (!(o2 instanceof TaggedFile))
			return 1;
		
		TaggedFile tf1 = (TaggedFile) o1;
		TaggedFile tf2 = (TaggedFile) o2;
		
		// check if Album is exists
		if (tf1.getAlbum() == null && tf2.getAlbum() == null)
			return 0;
		if (tf1.getAlbum() == null)
			return -1;
		if (tf2.getAlbum() == null)
			return 1;
		
		// check if Album is empty
		if (tf1.getAlbum().equals("") && tf2.getAlbum().equals(""))
			return 0;
		if (tf1.getAlbum().equals(""))
			return -1;
		if (tf2.getAlbum().equals(""))
			return 1;
		
		// if album is present, compare
		return tf1.getAlbum().compareTo(tf2.getAlbum());
	}

}
