public interface Modelable {
	
	public void updateSelf(Command command);	
	
	public void setView (Viewable view);
	
	public void sendUpdatedInfoToView(Command currentCommand);
	
	public void alertView();
}