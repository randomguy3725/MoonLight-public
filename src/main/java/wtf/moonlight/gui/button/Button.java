package wtf.moonlight.gui.button;

public interface Button {

    void initGui();

    void drawScreen(int mouseX, int mouseY);

    void mouseClicked(int mouseX, int mouseY, int button);

}