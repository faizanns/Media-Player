package studiplayer.audio;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class PlayList implements Iterable<AudioFile>{
	private LinkedList<AudioFile> list;
	private String search;
	private SortCriterion sortCriterion;
	private ControllablePlayListIterator iterator;
	private int currentIndex;
	
	public PlayList() {
		this.list = new LinkedList<AudioFile>();
		sortCriterion = SortCriterion.DEFAULT;
		this.iterator = this.iterator();
	}
	
	public PlayList(String m3uPathname) {
		this.list = new LinkedList<AudioFile>();
		sortCriterion = SortCriterion.DEFAULT;
		this.iterator = iterator();
		this.loadFromM3U(m3uPathname);
		this.iterator();
	}
	
	public void add(AudioFile file) {
		list.add(file);
	}
	
	public void remove(AudioFile file) {
		list.remove(file);
	}
	
	public int size() {
		return list.size();
	}
	
	public AudioFile currentAudioFile() {
		if (this.getList().size() == 0 || currentIndex > iterator().getList().size())
			return null;
		else {
			if (currentIndex == iterator().getList().size())
				return iterator().getList().get(0);
			else
				return iterator().getList().get(currentIndex);
		}
	}
	
	public void nextSong() {
		iterator();
		iterator.setIndex(currentIndex);
		iterator.next();
		currentIndex = iterator.getIndex();
		iterator.next();
	}
	
	public void loadFromM3U(String pathname) {
		list.clear();
		currentIndex = 0;
		Scanner scanner = null;
		
		try {
			// open the file for reading
			scanner = new Scanner(new File(pathname));			
			
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				// create AudioFile instance and add to AudioFle list
				if (line.trim().equals("") || line.substring(0, 1).equals("#"))
					continue;
				else {
					// check if the file exists before creating AudioFile instance
		            File audioFile = new File(line);
		            if (audioFile.exists()) {
		                try {
		                    add(AudioFileFactory.createAudioFile(line));
		                } catch (NotPlayableException e) {
		                	e.printStackTrace();
		                }
		            } else {
		                continue;
		            }
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("File " + pathname + " read!");
			scanner.close();
		}
		
	}
	
	public void saveAsM3U(String pathname) {
		FileWriter writer = null;
		String sep = System.getProperty("line.separator");
		
		try {
			// create the file if it does not exist, otherwise reset the file
			// and open it for writing
			writer = new FileWriter(pathname);
					
			for (AudioFile listFile : list) {
				// write the current line + newline char
				writer.write(listFile.getPathname() + sep);
			}
		} catch (IOException e) {
			throw new RuntimeException("Unable to write file " + pathname + "!");
		} finally {
			try {
				System.out.println("File " + pathname + " written!");
				// close the file writing back all buffers
				writer.close();
			} catch (Exception e) {
				// ignore exception; probably because file could not be opened
			}
		}
	}
	
	@Override
	public ControllablePlayListIterator iterator() {
		iterator = new ControllablePlayListIterator(getList(), getSearch(), getSortCriterion());
		return iterator;
	}
	
	public List<AudioFile> getList() {
		return list;
	}
	
	public void jumpToAudioFile(AudioFile audiofile) {
		AudioFile af = iterator().jumpToAudioFile(audiofile);
		currentIndex = list.indexOf(af);
		iterator.next();
	}
	
	public void setSearch(String search) {
		this.search = search;
		iterator();
		currentIndex = 0;
	}
	
	public String getSearch() {
		return search;
	}
	
	public void setSortCriterion(SortCriterion sortCriterion) {
		this.sortCriterion = sortCriterion;
		iterator();
		currentIndex = 0;
	}
	
	public SortCriterion getSortCriterion() {
		return sortCriterion;
	}
	
	public String toString() {
		
		String listString = "";
		for (AudioFile af : list) {
		    listString += af.toString() + ", ";
		}
		
		listString = "[" + listString.substring(0, listString.length() - 2) + "]";
		return listString;
	}

}