package studiplayer.audio;
import java.io.File;

public abstract class AudioFile {

	private String pathname;
	private String filename;
	protected String author;
	protected String title;
	
	private boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().indexOf("win") >= 0;
	}
	
	public AudioFile() {
		pathname = "";
		filename = "";
		author = "";
		title = "";
	}
	
	public AudioFile(String path) throws NotPlayableException {
		parsePathname(path);
		
		// Check if the file path is null or empty
        if (pathname == null || pathname.isEmpty()) {
            throw new NotPlayableException(pathname, "File path cannot be null or empty");
        }

        // Check if the file is readable
        File file = new File(pathname);
        if (!file.canRead()) {
            throw new NotPlayableException(pathname, "Cannot read file: " + pathname);
        }
        
		parseFilename(filename);

	}
	
	@Override
	public String toString() {
		if (getAuthor().equals("")) {
			return title;
		} else {
			String outputString = author + " - " + title;
			return (outputString);
		}
	}
	
	public void parseFilename(String filename) {
		
		if (filename.contains(" - ") == false) {
			
			// empty filename string
			if (filename.trim().equals("")) {
				author = "";
				title = "";
			}
			
			if (filename.contains(".")) {
				
				// contains only .extension
				if (filename.lastIndexOf(".") == 0) {
					author = "";
					title = "";
				}
				
				// contains only title and extension
				else if (filename.lastIndexOf(".") != 0) {
					author = "";
					title = filename.substring(0, filename.lastIndexOf(".")).trim();
				}
			}
			
		} else {
			
			// contains only a dash
			if (filename.equals(" - ")) {
				author = "";
				title = "";
			} else if (filename.trim().equals("-")) {
				author = "";
				title = "-";
			} else {
			
				// contains filename with author, title and/or extension
				author = filename.substring(0, filename.indexOf(" - ")).trim();
				
				// extension is present
				if (filename.substring(filename.indexOf(" - ") + 3).contains(".")) {
					title = filename.substring(filename.indexOf(" - ") + 3, filename.lastIndexOf(".")).trim();
				} else {
					// extension not present
					title = filename.substring(filename.indexOf(" - ") + 3).trim();
				}
				
			}
		}
	}
	
	public void parsePathname(String path) {
		
		path = path.trim();
		
		// Cases without a slash
		if (!path.contains("/") && !path.contains("\\")) {
			
			// empty path string
			if (path.equals("")) {
				filename = "";
				pathname = "";
				
			// no slashes, only file name
			} else if (path.indexOf(".") == path.lastIndexOf(".") && !path.contains("/") && !path.contains("\\")) {
				filename = path.trim();
				pathname = path.trim();
				
			// only dash
			} else if (path.trim().equals("-")) {
				filename = "-";
				pathname = "-";
				
			// Either empty path or invalid path without a slash
			} else {
				filename = "";
				pathname = "";
			}
				
		// cases with slashes
		} else {
			
			// check OS and correct path
			if (isWindows() == true) {
				path = path.replace("/d/", "d:");
				path = path.replace("/", "\\");
			} else {
				path = path.replace("d:", "/d/");
				path = path.replace("\\", "/");
			}
			
			// replacing multiple slashes with single slashes
			while (path.contains("//") || path.contains("\\\\")) {
				if (path.contains("//")) {
					path = path.replace("//", "/");
				} else if (path.contains("\\\\")) {
					path = path.replace("\\\\", "\\");
				}
			}
			
			// slash at the end of path
			if (path.substring(path.length() - 1).equals("\\") || path.substring(path.length() - 1).equals("/")) {
				pathname = path;
				filename = "";
			} else {
				// filename at end of path
				pathname = path;
				if (isWindows()) {
					filename = path.substring(path.lastIndexOf("\\") + 1).trim();
				} else {
					filename = path.substring(path.lastIndexOf("/") + 1).trim();
				}
			}
			
		}
	}
	
	public String getPathname() {
		return pathname;
	}
	
	public String getFilename() {
		return filename;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public String getTitle() {
		return title;
	}
	
	public abstract void play() throws NotPlayableException;
	public abstract void togglePause();
	public abstract void stop();
	public abstract String formatDuration();
	public abstract String formatPosition();
	
}