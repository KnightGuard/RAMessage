package com.softtanck.ramessageservice

import android.app.Service
import android.content.Intent
import android.os.*
import android.text.TextUtils
import android.util.Log
import com.softtanck.*
import com.softtanck.model.RaCustomMessenger
import com.softtanck.model.RaRequestTypeArg
import com.softtanck.model.RaRequestTypeParameter
import java.lang.reflect.Method

/**
 * @author Softtanck
 * @date 2022/3/12
 * Description: TODO
 */
abstract class BaseConnectionService : Service() {
    private val TAG: String = BaseConnectionService::class.java.simpleName
    private val workHandlerThread = HandlerThread(TAG)

    init {
        // Start the work's thread at first, And it is a single object!
        if (!workHandlerThread.isAlive) workHandlerThread.start()
    }

    fun onRemoteMessageArrived(message: Message, isSyncCall: Boolean): Message? {
        Log.d(TAG, "[SERVER] onRemoteMessageArrived: $message, what:${message.what} isSyncCall:$isSyncCall, trxID:${message.arg1}")
        try {
            val serBundle: Bundle? = message.data?.apply { classLoader = this@BaseConnectionService.javaClass.classLoader }
            if (serBundle == null) {
                return null
            } else {
                val remoteName = serBundle.getString(MESSAGE_BUNDLE_METHOD_NAME_KEY)
                if (TextUtils.isEmpty(remoteName)) return null
                Log.d(TAG, "[SERVER] remoteMethodName:$remoteName")
                val requestParameters: ArrayList<RaRequestTypeParameter> = serBundle.getParcelableArrayList<RaRequestTypeParameter>(MESSAGE_BUNDLE_TYPE_PARAMETER_KEY) as ArrayList<RaRequestTypeParameter>
                val requestArgs: ArrayList<RaRequestTypeArg> = serBundle.getParcelableArrayList<RaRequestTypeArg>(MESSAGE_BUNDLE_TYPE_ARG_KEY) as ArrayList<RaRequestTypeArg>
                val getPdfData: Method = this.javaClass.getDeclaredMethod(remoteName!!, *Array(requestParameters.size) { requestParameters[it].parameterTypeClasses })
                getPdfData.isAccessible = true
                val remoteCallResult = getPdfData.invoke(this, *Array(requestArgs.size) { requestArgs[it].arg })
                message.arg1 = message.arg1 // Remember the callback key
                message.data = Bundle().apply {
                    if (remoteCallResult is Parcelable) {
                        putInt(MESSAGE_BUNDLE_RSP_TYPE_KEY, MESSAGE_BUNDLE_PARCELABLE_TYPE)
                        putParcelable(MESSAGE_BUNDLE_NORMAL_RSP_KEY, remoteCallResult as Parcelable?)
                    } else if (remoteCallResult is ArrayList<*>) {
                        putInt(MESSAGE_BUNDLE_RSP_TYPE_KEY, MESSAGE_BUNDLE_ARRAYLIST_TYPE)
                        putParcelableArrayList(MESSAGE_BUNDLE_NORMAL_RSP_KEY, remoteCallResult as java.util.ArrayList<out Parcelable>?)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "[SERVER] Remote call failed:${e.message}")
            return null
        }
        return if (isSyncCall) {
            message
        } else {
            sendMsgToClient(message)
            null
        }
    }

    @Suppress("LeakingThis")
    private val customProcessHandler: RaServerHandler = RaServerHandler(workHandlerThread.looper, this)
    private val raCustomMessenger: RaCustomMessenger = RaCustomMessenger((customProcessHandler.getIMessenger(false) as IInterface).asBinder())

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "[SERVER] onCreate")
        startForeground(RaNotification.BASE_CONNECTION_SERVICE_NOTIFICATION_ID, RaNotification.getNotificationForInitSetup(this))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "[SERVER] onStartCommand")
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        val clientVersionCode = intent.getIntExtra(RaCustomMessenger.raMsgVersion.first, -1)
        Log.d(TAG, "[SERVER] onBind coming, the client version code is:$clientVersionCode")
        // Return our custom Binder if that is supported in Clients. (IPC)
        return if (raCustomMessenger.binder != null && clientVersionCode != -1 && RaCustomMessenger.raMsgVersion.second >= clientVersionCode) {
            raCustomMessenger.binder
        } else {
            (customProcessHandler.getIMessenger() as IInterface).asBinder()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (workHandlerThread.isAlive) workHandlerThread.quitSafely()
        Log.d(TAG, "[SERVER] onDestroy")
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Log.d(TAG, "[SERVER] onTaskRemoved")
    }

    private fun sendMsgToClient(message: Message) {
        customProcessHandler.sendMsgToClient(message)
    }
}