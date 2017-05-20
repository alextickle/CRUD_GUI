import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.InputMismatchException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class View extends JFrame{
	private Controller controller;
	private Model model;
	private int input;
	private Command currentCommand;
	private ArrayList<MediaItem> queryResults;
	private final JPanel buttonPanel;
	private final JPanel contentPanel;
	private final JLabel homeLabel;
	private final JPanel homePanel;
	private final JLabel createLabel;
	private final JPanel createPanel;
	private final JLabel createTitleLabel;
	private final JTextField createTitleField;
	private final JLabel createArtistLabel;
	private final JTextField createArtistField;
	private final JLabel createTypeLabel;
	private final JComboBox createTypeField;
	private final JButton[] buttons;
	private final JPanel[] panels;

	public View(){
		super("CRUD Application");
		this.queryResults = new ArrayList<MediaItem>();
		
		// initialize button panel
		buttons = new JButton[6];
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		
		buttons[0] = new JButton("Create");
		buttons[1] = new JButton("Search");
		buttons[2] = new JButton("Update");
		buttons[3] = new JButton("Delete");
		buttons[4] = new JButton("Submit");
		buttons[4].setVisible(false);
		buttons[5] = new JButton("Cancel");
		buttons[5].setVisible(false);
		
		for (int count = 0; count < buttons.length; count++){
			buttonPanel.add(buttons[count]);
		}		
		add(buttonPanel, BorderLayout.SOUTH);
		

		// initialize content panel
		contentPanel = new JPanel();
		panels = new JPanel[4];
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBackground(Color.GRAY);
		add(contentPanel, BorderLayout.CENTER);
		
		// initialize home panel
		homePanel = new JPanel();
		homePanel.setLayout(new FlowLayout());
		homeLabel = new JLabel("Welcome to CRUD. Please make a selection.");
		homePanel.add(homeLabel);
		contentPanel.add(homePanel);
		panels[0] = homePanel;
		homePanel.setVisible(true);
		
		// initialize create panel
		createPanel = new JPanel();
		createPanel.setLayout(new GridLayout(1, 5));
		createLabel = new JLabel("CREATE ITEM");
		createPanel.add(createLabel);
		createTitleLabel = new JLabel("Title:");
		createPanel.add(createTitleLabel);
		create
		
		contentPanel.add(createPanel);
		panels[1] = createPanel;
		createPanel.setVisible(false);
		
	}
}
