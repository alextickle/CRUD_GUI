public class CrudApp {

	public static void main(String[] args) {
		
		ProperitesModel model = new ProperitesModel();
		Controller controller = new Controller(model);
		GuiView view = new GuiView(controller);
		
		model.setView(view);
		view.setModel(model);
		view.start();
	}
}
