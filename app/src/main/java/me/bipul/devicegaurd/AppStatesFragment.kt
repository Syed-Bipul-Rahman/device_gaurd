package me.bipul.devicegaurd

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import me.bipul.devicegaurd.databinding.FragmentAppStatesBinding
import android.app.usage.UsageStatsManager
import android.os.Process
import java.util.*

class AppStatesFragment : Fragment() {

    private var _binding: FragmentAppStatesBinding? = null
    private val binding get() = _binding!!
    private lateinit var appListAdapter: AppListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppStatesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView
        appListAdapter = AppListAdapter()
        binding.appListRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = appListAdapter
        }

        // Setup permission request button
        binding.btnRequestUsageAccess.setOnClickListener {
            startActivityForResult(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),2)
        }

        // Update app list based on permission status
        updateAppList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun updateAppList() {
        if (hasUsageStatsPermission()) {
            binding.permissionPrompt.visibility = View.GONE
            binding.btnRequestUsageAccess.visibility = View.GONE
            binding.appListRecyclerView.visibility = View.VISIBLE
            loadAppUsageStats()
        } else {
            binding.permissionPrompt.visibility = View.VISIBLE
            binding.btnRequestUsageAccess.visibility = View.VISIBLE
            binding.appListRecyclerView.visibility = View.GONE
        }
    }

    private fun hasUsageStatsPermission(): Boolean {
        val appOps = requireContext().getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            requireContext().packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    //return list with system app
    private fun loadAppUsageStats() {
        val usageStatsManager = requireContext().getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            calendar.timeInMillis,
            System.currentTimeMillis()
        )

        val packageManager = requireContext().packageManager
        val appList = stats.mapNotNull { usageStats ->
            try {
                val appInfo = packageManager.getApplicationInfo(usageStats.packageName, 0)
                AppInfo(
                    packageName = usageStats.packageName,
                    appName = packageManager.getApplicationLabel(appInfo).toString(),
                    icon = packageManager.getApplicationIcon(appInfo),
                    lastUsed = usageStats.lastTimeUsed,
                    totalTimeInForeground = usageStats.totalTimeInForeground
                )
            } catch (e: PackageManager.NameNotFoundException) {
                null
            }
        }.sortedByDescending { it.totalTimeInForeground }

        appListAdapter.submitList(appList)
    }



    //return list without system app
//    private fun loadAppUsageStats() {
//        val usageStatsManager = requireContext().getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
//        val packageManager = requireContext().packageManager
//        val calendar = Calendar.getInstance()
//        calendar.add(Calendar.WEEK_OF_YEAR, -1) // Use weekly interval
//        val stats = usageStatsManager.queryUsageStats(
//            UsageStatsManager.INTERVAL_WEEKLY,
//            calendar.timeInMillis,
//            System.currentTimeMillis()
//        ).associateBy { it.packageName }
//
//        // Get all installed apps
//        val installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
//        val userFacingSystemApps = listOf(
//            "com.android.contacts",
//            "com.google.android.contacts",
//            "com.google.android.apps.photos",
//            "com.android.dialer",
//            "com.google.android.dialer",
//            "com.android.gallery3d",
//            "com.google.android.gallery3d"
//        )
//
//        val appList = installedApps.mapNotNull { appInfo ->
//            try {
//                val isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
//                val isUpdatedSystemApp = (appInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
//                val packageName = appInfo.packageName
//
//                // Include user-installed apps, updated system apps, or specific user-facing system apps
//                if (!isSystemApp || isUpdatedSystemApp || packageName in userFacingSystemApps) {
//                    val usageStats = stats[packageName]
//                    AppInfo(
//                        packageName = packageName,
//                        appName = packageManager.getApplicationLabel(appInfo).toString(),
//                        icon = packageManager.getApplicationIcon(appInfo),
//                        lastUsed = usageStats?.lastTimeUsed ?: 0L,
//                        totalTimeInForeground = usageStats?.totalTimeInForeground ?: 0L
//                    )
//                } else {
//                    null
//                }
//            } catch (e: PackageManager.NameNotFoundException) {
//                null
//            }
//        }.sortedByDescending { it.totalTimeInForeground }
//
//        appListAdapter.submitList(appList)
//    }
}