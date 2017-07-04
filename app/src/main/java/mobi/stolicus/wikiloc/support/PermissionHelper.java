package mobi.stolicus.wikiloc.support;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * For handling permissions
 * Created by shtolik on 03.07.2017.
 */

public class PermissionHelper {

	private static final Logger logger = LoggerFactory.getLogger(PermissionHelper.class);
	private static final int PERMISSIONS_CODE = 1;


	private PermissionHelper() {
		throw new IllegalAccessError("Utility helper class with only static methods and constants");
	}

	/**
	 * check permission of it's already granted, request it if not yet
	 *
	 * @param activity          activity which will handle onRequestPermissionResult
	 * @param permissionToCheck permission to check
	 * @param permissionsCode   code to use
	 * @return true if permission already granted, false otherwise
	 */
	public static boolean checkPermissionAndRequestIfNeeded(Activity activity, String permissionToCheck, int permissionsCode) {
		if (ContextCompat.checkSelfPermission(activity,
				permissionToCheck) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(activity, new String[]{permissionToCheck}, permissionsCode);
		} else {
			return true;
		}
		return false;

	}

	/**
	 * Handles checking of permission request results
	 *
	 * @param requestCode       code
	 * @param permissions       permissions requested
	 * @param grantResults      permissions granted
	 * @param permissionToCheck which permission to check
	 * @return true if permissionToCheck was granted
	 */
	public static boolean checkPermissionResult(int requestCode, String[] permissions, int[] grantResults, String permissionToCheck) {
		if (requestCode != PERMISSIONS_CODE) {
			return false;
		}
		for (int i = 0; i < permissions.length; i++) {
			String permission = permissions[i];
			int grantResult = grantResults[i];
			if (permission.equals(permissionToCheck) && grantResult == PackageManager.PERMISSION_GRANTED) {
				return true;
			}
		}
		return false;
	}
}
