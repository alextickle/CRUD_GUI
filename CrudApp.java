public class CrudApp {

	public static void main(String[] args) {
		
		Model model = new Model();
		Controller controller = new Controller(model);
		GuiView view = new GuiView(controller);
		
		model.setView(view);
		view.setModel(model);
		view.start();
	}
}
