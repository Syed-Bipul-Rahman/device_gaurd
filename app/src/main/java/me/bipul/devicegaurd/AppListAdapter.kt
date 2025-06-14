package me.bipul.devicegaurd

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AppListAdapter : ListAdapter<AppInfo, AppListAdapter.AppViewHolder>(AppDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_app, parent, false)
        return AppViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val appInfo = getItem(position)
        holder.bind(appInfo)
    }

    class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val appIcon: ImageView = itemView.findViewById(R.id.appIcon)
        private val appName: TextView = itemView.findViewById(R.id.appName)
        private val lastUsed: TextView = itemView.findViewById(R.id.lastUsed)
        private val usageTime: TextView = itemView.findViewById(R.id.usageTime)

        fun bind(appInfo: AppInfo) {
            appIcon.setImageDrawable(appInfo.icon)
            appName.text = appInfo.appName
            lastUsed.text = if (appInfo.lastUsed > 0) {
                SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                    .format(Date(appInfo.lastUsed))
            } else {
                "Not used recently"
            }
            usageTime.text = formatUsageTime(appInfo.totalTimeInForeground)

            // Set click listener to launch the app
            itemView.setOnClickListener {
                launchApp(itemView.context, appInfo.packageName)
            }
        }

        private fun launchApp(context: Context, packageName: String) {
            try {
                val intent = context.packageManager.getLaunchIntentForPackage(packageName)
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, "Cannot launch this app", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error launching app: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        private fun formatUsageTime(millis: Long): String {
            val hours = millis / (1000 * 60 * 60)
            val minutes = (millis % (1000 * 60 * 60)) / (1000 * 60)
            val seconds = (millis % (1000 * 60)) / 1000
            return when {
                hours > 0 -> "$hours h $minutes m"
                minutes > 0 -> "$minutes m $seconds s"
                else -> "$seconds s"
            }
        }
    }

    class AppDiffCallback : DiffUtil.ItemCallback<AppInfo>() {
        override fun areItemsTheSame(oldItem: AppInfo, newItem: AppInfo): Boolean {
            return oldItem.packageName == newItem.packageName
        }

        override fun areContentsTheSame(oldItem: AppInfo, newItem: AppInfo): Boolean {
            return oldItem == newItem
        }
    }
}