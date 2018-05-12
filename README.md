# accessible-web-photogallery
Master's thesis Java application, "Accessible web photogallery".

General notes:
- Methods looking for strings with the suffix "Apx" look for partial strings and ignore case, "Apx" standing for "Approximate".
- There's a PhotoGalleryBackendMapper; changed from a DozerBeanMapper because that wouldn't work with 
java.time.Instant instances, and the compatibility library ended up throwing parse errors, so I figured
it'd be simpler if I just rolled my own mapper and moved on.
- The DB, as it is now, is functional again (for testing at least). The accepted answer to
https://stackoverflow.com/questions/22922740/creating-configuring-derby-jdbc-client-in-intellij-idea-13
basically worked, except of course, there was no sub-menu for Derby in IDEA 2018.1.3, just the Derby(Remote) option;
and I've filled in my own preference as to the DB name
