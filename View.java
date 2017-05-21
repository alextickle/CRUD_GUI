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
	private int input;
	private Command currentCommand;
	private ArrayList<MediaItem> queryResults;
	private final JButton[] buttons;
	private final JPanel[] panels;
	private final String[] mediaTypes;
	private final String[] resultsArray = {"A", "B", "C", "D", "E", "F"};
	private final Dimension preferredPanelDimension;
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
	private final JList resultsList;

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

	public View(){
		super("CRUD Application");
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
				System.out.println("createButton pressed");
				if (state == State.HOME){
					hideAll();
					createButton.setVisible(true);
					cancelButton.setVisible(true);
					createPanel.setVisible(true);
					state = State.CREATE;
				}
				else {
					// TODO - createItem
					hideAll();
					continueButton.setVisible(true);
					itemCreatedPanel.setVisible(true);
					state = State.ITEM_CREATED;
				}
			}
		});
		
		searchButton = new JButton("Search");
		searchButton.setVisible(false);
		buttons[1] = searchButton;
		searchButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event){
				System.out.println("searchButton pressed");
				if (state == State.HOME){
					hideAll();
					searchButton.setVisible(true);
					cancelButton.setVisible(true);
					searchPanel.setVisible(true);
					state = State.SEARCH;
				}
				else if (state == State.SEARCH) {
					hideAll();
					cancelButton.setVisible(true);
					deleteButton.setVisible(true);
					updateButton.setVisible(true);
					resultsPanel.setVisible(true);
					state = State.RESULTS;
					// TODO - searchItem
				}
			}
		});
		
		updateButton = new JButton("Update");
		updateButton.setVisible(false);
		buttons[2] = updateButton;
		updateButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event){
				System.out.println("updateButton pressed");
				if (state == State.RESULTS){
					hideAll();
					updateButton.setVisible(true);
					cancelButton.setVisible(true);
					updatePanel.setVisible(true);
					state = State.UPDATE;
				}
				else {
					// TODO - updateItem
					hideAll();
					continueButton.setVisible(true);
					itemUpdatedPanel.setVisible(true);
					state = State.ITEM_UPDATED;
				}
			}
		});
		
		
		deleteButton = new JButton("Delete");
		deleteButton.setVisible(false);
		buttons[3] = deleteButton;
		deleteButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event){
				System.out.println("deleteButton pressed");
				// TODO - deleteItem
				hideAll();
				continueButton.setVisible(true);
				itemDeletedPanel.setVisible(true);
				state = State.ITEM_DELETED;
			}
		});
		
		cancelButton = new JButton("Cancel");
		cancelButton.setVisible(false);
		buttons[4] = cancelButton;
		cancelButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event){
				System.out.println("cancelButton pressed");
				hideAll();
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
				System.out.println("continueButton pressed");
				switch(state){
					case ITEM_CREATED:
						hideAll();
						homePanel.setVisible(true);
						createButton.setVisible(true);
						searchButton.setVisible(true);
						state = State.HOME;
						break;
					case ITEM_DELETED:
						hideAll();
						resultsPanel.setVisible(true);
						deleteButton.setVisible(true);
						updateButton.setVisible(true);
						cancelButton.setVisible(true);
						state = State.RESULTS;
						break;
					case ITEM_UPDATED:
						hideAll();
						resultsPanel.setVisible(true);
						deleteButton.setVisible(true);
						updateButton.setVisible(true);
						cancelButton.setVisible(true);
						state = State.RESULTS;
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
		homePanel.add(new JLabel("Please make a selection."));
		homePanel.setVisible(true);
		homePanel.setPreferredSize(preferredPanelDimension);
		contentPanel.add(homePanel);
		panels[0] = homePanel;
		
		// initialize create panel
		createPanel = new JPanel();
		createPanel.setLayout(new GridLayout(8, 1));
		createPanel.add(new JLabel("CREATE ITEM"));
		createPanel.add(new JLabel("Title:"));
		createTitleField = new JTextField();
		createPanel.add(createTitleField);
		createPanel.add(new JLabel("Artist:"));
		createArtistField = new JTextField();
		createPanel.add(createArtistField);
		createPanel.add(new JLabel("Media type:"));
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
		updatePanel.add(new JLabel("UPDATE ITEM"));
		updatePanel.add(new JLabel("Title:"));
		updateTitleField = new JTextField();
		updatePanel.add(updateTitleField);
		updatePanel.add(new JLabel("Artist:"));
		updateArtistField = new JTextField();
		updatePanel.add(updateArtistField);
		updatePanel.setVisible(false);
		updatePanel.setPreferredSize(preferredPanelDimension);
		contentPanel.add(updatePanel);
		panels[2] = updatePanel;
		
		// initialize search panel
		searchPanel = new JPanel();
		searchPanel.setLayout(new GridLayout(6, 2));
		searchPanel.add(new JLabel("SEARCH ITEM"));
		searchPanel.add(new JLabel("Leave blank to skip"));
		searchPanel.add(new JLabel("Id:"));
		searchIdField = new JTextField();
		searchPanel.add(searchIdField);
		searchPanel.add(new JLabel("Title:"));
		searchTitleField = new JTextField();
		searchPanel.add(searchTitleField);
		searchPanel.add(new JLabel("Artist:"));
		searchArtistField = new JTextField();
		searchPanel.add(searchArtistField);
		searchPanel.add(new JLabel("Media type:"));
		searchTypeField = new JComboBox<String>(this.mediaTypes);
		searchTypeField.setMaximumRowCount(4);
		searchPanel.add(searchTypeField);
		searchPanel.setVisible(false);
		searchPanel.setPreferredSize(preferredPanelDimension);
		contentPanel.add(searchPanel);
		panels[3] = searchPanel;
		
		
		// initialize results panel
		resultsPanel = new JPanel();
		resultsPanel.setLayout(new GridLayout(2,1));
		resultsPanel.add(new JLabel("RESULTS"));
		resultsList = new JList<String>(resultsArray);
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
		itemCreatedPanel.add(new JLabel("ITEM CREATED"));
		itemCreatedPanel.add(new JLabel("Title:"));
		itemCreatedTitle = new JLabel("(Title)");
		itemCreatedPanel.add(itemCreatedTitle);
		itemCreatedPanel.add(createTitleField);
		itemCreatedPanel.add(new JLabel("Artist:"));
		itemCreatedArtist = new JLabel("(Artist)");
		itemCreatedPanel.add(itemCreatedArtist);
		itemCreatedPanel.add(new JLabel("Media type:"));
		itemCreatedType = new JLabel("((Media type)");
		itemCreatedPanel.add(itemCreatedType);
		itemCreatedPanel.setVisible(false);
		itemCreatedPanel.setPreferredSize(preferredPanelDimension);
		contentPanel.add(itemCreatedPanel);
		panels[5] = itemCreatedPanel;
		
		// initialize itemUpdated panel
		itemUpdatedPanel = new JPanel();
		itemUpdatedPanel.setLayout(new GridLayout(8, 1));
		itemUpdatedPanel.add(new JLabel("ITEM UPDATED"));
		itemUpdatedPanel.add(new JLabel("Title:"));
		itemUpdatedTitle = new JLabel("(Title)");
		itemUpdatedPanel.add(itemUpdatedTitle);
		itemUpdatedPanel.add(createTitleField);
		itemUpdatedPanel.add(new JLabel("Artist:"));
		itemUpdatedArtist = new JLabel("(Artist)");
		itemUpdatedPanel.add(itemUpdatedArtist);
		itemUpdatedPanel.add(new JLabel("Media type:"));
		itemUpdatedType = new JLabel("((Media type)");
		itemUpdatedPanel.add(itemUpdatedType);
		itemUpdatedPanel.setVisible(false);
		itemUpdatedPanel.setPreferredSize(preferredPanelDimension);
		contentPanel.add(itemUpdatedPanel);
		panels[6] = itemUpdatedPanel;
		
		// initialize itemDeleted panel
		itemDeletedPanel = new JPanel();
		itemDeletedPanel.setLayout(new GridLayout(8, 1));
		itemDeletedPanel.add(new JLabel("ITEM DELETED"));
		itemDeletedPanel.add(new JLabel("Title:"));
		itemDeletedTitle = new JLabel("(Title)");
		itemDeletedPanel.add(itemDeletedTitle);
		itemDeletedPanel.add(new JLabel("Artist:"));
		itemDeletedArtist = new JLabel("(Artist)");
		itemDeletedPanel.add(itemDeletedArtist);
		itemDeletedPanel.add(new JLabel("Media type:"));
		itemDeletedType = new JLabel("((Media type)");
		itemDeletedPanel.add(itemDeletedType);
		itemDeletedPanel.setVisible(false);
		itemDeletedPanel.setPreferredSize(preferredPanelDimension);
		contentPanel.add(itemDeletedPanel);
		panels[7] = itemDeletedPanel;
	
	}
	
	public void hideAll(){
		System.out.println("in hideAll");
		for (int i = 0; i < buttons.length; i++){
			buttons[i].setVisible(false);
		}
		for (int j = 0; j < panels.length; j++){
			panels[j].setVisible(false);
		}
	}
}
