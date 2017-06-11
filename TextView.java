import java.util.ArrayList;
import java.util.Scanner;
import java.util.InputMismatchException;

public class TextView implements Viewable {
	private Controller controller;
	private Modelable model;
	private Scanner scanner;
	private int input;
	private Command currentCommand;
	private ArrayList<MediaItem> queryResults;

	public TextView(Controller c){
		this.controller = c;
		this.scanner = new Scanner(System.in);
		this.queryResults = new ArrayList<MediaItem>();
	}

	@Override
	public void setModel(Modelable m) {
		this.model = m;
	}

	// initial screen, called first during start method
	public int createOrSearch(){
		do {
			System.out.println("-------------------------------");
			System.out.println("Please input a selection (1-3)");
			System.out.println("1 - Create item");
			System.out.println("2 - Search item(s)");
			System.out.println("3 - Exit");

			input = 0;
			try {
					input = scanner.nextInt();
			}
			catch (InputMismatchException e){
				System.out.println("Invalid selection");
			}
			scanner.nextLine();
		} while (input < 1 || input > 3);
		return input;
	}

	// called at end of a search command
	public int deleteOrUpdate(){
		do {
			System.out.println("-------------------------------");
			System.out.println("Please input a selection (4-6)");
			System.out.println("4 - Delete item");
			System.out.println("5 - Update item");
			System.out.println("6 - Back to Main");

			input = 0;
			try {
					input = scanner.nextInt();
			}
			catch (InputMismatchException e){
				System.out.println("Invalid selection");
			}
			scanner.nextLine();
		} while (input < 4 || input > 6);
		return input;
	}

	// view prompts user to choose between create/search and informs
	// the controller (1)
		public void start(){
			int request = createOrSearch();
			if (request == 3){
				System.out.println("Session terminated.");
			}
			handleUserRequest(request);
		}

	// controller receives initial request from view and creates Command
	// object to pass to model (3)
	public void handleUserRequest(int request){
		switch (request){
		case 1:
			currentCommand = getCreateInfo();
			controller.requestModelUpdate(currentCommand);
			break;
		case 2:
			currentCommand = getSearchInfo();
			controller.requestModelUpdate(currentCommand);
			break;
		case 4:
			currentCommand = getDeleteInfo();
			controller.requestModelUpdate(currentCommand);
			break;
		case 5:
			currentCommand = getUpdateInfo();
			controller.requestModelUpdate(currentCommand);
			break;
		case 6:
			start();
			break;
		}
	}

	// view receives alert from model and asks
	// model for the current mediaItems (7)
	public void requestInfoFromModel(){
		model.sendUpdatedInfoToView(currentCommand);
	}

	// view receives info from model and updates self (9)
	// if command is type SEARCH, view calls updateOrDelete
	public void updateSelf(Object items){
		ArrayList<MediaItem> currentItems = (ArrayList<MediaItem>) items;
		MediaItem item = currentCommand.getMediaItem();
		switch (currentCommand.getType()){
			case CREATE:
				System.out.println("Item created:");
				System.out.printf("Media type: %s\nTitle: %s\nArtist: %s\n",
						item.getMediaType(),
						item.getTitle(),
						item.getArtist());
				break;
			case SEARCH:
				if (currentItems.size() == 0){
					System.out.println("No items matched your search criteria.");
					queryResults.clear();
				}
				else {
					System.out.println(currentItems.size() + " item(s) found:");
					for (int i = 0; i < currentItems.size(); i++){
						System.out.printf("Media type: %s\nTitle: %s\nArtist: %s\n\n",
								currentItems.get(i).getMediaType(),
								currentItems.get(i).getTitle(),
								currentItems.get(i).getArtist());
					}
					queryResults = currentItems;
				}
				break;
			case UPDATE:
				System.out.println("Item updated:");
				System.out.printf("Media type: %s\nTitle: %s\nArtist: %s\n",
						item.getMediaType(),
						item.getTitle(),
						item.getArtist());
				break;
			case DELETE:
				System.out.println("Item deleted:");
				System.out.printf("Media type: %s\nTitle: %s\nArtist: %s\n",
						item.getMediaType(),
						item.getTitle(),
						item.getArtist());
				break;
		}

		if (currentCommand.getType() == Command.Type.SEARCH){
			if (currentItems.size() == 0){
				start();
			}
			else {
				int request = deleteOrUpdate();
				handleUserRequest(request);
			}
		}
		else {
			int request = createOrSearch();
			if (request == 3){
				System.out.println("Session terminated.");
			}
			else {
				handleUserRequest(request);
			}
		}
	}

	// get info from user and package it
	// into a command object of type CREATE
	public Command getCreateInfo(){
		int mediaType = 0;
		String title = "";
		String artist = "";
		Command createCommand = new Command(Command.Type.CREATE);

		do {
			System.out.println("Please select media type below (1-3): ");
			System.out.println("1 - CD");
			System.out.println("2 - DVD");
			System.out.println("3 - Book");

			try {
					mediaType = scanner.nextInt();
			}
			catch (InputMismatchException e){
				System.out.println("Invalid selection");
			}
			scanner.nextLine();
		} while (mediaType < 1 || mediaType > 3);

		System.out.println("Please enter item title: ");
		title = scanner.nextLine();

		System.out.println("Please enter item artist: ");
		artist = scanner.nextLine();

		switch (mediaType){
			case 1:
				createCommand.setMediaItem(new CD(title, artist));
				break;
			case 2:
				createCommand.setMediaItem(new DVD(title, artist));
				break;
			case 3:
				createCommand.setMediaItem(new Book(title, artist));
				break;
		}
		return createCommand;
	}

	// get info from user and package
	// into a command object of type SEARCH
	public Command getSearchInfo(){
		String mediaType;
		String title = "";
		String artist = "";
		String id = "";
		Command searchCommand = new Command(Command.Type.SEARCH);

		do {
			System.out.println("Please enter item media type (<Enter> to skip): ");
			System.out.println("Options: CD - DVD - Book ");
			mediaType = scanner.nextLine();
			System.out.println(mediaType);
		} while (!(mediaType.equals("CD") || mediaType.equals("DVD") || mediaType.equals("Book") || mediaType.equals("")));

		System.out.println("Please enter item title (<Enter> to skip): ");
		title = scanner.nextLine();

		System.out.println("Please enter item artist (<Enter> to skip): ");
		artist = scanner.nextLine();

		System.out.println("Please enter item id number (<Enter> to skip): ");
		id = scanner.nextLine();

		switch (mediaType){
			case "CD":
				CD cd = new CD(title, artist);
				cd.setId(id);
				searchCommand.setMediaItem(cd);
				break;
			case "DVD":
				DVD dvd = new DVD(title, artist);
				dvd.setId(id);
				searchCommand.setMediaItem(dvd);
				break;
			case "Book":
				Book book = new Book(title, artist);
				book.setId(id);
				searchCommand.setMediaItem(book);
				break;
			case "":
				MediaItem mediaItem = new MediaItem(title, artist);
				mediaItem.setId(id);
				searchCommand.setMediaItem(mediaItem);
		}
		return searchCommand;
	}

	// get info from user and package
	// into a command object of type UPDATE
	public Command getUpdateInfo(){
		Command updateCommand = new Command(Command.Type.UPDATE);
		int itemToUpdate = 0;
		String updatedArtist;
		String updatedTitle;
		do {
			System.out.println("Which item would you like to update?");
			System.out.println("Please enter a number (1-" + queryResults.size() + ").");
			for (int i = 0; i < queryResults.size(); i++){
				System.out.printf("%d - Type: %s - Title: %s - Artist: %s\n",
						i + 1,
						queryResults.get(i).getMediaType(),
						queryResults.get(i).getTitle(),
						queryResults.get(i).getArtist());
			}
			try {
					itemToUpdate = scanner.nextInt();
			}
			catch (InputMismatchException e){
				System.out.println("Invalid selection");
			}
			scanner.nextLine();
		} while (itemToUpdate < 1 || itemToUpdate > queryResults.size() + 1);

		System.out.println("Please enter updated title (<Enter> to skip): ");
		updatedTitle = scanner.nextLine();

		System.out.println("Please enter updated artist (<Enter> to skip): ");
		updatedArtist = scanner.nextLine();

		switch (queryResults.get(itemToUpdate - 1).getMediaType()){
			case "CD":
				updateCommand.setMediaItem(new CD(updatedTitle, updatedArtist));
				break;
			case "DVD":
				updateCommand.setMediaItem(new DVD(updatedTitle, updatedArtist));
				break;
			case "Book":
				updateCommand.setMediaItem(new Book(updatedTitle, updatedArtist));
				break;
		}
		updateCommand.setQueryIndex(itemToUpdate - 1);
		return updateCommand;
	}

	// get info from user and package
	// into a command object of type DELETE
	public Command getDeleteInfo(){
		Command deleteCommand = new Command(Command.Type.DELETE);
		int itemToDelete = 0;
		do {
			System.out.println("Which item would you like to delete?");
			System.out.println("Please enter a number (1-" + queryResults.size() + ").");
			for (int i = 0; i < queryResults.size(); i++){
				System.out.printf("%d - Type: %s - Title: %s - Artist: %s\n",
						i + 1,
						queryResults.get(i).getMediaType(),
						queryResults.get(i).getTitle(),
						queryResults.get(i).getArtist());
			}
			try {
					itemToDelete = scanner.nextInt();
			}
			catch (InputMismatchException e){
				System.out.println("Invalid selection");
			}
			scanner.nextLine();
		} while (itemToDelete < 1 || itemToDelete > queryResults.size());
		deleteCommand.setQueryIndex(itemToDelete - 1);
		deleteCommand.setMediaItem(queryResults.get(itemToDelete - 1));
		return deleteCommand;
	}

	// FOR DEBUGGING ONLY
	public void logCommand(Command command){
		MediaItem mediaItem = command.getMediaItem();
		System.out.println("-------------------------------");
		System.out.println("Command Type: " + command.getType());
		System.out.println("Title: " + mediaItem.getTitle());
		System.out.println("Artist: " + mediaItem.getArtist());
		System.out.println("Id: " + mediaItem.getId());
		System.out.println("Media Type: " + mediaItem.getMediaType());
		System.out.println("-------------------------------");
	}
}
