package com.ascent.pmrsurveyapp.Models;

public class MenuModel {

    public String key, value, view, edit, delete, create, download, upload,icon;

    public MenuModel(String key, String value, String view, String edit, String delete, String create, String download, String upload, String icon) {

        this.key = key;
        this.value = value;
        this.view = view;
        this.edit = edit;
        this.delete = delete;
        this.create = create;
        this.download = download;
        this.upload = upload;
        this.icon = icon;

    }
}
