package cz.muni.fi.accessiblewebphotogallery.persistence.entity;

public enum AccountState {
    INACTIVE, // before activation
    USER, // regular user
    ADMINISTRATOR // administrator with extra options (remove others' photos, de-activate accounts)- reserved
}
