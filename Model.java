import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.FormatterClosedException;
import java.util.Properties;
import java.util.Scanner;

public class Model implements Modelable{

	private Viewable view;
	private ArrayList<Properties> inventory;
	private ArrayList<Properties> currentProperties;
	private static Formatter output;
	private Scanner input;
	private ArrayList<String[]> lastSearchParams;

	// next created item will receive this id number
	private int currentId;


	public Model(){
		this.inventory = new ArrayList<Properties>();
		this.currentProperties = new ArrayList<Properties>();
		loadInventory();
	}

	public void setView(Viewable view){
		this.view = view;
	}

	// model receives instructions from controller
	// and updates self (5)
	public void updateSelf(Command command){
		MediaItem item = command.getMediaItem();
		switch (command.getType()){
			case CREATE:
				Properties toAdd = new Properties();
				toAdd.setProperty("title", item.getTitle());
				toAdd.setProperty("artist", item.getArtist());
				toAdd.setProperty("mediaType", item.getMediaType());
				toAdd.setProperty("id", Integer.toString(currentId));
				currentId++;
				inventory.add(toAdd);
				currentProperties.add(toAdd);
				break;
			case SEARCH:
				ArrayList<String[]> searchParameters = new ArrayList<String[]>();
				String[] array = new String[2];
				String parameter = item.getTitle();
				if (!parameter.equals("")){
					array = new String[]{"title", parameter};
					searchParameters.add(array);
				}
				parameter = item.getArtist();
				if (!parameter.equals("")){
					array = new String[]{"artist", parameter};
					searchParameters.add(array);
				}

				parameter = item.getId();
				if (!parameter.equals("")){
					array = new String[]{"id", parameter};
					searchParameters.add(array);
				}

				parameter = item.getMediaType();
				if (!parameter.equals("")){
					array = new String[]{"mediaType", parameter};
					searchParameters.add(array);
				}
				lastSearchParams = searchParameters;
				ArrayList<Properties> results = parameterSearch(searchParameters);
				currentProperties = results;
				break;
			case UPDATE:
				System.out.println("command query index: " + command.getQueryIndex());
				System.out.println("currentProperties size: " + currentProperties.size());
				Properties recordToUpdate = currentProperties.get(command.getQueryIndex());
				String stringToUpdate = item.getTitle();
				if (!stringToUpdate.equals("")){
					recordToUpdate.setProperty("title", stringToUpdate);
				}
				stringToUpdate = item.getArtist();
				if (!stringToUpdate.equals("")){
					recordToUpdate.setProperty("artist", stringToUpdate);
				}
				currentProperties = new ArrayList<Properties>();
				currentProperties.add(recordToUpdate);
				break;
			case DELETE:
				System.out.println("command query index: " + command.getQueryIndex());
				Properties recordToDelete = currentProperties.get(command.getQueryIndex());
				currentProperties = new ArrayList<Properties>();
				currentProperties.add(recordToDelete);
				deleteItem(recordToDelete.getProperty("id"));
				break;
		}
		saveInventory();
		alertView();
	}

	// searches through each item in inventory and checks to see if it matches all
	// search parameters. If a match is found then the item is added to queryResults
	public ArrayList<Properties> parameterSearch(ArrayList<String[]> parameters){
		Properties currentRecord;
		String[] currentParameter;
		ArrayList<Properties> queryResults = new ArrayList<Properties>();
		int matches;
		for (int i = 0; i < inventory.size(); i++){
			currentRecord = inventory.get(i);
			matches = 0;
			for (int j = 0; j < parameters.size(); j++){
				currentParameter = parameters.get(j);
				if (currentRecord.containsKey(currentParameter[0])){
					if (currentRecord.getProperty(currentParameter[0]).equals(currentParameter[1])){
						matches++;
					}
				}
			}
			if (matches == parameters.size()){
				queryResults.add(currentRecord);
			}

		}
		return queryResults;
	}

	// deletes an item from the inventory array, but does not save to disk
	public void deleteItem(String idToDelete){
		int indexToDelete = -1;
		for (int i = 0; i < inventory.size(); i++){
			if (inventory.get(i).getProperty("id").equals(idToDelete)){
				indexToDelete = i;
			}
		}
		if (indexToDelete >= 0){
			inventory.remove(indexToDelete);
		}
	}

	public void saveInventory(){
		// open inventory.txt file
		try {
			output = new Formatter("inventory.txt");
		}
		catch (SecurityException securityException){
			System.err.println("Write permission denied. Terminating");
			System.exit(1);
		}
		catch (FileNotFoundException fileNotFoundException){
			System.err.println("Error opening file. Terminating.");
			System.exit(1);
		}

		// save to inventory.txt file
		try {
			output.format("currentId: %d\n", currentId);
			Properties currentRecord;
			for (int i = 0; i < inventory.size(); i++){
				currentRecord = inventory.get(i);
				output.format("%s_%s_%s_%s\n",
						currentRecord.getProperty("id"),
						currentRecord.getProperty("mediaType"),
						currentRecord.getProperty("title"),
						currentRecord.getProperty("artist"));
			}
		}
		catch (FormatterClosedException formatterClosedException){
			System.err.println("Error writing to file. Terminating");
			System.exit(1);
		}

		// close file
		if (output != null){
			output.close();
		}

	}

	// not called, was used for debugging
	public void deleteInventory(){
		File existingInventory = new File("inventory.txt");
		existingInventory.delete();
		File emptyInventory = new File("inventory.txt");
		try {
			emptyInventory.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// called in the model's constructor
	public void loadInventory(){
		String currentIdStr;
		// open file inventory.txt
		try {
			input = new Scanner(Paths.get("inventory.txt"));
		}
		// if file doesn't exist then create a blank inventory.txt file
		catch (IOException ioException){
			File newInventory = new File("inventory.txt");
			try {
				newInventory.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				output = new Formatter("inventory.txt");
			}
			catch (SecurityException securityException){
				System.err.println("Write permission denied. Terminating");
				System.exit(1);
			}
			catch (FileNotFoundException fileNotFoundException){
				System.err.println("Error opening file. Terminating.");
				System.exit(1);
			}

			try {
				output.format("currentId: %d\n", 1);
			}
			catch (FormatterClosedException formatterClosedException){
				System.err.println("Error writing to file. Terminating");
				System.exit(1);
			}

			// close file
			if (output != null){
				output.close();
			}

			try {
				input = new Scanner(Paths.get("inventory.txt"));
			}
			catch (IOException ioE){
				System.err.println("Error creating file. Terminating");
				System.exit(1);
			}
		}
		// read data from inventory.txt and populate inventory
		String line;
		String[] props = new String[4];
		Properties toAdd;
		if (input.hasNext()){
			currentIdStr = input.nextLine();
			currentId = Integer.parseInt(currentIdStr.split(": ")[1]);
		}
		while (input.hasNext()){
			line = input.nextLine();
			props = line.split("_");
			toAdd = new Properties();
			toAdd.setProperty("id", props[0]);
			toAdd.setProperty("mediaType", props[1]);
			toAdd.setProperty("title", props[2]);
			toAdd.setProperty("artist", props[3]);
			inventory.add(toAdd);
		}

		// close file
		if (input != null){
			input.close();
		}
	}

	// model informs the view that it has made
	// an update (6)
	public void alertView(){
		view.requestInfoFromModel();
	}

	// model receives detailed request from view
	// and sends the requested info (8)
	public void sendUpdatedInfoToView(Command current){
		if (current.getType() == Command.Type.UPDATE || current.getType() == Command.Type.DELETE){
			currentProperties = parameterSearch(lastSearchParams);
		}
		view.updateSelf(exportCurrentProps());
	}

	// converts a Properties object into a MediaItem for export to the view
	public MediaItem exportItem(Properties prop){
		MediaItem toExport;
		switch (prop.getProperty("mediaType")){
			case "Book":
				toExport = new Book(prop.getProperty("title"), prop.getProperty("artist"));
				toExport.setId(prop.getProperty("id"));
				break;
			case "CD":
				toExport = new CD(prop.getProperty("title"), prop.getProperty("artist"));
				toExport.setId(prop.getProperty("id"));
				break;
			case "DVD":
				toExport = new DVD(prop.getProperty("title"), prop.getProperty("artist"));
				toExport.setId(prop.getProperty("id"));
				break;
			default:
				toExport = new CD("", "");
				break;
		}
		return toExport;
	}


	// creates MediaItem objects for all Properties in currentProperties and places
	// them into an ArrayList for export to view
	public ArrayList<MediaItem> exportCurrentProps(){
		ArrayList<MediaItem> toExport = new ArrayList<MediaItem>();
		for (int i = 0; i < currentProperties.size(); i++){
			toExport.add(exportItem(currentProperties.get(i)));
		}
		return toExport;
	}

	// FOR DEBUGGING ONLY
	public void logSearchResults(){
		System.out.println("Search Results");
		for (int i = 0; i < currentProperties.size(); i++){
			System.out.printf("Media type: %s\nTitle: %s\nArtist: %s\n------\n",
				currentProperties.get(i).getProperty("mediaType"),
				currentProperties.get(i).getProperty("title"),
				currentProperties.get(i).getProperty("artist"),
				currentProperties.get(i).getProperty("id"));
		}
	}

	// FOR DEBUGGING ONLY
	public void logRecord(Properties record){
		System.out.println("------------");
		System.out.printf("Media type: %s\nTitle: %s\nArtist: %s\n------\n",
			record.getProperty("mediaType"),
			record.getProperty("title"),
			record.getProperty("artist"),
			record.getProperty("id"));
	}

	// FOR DEBUGGING ONLY
	public void logInventory(){
		System.out.println("Complete Inventory");
		for (int i = 0; i < inventory.size(); i++){
			System.out.printf("Media type: %s\nTitle: %s\nArtist: %s\n------\n",
				inventory.get(i).getProperty("mediaType"),
				inventory.get(i).getProperty("title"),
				inventory.get(i).getProperty("artist"),
				inventory.get(i).getProperty("id"));
		}
	}

	// FOR DEBUGGING ONLY
	public void logSearchParameters(ArrayList<String[]> parameters){
		for (int i = 0; i < parameters.size(); i++){
			System.out.println("---------------");
			System.out.println("parameter: " + parameters.get(i)[0]);
			System.out.println("value: " + parameters.get(i)[1]);
		}
	}
}
