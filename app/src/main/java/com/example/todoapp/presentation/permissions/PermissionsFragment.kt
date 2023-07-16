package com.example.todoapp.presentation.permissions

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.Fragment
import com.example.todoapp.R
import com.example.todoapp.databinding.FragmentPermissionsLayoutBinding
import com.example.todoapp.utils.checkPermissions

class PermissionsFragment : Fragment(R.layout.fragment_permissions_layout) {

    private val requestPermissionDialog = registerForPermissionDialogResult()
    private val requestSystemSettings = registerForSystemSettingsResult()
    private val requiredPermissions by lazy { readPermissionsFromArguments() }
    private val sharedPreferences by lazy { createSharedPreferences() }
    private var _binding: FragmentPermissionsLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onStart() {
        super.onStart()
        if (requireContext().checkPermissions(requiredPermissions.toList())) {
            // Все пермишены выданы, можно возращаться назад
            parentFragmentManager.popBackStack()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPermissionsLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpClickListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpClickListeners() {
        binding.permissionButtonRequest.setOnClickListener {
            // 1.       pref=false, shouldShowRequestPermissionRationale() = false
            // 2.       pref=true, shouldShowRequestPermissionRationale() = true
            // 3,4,5... pref=true, shouldShowRequestPermissionRationale() = false
            val isRequestedBefore = sharedPreferences.getBoolean(SHARED_PREF_KEY, false)
            val shouldOpenSettings = isRequestedBefore && !shouldShowRequestPermissionRationale(requiredPermissions)
            if (shouldOpenSettings) {
                val intent = createSystemSettingsIntent(it.context)
                requestSystemSettings.launch(intent)
            } else {
                requestPermissionDialog.launch(requiredPermissions)
            }
            sharedPreferences.edit().putBoolean(SHARED_PREF_KEY, true).apply()
        }

        binding.permissionButtonReject.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun createSystemSettingsIntent(context: Context): Intent {
        val settingsIntent = Intent(
            ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:" + context.packageName)
        )
        settingsIntent.addCategory(Intent.CATEGORY_DEFAULT)
        settingsIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        return settingsIntent
    }

    /**
     * Колбек для диалога с пермишенами
     */
    private fun registerForPermissionDialogResult(): ActivityResultLauncher<Array<String>> {
        return registerForActivityResult(RequestMultiplePermissions()) { permissions ->
            var isAllGranted = true
            for ((_, isGranted) in permissions) {
                isAllGranted = isAllGranted && isGranted
            }
            if (isAllGranted) {
                parentFragmentManager.popBackStack()
            }
        }
    }

    /**
     * Колбек для экрана системных настроек
     */
    private fun registerForSystemSettingsResult(): ActivityResultLauncher<Intent> {
        return registerForActivityResult(StartActivityForResult()) {
            val isGranted = requireContext().checkPermissions(requiredPermissions.toList())
            if (isGranted) parentFragmentManager.popBackStack()
        }
    }

    private fun shouldShowRequestPermissionRationale(permissions: Array<String>): Boolean {
        var shouldShow = true
        for (permission in permissions) {
            shouldShow = shouldShow && shouldShowRequestPermissionRationale(permission)
        }
        return shouldShow
    }

    private fun createSharedPreferences(): SharedPreferences {
        return requireContext().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
    }

    private fun readPermissionsFromArguments(): Array<String> {
        return requireArguments().getStringArray(ARGUMENT_KEY) ?: emptyArray()
    }

    companion object {
        private const val SHARED_PREF_NAME = "PermissionsFragment_sharedPref"
        private const val SHARED_PREF_KEY = "PermissionsFragment_permissionRequested"
        private const val ARGUMENT_KEY = "PermissionsFragment_permissionsArray"

        fun createNewInstance(permissions: ArrayList<String>): PermissionsFragment {
            val permissionsFragment = PermissionsFragment()
            val bundle = Bundle()
            bundle.putStringArray(ARGUMENT_KEY, permissions.toTypedArray())
            permissionsFragment.arguments = bundle
            return permissionsFragment
        }
    }
}
