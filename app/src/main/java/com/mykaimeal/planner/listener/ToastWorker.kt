package com.mykaimeal.planner.listener

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.Data

class ToastWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        Log.d("ToastWorker", "Worker started")

        // Show Toast on Main Thread
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(applicationContext, "24 hours passed! Calling API now.", Toast.LENGTH_LONG).show()
        }

        // Return Output Data
        val outputData = Data.Builder()
            .putString("response", "Time completed, calling API")
            .build()

        Log.d("ToastWorker", "Worker finished, returning data")
        return Result.success(outputData)
    }

}