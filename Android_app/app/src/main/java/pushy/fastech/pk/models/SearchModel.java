package pushy.fastech.pk.models;

import ir.mirrajabi.searchdialog.core.Searchable;

public class SearchModel implements Searchable {
    private String mTitle;
    private String mData;


    public SearchModel(String mTitle) {
        this.mTitle = mTitle;
    }

    public SearchModel(String mTitle, String data) {
        this.mTitle = mTitle;
        this.mData = data;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    public void setmData(String mData) {
        this.mData = mData;
    }
}
