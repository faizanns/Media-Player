package studiplayer.audio;
import java.util.Comparator;

public class TitleComparator implements Comparator<Object>{

	@Override
	public int compare(Object o1, Object o2) {
		if (o1 == null || o2 == null)
			throw new RuntimeException();
		AudioFile af1 = (AudioFile) o1;
		AudioFile af2 = (AudioFile) o2;
		return af1.getTitle().compareTo(af2.getTitle());
	}

}
