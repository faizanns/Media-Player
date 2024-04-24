public class AudioFile {

	private String pathname;
	private String filename;
	private String author;
	private String title;
	
	private boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().indexOf("win") >= 0;
	}
	
	public AudioFile() {
		pathname = "";
		filename = "";
		author = "";
		title = "";
	}
	
	public AudioFile(String path) {
		parsePathname(path);
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
		
		if (filename.contains("-") == false) {
			
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
		
		// empty path string
		if (path.equals("")) {
			filename = "";
			pathname = "";
			
		// no slashes, only file name
		} else if (!path.contains("/") && !path.contains("\\")) {
			filename = path.trim();
			pathname = path.trim();
			
		// only dash
		} else if (path.trim().equals("-")) {
			filename = "-";
			pathname = "-";
				
		// cases with slashes
		} else {
			
			while (path.charAt(path.length() - 1) == ' ') {
				path = path.substring(0, path.lastIndexOf(" "));
			}
			
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
	
}
