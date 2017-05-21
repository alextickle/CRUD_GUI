public class Controller {
	private Modelable model;
	
	public Controller(Modelable m){
		this.model = m;
	}
	// controller sends instructions to update model via 
	// command object (4)
	public void requestModelUpdate(Command command){
		model.updateSelf(command);
	}
}
