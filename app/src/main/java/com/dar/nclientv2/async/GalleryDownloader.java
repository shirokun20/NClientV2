package com.dar.nclientv2.async;

import com.dar.nclientv2.api.components.Gallery;
import com.dar.nclientv2.async.database.Queries;
import com.dar.nclientv2.settings.Database;
import com.dar.nclientv2.settings.Global;

import java.io.IOException;

public class GalleryDownloader {
    public enum Status{NOT_STARTED,DOWNLOADING,PAUSED,FINISHED}
    private Gallery gallery;
    private Status status;
    private int progress;
    private boolean downloaded;
    private int id;
    public final int notificationId=Global.getNotificationId();
    public GalleryDownloader(Gallery gallery,Status status) {
        this.gallery = gallery;
        id=gallery.getId();
        this.status=status;
        progress=0;
        setDownloaded(gallery.isComplete());
    }
    public GalleryDownloader(int id,Status status) {
        this.id = id;
        this.gallery = null;
        this.status=status;
        progress=0;
        setDownloaded(false);
    }
    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
    public int incrementProgress(){
        return ++progress;
    }

    public String getCover(){
        if(gallery!=null)return gallery.getThumbnail();
        return null;
    }

    public Gallery getGallery() {
        if(!downloaded) {
            try {
                gallery=Gallery.galleryFromId(id);
                setDownloaded(true);
            } catch (IOException ignore) { }
        }
        return gallery;
    }
    public Gallery completeGallery()throws IOException{
        if(downloaded)return gallery;
        gallery=Gallery.galleryFromId(id);
        setDownloaded(true);
        return gallery;
    }
    public Status getStatus() {
        return status;
    }

    public void setDownloaded(boolean downloaded) {
        this.downloaded = downloaded;
        if(downloaded)Queries.DownloadTable.addGallery(Database.getDatabase(),gallery);
    }

    public void setStatus(Status status) {
        this.status = status;
        if(status==Status.FINISHED)Queries.DownloadTable.removeGallery(Database.getDatabase(),id);
    }
    public String getTitle(){
        if(gallery!=null)return gallery.getSafeTitle();
        return "";
    }
    public int getPercentage(){
        if(!downloaded)return 0;
        return (progress*100)/gallery.getPageCount();
    }
}
