module br.com.mk {
    requires java.base;
    requires java.desktop;
    requires javafx.controls;
    requires javafx.graphics;
    requires com.github.oshi;
    
    exports br.com.mk;
    exports br.com.mk.components.buttons;
    exports br.com.mk.components.card;
    exports br.com.mk.components.cardGraphics;
    exports br.com.mk.components.graphics;
    exports br.com.mk.components.menu;
    exports br.com.mk.components.nav;
    exports br.com.mk.components.panels;
    exports br.com.mk.components.process.contextMenu;
    exports br.com.mk.components.window;
    exports br.com.mk.config;
    exports br.com.mk.data;
    exports br.com.mk.utils;


}
