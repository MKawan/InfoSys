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
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {

    // Instâncias globais, mas inicializadas sob demanda
    private Process processPanel;
    private Recourses recoursesPanel;
    private FilesSystem filesSystemPanel;

    @Override
    public void start(Stage stage) {
        VBox root = new VBox();
        root.getStyleClass().add("root");

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        // Painel de navegação
        NavPanel navPanel = new NavPanel(null); // Passa null por enquanto

        // Container para trocar os ambientes
        VBox contentContainer = new VBox();
        contentContainer.setSpacing(10);

        // Inicializa painel Recourses por padrão, sob demanda
        recoursesPanel = new Recourses();
        contentContainer.getChildren().add(recoursesPanel);
        navPanel.updateActiveButton("Recourses");

        // Listener de troca de ambiente (cria instâncias apenas quando necessário)
		navPanel.setOnNavChangeListener(env -> {
		    // Limpa o conteúdo anterior
		    contentContainer.getChildren().clear();
		
		    switch (env) {
		        case "Process" -> {
		            scrollPane.setFitToHeight(true);
		
				if (processPanel == null) {
		                processPanel = new Process();
		            }
		
		            // Faz o painel Process ocupar toda a altura disponível
		            VBox.setVgrow(processPanel, Priority.ALWAYS);
		            processPanel.setMaxHeight(Double.MAX_VALUE);
		
		            contentContainer.getChildren().add(processPanel);
		        }
		
		        case "Recourses" -> {
		            scrollPane.setFitToHeight(false);
		
		            if (recoursesPanel == null) {
		                recoursesPanel = new Recourses();
		            }
		
		            // Recourses não cresce verticalmente
		            VBox.setVgrow(recoursesPanel, Priority.NEVER);
		
		            contentContainer.getChildren().add(recoursesPanel);
		        }
		
		        case "Files System" -> {
		            scrollPane.setFitToHeight(true);
		
		            if (filesSystemPanel == null) {
		                filesSystemPanel = new FilesSystem();
		            }
		
		            // Faz o painel FilesSystem crescer verticalmente
		            VBox.setVgrow(filesSystemPanel, Priority.ALWAYS);
		            filesSystemPanel.setMaxHeight(Double.MAX_VALUE);
		
		            contentContainer.getChildren().add(filesSystemPanel);
		        }
		    }

    // Atualiza destaque dos botões
    navPanel.updateActiveButton(env);
});


        VBox.setVgrow(contentContainer, Priority.ALWAYS);
        root.getChildren().addAll(navPanel, contentContainer);

        // Cena principal
        Scene scene = new Scene(scrollPane, 1200, 550);
        scene.getStylesheets().add(
                getClass().getResource("/css/" + ConfigManager.getString("themes", "Default") + ".css").toExternalForm()
        );
        ThemeManager.registerScene(scene);

        // Atalhos de teclado unificados
        scene.setOnKeyPressed(event -> {
            String combo = KeyMaps.getComboKeyName(event);
            if (combo != null) {
                System.out.println("Atalho detectado: " + combo);
            }

            switch (event.getCode()) {
                case F11 -> stage.setFullScreen(!stage.isFullScreen());
                case F1 -> {
                    if (processPanel == null) processPanel = new Process();
                    NavPanel.MenuShow(processPanel);
                }
                case F10 -> Help.openLink("https://github.com/MKawan/InfoSys");
            }
        });

        // Ícone do app
        Image icon = new Image(getClass().getResourceAsStream("/icons/AppIcon.png"));
        stage.getIcons().add(icon);

        stage.setTitle("InfoSys - System Monitor");
        stage.setMinWidth(1200);
        stage.setMinHeight(550);

        // Encerramento limpo da aplicação
        stage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
