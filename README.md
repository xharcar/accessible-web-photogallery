# accessible-web-photogallery
Master's thesis Java application, "Accessible web photogallery".

General notes:
- Methods looking for strings with the suffix "Apx" look for partial strings and ignore case, "Apx" standing for "Approximate".
- There's a PhotoGalleryBackendMapper; changed from a DozerBeanMapper because that wouldn't work with 
java.time.Instant instances, and the compatibility library ended up throwing parse errors, so I figured
it'd be simpler if I just rolled my own mapper and moved on.
- Derby ended up not working yet again, so a switch to PostgreSQL was made.
how-to:
1) install PostgreSQL (unless you want to switch to your preferred DBMS)
2) (assuming PGDATA is set) postgres -D $PGDATA/%PGDATA% (Linux/Windows)
- DB and application config had to (for now) be internalised again.
Will try to re-externalise.
