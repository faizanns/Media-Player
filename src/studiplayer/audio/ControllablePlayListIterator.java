package studiplayer.audio;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ControllablePlayListIterator implements Iterator<AudioFile>{
	private List<AudioFile> list;
	private int index;
	
	public ControllablePlayListIterator(List<AudioFile> list) {
		this.list = list;
		index = 0;
	}
	
	public ControllablePlayListIterator(List<AudioFile> list, String search, SortCriterion sortCriterion) {
		index = 0;
		
		// Initialise to prevent NullPointerException when adding to empty list
		this.list = new LinkedList<AudioFile>();
		
		// create new list that contains searched string
		if (search != null && !search.equals(""))
			for (AudioFile file: list) {
				if (file.getAuthor().toLowerCase().contains(search.toLowerCase()) || file.getTitle().toLowerCase().contains(search.toLowerCase()))
					this.list.add(file);
				else if (file instanceof TaggedFile && ((TaggedFile) file).getAlbum() != null)
					if (((TaggedFile) file).getAlbum().toLowerCase().contains(search.toLowerCase()))
						this.list.add(file);
			}
		else
			this.list.addAll(list);
		
		// sorts according to sort criterion
		if (!sortCriterion.name().equals("DEFAULT"))
			if (sortCriterion.name().equals("AUTHOR"))
				this.list.sort(new AuthorComparator());
			else if (sortCriterion.name().equals("ALBUM"))
				this.list.sort(new AlbumComparator());
			else if (sortCriterion.name().equals("TITLE"))
				this.list.sort(new TitleComparator());
			else
				this.list.sort(new DurationComparator());
		
	}
	
	public AudioFile jumpToAudioFile(AudioFile file) {
		if (list.contains(file)) {
			index = list.indexOf(file);
			return list.get(index++);
		} else {
			return null;
		}
	}
	
	@Override
	public AudioFile next() {
		if (hasNext())
			return list.get(index++);
		else {
			if (list.size() == 0)
				return null;
			index = 0;
			return list.get(index++);
		}
	}
		
	@Override
	public boolean hasNext() {
		return index < list.size();
	}
	
	public int getIndex() {	
		return index;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public List<AudioFile> getList() {
		return this.list;
	}
}
