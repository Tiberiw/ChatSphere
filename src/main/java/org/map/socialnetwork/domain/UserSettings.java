package org.map.socialnetwork.domain;

public class UserSettings {
    private User user;
    private int nrElementsPage;

    public UserSettings(User user, int nrElementsPage) {
        this.user = user;
        this.nrElementsPage = nrElementsPage;
    }

    public void setNrElementsPage(int nrElementsPage) {
        this.nrElementsPage = nrElementsPage;
    }

    public int getNrElementsPage() {
        return this.nrElementsPage;
    }
}
