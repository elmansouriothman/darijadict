package com.example.darijadict.util

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ApkgDownloader {
    private const val TAG = "ApkgDownloader"
    private const val FILENAME = "darija_deck.apkg"
    private const val PROVIDER_AUTHORITY = "com.example.darijadict.fileprovider"
    private const val BUFFER_SIZE = 8192 // Optimal buffer size for file operations

    /**
     * Downloads the APKG file from assets to device storage
     * @return Pair<Boolean, String> where first is success status and second is message/file path
     */
    fun downloadApkg(context: Context): Pair<Boolean, String> {
        return try {
            // Verify asset exists
            if (!isAssetExists(context, FILENAME)) {
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
            copyAssetToFile(context, FILENAME, destinationFile)

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
    private fun isAssetExists(context: Context, filename: String): Boolean {
        return try {
            context.assets.list("")?.contains(filename) == true
        } catch (e: IOException) {
            false
        }
    }

    /**
     * Determines the appropriate storage location based on Android version
     */
    private fun getDestinationFile(context: Context): File? {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                // Use app-specific storage on Android 10+
                context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.let {
                    File(it, FILENAME).apply {
                        parentFile?.mkdirs() // Ensure directory exists
                    }
                }
            }
            else -> {
                // Try public Downloads first, then fallback to app-specific
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)?.let {
                        publicDir ->
                    if (publicDir.exists() || publicDir.mkdirs()) {
                        File(publicDir, FILENAME)
                    } else {
                        context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.let{
                            File(it, FILENAME).apply {
                                parentFile?.mkdirs()
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Performs the actual file copy operation with progress tracking
     */
    private fun copyAssetToFile(context: Context, assetName: String, destFile: File) {
        context.assets.open(assetName).use { inputStream ->
            FileOutputStream(destFile).use { outputStream ->
                val buffer = ByteArray(BUFFER_SIZE)
                var bytesRead: Int
                var totalBytes = 0L

                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                    totalBytes += bytesRead
                    Log.v(TAG, "Copied $totalBytes bytes...")
                }
                outputStream.flush()
            }
        }
    }

    /**
     * Generates a content URI for the downloaded file
     */
    fun getFileUri(context: Context, file: File): Uri {
        require(file.exists()) { "File must exist before generating URI" }
        return FileProvider.getUriForFile(
            context,
            PROVIDER_AUTHORITY,
            file
        ).also {
            Log.d(TAG, "Generated content URI: $it")
        }
    }

    /**
     * Helper to get human-readable file size
     */
    fun getFileSizeFormatted(file: File): String {
        val bytes = file.length()
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "%.1f KB".format(bytes / 1024.0)
            else -> "%.1f MB".format(bytes / (1024.0 * 1024.0))
        }
    }
}