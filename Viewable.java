import java.util.ArrayList;

public interface Viewable {
	
	public void setModel(Model m);	
	
	public void requestInfoFromModel();
	
	public void updateSelf(ArrayList<MediaItem> currentItems);
	
	public Command getCreateInfo();
	
	public void start();
}