package com.example.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.media3.common.PlaybackException
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.NoRouteToHostException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.nio.channels.UnresolvedAddressException

object NetworkUtils {
    fun hasActiveInternetConnection(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return true
        val activeNetwork = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN))
    }

    fun isNetworkOrDnsFailure(error: Throwable?): Boolean {
        if (error is PlaybackException) {
            when (error.errorCode) {
                PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED,
                PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT -> return true
            }
        }
        var current: Throwable? = error
        var depth = 0
        while (current != null && depth < 12) {
            when (current) {
                is UnknownHostException,
                is UnresolvedAddressException,
                is ConnectException,
                is NoRouteToHostException,
                is SocketTimeoutException,
                is SocketException,
                is InterruptedIOException -> return true
            }
            val msg = current.message?.lowercase().orEmpty()
            if (msg.contains("unable to resolve host") ||
                msg.contains("no address associated with hostname") ||
                msg.contains("failed to connect") ||
                msg.contains("network is unreachable")
            ) {
                return true
            }
            current = current.cause
            depth++
        }
        return false
    }
}
