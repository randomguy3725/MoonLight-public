package net.optifine.http;

import net.minecraft.client.Minecraft;

public class FileDownloadThread extends Thread
{
    private String urlString = null;
    private IFileDownloadListener listener = null;

    public FileDownloadThread(String urlString, IFileDownloadListener listener)
    {
        this.urlString = urlString;
        this.listener = listener;
    }

    public void run()
    {
        try
        {
            byte[] abyte = HttpPipeline.get(this.urlString, Minecraft.getMinecraft().getProxy());
            this.listener.fileDownloadFinished(this.urlString, abyte, null);
        }
        catch (Exception exception)
        {
            this.listener.fileDownloadFinished(this.urlString, null, exception);
        }
    }

    public String getUrlString()
    {
        return this.urlString;
    }

    public IFileDownloadListener getListener()
    {
        return this.listener;
    }
}
