public class MediaItem{
	private String title;
	private String artist;
	protected String mediaType;
	private String id;
	
	public MediaItem(String title, String artist){
		this.title = title;
		this.artist = artist;
		this.mediaType = "";
	}
	
	public String getTitle(){
		return this.title;
	}
	
	public String getArtist(){
		return this.artist;
	}
	
	public String getMediaType(){
		return this.mediaType;
	}
	
	@Override
	public String toString(){
		return String.format("Title: %s\nArtist: %s\n", 
			this.title, this.artist);
	}
	
	public String getId(){
		return id;
	}
	
	public void setId(String id){
		this.id = id;
	}
}
