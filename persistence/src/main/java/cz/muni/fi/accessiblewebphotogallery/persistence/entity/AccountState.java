package cz.muni.fi.accessiblewebphotogallery.persistence.entity;

/**
 * A simple account state to distinguish between regular users and administrators.
 * Regular users can upload, remove and edit(info about) their own photos.
 * Administrators can remove and edit(info about) anyone's photos.
 * Inactive users first need to activate their account via an activation link that will be e-mailed to them (TBD).
 */
public enum AccountState {
    INACTIVE, // before activation/ after ban(?)
    USER, // regular user
    ADMINISTRATOR // administrator with extra options (remove others' photos, de-activate accounts)- reserved
}
