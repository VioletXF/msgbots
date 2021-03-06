package org.beuwi.msgbots.platform.gui.control;
import org.beuwi.msgbots.platform.gui.layout.StackPanel;

public class ColorBar extends StackPanel
{
	private static final String DEFAULT_STYLE_CLASS = "color-bar";
	private static final int DEFAULT_PREF_WIDTH = 5;

	public ColorBar()
	{
		setPrefWidth(DEFAULT_PREF_WIDTH);
		addStyleClass(DEFAULT_STYLE_CLASS);
	}

	public void setColor(String color)
	{
		setStyle("-fx-background-color:" + color);
	}
}