import javax.swing.JFrame;

public class CrudGui {

	public static void main(String[] args) {
		
		Model model = new Model();
		Controller controller = new Controller(model);
		View view = new View(controller);
		
		model.setView(view);
		view.setModel(model);
		view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		view.setSize(500, 400);
		view.setVisible(true);
	}
}
