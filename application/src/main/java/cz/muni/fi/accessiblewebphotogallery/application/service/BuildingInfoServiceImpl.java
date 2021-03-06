package cz.muni.fi.accessiblewebphotogallery.application.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import cz.muni.fi.accessiblewebphotogallery.application.service.iface.BuildingInfoService;
import cz.muni.fi.accessiblewebphotogallery.persistence.dao.BuildingInfoDao;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.BuildingInfo;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.PhotoEntity;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
public class BuildingInfoServiceImpl implements BuildingInfoService {


    private static final Logger log = LogManager.getLogger(BuildingInfoServiceImpl.class);
    private BuildingInfoDao infoDao;
    private static final double EPSILON = 0.0001;
    private MessageDigest hasher;
    private Base64.Encoder enc;
    private Random rng;
    private Gson jsonConverter;
    // 7 digits of precision in OSM GPS coordinates(https://wiki.openstreetmap.org/wiki/Node) allows ~6mm precision;
    // after that new Nominatim result, 4 are left, meaning ~5.566 m... oh well not like the average building is smaller
    // than that, right...

    @Autowired
    public BuildingInfoServiceImpl(BuildingInfoDao infoDao) {
        this.infoDao = infoDao;
        enc = Base64.getUrlEncoder();
        rng = new Random();
        hasher = null;
        jsonConverter = new Gson();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BuildingInfo> findAll() {
        return infoDao.findAll();
    }

    @Override
    public Optional<BuildingInfo> findById(String id) {
        return infoDao.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BuildingInfo> findByPhoto(PhotoEntity photo) {
        return infoDao.findByPhoto(photo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BuildingInfo> findByBuildingNameApx(String searchStr) {
        return infoDao.findByBuildingNameContainingIgnoreCase(searchStr);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BuildingInfo> findByOsmId(Long osmId) {
        return infoDao.findByOsmId(osmId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BuildingInfo> findByGPSPosition(double lat, double lon) {
        return infoDao.findByLatitudeBetweenAndLongitudeBetween(lat - EPSILON, lat + EPSILON, lon - EPSILON, lon + EPSILON);
    }

    @Override
    @Transactional
    public List<BuildingInfo> registerBuildings(Map<JsonObject, JsonObject> buildingMap, JsonObject camData, PhotoEntity photo) {
        Validate.notNull(camData);
        if (hasher == null) {
            try {
                hasher = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException nsae) {
                log.catching(nsae);
                log.error(nsae.getMessage());
                log.error("BuildingInfoService could not obtain an MD5 MessageDigest instance.");
                return null;
            }
        }
        List<BuildingInfo> rv = new ArrayList<>();
        Pair<Double, Double> cameraGPS = Pair.of(camData.get("latitude").getAsDouble(), camData.get("longitude").getAsDouble());
        double azimuth = camData.get("azimuth").getAsDouble();
        double hFOV = camData.get("horizontal").getAsDouble();
        // not sure how modulo works on doubles, but ints will work
        int azimuthInt = (int) Math.round(azimuth);
        int hFOVInt = (int) Math.round(hFOV);// we're approximating here, this will do
        BigInteger threeSixty = new BigInteger(String.valueOf(360));
        int photoLeftBound = new BigInteger(String.valueOf(azimuthInt - (hFOVInt / 2))).mod(threeSixty).intValue();
        int photoRightBound = new BigInteger(String.valueOf(azimuthInt + (hFOVInt / 2))).mod(threeSixty).intValue();
        // prefer Nominatim data, as based on given samples, AssistiveCamera data may be inaccurate
        for (JsonObject k : buildingMap.keySet()) {
            List<ByteBuffer> hashData = new ArrayList<>();
            JsonObject v = buildingMap.get(k);
            BuildingInfo newInfo = new BuildingInfo();
            newInfo.setPhoto(photo);
            hashData.add(ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(photo.hashCode()));
            if (v != null && v.has("osm_id")) {
                newInfo.setOsmId(v.get("osm_id").getAsLong());
            } else {
                newInfo.setOsmId(-1L);
            }
            hashData.add(ByteBuffer.allocate(8).order(ByteOrder.BIG_ENDIAN).putLong(newInfo.getOsmId()));
            if (k.has("distance")) {
                newInfo.setDistance(k.get("distance").getAsInt());
            } else {
                newInfo.setDistance(-1);
            }
            if (v != null && v.has("lat")) {
                newInfo.setLatitude(v.get("lat").getAsDouble());
            } else if (k.has("latitude")) {
                newInfo.setLatitude(k.get("latitude").getAsDouble());
            } else {
                newInfo.setLatitude(0);
            }
            hashData.add(ByteBuffer.allocate(8).putDouble(newInfo.getLatitude()));
            if (v != null && v.has("lon")) {
                newInfo.setLongitude(v.get("lon").getAsDouble());
            } else if (k.has("longitude")) {
                newInfo.setLongitude(k.get("longitude").getAsDouble());
            } else {
                newInfo.setLongitude(0);
            }
            if (v != null && v.has("display_name")) {
                String s = v.get("display_name").getAsString();
                newInfo.setBuildingName(s);
                newInfo.setFocusText(s);
            } else if (k.has("name")) {
                String s = k.get("name").getAsString();
                newInfo.setBuildingName(s);
                newInfo.setFocusText(s);
            } else {
                newInfo.setBuildingName(null);
                newInfo.setFocusText("Unknown building at latitude " + newInfo.getLatitude() + " and logitude: " + newInfo.getLongitude());
            }
            hashData.add(ByteBuffer.allocate(8).putDouble(newInfo.getLongitude()));
            if (v == null || !v.has("boundingbox")) {
                newInfo.setLeftBoundInPhoto(-1);
                newInfo.setRightBoundInPhoto(-1);
                rv.add(infoDao.save(newInfo));
            } else {
                JsonArray coords = v.get("boundingbox").getAsJsonArray();
                Double latMin = coords.get(0).getAsDouble();
                Double latMax = coords.get(1).getAsDouble();
                Double lonMin = coords.get(2).getAsDouble();
                Double lonMax = coords.get(3).getAsDouble();
                List<Pair<Double, Double>> boundingBox = new ArrayList<>();
                boundingBox.add(Pair.of(latMin, lonMin));
                boundingBox.add(Pair.of(latMin, lonMax));
                boundingBox.add(Pair.of(latMax, lonMin));
                boundingBox.add(Pair.of(latMax, lonMax));
                double[] bBoxDists = new double[4];
                for (int i = 0; i < 4; i++) {
                    bBoxDists[i] = computeDistance(cameraGPS, boundingBox.get(i));
                }
                List<Pair<Double, Double>> threeClosest = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    int idx = findMinIndex(bBoxDists);
                    threeClosest.add(boundingBox.get(idx));
                    bBoxDists[idx] = Double.MAX_VALUE;
                }
                int[] bearings = new int[3];
                for (int i = 0; i < 3; i++) {
                    bearings[i] = calculateBearing(cameraGPS, threeClosest.get(i));
                }
                Arrays.sort(bearings);
                for (int i = 0; i < 3; i++) {
                    bearings[i] = (bearings[i] + 360) % 360; // normalise to [0,359]
                }
                int leftBoundInPhoto = -1, rightBoundInPhoto = -1;
                if (isPastLeftBound(bearings[0], photoLeftBound)) {
                    leftBoundInPhoto = 0;
                } else {
                    int bearingDiff1 = Math.abs(bearings[0] - photoLeftBound);
                    int bearingDiff2 = Math.min(bearingDiff1, 360 - bearingDiff1);
                    double bearingPhotoStart = bearingDiff2 / photo.getCameraFOV();
                    // how far, in ratio, into the photo the building 'starts'
                    leftBoundInPhoto = (int) Math.round(bearingPhotoStart * photo.getImageWidth());
                }
                newInfo.setLeftBoundInPhoto(leftBoundInPhoto);
                if (isPastRightBound(bearings[2], photoRightBound)) {
                    rightBoundInPhoto = photo.getImageWidth();
                } else {
                    int bearingDiff1 = Math.abs(photoRightBound - bearings[2]);
                    int bearingDiff2 = Math.min(bearingDiff1, 360 - bearingDiff1);
                    double bearingPhotoEnd = bearingDiff2 / photo.getCameraFOV();
                    // how far, in ratio, into the photo the building 'ends' (from the right, so we subtract that much from the end)
                    rightBoundInPhoto = (int) Math.round(bearingPhotoEnd - (bearingPhotoEnd * photo.getImageWidth()));
                }
                newInfo.setRightBoundInPhoto(rightBoundInPhoto);
                String base64;
                do{
                    hashData.add(ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(rng.nextInt()));
                    base64 = computeBase64(hashData);
                }while (infoDao.findById(base64).isPresent());
                newInfo.setId(base64);
                rv.add(infoDao.save(newInfo));
            }
        }
        return rv;
    }

    @Override
    @Transactional
    public BuildingInfo updateBuildingInfo(BuildingInfo buildingInfo) {
        return infoDao.save(buildingInfo);
    }

    @Override
    @Transactional
    public List<BuildingInfo> updateBuildings(List<BuildingInfo> buildingList) {
        return infoDao.saveAll(buildingList);
    }

    @Override
    @Transactional
    public void delete(BuildingInfo buildingInfo) {
        infoDao.delete(buildingInfo);
    }

    private int findMinIndex(double[] data) {
        double z = Double.MAX_VALUE;
        int rv = -1;
        for (int i = 0; i < data.length; i++) {
            if (data[i] < z) rv = i;
        }
        return rv;
    }

    private double computeDistance(Pair<Double, Double> start, Pair<Double, Double> end) {
        // using haversine formula
        double earthAvgRadius = 6371; // in km
        double deltaLat = Math.toRadians(end.getFirst() - start.getFirst());
        double deltaLon = Math.toRadians(end.getSecond() - start.getSecond());
        double radLat1 = Math.toRadians(start.getFirst());
        double radLat2 = Math.toRadians(end.getFirst());
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthAvgRadius * c;
    }

    private int calculateBearing(Pair<Double, Double> start, Pair<Double, Double> end) {
        // calculates forward azimuth, which, followed in a straight line, will lead from start to end;
        // technically the path ends up following its inverse toward the end, but for short distances
        // (~200 m or less) this should do;
        // returns a value within [-180;180] which is good for sorting
        double deltaLon = Math.toRadians(end.getSecond() - start.getSecond());
        double lat1Rad = Math.toRadians(start.getFirst());
        double lat2Rad = Math.toRadians(end.getFirst());
        double y = Math.sin(deltaLon) * Math.cos(lat2Rad);
        double x = Math.cos(lat1Rad) * Math.sin(lat2Rad) - Math.sin(lat1Rad) * Math.cos(lat2Rad) * Math.cos(deltaLon);
        return (int) Math.round(Math.toDegrees(Math.atan2(y, x)));
    }

    private boolean isPastLeftBound(int bearing, int leftBound) {
        return leftBound - bearing >= 0 || (bearing > 180 && leftBound < 180);
    }

    private boolean isPastRightBound(int bearing, int rightBound) {
        return bearing - rightBound >= 0 || (bearing < 180 && rightBound > 180);
    }

    private String computeBase64(List<ByteBuffer> data) {
        hasher.reset();
        for (ByteBuffer buf : data) {
            hasher.update(buf.array());
        }
        byte[] hashResult = hasher.digest();
        byte[] dbHash = Arrays.copyOfRange(hashResult, 8, 20);
        return enc.encodeToString(dbHash);
    }

}
