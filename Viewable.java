import java.util.ArrayList;

public interface Viewable {
	
	public void setModel(Modelable m);	
	
	public void requestInfoFromModel();
	
	public void updateSelf(Object currentItems);
	
	public Command getCreateInfo();
	
	public void start();
}