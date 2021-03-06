package org.beuwi.msgbots.platform.app.view.dialogs;

import javafx.application.Platform;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;
import org.beuwi.msgbots.manager.FileManager;
import org.beuwi.msgbots.openapi.FormLoader;
import org.beuwi.msgbots.platform.app.action.CreateBotAction;
import org.beuwi.msgbots.platform.app.view.MainView;
import org.beuwi.msgbots.platform.gui.control.Button;
import org.beuwi.msgbots.platform.gui.control.TextField;
import org.beuwi.msgbots.platform.gui.control.VBox;
import org.beuwi.msgbots.platform.gui.dialog.DialogBoxWrap;

import java.io.File;

public class ImportBotDialog extends DialogBoxWrap
{
	private final ObservableMap<String, Object> nameSpace;

	private final FormLoader loader;

	private final VBox root;

	@FXML private TextField txfScriptName;
	@FXML private Button btnScriptOpen;

	// Use Unified Parameters
	@FXML private CheckBox chkIsUnified;

	// Off On Runtime Error
	@FXML private CheckBox chkIsOffError;

	private File file;

	public ImportBotDialog()
	{
		loader = new FormLoader("dialog", "import-bot-dialog", this);
		nameSpace = loader.getNamespace();
		root = loader.getRoot();

		btnScriptOpen.setOnAction(event ->
		{
			FileChooser fileChooser = new FileChooser();
			fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JavaScript File", "*.js"));
			fileChooser.setTitle("Import Script");

			file = fileChooser.showOpenDialog(MainView.getStage());

			if (file != null)
			{
				txfScriptName.setText(file.getName());
			}
		});

		setOnKeyPressed(event ->
		{
			if (event.getCode().equals(KeyCode.ENTER))
			{
				if (!txfScriptName.getText().isEmpty())
				{
					this.action();
				}
			}
		});

		Platform.runLater(() ->
		{
			txfScriptName.requestFocus();
		});
	}

	@Override
	public void open()
	{
		setContent(root);
		setTitle("Import Script");
		create();
	}

	@Override
	public void action()
	{
		CreateBotAction.execute
		(
			txfScriptName.getText(),
			FileManager.read(file), true,
			chkIsUnified.isSelected(),
			chkIsOffError.isSelected()
		);
	}
}
