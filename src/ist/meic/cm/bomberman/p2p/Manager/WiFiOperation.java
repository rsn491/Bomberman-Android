package ist.meic.cm.bomberman.p2p.Manager;

import ist.meic.cm.bomberman.multiplayerC.Message;
import android.os.Handler;

/**
 * This fragment handles chat related UI which includes a list view for messages
 * and a message entry field with send button.
 */
public class WiFiOperation {

	private Manager manager;

	public interface MessageTarget {
		public Handler getHandler();
	}

	public void setChatManager(Manager obj) {
		manager = obj;
	}

	public void pushOperation(Message readMessage) {// To Do
		/*
		 * adapter.add(readMessage); adapter.notifyDataSetChanged();
		 */
	}
}
