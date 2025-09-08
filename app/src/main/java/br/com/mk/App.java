package br.com.mk;

import br.com.mk.components.menu.Help;
import br.com.mk.components.nav.NavPanel;
import br.com.mk.components.window.FilesSystem;
import br.com.mk.components.window.Process;
import br.com.mk.components.window.Recourses;
import br.com.mk.config.ConfigManager;
import br.com.mk.utils.KeyMaps;
import br.com.mk.utils.ThemeManager;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {

	@Override
	public void start(Stage stage) {
		VBox root = new VBox();
		root.getStyleClass().add("root");

		// Coloca o VBox dentro de um ScrollPane
		ScrollPane scrollPane = new ScrollPane(root);
		scrollPane.setFitToWidth(true);
		scrollPane.setFitToHeight(false);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);


		Process process = new Process();
		Recourses recourses = new Recourses();
		FilesSystem filesSystem = new FilesSystem();

		// Painel de navegação
		NavPanel navPanel = new NavPanel(process);

		// Container que vai trocar os ambientes
		VBox contentContainer = new VBox();
		contentContainer.setSpacing(10);

		// Inicializa com o ambiente padrão
		contentContainer.getChildren().add(recourses);
		navPanel.updateActiveButton("Recourses");

		// Listener de troca de ambiente
		navPanel.setOnNavChangeListener(env -> {
			contentContainer.getChildren().clear(); // limpa só o conteúdo

			switch (env) {
			case "Process":
				scrollPane.setFitToHeight(true);
				contentContainer.getChildren().add(process);
				VBox.setVgrow(process, Priority.ALWAYS); // apenas Process cresce
				break;
			case "Recourses":
				scrollPane.setFitToHeight(false);
				contentContainer.getChildren().add(recourses);
				break;
			case "Files System":
				// Adicione aqui seu painel de sistema de arquivos
				scrollPane.setFitToHeight(true);
				contentContainer.getChildren().add(filesSystem);
				break;
			}

			// Atualiza destaque dos botões
			navPanel.updateActiveButton(env);
		});

		// Adiciona NavPanel e container de conteúdo ao root
		VBox.setVgrow(contentContainer, Priority.ALWAYS); // ocupa todo espaço restante
		root.getChildren().addAll(navPanel, contentContainer);

		// Scene
		Scene scene = new Scene(scrollPane, 1200, 550);
		scene.getStylesheets().add(getClass()
				.getResource("/css/" + ConfigManager.getString("themes", "Default") + ".css").toExternalForm());
		ThemeManager.registerScene(scene); // registra a scene principal


		scene.setOnKeyPressed(event -> {

		    if (event.getCode() == KeyCode.F11) {
		        stage.setFullScreen(!stage.isFullScreen());
		    } else if (event.getCode() == KeyCode.F1) {
		        NavPanel.MenuShow(process);
		    } else if (event.getCode() == KeyCode.F10) {
		        Help.openLink("https://github.com/MKawan/InfoSys");
		    }
		});


		// Define tamanho mínimo da janela
		// stage.initStyle(StageStyle.UNDECORATED); // remove barra nativa

		Image icon = new Image(getClass().getResourceAsStream("/icons/AppIcon.png"));
		stage.getIcons().add(icon);

		stage.setTitle("InfoSys - System Monitor");
		stage.setMinWidth(1200);
		stage.setMinHeight(550);
		stage.setOnCloseRequest(event -> {
			Platform.exit(); // encerra todas as Scenes e Stages
			System.exit(0); // encerra a JVM
		});

		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch();
	}
}