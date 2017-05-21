import javax.swing.JFrame;

public class ViewTest {

	public static void main(String[] args) {
		View view = new View();
		view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		view.setSize(500, 400);
		view.setVisible(true);
	}
}