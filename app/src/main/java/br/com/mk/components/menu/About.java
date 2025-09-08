package br.com.mk.components.menu;

import br.com.mk.config.ConfigManager;
import br.com.mk.utils.ThemeManager;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class About extends Stage {

	public About() {
		this.initModality(Modality.APPLICATION_MODAL);

		VBox containerBox = new VBox(30);

		Image icon = new Image(getClass().getResourceAsStream("/icons/AppIcon.png"));
		ImageView iconView = new ImageView(icon);
		iconView.getStyleClass().add("disk-icon"); // classe CSS

		Text title = new Text("InfoSys - System Monitor");
		title.getStyleClass().add("title");

		String[] about = { "1.0.0", "View current processes and monitor system health",
				"Copyright © 2025 Mateus Oliveira", "This program comes with absolutely no guarantees." };

		containerBox.getChildren().addAll(iconView, title);

		for (String t : about) {

			Text textDev = new Text(t);
//        	textDev.setStyle("-fx-fill: #c2c2ff; -fx-font-weight: bold; -fx-font-size: 12px;");
			textDev.getStyleClass().add("infor-about");
//        	textBox.getChildren().add(textDev);
			containerBox.getChildren().add(textDev);

		}
		Text textLink = new Text("For more details, visit ");
		textLink.getStyleClass().add("infor-about");

		Label link = new Label("GitHub");
		link.setOnMouseClicked(e -> {
			Help.openLink("https://github.com/MKawan/System_Monitor");
		});
		link.getStyleClass().add("infor-about-link");

		HBox textLinkBox = new HBox();
		textLinkBox.getChildren().addAll(textLink, link);
		textLinkBox.setAlignment(Pos.CENTER);

		containerBox.getChildren().add(textLinkBox);
		containerBox.setAlignment(Pos.CENTER);

		Scene scene = new Scene(containerBox, 400, 450);
		scene.getStylesheets().add(
			    getClass().getResource("/css/" + ConfigManager.getString("themes", "Default") + ".css").toExternalForm()
		);
		ThemeManager.registerScene(scene); // registra a scene principal

		this.setScene(scene);
		this.show();
	}
}
