package studiplayer.ui;
import java.io.File;
import java.net.URL;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import studiplayer.audio.AudioFile;
import studiplayer.audio.NotPlayableException;
import studiplayer.audio.PlayList;
import studiplayer.audio.SortCriterion;

public class Player extends Application{
	public static final String DEFAULT_PLAYLIST = "playlists/DefaultPlayList.m3u";
	private PlayList playList = new PlayList();
	private boolean useCertPlayList = false;
	private static final String PLAYLIST_DIRECTORY = "playlists";
	private static final String INITIAL_PLAY_TIME_LABEL = "00:00";
	private static final String NO_CURRENT_SONG = "-";
	private Button playButton;
	private Button pauseButton;
	private Button stopButton;
	private Button nextButton;
	private Label playListLabel;
	private Label playTimeLabel;
	private Label currentSongLabel;
	private ChoiceBox<SortCriterion> sortChoiceBox;
	private TextField searchTextField;
	private Button filterButton;
	private SongTable songTable;
	private boolean isSongPaused = false;
	private PlayerThread playerThread;
    private TimerThread timerThread;
	
	public Player() {
		playList = new PlayList();
		songTable = new SongTable(playList);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		BorderPane mainPane = new BorderPane();
		primaryStage.setTitle("APA Player");
		
		// Select and load playlist --------------
		// variable to store PlayList path to load
		String playListPath = "";
		// if not running tests
		if (useCertPlayList != true) {
			// open file chooser window
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open Resource File");
			File playListFile = fileChooser.showOpenDialog(primaryStage);
			// if file selected
			if (playListFile != null || !playListPath.isEmpty())
				playListPath = playListFile.getPath();
			else
				playListPath = DEFAULT_PLAYLIST;
		// if running tests
		} else
			playListPath = DEFAULT_PLAYLIST;
		// sets and loads playlist
		setPlayList(playListPath);
		loadPlayList(playListPath);
		
		// Filter form in top area ------------
		TitledPane gridTitlePane = new TitledPane();
		GridPane filterGrid = new GridPane();
		filterGrid.setHgap(10);
        filterGrid.setVgap(10);
        filterGrid.setPadding(new Insets(10));
        
        // Initialize controls
        searchTextField = new TextField();
        sortChoiceBox = new ChoiceBox<>();
        filterButton = new Button();
        
		filterGrid.add(new Label("Search text"), 0, 0);
		searchTextField.setPrefWidth(170);
		filterGrid.add(searchTextField, 1, 0);
		
		filterGrid.add(new Label("Sort by"), 0, 1);
		sortChoiceBox.getItems().addAll(SortCriterion.values());
		sortChoiceBox.setValue(SortCriterion.DEFAULT);
		sortChoiceBox.setPrefWidth(170);
		filterGrid.add(sortChoiceBox, 1, 1);
		
		filterButton.setText("display");
		filterGrid.add(filterButton, 2, 1);
		gridTitlePane.setText("Filter");
		gridTitlePane.setContent(filterGrid);
		
		mainPane.setTop(gridTitlePane);
		
		// Song table in Table view in centre area -------------
		SongTable songTable = new SongTable(getPlayList());
		mainPane.setCenter(songTable);
		
		// Buttons and current song details in bottom area -------------
		// Bottom area: Song details		
		playListLabel = new Label(playListPath);
		currentSongLabel = new Label(NO_CURRENT_SONG);
		playTimeLabel = new Label(INITIAL_PLAY_TIME_LABEL);
		
		GridPane gridpane = new GridPane();
		gridpane.setPadding(new Insets(10));
		gridpane.setHgap(20);
        gridpane.setVgap(5);
		
	    gridpane.add(new Label("Playlist"), 0, 0);
	    gridpane.add(playListLabel, 1, 0);
	    gridpane.add(new Label("Current Song"), 0, 1);
	    gridpane.add(currentSongLabel, 1, 1);
	    gridpane.add(new Label("Playtime"), 0, 2);
	    gridpane.add(playTimeLabel, 1, 2);
	    		
		// Bottom area: Buttons
		playButton = createButton("play.jpg");
		pauseButton = createButton("pause.jpg");
		stopButton = createButton("stop.jpg");
		nextButton = createButton("next.jpg");
		setButtonStates(false, true, true, false);
		
		HBox notVerticalBox = new HBox(5);
		notVerticalBox.setAlignment(Pos.CENTER);
	    notVerticalBox.getChildren().addAll(playButton, pauseButton, stopButton, nextButton);
		
	    // Combining bottom parts in vertical box
		VBox verticalBox = new VBox(10);
	    verticalBox.getChildren().addAll(gridpane, notVerticalBox);
	    mainPane.setBottom(verticalBox);
	    mainPane.setPadding(new Insets(0, 0, 20, 0));
	    
	    // Event handling using Lambda Expressions
 		filterButton.setOnAction(e -> {
 			// setting new value of sort criterion
 			playList.setSortCriterion(sortChoiceBox.getValue());
 			// searching input text
 			playList.setSearch(searchTextField.getText());
 			// updateing playlist
 			songTable.refreshSongs();
 		});

	    // Create scene --------------
		Scene scene = new Scene(mainPane, 600, 400);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		// Calling appropriate event handling methods
		playButton.setOnAction(e -> playButtonHandler());
		pauseButton.setOnAction(e -> pauseButtonHandler());
		stopButton.setOnAction(e -> stopButtonHandler());
		nextButton.setOnAction(e -> nextButtonHandler());
		
		// Handling selection of song from row
		songTable.setRowSelectionHandler(e -> rowSelectionHandler(songTable.getSelectionModel().getSelectedItem()));
		
	}
	
	private void rowSelectionHandler(Song selectedSong) {
        if (selectedSong != null) {
        	stopButtonHandler();
        	playList.jumpToAudioFile(selectedSong.getAudioFile());
        	playButtonHandler();
        }
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	private void nextButtonHandler() {
		// console output
		System.out.println("Switching to next audio file: Stopped = " + ((playerThread != null) ? playerThread.stopped:"thread is currently empty") + ", paused = " + isSongPaused);
		stopButtonHandler();
        playList.nextSong();
        setButtonStates(true, false, false, false);
        updateSongInfo(playList.currentAudioFile());
        playButtonHandler();
     // Console output
		System.out.println("Switched to next audio file: Stopped = " + playerThread.stopped + ", paused = " + isSongPaused);
	}

	private void stopButtonHandler() {
		isSongPaused = false;
		terminateThreads(false); // stop both threads
		playList.currentAudioFile().stop();
		// update song info
		updateSongInfo(null); // set song info to default
		// adjust button states 
		setButtonStates(false, true, true, false); // play and next enabled
		// Console output
		System.out.println("Stopping " + playList.currentAudioFile().getTitle());
		System.out.println("Filename is " + playList.currentAudioFile().getFilename());
	}

	private void pauseButtonHandler() {
		if (isSongPaused) {
			isSongPaused = false;
			startThreads(true); // start only timer thread
			// console output
			System.out.println("Playing " + playList.currentAudioFile().getTitle());

		} else {
			isSongPaused = true;
			terminateThreads(true); // terminate only timer thread
			// console output
			System.out.println("Pausing " + playList.currentAudioFile().getTitle());

		}
		playList.currentAudioFile().togglePause();
		setButtonStates(true, false, false, false); // play and next enabled
		// Console output
		System.out.println("Filename is " + playList.currentAudioFile().getFilename());
	}

	private void playButtonHandler(){
		// Activate only timer thread if song is paused
		if (!isSongPaused) {
			startThreads(false); // start both threads
		} else {
			isSongPaused = false;
			startThreads(true); // start only timer thread
			playList.currentAudioFile().togglePause();
		}
		
		// update song info
		songTable.selectSong(playList.currentAudioFile());
		updateSongInfo(playList.currentAudioFile());
		
		// adjust button states 
		setButtonStates(true, false, false, false); // play disabled
		// Console output
		System.out.println("Playing " + playList.currentAudioFile().getTitle());
		System.out.println("Filename is " + playList.currentAudioFile().getFilename());
	}
	
	// method to create button
	private Button createButton(String iconfile) {
		Button button = null;
		try {
			URL url = getClass().getResource("/icons/" + iconfile);
			Image icon = new Image(url.toString());
			ImageView imageView = new ImageView(icon);
			imageView.setFitHeight(20);
			imageView.setFitWidth(20);
			button = new Button("", imageView);
			button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
			button.setStyle("-fx-background-color: #fff;");
		} catch (Exception e) {
			System.out.println("Image " + "icons/"
			+ iconfile + " not found!");
			System.exit(-1);
		}
		return button;
	}
	
	// method to set button states
	private void setButtonStates(boolean playButtonState, boolean pauseButtonState, boolean stopButtonState, boolean nextButtonState) {
		Platform.runLater(() -> {
			playButton.setDisable(playButtonState);
			pauseButton.setDisable(pauseButtonState);
			stopButton.setDisable(stopButtonState);
			nextButton.setDisable(nextButtonState);
		});
	}
	
	// method to update song info
	private void updateSongInfo(AudioFile af) {
		Platform.runLater(() -> {
			// if audiofile doesn't exist, use default values
			if (af == null) {
				currentSongLabel.setText(NO_CURRENT_SONG);
	            playTimeLabel.setText(INITIAL_PLAY_TIME_LABEL);
			} else {
				currentSongLabel.setText(af.getTitle());
	            playTimeLabel.setText(af.formatPosition());
			}
		});
	}

	// method to load selected playlist
	public void loadPlayList(String pathname) {
		if (pathname == null || pathname.isEmpty()) {
        	setPlayList(DEFAULT_PLAYLIST);
        } else {
            setPlayList(pathname);
        }
	}
	
	public void setUseCertPlayList(boolean value) {
		this.useCertPlayList = value;
	}
	
	public void setPlayList(String pathname) {
		this.playList = new PlayList(pathname);
		songTable.refreshSongs();
	}
	
	public PlayList getPlayList() {
		return this.playList;
	}
	
	// PlayerThread class to handle song playback in a separate thread
	class PlayerThread extends Thread {
		private boolean stopped = false;
		
		// method to set thread terminate condition
		public void terminate() {
			stopped = true;
		}
		
		@Override
	    public void run() {
			// run thread until stopped
	        while (!stopped) {
	        	// ensure playlist is not empty
	            if (playList.currentAudioFile() != null) {
	            	// select song in table
	                Platform.runLater(() -> songTable.selectSong(playList.currentAudioFile()));
	                try {
	                	playList.currentAudioFile().play();
	                	// continue playing next song if thread not stopped
	                    if (!stopped) {
		                    playList.nextSong(); 
		                    updateSongInfo(playList.currentAudioFile());
		                }
	                } catch (NotPlayableException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	    }
	}
	
	// TimerThread class to update the playback time in the UI
	class TimerThread extends Thread {
		private boolean stopped = false;
		
		// method to set thread terminate condition
		public void terminate() {
			stopped = true;
		}
		
		@Override
	    public void run() {
			// run thread until stopped
	        while (!stopped) {
	            Platform.runLater(() -> {
	            	// ensure playlist is not empty
	                if (playList.currentAudioFile() != null) {
	                    playTimeLabel.setText(playList.currentAudioFile().formatPosition());
	                    currentSongLabel.setText(playList.currentAudioFile().getTitle());
	                } else {
	                	// set default values if no audiofile
	                    playTimeLabel.setText(INITIAL_PLAY_TIME_LABEL);
	                    currentSongLabel.setText(NO_CURRENT_SONG);
	                }
	            });
	            
	            try {
	                Thread.sleep(1000); // Update every second
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	}
	
	// method to activate one or both threads
	private void startThreads(boolean timerThreadOnly) {
	    if (timerThread == null || !timerThread.isAlive()) {
	        timerThread = new TimerThread();
	        timerThread.start();
	    }
	    // if !timerTimerOnly: create and start PlayerThread
	    if (!timerThreadOnly && (playerThread == null || !playerThread.isAlive())) {
	        playerThread = new PlayerThread();
	        playerThread.start();
	    }
	}

	// method to terminate one or both threads
	private void terminateThreads(boolean timerThreadOnly) {
	    if (timerThread != null) {
	        timerThread.terminate();
	        timerThread = null;
	    }
	    // if !timerTimerOnly: terminate PlayerThread
	    if (!timerThreadOnly && playerThread != null) {
	        playerThread.terminate();
	        playerThread = null;
	    }
	}
}
