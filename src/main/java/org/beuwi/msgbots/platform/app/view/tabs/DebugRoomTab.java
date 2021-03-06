package org.beuwi.msgbots.platform.app.view.tabs;

import javafx.collections.ObservableMap;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.beuwi.msgbots.openapi.FormLoader;
import org.beuwi.msgbots.platform.app.impl.View;
import org.beuwi.msgbots.platform.app.view.actions.SendChatMessageAction;
import org.beuwi.msgbots.platform.gui.control.ChatView;
import org.beuwi.msgbots.platform.gui.control.Tab;
import org.beuwi.msgbots.platform.gui.control.TextArea;
import org.beuwi.msgbots.platform.gui.control.VBox;

public class DebugRoomTab implements View
{
	private static ObservableMap<String, Object> namespace;

	private static FormLoader loader;

	private static Tab root;

	private static VBox component;

	@Override
	public void init()
	{
		loader = new FormLoader("tab", "debug-room-tab");
		namespace = loader.getNamespace();
		root = loader.getRoot();
		component = loader.getComponent();

		ChatView listView = (ChatView) namespace.get("lsvChatView");
		TextArea textArea = (TextArea) namespace.get("txaChatInput");

		textArea.addEventFilter(KeyEvent.KEY_PRESSED, event ->
		{
			if (event.getCode().equals(KeyCode.ENTER))
			{
				if (event.isShiftDown())
				{
					textArea.appendText(System.lineSeparator());
					event.consume();
					return ;
				}

				String text = textArea.getText();

				if (text.trim().isEmpty())
				{
					if (text.isEmpty())
					{
						textArea.clear();
					}

					event.consume();
					return ;
				}

				SendChatMessageAction.execute(text);

				textArea.clear();
				event.consume();
			}
		});

		textArea.requestFocus();
	}

	public static Tab getRoot()
	{
		return root;
	}

	public static VBox getComponent()
	{
		return component;
	}

	public static <T> T getComponent(String key)
	{
		return (T) namespace.get(key);
	}

	public static ObservableMap<String, Object> getNamespace()
	{
		return namespace;
	}
}