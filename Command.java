public class Command {
	private MediaItem item;
	public static enum Type {
		CREATE, UPDATE, DELETE, SEARCH
	};
	private Type type;

	// used by model to retain info of original item during an update command
	private int queryIndex;

	public Command(Type aType){
		this.type = aType;
	}

	public Type getType(){
		return this.type;
	}

	public MediaItem getMediaItem(){
		return this.item;
	}

	public void setMediaItem(MediaItem item){
		this.item = item;
	}

	public int getQueryIndex(){
		return this.queryIndex;
	}

	public void setQueryIndex(int index){
		this.queryIndex = index;
	}
}
