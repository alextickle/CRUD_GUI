import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.InputMismatchException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

public class View extends JFrame{
	private Controller controller;
	private Model model;
	private Command currentCommand;
	private ArrayList<MediaItem> queryResults;
	private final JButton[] buttons;
	private final JPanel[] panels;
	private final String[] mediaTypes;
	private String[] resultsArray;
	private final Dimension preferredPanelDimension;
	private String[] selectedInfoArray;
	public static enum State {
		HOME, CREATE, UPDATE, DELETE, SEARCH, 
		RESULTS, ITEM_CREATED, ITEM_DELETED,
		ITEM_UPDATED
	};
	private State state; 
	
	// button panel
	private final JButton createButton;
	private final JButton searchButton;
	private final JButton updateButton;
	private final JButton deleteButton;
	private final JButton cancelButton;
	private final JButton continueButton;
	private final JPanel buttonPanel;
	private final JPanel contentPanel;
	private final JPanel homePanel;
	
	// create panel
	private final JPanel createPanel;
	private final JTextField createTitleField;
	private final JTextField createArtistField;
	private final JComboBox<String> createTypeField;
	
	// results panel
	private final JPanel resultsPanel;
	private JLabel resultsMessage;
	private final JList<String> resultsList;
	
	// search panel
	private final JPanel searchPanel;
	private final JTextField searchIdField;
	private final JTextField searchTitleField;
	private final JTextField searchArtistField;
	private final JComboBox<String> searchTypeField;
	
	// update panel
	private final JPanel updatePanel;
	private final JTextField updateTitleField;
	private final JTextField updateArtistField;
	
	// itemCreated panel
	private final JPanel itemCreatedPanel;
	private JLabel itemCreatedTitle;
	private JLabel itemCreatedArtist;
	private JLabel itemCreatedType;
	
	// itemUpdated panel
	private final JPanel itemUpdatedPanel;
	private JLabel itemUpdatedTitle;
	private JLabel itemUpdatedArtist;
	private JLabel itemUpdatedType;
	
	// itemDeleted panel
	private final JPanel itemDeletedPanel;
	private JLabel itemDeletedTitle;
	private JLabel itemDeletedArtist;
	private JLabel itemDeletedType;

	public View(Controller controller){
		super("CRUD Application");
		this.controller = controller;
		this.queryResults = new ArrayList<MediaItem>();
		String[] temp = {"", "CD", "DVD", "Book"};
		this.mediaTypes = temp;
		this.preferredPanelDimension = new Dimension(475, 315);
		this.state = State.HOME;
		
		// initialize button panel
		buttons = new JButton[6];
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		
		createButton = new JButton("Create");
		createButton.setVisible(true);
		buttons[0] = createButton;
		createButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event){
				if (state == State.HOME){
					hideAllComponents();
					createButton.setVisible(true);
					cancelButton.setVisible(true);
					createPanel.setVisible(true);
					state = State.CREATE;
				}
				else {
					currentCommand = getCreateInfo();
					controller.requestModelUpdate(currentCommand);
				}
			}
		});
		
		searchButton = new JButton("Search");
		searchButton.setVisible(true);
		buttons[1] = searchButton;
		searchButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event){
				if (state == State.HOME){
					hideAllComponents();
					searchButton.setVisible(true);
					cancelButton.setVisible(true);
					searchPanel.setVisible(true);
					state = State.SEARCH;
				}
				else if (state == State.SEARCH) {
					currentCommand = getSearchInfo();
					controller.requestModelUpdate(currentCommand);
				}
			}
		});
		
		updateButton = new JButton("Update");
		updateButton.setVisible(false);
		buttons[2] = updateButton;
		updateButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event){
				if (resultsList.getSelectedIndex() > -1){
					if (state == State.RESULTS){
						String infoStr = resultsArray[resultsList.getSelectedIndex()];
						selectedInfoArray = infoStr.split(" - ");
						updateTitleField.setText(selectedInfoArray[2]);
						updateArtistField.setText(selectedInfoArray[3]);
						hideAllComponents();
						updateButton.setVisible(true);
						cancelButton.setVisible(true);
						updatePanel.setVisible(true);
						state = State.UPDATE;
					}
					else {
						currentCommand = getUpdateInfo();
						controller.requestModelUpdate(currentCommand);
					}
				}
				else {
					resultsMessage.setText("Please select an item to update.");
				}
			}
		});
		
		
		deleteButton = new JButton("Delete");
		deleteButton.setVisible(false);
		buttons[3] = deleteButton;
		deleteButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event){
				if (resultsList.getSelectedIndex() > -1){
					currentCommand = getDeleteInfo();
					controller.requestModelUpdate(currentCommand);
				}
				else {
					resultsMessage.setText("Please select an item to delete.");
				}
			}
		});
		
		cancelButton = new JButton("Cancel");
		cancelButton.setVisible(false);
		buttons[4] = cancelButton;
		cancelButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event){
				hideAllComponents();
				createButton.setVisible(true);
				searchButton.setVisible(true);
				homePanel.setVisible(true);
				state = State.HOME;
			}
		});
		
		continueButton = new JButton("Continue");
		continueButton.setVisible(false);
		buttons[5] = continueButton;
		continueButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event){
				switch(state){
					case ITEM_CREATED:
						hideAllComponents();
						homePanel.setVisible(true);
						createButton.setVisible(true);
						searchButton.setVisible(true);
						state = State.HOME;
						createTitleField.setText("");
						createArtistField.setText("");
						break;
					case ITEM_DELETED:
						resultsArray = removeDeletedItem(resultsList.getSelectedIndex());
						resultsList.setListData(resultsArray);
						resultsMessage.setText(resultsArray.length + " item(s) found:");
						hideAllComponents();
						resultsPanel.setVisible(true);
						deleteButton.setVisible(true);
						updateButton.setVisible(true);
						cancelButton.setVisible(true);
						state = State.RESULTS;
						break;
					case ITEM_UPDATED:
						hideAllComponents();
						MediaItem item = currentCommand.getMediaItem();
						String updated = String.format("%s - %s - %s - %s",
							selectedInfoArray[0],
							selectedInfoArray[1],
							updateTitleField.getText() == "" ? item.getTitle() : updateTitleField.getText(),
							updateArtistField.getText() == "" ? item.getArtist() : updateTitleField.getText());
						resultsArray[resultsList.getSelectedIndex()] = updated;
						resultsList.setListData(resultsArray);
						resultsPanel.setVisible(true);
						deleteButton.setVisible(true);
						updateButton.setVisible(true);
						cancelButton.setVisible(true);
						state = State.RESULTS;
						updateTitleField.setText("");
						updateArtistField.setText("");
						break;
					default:
						break;
				}
			}
		});
		
		for (int count = 0; count < buttons.length; count++){
			buttonPanel.add(buttons[count]);
		}		
		add(buttonPanel, BorderLayout.SOUTH);
		

		// initialize content panel
		contentPanel = new JPanel();
		panels = new JPanel[8];
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBackground(Color.GRAY);
		add(contentPanel, BorderLayout.CENTER);
		
		// initialize home panel
		homePanel = new JPanel();
		homePanel.setLayout(new GridLayout(2, 1));
		homePanel.add(new JLabel("Welcome to CRUD.", SwingConstants.CENTER));
		homePanel.add(new JLabel("Please make a selection.", SwingConstants.CENTER));
		homePanel.setVisible(true);
		homePanel.setPreferredSize(preferredPanelDimension);
		contentPanel.add(homePanel);
		panels[0] = homePanel;
		
		// initialize create panel
		createPanel = new JPanel();
		createPanel.setLayout(new GridLayout(8, 1));
		createPanel.add(new JLabel("CREATE ITEM", SwingConstants.CENTER));
		createPanel.add(new JLabel("   Title:"));
		createTitleField = new JTextField();
		createPanel.add(createTitleField);
		createPanel.add(new JLabel("   Artist:"));
		createArtistField = new JTextField();
		createPanel.add(createArtistField);
		createPanel.add(new JLabel("   Media type:"));
		createTypeField = new JComboBox<String>(this.mediaTypes);
		createTypeField.setMaximumRowCount(4);
		createPanel.add(createTypeField);
		createPanel.setVisible(false);
		createPanel.setPreferredSize(preferredPanelDimension);
		contentPanel.add(createPanel);
		panels[1] = createPanel;
		
		// initialize update panel
		updatePanel = new JPanel();
		updatePanel.setLayout(new GridLayout(6, 1));
		updatePanel.add(new JLabel("UPDATE ITEM", SwingConstants.CENTER));
		updatePanel.add(new JLabel("   Title:"));
		updateTitleField = new JTextField();
		updatePanel.add(updateTitleField);
		updatePanel.add(new JLabel("   Artist:"));
		updateArtistField = new JTextField();
		updatePanel.add(updateArtistField);
		updatePanel.setVisible(false);
		updatePanel.setPreferredSize(preferredPanelDimension);
		contentPanel.add(updatePanel);
		panels[2] = updatePanel;
		
		// initialize search panel
		searchPanel = new JPanel();
		searchPanel.setLayout(new GridLayout(6, 2));
		searchPanel.add(new JLabel("SEARCH ITEM", SwingConstants.CENTER));
		searchPanel.add(new JLabel("Leave blank to skip", SwingConstants.CENTER));
		searchPanel.add(new JLabel("   Id:"));
		searchIdField = new JTextField();
		searchPanel.add(searchIdField);
		searchPanel.add(new JLabel("   Title:"));
		searchTitleField = new JTextField();
		searchPanel.add(searchTitleField);
		searchPanel.add(new JLabel("   Artist:"));
		searchArtistField = new JTextField();
		searchPanel.add(searchArtistField);
		searchPanel.add(new JLabel("   Media type:"));
		searchTypeField = new JComboBox<String>(this.mediaTypes);
		searchTypeField.setMaximumRowCount(4);
		searchPanel.add(searchTypeField);
		searchPanel.setVisible(false);
		searchPanel.setPreferredSize(preferredPanelDimension);
		contentPanel.add(searchPanel);
		panels[3] = searchPanel;
		
		
		// initialize results panel
		resultsPanel = new JPanel();
		resultsPanel.setLayout(new GridLayout(3,1));
		resultsPanel.add(new JLabel("RESULTS", SwingConstants.CENTER));
		resultsMessage = new JLabel("");
		resultsPanel.add(resultsMessage);
		resultsList = new JList<String>(new String[1]);
		resultsList.setVisibleRowCount(20);
	    resultsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    resultsPanel.add(new JScrollPane(resultsList));
	    resultsPanel.setVisible(false);
	    resultsPanel.setPreferredSize(preferredPanelDimension);
		contentPanel.add(resultsPanel);
		panels[4] = resultsPanel;
		
		// initialize itemCreated panel
		itemCreatedPanel = new JPanel();
		itemCreatedPanel.setLayout(new GridLayout(8, 1));
		itemCreatedPanel.add(new JLabel("ITEM CREATED", SwingConstants.CENTER));
		itemCreatedPanel.add(new JLabel("   Title:"));
		itemCreatedTitle = new JLabel("   (Title)");
		itemCreatedPanel.add(itemCreatedTitle);
		itemCreatedPanel.add(new JLabel("  Artist:"));
		itemCreatedArtist = new JLabel("   (Artist)");
		itemCreatedPanel.add(itemCreatedArtist);
		itemCreatedPanel.add(new JLabel("   Media type:"));
		itemCreatedType = new JLabel("   (Media type)");
		itemCreatedPanel.add(itemCreatedType);
		itemCreatedPanel.setVisible(false);
		itemCreatedPanel.setPreferredSize(preferredPanelDimension);
		contentPanel.add(itemCreatedPanel);
		panels[5] = itemCreatedPanel;
		
		// initialize itemUpdated panel
		itemUpdatedPanel = new JPanel();
		itemUpdatedPanel.setLayout(new GridLayout(8, 1));
		itemUpdatedPanel.add(new JLabel("ITEM UPDATED", SwingConstants.CENTER));
		itemUpdatedPanel.add(new JLabel("   Title:"));
		itemUpdatedTitle = new JLabel("   (Title)");
		itemUpdatedPanel.add(itemUpdatedTitle);
		itemUpdatedPanel.add(new JLabel("   Artist:"));
		itemUpdatedArtist = new JLabel("   (Artist)");
		itemUpdatedPanel.add(itemUpdatedArtist);
		itemUpdatedPanel.add(new JLabel("   Media type:"));
		itemUpdatedType = new JLabel("   (Media type)");
		itemUpdatedPanel.add(itemUpdatedType);
		itemUpdatedPanel.setVisible(false);
		itemUpdatedPanel.setPreferredSize(preferredPanelDimension);
		contentPanel.add(itemUpdatedPanel);
		panels[6] = itemUpdatedPanel;
		
		// initialize itemDeleted panel
		itemDeletedPanel = new JPanel();
		itemDeletedPanel.setLayout(new GridLayout(8, 1));
		itemDeletedPanel.add(new JLabel("ITEM DELETED", SwingConstants.CENTER));
		itemDeletedPanel.add(new JLabel("   Title:"));
		itemDeletedTitle = new JLabel("   (Title)");
		itemDeletedPanel.add(itemDeletedTitle);
		itemDeletedPanel.add(new JLabel("   Artist:"));
		itemDeletedArtist = new JLabel("   (Artist)");
		itemDeletedPanel.add(itemDeletedArtist);
		itemDeletedPanel.add(new JLabel("   Media type:"));
		itemDeletedType = new JLabel("   (Media type)");
		itemDeletedPanel.add(itemDeletedType);
		itemDeletedPanel.setVisible(false);
		itemDeletedPanel.setPreferredSize(preferredPanelDimension);
		contentPanel.add(itemDeletedPanel);
		panels[7] = itemDeletedPanel;
	
	}
	
	public void hideAllComponents(){
		for (int i = 0; i < buttons.length; i++){
			buttons[i].setVisible(false);
		}
		for (int j = 0; j < panels.length; j++){
			panels[j].setVisible(false);
		}
	}
	
	public void setModel(Model m) {
		this.model = m;
	}
	
	public Command getCreateInfo(){
		Command createCommand = new Command(Command.Type.CREATE);
		String title = createTitleField.getText();
		String artist = createArtistField.getText();
		String mediaType = mediaTypes[createTypeField.getSelectedIndex()];
		switch (mediaType){
			case "CD":
				createCommand.setMediaItem(new CD(title, artist));
				break;
			case "DVD":
				createCommand.setMediaItem(new DVD(title, artist));
				break;
			case "Book":
				createCommand.setMediaItem(new Book(title, artist));
				break;
			default:
				break;
		}
		return createCommand;
	}
	
	public Command getSearchInfo(){
		Command searchCommand = new Command(Command.Type.SEARCH);
		String id = searchIdField.getText();
		String title = searchTitleField.getText();
		String artist = searchArtistField.getText();
		String mediaType = "";
		if (searchTypeField.getSelectedIndex() > -1){
			mediaType = mediaTypes[searchTypeField.getSelectedIndex()];
		}
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
			default:
				MediaItem item = new MediaItem(title, artist);
				item.setId(id);
				searchCommand.setMediaItem(item);
				break;
		}
		searchIdField.setText("");
		searchTitleField.setText("");
		searchArtistField.setText("");
		return searchCommand;
	}
	
	public Command getUpdateInfo(){
		Command updateCommand = new Command(Command.Type.UPDATE);
		String title = updateTitleField.getText();
		String artist = updateArtistField.getText();
		String mediaType = selectedInfoArray[1];
		switch (mediaType){
			case "CD":
				updateCommand.setMediaItem(new CD(title, artist));
				break;
			case "DVD":
				updateCommand.setMediaItem(new DVD(title, artist));
				break;
			case "Book":
				updateCommand.setMediaItem(new Book(title, artist));
				break;
			default:
				break;
		}
		int index = resultsList.getSelectedIndex();
		System.out.println("index: " + index);
		updateCommand.setQueryIndex(index);
		return updateCommand;
	}
	
	public Command getDeleteInfo(){
		Command deleteCommand = new Command(Command.Type.DELETE);
		int index = resultsList.getSelectedIndex();
		deleteCommand.setQueryIndex(index);
		deleteCommand.setMediaItem(queryResults.get(index));
		return deleteCommand;
	}
	
	public String[] stringifyQueryResults(){
		String[] toReturn = new String[queryResults.size()];
		for (int i = 0; i < queryResults.size(); i++){
			toReturn[i] = String.format("%s - %s - %s - %s",
				queryResults.get(i).getId(),
				queryResults.get(i).getMediaType(),
				queryResults.get(i).getTitle(),
				queryResults.get(i).getArtist());
		}
		return toReturn;
	}
	
	public String[] removeDeletedItem(int index){
		String[] toReturn = new String[resultsArray.length - 1];
		int j = 0;
		for (int i = 0; i < resultsArray.length; i++){
			if (i != index){
				toReturn[j] = resultsArray[i];
				j++;
			}
		}
		return toReturn;
	}
	
	public void start(){
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(500, 400);
		this.setVisible(true);
	}
	
	
	// view receives alert from model and asks
	// model for the current mediaItems (7)
	public void requestInfoFromModel(){
		model.sendUpdatedInfoToView();
	}
	
	// view receives info from model and updates self (9)
	// if command is type SEARCH, view calls updateOrDelete
	public void updateSelf(ArrayList<MediaItem> currentItems){
		MediaItem item = currentCommand.getMediaItem();
		switch (currentCommand.getType()){
			case CREATE:
				itemCreatedTitle.setText(item.getTitle());
				itemCreatedArtist.setText(item.getArtist());
				itemCreatedType.setText(item.getMediaType());
				hideAllComponents();
				continueButton.setVisible(true);
				itemCreatedPanel.setVisible(true);
				state = State.ITEM_CREATED;
				break;
			case SEARCH:
				if (currentItems.size() == 0){
					hideAllComponents();
					cancelButton.setVisible(true);
					deleteButton.setVisible(true);
					updateButton.setVisible(true);
					resultsMessage.setText("No items matched your search.");
					resultsPanel.setVisible(true);
					state = State.RESULTS;
					queryResults.clear();
				}
				else {
					queryResults = currentItems;
					hideAllComponents();
					cancelButton.setVisible(true);
					deleteButton.setVisible(true);
					updateButton.setVisible(true);
					resultsArray = stringifyQueryResults();
					resultsList.setListData(resultsArray);
					resultsMessage.setText(resultsArray.length + " item(s) found:");
					resultsPanel.setVisible(true);
					state = State.RESULTS;
				}
				break;
			case UPDATE:
				hideAllComponents();
				continueButton.setVisible(true);
				itemUpdatedTitle.setText(item.getTitle());
				itemUpdatedArtist.setText(item.getArtist());
				itemUpdatedPanel.setVisible(true);
				state = State.ITEM_UPDATED;
				break;
			case DELETE:
				hideAllComponents();
				continueButton.setVisible(true);
				itemDeletedTitle.setText(item.getTitle());
				itemDeletedArtist.setText(item.getArtist());
				itemDeletedType.setText(item.getMediaType());
				itemDeletedPanel.setVisible(true);
				state = State.ITEM_DELETED;
				break;
		}
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
