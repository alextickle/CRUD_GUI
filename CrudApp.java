public class CrudApp {

	public static void main(String[] args) {
		
		DatabaseModel model = new DatabaseModel();
		Controller controller = new Controller(model);
		GuiViewWithJTable view = new GuiViewWithJTable(controller);
		
		model.setView(view);
		view.setModel(model);
		view.start();
	}
}
