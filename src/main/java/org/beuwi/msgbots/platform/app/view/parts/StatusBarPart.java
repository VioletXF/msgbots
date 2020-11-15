package org.beuwi.msgbots.platform.app.view.parts;

import javafx.collections.ObservableMap;
import javafx.scene.layout.AnchorPane;

import org.beuwi.msgbots.openapi.FormLoader;
import org.beuwi.msgbots.platform.app.impl.View;
import org.beuwi.msgbots.platform.gui.control.HBox;

public class StatusBarPart implements View
{
	private static ObservableMap<String, Object> nameSpace;

	private static FormLoader loader;

	private static AnchorPane root;

	private static HBox component;

	@Override
	public void init()
	{
		loader = new FormLoader("status-bar-part");
		nameSpace = loader.getNamespace();
		root = loader.getRoot();
		component = loader.getComponent();
	}

	public static AnchorPane getRoot()
	{
		return root;
	}

	public static HBox getComponent()
	{
		return component;
	}

	public static ObservableMap<String, Object> getNameSpace()
	{
		return nameSpace;
	}
}