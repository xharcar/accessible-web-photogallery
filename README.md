# accessible-web-photogallery
Master's thesis Java application, "Accessible web photogallery".

General notes:
- Methods looking for strings with the suffix "Apx" look for partial strings and ignore case, "Apx" standing for "Approximate".
- There's a PhotoGalleryBackendMapper; changed from a DozerBeanMapper because that wouldn't work with 
java.time.Instant instances, and the compatibility library ended up throwing parse errors, so I figured
it'd be simpler if I just rolled my own mapper and moved on.
