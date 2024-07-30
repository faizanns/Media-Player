package studiplayer.audio;

public enum SortCriterion {
	DEFAULT, AUTHOR, TITLE, ALBUM, DURATION;
	
	private static final String[] labels = {
		"Default", "Author", "Title", "Album", "Duration"
	};

    @Override
    public String toString() {
        return labels[this.ordinal()];
    }
}
