package org.beuwi.msgbots.platform.win;

import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

public class WindowScene extends Scene
{
	public WindowScene(Region root)
	{
		super(root);

		setFill(Color.TRANSPARENT);

		// getStylesheets().add(ResourceUtils.getTheme("base"));
	}
}