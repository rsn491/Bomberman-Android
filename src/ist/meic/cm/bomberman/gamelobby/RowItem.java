package ist.meic.cm.bomberman.gamelobby;

public class RowItem {
	private int imageId;
	private String title;
	private String desc;

	public RowItem(int imageId, String title, String desc) {
		this.imageId = imageId;
		this.title = title;
		this.desc = desc;
	}

	public int getImageId() {
		return imageId;
	}

	public void setImageId(int imageId) {
		this.imageId = imageId;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(title);
		sb.append("\n> ");
		sb.append(desc);
		return sb.toString();
	}
}