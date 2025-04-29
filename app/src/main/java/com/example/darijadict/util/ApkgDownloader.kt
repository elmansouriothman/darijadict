package com.example.darijadict.util

import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ApkgDownloader {
    private const val TAG = "ApkgDownloader"
    private const val FILENAME = "darija_deck.zip"
    private const val BUFFER_SIZE = 8192 // Optimal buffer size for file operations

    /**
     * Downloads the APKG file from assets to device storage
     * @return Pair<Boolean, String> where first is success status and second is message/file path
     */
    fun downloadApkg(context: Context): Pair<Boolean, String> {
        return try {
            // Verify asset exists
            if (!isAssetExists(context)) {
                Log.e(TAG, "Asset $FILENAME not found")
                return Pair(false, "File not found in app assets")
            }

            // Get destination file with fallback logic
            val destinationFile = getDestinationFile(context) ?: run {
                Log.e(TAG, "Failed to access storage")
                return Pair(false, "Cannot access device storage")
            }

            Log.d(TAG, "Target location: ${destinationFile.absolutePath}")

            // Handle existing file
            if (destinationFile.exists()) {
                if (!destinationFile.delete()) {
                    Log.e(TAG, "Failed to delete existing file")
                    return Pair(false, "Cannot overwrite existing file")
                }
            }

            // Perform the file copy
            copyAssetToFile(context, destinationFile)

            // Verify result
            if (!destinationFile.exists()) {
                Log.e(TAG, "File copy verification failed")
                return Pair(false, "Download failed")
            }

            Log.d(TAG, "Success. File size: ${destinationFile.length()} bytes")
            Pair(true, destinationFile.absolutePath)

        } catch (e: SecurityException) {
            Log.e(TAG, "Security violation", e)
            Pair(false, "Storage permission denied")
        } catch (e: IOException) {
            Log.e(TAG, "File operation failed", e)
            Pair(false, "File system error: ${e.localizedMessage}")
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error", e)
            Pair(false, "Unknown error occurred")
        }
    }

    /**
     * Checks if the specified asset exists
     */
    private fun isAssetExists(context: Context): Boolean {
        return try {
            context.assets.list("")?.contains(FILENAME) == true
        } catch (_: IOException) {
            false
        }
    }

    /**
     * Determines the appropriate storage location based on Android version
     */
    private fun getDestinationFile(context: Context): File? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Use getExternalFilesDir() for Android 10 and above
            File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), FILENAME)
        } else {
            // Use getExternalStoragePublicDirectory() for older versions
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), FILENAME)
        }
    }

    /**
     * Performs the actual file copy operation with progress tracking
     */
    private fun copyAssetToFile(context: Context, destFile: File) {
        try {
            context.assets.open(FILENAME).use { inputStream ->
                FileOutputStream(destFile).use { outputStream ->
                    val buffer = ByteArray(BUFFER_SIZE)
                    var bytesRead: Int
                    var totalBytes = 0L
                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                        totalBytes += bytesRead
                    }
                    Log.d(TAG, "File copy completed. Total bytes: $totalBytes")
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error copying asset to file: ${e.message}")
        }
    }

}