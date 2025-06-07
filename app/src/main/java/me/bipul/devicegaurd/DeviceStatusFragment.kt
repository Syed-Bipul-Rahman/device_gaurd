package me.bipul.devicegaurd

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.admin.DevicePolicyManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import me.bipul.devicegaurd.databinding.FragmentDeviceStatusBinding

class DeviceStatusFragment : Fragment() {

    private var _binding: FragmentDeviceStatusBinding? = null
    private val binding get() = _binding!!
    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var componentName: ComponentName

    // BroadcastReceiver to receive battery updates
    private val batteryInfoReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val batteryLevel: Int = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, 0) ?: 0
            val scale: Int = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, 100) ?: 100
            val status: Int = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
            val health: Int = intent?.getIntExtra(BatteryManager.EXTRA_HEALTH, -1) ?: -1

            val percentage = (batteryLevel * 100 / scale).toInt()

            // Update device card info
            updateDeviceCardInfo(percentage, status)

            // Update detailed battery info
            updateDetailedBatteryInfo(percentage, status, health)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeviceStatusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize device admin
        devicePolicyManager = requireContext().getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        componentName = ComponentName(requireContext(), MyDeviceAdminReceiver::class.java)

        // Set device name
        binding.deviceName.text = Build.MODEL
        binding.lastSeen.text = "Last seen just now"

        // Register receiver to get battery info
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        requireContext().registerReceiver(batteryInfoReceiver, filter)

        // Initial battery info update
        updateInitialBatteryInfo()

        // Setup button listeners
        binding.btnRequestAdmin.setOnClickListener {
            requestAdminPermission()
        }
        binding.btnLockDevice.setOnClickListener {
            lockDevice()
        }

        updateButtonStates()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireContext().unregisterReceiver(batteryInfoReceiver)
        _binding = null
    }

    private fun updateInitialBatteryInfo() {
        val batteryManager = requireContext().getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val batteryLevel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        } else {
            val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val batteryStatus = requireContext().registerReceiver(null, filter)
            val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
            if (level != -1 && scale != -1) (level * 100 / scale) else 0
        }

        binding.batteryPercentage.text = "$batteryLevel%"
    }

    private fun updateDeviceCardInfo(percentage: Int, status: Int) {
        binding.batteryPercentage.text = "$percentage%"

        // Update network type in card (simplified)
        val connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkType = when {
            connectivityManager.activeNetwork == null -> "Disconnected"
            else -> {
                val capabilities =
                    connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                when {
                    capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> getWifiNetworkName()
                    capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> getMobileNetworkType()
                    else -> "Unknown"
                }
            }
        }
        binding.networkType.text = networkType
    }

    private fun updateDetailedBatteryInfo(percentage: Int, status: Int, health: Int) {
        // Battery Status with color
        val batteryStatusText = getBatteryStatus(status)
        binding.batteryStatus.text = batteryStatusText
        when (status) {
            BatteryManager.BATTERY_STATUS_CHARGING, BatteryManager.BATTERY_STATUS_FULL -> {
                binding.batteryStatus.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        android.R.color.holo_green_dark
                    )
                )
            }
            else -> {
                binding.batteryStatus.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        android.R.color.holo_orange_dark
                    )
                )
            }
        }

        // Battery Health with color
        val batteryHealthText = getBatteryHealth(health)
        binding.batteryHealth.text = batteryHealthText
        when (health) {
            BatteryManager.BATTERY_HEALTH_GOOD -> {
                binding.batteryHealth.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        android.R.color.holo_green_dark
                    )
                )
            }
            BatteryManager.BATTERY_HEALTH_COLD, BatteryManager.BATTERY_HEALTH_OVERHEAT -> {
                binding.batteryHealth.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        android.R.color.holo_orange_dark
                    )
                )
            }
            else -> {
                binding.batteryHealth.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        android.R.color.holo_red_dark
                    )
                )
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun updateNetworkInfo() {
        val connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // Network Type for detailed section
        val networkType = when {
            connectivityManager.activeNetwork == null -> "No Connection"
            else -> {
                val capabilities =
                    connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                when {
                    capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> "WiFi"
                    capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> "Mobile Data"
                    else -> "Unknown"
                }
            }
        }
        binding.networkTypeDetailed.text = networkType

        // WiFi Name (SSID)
        val wifiName = getWifiNetworkName()
        binding.wifiName.text = wifiName

        // SIM Info and Network Status
        updateSimInfo()
    }

    private fun getWifiNetworkName(): String {
        return if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_WIFI_STATE)
            == PackageManager.PERMISSION_GRANTED
        ) {
            val wifiManager =
                requireContext().getSystemService(Context.WIFI_SERVICE) as WifiManager
            if (wifiManager.isWifiEnabled) {
                val wifiInfo = wifiManager.connectionInfo
                if (wifiInfo != null) {
                    val ssid = wifiInfo.ssid
                    if (ssid != null && ssid != "<unknown ssid>" && ssid != "\"<unknown ssid>\"") {
                        ssid.removeSurrounding("\"")
                    } else {
                        "<unknown ssid>"
                    }
                } else {
                    "Not Connected"
                }
            } else {
                "WiFi Disabled"
            }
        } else {
            "Permission Denied"
        }
    }

    private fun getMobileNetworkType(): String {
        return if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_PHONE_STATE)
            == PackageManager.PERMISSION_GRANTED
        ) {
            val telephonyManager = requireContext().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            when (telephonyManager.networkType) {
                TelephonyManager.NETWORK_TYPE_LTE -> "4G LTE"
                TelephonyManager.NETWORK_TYPE_HSPAP, TelephonyManager.NETWORK_TYPE_HSPA -> "3G+"
                TelephonyManager.NETWORK_TYPE_UMTS -> "3G"
                TelephonyManager.NETWORK_TYPE_EDGE -> "2G"
                else -> "Mobile Data"
            }
        } else {
            "Mobile Data"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateSimInfo() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_PHONE_STATE)
            == PackageManager.PERMISSION_GRANTED
        ) {
            val telephonyManager = requireContext().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

            // Operator
            val operatorName =
                telephonyManager.networkOperatorName?.takeIf { it.isNotEmpty() } ?: "No SIM"
            binding.simOperator.text = operatorName

            // SIM State
            val simStateText = when (telephonyManager.simState) {
                TelephonyManager.SIM_STATE_READY -> "Ready"
                TelephonyManager.SIM_STATE_ABSENT -> "Absent"
                TelephonyManager.SIM_STATE_NOT_READY -> "Not Ready"
                TelephonyManager.SIM_STATE_NETWORK_LOCKED -> "Network Locked"
                TelephonyManager.SIM_STATE_UNKNOWN -> "Unknown"
                else -> "Not Available"
            }
            binding.simState.text = simStateText

            // Network Status
            val networkStatus = if (operatorName != "No SIM") {
                val connectivityManager =
                    requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val capabilities =
                    connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                if (capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true) {
                    "Connected"
                } else {
                    "Disconnected"
                }
            } else {
                "Disconnected"
            }
            binding.networkStatus.text = networkStatus

            // Set colors for network status
            when (networkStatus) {
                "Connected" -> {
                    binding.networkStatus.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            android.R.color.holo_green_dark
                        )
                    )
                }
                else -> {
                    binding.networkStatus.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            android.R.color.holo_red_dark
                        )
                    )
                }
            }
        } else {
            binding.simOperator.text = "Permission Denied"
            binding.simState.text = "Permission Denied"
            binding.networkStatus.text = "Permission Denied"
        }
    }

    private fun getBatteryStatus(status: Int): String {
        return when (status) {
            BatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
            BatteryManager.BATTERY_STATUS_DISCHARGING -> "Discharging"
            BatteryManager.BATTERY_STATUS_FULL -> "Full"
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Not Charging"
            BatteryManager.BATTERY_STATUS_UNKNOWN -> "Unknown"
            else -> "Unknown"
        }
    }

    private fun getBatteryHealth(health: Int): String {
        return when (health) {
            BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
            BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
            BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Unspecified Failure"
            else -> "Unknown"
        }
    }

    private fun requestAdminPermission() {
        if (!devicePolicyManager.isAdminActive(componentName)) {
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
                putExtra(
                    DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    "This app needs device admin permission to lock the screen"
                )
            }
            startActivityForResult(intent, 1)
        } else {
            Toast.makeText(requireContext(), "Device admin already enabled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun lockDevice() {
        if (devicePolicyManager.isAdminActive(componentName)) {
            devicePolicyManager.lockNow()
            Toast.makeText(requireContext(), "Device locked", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Device admin permission required", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateButtonStates() {
        val isAdminActive = devicePolicyManager.isAdminActive(componentName)
        binding.btnRequestAdmin.isEnabled = !isAdminActive
        binding.btnLockDevice.isEnabled = isAdminActive
    }

    fun onAdminPermissionResult(resultCode: Int) {
        if (resultCode == RESULT_OK) {
            Toast.makeText(requireContext(), "Device admin enabled successfully", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Device admin permission denied", Toast.LENGTH_SHORT).show()
        }
        updateButtonStates()
    }
}