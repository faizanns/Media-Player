package studiplayer.audio;
import java.util.Comparator;

public class DurationComparator implements Comparator<Object>{

	@Override
	public int compare(Object o1, Object o2) {
		if (o1 == null || o2 == null)
			throw new RuntimeException();
		
		// if AudioFile is empty
		if (!(o1 instanceof SampledFile))
			return -1;
		if (!(o2 instanceof SampledFile))
			return 1;
		
		SampledFile tf1 = (SampledFile) o1;
		SampledFile tf2 = (SampledFile) o2;
		
		// if duration is present, compare
		if (tf1.getDuration() == tf2.getDuration())
			return 0;
		else if (tf1.getDuration() < tf2.getDuration())
			return -1;
		return 1;
		
	}

}
