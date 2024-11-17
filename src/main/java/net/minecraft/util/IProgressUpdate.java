package net.minecraft.util;

public interface IProgressUpdate
{
    void displaySavingString(String message);

    void resetProgressAndMessage(String message);

    void displayLoadingString(String message);

    void setLoadingProgress(int progress);

    void setDoneWorking();
}
