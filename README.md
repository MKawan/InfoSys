# InfoSys 🌟

**InfoSys** is a system manager with a JavaFX graphical interface. It provides real-time visualization of active processes and mounted disks, with features like usage bars, disk root opening, dynamic themes, and customizable keyboard shortcuts. 🚀

---

## Features ✨

### Process Monitoring 🖥️
- Displays running processes with details such as PID, user, CPU, memory, and threads.
- Automatic periodic updates (e.g., every 2 seconds) ⏱️.
- Filter by process name and options to show all or only the current user's processes 🔍.
- Persistent selection, even after updates 🔒.

### Process Context Menu 🖱️
- Right-click on a process row opens a menu with options like:
  - Properties, Memory Map, Open Files, Change Priority, Set Affinity, Suspend, Resume, Terminate, Kill ⚙️.
- Actions available per process with specific windows 🪟.

### Keyboard Shortcuts ⌨️
- Navigation with global and process-row-specific keys.
- Example: `Enter` opens the affinity window, `Ctrl+P` opens **Process Properties**, etc. 🚪.

### Disk Visualization 💾
- Cards showing disk usage with progress bars, model, serial, size, and partitions 📊.
- Dynamic theme for icons — updates in real-time when the theme changes 🎨.
- Double-click on a card opens the **disk root** in the system's file explorer 📂.

### Panel Navigation 🧭
- Navigation bar with buttons to alternation between `Process`, `Resources`, and `File System` 🔄.
- Shortcut `F1` opens/closes the main navigation menu 🚪.

---

## How to Use 🛠️

### Prerequisites 📋
- Java 11 or higher with JavaFX configured ☕.
- Dependencies: OSHI (for disk and process monitoring) 📦.

### Running ▶️
```bash
git clone https://github.com/MKawan/InfoSys.git
cd InfoSys
mvn clean javafx:run
```
Or run the main class with your IDE (e.g., ApplicationLauncher) 🖥️.

### Project Structure 📁
```bash
src/
├── main/
│   ├── java/br/com/mk/
│   │   ├── components/
│   │   │   ├── menu/             # Context menus for processes 🖱️
│   │   │   ├── window/           # Process, Resources, FileSystem panels 🪟
│   │   │   └── nav/              # Navigation bar 🧭
│   │   ├── config/               # Configuration management ⚙️
│   │   ├── data/                 # Process and disk monitoring 📊
│   │   └── utils/                # Helpers: KeyMaps, FileSystemServices, ThemeManager 🛠️
│   └── resources/
│       ├── icons/                # Application icons 🖼️
│       └── css/                  # Application CSS themes 🎨
└── pom.xml
```

### Relevant Code Examples 📜

#### Disk Opening on Double Click 🖱️
```java
card.setOnMouseClicked(event -> {
    if (event.getClickCount() == 2) {
        String path = d.partitions().isEmpty()
            ? d.name()
            : d.partitions().get(0).mountPoint();

        if (path == null || path.isEmpty()) return;
        File diskRoot = new File(path);

        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            Runtime.getRuntime().exec(new String[]{"explorer.exe", diskRoot.getAbsolutePath()});
        } else if (os.contains("mac")) {
            Runtime.getRuntime().exec(new String[]{"open", diskRoot.getAbsolutePath()});
        } else {
            Runtime.getRuntime().exec(new String[]{"xdg-open", diskRoot.getAbsolutePath()});
        }
    }
});
```

#### Global Shortcut in TableView ⌨️
```java
table.setOnKeyPressed(event -> {
    ProcessInfo selected = table.getSelectionModel().getSelectedItem();
    if (selected == null) return;

    String keyName = KeyMaps.getSimpleKeyName(event.getCode());
    if ("Enter".equals(keyName)) {
        int cores = Runtime.getRuntime().availableProcessors();
        new SetAffinityWindow(selected.pid(), cores).show();
        event.consume();
    }

    String combo = KeyMaps.getComboKeyName(event);
    if ("Copy".equals(combo)) { /* Copy action */ }
});
```

#### Dynamic Theme with ThemeManager 🎨
```java
ImageView icon = new ImageView(getIconForDevice(d, ThemeManager.getCurrentTheme()));
ThemeManager.addThemeChangeListener(newTheme ->
    icon.setImage(getIconForDevice(d, newTheme))
);
```

### Suggested Future Improvements 🚀
- Support for multiple partitions with individual opening 💾.
- Add real-time CPU/memory graphs 📈.
- Internationalization (i18n) for multiple languages 🌐.
- Native packaging installation (jpackage / exe / app bundle) 📦.
- CLI support for automation 🖥️.

### License 📜
This project is licensed under the **MIT License** ✅.

### Contribution 🤝
Contributions are welcome! To contribute:
1. Fork the repository 🍴.
2. Create a branch (`feature/new-feature`) 🌿.
3. Commit your changes 📝.
4. Submit a pull request explaining your enhancement 🚀.

### Contact 📬
Developed by [MKawan](https://github.com/MKawan). To discuss improvements or provide feedback, open an issue or contact via GitHub 🙌.

**InfoSys** is an evolving project — thank you for exploring, testing, and contributing! 🎉
