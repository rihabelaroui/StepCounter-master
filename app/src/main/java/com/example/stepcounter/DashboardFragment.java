package com.example.stepcounter;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.microsoft.maps.Geopoint;
import com.microsoft.maps.MapAnimationKind;
import com.microsoft.maps.MapElement;
import com.microsoft.maps.MapElementLayer;
import com.microsoft.maps.MapIcon;
import com.microsoft.maps.MapRenderMode;
import com.microsoft.maps.MapScene;
import com.microsoft.maps.MapTappedEventArgs;
import com.microsoft.maps.MapView;
import com.microsoft.maps.OnMapTappedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment implements LocationListener {

    private MapView mMapView;
    private LocationManager locationManager;
    private SearchView searchView;
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Initialize the map view
        mMapView = new MapView(getActivity(), MapRenderMode.VECTOR);
        mMapView.setCredentialsKey(getString(R.string.maps_api_key));
        ((FrameLayout) rootView.findViewById(R.id.map_view)).addView(mMapView);


        // Get the LocationManager
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        showUserLocation();
        // Setup the button click listener
        Button btnShowLocation = rootView.findViewById(R.id.btnShowLocation);
        btnShowLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Request location updates only when the button is clicked
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            1000, 10, DashboardFragment.this);
                }
                showUserLocation();
            }
        });

        searchView = rootView.findViewById(R.id.searchView);

        // Set up a click listener for the search button
        Button btnSearch = rootView.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch();
            }
        });

        // Set up a query listener for the SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle search when user submits query
                performSearch();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle search as the user types (optional)
                return false;
            }
        });

        mMapView.addOnMapTappedListener(new OnMapTappedListener() {
            @Override
            public boolean onMapTapped(MapTappedEventArgs mapTappedEventArgs) {
                Point position = mapTappedEventArgs.position;
                List<MapElement> elements = mMapView.findMapElementsAtOffset(position);

                for (MapElement mapElement : elements) {
                    if (mapElement instanceof MapIcon) {
                        MapIcon mapIcon = (MapIcon) mapElement;
                        showMapElementDetails(mapIcon);
                        return true; // Return true to consume the event
                    }
                }

                return false;
            }
        });

        return rootView;
    }

    private void performSearch() {
        // Get the query from the SearchView
        mMapView.getLayers().clear();
        String query = searchView.getQuery().toString();

        // Execute the search task
        new LocalSearchTask().execute(query);
    }

    private void showUserLocation() {
        if (mMapView != null) {
            // Get the last known location
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastKnownLocation != null) {
                    Geopoint userLocation = new Geopoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                    MapIcon userLocationIcon = new MapIcon();
                    userLocationIcon.setLocation(userLocation);
                    userLocationIcon.setTitle("Your Location");

                    MapElementLayer userLocationLayer = new MapElementLayer();
                    userLocationLayer.getElements().add(userLocationIcon);

                    mMapView.getLayers().add(userLocationLayer);

                    mMapView.setScene(MapScene.createFromLocationAndZoomLevel(userLocation, 15), MapAnimationKind.BOW);
                }
            }
        }
    }

    // Implement the LocationListener methods
    @Override
    public void onLocationChanged(Location location) {
        // Update the map with the new location
        if (mMapView != null) {
            Geopoint userLocation = new Geopoint(location.getLatitude(), location.getLongitude());
            mMapView.setScene(MapScene.createFromLocationAndZoomLevel(userLocation, 15), MapAnimationKind.BOW);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Handle status changes if needed
    }

    @Override
    public void onProviderEnabled(String provider) {
        // Handle provider enabled
    }

    @Override
    public void onProviderDisabled(String provider) {
        // Handle provider disabled
    }

    // Override other lifecycle methods as needed

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
    private class LocalSearchTask extends AsyncTask<String, Void, List<MapElement>> {
        @Override
        protected List<MapElement> doInBackground(String... params) {
            String query = params[0];
            Log.d("query",query);

            String bingMapsKey = getString(R.string.maps_api_key);
            String searchUrl = null;
            try {
                searchUrl = "https://dev.virtualearth.net/REST/v1/Locations/" +
                        URLEncoder.encode(query, "UTF-8") + "?o=json&key=" + bingMapsKey;
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

            List<MapElement> searchResults = new ArrayList<>();
            try {
                URL url = new URL(searchUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                JSONObject jsonResponse = new JSONObject(response.toString());
                Log.d("query",jsonResponse.toString());
                JSONArray resources = jsonResponse.getJSONArray("resourceSets")
                        .getJSONObject(0).getJSONArray("resources");
                for (int i = 0; i < resources.length(); i++) {
                    JSONObject resource = resources.getJSONObject(i);
                    double lat = resource.getJSONObject("point").getJSONArray("coordinates").getDouble(0);
                    double lon = resource.getJSONObject("point").getJSONArray("coordinates").getDouble(1);
                    String name = resource.getString("name");
                    JSONObject address = resource.optJSONObject("address");
                    String formattedAddress = (address != null) ? address.optString("formattedAddress", "") : "";
                    String locality = (address != null) ? address.optString("locality", "") : "";
                    String entityType = resource.optString("entityType", "");

                    Log.d("LocationInfo", "Name: " + name);
                    Log.d("LocationInfo", "Formatted Address: " + formattedAddress);
                    Log.d("LocationInfo", "Locality: " + locality);
                    Log.d("LocationInfo", "Entity Type: " + entityType);

                    MapIcon mapIcon = new MapIcon();
                    mapIcon.setLocation(new Geopoint(lat, lon));
                    mapIcon.setTitle(name);
                    Bundle detailsBundle = new Bundle();
                    detailsBundle.putString("formattedAddress", formattedAddress);
                    detailsBundle.putString("locality", locality);
                    detailsBundle.putString("entityType", entityType);

                    mapIcon.setTag(detailsBundle);
                    searchResults.add(mapIcon);
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return searchResults;
        }

        @Override
        protected void onPostExecute(List<MapElement> searchResults) {
            if (mMapView != null) {
                MapElementLayer searchResultLayer = new MapElementLayer();
                for (MapElement mapElement : searchResults) {
                    searchResultLayer.getElements().add(mapElement);
                }
                mMapView.getLayers().add(searchResultLayer);
                MapIcon firstResult = (MapIcon) searchResults.get(0);
                Geopoint resultLocation = firstResult.getLocation();
                mMapView.setScene(MapScene.createFromLocationAndZoomLevel(resultLocation, 15), MapAnimationKind.BOW);

            }
        }}
    private void showMapElementDetails(MapIcon mapIcon) {
        String title = mapIcon.getTitle();
        Geopoint location = mapIcon.getLocation();

        Bundle detailsBundle = (Bundle) mapIcon.getTag();
        if (detailsBundle != null) {
            String formattedAddress = detailsBundle.getString("formattedAddress", "");
            String locality = detailsBundle.getString("locality", "");
            String entityType = detailsBundle.getString("entityType", "");

            StringBuilder detailsBuilder = new StringBuilder();
            detailsBuilder.append("Title: ").append(title).append("\n");
            detailsBuilder.append("Formatted Address: ").append(formattedAddress).append("\n");
            detailsBuilder.append("Locality: ").append(locality).append("\n");
            detailsBuilder.append("Entity Type: ").append(entityType);

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Location Details")
                    .setMessage(detailsBuilder.toString())
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }
}
