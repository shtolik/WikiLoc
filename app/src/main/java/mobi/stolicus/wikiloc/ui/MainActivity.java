package mobi.stolicus.wikiloc.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mobi.stolicus.wikiloc.BuildConfig;
import mobi.stolicus.wikiloc.R;
import mobi.stolicus.wikiloc.WikiLocApp;
import mobi.stolicus.wikiloc.model.Continue;
import mobi.stolicus.wikiloc.model.Image;
import mobi.stolicus.wikiloc.model.WikiArticlesResponse;
import mobi.stolicus.wikiloc.model.WikiImagesResponse;
import mobi.stolicus.wikiloc.network.WikiService;
import mobi.stolicus.wikiloc.support.ModelHelper;
import mobi.stolicus.wikiloc.support.SimilarityHelper;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends AppCompatActivity {

	public static final int PERMISSIONS_CODE = 1;
	protected static final Logger logger = LoggerFactory.getLogger(MainActivity.class);
	@Inject
	public FusedLocationProviderClient mFusedLocationClient;

	@Inject
	public WikiService mWikiService;

	@BindView(R.id.location)
	public TextView tvLocation;

	@BindView(R.id.articles)
	public TextView tvArticles;

	@BindView(R.id.similar)
	public TextView tvSimilar;

	@BindView(R.id.all_images)
	public TextView tvAllImages;
	ProgressDialog progressDialog;
	private CompositeSubscription compositeSubscription = new CompositeSubscription();
	private Location lastLocation = null;
	private Set<Image> allImages = new HashSet<>();
	private Set<Integer> imageIds = new HashSet<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		WikiLocApp.get(this).getAppComponent().inject(this);

		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(R.string.please_wait);
		progressDialog.setMessage(getString(R.string.getting_location));
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(true);
	}

	@Override
	protected void onResume() {
		super.onResume();
		startLookup(false);
	}

	/**
	 * checking permission and requesting location on start and on button click
	 */
	@OnClick(R.id.lookup)
	public void onLookup() {
		startLookup(true);
	}

	/**
	 * @param reset whether to reset data to start a new request from scratch
	 */
	public void startLookup(boolean reset) {
		if (BuildConfig.IS_DEBUG && reset) {
			lastLocation = null;
			allImages = new HashSet<>();
			imageIds = new HashSet<>();
		}

		if (ContextCompat.checkSelfPermission(this,
				Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_CODE);
		} else {
			requestLocation();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		compositeSubscription.clear();
	}

	/**
	 * requesting location through google play service
	 */
	@SuppressWarnings("MissingPermission")
	private void requestLocation() {
		logger.debug("/requestLocation/starting getting location");
		mFusedLocationClient.getLastLocation().addOnSuccessListener(this, this::onLocationReceived);
		showProgress(R.string.getting_location);
	}

	/**
	 * updating location in ui and requesting data from wiki
	 *
	 * @param location last known location
	 */
	public void onLocationReceived(Location location) {
		logger.debug("/requestLocation/onSuccess/{}", location);
		//remembering last location in case we need to get permission from user
		lastLocation = location;

		// Got last known location. In some rare situations this can be null.
		if (location != null) {

			tvLocation.post(() -> tvLocation.setText(getString(R.string.location, location.toString())));

			startWikiRequest(location);
		}
	}

	/**
	 * starting to request wiki articles near location if internet permission given
	 *
	 * @param location last known location
	 */
	private void startWikiRequest(Location location) {
		if (ContextCompat.checkSelfPermission(this,
				Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
			//but no internet permission yet
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, PERMISSIONS_CODE);
		} else {
			requestWikiArticles(location, null);
		}
	}

	/**
	 * starts to request wiki articles near given location
	 *
	 * @param location device location
	 * @param continueInResponse continue field from response if it was there
	 */
	private void requestWikiArticles(@Nullable Location location, @Nullable Continue continueInResponse) {
		if (location == null) {
			return;
		}

		if (lastLocation != null && location.equals(lastLocation) && allImages != null && !allImages.isEmpty()) {
			logger.debug("/requestWikiArticles/Location didn't change:{} and there are images downloaded already, not requesting again", location.toString());
			String warn = getString(R.string.location, location.toString()) + getString(R.string.location_didnt_change);
			tvLocation.post(() -> tvLocation.setText(warn));
			stopProgress();
			return;
		}

		final Observable<WikiArticlesResponse> observable = mWikiService.getArticles(location, continueInResponse);

		Subscription subscription = observable.subscribeOn(Schedulers.newThread())
				.subscribe(this::onWikiArticlesReceived, this::handleConnectionError);
		compositeSubscription.add(subscription);

		if (continueInResponse == null)
			showProgress(R.string.requesting_articles);
		else
			showProgress(R.string.requesting_more_articles);
	}

	/**
	 * Called when response with articles is received and parsed.
	 * Updates ui and finds article ids to requests images in those articles
	 *
	 * @param articlesResponse response with articles
	 */
	private void onWikiArticlesReceived(@Nullable WikiArticlesResponse articlesResponse) {

		tvArticles.post(() -> tvArticles.setText(String.format(getString(R.string.articles), ModelHelper.getTitlesFromArticles(articlesResponse))));

		List<Integer> newImageIds = ModelHelper.getImageIdsFromArticles(articlesResponse);
		imageIds.addAll(newImageIds);

		if (articlesResponse != null && articlesResponse.batchcomplete == null && articlesResponse._continue != null) {
			logger.debug("/onWikiArticlesReceived/{}/{}/request is not yet complete: need to request continuation of data from:{}"
					, newImageIds.size(), imageIds.size(), articlesResponse._continue.imcontinue);
			requestWikiArticles(lastLocation, articlesResponse._continue);
		} else {
			logger.debug("/onWikiImagesReceived//{}/{}/all articles received. Requesting images now.",
					newImageIds.size(), imageIds.size());
			requestImages(imageIds, null);
		}
	}

	/**
	 * requests images found in articles from wiki
	 *
	 * @param pageids list of page ids found
	 */
	private void requestImages(@NonNull final Set<Integer> pageids, Continue continueInResponse) {
		final Observable<WikiImagesResponse> observable = mWikiService.getImages(pageids.iterator(), continueInResponse);

		Subscription subscription = observable.subscribeOn(Schedulers.newThread())
				.subscribe(this::onWikiImagesReceived, this::handleConnectionError);
		compositeSubscription.add(subscription);
		if (continueInResponse == null)
			showProgress(R.string.requesting_images);
		else
			showProgress(R.string.requesting_more_images);
	}

	/**
	 * On receiving list of images, looks for similar images and updates ui.
	 *
	 * @param wikiImagesResponse response model with images from wiki
	 */
	private void onWikiImagesReceived(@Nullable WikiImagesResponse wikiImagesResponse) {

		List<Image> newImages = ModelHelper.getImageNamesFromWikiImages(wikiImagesResponse);
		allImages.addAll(newImages);

		String allImagesText = ModelHelper.getImageTitlesString(allImages.iterator(), false);
		tvAllImages.post(() -> tvAllImages.setText(String.format(getString(R.string.all_images), allImagesText)));


		if (wikiImagesResponse != null && wikiImagesResponse.batchcomplete == null && wikiImagesResponse._continue != null) {
			logger.debug("/onWikiImagesReceived/{}/{}/request is not yet complete: need to request continuation of data from:{}"
					, newImages.size(), allImages.size(), wikiImagesResponse._continue.imcontinue);

			requestImages(imageIds, wikiImagesResponse._continue);

		} else {
			logger.debug("/onWikiImagesReceived//{}/{}/all images received",
					newImages.size(), allImages.size());
			prepareAndShowResultingTopImages();
		}

	}

	/**
	 * once all images received, sort them and show
	 */
	private void prepareAndShowResultingTopImages() {

		SimilarityHelper.calculateSimilarity(allImages);

		List<Image> similarImages = SimilarityHelper.getSortedTopSimilar(allImages, SimilarityHelper.MAX_SIMILAR_COUNT);

		String similarImagesText = ModelHelper.getImageTitlesString(similarImages.iterator(), true);
		tvSimilar.post(() -> tvSimilar.setText(String.format(getString(R.string.similar_images), similarImagesText)));

		stopProgress();
	}

	/**
	 * showing progress with specific message
	 * @param id string resource id
	 */
	private void showProgress(int id) {
		runOnUiThread(() -> {
			progressDialog.setMessage(getString(id));
			progressDialog.show();
		});

	}

	/**
	 * stop showing progress
	 */
	private void stopProgress() {
		runOnUiThread(() -> {
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
		});
	}

	/**
	 * handles conenctions error and warns user
	 *
	 * @param error info about error
	 */
	private void handleConnectionError(@NonNull Throwable error) {
		if (error instanceof UnknownHostException) {
			logger.warn("/handleConnectionError/no internet or problem with host address");
			Snackbar.make(tvSimilar, R.string.NotifMessNoConnAvailable, Snackbar.LENGTH_LONG).show();
		} else {
			logger.error("/handleConnectionError/{}", error);
		}
		stopProgress();
	}

	/**
	 * Handles response from OS, after request to check if permissions are granted for app to function
	 * Depending on requested permission and if it's granted could start request for location
	 * or request for articles with last location
	 *
	 * @param requestCode  request code
	 * @param permissions  permissions about which info is given
	 * @param grantResults whether permission is granted
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		if (requestCode != PERMISSIONS_CODE) {
			return;
		}
		for (int i = 0; i < permissions.length; i++) {
			String permission = permissions[i];
			int grantResult = grantResults[i];
			if (permission.equals(Manifest.permission.ACCESS_FINE_LOCATION) && grantResult == PackageManager.PERMISSION_GRANTED) {
				requestLocation();
			} else if (permission.equals(Manifest.permission.INTERNET) && grantResult == PackageManager.PERMISSION_GRANTED) {
				if (lastLocation != null)
					requestWikiArticles(lastLocation, null);
			} else {
				Toast.makeText(this, R.string.need_internet, Toast.LENGTH_LONG).show();
			}
		}
	}
}
