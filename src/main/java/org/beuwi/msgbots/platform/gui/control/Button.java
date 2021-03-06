package org.beuwi.msgbots.platform.gui.control;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;

public class Button extends javafx.scene.control.Button
{
	private static final String DEFAULT_STYLE_CLASS = "button";
	private static final Insets DEFAULT_PADDING_INSETS = new Insets(0, 10, 0, 10);

	private static final PseudoClass ACTION_PSEUDO_CLASS = PseudoClass.getPseudoClass("action");
	private static final PseudoClass CANCEL_PSEUDO_CLASS = PseudoClass.getPseudoClass("cancel");

	private static final int DEFAULT_MIN_WIDTH = 50;
 	private static final int DEFAULT_MIN_HEIGHT = 20;

	private static final int DEFAULT_PREF_WIDTH = 70;
	private static final int DEFAULT_PREF_HEIGHT = 50;

	private final ObjectProperty<Type> type = new SimpleObjectProperty(null);

	private final BooleanProperty styled = new SimpleBooleanProperty(false);

	public enum Type
	{
		ACTION, CANCEL
	}

	public Button()
	{
		this(null);
	}

	public Button(String text)
	{
		if (text != null)
		{
			setText(text);
		}

		styled.addListener(change ->
		{
			pseudoClassStateChanged(PseudoClass.getPseudoClass("styled"), getStyled());

			if (getStyled())
			{
				setMinWidth(DEFAULT_MIN_WIDTH);
				setMinHeight(DEFAULT_MIN_HEIGHT);
				// setPrefWidth(DEFAULT_PREF_WIDTH);
				setPrefHeight(DEFAULT_PREF_HEIGHT);
			}
		});

		type.addListener(change ->
		{
			pseudoClassStateChanged(ACTION_PSEUDO_CLASS, getType().equals(Type.ACTION));
			pseudoClassStateChanged(CANCEL_PSEUDO_CLASS, getType().equals(Type.CANCEL));
		});

		getTextProperty().addListener((observable, oldValue, newValue) ->
		{
			if (getText() != null)
			{
				setPadding(DEFAULT_PADDING_INSETS);
			}
		});

		// setType(Type.CANCEL);
	}

	public Type getType()
	{
		return type.get();
	}

	public boolean getStyled()
	{
		return styled.get();
	}

	public void setType(Type type)
	{
		this.type.set(type);
	}

	public void setMenu(ContextMenu menu)
	{
		menu.setNode(this);
	}

	public void setStyled(boolean styled)
	{
		this.styled.set(styled);
	}

	public ReadOnlyStringProperty getTextProperty()
	{
		return textProperty();
	}
}
